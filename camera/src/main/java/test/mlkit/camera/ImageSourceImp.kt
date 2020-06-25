@file:Suppress("DEPRECATION")

package test.mlkit.camera

import android.hardware.Camera
import android.view.Display
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import io.reactivex.Single
import test.mlkit.domain.model.Image
import test.mlkit.domain.modules.IImageRatio
import test.mlkit.domain.modules.IImageSource
import test.mlkit.domain.modules.ILifecycleObserver
import kotlin.math.abs

class ImageSourceImp(
    private val surfaceView: SurfaceView,
    private val display: Display,
    private val screenRatio: Float
) : IImageSource, IImageRatio, ILifecycleObserver {

    private lateinit var camera: Camera

    private lateinit var imageRatio: (Float) -> Unit

    private val surfaceHolderCallback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {}

        override fun surfaceCreated(holder: SurfaceHolder) {
            camera.setPreviewDisplay(holder)
        }
    }

    override fun getImage(): Single<Image> = Single.create {
        camera.setOneShotPreviewCallback { data, camera ->
            val previewSize = camera.parameters.previewSize
            val previewImage = Image(
                data,
                previewSize.width,
                previewSize.height,
                rotationDegreesImage()
            )
            it.onSuccess(previewImage)
        }
    }

    override fun ratio(): Single<Float> = Single.create {
        imageRatio = { ratio ->
            it.onSuccess(ratio)
        }
    }

    private fun configureCamera() {
        camera = Camera.open(getCameraIdBack())
        camera.run {
            val customParameters = parameters

            var diff = Float.MAX_VALUE
            var previewWidth = 0
            var previewHeight = 0

            customParameters.supportedPreviewSizes
                .filter { it.width == 640 }
                .apply {
                    this.forEach {
                        val previewDiff = abs((it.width / it.height.toFloat()) - screenRatio)
                        if (previewDiff < diff) {
                            diff = previewDiff
                            previewWidth = it.width
                            previewHeight = it.height
                        }
                    }
                }
                .filter { screenRatio == (it.width / it.height.toFloat()) }
                .run {
                    if (size > 0) {
                        get(0).let { customParameters.setPreviewSize(it.width, it.height) }
                    } else {
                        customParameters.setPreviewSize(previewWidth, previewHeight)
                    }
                }

            imageRatio(customParameters.previewSize.run {
                height / width.toFloat()
            })

            if (parameters.isVideoStabilizationSupported) {
                customParameters.videoStabilization = true
            }

            parameters.supportedFocusModes.run {
                when {
                    this.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) -> customParameters.focusMode =
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                    this.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) -> customParameters.focusMode =
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                    this.contains(Camera.Parameters.FOCUS_MODE_AUTO) -> customParameters.focusMode =
                        Camera.Parameters.FOCUS_MODE_AUTO
                }
            }

            parameters = customParameters

            setDisplayOrientation(rotationDegreesSurface())
        }
    }

    private fun rotationDegreesSurface(): Int =
        (getCameraRotation() - displayRotationDegree() + 360) % 360

    private fun rotationDegreesImage(): Int {
        val degrees = 360 - displayRotationDegree()
        return ((getCameraRotation() + degrees) % 360)
    }

    private fun getCameraIdBack(): Int {
        for (id in 0 until Camera.getNumberOfCameras()) {
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(id, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return id
            }
        }
        throw IllegalStateException("BACK camera not found")
    }

    private fun getCameraRotation(): Int {
        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(getCameraIdBack(), cameraInfo)
        return cameraInfo.orientation
    }

    private fun displayRotationDegree(): Int = when (display.rotation) {
        Surface.ROTATION_0 -> 0
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        Surface.ROTATION_270 -> 270
        else -> 0
    }

    override fun create() {
        configureCamera()
    }

    override fun start() {
        surfaceView.holder.addCallback(surfaceHolderCallback)
        camera.run {
            setPreviewDisplay(surfaceView.holder)
            startPreview()
        }
    }

    override fun stop() {
        camera.stopPreview()
    }

    override fun destroy() {
        surfaceView.holder.removeCallback(surfaceHolderCallback)
        camera.run {
            cancelAutoFocus()
            stopPreview()
            release()
        }
    }
}
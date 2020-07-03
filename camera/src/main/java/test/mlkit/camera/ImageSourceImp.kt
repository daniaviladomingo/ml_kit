@file:Suppress("DEPRECATION")

package test.mlkit.camera

import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import android.view.Display
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import io.reactivex.Completable
import io.reactivex.Single
import test.mlkit.domain.model.Image
import test.mlkit.domain.model.Size
import test.mlkit.domain.modules.IImageSource
import test.mlkit.domain.modules.IImageSourceSetupCompleted
import test.mlkit.domain.modules.ILifecycleObserver
import test.mlkit.domain.modules.debug.PreviewImageListener
import java.io.ByteArrayOutputStream
import kotlin.math.abs

class ImageSourceImp(
    private val surfaceView: SurfaceView,
    private val display: Display,
    private val screenSize: Size,
    private val imageSize: (Size) -> Unit,
    private val visibleImageSize: (Size) -> Unit,
    private val portrait: Boolean,
    private val previewImageListener: () -> PreviewImageListener
) : IImageSource, IImageSourceSetupCompleted, ILifecycleObserver {

    private lateinit var camera: Camera

    private lateinit var rxSetupCompleted: () -> Unit

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

            val yuv = YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null)
            val out = ByteArrayOutputStream()

            yuv.compressToJpeg(Rect(0, 0, previewSize.width, previewSize.height), 100, out)

            val previewImage = Image(
                out.toByteArray(),
                previewSize.width,
                previewSize.height,
                rotationDegreesImage()
            )

//            previewImageListener().onPreviewImage(previewImage)

            it.onSuccess(previewImage)
        }
    }

    override fun setupCompleted(): Completable = Completable.create {
        rxSetupCompleted = {
            it.onComplete()
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
                .filter { if (portrait) it.height in 721..899 else it.width in 1001..1299 }
                .apply {
                    this.forEach {
                        val ratio =
                            if (portrait) (it.height / it.width.toFloat()) else (it.width / it.height.toFloat())
                        val previewDiff = abs(ratio - screenSize.ratio())
                        if (previewDiff < diff) {
                            diff = previewDiff
                            previewWidth = it.width
                            previewHeight = it.height
                        }
                    }
                }
                .filter {
                    val ratio =
                        if (portrait) (it.height / it.width.toFloat()) else (it.width / it.height.toFloat())
                    screenSize.ratio() == ratio
                }
                .run {
                    if (size > 0) {
                        get(0).let { customParameters.setPreviewSize(it.width, it.height) }
                    } else {
                        customParameters.setPreviewSize(previewWidth, previewHeight)
                    }
                }

            customParameters.previewSize.run {
                val imageSize = if(portrait) test.mlkit.domain.model.Size(height, width) else test.mlkit.domain.model.Size(width, height)
                imageSize(imageSize)
                visibleImageSize(calculateImageVisibleSize(imageSize))
            }

            rxSetupCompleted()

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

    private fun calculateImageVisibleSize(imageSize: Size): Size {
        val ratioImage = imageSize.ratio()
        val ratioScreen = screenSize.ratio()

        val visibleWidth =
            if (ratioImage > ratioScreen) {
                val widthScaled: Float = screenSize.height * ratioImage
                (imageSize.width / (widthScaled / screenSize.width)).toInt()
            } else {
                imageSize.width
            }

        val visibleHeight =
            if (ratioImage < ratioScreen) {
                val heightScaled: Float = screenSize.width / ratioImage
                (imageSize.height / (heightScaled / screenSize.height)).toInt()
            } else {
                imageSize.height
            }

        return if(portrait) Size(visibleHeight, visibleWidth) else Size(visibleWidth, visibleHeight)
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
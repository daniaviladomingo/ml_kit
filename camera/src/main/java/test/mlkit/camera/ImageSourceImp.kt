@file:Suppress("DEPRECATION")

package test.mlkit.camera

import android.hardware.Camera
import android.os.Handler
import android.view.Display
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import io.reactivex.Single
import test.mlkit.domain.model.CameraFacing
import test.mlkit.domain.model.Image
import test.mlkit.domain.model.Size
import test.mlkit.domain.modules.ICameraResolution
import test.mlkit.domain.modules.IImageSource
import test.mlkit.domain.modules.ILifecycleObserver
import test.mlkit.domain.modules.debug.PreviewImageListener
import java.util.concurrent.atomic.AtomicBoolean

class ImageSourceImp(
    private val isPortrait: Boolean,
    private val surfaceView: SurfaceView,
    private val display: Display,
    private val cameraResolution: ICameraResolution,
    private val imageSize: (Size) -> Unit,
    private val previewImageListener: () -> PreviewImageListener,
    facing: CameraFacing
) : IImageSource, ILifecycleObserver {

    private var currentFacing = facing

    private lateinit var camera: Camera

    private lateinit var imageRatio: (Float) -> Unit

    private val switching = AtomicBoolean(false)

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
        while (true) {
            if (switching.compareAndSet(false, true)) {
                camera.setOneShotPreviewCallback { data, camera ->
                    val previewSize = camera.parameters.previewSize

                    switching.set(false)

                    val previewImage = Image(
                        data,
                        previewSize.width,
                        previewSize.height,
                        rotationDegreesImage()
                    )

                    previewImageListener().onPreviewImage(previewImage)

                    it.onSuccess(previewImage)
                }
                break
            }
        }
    }

    override fun switchFacing() {
        while (true) {
            if (switching.compareAndSet(false, true)) {
                camera.release()
                currentFacing = if (currentFacing == CameraFacing.FRONT) {
                    CameraFacing.BACK
                } else {
                    CameraFacing.FRONT
                }
                configureCamera()
                camera.setPreviewDisplay(surfaceView.holder)
                camera.startPreview()
                switching.set(false)
                break
            }
        }
    }

    override fun ratio(): Single<Float> = Single.create {
        imageRatio = { ratio ->
            it.onSuccess(if (isPortrait) ratio else 1 / ratio)
        }
    }

    private fun configureCamera() {
        camera = Camera.open(getCameraId(currentFacing)).apply {
            val customParameters = parameters

            val resolution = cameraResolution.getResolution(currentFacing)

            imageSize(
                if (isPortrait) test.mlkit.domain.model.Size(
                    resolution.height,
                    resolution.width
                ) else test.mlkit.domain.model.Size(resolution.width, resolution.height)
            )

            customParameters.setPreviewSize(resolution.width, resolution.height)

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

            imageRatio(customParameters.previewSize.run {
                height / width.toFloat()
            })
        }
    }

    /*

    private fun rotationDegreesSurface(): Int =
        (getCameraRotation() - displayRotationDegree() + 360) % 360

    private fun rotationDegreesImage(): Int {
        val degrees = 360 - displayRotationDegree()
        return ((getCameraRotation() + degrees) % 360)
    }

     */


    private fun rotationDegreesSurface(): Int {
        val degrees = displayRotationDegree()

        return if (currentFacing == CameraFacing.FRONT) {
            (360 - (getCameraRotation() + degrees) % 360) % 360
        } else {
            (getCameraRotation() - degrees + 360) % 360
        }
    }

    private fun rotationDegreesImage(): Int {
        var degrees = displayRotationDegree()

        if (currentFacing == CameraFacing.BACK) {
            degrees = 360 - degrees
        }

        return ((getCameraRotation() + degrees) % 360)
    }

    private fun getCameraRotation(): Int {
        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(getCameraId(currentFacing), cameraInfo)
        return cameraInfo.orientation
    }

    private fun displayRotationDegree(): Int = when (display.rotation) {
        Surface.ROTATION_0 -> 0
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        Surface.ROTATION_270 -> 270
        else -> 0
    }

    private fun getCameraId(facing: CameraFacing): Int {
        val f = when (facing) {
            CameraFacing.FRONT -> Camera.CameraInfo.CAMERA_FACING_FRONT
            CameraFacing.BACK -> Camera.CameraInfo.CAMERA_FACING_BACK
        }
        for (id in 0 until Camera.getNumberOfCameras()) {
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(id, cameraInfo)
            if (cameraInfo.facing == f) {
                return id
            }
        }
        throw IllegalStateException("BACK camera not found")
    }

//    private fun calculateImageVisibleSize(imageSize: Size): Size {
//        val ratioImage = imageSize.ratio()
//        val ratioScreen = screenSize.ratio()
//
//        val visibleWidth =
//            if (ratioImage > ratioScreen) {
//                val widthScaled: Float = screenSize.height * ratioImage
//                (imageSize.width / (widthScaled / screenSize.width)).toInt()
//            } else {
//                imageSize.width
//            }
//
//        val visibleHeight =
//            if (ratioImage < ratioScreen) {
//                val heightScaled: Float = screenSize.width / ratioImage
//                (imageSize.height / (heightScaled / screenSize.height)).toInt()
//            } else {
//                imageSize.height
//            }
//
//        return if (portrait) Size(visibleHeight, visibleWidth) else Size(
//            visibleWidth,
//            visibleHeight
//        )
//    }

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
        Handler().postDelayed({
            camera.run {
                stopPreview()
                release()
            }
            switching.set(false)
        }, 200)
    }
}
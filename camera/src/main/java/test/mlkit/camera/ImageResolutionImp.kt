package test.mlkit.camera

import android.hardware.Camera
import test.mlkit.domain.model.CameraFacing
import test.mlkit.domain.model.Size
import test.mlkit.domain.modules.ICameraResolution
import java.lang.Exception
import kotlin.math.abs

class ImageResolutionImp(
    private val minHeight: Int,
    private val maxHeight: Int,
    private val screenSize: Size,
    private val isPortrait: Boolean
) : ICameraResolution {

    private var backSize: Size? = null
    private var frontSize: Size? = null

    init {
        try {
            Camera.open(getCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)).apply {
                var diff = Float.MAX_VALUE
                var previewWidth = 0
                var previewHeight = 0

                parameters.supportedPreviewSizes
                    .filter { it.height in minHeight..maxHeight }
                    .apply {
                        this.forEach {
                            val ratio =
                                if (isPortrait) (it.height / it.width.toFloat()) else (it.width / it.height.toFloat())
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
                            if (isPortrait) (it.height / it.width.toFloat()) else (it.width / it.height.toFloat())
                        screenSize.ratio() == ratio
                    }
                    .run {
                        if (size > 0) {
                            get(0).let {
                                backSize = test.mlkit.domain.model.Size(it.width, it.height)
                            }
                        } else {
                            backSize = test.mlkit.domain.model.Size(previewWidth, previewHeight)
                        }
                    }
                release()
            }
            Camera.open(getCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)).apply {
                var diff = Float.MAX_VALUE
                var previewWidth = 0
                var previewHeight = 0

                parameters.supportedPreviewSizes
                    .filter { it.height in minHeight..maxHeight }
                    .apply {
                        this.forEach {
                            val ratio =
                                if (isPortrait) (it.height / it.width.toFloat()) else (it.width / it.height.toFloat())
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
                            if (isPortrait) (it.height / it.width.toFloat()) else (it.width / it.height.toFloat())
                        screenSize.ratio() == ratio
                    }
                    .run {
                        if (size > 0) {
                            get(0).let {
                                frontSize = test.mlkit.domain.model.Size(it.width, it.height)
                            }
                        } else {
                            frontSize = test.mlkit.domain.model.Size(previewWidth, previewHeight)
                        }
                    }
                release()
            }
        } catch (e: Exception) {

        }
    }

    override fun getResolution(facing: CameraFacing): Size = when (facing) {
        CameraFacing.FRONT -> frontSize
        CameraFacing.BACK -> backSize
    } ?: throw IllegalAccessException("Error to camera access")

    private fun getCameraId(facing: Int): Int {
        for (id in 0 until Camera.getNumberOfCameras()) {
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(id, cameraInfo)
            if (cameraInfo.facing == facing) {
                return id
            }
        }
        throw IllegalStateException("BACK camera not found")
    }
}

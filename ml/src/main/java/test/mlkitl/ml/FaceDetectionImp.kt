package test.mlkitl.ml

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector
import io.reactivex.Single
import test.mlkit.domain.model.Image
import test.mlkit.domain.model.face.FaceData
import test.mlkit.domain.model.mapper.Mapper
import test.mlkit.domain.modules.ml.IFaceDetection

class FaceDetectionImp(
    private val faceDetector: FaceDetector,
    private val bitmapMapper: Mapper<Image, Bitmap>,
    private val smilingThreshold: Float
) : IFaceDetection {
    override fun detection(image: Image): Single<List<FaceData>> = Single.create { emitter ->
        val img = InputImage.fromBitmap(bitmapMapper.map(image), image.rotation)
        faceDetector.process(img).addOnSuccessListener { faces ->
            emitter.onSuccess(
                faces.map {
                    FaceData(
                        it.smilingProbability?.run { this > smilingThreshold } ?: false,
                        it.rightEyeOpenProbability?.run { this > smilingThreshold } ?: false,
                        it.leftEyeOpenProbability?.run { this > smilingThreshold } ?: false
                    )
                }
            )
        }
    }
}
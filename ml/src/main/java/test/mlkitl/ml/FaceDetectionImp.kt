package test.mlkitl.ml

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetector
import io.reactivex.Single
import test.mlkit.domain.model.Image
import test.mlkit.domain.model.Point
import test.mlkit.domain.model.face.FaceData
import test.mlkit.domain.modules.ml.IFaceDetection

class FaceDetectionImp(
    private val faceDetector: FaceDetector,
    private val classificationThreshold: Float
) : IFaceDetection {
    override fun detection(image: Image): Single<List<FaceData>> = Single.create { emitter ->
        val img = InputImage.fromByteArray(image.data, image.width, image.height, image.rotation, InputImage.IMAGE_FORMAT_NV21)
        faceDetector.process(img).addOnSuccessListener { faces ->
            emitter.onSuccess(
                faces.map { face ->
                    FaceData(
                        face.smilingProbability?.run { this > classificationThreshold } ?: false,
                        face.rightEyeOpenProbability?.run { this > classificationThreshold }
                            ?: false,
                        face.leftEyeOpenProbability?.run { this > classificationThreshold }
                            ?: false,
                        face.getContour(FaceContour.FACE)?.points?.map { p -> Point(p.x, p.y) },
                        face.getContour(FaceContour.LEFT_EYE)?.points?.map { p -> Point(p.x, p.y) },
                        face.getContour(FaceContour.RIGHT_EYE)?.points?.map { p ->
                            Point(
                                p.x,
                                p.y
                            )
                        },
                        face.getContour(FaceContour.UPPER_LIP_TOP)?.points?.map { p ->
                            Point(
                                p.x,
                                p.y
                            )
                        },
                        face.getContour(FaceContour.LOWER_LIP_BOTTOM)?.points?.map { p ->
                            Point(
                                p.x,
                                p.y
                            )
                        }
                    )
                }
            )
        }
    }
}
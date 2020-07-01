package test.mlkit.domain.modules.ml

import io.reactivex.Single
import test.mlkit.domain.model.Image
import test.mlkit.domain.model.face.FaceData

interface IFaceDetection {
    fun detection(image: Image): Single<List<FaceData>>
}
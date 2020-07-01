package test.mlkit.domain.modules.manager

import io.reactivex.Observable
import test.mlkit.domain.model.face.FaceData

interface IMLManager {
    fun recognizedText(): Observable<String>
    fun faceDetection(): Observable<List<FaceData>>
}
package test.mlkit.domain.interactor

import io.reactivex.Observable
import test.mlkit.domain.interactor.type.ObservableUseCase
import test.mlkit.domain.model.face.FaceData
import test.mlkit.domain.modules.manager.IMLManager

class FaceDetectionUseCase(
    private val mlManager: IMLManager
) : ObservableUseCase<List<FaceData>> {
    override fun execute(): Observable<List<FaceData>> = mlManager.faceDetection()
}
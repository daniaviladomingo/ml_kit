package test.mlkit.domain.interactor

import io.reactivex.Observable
import test.mlkit.domain.interactor.type.ObservableUseCase
import test.mlkit.domain.model.face.FaceData
import test.mlkit.domain.modules.manager.IMLManager

class BarcodeScannerUseCase(
    private val mlManager: IMLManager
) : ObservableUseCase<List<String>> {
    override fun execute(): Observable<List<String>> = mlManager.scanBarcode()
}
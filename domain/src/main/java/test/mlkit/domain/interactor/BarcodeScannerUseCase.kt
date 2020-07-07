package test.mlkit.domain.interactor

import io.reactivex.Observable
import test.mlkit.domain.interactor.type.ObservableUseCase
import test.mlkit.domain.model.BarcodeData
import test.mlkit.domain.modules.manager.IMLManager

class BarcodeScannerUseCase(
    private val mlManager: IMLManager
) : ObservableUseCase<List<BarcodeData>> {
    override fun execute(): Observable<List<BarcodeData>> = mlManager.scanBarcode()
}
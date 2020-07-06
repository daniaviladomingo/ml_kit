package test.mlkit.domain.interactor

import io.reactivex.Observable
import test.mlkit.domain.interactor.type.ObservableUseCase
import test.mlkit.domain.modules.manager.IMLManager

class TextRecognitionUseCase(
    private val mlManager: IMLManager
): ObservableUseCase<String> {
    override fun execute(): Observable<String> = mlManager.recognizedText()
}
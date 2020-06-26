package test.mlkit.domain.interactor

import io.reactivex.Observable
import test.mlkit.domain.interactor.type.ObservableUseCase
import test.mlkit.domain.modules.manager.ITextRecognitionManager

class TextRecognitionUseCase(
    private val textRecognitionManager: ITextRecognitionManager
): ObservableUseCase<String> {
    override fun execute(): Observable<String> = textRecognitionManager.read()
}
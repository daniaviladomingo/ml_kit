package test.mlkit.domain.interactor

import io.reactivex.Completable
import io.reactivex.Observable
import test.mlkit.domain.interactor.type.CompletableUseCase
import test.mlkit.domain.interactor.type.ObservableUseCase
import test.mlkit.domain.modules.IImageSource
import test.mlkit.domain.modules.manager.IMLManager

class SwitchFacingCameraUseCase(
    private val imageSource: IImageSource
): CompletableUseCase {
    override fun execute(): Completable = Completable.create {
        imageSource.switchFacing()
        it.onComplete()
    }
}
package test.mlkit.domain.interactor

import io.reactivex.Completable
import test.mlkit.domain.interactor.type.CompletableUseCase
import test.mlkit.domain.modules.IImageSourceSetupCompleted

class SetupCompletedUseCase(
    private val imageSourceSetupCompleted: IImageSourceSetupCompleted
) : CompletableUseCase {
    override fun execute(): Completable = imageSourceSetupCompleted.setupCompleted()
}
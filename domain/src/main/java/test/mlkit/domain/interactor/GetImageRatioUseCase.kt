package test.mlkit.domain.interactor

import io.reactivex.Single
import test.mlkit.domain.interactor.type.SingleUseCase
import test.mlkit.domain.model.Size

class GetImageRatioUseCase(
    private val imageSize: () -> Size
) : SingleUseCase<Float> {
    override fun execute(): Single<Float> = Single.create {
        it.onSuccess(imageSize().ratio())
    }
}
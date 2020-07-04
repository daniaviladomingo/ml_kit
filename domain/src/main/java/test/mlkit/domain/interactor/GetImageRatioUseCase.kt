package test.mlkit.domain.interactor

import io.reactivex.Single
import test.mlkit.domain.interactor.type.SingleUseCase
import test.mlkit.domain.modules.IImageSource

class GetImageRatioUseCase(
    private val imageSource: IImageSource
) : SingleUseCase<Float> {
    override fun execute(): Single<Float> = imageSource.ratio()
}
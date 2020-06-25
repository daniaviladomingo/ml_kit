package test.mlkit.domain.interactor

import io.reactivex.Single
import test.mlkit.domain.interactor.type.SingleUseCase
import test.mlkit.domain.modules.IImageRatio

class GetImageRatioUseCase(
    private val imageRatio: IImageRatio
) : SingleUseCase<Float> {
    override fun execute(): Single<Float> = imageRatio.ratio()
}
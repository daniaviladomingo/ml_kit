package test.mlkit.domain.modules

import io.reactivex.Single
import test.mlkit.domain.model.Image

interface IImageVisible {
    fun visible(image: Image): Single<Image>
}
package test.mlkit.domain.modules

import io.reactivex.Single
import test.mlkit.domain.model.Image

interface IImageSource {
    fun getImage(): Single<Image>
}
package test.mlkit.domain.modules

import io.reactivex.Single

interface IImageRatio {
    fun ratio(): Single<Float>
}
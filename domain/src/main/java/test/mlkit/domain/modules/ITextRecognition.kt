package test.mlkit.domain.modules

import io.reactivex.Single
import test.mlkit.domain.model.Image

interface ITextRecognition {
    fun extractText(image: Image): Single<String>
}
package test.mlkitl.ml

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import io.reactivex.Single
import test.mlkit.domain.model.Image
import test.mlkit.domain.modules.ml.ITextRecognition

class TextRecognitionImp(
    private val textRecognizer: TextRecognizer
) : ITextRecognition {
    override fun extractText(image: Image): Single<String> = Single.create { emitter ->
        val img = InputImage.fromByteArray(
            image.data,
            image.width,
            image.height,
            image.rotation,
            InputImage.IMAGE_FORMAT_NV21
        )
        textRecognizer.process(img).addOnSuccessListener {
            if (it.text.isNotEmpty()) {
                emitter.onSuccess(it.text)
            }
        }
    }
}
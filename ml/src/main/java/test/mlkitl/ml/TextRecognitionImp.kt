package test.mlkitl.ml

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import io.reactivex.Single
import test.mlkit.domain.model.Image
import test.mlkit.domain.model.mapper.Mapper
import test.mlkit.domain.modules.ml.ITextRecognition

class TextRecognitionImp(
    private val textRecognizer: TextRecognizer,
    private val bitmapMapper: Mapper<Image, Bitmap>
) : ITextRecognition {
    override fun extractText(image: Image): Single<String> = Single.create { emitter ->
        val img = InputImage.fromBitmap(bitmapMapper.map(image), image.rotation)
        textRecognizer.process(img).addOnSuccessListener {
            if (it.text.isNotEmpty()) {
                emitter.onSuccess(it.text)
            }
        }
    }
}
package test.mlkit.image_transform

import android.graphics.*
import io.reactivex.Single
import test.mlkit.domain.model.Image
import test.mlkit.domain.model.Size
import test.mlkit.domain.modules.IImageVisible
import java.io.ByteArrayOutputStream

class ImageVisibleImp(
    private val screenSize: Size
) : IImageVisible {
    override fun visible(image: Image): Single<Image> = Single.create {
        val ratioImage = image.width / image.height.toFloat()
        val ratioScreen = screenSize.ratio()

        val visibleWidth =
            if (ratioImage > ratioScreen) {
                val widthScaled: Float = screenSize.height * ratioImage
                (image.width / (widthScaled / screenSize.width)).toInt()
            } else {
                image.width
            }

        val visibleHeight =
            if (ratioImage < ratioScreen) {
                val heightScaled: Float = screenSize.width / ratioImage
                (image.height / (heightScaled / screenSize.height)).toInt()
            } else {
                image.height
            }

        val x = (image.width - visibleWidth) / 2
        val y = (image.height - visibleHeight) / 2

        val bitmap = Bitmap.createBitmap(
            BitmapFactory.decodeByteArray(image.data, 0, image.data.size),
            x,
            y,
            visibleWidth,
            visibleHeight,
            null, //Matrix().apply { postRotate(rotation.toFloat()) },
            true
        )

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        it.onSuccess(Image(outputStream.toByteArray(), visibleWidth, visibleHeight, image.rotation))
    }
}
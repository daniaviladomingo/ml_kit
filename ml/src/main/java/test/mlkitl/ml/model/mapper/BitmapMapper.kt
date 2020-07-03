package test.mlkitl.ml.model.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import test.mlkit.domain.model.Image
import test.mlkit.domain.model.Size
import test.mlkit.domain.model.mapper.Mapper

class BitmapMapper(
    private val imageSizeVisible: () -> Size
) : Mapper<Image, Bitmap>() {
    override fun map(model: Image): Bitmap = model.run {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

        val x = (bitmap.width - imageSizeVisible().width) / 2
        val y = (bitmap.height - imageSizeVisible().height) / 2

        Log.d("aaa", "${x}x${y}")

        Bitmap.createBitmap(
            bitmap,
            x,
            y,
            imageSizeVisible().width,
            imageSizeVisible().height,
            null, //Matrix().apply { postRotate(rotation.toFloat()) },
            true
        )
    }

    override fun inverseMap(model: Bitmap): Image {
        TODO("Not yet implemented")
    }
}
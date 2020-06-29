package test.mlkitl.ml.model.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import test.mlkit.domain.model.Image
import test.mlkit.domain.model.mapper.Mapper

class BitmapMapper : Mapper<Image, Bitmap>() {
    override fun map(model: Image): Bitmap = model.run {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            null, //Matrix().apply { postRotate(rotation.toFloat()) },
            true
        )
    }

    override fun inverseMap(model: Bitmap): Image {
        TODO("Not yet implemented")
    }
}
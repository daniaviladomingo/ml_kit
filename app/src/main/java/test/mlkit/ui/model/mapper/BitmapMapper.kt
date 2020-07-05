package test.mlkit.ui.model.mapper

import android.graphics.*
import test.mlkit.domain.model.Image
import test.mlkit.domain.model.mapper.Mapper
import java.io.ByteArrayOutputStream

class BitmapMapper : Mapper<Image, Bitmap>() {
    override fun map(model: Image): Bitmap {
        val yuv = YuvImage(model.data, ImageFormat.NV21, model.width, model.height, null)
        val out = ByteArrayOutputStream()

        yuv.compressToJpeg(Rect(0, 0, model.width, model.height), 100, out)

        val bytes = out.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            Matrix().apply {
                postRotate(model.rotation.toFloat())
            },
            true
        )
    }

    override fun inverseMap(model: Bitmap): Image {
        TODO("Not yet implemented")
    }
}
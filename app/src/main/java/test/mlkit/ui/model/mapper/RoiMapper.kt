package test.mlkit.ui.model.mapper

import android.util.Log
import test.mlkit.domain.model.Orientation
import test.mlkit.domain.model.Roi
import test.mlkit.domain.model.Size
import test.mlkit.domain.model.mapper.Mapper
import test.mlkit.ui.model.HighLight

class RoiMapper(
    private val color: Int,
    private val screenSize: Size,
    private val imageSize: () -> Size,
    private val orientation: Orientation
) : Mapper<Roi, HighLight>() {
    override fun map(model: Roi): HighLight {
        val newWidth =
            imageSize().height * if (orientation == Orientation.PORTRAIT) screenSize.ratio() else 1 / screenSize.ratio()
        val newHeight =
            imageSize().width * if (orientation == Orientation.PORTRAIT) screenSize.ratio() else 1 / screenSize.ratio()

        val scaleWidth =
            screenSize.width / (if (orientation == Orientation.PORTRAIT) newWidth else imageSize().width.toFloat())
        val scaleHeight =
            screenSize.height / (if (orientation == Orientation.PORTRAIT) imageSize().height.toFloat() else newHeight)

        return HighLight(color, model.scale(scaleWidth, scaleHeight).run {
            floatArrayOf(
                left, top,
                right, top,
                right, top,
                right, bottom,
                right, bottom,
                left, bottom,
                left, bottom,
                left, top
            )
        })
    }

    override fun inverseMap(model: HighLight): Roi {
        TODO("Not yet implemented")
    }

}

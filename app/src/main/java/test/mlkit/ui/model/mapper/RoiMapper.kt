package test.mlkit.ui.model.mapper

import test.mlkit.domain.model.Roi
import test.mlkit.domain.model.Size
import test.mlkit.domain.model.mapper.Mapper
import test.mlkit.ui.model.HighLight

class RoiMapper(
    private val color: Int,
    private val screenSize: Size,
    private val imageSize: () -> Size
) : Mapper<Roi, HighLight>() {
    override fun map(model: Roi): HighLight {

        val scaleWidth = screenSize.width / imageSize().width.toFloat()
        val scaleHeight = screenSize.height / imageSize().height.toFloat()

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

package test.mlkit.ui.model.mapper

import test.mlkit.domain.model.Point
import test.mlkit.domain.model.mapper.Mapper

class PointsMapper : Mapper<List<Point>, FloatArray>() {
    override fun map(model: List<Point>): FloatArray =
        FloatArray(model.size * 2).apply {
            var index = 0
            model.forEach { coordinate ->
                this[index] = coordinate.x
                this[index + 1] = coordinate.y

                index += 2
            }
        }

    override fun inverseMap(model: FloatArray): List<Point> {
        TODO("Not yet implemented")
    }
}
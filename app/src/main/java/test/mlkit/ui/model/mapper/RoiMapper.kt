package test.mlkit.ui.model.mapper

import test.mlkit.domain.model.Roi
import test.mlkit.domain.model.mapper.Mapper

class RoiMapper : Mapper<Roi, FloatArray>() {
    override fun map(model: Roi): FloatArray = model.run {
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
    }

    override fun inverseMap(model: FloatArray): Roi {
        TODO("Not yet implemented")
    }

}

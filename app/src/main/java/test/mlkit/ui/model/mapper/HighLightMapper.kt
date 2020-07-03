package test.mlkit.ui.model.mapper

import android.util.Log
import test.mlkit.domain.model.Size
import test.mlkit.domain.model.face.FaceData
import test.mlkit.domain.model.mapper.Mapper
import test.mlkit.ui.model.HighLight

class HighLightMapper(
    private val screenSize: Size,
    private val imageSize: () -> Size,
    private val color: Int,
    private val colorTrue: Int,
    private val colorFalse: Int,
    private val pointsMapper: PointsMapper
) : Mapper<FaceData, List<HighLight>>() {
    override fun map(model: FaceData): List<HighLight> = model.run {
        val scale = if (imageSize().width < screenSize.width) {
            screenSize.ratio()
        } else {
            1 / screenSize.ratio()
        }

        Log.d("aaa", "Screen size: ${screenSize}")
        Log.d("aaa", "Image size: ${imageSize()}")
        Log.d("aaa", "Screen ratio: ${screenSize.ratio()}")
        Log.d("aaa", "Image ratio: ${imageSize().ratio()}")

        mutableListOf<HighLight>().apply {
            faceOval?.run { add(HighLight(color, pointsMapper.map(this.map { it.scale(scale) }))) }
            rightEye?.run {
                add(
                    HighLight(
                        if (isRightEyeOpen) colorTrue else colorFalse,
                        pointsMapper.map(this.map { it.scale(scale) })
                    )
                )
            }
            leftEye?.run {
                add(
                    HighLight(
                        if (isLeftEyeOpen) colorTrue else colorFalse,
                        pointsMapper.map(this.map { it.scale(scale) })
                    )
                )
            }
            topLip?.run {
                add(
                    HighLight(
                        if (isSmiling) colorTrue else colorFalse,
                        pointsMapper.map(this.map { it.scale(scale) })
                    )
                )
            }
            bottomLip?.run {
                add(
                    HighLight(
                        if (isSmiling) colorTrue else colorFalse,
                        pointsMapper.map(this.map { it.scale(scale) })
                    )
                )
            }
        }
    }

    override fun inverseMap(model: List<HighLight>): FaceData {
        TODO("Not yet implemented")
    }
}
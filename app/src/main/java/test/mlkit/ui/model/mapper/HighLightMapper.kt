package test.mlkit.ui.model.mapper

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
        val newWidth = imageSize().height * screenSize.ratio()

        val scaleWidth = screenSize.width / newWidth
        val scaleHeight = screenSize.height / imageSize().height.toFloat()

        mutableListOf<HighLight>().apply {
            faceOval?.run { add(HighLight(color, pointsMapper.map(this.map { it.scale(scaleWidth, scaleHeight) }))) }
            rightEye?.run {
                add(
                    HighLight(
                        if (isRightEyeOpen) colorTrue else colorFalse,
                        pointsMapper.map(this.map { it.scale(scaleWidth, scaleHeight) })
                    )
                )
            }
            leftEye?.run {
                add(
                    HighLight(
                        if (isLeftEyeOpen) colorTrue else colorFalse,
                        pointsMapper.map(this.map { it.scale(scaleWidth, scaleHeight) })
                    )
                )
            }
            topLip?.run {
                add(
                    HighLight(
                        if (isSmiling) colorTrue else colorFalse,
                        pointsMapper.map(this.map { it.scale(scaleWidth, scaleHeight) })
                    )
                )
            }
            bottomLip?.run {
                add(
                    HighLight(
                        if (isSmiling) colorTrue else colorFalse,
                        pointsMapper.map(this.map { it.scale(scaleWidth, scaleHeight) })
                    )
                )
            }
        }
    }

    override fun inverseMap(model: List<HighLight>): FaceData {
        TODO("Not yet implemented")
    }
}
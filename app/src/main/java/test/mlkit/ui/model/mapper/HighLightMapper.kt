package test.mlkit.ui.model.mapper

import test.mlkit.domain.model.Orientation
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
    private val pointsMapper: PointsMapper,
    private val orientation: Orientation
) : Mapper<FaceData, List<HighLight>>() {
    override fun map(model: FaceData): List<HighLight> = model.run {
        val newWidth =
            imageSize().height * if (orientation == Orientation.PORTRAIT) screenSize.ratio() else 1 / screenSize.ratio()
        val newHeight =
            imageSize().width * if (orientation == Orientation.PORTRAIT) screenSize.ratio() else 1 / screenSize.ratio()

        val scaleWidth =
            screenSize.width / (if (orientation == Orientation.PORTRAIT) newWidth else imageSize().width.toFloat())
        val scaleHeight =
            screenSize.height / (if (orientation == Orientation.PORTRAIT) imageSize().height.toFloat() else newHeight)

        mutableListOf<HighLight>().apply {
            faceOval?.run {
                add(
                    HighLight(
                        color,
                        pointsMapper.map(this.map { it.scale(scaleWidth, scaleHeight) })
                    )
                )
            }
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
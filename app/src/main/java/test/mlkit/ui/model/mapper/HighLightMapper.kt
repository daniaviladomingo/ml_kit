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
//    private val roiMapper: RoiMapper
) : Mapper<FaceData, List<HighLight>>() {
    override fun map(model: FaceData): List<HighLight> = model.run {
        val scaleWidth = screenSize.width / imageSize().width.toFloat()
        val scaleHeight = screenSize.height / imageSize().height.toFloat()


//        Log.d("aaa", "Screen size: $screenSize")
//        Log.d("aaa", "Image size: ${imageSize()}")
//        Log.d("aaa", "Screen ratio: ${screenSize.ratio()}")
//        Log.d("aaa", "Image ratio: ${imageSize().ratio()}")

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
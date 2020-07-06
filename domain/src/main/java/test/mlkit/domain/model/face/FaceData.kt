package test.mlkit.domain.model.face

import test.mlkit.domain.model.Point
import test.mlkit.domain.model.Roi

data class FaceData(
    val isSmiling: Boolean,
    val isLeftEyeOpen: Boolean,
    val isRightEyeOpen: Boolean,
    val box: Roi,
    val faceOval: List<Point>?,
    val leftEye: List<Point>?,
    val rightEye: List<Point>?,
    val topLip: List<Point>?,
    val bottomLip: List<Point>?
)
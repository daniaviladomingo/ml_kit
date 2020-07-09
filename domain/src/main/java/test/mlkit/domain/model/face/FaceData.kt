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
){
    fun getContours(): List<List<Point>> = mutableListOf<List<Point>>().apply{
        faceOval?.run { add(this) }
        leftEye?.run { add(this) }
        rightEye?.run { add(this) }
        topLip?.run { add(this) }
        bottomLip?.run { add(this) }
    }
}
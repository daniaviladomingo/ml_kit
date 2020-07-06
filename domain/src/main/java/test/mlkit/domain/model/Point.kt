package test.mlkit.domain.model

data class Point(val x: Float, val y: Float) {
    fun scale(scaleW: Float, scaleH: Float): Point = Point(x * scaleW, y * scaleH)
}
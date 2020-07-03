package test.mlkit.domain.model

data class Point(val x: Float, val y: Float) {
    fun scale(scale: Float): Point = Point(x * scale, y * scale)
}
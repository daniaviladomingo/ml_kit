package test.mlkit.domain.model

data class Roi(val top: Float, val left: Float, val right: Float, val bottom: Float){
    fun scale(scale: Float): Roi = Roi(top * scale, left * scale, right * scale, bottom * scale)
}
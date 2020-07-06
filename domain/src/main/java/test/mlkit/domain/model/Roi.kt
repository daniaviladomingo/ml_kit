package test.mlkit.domain.model

data class Roi(val top: Float, val left: Float, val right: Float, val bottom: Float){
    fun scale(scaleW: Float, scaleH: Float): Roi = Roi(top * scaleH, left * scaleW, right * scaleW, bottom * scaleH)
}
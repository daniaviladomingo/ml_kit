package test.mlkit.domain.model

data class Size(
    val width: Int,
    val height: Int
) {
    fun ratio(): Float = width / height.toFloat()

    override fun toString(): String = "$width x $height"
}
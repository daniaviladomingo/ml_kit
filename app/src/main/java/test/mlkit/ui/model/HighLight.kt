package test.mlkit.ui.model

data class HighLight(
    val color: Int,
    val points: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HighLight

        if (color != other.color) return false
        if (!points.contentEquals(other.points)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color
        result = 31 * result + points.contentHashCode()
        return result
    }
}
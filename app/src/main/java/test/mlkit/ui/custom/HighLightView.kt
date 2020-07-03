package test.mlkit.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import test.mlkit.ui.model.HighLight

class HighLightView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var highLights: List<HighLight>? = null

    private val paint = Paint().apply {
        color = Color.BLUE
        isAntiAlias = true
        strokeWidth = 5f
        textSize = 30f
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            highLights?.forEach {
                drawLines(it.points, paint.apply { color = it.color })
            }
        }
    }

    fun clearHighLight() {
        this.highLights = null
        invalidate()
    }

    fun drawHighLight(highLights: List<HighLight>) {
        this.highLights = highLights
        invalidate()
    }
}
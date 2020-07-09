package test.mlkit.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import kotlin.math.abs

class AdaptableContainerSurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var measureWidth = 0
    private var measureHeight = 0

    private var ratio = -1f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureWidth = 0
        measureHeight = 0

        val width = View.resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = View.resolveSize(suggestedMinimumHeight, heightMeasureSpec)

        if (ratio != -1f) {
            val ratioView = width / height.toFloat()
            Log.d("aaa", "$width / $height")
            Log.d("aaa", "ratioView: $ratioView, ratio: $ratio")
            if (ratioView != ratio) {
                when {
                    ratioView < ratio -> {
                        measureWidth = ((height - width / ratio) * ratio).toInt()
                        measureHeight = ((width - height * ratioView) / ratioView).toInt()
                    }
                    ratioView > ratio -> {
                        measureWidth = ((height - width / ratioView) * ratioView).toInt()
                        measureHeight = ((width - height * ratio) / ratio).toInt()
                    }
                    else -> {
                        measureHeight = ((width - height / ratio) * ratio).toInt()
                        measureWidth = ((height - width / ratio) * ratio).toInt()
                    }
                }
            }

            Log.d("aaa", "measureWidth: $measureWidth, measureHeight: $measureHeight")

            setMeasuredDimension(width + measureWidth, height + measureHeight)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (ratio != -1f) {
            (0..childCount).forEach {
                getChildAt(it)?.run {
                    this.layout(
                        l - (measureWidth / 2),
                        t - (measureHeight / 2),
                        r + (measureWidth / 2),
                        b + (measureHeight / 2)
                    )
                }
            }
        } else {
            super.onLayout(changed, left, top, right, bottom)
        }
    }

    fun setRatio(ratio: Float) {
        this.ratio = ratio
        requestLayout()
    }
}
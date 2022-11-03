package com.brave.adaptive

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatTextView

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/11/3 10:33
 *
 * ***desc***       ：自适应文本字号的[TextView][android.widget.TextView]
 *
 * @see R.styleable.AdaptiveTextView_precision
 * @see R.styleable.AdaptiveTextView_minTextSize
 * @see R.styleable.AdaptiveTextView_sizeToFit
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class AdaptiveTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle), AdaptiveHelper.OnTextSizeChangeListener {
    /**
     * @see AdaptiveHelper
     */
    var adaptiveHelper: AdaptiveHelper? = null
        private set

    init {
        adaptiveHelper = AdaptiveHelper.create(this, attrs, defStyle)
            .addOnTextSizeChangeListener(this)
    }

    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
        adaptiveHelper?.setTextSize(unit, size)
    }

    override fun setLines(lines: Int) {
        super.setLines(lines)
        adaptiveHelper?.maxLines = lines
    }

    override fun setMaxLines(maxLines: Int) {
        super.setMaxLines(maxLines)
        adaptiveHelper?.maxLines = maxLines
    }

    /**
     * 自动调整文本字号
     */
    var isSizeToFit: Boolean
        get() = adaptiveHelper?.isEnabled ?: true
        set(sizeToFit) {
            adaptiveHelper?.isEnabled = sizeToFit
        }

    /**
     * 最大文本字号
     */
    var maxTextSize: Float
        @Px get() = adaptiveHelper?.maxTextSize ?: -1f
        set(@Px size) {
            adaptiveHelper?.maxTextSize = size
        }

    /**
     * 设置[指定单位][unit]的最大文本字号
     */
    fun setMaxTextSize(unit: Int, size: Float) {
        adaptiveHelper?.setMaxTextSize(unit, size)
    }

    /**
     * 最小文本字号
     */
    var minTextSize: Float
        @Px get() = adaptiveHelper?.minTextSize ?: -1f
        set(@Px size) {
            adaptiveHelper?.minTextSize = size
        }

    /**
     * 设置[指定单位][unit]的最小文本字号
     */
    fun setMinTextSize(unit: Int, minSize: Float) {
        adaptiveHelper?.setMinTextSize(unit, minSize)
    }

    /**
     * 二进制递归字号的精度
     */
    var precision: Float
        @Px get() = adaptiveHelper?.precision ?: 0f
        set(@Px precision) {
            adaptiveHelper?.precision = precision
        }

    override fun onTextSizeChange(textSize: Float, oldTextSize: Float) {
        // do nothing
    }
}
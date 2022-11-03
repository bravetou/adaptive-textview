package com.brave.adaptive

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.Px
import java.util.*

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/11/3 11:19
 *
 * ***desc***       ：一个自适应文本字号的[容器][FrameLayout]
 *
 * @see R.styleable.AdaptiveLayout_precision
 * @see R.styleable.AdaptiveLayout_minTextSize
 * @see R.styleable.AdaptiveLayout_sizeToFit
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class AdaptiveLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {
    private var mEnabled = true

    @Px
    private var mMinTextSize = -1f

    @Px
    private var mPrecision = -1f
    
    private val mHelpers = WeakHashMap<View, AdaptiveHelper>()

    init {
        val ta = if (attrs != null) {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.AdaptiveLayout,
                defStyle,
                0
            )
        } else null
        mEnabled = ta?.getBoolean(
            R.styleable.AdaptiveLayout_sizeToFit,
            true
        ) ?: true
        mMinTextSize = ta?.getDimensionPixelSize(
            R.styleable.AdaptiveLayout_minTextSize,
            -1
        )?.toFloat() ?: -1f
        mPrecision = ta?.getFloat(
            R.styleable.AdaptiveLayout_precision,
            -1f
        ) ?: -1f
        ta?.recycle()
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        if (child is TextView) {
            val helper = AdaptiveHelper.create(child)
            helper.isEnabled = mEnabled
            if (mPrecision > 0) {
                helper.precision = mPrecision
            }
            if (mMinTextSize > 0) {
                helper.setMinTextSize(TypedValue.COMPLEX_UNIT_PX, mMinTextSize)
            }
            mHelpers[child] = helper
        }
    }

    /**
     * 返回指定[TextView]的[AdaptiveHelper]
     */
    fun getAdaptiveHelper(child: View?): AdaptiveHelper? {
        return if (child is TextView) {
            mHelpers[child]
        } else null
    }

    /**
     * 返回指定[子视图索引][index]的[AdaptiveHelper]
     */
    fun getAdaptiveHelper(index: Int): AdaptiveHelper? {
        return if (index < childCount) {
            getAdaptiveHelper(getChildAt(index))
        } else null
    }
}
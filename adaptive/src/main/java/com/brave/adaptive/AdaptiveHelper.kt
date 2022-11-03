package com.brave.adaptive

import android.content.res.Resources
import android.text.*
import android.text.method.SingleLineTransformationMethod
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.TextView
import androidx.annotation.Px

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/11/3 10:31
 *
 * ***desc***       ： 用于自动调整[TextView][TextView]文本大小，
 * 以适应其范围的帮助类
 *
 * @see R.styleable.AdaptiveTextView_precision
 * @see R.styleable.AdaptiveTextView_minTextSize
 * @see R.styleable.AdaptiveTextView_sizeToFit
 */
@Suppress("DEPRECATION", "MemberVisibilityCanBePrivate")
class AdaptiveHelper private constructor(private val mTextView: TextView) {
    private val mPaint by lazy { TextPaint() }

    private val resources: Resources
        get() = if (null != mTextView.context) {
            mTextView.context.resources
        } else {
            Resources.getSystem()
        }

    /**
     * 原始文本字号
     */
    @Px
    private var mTextSize = 0.0f

    /**
     * 文本最大显示行数
     * @see TextView.getMaxLines
     * @see TextView.setMaxLines
     */
    var maxLines: Int = Int.MAX_VALUE
        set(value) {
            if (maxLines != value) {
                field = value
                autoFit()
            }
        }

    /**
     * 最小文本字号
     */
    @Px
    var minTextSize: Float = 0.0f
        set(@Px value) {
            if (minTextSize != value) {
                field = value
                autoFit()
            }
        }

    /**
     * 最大文本字号
     */
    @Px
    var maxTextSize: Float = 0.0f
        set(@Px value) {
            if (maxTextSize != value) {
                field = value
                autoFit()
            }
        }

    /**
     * 二进制递归文本字号的精度
     */
    @Px
    var precision: Float = 0.0f
        set(@Px value) {
            if (precision != value) {
                field = value
                autoFit()
            }
        }

    /**
     * 自动调整文本字号
     */
    var isEnabled = false
        set(value) {
            if (isEnabled != value) {
                field = value
                if (value) {
                    mTextView.addTextChangedListener(mTextWatcher)
                    mTextView.addOnLayoutChangeListener(mOnLayoutChangeListener)
                    autoFit()
                } else {
                    mTextView.removeTextChangedListener(mTextWatcher)
                    mTextView.removeOnLayoutChangeListener(mOnLayoutChangeListener)
                    mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize)
                }
            }
        }

    /**
     * 正在调整文本字号
     */
    private var mIsAutoFitting = false
    private val mListeners = mutableListOf<OnTextSizeChangeListener>()
    private val mTextWatcher: TextWatcher = AutoFitTextWatcher()
    private val mOnLayoutChangeListener: OnLayoutChangeListener =
        AutoFitOnLayoutChangeListener()

    /**
     * 添加 => 文本字号更改的监听事件
     * @see removeOnTextSizeChangeListener
     */
    fun addOnTextSizeChangeListener(listener: OnTextSizeChangeListener): AdaptiveHelper {
        mListeners.add(listener)
        return this
    }

    /**
     * 移除 => 文本字体大小更改的监听事件
     * @see addOnTextSizeChangeListener
     */
    fun removeOnTextSizeChangeListener(listener: OnTextSizeChangeListener): AdaptiveHelper {
        mListeners.remove(listener)
        return this
    }

    /**
     * 设置指定[单位][unit]的最小文本字号
     * @param unit 单位
     * @param size 字号
     */
    fun setMinTextSize(unit: Int, size: Float): AdaptiveHelper {
        minTextSize = TypedValue.applyDimension(
            unit,
            size,
            resources.displayMetrics
        )
        return this
    }

    /**
     * 设置指定[单位][unit]的最大文本字号
     * @param unit 单位
     * @param size 字号
     */
    fun setMaxTextSize(unit: Int, size: Float): AdaptiveHelper {
        maxTextSize = TypedValue.applyDimension(
            unit,
            size,
            resources.displayMetrics
        )
        return this
    }

    /**
     * 原始文本字号
     * @see TextView.setTextSize
     */
    var textSize: Float
        @Px get() = mTextSize
        set(@Px size) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }

    /**
     * 设置指定[单位][unit]的原始文本字号
     * @see TextView.setTextSize
     */
    fun setTextSize(unit: Int, size: Float) {
        // 正在递归最佳文本字体大小时，不允许设置文本字体大小
        if (mIsAutoFitting) return
        mTextSize = TypedValue.applyDimension(
            unit,
            size,
            resources.displayMetrics
        )
    }

    /**
     * 自动调整字号
     */
    private fun autoFit() {
        val oldTextSize = mTextView.textSize
        mIsAutoFitting = true
        autoFit(mTextView, mPaint, minTextSize, maxTextSize, maxLines, precision)
        mIsAutoFitting = false
        val textSize: Float = mTextView.textSize
        if (textSize != oldTextSize) {
            sendTextSizeChange(textSize, oldTextSize)
        }
    }

    private fun sendTextSizeChange(textSize: Float, oldTextSize: Float) {
        mListeners.forEach { listener ->
            listener.onTextSizeChange(textSize, oldTextSize)
        }
    }

    private inner class AutoFitTextWatcher : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
            // do nothing
        }

        override fun onTextChanged(
            charSequence: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
            autoFit()
        }

        override fun afterTextChanged(editable: Editable) {
            // do nothing
        }
    }

    private inner class AutoFitOnLayoutChangeListener : OnLayoutChangeListener {
        override fun onLayoutChange(
            view: View, left: Int, top: Int, right: Int, bottom: Int,
            oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
        ) {
            autoFit()
        }
    }

    /**
     * 文本字号更改监听
     */
    interface OnTextSizeChangeListener {
        /**
         * 文本字号从[oldTextSize]更改为[textSize]
         */
        fun onTextSizeChange(textSize: Float, oldTextSize: Float)
    }

    companion object {
        private const val TAG = "AdaptiveHelper"
        private const val SPEW = false

        /**
         * 默认的文本最小字号（单位sp）
         */
        private const val DEFAULT_MIN_TEXT_SIZE = 8

        /**
         * 二进制递归文本字号的精度
         */
        private const val DEFAULT_PRECISION = 0.5f

        /**
         * 创建一个[AdaptiveHelper]实例，
         * 该实例包装一个[TextView]，
         * 并启用自动调整文本字号
         */
        @JvmStatic
        @JvmOverloads
        fun create(
            view: TextView,
            attrs: AttributeSet? = null,
            defStyle: Int = 0
        ): AdaptiveHelper {
            val helper = AdaptiveHelper(view)
            if (attrs != null) {
                val array = view.context.obtainStyledAttributes(
                    attrs,
                    R.styleable.AdaptiveTextView,
                    defStyle,
                    0
                )
                val sizeToFit = array.getBoolean(
                    R.styleable.AdaptiveTextView_sizeToFit,
                    true
                )
                val minTextSize = array.getDimensionPixelSize(
                    R.styleable.AdaptiveTextView_minTextSize,
                    helper.minTextSize.toInt()
                )
                val precision = array.getFloat(
                    R.styleable.AdaptiveTextView_precision,
                    helper.precision
                )
                array.recycle()
                helper.setMinTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    minTextSize.toFloat()
                ).precision = precision
                sizeToFit
            } else {
                true
            }.also { sizeToFit ->
                helper.isEnabled = sizeToFit
            }
            return helper
        }

        /**
         * 重新调整[TextView]的文本字号，使文本适合视图的边界
         */
        private fun autoFit(
            view: TextView,
            paint: TextPaint,
            minTextSize: Float,
            maxTextSize: Float,
            maxLines: Int,
            precision: Float
        ) {
            if (maxLines <= 0 || maxLines == Int.MAX_VALUE) {
                // 没有`maxLines`限制时，不需自动调整大小
                return
            }
            val targetWidth = view.width - view.paddingLeft - view.paddingRight
            if (targetWidth <= 0) {
                return
            }
            var text = view.text
            val method = view.transformationMethod
            if (method != null) {
                text = method.getTransformation(text, view)
            }
            var size = maxTextSize
            val high = size
            val low = 0f
            val displayMetrics = if (null != view.context) {
                view.context.resources
            } else {
                Resources.getSystem()
            }.displayMetrics
            paint.set(view.paint)
            paint.textSize = size
            if (maxLines == 1 && paint.measureText(
                    text,
                    0,
                    text.length
                ) > targetWidth ||
                getLineCount(
                    text,
                    paint,
                    size,
                    targetWidth.toFloat(),
                    displayMetrics
                ) > maxLines
            ) {
                size = getAutoFitTextSize(
                    text,
                    paint,
                    targetWidth.toFloat(),
                    maxLines,
                    low,
                    high,
                    precision,
                    displayMetrics
                )
            }
            if (size < minTextSize) {
                size = minTextSize
            }
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }

        /**
         * 二进制递归搜索以找到文本的最佳字体字号
         * @param text 文本
         * @param paint 画笔
         * @param targetWidth 宽度
         * @param maxLines 最大行数
         * @param low 最小字号
         * @param high 最大字号
         * @param precision 精度
         */
        private fun getAutoFitTextSize(
            text: CharSequence,
            paint: TextPaint,
            targetWidth: Float,
            maxLines: Int,
            low: Float,
            high: Float,
            precision: Float,
            displayMetrics: DisplayMetrics
        ): Float {
            val mid = (low + high) / 2.0f
            var lineCount = 1
            var layout: StaticLayout? = null
            paint.textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                mid,
                displayMetrics
            )
            if (maxLines != 1) {
                layout = StaticLayout(
                    text, paint, targetWidth.toInt(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f, 0.0f,
                    true
                )
                //    layout = StaticLayout(
                //        text, 0, text.length, paint, targetWidth.toInt(),
                //        Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f,
                //        true, TextUtils.TruncateAt.END, targetWidth.toInt()
                //    )
                lineCount = layout.lineCount // 使用[StaticLayout]计算文本行数
            }
            if (SPEW) Log.d(
                TAG,
                "low=$low high=$high mid=$mid target=$targetWidth maxLines=$maxLines lineCount=$lineCount"
            )
            return if (lineCount > maxLines) { // 测量行数已超出最大行数
                if (high - low < precision) {
                    low
                } else getAutoFitTextSize(
                    text,
                    paint,
                    targetWidth,
                    maxLines,
                    low,
                    mid,
                    precision,
                    displayMetrics
                )
            } else if (lineCount < maxLines) {
                getAutoFitTextSize(
                    text,
                    paint,
                    targetWidth,
                    maxLines,
                    mid,
                    high,
                    precision,
                    displayMetrics
                )
            } else {
                var maxLineWidth = 0f
                if (maxLines == 1) {
                    maxLineWidth = paint.measureText(text, 0, text.length)
                } else {
                    for (i in 0 until lineCount) {
                        if (layout!!.getLineWidth(i) > maxLineWidth) {
                            maxLineWidth = layout.getLineWidth(i)
                        }
                    }
                }
                if (high - low < precision) {
                    low
                } else if (maxLineWidth > targetWidth) {
                    getAutoFitTextSize(
                        text,
                        paint,
                        targetWidth,
                        maxLines,
                        low,
                        mid,
                        precision,
                        displayMetrics
                    )
                } else if (maxLineWidth < targetWidth) {
                    getAutoFitTextSize(
                        text,
                        paint,
                        targetWidth,
                        maxLines,
                        mid,
                        high,
                        precision,
                        displayMetrics
                    )
                } else {
                    mid
                }
            }
        }

        /**
         * 获取文本测量行数
         * @param text 文本
         * @param paint 画笔
         * @param size 字号
         * @param width 宽度
         */
        private fun getLineCount(
            text: CharSequence,
            paint: TextPaint,
            size: Float,
            width: Float,
            displayMetrics: DisplayMetrics
        ): Int {
            paint.textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                size,
                displayMetrics
            )
            val layout = StaticLayout(
                text, paint, width.toInt(),
                Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0.0f,
                true
            )
            return layout.lineCount
        }

        private fun getMaxLines(view: TextView): Int {
            val method = view.transformationMethod
            return if (method != null && method is SingleLineTransformationMethod) {
                1
            } else {
                view.maxLines
            }
        }
    }

    init {
        val scaledDensity = resources.displayMetrics
            .scaledDensity
        textSize = mTextView.textSize
        maxLines = getMaxLines(mTextView)
        minTextSize = scaledDensity * DEFAULT_MIN_TEXT_SIZE
        maxTextSize = mTextSize
        precision = DEFAULT_PRECISION
    }
}
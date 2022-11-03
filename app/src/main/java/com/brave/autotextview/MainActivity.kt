package com.brave.autotextview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.brave.adaptive.AdaptiveTextView

class MainActivity : AppCompatActivity() {

    private val tvContent: TextView by lazy {
        findViewById(R.id.tv_content)
    }
    private val tvContent2: TextView by lazy {
        findViewById(R.id.tv_content_2)
    }
    private val atvContent: AdaptiveTextView by lazy {
        findViewById(R.id.atv_content)
    }
    private val input: EditText by lazy {
        findViewById(R.id.input)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        input.addTextChangedListener {
            val text = it?.toString() ?: ""
            tvContent.text = text
            //tvContent2.text = text
            atvContent.text = text
        }
    }
}
package com.example.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders

private const val EXTRA_ANSWER_IS_TRUE = "answer_is_true"
const val EXTRA_ANSWER_SHOWN = "answer_shown"
const val EXTRA_CHEAT_COUNT = "cheat_count"
private const val KEY_WAS_CHEATED = "was_cheated"

class CheatActivity : AppCompatActivity() {
    private var answerIsTrue = false
    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private var wasCheated = false
    var cheatCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)

        wasCheated = savedInstanceState?.getBoolean(KEY_WAS_CHEATED, false) ?: false
        setAnswerShownResult(wasCheated, cheatCount)

        showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            cheatCount = intent?.getIntExtra(EXTRA_CHEAT_COUNT, 0)?: 0

            setAnswerShownResult(true, cheatCount + 1)
        }
    }

    override fun onSaveInstanceState(onSaveInstanceState: Bundle) {
        super.onSaveInstanceState(onSaveInstanceState)
        onSaveInstanceState.putBoolean(KEY_WAS_CHEATED, wasCheated)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean, cheatCount:Int): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
                putExtra(EXTRA_CHEAT_COUNT, cheatCount)
            }
        }
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean, cheatCount: Int) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
            putExtra(EXTRA_CHEAT_COUNT, cheatCount)
        }
        setResult(Activity.RESULT_OK, data)
        wasCheated = isAnswerShown
    }
}
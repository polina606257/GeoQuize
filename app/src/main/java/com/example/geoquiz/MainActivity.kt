package com.example.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var cheatCountTextView: TextView
    private var answerMade: Boolean = false
    private var rightAnswer: Int = 0
    var percentCorrectAnswers: Int = 0
    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this)
            .get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate(Bundle?) called")

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        cheatButton = findViewById(R.id.cheat_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatCountTextView = findViewById(R.id.cheatCountTextView)

        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
            answerMade()
        }

        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
            answerMade()
        }

        cheatButton.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val cheatCount = quizViewModel.cheatCount
            val intent = CheatActivity.newIntent(this, answerIsTrue, cheatCount)
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }

        nextButton.setOnClickListener {
            answerMade()
            quizViewModel.moveToNext()
            updateQuestion()
        }

        prevButton.setOnClickListener {
            answerMade()
            quizViewModel.moveToPrev()
            updateQuestion()
        }

        updateQuestion()
    }

    override fun onResume() {
        super.onResume()
        if(quizViewModel.cheatCount == 3) {
            cheatButton.isEnabled = false
            cheatCountTextView.text = "You cant cheat any more"
        } else
            cheatCountTextView.text = "You can cheat ${3 - quizViewModel.cheatCount} more times"

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if(requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false)?: false
            quizViewModel.cheatCount = data?.getIntExtra(EXTRA_CHEAT_COUNT, 0)?: 0
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSavedInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        var correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheater -> R.string.jughement_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        if (quizViewModel.currentIndex == quizViewModel.questionBank.size - 1) {
            Toast.makeText(
                this, "${getString(messageResId)}\n You got ${getCorrectAnswersPercent()} %",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        }
        quizViewModel.isCheater = false
    }

    private fun answerMade() {
        if (!answerMade) {
            answerMade = true
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        } else {
            answerMade = false
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        }
    }

    private fun getCorrectAnswersPercent(): Int {
        percentCorrectAnswers = (rightAnswer * 100) / quizViewModel.questionBank.size
        return percentCorrectAnswers
    }
}
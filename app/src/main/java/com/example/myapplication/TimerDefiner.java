package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


/**
 * This activity allows the user to define and start a timer.
 */
public class TimerDefiner extends AppCompatActivity {
    private static final long START_TIME_IN_MILLIS = 600000;

    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;
    private EditText minutes;
    private EditText seconds;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_definer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        mButtonStartPause = findViewById(R.id.startButton);
        mButtonReset = findViewById(R.id.CancelButton);
        minutes = findViewById(R.id.minutes);
        seconds = findViewById(R.id.seconds);

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        updateCountDownText();

        if (getIntent().hasExtra("minutes") && getIntent().hasExtra("seconds")) {
            int minutes = getIntent().getIntExtra("minutes", 0);
            int seconds = getIntent().getIntExtra("seconds", 0);
            setTime(minutes, seconds);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Set the time for the timer using the provided minutes and seconds.
     *
     * @param minutes the minutes value for the timer
     * @param seconds the seconds value for the timer
     */
    public void setTime(int minutes, int seconds) {
        long milliminutes = minutes * 60000L;
        long milliseconds = seconds * 1000L;

        mTimeLeftInMillis = milliminutes + milliseconds;

        if (minutes > 59 || seconds > 59 || minutes < 0 || seconds < 0) {
            Toast.makeText(this, "Invalid values entered.", Toast.LENGTH_SHORT).show();
        } else {
            updateCountDownText();
        }
    }

    /**
     * Set the time for the timer using the values entered in the EditText fields.
     *
     * @param v the View that was clicked
     */
    public void setTime(View v) {
        String minutesStr = minutes.getText().toString();
        String secondsStr = seconds.getText().toString();

        if (minutesStr.isEmpty() || secondsStr.isEmpty()) {
            Toast.makeText(this, "Please enter a value for minutes and seconds.", Toast.LENGTH_SHORT).show();
        } else {
            Long minutesLong = Long.parseLong(minutesStr);
            Long secondsLong = Long.parseLong(secondsStr);

            long milliminutes = minutesLong * 60000;
            long milliseconds = secondsLong * 1000;

            mTimeLeftInMillis = milliminutes + milliseconds;

            if (minutesLong > 59 || secondsLong > 59 || minutesLong < 0 || secondsLong < 0) {
                Toast.makeText(this, "Invalid values entered.", Toast.LENGTH_SHORT).show();
            } else {
                updateCountDownText();
            }
        }
    }

    /**
     * Start the timer.
     */
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    /**
     * Pause the timer.
     */
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
    }

    /**
     * Reset the timer to its initial value.
     */
    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    /**
     * Update the text displayed in the countdown TextView.
     */
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }
}
package com.example.chessclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

import static com.example.chessclock.Constants.*;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    
    /* Layout Elements */
    Button mPlayer1Btn;
    Button mPlayer2Btn;
    ImageButton mStartStopBtn;
    ImageButton mRestartBtn;
    TextView mTestDelay;

    /* Time Variables */
    private long mTimeRemainingP1;
    private long mTimeRemainingP2;
    private GameMode mCurrentGameMode;

    /* Script Tools */
    private CountDownTimer mCountDownTimer;
    private int mMovesCounter;

    /* Flags */
    private boolean mTurnP1;
    private boolean mGamePaused;


    /* ============================== Activities ============================== */
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.preference.PreferenceManager
                .setDefaultValues(this, R.xml.user_preferences, false);
        mTestDelay = findViewById(R.id.testDelay);
        initializeLayout();
        getPreferences();
        initialize();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getPreferences();
    }


    /**
     * Gets the preference info and stores it into our current game mode object. Checks if it has
     * been changes and restarts the clocks if it has.
     */
    private void getPreferences(){
        if(mCurrentGameMode == null)
            mCurrentGameMode = new GameMode(
                    "def","def",true,1,0,0,0,0,0,0,0);
        boolean restartFlag = false;
        SharedPreferences sharedPref = android.support.v7.preference.PreferenceManager
                        .getDefaultSharedPreferences(this);

        /* Check symmetric time or not */
        Boolean sameTime = sharedPref.getBoolean(SAME_TIME_KEY,false);
        if(mCurrentGameMode.symmetric != sameTime){
            mCurrentGameMode.symmetric = sameTime;
            restartFlag = true;
        }
        /* If symmetric has been selected, then the times for each player are the same. */
        if(sameTime){
            String gameMode = sharedPref.getString(
                    STANDARD_GAME_MODE_KEY,"predefined_mode");
            assert gameMode != null;
            /* Restart if the mode has changed */
            if(!mCurrentGameMode.name.equals(gameMode)) {
                mCurrentGameMode.name = gameMode;
                restartFlag = true;
            }
            /* Get pref values using requireNonNull just in case */
            String mStartingTimeString = sharedPref.getString(
                    STANDARD_STARTING_TIME_KEY, "3:0");
            long mIncrement = Long.parseLong(Objects.requireNonNull(sharedPref.getString(
                    STANDARD_INCREMENT_KEY, "1"))) * 1000;
            long mDelay = Long.parseLong(Objects.requireNonNull(sharedPref.getString(
                    STANDARD_DELAY_KEY, "1"))) * 1000;
            int mTimeControl = Integer.parseInt(Objects.requireNonNull(sharedPref.getString(
                    STANDARD_TIME_CONTROL_KEY, "0")));
            /* Parse the time from min and secs into millis */
            assert mStartingTimeString != null;
            String[] splattedTime = mStartingTimeString.split(":");
            long mStartingTime = Long.parseLong(splattedTime[0]) * 60000 +
                                         Long.parseLong(splattedTime[1]) * 1000;
            /* Check the correct settings depending on the selected game mode */
            switch(gameMode) {
                case "custom_mode":
                    /* Custom Mode: Check all the prefs */
                    if (mCurrentGameMode.startTimeP1 != mStartingTime) {
                        mCurrentGameMode.startTimeP1 = mStartingTime;
                        mCurrentGameMode.startTimeP2 = mStartingTime;
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.delayP1 != mDelay) {
                        mCurrentGameMode.delayP1 = mDelay;
                        mCurrentGameMode.delayP2 = mDelay;
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.incrementP1 != mIncrement) {
                        mCurrentGameMode.incrementP1 = mIncrement;
                        mCurrentGameMode.incrementP2 = mIncrement;
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.timeControlP1 != mTimeControl) {
                        mCurrentGameMode.timeControlP1 = mTimeControl;
                        mCurrentGameMode.timeControlP2 = mTimeControl;
                        restartFlag = true;
                    }
                    break;
                case "predefined_mode":
                    /* Predefined mode: must check which mode is selected */
                    String mPredefinedMode = sharedPref.getString(
                            STANDARD_PREDEFINED_MODE_KEY,"blitz_3min");
                    if (mCurrentGameMode.predefinedName == null ||
                                !mCurrentGameMode.predefinedName.equals(mPredefinedMode)) {
                        mCurrentGameMode.predefinedName = mPredefinedMode;
                        setGameMode(mPredefinedMode);
                        restartFlag = true;
                    }
                    break;
                case "blitz_mode":
                    /* Blitz Mode: Starting time and increment */
                    if (mCurrentGameMode.startTimeP1 != mStartingTime) {
                        mCurrentGameMode.startTimeP1 = mStartingTime;
                        mCurrentGameMode.startTimeP2 = mStartingTime;
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.incrementP1 != mIncrement) {
                        mCurrentGameMode.incrementP1 = mIncrement;
                        mCurrentGameMode.incrementP2 = mIncrement;
                        restartFlag = true;
                    }
                    mCurrentGameMode.delayP1 = 0;
                    mCurrentGameMode.delayP2 = 0;
                    mCurrentGameMode.timeControlP1 = 0;
                    mCurrentGameMode.timeControlP2 = 0;
                    break;
                case "rapid_mode":
                    /* Rapid Mode: Starting time */
                    if (mCurrentGameMode.startTimeP1 != mStartingTime) {
                        mCurrentGameMode.startTimeP1 = mStartingTime;
                        mCurrentGameMode.startTimeP2 = mStartingTime;
                        restartFlag = true;
                    }
                    mCurrentGameMode.delayP1 = 0;
                    mCurrentGameMode.delayP2 = 0;
                    mCurrentGameMode.timeControlP1 = 0;
                    mCurrentGameMode.timeControlP2 = 0;
                    mCurrentGameMode.incrementP1 = 0;
                    mCurrentGameMode.incrementP2 = 0;
                    break;
                case "rapid_delay_mode":
                    /* Rapid w/delay mode: Starting time and delay. */
                    if (mCurrentGameMode.startTimeP1 != mStartingTime) {
                        mCurrentGameMode.startTimeP1 = mStartingTime;
                        mCurrentGameMode.startTimeP2 = mStartingTime;
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.delayP1 != mDelay) {
                        mCurrentGameMode.delayP1 = mDelay;
                        mCurrentGameMode.delayP2 = mDelay;
                        restartFlag = true;
                    }
                    mCurrentGameMode.timeControlP1 = 0;
                    mCurrentGameMode.timeControlP2 = 0;
                    mCurrentGameMode.incrementP1 = 0;
                    mCurrentGameMode.incrementP2 = 0;
                    break;
                default:
                    /* Must be an error */
                    mCurrentGameMode = new GameMode(
                            " "," ",true,0,0,0,0,0,0,0,0);
                    restartFlag = true;
            }

        /* If it isn't symmetric, the values for each player must be checked */
        } else {
            /* Get pref values using requireNonNull just in case */
            String mWhiteStartingTimeString = sharedPref.getString(WHITE_STARTING_TIME_KEY,
                    "3:0");
            long mWhiteIncrement = Long.parseLong(Objects.requireNonNull(sharedPref.getString(
                    WHITE_INCREMENT_KEY, "0"))) * 1000;
            int mWhiteTimeControl = Integer.parseInt(Objects.requireNonNull(sharedPref.getString(
                    WHITE_TIME_CONTROL_KEY,"0")));
            String mBlackStartingTimeString = sharedPref.getString(
                    BLACK_STARTING_TIME_KEY, "3:0");
            long mBlackIncrement = Long.parseLong(Objects.requireNonNull(sharedPref.getString(
                    BLACK_INCREMENT_KEY, "0"))) * 1000;
            int mBlackTimeControl = Integer.parseInt(Objects.requireNonNull(sharedPref.getString(
                    BLACK_TIME_CONTROL_KEY, "0")));
            /* Parse the time from min and secs into millis */
            assert mWhiteStartingTimeString != null;
            String[] splattedWhiteTime = mWhiteStartingTimeString.split(":");
            long mWhiteStartingTime = Long.parseLong(splattedWhiteTime[0])*60000 +
                                              Long.parseLong(splattedWhiteTime[1]) * 1000;
            assert mBlackStartingTimeString != null;
            String[] splattedBlackTime = mBlackStartingTimeString.split(":");
            long mBlackStartingTime = Long.parseLong(splattedBlackTime[0])*60000 +
                                              Long.parseLong(splattedBlackTime[1]) * 1000;
            /* Apply changes, if anyone changes restarts the clocks */
            if(mCurrentGameMode.startTimeP1 != mWhiteStartingTime){
                mCurrentGameMode.startTimeP1 = mWhiteStartingTime;
                restartFlag = true;
            }
            if(mCurrentGameMode.timeControlP1 != mWhiteTimeControl){
                mCurrentGameMode.timeControlP1 = mWhiteTimeControl;
                restartFlag = true;
            }
            if(mCurrentGameMode.incrementP1 != mWhiteIncrement){
                mCurrentGameMode.incrementP1 = mWhiteIncrement;
                restartFlag = true;
            }
            if(mCurrentGameMode.startTimeP2 != mBlackStartingTime){
                mCurrentGameMode.startTimeP2 = mBlackStartingTime;
                restartFlag = true;
            }
            if(mCurrentGameMode.timeControlP2 != mBlackTimeControl){
                mCurrentGameMode.timeControlP2 = mBlackTimeControl;
                restartFlag = true;
            }
            if(mCurrentGameMode.incrementP2 != mBlackIncrement){
                mCurrentGameMode.incrementP2 = mBlackIncrement;
                restartFlag = true;
            }
        }
        if(restartFlag) restart();
    }


    /**
     * Go to Move recognition activity
     */
    public void goToSpeechRecognition(View view) {
        Intent intent = new Intent(this, SpeechRecognition.class);
        startActivity(intent);
    }


    /**
     * Go to Settings activity
     */
    public void goToSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }


    /**
     * Start or pause time counting
     */
    public void startStop() {
        if(mGamePaused) {
            if(mTurnP1) {
                startDelay(1);
            } else {
                startDelay(2);
            }
            mStartStopBtn.setImageResource(PAUSE_ICON);
            mGamePaused = false;
        } else {
            stop();
            mGamePaused = true;
        }
    }


    /**
     * Stops time, sets flags and icons to "Game paused mode"
     */
    private void stop() {
        mStartStopBtn.setImageResource(PLAY_ICON);
        stopTimer();
        mGamePaused = true;
    }


    /**
     * Restart the clocks
     */
    public void restart() {
        stop();
        initialize();
    }

    /* ============================== Time Methods ============================== */

    /**
     * Ends Player 1 turn and starts player 2. Sets timers, flags, changes colors and increments
     * P1 time if there's increment.
     */
    public void endTurn(int player) {
        if(player == 1) {
            if(mTurnP1 && !mGamePaused) {
                incrementTime(1);
                mMovesCounter++;
                /* Switch enabled time */
                stopTimer();
                startDelay(2);
                /* Switch turns */
                mTurnP1 = false;
            }
        } else {
            if(!mTurnP1 && !mGamePaused) {
                incrementTime(2);
                mMovesCounter++;
                /* Switch enabled time */
                stopTimer();
                startDelay(1);
                /* Initialize times */
                mTurnP1 = true;
            }
        }
    }


    private void startDelay(int player) {
        if(player == 1) {
            if(mCurrentGameMode.delayP1 > 0) {
                long delay = mCurrentGameMode.delayP1;
                mCountDownTimer = new CountDownTimer(delay,TICK_TIME) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mTestDelay.setText(String.format(Locale.getDefault(),"%02d",millisUntilFinished));
                    }
                    @Override
                    public void onFinish() {
                        startTimer(1);
                    }

                }.start();
            } else {
                startTimer(1);
            }
        } else {
            if(mCurrentGameMode.delayP2 > 0) {
                long delay = mCurrentGameMode.delayP2;
                mCountDownTimer = new CountDownTimer(delay,TICK_TIME) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mTestDelay.setText(String.format(Locale.getDefault(),"%02d",millisUntilFinished));
                    }
                    @Override
                    public void onFinish() {
                        startTimer(2);
                    }

                }.start();
            } else {
                startTimer(2);
            }
        }
    }

    /**
     * Start the players clock to start counting down.
     * @param player the players clock we want to start.
     */
    private void startTimer(int player) {
        if(player == 1) {
            mCountDownTimer = new CountDownTimer(mTimeRemainingP1,TICK_TIME) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeRemainingP1 = millisUntilFinished;
                    updateClock(1,mTimeRemainingP1);
                }
                @Override
                public void onFinish() {
                    gameOver(1);
                }

            }.start();
        } else {
            mCountDownTimer = new CountDownTimer(mTimeRemainingP2,TICK_TIME) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeRemainingP2 = millisUntilFinished;
                    updateClock(2,mTimeRemainingP2);
                }
                @Override
                public void onFinish() {
                    gameOver(2);
                }

            }.start();
        }
    }


    /**
     * Stop the Countdown Timer that's currently executing.
     */
    private void stopTimer(){
        if(mCountDownTimer != null) mCountDownTimer.cancel();
    }


    /**
     * Sets the UI of the clock to the actual time remaining for the player.
     * @param player the players clock to modify.
     * @param time the remaining time for that player.
     */
    private void updateClock(int player, long time) {
        /* Get time */
        int minutes = (int) time / 60000;
        int seconds = (int) (time / 1000) % 60;
        int milliseconds = (int) (time % 1000)/100;

        if(player == 1){
            /* Update buttons text to actual time left */
            if(minutes > 0) {
                String timeLeftFormatted = String.format(Locale.getDefault(),
                        "%02d:%02d",minutes,seconds);
                mPlayer1Btn.setText(timeLeftFormatted);
            } else {
                String timeLeftFormatted = String.format(Locale.getDefault(),
                        "%01d.%01d",seconds,milliseconds);
                mPlayer1Btn.setText(timeLeftFormatted);
                /* Check if a player is running out of time */
                if(seconds < 20) {
                    mPlayer1Btn.setTextColor(ACTIVE_PLAYER_LOW_COLOR);
                } else {
                    mPlayer1Btn.setTextColor(BLACK_COLOR);
                }
            }
        } else {
            /* Update buttons text to actual time left */
            if(minutes > 0) {
                String timeLeftFormatted = String.format(Locale.getDefault(),
                        "%02d:%02d",minutes,seconds);
                mPlayer2Btn.setText(timeLeftFormatted);
            } else {
                String timeLeftFormatted = String.format(Locale.getDefault(),
                        "%01d.%01d",seconds,milliseconds);
                mPlayer2Btn.setText(timeLeftFormatted);
                /* Check if a player is running out of time */
                if(seconds < 20) {
                    mPlayer2Btn.setTextColor(ACTIVE_PLAYER_LOW_COLOR);
                } else {
                    mPlayer2Btn.setTextColor(WHITE_COLOR);
                }
            }
        }
    }


    /**
     * Increments the time of the player and calls the update clock function.
     * @param player the player to increment and update.
     */
    private void incrementTime(int player) {
        if(player == 1 && mMovesCounter >= mCurrentGameMode.timeControlP1) {
            mTimeRemainingP1 += mCurrentGameMode.incrementP1;
            updateClock(1,mTimeRemainingP1);
        } else if (player == 2 && mMovesCounter >= mCurrentGameMode.timeControlP2) {
            mTimeRemainingP2 += mCurrentGameMode.incrementP2;
            updateClock(2,mTimeRemainingP2);
        }
    }


    /**
     * Sets the current game mode to the selected mode, searching for the name in all the
     * modes in the list.
     * @param gameMode a string with the gme modes name.
     */
    private void setGameMode(String gameMode){
        for (GameMode mode : gameModes) {
            if(mode.predefinedName.equals(gameMode)) {
                mCurrentGameMode = new GameMode(mode);
            }
        }
    }
    
    /* ============================== Game Control ============================== */

    /**
     * Start game over mode, set flags and stop timers. Disables the play/pause button and
     * sets it to play icon.
     * @param player the player that runned out of time.
     */
    private void gameOver(int player){
        mGamePaused = true;
        mStartStopBtn.setImageResource(PLAY_ICON);
        mStartStopBtn.setEnabled(false);
        /* Sets losers time to 0.0 because of CountDownTimer error. */
        if(player == 1){
            mPlayer1Btn.setText("0.0");
        } else {
            mPlayer2Btn.setText("0.0");
        }
    }


    /**
     * Initializes times and counters, updates layout and sets flags
     */
    private void initialize() {
        resetColors();
        /* Init times for each player */
        mTimeRemainingP1 = mCurrentGameMode.startTimeP1;
        mTimeRemainingP2 = mCurrentGameMode.startTimeP2;
        /* Update times on the UI */
        updateClock(1,mCurrentGameMode.startTimeP1);
        updateClock(2,mCurrentGameMode.startTimeP2);
        /* Set flags */
        mTurnP1 = true;
        mGamePaused = true;
        /* Reset counter */
        mMovesCounter = 0;

        mStartStopBtn.setEnabled(true);
    }

    /* ============================== Layout ============================== */

    private void initializeLayout() {
        mPlayer1Btn = findViewById(R.id.TimeBtn1);
        mPlayer2Btn = findViewById(R.id.TimeBtn2);
        mStartStopBtn = findViewById(R.id.PlayPauseBtn);
        mRestartBtn = findViewById(R.id.RestartBtn);
        mPlayer1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTurn(1);
            }
        });
        mPlayer2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTurn(2);
            }
        });
        mStartStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });
        mRestartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restart();
            }
        });
    }


    private void resetColors(){
        mPlayer1Btn.setBackgroundColor(WHITE_COLOR);
        mPlayer2Btn.setBackgroundColor(BLACK_COLOR);
        mPlayer2Btn.setTextColor(WHITE_COLOR);
        mPlayer1Btn.setTextColor(BLACK_COLOR);
    }
}

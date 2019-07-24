package com.example.chessclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Locale;

import static com.example.chessclock.Constants.ACTIVE_PLAYER_LOW_COLOR;
import static com.example.chessclock.Constants.ACTIVE_PLAYER_COLOR;
import static com.example.chessclock.Constants.BLACK_COLOR;
import static com.example.chessclock.Constants.BLACK_INCREMENT_KEY;
import static com.example.chessclock.Constants.BLACK_STARTING_TIME_KEY;
import static com.example.chessclock.Constants.BLACK_TIME_CONTROL_KEY;
import static com.example.chessclock.Constants.DEFAULT_STARTING_TIME;
import static com.example.chessclock.Constants.IDLE_PLAYER_COLOR;
import static com.example.chessclock.Constants.PAUSE_ICON;
import static com.example.chessclock.Constants.PLAY_ICON;
import static com.example.chessclock.Constants.SAME_TIME_KEY;
import static com.example.chessclock.Constants.STANDARD_GAME_MODE_KEY;
import static com.example.chessclock.Constants.STANDARD_INCREMENT_KEY;
import static com.example.chessclock.Constants.STANDARD_STARTING_TIME_KEY;
import static com.example.chessclock.Constants.STANDARD_TIME_CONTROL_KEY;
import static com.example.chessclock.Constants.TICK_TIME;
import static com.example.chessclock.Constants.WHITE_COLOR;
import static com.example.chessclock.Constants.WHITE_INCREMENT_KEY;
import static com.example.chessclock.Constants.WHITE_STARTING_TIME_KEY;
import static com.example.chessclock.Constants.WHITE_TIME_CONTROL_KEY;

public class MainActivity extends AppCompatActivity {
    
    
    /* Layout Elements */
    Button mPlayer1Btn;
    Button mPlayer2Btn;
    ImageButton mStartStopBtn;

    /* Time Variables */
    public long mStartTimeP1 = DEFAULT_STARTING_TIME;
    public long mStartTimeP2 = DEFAULT_STARTING_TIME;
    public long mIncrementP1;
    public long mIncrementP2;
    private long mTimeRemainingP1;
    private long mTimeRemainingP2;
    public int mTimeControlP1;
    public int mTimeControlP2;
    private String mGameMode;

    /* Script Tools */
    private CountDownTimer mCountDownTimer;
    private int mMovesCounter;

    /* Flags */
    private boolean mTurnP1;
    private boolean mGamePaused;
    private boolean mSymetric;


    /* ============================== Layout & Activities ============================== */
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.preference.PreferenceManager
                .setDefaultValues(this, R.xml.user_preferences, false);


        mPlayer1Btn = findViewById(R.id.TimeBtn1);
        mPlayer2Btn = findViewById(R.id.TimeBtn2);
        mStartStopBtn = findViewById(R.id.PlayPauseBtn);

        getPreferences();
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getPreferences();

    }

    /** Update the values that can be changed by the client from Settings */
    private void getPreferences(){
        boolean restartFlag = false;

        SharedPreferences sharedPref = android.support.v7.preference.PreferenceManager
                        .getDefaultSharedPreferences(this);

        Boolean sameTime = sharedPref.getBoolean(SAME_TIME_KEY,false);

        if(mSymetric != sameTime){
            mSymetric = sameTime;
            restartFlag = true;
        }

        if(sameTime){

            String gameMode = sharedPref.getString(STANDARD_GAME_MODE_KEY,"blitz_3min");

            if(gameMode.equals("custom")){

                mGameMode = gameMode;

                long mStartingTime = Long.parseLong(sharedPref.getString(STANDARD_STARTING_TIME_KEY,
                        "3")) *1000;
                long mIncrement = Long.parseLong(sharedPref.getString(STANDARD_INCREMENT_KEY,
                        "0")) *1000;
                int mTimeControl = Integer.parseInt(sharedPref.getString(STANDARD_TIME_CONTROL_KEY,
                        "0"));

                if(mStartTimeP1 != mStartingTime){
                    mStartTimeP1 = mStartingTime;
                    mStartTimeP2 = mStartingTime;
                    restartFlag = true;
                }

                if(mIncrementP1 != mIncrement){
                    mIncrementP1 = mIncrement;
                    mIncrementP2 = mIncrement;
                    restartFlag = true;
                }

                if(mTimeControlP1 != mTimeControl){
                    mTimeControlP1 = mTimeControl;
                    mTimeControlP2 = mTimeControl;
                    restartFlag = true;
                }

            } else {
                if(mGameMode != gameMode){
                    mGameMode = gameMode;
                    setGameMode(gameMode);
                    restartFlag = true;
                }
            }


        } else {

            long mWhiteStartingTime = Long.parseLong(sharedPref.getString(WHITE_STARTING_TIME_KEY,
                    "3")) * 1000;
            long mWhiteIncrement = Long.parseLong(sharedPref.getString(WHITE_INCREMENT_KEY,
                    "0")) * 1000;
            int mWhiteTimeControl = Integer.parseInt(sharedPref.getString(WHITE_TIME_CONTROL_KEY,
                    "0"));
            long mBlackStartingTime = Long.parseLong(sharedPref.getString(BLACK_STARTING_TIME_KEY,
                    "3")) * 1000;
            long mBlackIncrement = Long.parseLong(sharedPref.getString(BLACK_INCREMENT_KEY,
                    "0")) * 1000;
            int mBlackTimeControl = Integer.parseInt(sharedPref.getString(BLACK_TIME_CONTROL_KEY,
                    "0"));

            if(mStartTimeP1 != mWhiteStartingTime){
                mStartTimeP1 = mWhiteStartingTime;
                restartFlag = true;
            }

            if(mTimeControlP1 != mWhiteTimeControl){
                mTimeControlP1 = mWhiteTimeControl;
                restartFlag = true;
            }

            if(mIncrementP1 != mWhiteIncrement){
                mIncrementP1 = mWhiteIncrement;
                restartFlag = true;
            }

            if(mStartTimeP2 != mBlackStartingTime){
                mStartTimeP2 = mBlackStartingTime;
                restartFlag = true;
            }

            if(mTimeControlP2 != mBlackTimeControl){
                mTimeControlP2 = mBlackTimeControl;
                restartFlag = true;
            }

            if(mIncrementP2 != mBlackIncrement){
                mIncrementP2 = mBlackIncrement;
                restartFlag = true;
            }

        }

        if(restartFlag) restart(null);
    }

    /** Go to Move recognition activity */
    public void goToSpeechRecognition(View view) {

        Intent intent = new Intent(this,SpeechRecognition.class);
        startActivity(intent);
    }


    /** Go to Settings recognition activity */
    public void goToSettings(View view) {

        Intent intent = new Intent(this,Settings.class);
        startActivity(intent);
    }


    /** Start or pause time counting */
    public void startStop(View view){

        if(mGamePaused){
            if(mTurnP1) {
                startTimer1();
            } else {
                startTimer2();
            }
            mStartStopBtn.setImageResource(PAUSE_ICON);
            mGamePaused = false;
        } else {
            stop();
            mGamePaused = true;
        }
    }


    /** Stops time, sets flags and icons to "Game paused mode" */
    private void stop(){

        mStartStopBtn.setImageResource(PLAY_ICON);
        stopTimer();
        mGamePaused = true;
    }


    /** Restart the match */
    public void restart(View view){

        stop();
        initialize();
    }

    /* ============================== Time Methods ============================== */
    
    /** Ends Player 1 turn and starts player 2. Sets timers, flags, changes colors and mIncrements
     * P1 time if there's mIncrement.
     */
    public void timePlayer1(View view){
        if(mTurnP1 && !mGamePaused){
            mIncrement(1);

            mMovesCounter++;

            /* Switch enabled time */
            stopTimer();
            startTimer2();

            /* Switch turns */
            mTurnP1 = false;
        }
    }


    /** Ends Player 2 turn and starts player 1. Sets timers, flags, changes colors and mIncrements
     * P2 time if there's mIncrement.
     */
    public void timePlayer2(View view){
        if(!mTurnP1 && !mGamePaused){
            mIncrement(2);

            mMovesCounter++;

            /* Switch enabled time */
            stopTimer();
            startTimer1();

            /* Initialize times */
            mTurnP1 = true;
        }
    }


    /** Start Player 1 clock */
    private void startTimer1(){
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
    }

    /** Start Player 2 clock */
    private void startTimer2(){
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


    /** Stop time counting */
    private void stopTimer(){
        if(mCountDownTimer != null) mCountDownTimer.cancel();
    }

    /** Update text on the $player to the $time */
    private void updateClock(int player, long time){
        /* Get time */
        int minutes = (int) time / 60000;
        int seconds = (int) (time / 1000) % 60;
        int milliseconds = (int) (time % 1000)/100;

        if(player == 1){
            /* Update buttons text to actual time left */
            if(minutes > 0){
                
                String timeLeftFormatted = String.format(Locale.getDefault(),
                        "%02d:%02d",minutes,seconds);
                mPlayer1Btn.setText(timeLeftFormatted);
            } else {
                
                String timeLeftFormatted = String.format(Locale.getDefault(),
                        "%01d.%01d",seconds,milliseconds);
                mPlayer1Btn.setText(timeLeftFormatted);
                
                /* Check if a player is running out of time */
                if(seconds < 20){ 
                    mPlayer1Btn.setTextColor(ACTIVE_PLAYER_LOW_COLOR);
                } else {
                    mPlayer1Btn.setTextColor(BLACK_COLOR);
                }
            }
        } else {
            /* Update buttons text to actual time left */
            if(minutes > 0){
                String timeLeftFormatted = String.format(Locale.getDefault(),
                        "%02d:%02d",minutes,seconds);
                mPlayer2Btn.setText(timeLeftFormatted);
            } else {
                String timeLeftFormatted = String.format(Locale.getDefault(),
                        "%01d.%01d",seconds,milliseconds);
                mPlayer2Btn.setText(timeLeftFormatted);
                /* Check if a player is running out of time */
                if(seconds < 20){
                    mPlayer2Btn.setTextColor(ACTIVE_PLAYER_LOW_COLOR);
                } else {
                    mPlayer2Btn.setTextColor(WHITE_COLOR);
                }
            }
        }
    }

    
    /** Increments the time of $player by the actual mIncrement and updates both clocks. */
    private void mIncrement(int player) {
        if(player == 1 && mMovesCounter >= mTimeControlP1) {

            mTimeRemainingP1 += mIncrementP1;

        } else if (player == 2 && mMovesCounter >= mTimeControlP2) {

            mTimeRemainingP2 += mIncrementP2;

        }
        updateClock(1,mTimeRemainingP1);
        updateClock(2,mTimeRemainingP2);
    }

    private void setGameMode(String mode){
        switch (mode){
            case "blitz_3min":
                mStartTimeP1 = 180000;
                mStartTimeP2 = 180000;
                mIncrementP1 = 2000;
                mIncrementP2 = 2000;
                mTimeControlP1 = 0;
                mTimeControlP2 = 0;
                break;
            case "blitz_1min":
                mStartTimeP1 = 60000;
                mStartTimeP2 = 60000;
                mIncrementP1 = 2000;
                mIncrementP2 = 2000;
                mTimeControlP1 = 0;
                mTimeControlP2 = 0;
                break;
            default:
                mStartTimeP1 = 0;
                mStartTimeP2 = 0;
                mIncrementP1 = 0;
                mIncrementP2 = 0;
                mTimeControlP1 = 0;
                mTimeControlP2 = 0;
                break;
        }
    }
    
    
    /* ============================== Game Control ============================== */

    
    /** Sets flags and switches to "endgame mode" colors */
    private void gameOver(int player){

        mGamePaused = true;

        mStartStopBtn.setImageResource(PLAY_ICON);
        mStartStopBtn.setEnabled(false);

        if(player == 1){
            mPlayer1Btn.setText("0.0");
        } else {
            mPlayer2Btn.setText("0.0");
        }
    }


    /** Initializes times and counters, updates layout and sets flags */
    private void initialize(){

        setBtnsColor(0);

        mTimeRemainingP1 = mStartTimeP1;
        mTimeRemainingP2 = mStartTimeP2;

        updateClock(1,mStartTimeP1);
        updateClock(2,mStartTimeP2);

        mTurnP1 = true;
        mGamePaused = true;

        mMovesCounter = 0;

        mStartStopBtn.setEnabled(true);
    }

    /* ============================== Layout Design ============================== */


    /** Set buttons background color.
        0: Starting mode.
        1: Player 1 turn.
        2: Player 2 turn.
    */
    private void setBtnsColor(int mode){
        switch (mode){
            case 0 :
                mPlayer1Btn.setBackgroundColor(WHITE_COLOR);
                mPlayer2Btn.setBackgroundColor(BLACK_COLOR);
                mPlayer2Btn.setTextColor(WHITE_COLOR);
                break;
            case 1 :
                mPlayer1Btn.setBackgroundColor(ACTIVE_PLAYER_COLOR);
                mPlayer2Btn.setBackgroundColor(IDLE_PLAYER_COLOR);
                break;
            case 2 :
                mPlayer2Btn.setBackgroundColor(ACTIVE_PLAYER_COLOR);
                mPlayer1Btn.setBackgroundColor(IDLE_PLAYER_COLOR);
                break;

        }
    }

}

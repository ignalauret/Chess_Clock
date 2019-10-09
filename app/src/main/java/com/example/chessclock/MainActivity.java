package com.example.chessclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

import static com.example.chessclock.Constants.*;
import static com.example.chessclock.Constants.WHITE_INCREMENT_KEY;

public class MainActivity extends AppCompatActivity {

    public String TAG = "MainActivity";
    
    /* Layout Elements */
    Button mPlayer1Btn;
    Button mPlayer2Btn;
    ImageButton mStartStopBtn;
    ImageButton mRestartBtn;

    /* Time Variables */
    private long mTimeRemainingP1;
    private long mTimeRemainingP2;
    private GameMode mCurrentGameMode;

    /* Script Tools */
    private CountDownTimer mCountDownTimer;
    private int mMovesCounter; //TODO: Show moves counter on UI.

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
            mCurrentGameMode = new GameMode("def","","def","",
                    true,1,0,0,0,0,0,0,0);
        boolean restartFlag = false;
        SharedPreferences sharedPref = android.support.v7.preference.PreferenceManager
                        .getDefaultSharedPreferences(this);

        /* Check symmetric time or not */
        boolean sameTime = sharedPref.getBoolean(SAME_TIME_KEY,false);
        if(mCurrentGameMode.isSymmetric() != sameTime){
            mCurrentGameMode.setSymmetry(sameTime);
            restartFlag = true;
        }
        /* If symmetric has been selected, then the times for each player are the same. */
        if(sameTime){
            String gameMode = sharedPref.getString(
                    STANDARD_GAME_MODE_KEY,"predefined_mode");
            assert gameMode != null;
            /* Restart if the mode has changed */
            if(!mCurrentGameMode.getName(1).equals(gameMode)) {
                mCurrentGameMode.setName(1,gameMode);
                mCurrentGameMode.setName(2,gameMode);
                restartFlag = true;
            }
            /* Get pref values using requireNonNull just in case */
            String mStartingTimeString = sharedPref.getString(
                    STANDARD_STARTING_TIME_KEY, "3:0");
            long mIncrement = Long.parseLong(Objects.requireNonNull(sharedPref.getString(
                    STANDARD_INCREMENT_KEY, "2"))) * 1000;
            long mDelay = Long.parseLong(Objects.requireNonNull(sharedPref.getString(
                    STANDARD_DELAY_KEY, "0"))) * 1000;
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
                    if (mCurrentGameMode.getStartTime(1) != mStartingTime) {
                        mCurrentGameMode.setStartTime(1,mStartingTime);
                        mCurrentGameMode.setStartTime(2,mStartingTime);
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.getDelay(1) != mDelay) {
                        mCurrentGameMode.setDelay(1, mDelay);
                        mCurrentGameMode.setDelay(2, mDelay);
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.getIncrement(1) != mIncrement) {
                        mCurrentGameMode.setIncrement(1, mIncrement);
                        mCurrentGameMode.setIncrement(2,mIncrement);
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.getTimeControl(1) != mTimeControl) {
                        mCurrentGameMode.setTimeControl(1,mTimeControl);
                        mCurrentGameMode.setTimeControl(2,mTimeControl);
                        restartFlag = true;
                    }
                    break;
                case "predefined_mode":
                    /* Predefined mode: must check which mode is selected */
                    String mPredefinedMode = sharedPref.getString(
                            STANDARD_PREDEFINED_MODE_KEY,"blitz_3min");
                    if (mCurrentGameMode.getPredefinedName(1) == null ||
                                !mCurrentGameMode.getPredefinedName(1).equals(
                                        mPredefinedMode)) {
                        mCurrentGameMode.setPredefinedName(1,mPredefinedMode);
                        mCurrentGameMode.setPredefinedName(2,mPredefinedMode);
                        setGameMode(mPredefinedMode,0);
                        restartFlag = true;
                    }
                    break;
                case "blitz_mode":
                    /* Blitz Mode: Starting time and increment */
                    if (mCurrentGameMode.getStartTime(1) != mStartingTime) {
                        mCurrentGameMode.setStartTime(1,mStartingTime);
                        mCurrentGameMode.setStartTime(2,mStartingTime);
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.getIncrement(1) != mIncrement) {
                        mCurrentGameMode.setIncrement(1, mIncrement);
                        mCurrentGameMode.setIncrement(2,mIncrement);
                        restartFlag = true;
                    }
                    mCurrentGameMode.setDelay(1,0);
                    mCurrentGameMode.setDelay(2,0);
                    mCurrentGameMode.setTimeControl(1,0);
                    mCurrentGameMode.setTimeControl(2,0);
                    break;
                case "rapid_mode":
                    /* Rapid Mode: Starting time */
                    if (mCurrentGameMode.getStartTime(1) != mStartingTime) {
                        mCurrentGameMode.setStartTime(1,mStartingTime);
                        mCurrentGameMode.setStartTime(2,mStartingTime);
                        restartFlag = true;
                    }
                    mCurrentGameMode.setDelay(1,0);
                    mCurrentGameMode.setDelay(2,0);
                    mCurrentGameMode.setTimeControl(1,0);
                    mCurrentGameMode.setTimeControl(2,0);
                    mCurrentGameMode.setIncrement(1,0);
                    mCurrentGameMode.setIncrement(2,0);
                    break;
                case "rapid_delay_mode":
                    /* Rapid w/delay mode: Starting time and delay. */
                    if (mCurrentGameMode.getStartTime(1) != mStartingTime) {
                        mCurrentGameMode.setStartTime(1,mStartingTime);
                        mCurrentGameMode.setStartTime(2,mStartingTime);
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.getDelay(1) != mDelay) {
                        mCurrentGameMode.setDelay(1, mDelay);
                        mCurrentGameMode.setDelay(2, mDelay);
                        restartFlag = true;
                    }
                    mCurrentGameMode.setTimeControl(1,0);
                    mCurrentGameMode.setTimeControl(2,0);
                    mCurrentGameMode.setIncrement(1,0);
                    mCurrentGameMode.setIncrement(2,0);
                    break;
                default:
                    /* Must be an error */
                    mCurrentGameMode = new GameMode("","","","",
                            true,0,0,0,0,0,0,0,0);
                    restartFlag = true;
            }

        /* If it isn't symmetric, the values for each player must be checked */
        } else {
            String whiteGameMode = sharedPref.getString(
                    WHITE_GAME_MODE_KEY,"predefined_mode");
            assert whiteGameMode != null;
            /* Restart if the mode has changed */
            if(!mCurrentGameMode.getName(1).equals(whiteGameMode)) {
                mCurrentGameMode.setName(1,whiteGameMode);
                restartFlag = true;
            }
            /* Get pref values using requireNonNull just in case */
            String mWhiteStartingTimeString = sharedPref.getString(
                    WHITE_STARTING_TIME_KEY, "3:0");
            long mWhiteIncrement = Long.parseLong(Objects.requireNonNull(sharedPref.getString(
                    WHITE_INCREMENT_KEY, "1"))) * 1000;
            long mWhiteDelay = Long.parseLong(Objects.requireNonNull(sharedPref.getString(
                    WHITE_DELAY_KEY, "1"))) * 1000;
            /* Parse the time from min and secs into millis */
            assert mWhiteStartingTimeString != null;
            String[] whiteSplattedTime = mWhiteStartingTimeString.split(":");
            long mWhiteStartingTime = Long.parseLong(whiteSplattedTime[0]) * 60000 +
                                         Long.parseLong(whiteSplattedTime[1]) * 1000;
            /* Check the correct settings depending on the selected game mode */
            switch(whiteGameMode) {
                case "custom_mode":
                    /* Custom Mode: Check all the prefs */
                    if (mCurrentGameMode.getStartTime(1) != mWhiteStartingTime) {
                        mCurrentGameMode.setStartTime(1, mWhiteStartingTime);
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.getDelay(1) != mWhiteDelay) {
                        mCurrentGameMode.setDelay(1, mWhiteDelay);
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.getIncrement(1) != mWhiteIncrement) {
                        mCurrentGameMode.setIncrement(1, mWhiteIncrement);
                        restartFlag = true;
                    }
                    break;
                case "predefined_mode":
                    /* Predefined mode: must check which mode is selected */
                    String mPredefinedMode = sharedPref.getString(
                            WHITE_PREDEFINED_MODE_KEY, "blitz_3min");
                    if (mCurrentGameMode.getPredefinedName(1) == null ||
                                !mCurrentGameMode.getPredefinedName(1).equals(
                                        mPredefinedMode)) {
                        mCurrentGameMode.setPredefinedName(1,mPredefinedMode);
                        setGameMode(mPredefinedMode,1);
                        restartFlag = true;
                    }
                    break;
                case "blitz_mode":
                    /* Blitz Mode: Starting time and increment */
                    if (mCurrentGameMode.getStartTime(1) != mWhiteStartingTime) {
                        mCurrentGameMode.setStartTime(1, mWhiteStartingTime);
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.getIncrement(1) != mWhiteIncrement) {
                        mCurrentGameMode.setIncrement(1, mWhiteIncrement);
                        restartFlag = true;
                    }
                    mCurrentGameMode.setDelay(1, 0);
                    break;
                case "rapid_mode":
                    /* Rapid Mode: Starting time */
                    if (mCurrentGameMode.getStartTime(1) != mWhiteStartingTime) {
                        mCurrentGameMode.setStartTime(1, mWhiteStartingTime);
                        restartFlag = true;
                    }
                    mCurrentGameMode.setDelay(1, 0);
                    mCurrentGameMode.setIncrement(1, 0);
                    break;
                case "rapid_delay_mode":
                    /* Rapid w/delay mode: Starting time and delay. */
                    if (mCurrentGameMode.getStartTime(1) != mWhiteStartingTime) {
                        mCurrentGameMode.setStartTime(1, mWhiteStartingTime);
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.getDelay(1) != mWhiteDelay) {
                        mCurrentGameMode.setDelay(1, mWhiteDelay);
                        restartFlag = true;
                    }
                    mCurrentGameMode.setIncrement(1, 0);
                    break;
                default:
                    /* Must be an error */
                    mCurrentGameMode = new GameMode("", "","","",
                            true,0,0,0,0,0,0, 0, 0);
                    restartFlag = true;
            }
            String blackGameMode = sharedPref.getString(
                    BLACK_GAME_MODE_KEY,"predefined_mode");
            assert blackGameMode != null;
            /* Restart if the mode has changed */
            if(!mCurrentGameMode.getName(2).equals(blackGameMode)) {
                mCurrentGameMode.setName(2,blackGameMode);
                restartFlag = true;
            }
            /* Get pref values using requireNonNull just in case */
            String mBlackStartingTimeString = sharedPref.getString(
                    BLACK_STARTING_TIME_KEY, "3:0");
            long mBlackIncrement = Long.parseLong(Objects.requireNonNull(sharedPref.getString(
                    BLACK_INCREMENT_KEY, "1"))) * 1000;
            long mBlackDelay = Long.parseLong(Objects.requireNonNull(sharedPref.getString(
                    BLACK_DELAY_KEY, "1"))) * 1000;
            /* Parse the time from min and secs into millis */
            assert mBlackStartingTimeString != null;
            String[] blackSplattedTime = mBlackStartingTimeString.split(":");
            long mBlackStartingTime = Long.parseLong(blackSplattedTime[0]) * 60000 +
                                              Long.parseLong(blackSplattedTime[1]) * 1000;
            /* Check the correct settings depending on the selected game mode */
            switch(blackGameMode) {
                case "custom_mode":
                    /* Custom Mode: Check all the prefs */
                    if (mCurrentGameMode.getStartTime(2) != mBlackStartingTime) {
                        mCurrentGameMode.setStartTime(2, mBlackStartingTime);
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.getDelay(2) != mBlackDelay) {
                        mCurrentGameMode.setDelay(2, mBlackDelay);
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.getIncrement(2) != mBlackIncrement) {
                        mCurrentGameMode.setIncrement(2, mBlackIncrement);
                        restartFlag = true;
                    }
                    break;
                case "predefined_mode":
                    /* Predefined mode: must check which mode is selected */
                    String mPredefinedMode = sharedPref.getString(
                            BLACK_PREDEFINED_MODE_KEY, "blitz_3min");
                    if (mCurrentGameMode.getPredefinedName(1) == null ||
                                !mCurrentGameMode.getPredefinedName(1).equals(
                                        mPredefinedMode)) {
                        mCurrentGameMode.setPredefinedName(1,mPredefinedMode);
                        setGameMode(mPredefinedMode,2);
                        restartFlag = true;
                    }
                    break;
                case "blitz_mode":
                    /* Blitz Mode: Starting time and increment */
                    if (mCurrentGameMode.getStartTime(2) != mBlackStartingTime) {
                        mCurrentGameMode.setStartTime(2, mBlackStartingTime);
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.getIncrement(2) != mBlackIncrement) {
                        mCurrentGameMode.setIncrement(2, mBlackIncrement);
                        restartFlag = true;
                    }
                    mCurrentGameMode.setDelay(2, 0);
                    break;
                case "rapid_mode":
                    /* Rapid Mode: Starting time */
                    if (mCurrentGameMode.getStartTime(2) != mBlackStartingTime) {
                        mCurrentGameMode.setStartTime(2, mBlackStartingTime);
                        restartFlag = true;
                    }
                    mCurrentGameMode.setDelay(2, 0);
                    mCurrentGameMode.setIncrement(2, 0);
                    break;
                case "rapid_delay_mode":
                    /* Rapid w/delay mode: Starting time and delay. */
                    if (mCurrentGameMode.getStartTime(2) != mBlackStartingTime) {
                        mCurrentGameMode.setStartTime(2, mBlackStartingTime);
                        restartFlag = true;
                    }
                    if (mCurrentGameMode.getDelay(2) != mBlackDelay) {
                        mCurrentGameMode.setDelay(2, mBlackDelay);
                        restartFlag = true;
                    }
                    mCurrentGameMode.setIncrement(2, 0);
                    break;
                default:
                    /* Must be an error */
                    mCurrentGameMode = new GameMode("", "","","",
                            true, 0, 0, 0, 0, 0, 0, 0, 0);
                    restartFlag = true;
            }
        }
        if(restartFlag) restart();
    }


    /**
     * Go to Move recognition activity.
     * TODO: Speech Recognition feature. Just shows toast of "Not available yet".
     */
    public void goToSpeechRecognition(View view) {
        //Intent intent = new Intent(this, SpeechRecognition.class);
        //startActivity(intent);
        Toast.makeText(this,"This feature will be available soon.",Toast.LENGTH_LONG).show();
    }


    /**
     * Go to Settings activity.
     */
    public void goToSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    /* ============================== Time Methods ============================== */

    /**
     * Start or pause time counting on the clock, set the icon play/pause and set game paused flag.
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
     * Restart the clocks and the UI.
     */
    public void restart() {
        stop();
        initialize();
    }

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


    private void startDelay(final int player) {
            if(mCurrentGameMode.getDelay(player) > 0) {
                long delay = mCurrentGameMode.getDelay(player);
                mCountDownTimer = new CountDownTimer(delay,TICK_TIME) {
                    @Override
                    public void onTick(long millisUntilFinished) {}
                    @Override
                    public void onFinish() {
                        startTimer(player);
                    }

                }.start();
            } else {
                startTimer(player);
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
        if(player == 1) {
            mTimeRemainingP1 += mCurrentGameMode.getIncrement(1);
            updateClock(1,mTimeRemainingP1);
        } else if (player == 2) {
            mTimeRemainingP2 += mCurrentGameMode.getIncrement(2);
            updateClock(2,mTimeRemainingP2);
        }
    }


    /**
     * Sets the current game mode to the selected mode, searching for the name in all the
     * modes in the list.
     * @param gameMode a string with the gme modes name.
     */
    private void setGameMode(String gameMode, int player){
        if(player == 0) {
            for (GameMode mode : gameModes) {
                if(mode.getPredefinedName(1).equals(gameMode)) {
                    mCurrentGameMode = new GameMode(mode);
                }
            }
        } else {
            for (GameMode mode : gameModes) {
                if(mode.getPredefinedName(player).equals(gameMode)) {
                    mCurrentGameMode.copyValues(mode, player);
                }
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
        mTimeRemainingP1 = mCurrentGameMode.getStartTime(1);
        mTimeRemainingP2 = mCurrentGameMode.getStartTime(2);
        /* Update times on the UI */
        updateClock(1,mCurrentGameMode.getStartTime(1));
        updateClock(2,mCurrentGameMode.getStartTime(2));
        /* Set flags */
        mTurnP1 = true;
        mGamePaused = true;
        /* Reset counter */
        mMovesCounter = 0;

        mStartStopBtn.setEnabled(true);
    }

    /* ============================== Layout ============================== */

    /**
     * References the views in the layout, hides app bar in main screen and sets onClick listeners
     * to the buttons.
     */
    private void initializeLayout() {
        if(getActionBar() != null) getActionBar().hide();
        if(getSupportActionBar() != null) getSupportActionBar().hide();
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

    /**
     * Sets the text colors to its default values (Black and White).
     */
    private void resetColors(){
        mPlayer2Btn.setTextColor(WHITE_COLOR);
        mPlayer1Btn.setTextColor(BLACK_COLOR);
    }
}

package com.example.chessclock;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;


import static com.example.chessclock.Constants.*;


public class MainSettingsFragment extends PreferenceFragmentCompat {

    public String TAG = "MainSettingsFragment";

    public MainSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.user_preferences, s);
        bindPreferences(findPreference(SAME_TIME_KEY));
        bindPreferences(findPreference(STANDARD_GAME_MODE_KEY));
        bindPreferences(findPreference(WHITE_GAME_MODE_KEY));
        bindPreferences(findPreference(BLACK_GAME_MODE_KEY));

        setDefaults();
    }


    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;
        /* Binding the NumberPickers on dialog creation */
        if (preference instanceof NumberPickerPreference) {

            dialogFragment = new NumberPickerPreferenceCompat();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            dialogFragment.setArguments(bundle);

        } else if(preference instanceof DoubleNumberPickerPreference) {

            dialogFragment = new DoubleNumberPickerPreferenceCompat();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            dialogFragment.setArguments(bundle);

        }

        /* Show dialog on the fragment */
        if (dialogFragment != null && this.getFragmentManager() != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(),
                    "android.support.v7.preference.PreferenceFragment.DIALOG");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    private void setDefaults() {
        changeDisplay("");
        ListPreference whiteGameMode = (ListPreference) findPreference(WHITE_GAME_MODE_KEY);
        ListPreference blackGameMode = (ListPreference) findPreference(BLACK_GAME_MODE_KEY);

        if(whiteGameMode.getValue() == null) whiteGameMode.setValueIndex(4);
        if(blackGameMode.getValue() == null) blackGameMode.setValueIndex(4);
    }

    /**
     * The listener that manages the changes in the preferences.
     */
    private Preference.OnPreferenceChangeListener listener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.d(TAG, "onPreferenceChange: "+ newValue.toString());
                    switch (preference.getKey()) {
                        case SAME_TIME_KEY:
                            changeDisplay(newValue.toString());
                            break;
                        case STANDARD_GAME_MODE_KEY:
                            checkGameMode(newValue.toString());
                            break;
                        case WHITE_GAME_MODE_KEY:
                            checkWhiteGameMode(newValue.toString());
                            break;
                        case BLACK_GAME_MODE_KEY:
                            checkBlackGameMode(newValue.toString());
                            break;
                    }

                    return true;
                }
            };

    /**
     * Bind a preference to the listener to call onPreferenceChange when its modified.
     * @param pref the preference to bind.
     */
    private void bindPreferences(Preference pref){
        pref.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(pref,
                PreferenceManager.getDefaultSharedPreferences(pref.getContext())
                .getString(pref.toString(),""));
    }

    /**
     * Display standard time if Symmetric switch is checked, else display white and black time
     * @param value empty string at start, and a boolean in string format that says if the switch
     *              is enabled.
     */
    private void changeDisplay(String value){
        boolean mode;
        if(value.equals("")){
            SwitchPreference mSwitch = (SwitchPreference) findPreference(SAME_TIME_KEY);
            mode = mSwitch.isChecked();
        } else {
            mode = Boolean.parseBoolean(value);
        }
        PreferenceCategory mSameTimeCategory =
                (PreferenceCategory) findPreference(STANDARD_TIME_CATEGORY_KEY);
        PreferenceCategory mWhiteTimeCategory =
                (PreferenceCategory) findPreference(WHITE_TIME_CATEGORY_KEY);
        PreferenceCategory mBlackTimeCategory =
                (PreferenceCategory) findPreference(BLACK_TIME_CATEGORY_KEY);
        if (mode) {
            mWhiteTimeCategory.setVisible(false);
            mBlackTimeCategory.setVisible(false);
            mSameTimeCategory.setVisible(true);
        } else {
            mWhiteTimeCategory.setVisible(true);
            mBlackTimeCategory.setVisible(true);
            mSameTimeCategory.setVisible(false);
        }
    }


    /**
     * Display each symmetric mode setting if its flag is true, or hide it if its false.
     * @param pre Predefined Mode setting flag.
     * @param st Starting Time setting flag.
     * @param d Delay setting flag.
     * @param i Increment setting flag.
     */
    private void showTimeDisplays(boolean pre, boolean st, boolean d, boolean i){
        Preference mStartingTime = findPreference(STANDARD_STARTING_TIME_KEY);
        Preference mDelay = findPreference(STANDARD_DELAY_KEY);
        Preference mIncrement = findPreference(STANDARD_INCREMENT_KEY);
        //Preference mTimeControl = findPreference(STANDARD_TIME_CONTROL_KEY);
        Preference mPredefinedMode = findPreference(STANDARD_PREDEFINED_MODE_KEY);

        /* Set prefs visible depending on the parameters */
        mPredefinedMode.setVisible(pre);
        mStartingTime.setVisible(st);
        mDelay.setVisible(d);
        mIncrement.setVisible(i);
        //mTimeControl.setVisible(tc);
    }

    /**
     * Display each white setting if its flag is true, or hide it if its false.
     * @param pre Predefined Mode setting flag.
     * @param st Starting Time setting flag.
     * @param d Delay setting flag.
     * @param i Increment setting flag.
     */
    private void showWhitePrefs(boolean pre, boolean st, boolean d, boolean i) {
        Preference mStartingTime = findPreference(WHITE_STARTING_TIME_KEY);
        Preference mDelay = findPreference(WHITE_DELAY_KEY);
        Preference mIncrement = findPreference(WHITE_INCREMENT_KEY);
        Preference mPredefinedMode = findPreference(WHITE_PREDEFINED_MODE_KEY);

        /* Set prefs visible depending on the parameters */
        mPredefinedMode.setVisible(pre);
        mStartingTime.setVisible(st);
        mDelay.setVisible(d);
        mIncrement.setVisible(i);
        PreferenceCategory mWhiteTimeCategory =
                (PreferenceCategory) findPreference(WHITE_TIME_CATEGORY_KEY);
        PreferenceCategory mBlackTimeCategory =
                (PreferenceCategory) findPreference(BLACK_TIME_CATEGORY_KEY);
        mWhiteTimeCategory.setVisible(false);
        mBlackTimeCategory.setVisible(false);
        mWhiteTimeCategory.setVisible(true);
        mBlackTimeCategory.setVisible(true);
    }

    /**
     * Display each black setting if its flag is true, or hide it if its false.
     * @param pre Predefined Mode setting flag.
     * @param st Starting Time setting flag.
     * @param d Delay setting flag.
     * @param i Increment setting flag.
     */
    private void showBlackPrefs(boolean pre, boolean st, boolean d, boolean i) {
        Preference mStartingTime = findPreference(BLACK_STARTING_TIME_KEY);
        Preference mDelay = findPreference(BLACK_DELAY_KEY);
        Preference mIncrement = findPreference(BLACK_INCREMENT_KEY);
        Preference mPredefinedMode = findPreference(BLACK_PREDEFINED_MODE_KEY);

        /* Set prefs visible depending on the parameters */
        mPredefinedMode.setVisible(pre);
        mStartingTime.setVisible(st);
        mDelay.setVisible(d);
        mIncrement.setVisible(i);
        PreferenceCategory mWhiteTimeCategory =
                (PreferenceCategory) findPreference(WHITE_TIME_CATEGORY_KEY);
        PreferenceCategory mBlackTimeCategory =
                (PreferenceCategory) findPreference(BLACK_TIME_CATEGORY_KEY);
        mWhiteTimeCategory.setVisible(false);
        mBlackTimeCategory.setVisible(false);
        mWhiteTimeCategory.setVisible(true);
        mBlackTimeCategory.setVisible(true);
    }


    /**
     * Manage the symmetric mode settings that must be displayed depending in the selected game mode.
     * @param gameMode the current selected game mode.
     */
    private void checkGameMode(String gameMode){
        switch (gameMode){
            case "":
                /* Called with this argument on preference resume */
                ListPreference mList = (ListPreference) findPreference(STANDARD_GAME_MODE_KEY);
                if(mList.getValue() != null) {
                    checkGameMode(mList.getValue());
                } else {
                    showTimeDisplays(false,true,true,true);
                }
                break;
            case "custom_mode":
                showTimeDisplays(false,true,true,true);
                break;
            case "predefined_mode":
                showTimeDisplays(true,false,false,false);
                break;
            case "blitz_mode":
                showTimeDisplays(false,true,false,true);
                break;
            case "rapid_mode":
                showTimeDisplays(false,true,false,false);
                break;
            case "rapid_delay_mode":
                showTimeDisplays(false,true,true,false);
                break;
            default:
                showTimeDisplays(true,true,true,true);
                break;

        }
    }

    /**
     * Manage the white settings that must be displayed depending in the selected game mode.
     * @param gameMode the current selected game mode.
     */
    private void checkWhiteGameMode(String gameMode){
        switch (gameMode){
            case "":
                /* Called with this argument on preference resume */
                ListPreference mList = (ListPreference) findPreference(WHITE_GAME_MODE_KEY);
                if(mList.getValue() != null) {
                    checkWhiteGameMode(mList.getValue());
                } else {
                    showWhitePrefs(false,true,true,true);
                }
                break;
            case "custom_mode":
                showWhitePrefs(false,true,true,true);
                break;
            case "predefined_mode":
                showWhitePrefs(true,false,false,false);
                break;
            case "blitz_mode":
                showWhitePrefs(false,true,false,true);
                break;
            case "rapid_mode":
                showWhitePrefs(false,true,false,false);
                break;
            case "rapid_delay_mode":
                showWhitePrefs(false,true,true,false);
                break;
            default:
                showWhitePrefs(true,true,true,true);
                break;

        }
    }

    /**
     * Manage the black settings that must be displayed depending in the selected game mode.
     * @param gameMode the current selected game mode.
     */
    private void checkBlackGameMode(String gameMode){
        switch (gameMode){
            case "":
                /* Called with this argument on preference resume */
                ListPreference mList = (ListPreference) findPreference(BLACK_GAME_MODE_KEY);
                if(mList.getValue() != null) {
                    checkBlackGameMode(mList.getValue());
                } else {
                    showBlackPrefs(false,true,true,true);
                }
                break;
            case "custom_mode":
                showBlackPrefs(false,true,true,true);
                break;
            case "predefined_mode":
                showBlackPrefs(true,false,false,false);
                break;
            case "blitz_mode":
                showBlackPrefs(false,true,false,true);
                break;
            case "rapid_mode":
                showBlackPrefs(false,true,false,false);
                break;
            case "rapid_delay_mode":
                showBlackPrefs(false,true,true,false);
                break;
            default:
                showBlackPrefs(true,true,true,true);
                break;

        }
    }
}

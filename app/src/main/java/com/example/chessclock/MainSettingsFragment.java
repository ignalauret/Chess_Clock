package com.example.chessclock;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import static com.example.chessclock.Constants.*;


public class MainSettingsFragment extends PreferenceFragmentCompat {

    public MainSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.user_preferences, s);
        bindPreferences(findPreference(SAME_TIME_KEY));
        bindPreferences(findPreference(STANDARD_GAME_MODE_KEY));
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


    /** Bind a preference to the listener to call onPreferenceChange when its modified */
    private void bindPreferences(Preference pref){
        pref.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(pref,
                PreferenceManager.getDefaultSharedPreferences(pref.getContext())
                .getString(pref.toString(),""));
    }


    /** Listens to changes on BOUND preferences */
    private Preference.OnPreferenceChangeListener listener =
            new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            switch (preference.getKey()) {
                case SAME_TIME_KEY:
                    changeDisplay(newValue.toString());
                    break;
                case STANDARD_GAME_MODE_KEY:
                    checkGameMode(newValue.toString());
                    break;
            }

            return true;
        }
    };


    /** Display standard time if Symmetric switch is checked, else display white and black time */
    private void changeDisplay(String value){
        boolean mode;
        if(value.equals("")){
            SwitchPreference mSwitch = (SwitchPreference) findPreference(SAME_TIME_KEY);
            mode = mSwitch.isChecked();
        } else mode = Boolean.parseBoolean(value);

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


    /** Display manual time settings if show is true, else hide them */
    private void showTimeDisplays(boolean pre, boolean st, boolean d, boolean i, boolean tc){
        Preference mStartingTime = findPreference(STANDARD_STARTING_TIME_KEY);
        Preference mDelay = findPreference(STANDARD_DELAY_KEY);
        Preference mIncrement = findPreference(STANDARD_INCREMENT_KEY);
        Preference mTimeControl = findPreference(STANDARD_TIME_CONTROL_KEY);
        Preference mPredefinedMode = findPreference(STANDARD_PREDEFINED_MODE_KEY);

        /* Set prefs visible depending on the parameters */
        mPredefinedMode.setVisible(pre);
        mStartingTime.setVisible(st);
        mDelay.setVisible(d);
        mIncrement.setVisible(i);
        mTimeControl.setVisible(tc);
    }


    /** Display manual time settings if custom mode is selected */
    private void checkGameMode(String gameMode){
        switch (gameMode){
            case "":
                /* Called with this argument on preference resume */
                ListPreference mList = (ListPreference) findPreference(STANDARD_GAME_MODE_KEY);
                if(mList.getValue() != null) {
                    checkGameMode(mList.getValue());
                } else {
                    showTimeDisplays(false,true,true,true,true);
                }
                break;
            case "custom_mode":
                showTimeDisplays(false,true,true,true,true);
                break;
            case "predefined_mode":
                showTimeDisplays(true,false,false,false,false);
                break;
            case "blitz_mode":
                showTimeDisplays(false,true,false,true,false);
                break;
            case "rapid_mode":
                showTimeDisplays(false,true,false,false,false);
                break;
            case "rapid_delay_mode":
                showTimeDisplays(false,true,true,false,false);
                break;
            default:
                showTimeDisplays(true,true,true,true,true);
                break;

        }
    }
}

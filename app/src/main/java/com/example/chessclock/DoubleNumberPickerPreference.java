package com.example.chessclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

/** A Number Picker widget for the preference screen. */
public class DoubleNumberPickerPreference extends DialogPreference
{
    /* Value */
    int minutes;
    int seconds;

    /** Constructor */
    public DoubleNumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    /** Get the initial value as a String and sets the time variable */
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String value;
        if (restoreValue) {
            if (defaultValue == null) value = getPersistedString("3:0");
            else value = getPersistedString(defaultValue.toString());
        }
        else {
            value = defaultValue.toString();
        }

        String[] splattedString = value.split(":");
        minutes = Integer.parseInt(splattedString[0]);
        seconds = Integer.parseInt(splattedString[1]);
    }

    void persistStringValue(String value) {
        persistString(value);
    }
}
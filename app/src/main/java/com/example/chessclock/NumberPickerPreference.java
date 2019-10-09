package com.example.chessclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * A Number picker that can be used in the preference screen.
 */
public class NumberPickerPreference extends DialogPreference
{
    /* Value */
    int time;

    /**
     * Constructor.
     * @param context the context where is created.
     * @param attrs the settings.
     */
    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    /**
     * Get the initial value as a String and sets the time variable.
     * @param restoreValue new value.
     * @param defaultValue value that is used as default at start.
     */
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String value;
        if (restoreValue) {
            if (defaultValue == null) value = getPersistedString("0");
            else value = getPersistedString(defaultValue.toString());
        }
        else {
            value = defaultValue.toString();
        }

        time = Integer.parseInt(value);
    }

    /**
     * Makes a String into a persisting string in the object.
     * @param value the string to make persistent.
     */
    void persistStringValue(String value) {
        persistString(value);
    }
}
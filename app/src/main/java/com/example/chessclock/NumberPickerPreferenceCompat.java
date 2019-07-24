package com.example.chessclock;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.NumberPicker;


public class NumberPickerPreferenceCompat extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment
{
    NumberPicker mNumberPicker;


    @Override
    protected View onCreateDialogView(Context context)
    {
        mNumberPicker = new NumberPicker(context);
        return (mNumberPicker);
    }

    @Override
    protected void onBindDialogView(View v)
    {
        super.onBindDialogView(v);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setMaxValue(60);
        NumberPickerPreference pref = (NumberPickerPreference) getPreference();
        mNumberPicker.setValue(pref.time);
    }

    @Override
    public void onDialogClosed(boolean positiveResult)
    {
        if (positiveResult)
        {
            NumberPickerPreference pref = (NumberPickerPreference) getPreference();
            pref.time = mNumberPicker.getValue();

            String value = "" + pref.time;
            pref.persistStringValue(value);
        }
    }

    @Override
    public Preference findPreference(CharSequence charSequence)
    {
        return getPreference();
    }
}
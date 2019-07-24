package com.example.chessclock;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.NumberPicker;


public class NumberPickerPreferenceCompat extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment
{
    NumberPicker numberPicker;

    @Override
    protected View onCreateDialogView(Context context)
    {
        numberPicker = new NumberPicker(context);
        return (numberPicker);
    }

    @Override
    protected void onBindDialogView(View v)
    {
        super.onBindDialogView(v);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(7);
        NumberPickerPreference pref = (NumberPickerPreference) getPreference();
        numberPicker.setValue(pref.time);
    }

    @Override
    public void onDialogClosed(boolean positiveResult)
    {
        if (positiveResult)
        {
            NumberPickerPreference pref = (NumberPickerPreference) getPreference();
            pref.time = numberPicker.getValue();

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
package com.example.chessclock;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;


public class DoubleNumberPickerPreferenceCompat extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment
{
    NumberPicker mNumberPicker1;
    NumberPicker mNumberPicker2;
    LinearLayout mLinearLayout;


    @Override
    protected View onCreateDialogView(Context context)
    {
        mNumberPicker1 = new NumberPicker(context);
        mNumberPicker2 = new NumberPicker(context);
        mLinearLayout = new LinearLayout(context);
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mLinearLayout.setGravity(Gravity.CENTER);
        mLinearLayout.addView(mNumberPicker1);
        mLinearLayout.addView(mNumberPicker2);
        return (mLinearLayout);
    }

    @Override
    protected void onBindDialogView(View v)
    {
        super.onBindDialogView(v);
        configViews();
    }

    @Override
    public void onDialogClosed(boolean positiveResult)
    {
        if (positiveResult)
        {
            DoubleNumberPickerPreference pref = (DoubleNumberPickerPreference) getPreference();
            pref.minutes = mNumberPicker1.getValue();
            pref.seconds = mNumberPicker2.getValue();

            String value = "" + pref.minutes + ":" + pref.seconds;
            pref.persistStringValue(value);
        }
    }

    @Override
    public Preference findPreference(CharSequence charSequence)
    {
        return getPreference();
    }

    private void configViews(){

        DoubleNumberPickerPreference pref = (DoubleNumberPickerPreference) getPreference();

        mNumberPicker1.setMinValue(0);
        mNumberPicker1.setMaxValue(60);
        mNumberPicker2.setMinValue(0);
        mNumberPicker2.setMaxValue(60);

        mNumberPicker1.setValue(pref.minutes);
        mNumberPicker2.setValue(pref.seconds);
    }
}
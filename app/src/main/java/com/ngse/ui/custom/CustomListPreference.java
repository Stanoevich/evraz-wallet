package com.ngse.ui.custom;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;


import android.support.v7.preference.ListPreference;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;


public class CustomListPreference extends ListPreference {
    private static int[] ATTRS = {android.R.attr.theme};
    private ContextThemeWrapper mContextWrapper;


    public CustomListPreference(Context context) {
        this(context, null);
    }

    public CustomListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mContextWrapper = new ContextThemeWrapper(context, a.getResourceId(0, 0));
        a.recycle();
    }

    @Override
    public Context getContext() {
        return mContextWrapper;
    }
}
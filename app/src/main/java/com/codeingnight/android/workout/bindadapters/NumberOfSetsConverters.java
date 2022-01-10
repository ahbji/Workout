package com.codeingnight.android.workout.bindadapters;

import android.content.Context;

import androidx.databinding.InverseMethod;

import com.codeingnight.android.workout.R;

public class NumberOfSetsConverters {

    @InverseMethod("stringToSetArray")
    public static String setArrayToString(Context context, int[] value) {
        return context.getString(R.string.sets_format, value[0] + 1, value[1]);
    }

    public static int[] stringToSetArray(Context context, String value) {
        if (value.isEmpty()) {
            return new int[]{0, 0};
        }
        return new int[]{0, Integer.parseInt(value)};
    }
}

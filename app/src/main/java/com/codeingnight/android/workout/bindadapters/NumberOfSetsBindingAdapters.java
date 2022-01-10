package com.codeingnight.android.workout.bindadapters;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

public class NumberOfSetsBindingAdapters {
    @BindingAdapter("numberOfSets")
    public static void setNumberOfSets(@NonNull EditText view, String value) {
        view.setText(value);
    }

    @NonNull
    @InverseBindingAdapter(attribute = "numberOfSets")
    public static String getNumberOfSets(@NonNull EditText view) {
        return view.getText().toString();
    }

    @BindingAdapter("numberOfSetsAttrChanged")
    public static void setListener(@NonNull EditText view, final InverseBindingListener listener) {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View focusedView, boolean hasFocus) {
                TextView textView = (TextView) focusedView;
                if (hasFocus)
                    textView.setText("");
                else {
                    // 如果 focus left, update the listener
                    if (listener != null) {
                        listener.onChange();
                    }
                }
            }
        });
    }
}

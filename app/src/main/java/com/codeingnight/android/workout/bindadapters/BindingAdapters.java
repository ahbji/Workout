package com.codeingnight.android.workout.bindadapters;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.codeingnight.android.workout.R;

public class BindingAdapters {
    @BindingAdapter("clearOnFocusAndDispatch")
    public static void clearOnFocusAndDispatch(EditText view, View.OnFocusChangeListener listener) {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View focusedView, boolean hasFocus) {
                TextView textView = (TextView) focusedView;
                if (hasFocus) {
                    view.setTag(R.id.previous_value, textView.getText());
                    textView.setText("");
                } else {
                    if (textView.getText().toString().isEmpty()) {
                        CharSequence tag = (CharSequence)textView.getTag(R.id.previous_value);
                        if (tag != null) {
                            textView.setText(tag);
                        } else {
                            textView.setText("");
                        }
                    }
                    if (listener != null) {
                        listener.onFocusChange(focusedView, hasFocus);
                    }
                }
            }
        });
    }

    @BindingAdapter("hideKeyboardOnInputDone")
    public static void hideKeyboardOnInputDone(EditText view, boolean enabled) {
        if (!enabled)
            return;
        view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return false;
            }
        });
    }

    @BindingAdapter("clearTextOnFocus")
    public static void clearTextOnFocus(EditText view, boolean enabled) {
        if (enabled) {
            clearOnFocusAndDispatch(view, null);
        } else {
            view.setOnFocusChangeListener(null);
        }
    }
}

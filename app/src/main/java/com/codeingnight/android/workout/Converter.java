package com.codeingnight.android.workout;

import android.annotation.TargetApi;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {
    public static String fromTenthsToSeconds(int tenths) {
        if (tenths < 600) {
            return String.format("%.1f", tenths / 10.0);
        } else {
            int minutes = (tenths / 10) / 60;
            int seconds = (tenths / 10) % 60;
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    public static int cleanSecondsString(String seconds) {
        String filteredValue = seconds.replaceAll("[^\\d:.]", "");
        if (filteredValue.isEmpty())
            return 0;
        List<Integer> elements = Arrays.stream(filteredValue.split(":"))
                .map((it) -> Double.valueOf(Math.rint(Double.parseDouble(it))).intValue())
                .collect(Collectors.toList());
        if (elements.size() > 2) {
            return 0;
        } else if (elements.size() > 1) {
            int result;
            result = elements.get(0) * 60;
            result += elements.get(1);
            return result * 10;
        } else {
            return elements.get(0) * 10;
        }
    }
}

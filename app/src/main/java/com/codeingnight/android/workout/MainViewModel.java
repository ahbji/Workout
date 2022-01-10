package com.codeingnight.android.workout;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableInt;
import androidx.databinding.PropertyChangeRegistry;

public class MainViewModel extends ObservableViewModel{
    private static final int INITIAL_SECONDS_PER_WORK_SET = 5;
    private static final int INITIAL_SECONDS_PER_REST_SET = 2;
    private static final int INITIAL_SECONDS_OF_SET = 5;

    private int numberOfSetsTotal = INITIAL_SECONDS_OF_SET;
    private int numberOfSetsElapsed = 0;

    private boolean timerRunning;
    private TimerStates state = TimerStates.STOPPED;

    private int[] numberOfSets = {0, 0};

    private ObservableInt timePerWorkSet;
    private ObservableInt timePerRestSet;
    private ObservableInt workTimeLeft;
    private ObservableInt restTimeLeft;

    public MainViewModel() {
        timePerWorkSet = new ObservableInt(INITIAL_SECONDS_PER_WORK_SET * 10);
        timePerRestSet = new ObservableInt(INITIAL_SECONDS_PER_REST_SET * 10);
        workTimeLeft = new ObservableInt(timePerWorkSet.get());
        restTimeLeft = new ObservableInt(timePerRestSet.get());
    }

    @Bindable
    public boolean getTimerRunning() {
        return state == TimerStates.STARTED;
    }

    public void setTimerRunning(boolean timerRunning) {
        if (timerRunning) {
            startButtonClicked();
        } else {
            pauseButtonClicked();
        }
    }

    private void startButtonClicked() {
        switch (state) {
            case STOPPED:
                pausedToStart();
                break;
            case STARTED:
                break;
            case PAUSED:
                stoppedToStarted();
                break;
        }
    }

    private void pauseButtonClicked() {
        if (state == TimerStates.STARTED) {
            startedToPaused();
        }
    }

    private void stoppedToStarted() {
        state = TimerStates.STARTED;
        notifyPropertyChanged(BR.timerRunning);
    }

    private void pausedToStart() {
        state = TimerStates.STARTED;
        notifyPropertyChanged(BR.timerRunning);
    }

    private void startedToPaused() {
        state = TimerStates.PAUSED;
        notifyPropertyChanged(BR.timerRunning);
    }

    @Bindable
    public int[] getNumberOfSets() {
        this.numberOfSets[0] = numberOfSetsElapsed;
        this.numberOfSets[1] = numberOfSetsTotal;
        return this.numberOfSets;
    }

    public void setNumberOfSets(@NonNull int[] value) {
        int newTotal = value[1];
        if (newTotal == numberOfSets[1])
            return;
        if (newTotal != 0 && newTotal > numberOfSetsElapsed) {
            this.numberOfSets = value;
            numberOfSetsTotal = newTotal;
        }
        notifyPropertyChanged(BR.numberOfSets);
    }

    public ObservableInt getTimePerWorkSet() {
        return timePerWorkSet;
    }

    public void timePerWorkSetChanged(@NonNull CharSequence newValue) {
        try {
            timePerWorkSet.set(Converter.cleanSecondsString(newValue.toString()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        if (!timerRunning) {
            workTimeLeft.set(timePerWorkSet.get());
        }
    }

    public ObservableInt getTimePerRestSet() {
        return timePerRestSet;
    }

    public void timePerRestSetChanged(@NonNull CharSequence newValue) {
        try {
            timePerRestSet.set(Converter.cleanSecondsString(newValue.toString()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        if (!timerRunning) {
            restTimeLeft.set(timePerRestSet.get());
        }
    }

    public ObservableInt getWorkTimeLeft() {
        return workTimeLeft;
    }

    public ObservableInt getRestTimeLeft() {
        return restTimeLeft;
    }

    public void workTimeIncrease() {
        timePerSetIncrease(timePerWorkSet,1, 0);
    }

    public void workTimeDecrease() {
        timePerSetIncrease(timePerWorkSet,-1, 10);
    }

    public void restTimeIncrease() {
        timePerSetIncrease(timePerRestSet,1, 0);
    }

    public void restTimeDecrease() {
        timePerSetIncrease(timePerRestSet,-1, 10);
    }

    private void timePerSetIncrease(@NonNull ObservableInt timePerSet, int sign, int min) {
        if (timePerSet.get() < 10 && sign < 0)
            return;
        roundTimeIncrease(timePerSet, sign, min);
        if (state == TimerStates.STOPPED) {
            workTimeLeft.set(timePerWorkSet.get());
            restTimeLeft.set(timePerRestSet.get());
        }
    }

    private void roundTimeIncrease(@NonNull ObservableInt timePerSet, int sign, int min) {
        int currentValue = timePerSet.get();
        int newValue;
        if (currentValue < 100)
            // <10 Seconds - increase 1
            newValue = timePerSet.get() + sign * 10;
        else if (currentValue < 600)
            // >10 seconds, 5-second increase
            newValue = Double.valueOf(Math.rint(currentValue / 50.0) * 50 + (50 * sign)).intValue();
        else
            // >60 seconds, 10-second increase
            newValue = Double.valueOf(Math.rint(currentValue / 100.0) * 100 + (100 * sign)).intValue();
        timePerSet.set(Math.max(newValue, min));
    }

    public void setsIncrease() {
        numberOfSetsTotal += 1;
        notifyPropertyChanged(BR.numberOfSets);
    }

    public void setsDecrease() {
        if (numberOfSetsTotal > numberOfSetsElapsed + 1) {
            numberOfSetsTotal -= 1;
            notifyPropertyChanged(BR.numberOfSets);
        }
    }

    private enum TimerStates {
        STOPPED, STARTED, PAUSED
    }

    public void resetButtonClicked() {
        timePerWorkSet.set(INITIAL_SECONDS_PER_WORK_SET * 10);
        timePerRestSet.set(INITIAL_SECONDS_PER_REST_SET * 10);
        workTimeLeft.set(timePerWorkSet.get());
        restTimeLeft.set(timePerRestSet.get());
        numberOfSetsTotal = INITIAL_SECONDS_OF_SET;
        notifyPropertyChanged(BR.numberOfSets);
    }
}



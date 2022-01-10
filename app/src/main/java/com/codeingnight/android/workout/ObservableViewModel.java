package com.codeingnight.android.workout;

import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;

public class ObservableViewModel extends ViewModel implements Observable {

    public ObservableViewModel() {
    }

    private transient PropertyChangeRegistry callbacks;

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        synchronized(this) {
            if (callbacks == null) {
                this.callbacks = new PropertyChangeRegistry();
            }
        }
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        synchronized(this) {
            if (callbacks == null) {
                return;
            }
        }
        callbacks.remove(callback);
    }

    public void notifyPropertyChanged(int fieldId) {
        synchronized (this) {
            if (callbacks == null) {
                return;
            }
        }
        callbacks.notifyCallbacks(this, fieldId, null);
    }
}

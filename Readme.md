# Twoway DataBinding

如果使用单向绑定，可以在属性上设置一个值，并设置一个监听器来对该属性中的更改做出反应。

使用双向绑定可以为这个过程提供了一个快捷方式：
- @={} 符号负责接收属性的数据更改并同时监听用户更新。
- 通过实现 Observable 接口对属性的变化作出反应，使用 @Bindable 注解标注其 getter 方法获取属性的变化。
```xml
        <ToggleButton
            android:id="@+id/startPause"

            android:checked="@={viewModel.timerRunning}"
            android:textOff="@string/start"
            android:textOn="@string/pause" />
```

```java
public class MainViewModel extends ObservableViewModel {
    private boolean timerRunning;
    private TimerStates state = TimerStates.STOPPED;
    
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
}
```

设置自定义双向数据绑定：
- 使用 @BindingAdapter 注解 view 的 setter 方法，该方法用来设置初始值并在值更改时被调用。
- 使用 @InverseBindingAdapter 对从视图中读取值的方法进行注解。
- 为在 View 上设置监听器的方法添加 @BindingAdapter 注解。
```java
public class NumberOfSetsBindingAdapters {
    @BindingAdapter("numberOfSets")
    public static void setNumberOfSets(EditText view, String value) {
        // 将双向绑定表达式的值写回 View
        view.setText(value);
    }

    @InverseBindingAdapter(attribute = "numberOfSets")
    public static String getNumberOfSets(EditText view) {
        // 将读取到的值传参给 numberOfSets 属性
        return view.getText().toString();
    }

    @BindingAdapter("numberOfSetsAttrChanged")
    public static void setListener(EditText view, final InverseBindingListener listener) {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View focusedView, boolean hasFocus) {
                TextView textView = (TextView) focusedView;
                if (hasFocus)
                    textView.setText("");
                else {
                    // 如果 focus left, 通知 listener 需要更新
                    if (listener != null) {
                        // 调用 InverseBindingAdapter 注解的方法
                        listener.onChange();
                    }
                }
            }
        });
    }
}
```

然后就可以使用双向绑定的属性

```xml
<EditText

    numberOfSets="@={NumberOfSetsConverters.setArrayToString(context, viewModel.numberOfSets)}"
    hideKeyboardOnInputDone="@{true}"
    clearTextOnFocus="@{true}"/>
```

除了 LiveData ，实现 Observable 接口也可以将数据绑定到 UI： 

```java
public class MainViewModel extends ObservableViewModel {
    
    private int[] numberOfSets = {0, 0};
    
    private int numberOfSetsTotal = INITIAL_SECONDS_OF_SET;
    private int numberOfSetsElapsed = 0;
    
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
    
    ...

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
}
```

实现了 Observable 接口的类允许注册侦听器，这些侦听器希望得到可观察对象的属性变化的通知。

Observable 接口具有添加和删除侦听器的机制，但是您必须决定何时发送通知。

为了简化开发，数据绑定库提供了 BaseObservable 类，它实现了侦听器注册机制。

实现 BaseObservable 的数据类负责在属性更改时发出通知，这是通过为 getter 分配一个 Bindable 注解，并在 setter 中调用 notifyPropertyChanged ()方法来实现。

```java
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
        ...
        callbacks.remove(callback);
    }

    public void notifyPropertyChanged(int fieldId) {
        ...
        callbacks.notifyCallbacks(this, fieldId, null);
    }
}
```
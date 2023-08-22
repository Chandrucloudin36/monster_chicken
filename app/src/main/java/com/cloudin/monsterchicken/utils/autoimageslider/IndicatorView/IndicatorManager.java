package com.cloudin.monsterchicken.utils.autoimageslider.IndicatorView;

import androidx.annotation.Nullable;

import com.cloudin.monsterchicken.utils.autoimageslider.IndicatorView.animation.AnimationManager;
import com.cloudin.monsterchicken.utils.autoimageslider.IndicatorView.animation.controller.ValueController;
import com.cloudin.monsterchicken.utils.autoimageslider.IndicatorView.animation.data.Value;
import com.cloudin.monsterchicken.utils.autoimageslider.IndicatorView.draw.DrawManager;
import com.cloudin.monsterchicken.utils.autoimageslider.IndicatorView.draw.data.Indicator;


public class IndicatorManager implements ValueController.UpdateListener {

    private final DrawManager drawManager;
    private final AnimationManager animationManager;
    private final Listener listener;

    IndicatorManager(@Nullable Listener listener) {
        this.listener = listener;
        this.drawManager = new DrawManager();
        this.animationManager = new AnimationManager(drawManager.indicator(), this);
    }

    public AnimationManager animate() {
        return animationManager;
    }

    public Indicator indicator() {
        return drawManager.indicator();
    }

    public DrawManager drawer() {
        return drawManager;
    }

    @Override
    public void onValueUpdated(@Nullable Value value) {
        drawManager.updateValue(value);
        if (listener != null) {
            listener.onIndicatorUpdated();
        }
    }

    interface Listener {
        void onIndicatorUpdated();
    }
}

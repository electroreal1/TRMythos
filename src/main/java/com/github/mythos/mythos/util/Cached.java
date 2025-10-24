package com.github.mythos.mythos.util;

import org.jetbrains.annotations.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class Cached<T, B> {
    private T lastValue;
    private final Supplier<T> updatedValueGetter;
    private final Function<CallbackInfo<B>, CallbackInfo<B>> updateCheck;
    private CallbackInfo<B> callbackInfo = new CallbackInfo<>();

    public Cached(Supplier<T> valueGetter, Function<CallbackInfo<B>, CallbackInfo<B>> updateCheck) {
        this.updatedValueGetter = valueGetter;
        this.updateCheck = updateCheck;
        this.lastValue = null;
    }

    public T getValue() {
        this.callbackInfo = this.updateCheck.apply(this.callbackInfo);
        if (this.callbackInfo.needsUpdate) {
            this.lastValue = this.updatedValueGetter.get();
            this.callbackInfo.needsUpdate = false;
        }
        return this.lastValue;
    }

    public static class CallbackInfo<B> {
        public boolean needsUpdate = false;
        public @Nullable B lastCallbackReference = null;
    }
}

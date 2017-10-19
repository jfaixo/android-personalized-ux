package com.singular_dreams.pux.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class UserAwareLayout extends ViewGroup {

    public UserAwareLayout(Context context) {
        super(context);
    }

    public UserAwareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserAwareLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UserAwareLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Any layout manager that doesn't scroll will want this.
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}

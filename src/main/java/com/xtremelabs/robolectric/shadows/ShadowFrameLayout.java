package com.xtremelabs.robolectric.shadows;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

/**
 * Shadow for {@link FrameLayout} that simulates its implementation.
 */
@SuppressWarnings("UnusedDeclaration")
@Implements(FrameLayout.class)
public class ShadowFrameLayout extends ShadowViewGroup {
    private FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(0, 0);

    public void __constructor__(Context context, AttributeSet attributeSet, int defStyle) {
        setLayoutParams(new ViewGroup.MarginLayoutParams(0, 0));
        super.__constructor__(context, attributeSet, defStyle);
    }
    
    @Implementation
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int width = MeasureSpec.getSize(widthMeasureSpec);
    	int height = MeasureSpec.getSize(heightMeasureSpec);
    	
    	layout(right, top, right + width, top + height);
    }

    @Implementation
    @Override
    public ViewGroup.LayoutParams getLayoutParams() {
        return layoutParams;
    }

    @Override
    protected void setChildLayoutParams(View child) {
        child.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
    }
}

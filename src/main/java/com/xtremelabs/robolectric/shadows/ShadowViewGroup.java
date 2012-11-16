package com.xtremelabs.robolectric.shadows;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LayoutAnimationController;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;

/**
 * Shadow for {@code ViewGroup} that simulates its implementation
 */
@SuppressWarnings({"UnusedDeclaration"})
@Implements(ViewGroup.class)
public class ShadowViewGroup extends ShadowView {
    private List<View> children = new ArrayList<View>();
    private AnimationListener animListener;
    private LayoutAnimationController layoutAnim;
    private boolean disallowInterceptTouchEvent = false;

    @Implementation
    @Override
    public View findViewById(int id) {
        if (id == getId()) {
            return realView;
        }

        for (View child : children) {
            View found = child.findViewById(id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    @Implementation
    @Override
    public View findViewWithTag(Object obj) {
        if (obj.equals(realView.getTag())) {
            return realView;
        }

        for (View child : children) {
            View found = child.findViewWithTag(obj);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    @Implementation
    public void addView(View child) {
        ((ViewGroup) realView).addView(child, -1);
    }

    @Implementation
    public void addView(View child, int index) {
        if (index == -1) {
            children.add(child);
        } else {
            children.add(index, child);
        }
        shadowOf(child).parent = this;
        addedChild(child);
    }

    @Implementation
    public void addView(View child, int width, int height) {
        ((ViewGroup) realView).addView(child, -1);
    }

    @Implementation
    public void addView(View child, ViewGroup.LayoutParams params) {
        ((ViewGroup) realView).addView(child, -1, params);
    }

    @Implementation
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        child.setLayoutParams(params);
        ((ViewGroup) realView).addView(child, index);
    }

    @Implementation
    public void removeView(View child) {
        // Android's ViewGroup ignores the child when it is null. Do the same here.
        if (child == null) return;
        shadowOf(child).parent = null;
        children.remove(child);
        removedChild(child);
    }

    @Implementation
    public int indexOfChild(View child) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            if (children.get(i) == child) {
                return i;
            }
        }
        return -1;
    }

    @Implementation
    public int getChildCount() {
        return children.size();
    }

    @Implementation
    public View getChildAt(int index) {
        return isValidIndex(index) ? children.get(index) : null;
    }

    @Implementation
    public void removeAllViews() {
        for (View child : children) {
            shadowOf(child).parent = null;
            removedChild(child);
        }
        children.clear();
    }

    @Implementation
    public void removeViewAt(int position) {
        View child = children.remove(position);
        shadowOf(child).parent = null;
        removedChild(child);
    }

    @Override
    @Implementation
    public boolean hasFocus() {
        if (super.hasFocus()) return true;

        for (View child : children) {
            if (child.hasFocus()) return true;
        }

        return false;
    }

    @Implementation
    @Override
    public void clearFocus() {
        if (hasFocus()) {
            super.clearFocus();

            for (View child : children) {
                child.clearFocus();
            }
        }
    }

    /**
     * Returns a string representation of this {@code ViewGroup} by concatenating all of the strings contained in all
     * of the descendants of this {@code ViewGroup}.
     * <p/>
     * Robolectric extension.
     */
    @Override
    public String innerText() {
        String innerText = "";
        String delimiter = "";

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            String childText = shadowOf(child).innerText();
            if (childText.length() > 0) {
                innerText += delimiter;
                delimiter = " ";
            }
            innerText += childText;
        }
        return innerText;
    }

    /**
     * Non-Android method that dumps the state of this {@code ViewGroup} to {@code System.out}
     */
    @Override
    public void dump(PrintStream out, int indent) {
        dumpFirstPart(out, indent);
        if (children.size() > 0) {
            out.println(">");

            for (View child : children) {
                shadowOf(child).dump(out, indent + 2);
            }

            dumpIndent(out, indent);
            out.println("</" + realView.getClass().getSimpleName() + ">");
        } else {
            out.println("/>");
        }
    }

    @Implementation
    public void setLayoutAnimationListener(AnimationListener listener) {
        animListener = listener;
    }

    @Implementation
    public AnimationListener getLayoutAnimationListener() {
        return animListener;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int i = 0; i < getChildCount(); i++) {
            shadowOf(getChildAt(i)).callOnAttachedToWindow();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        for (int i = 0; i < getChildCount(); i++) {
            shadowOf(getChildAt(i)).callOnDetachedFromWindow();
        }
        super.onDetachedFromWindow();
    }

    @Implementation
    public void setLayoutAnimation(LayoutAnimationController layoutAnim) {
        this.layoutAnim = layoutAnim;
    }

    @Implementation
    public LayoutAnimationController getLayoutAnimation() {
        return layoutAnim;
    }

    @Implementation
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        disallowInterceptTouchEvent = disallowIntercept;
    }

    public boolean getDisallowInterceptTouchEvent() {
        return disallowInterceptTouchEvent;
    }

    protected void addedChild(View child) {
        if (isAttachedToWindow()) shadowOf(child).callOnAttachedToWindow();
        setChildLayoutParams(child);
    }

    protected void removedChild(View child) {
        if (isAttachedToWindow()) shadowOf(child).callOnDetachedFromWindow();
    }

    protected void setChildLayoutParams(View child) {
        shadowOf(child).setLayoutParams(new ViewGroup.LayoutParams(0, 0));
    }

    private boolean isValidIndex(int i) {
        return i >= 0 && i < children.size();
    }
}

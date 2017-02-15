package com.example.raphaelattali.rythmrun.Android_activities;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

class ViewAnimationUtils {

    private static float scale;
    static final int expandedHeight=340;
    private static final int collapsedHeight=52;

    static Animation expand(final View v) {
        //Saving scale to convert dp to px
        scale = v.getContext().getResources().getDisplayMetrics().density;

        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        v.getLayoutParams().height = dpToPx(collapsedHeight);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = dpToPx(collapsedHeight)
                        + (int)(dpToPx(expandedHeight-collapsedHeight) * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(dpToPx(expandedHeight) / v.getContext().getResources().getDisplayMetrics().density));
        return a;
    }

    static Animation collapse(final View v) {
        //Saving scale to convert dp to px
        scale = v.getContext().getResources().getDisplayMetrics().density;

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = dpToPx(expandedHeight)
                        - (int)(dpToPx(expandedHeight-collapsedHeight) * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(dpToPx(expandedHeight) / v.getContext().getResources().getDisplayMetrics().density));
        return a;
    }

    static int dpToPx(int dp){
        return (int) (dp * scale + 0.5f);
    }
}
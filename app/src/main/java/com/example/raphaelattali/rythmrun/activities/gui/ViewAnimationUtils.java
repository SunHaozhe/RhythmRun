package com.example.raphaelattali.rythmrun.activities.gui;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ViewAnimationUtils {

    private static float scale;
    public static final int expandedHeight=340;
    public static final int collapsedHeight=52;

    public static void expand(final View v) {
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
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        scale = v.getContext().getResources().getDisplayMetrics().density;
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    //v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = dpToPx(expandedHeight)
                            - (int)(dpToPx(expandedHeight-collapsedHeight) * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(dpToPx(expandedHeight) / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static int dpToPx(int dp){
        return (int) (dp * scale + 0.5f);
    }
}
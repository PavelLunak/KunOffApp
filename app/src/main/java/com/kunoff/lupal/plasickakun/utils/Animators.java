package com.kunoff.lupal.plasickakun.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;


public class Animators {

    public static void animateButtonClick(View view) {
        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.3f, 1f);
        objectAnimatorAlpha.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.98f, 1.02f, 1f);
        objectAnimatorScaleX.setInterpolator(new LinearInterpolator());
        objectAnimatorScaleX.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator objectAnimatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.98f, 1.02f, 1f);
        objectAnimatorScaleY.setInterpolator(new LinearInterpolator());
        objectAnimatorScaleY.setStartDelay(50);
        objectAnimatorScaleY.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(objectAnimatorAlpha, objectAnimatorScaleX, objectAnimatorScaleY);
        animatorSet.start();
    }

    public static void animateRotate(View view, int startDelay) {
        ObjectAnimator objectAnimatorRotate = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 90f);
        objectAnimatorRotate.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.3f, 1f);
        objectAnimatorAlpha.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.8f, 1.2f, 1f);
        objectAnimatorScaleX.setInterpolator(new LinearInterpolator());
        objectAnimatorScaleX.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator objectAnimatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.8f, 1.2f, 1f);
        objectAnimatorScaleY.setInterpolator(new LinearInterpolator());
        objectAnimatorScaleY.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.setStartDelay(startDelay);
        animatorSet.playTogether(objectAnimatorRotate, objectAnimatorAlpha, objectAnimatorScaleX, objectAnimatorScaleY);
        animatorSet.start();
    }

    public static void AnimateLogoStop(View view) {
        ObjectAnimator objectAnimatorRotate = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 360f);
        objectAnimatorRotate.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 1f);
        objectAnimatorAlpha.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 10f, 1f);
        objectAnimatorScaleX.setInterpolator(new AnticipateInterpolator());
        objectAnimatorScaleX.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator objectAnimatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 10f, 1f);
        objectAnimatorScaleY.setInterpolator(new AnticipateInterpolator());
        objectAnimatorScaleY.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.setStartDelay(600);
        animatorSet.playTogether(objectAnimatorAlpha, objectAnimatorScaleX, objectAnimatorScaleY);
        animatorSet.start();
    }

    public static void AnimateLogoMarten(View view) {

        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 1f);
        objectAnimatorAlpha.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0f, 1f);
        objectAnimatorScaleX.setInterpolator(new LinearInterpolator());
        objectAnimatorScaleX.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator objectAnimatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0f, 1f);
        objectAnimatorScaleY.setInterpolator(new LinearInterpolator());
        objectAnimatorScaleY.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.setStartDelay(500);
        animatorSet.playTogether(objectAnimatorAlpha, objectAnimatorScaleX, objectAnimatorScaleY);
        animatorSet.start();
    }

    public static void animateVibrate(View view) {
        ObjectAnimator objectAnimatorTrasX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 5.0f, -5.0f, 4f, -4f, 3f, -3f, 1f);
        objectAnimatorTrasX.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorTrasY = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 5.0f, -5.0f, 4f, -4f, 3f, -3f, 1f);
        objectAnimatorTrasY.setInterpolator(new LinearInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.setStartDelay(900);
        animatorSet.playTogether(objectAnimatorTrasX, objectAnimatorTrasY);
        animatorSet.start();
    }

    public static void animateHide(final View view) {
        ObjectAnimator objectAnimatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0f);
        objectAnimatorScaleX.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0f);
        objectAnimatorScaleY.setInterpolator(new LinearInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(objectAnimatorScaleX, objectAnimatorScaleY);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
        animatorSet.start();
    }

    public static void animateShow(final View view) {
        ObjectAnimator objectAnimatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0f, 1f);
        objectAnimatorScaleX.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0f, 1f);
        objectAnimatorScaleY.setInterpolator(new LinearInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(objectAnimatorScaleX, objectAnimatorScaleY);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }
}

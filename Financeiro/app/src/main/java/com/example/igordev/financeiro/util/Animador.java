package com.example.igordev.financeiro.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by igordev on 27/06/17.
 */

public class Animador {
    public static void animaView(View v, long duracao) {
        Animator animator = ObjectAnimator.ofFloat(v, "rotation", 0f, 360f);
        animator.setDuration(duracao);
        animator.start();
    }
}

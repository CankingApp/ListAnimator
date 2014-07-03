package net.canking.myanimtest;

import java.util.List;

import net.canking.myanimtest.MainActivity.MyAnimListAdapter;
import net.canking.myanimtest.MainActivity.ViewHolder;
import android.view.View;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

public class AnimationBuilde {
    public static final int ANIMATION_DURATION = 200;

    public static AnimatorSet buildListRemoveAnimator(final View view, final List list,final MyAnimListAdapter adapter, final int index) {
        AnimatorListener al = new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                list.remove(index);
                ViewHolder vh = (ViewHolder) view.getTag();
                vh.needInflate = true;

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        };

        AnimatorSet animatorSet = new AnimatorSet();
        Animator anim = ObjectAnimator.ofFloat(view, "rotationX", 0, 90);
        Animator animb = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        final int height = view.getMeasuredHeight();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // TODO Auto-generated method stub
                if (animation.getAnimatedFraction() >= 1) {
                    view.setVisibility(View.GONE);
                }
                else {
                    view.getLayoutParams().height = height
                            - (int) (height * animation.getAnimatedFraction());
                    view.requestLayout();
                }
            }
        });

        anim.setDuration(ANIMATION_DURATION);
        animb.setDuration(ANIMATION_DURATION);
        valueAnimator.setDuration(ANIMATION_DURATION + ANIMATION_DURATION+100);
        animatorSet.playTogether(anim, animb, valueAnimator);
        animatorSet.addListener(al);
        return animatorSet;
    }
}

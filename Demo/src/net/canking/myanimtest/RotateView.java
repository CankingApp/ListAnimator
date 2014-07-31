package net.canking.myanimtest;

import com.nineoldandroids.animation.ValueAnimator;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

public class RotateView extends View {

    private Drawable mDrawable;
    private float mSpeed;
    private boolean mRotating;
    private long mStartTime;
    private Context mContext;

    public RotateView(Context context) {
        super(context);
        init(context);
    }

    public RotateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context) {
        mContext = context;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.circleprogress_layer);
        mDrawable = bitmap2Drawable(mContext, bitmap);
        setRotateView(1f, true);
        setAccelerateSpeed();
    }

    /**
     * Create a BitmapDrawable object from the specified Bitmap object.
     */
    public static BitmapDrawable bitmap2Drawable(Context cxt, Bitmap bmp) {
        if (bmp == null) {
            return null;
        }
        return new BitmapDrawable(cxt.getResources(), bmp);
    }

    /**
     * @param speed (degrees/ms)
     */
    public void setRotateView(float speed, boolean clockwise) {
        this.mSpeed = speed;
        this.mRotating = true;
        mStartTime = System.currentTimeMillis();

    }

    public void startRotate() {
        this.mRotating = true;
        mStartTime = System.currentTimeMillis();
        setAccelerateSpeed();
        setVisibility(View.VISIBLE);
        postInvalidate();
    }

    public void stopRotate() {
        this.mRotating = false;
        setVisibility(View.GONE);
        postInvalidate();
    }


    public void setAccelerateSpeed() {
        AccelerateInterpolator interpolator = new AccelerateInterpolator();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 1000);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                // TODO Auto-generated method stub
                mSpeed = (Float) arg0.getAnimatedValue();
            }
        });
        valueAnimator.start();
    }

    public boolean isRotating() {
        return mRotating;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.mRotating) {
            canvas.save();
            if (mSpeed >= 1000) {
                mSpeed = System.currentTimeMillis() - this.mStartTime;
            }
            float angle = mSpeed;
            angle = 360.0f - angle;
            canvas.rotate(angle, this.getWidth() / 2, this.getHeight() / 2);
            this.mDrawable.setBounds(0, 0, this.getWidth(),
                    this.getHeight());
            this.mDrawable.draw(canvas);
            canvas.restore();
        }

        invalidate();

        super.onDraw(canvas);
    }

}

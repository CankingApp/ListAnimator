package net.canking.myanimtest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.w3c.dom.Text;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class CircleProgressBarView extends View {

    private float mTextSize;
    private int mTextColor;
    private String mTextString;
    public static final int Default_TEXT_SIZE_SP = 14;

    private int mStartAngle;
    private int mSweepAngle;
    private final Context mContext;
    private Paint mPaint;

    private boolean mIsProgressVisiable = true;
    private boolean mIsScanComplate;
    private int mPercent;
    private static int mScanState;
    private Bitmap mReDrawBitmap;
    private int mNotificationScanPercent;
    public static final String FONT_PATH = "fonts/addetector.ttf";
    private Typeface mTF = null;
    private RectF mDrawableRect;

    public CircleProgressBarView(Context context) {
        super(context);
        mContext = context;
        initParams();
    }

    public CircleProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
        initParams();
    }

    private void init(AttributeSet attrs) {
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.Circle_Progress_Bar);
        mTextColor = t.getColor(R.styleable.Circle_Progress_Bar_textColor, Color.BLACK);
        mTextSize = t.getDimension(R.styleable.Circle_Progress_Bar_textSize, getTextDefault());
        mTextString = t.getString(R.styleable.Circle_Progress_Bar_text);
    }

    public static final int DEF_ALPHA = 168;
    public static final int DEF_START_ANGLE = -90;

    private void initParams() {
        initBitmapFromRes();
        setPercent(0);
        mTF = Typeface.createFromAsset(mContext.getAssets(), FONT_PATH);

        mPercent = 0;
        mNotificationScanPercent = 0;
        mIsScanComplate = false;
        mStartAngle = DEF_START_ANGLE;
        mSweepAngle = DEF_START_ANGLE;
        mIsScanComplate = false;
        mPaint = new Paint();
        mPaint.reset();
    }

    public void startProgress(){
        prepareScan();
        this.mIsProgressVisiable = true;
        
    }
    
    public void stopProgress(){
        this.mIsProgressVisiable = false;
        
        recycleBitmap();
    }
    
    public boolean isInProgress(){
        return this.mIsProgressVisiable;
    }
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context context) {

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();

        return display.getWidth();
    }

    public void initBitmapFromRes() {

        mReDrawBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.circleprogress_redraw_layer);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // View.LAYER_TYPE_SOFTWARE
        setLayerType(this, 1, null);
    }

    private static Method sSetLayerType;

    static {
        try {
            Class<?>[] arrayOfClass = new Class[] {
                    int.class, Paint.class
            };
            sSetLayerType = View.class.getMethod("setLayerType", arrayOfClass);
        } catch (NoSuchMethodException localNoSuchMethodException) {
            sSetLayerType = null;
        }
    }

    public static void setLayerType(View view, int layerType, Paint paint) {
        if (sSetLayerType != null) {
            try {
                Method localMethod = sSetLayerType;
                Object[] arrayOfObject = new Object[] {
                        layerType, paint
                };
                localMethod.invoke(view, arrayOfObject);
                return;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        // if anything wrong, will be here
    }

    public float getPercent() {
        return mPercent;
    }

    public void setStartAngle(int startAngle) {
        mStartAngle = startAngle;
        postInvalidate();
    }

    public void setAppScanPercent(int percent) {
        setPercent((percent * (100 - mNotificationScanPercent) / 100) + mNotificationScanPercent);
    }

    public static final float CARDINAL_NUMBER = 3.6f;

    private void setPercent(int percent) {
        mPercent = percent;
        if (mPercent > 100) {
            mPercent = 100;
        }
        this.mSweepAngle = (int) (percent * CARDINAL_NUMBER)
                + DEF_START_ANGLE;

        postInvalidate();
    }

    public boolean isScanComplete() {
        return mIsScanComplate;
    }

    public void setScanState(int state) {
        mScanState = state;

        postInvalidate();
    }

    public void setProgressVisiable(boolean visiable) {
        mIsProgressVisiable = visiable;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mReDrawBitmap == null) return;
        Log.e("", "mStartAngle+;" + mStartAngle + "+" + mSweepAngle);
        
        if (mIsProgressVisiable) {
            if (mDrawableRect == null) {
                mDrawableRect = new RectF(0, 0, this.getWidth(), this.getHeight());
            }
            canvas.save();
            int drawWidth = mReDrawBitmap.getWidth();
            int drawHeight = mReDrawBitmap.getHeight();
            Log.e("", "width+height:" + drawWidth + "+" + drawHeight);
            getSectorClip(canvas, mDrawableRect, mStartAngle, mSweepAngle);
            Rect src = new Rect();
            src.set(0, 0, mReDrawBitmap.getWidth(), mReDrawBitmap.getHeight());
            Rect offRect = new Rect();
            offRect.set(0, 0, this.getWidth(), this.getHeight());
            canvas.drawBitmap(mReDrawBitmap, src, offRect, mPaint);
            canvas.restore();

            canvas.save();
            drawScanState(canvas, mPercent + "%");
            canvas.restore();
            invalidate();
        }
    }

    private void getSectorClip(Canvas canvas, RectF rect, float startAngle, float sweepAngle)
    {
        Path p = new Path();
        float center_X = rect.width() / 2.0F;
        float center_Y = rect.height() / 2.0F;
        float r = rect.width() / 2.0F;
        p.reset();
        p.moveTo(center_X, center_Y);
        p.lineTo((float) (center_X + r * Math.cos(Math.PI * startAngle / 180.0D)),
                (float) (center_Y + r * Math.sin(Math.PI * startAngle / 180.0D)));
        p.lineTo((float) (center_X + r * Math.cos(Math.PI * sweepAngle / 180.0D)),
                (float) (center_Y + r * Math.sin(Math.PI * sweepAngle / 180.0D)));
        p.close();
        p.addArc(rect, startAngle, sweepAngle - startAngle);
        canvas.clipPath(p);
    }

    private void drawScanState(Canvas canvas, String text) {
        mPaint.reset();
        mPaint.setAntiAlias(true);

        if (text != null) {
            drawPercentText(canvas, text);
        }
    }

    public void setTextSize(float size) {
        mTextSize = size;
    }

    public static DisplayMetrics getDM(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    private void drawPercentText(Canvas canvas, String text) {
        mPaint.setColor(getResources().getColor(R.color.scan_prcent_color));
        mPaint.setShadowLayer(1, 0, 1, Color.BLUE);
        mPaint.setTypeface(mTF);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setTextSize((float) mTextSize);
        // 需给paint设置TextAlign，否则为字体左边框
        canvas.drawText(text, getCenterX(), getTextBaseLine(mPaint, getCenterY()),
                mPaint);
    }

    public static float getTextBaseLine(Paint paint, int centerY) {
        FontMetrics fontMetrics = paint.getFontMetrics();
        // 计算文字高度
        float fontHeight = fontMetrics.bottom - fontMetrics.top;

        // 计算文字baseline
        float textBaseY = centerY + fontHeight / 2;
        return textBaseY;
    }

    public int getCenterX() {
        return getWidth() / 2;
    }

    public int getCenterY() {
        return getHeight() / 2;
    }

    private float getTextDefault() {
        return sp2px(Default_TEXT_SIZE_SP
                , getDM((Activity) mContext).scaledDensity);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(float spValue, float fontScale) {
        return (int) (spValue * fontScale + 0.5f);
    }

    public void prepareScan() {
        initParams();
    }

    public void recycleBitmap() {
        if (mReDrawBitmap != null && mReDrawBitmap.isRecycled()) {
            mReDrawBitmap.recycle();
        }
    }

}

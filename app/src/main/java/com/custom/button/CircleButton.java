package com.custom.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created UseIT for  ExampleCircleButton
 * User: maxrovkin
 * Date: 17.11.13
 * Time: 21:22
 */
public class CircleButton extends View
{
    /**
     * Состояние начальное, когда отображается текст
     */
    public static final int DEFAULT_STATE = 0;

    /**
     * Второе состояние, когда отображается значек паузы
     */
    public static final int PLAY_STATE = 1;

    private static final int DEFOULT_FIRST_COLOR = 0xFF9ACEEE;
    private static final int DEFOULT_SECOND_COLOR = 0xFFEE9A8C;


    private final static int PROGRESS_WIDTH = 7;
    private Paint circlePaint;
    private Paint circleProgressPaint;
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private RectF circleArc;
    private RectF progressArc;

    // Attrs
    private int circleRadius;
    private int circleFillColor;

    private static final int circleStartAngle = 0;
    private static final int circleEndAngle = 360;

    private int progressAngleStart = 0;
    private int progressAngleDelta = 0;
    private boolean mShowProgress;
    private boolean progressBack = false;

    //private float[] progressColorPosition = new float[]{0,(float) (progressAngleDelta) / 360f,(float) (progressAngleDelta) / 360f}


    private String text = "1";

    private int currentState = DEFAULT_STATE;

    public void init(AttributeSet attrs)
    {
        // получаем кастомные атрибуты
        circleRadius = getResources().getDimensionPixelOffset(R.dimen.circle_default_radius);
        if (attrs != null)
        {
            final TypedArray attrsArray = getContext().obtainStyledAttributes(attrs, R.styleable.circlebutton);

            circleRadius = attrsArray.getDimensionPixelOffset(R.styleable.circlebutton_circle_radius, circleRadius);
            if (attrsArray.getString(R.styleable.circlebutton_circle_text) != null)
                text = attrsArray.getString(R.styleable.circlebutton_circle_text);
            attrsArray.recycle();
        }
    }

    private final static int delayMillis = 10;
    private final Handler spinHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            invalidate();
            if (mShowProgress)
            {

                if (progressAngleStart < 360 && !progressBack)
                {
                    progressAngleStart += 2;
                    spinHandler.sendEmptyMessageDelayed(0, delayMillis);
                }
                else
                {
                    progressBack = true;
                    progressAngleStart = -360;
                    progressAngleDelta = 0;
                    spinHandler2.sendEmptyMessageDelayed(0, delayMillis);
                }


            }

        }
    };

    private final Handler spinHandler2 = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            invalidate();
            if (mShowProgress)
            {

                if (progressAngleStart < 0 && progressBack)
                {
                    progressAngleStart += 2;
                    spinHandler2.sendEmptyMessageDelayed(0, delayMillis);
                }
                else
                {
                    progressBack = false;
                    progressAngleStart = 0;
                    progressAngleDelta = 0;
                    spinHandler.sendEmptyMessageDelayed(0, delayMillis);
                }


            }

        }
    };


    public CircleButton(final Context context)
    {
        this(context, null);
    }

    public CircleButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs); // читаем все атрибуты

        float cX = getWidth() / 2F, cY = getHeight() / 2F;

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(DEFOULT_FIRST_COLOR);

        circleProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleProgressPaint.setStyle(Paint.Style.STROKE);
        circleProgressPaint.setStrokeWidth(PROGRESS_WIDTH);
        circleProgressPaint.setColor(0xFFFF5555);
        //circleProgressPaint.setShader(new SweepGradient(cX, cY, new int[]{0xFFFF5555, DEFOULT_SECOND_COLOR, DEFOULT_SECOND_COLOR}, new float[]{0,(float) (progressAngleDelta) / 360f,(float) (progressAngleDelta) / 360f}));
        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (currentState == DEFAULT_STATE && !mShowProgress)
                {
                    if (stateListener != null)
                        stateListener.onShowProgress();
                    showProgress();
                }
                else
                {
                    if (currentState == PLAY_STATE)
                    {
                        currentState = DEFAULT_STATE;
                        invalidate();
                        if (stateListener != null)
                            stateListener.onReturnDefState();
                    }
                }
            }
        });

    }

    public CircleButton(final Context context, final AttributeSet attrs, final int defStyle)
    {
        this(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {


        if (currentState == DEFAULT_STATE || mShowProgress)
        {
            circlePaint.setColor(DEFOULT_FIRST_COLOR);
            drawBackground(canvas);
            drawText(canvas);

        }

        if (mShowProgress)
        {
            circlePaint.setColor(DEFOULT_SECOND_COLOR);
            drawBackground(canvas);

            canvas.drawArc(progressArc, progressAngleDelta, progressAngleStart, false, circleProgressPaint);
            drawText(canvas);

        }
        else
        {
            if (currentState == PLAY_STATE)
            {
                circlePaint.setColor(DEFOULT_SECOND_COLOR);
                drawBackground(canvas);
                drawPause(canvas);
            }
        }


    }


    private void drawBackground(Canvas canvas)
    {
        canvas.drawArc(circleArc, circleStartAngle, circleEndAngle, true, circlePaint);
    }

    private void drawPause(Canvas canvas)
    {
        final Paint paint = new Paint();
        paint.setStrokeWidth(0);
        paint.setColor(Color.WHITE);

        int deltaCenter = (int) (circleArc.right / 9);
        int width = (int) (circleArc.right / 15);
        int leftFirst = (int) (circleArc.right / 2) - deltaCenter;
        int leftSecond = (int) (circleArc.right / 2) + deltaCenter;
        int top = (int) (circleArc.bottom / 3.1f);
        int bottom = (int) circleArc.bottom - top;

        canvas.drawRect(leftFirst, top, leftFirst + width, bottom, paint);
        canvas.drawRect(leftSecond - width, top, leftSecond, bottom, paint);

    }


    private void drawText(Canvas canvas)
    {

        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(30);
        adjustTextSize();

        int xPos = (int) (circleArc.right / 2);
        int yPos = (int) ((circleArc.bottom / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.

        canvas.drawText(text, xPos, yPos, mTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {

        int measuredWidth = measureWidth(widthMeasureSpec);
        if (circleRadius == 0)
        {
            circleRadius = measuredWidth / 2;
            int tempRadiusHeight = measureHeight(heightMeasureSpec) / 2;
            if (tempRadiusHeight < circleRadius)
                circleRadius = tempRadiusHeight;
        }
        int circleDiameter = circleRadius * 2 - 2;

        circleArc = new RectF(0, 0, circleDiameter, circleDiameter);
        final int halfWidth = PROGRESS_WIDTH / 2;
        progressArc = new RectF(halfWidth, halfWidth, circleDiameter - halfWidth, circleDiameter - halfWidth);

        int measuredHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private int measureHeight(int measureSpec)
    {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;

        if (specMode == MeasureSpec.AT_MOST)
            result = circleRadius * 2;

        if (specMode == MeasureSpec.EXACTLY)
            result = specSize;
        return result;
    }

    private int measureWidth(int measureSpec)
    {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;

        if (specMode == MeasureSpec.AT_MOST)
            result = circleRadius * 2;

        if (specMode == MeasureSpec.EXACTLY)
            result = specSize;

        return result;
    }

    /**
     * Показывает прогресс
     */
    private void showProgress()
    {
        progressAngleStart = 0;
        progressAngleDelta = 0;
        mShowProgress = true;
        spinHandler2.sendEmptyMessage(0);
    }

    /**
     * Скрывает прогресс
     */
    public void hideProgress()
    {
        mShowProgress = false;
        currentState = PLAY_STATE;
        invalidate();
    }

    private void adjustTextSize()
    {
        mTextPaint.setTextSize(100);
        mTextPaint.setTextScaleX(1.0f);
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), bounds);

        int h = bounds.bottom - bounds.top;

        float target = circleArc.bottom * .35f;//35% от диаметра круга

        float size = ((target / h) * 100f);

        mTextPaint.setTextSize(size);
    }


    public interface StateListener
    {
        /**
         * Вызывается, когда происходит нажатие в начальном состоянии
         */
        public void onShowProgress();

        /**
         * Вызывается, когда возвращается в начальное состояние, т.е. нажатие происходит в режиме роигрывания
         */
        public void onReturnDefState();
    }

    private StateListener stateListener;

    /**
     * Устанавливает слушателя на состояние кнопки
     *
     * @param stateListener
     */
    public void setStateListener(final StateListener stateListener)
    {
        this.stateListener = stateListener;
    }


    public void setCircleRadius(int radius)
    {
        this.circleRadius = radius;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public void setState(int state)
    {
        currentState = state;
    }

}

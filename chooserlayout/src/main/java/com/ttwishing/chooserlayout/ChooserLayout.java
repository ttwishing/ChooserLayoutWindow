package com.ttwishing.chooserlayout;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class ChooserLayout extends FrameLayout {

    private float mBackgroundAlpha; //背影透明度
    private ColorDrawable mColorDrawable = new ColorDrawable(Color.BLACK);

    private ChooserLayoutListener mListener;
    private int mChooserItemSize;
    private View mChooserPlusButton;

    private List<ChooserItemView> mChooserItemViewList = new ArrayList<>();

    //是否已展开
    private boolean mOpened;
    //是否有item被选中
    private boolean mChoosed;
    private int mChoosedIndex;

    private int mAllLayoutPadding = -1;
    private int mAllButtonMargin = 0;
    private int mHeight = 0;
    private boolean mHandleTouchEvent = false;

    private final OvershootInterpolator mOvershootInterpolator = new OvershootInterpolator(2.0F); //  到点折回
    private final AccelerateDecelerateInterpolator mAccelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator(); // 加速后减速前行
    private final AnticipateInterpolator mAnticipateInterpolator = new AnticipateInterpolator();// 后退后前行
    private final AccelerateInterpolator mAccelerateInterpolator = new AccelerateInterpolator();// 加速前行

    private float minAngle;
    private float maxAngle;
    private float maxRadius;
    private int undershoot = 4;
    private int angleIndexOffset;
    private int chooserItemOffsetY;
    private boolean isCircular;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v instanceof ChooserItemView) {
                choose((ChooserItemView) v);
            } else {
                openOrClose();
            }
        }
    };

    public ChooserLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ChooserLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ChooserLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = null;
            try {
                a = context.obtainStyledAttributes(attrs, R.styleable.ChooserLayout);

                this.minAngle = getAngleValue(a.getFloat(R.styleable.ChooserLayout_minAngle, 0.0F));
                this.maxAngle = getAngleValue(a.getFloat(R.styleable.ChooserLayout_maxAngle, 180.0F));
                this.maxRadius = a.getDimension(R.styleable.ChooserLayout_maxRadius, 100.0F);
                this.undershoot = a.getDimensionPixelSize(R.styleable.ChooserLayout_undershoot, 0);
                this.isCircular = a.getBoolean(R.styleable.ChooserLayout_isCircular, true);
                this.angleIndexOffset = a.getInteger(R.styleable.ChooserLayout_angleIndexOffset, 0);
                this.chooserItemOffsetY = a.getDimensionPixelSize(R.styleable.ChooserLayout_chooserItemOffsetY, 0);
            } finally {
                if (a != null) {
                    a.recycle();
                }
            }
        }
    }

    public void setChooserItems(List<ChooserItemView.ChooserItem> list) {
        this.mChooserItemSize = (int) getResources().getDimension(R.dimen.chooser_item_size);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(mChooserItemSize, mChooserItemSize);
        for (int i = 0; i < list.size(); i++) {
            ChooserItemView.ChooserItem chooserItem = list.get(i);
            ChooserItemView chooserItemView = new ChooserItemView(getContext());
            chooserItemView.setChooserItem(chooserItem);
            chooserItemView.setOnClickListener(mOnClickListener);
            addView(chooserItemView, i, lp);
            mChooserItemViewList.add(chooserItemView);
        }
    }

    public void setListener(ChooserLayoutListener chooserLayoutListener) {
        this.mListener = chooserLayoutListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mChooserPlusButton = findViewById(R.id.chooser_plus_button);
        mChooserPlusButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.mHandleTouchEvent || (!mOpened && (ev.getX() < mChooserPlusButton.getLeft()
                || ev.getX() > mChooserPlusButton.getRight()
                || ev.getY() < mChooserPlusButton.getTop()
                || ev.getY() > mChooserPlusButton.getBottom()))) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                this.mHandleTouchEvent = true;
            } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
                this.mHandleTouchEvent = false;
            }
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOpened) {
            //空白处点击事件
            openOrClose();
            return true;
        }
        return true;
    }

    public boolean ismOpened(){
        return this.mOpened;
    }

    /**
     * 设置背景的渐隐/渐显动画
     *
     * @param alpha
     */
    public void setDimAmount(float alpha) {
        mBackgroundAlpha = alpha;
        mColorDrawable.setAlpha((int) (127.0F * mBackgroundAlpha));
        if (Build.VERSION.SDK_INT >= 16) {
            setBackground(mColorDrawable);
        } else {
            setBackgroundDrawable(mColorDrawable);
        }
    }

    private void layoutChooserItems() {
        for (int i = 0; i < mChooserItemViewList.size(); i++) {
            float f = getAngle(i);
            layoutChooserItem(mChooserItemViewList.get(i), f);
        }
    }

    /**
     * 根据角度布局
     * @param chooserItemView
     * @param angle
     */
    private void layoutChooserItem(ChooserItemView chooserItemView, float angle) {
        int xOffset = getOffsetX(angle);
        int yOffset = getOffsetY(angle);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) chooserItemView.getLayoutParams();
        lp.leftMargin = xOffset + (this.mChooserPlusButton.getLeft() + this.mChooserPlusButton.getWidth() / 2 - this.mChooserItemSize / 2);
        lp.topMargin = yOffset + (this.mChooserPlusButton.getTop() + this.mChooserPlusButton.getHeight() / 2 - this.mChooserItemSize / 2);
        chooserItemView.setLayoutParams(lp);

        float translationX;
        if (mOpened) {
            translationX = 0;
        } else {
            translationX = -xOffset;
        }

        chooserItemView.setTranslationX(translationX);

        float translationY;
        if (mOpened) {
            translationY = 0;
        } else {
            translationY = -yOffset;
        }
        chooserItemView.setTranslationY(translationY);
    }

    /**
     * 重置各item的属性
     */
    private void resetChooserItem() {
        for (int i = 0; i < mChooserItemViewList.size(); i++) {
            ChooserItemView chooserItemView = mChooserItemViewList.get(i);
            float angle = getAngle(i);
            chooserItemView.setVisibility(View.VISIBLE);
            chooserItemView.setTranslationX(-getOffsetX(angle));
            chooserItemView.setTranslationY(-getOffsetY(angle));
            chooserItemView.setScaleX(1.0F);
            chooserItemView.setScaleY(1.0F);
            chooserItemView.setAlpha(1.0F);
            chooserItemView.getIconView().setRotation(0.0F);
            chooserItemView.getIconView().setScaleX(1.0F);
            chooserItemView.getIconView().setScaleY(1.0F);
        }
    }

    /**
     * 区别是打开还是关闭
     */
    private void openOrClose() {
        if (mChoosed) {
            return;
        }
        if (this.mListener != null)
            this.mListener.onPlusButtonClick();

        if (this.mOpened) {
            close();
        } else {
            open();
        }
    }

    /**
     * 打开
     */
    public void open() {
        resetChooserItem();
        this.mOpened = true;

        this.mChooserPlusButton.setRotation(0.0F);
        this.mChooserPlusButton.animate().rotation(-135.0F).setDuration(200L).setListener(null);

        ObjectAnimator.ofFloat(this, "dimAmount", new float[]{0.0F, 1.0F}).setDuration(200L).start();
        int i = 0;
        while (i < mChooserItemViewList.size()) {
            ChooserItemView chooserItemView = mChooserItemViewList.get(i);
            ViewPropertyAnimator animator = chooserItemView.animate()
                    .translationX(0.0F)
                    .translationY(0.0F)
                    .setInterpolator(mOvershootInterpolator)
                    .setDuration(350L);
            AnimatorListenerAdapter animatorListener;
            if (i == 0) {
                animatorListener = this.mOpenAnimatorListener;
            } else {
                animatorListener = null;
            }

            animator.setListener(animatorListener);

            View iconView = chooserItemView.getIconView();
            iconView.setRotation(360.0F);
            iconView.animate().rotation(0.0F).setInterpolator(this.mAccelerateDecelerateInterpolator).setDuration(350L);
            animator.setListener(animatorListener);
            i++;
        }
    }

    /**
     * 关闭
     */
    public void close() {
        this.mOpened = false;
        this.mChooserPlusButton.setRotation(-135.0F);
        this.mChooserPlusButton.animate().rotation(0.0F).setDuration(200L).setListener(null);

        ObjectAnimator.ofFloat(this, "dimAmount", new float[]{1.0F, 0.0F}).setDuration(200L).start();

        int i = 0;
        while (i < mChooserItemViewList.size()) {
            ChooserItemView chooserItemView = mChooserItemViewList.get(i);

            float angle = getAngle(i);

            ViewPropertyAnimator animator = chooserItemView.animate()
                    .translationX(-getOffsetX(angle))
                    .translationY(-getOffsetY(angle))
                    .setInterpolator(this.mAnticipateInterpolator)
                    .setDuration(350L);
            AnimatorListenerAdapter animatorListener;
            if (i == 0) {
                animatorListener = mCloseAnimatorListener;
            } else {
                animatorListener = null;
            }
            animator.setListener(animatorListener);

            View iconView = chooserItemView.getIconView();
            iconView.setRotation(0.0F);
            iconView.animate().rotation(720.0F).setInterpolator(this.mAccelerateInterpolator).setDuration(350L);
            i++;
        }
    }

    /**
     * 选中某个item
     *
     * @param chooserItemView
     */
    private void choose(ChooserItemView chooserItemView) {
        mOpened = false;
        mChoosed = true;
        mChoosedIndex = indexOfChild(chooserItemView);
        if (this.mListener != null) {
            this.mListener.onItemButtonChoosed();
        }

        this.mChooserPlusButton.setRotation(-135.0F);
        this.mChooserPlusButton.animate().rotation(0.0F).setDuration(200L).setListener(null);

        ObjectAnimator.ofFloat(this, "dimAmount", new float[]{1.0F, 0.0F}).setDuration(200L).start();

        for (ChooserItemView itemView : mChooserItemViewList) {
            boolean selected = chooserItemView == itemView;
            itemView.setAlpha(1.0F);
            itemView.setScaleX(1.0F);
            itemView.setScaleY(1.0F);
            int scale;
            if (selected) {
                scale = 5;
            } else {
                scale = 0;
            }
            ViewPropertyAnimator viewPropertyAnimator = itemView.animate().alpha(0.0f).scaleX(scale).scaleY(scale).setDuration(500L);
            if (selected) {
                viewPropertyAnimator.setListener(mChooseAnimatorListener);
            } else {
                viewPropertyAnimator.setListener(null);
            }
        }
    }

    private float getAngleValue(float angle) {
        return (float) (3.141592653589793D * angle / 180.0D);
    }

    private float getAngle(int index) {
        int offset;
        if (this.isCircular) {
            offset = 0;
        } else {
            offset = this.angleIndexOffset;
        }

        float angleInterval = this.maxAngle - this.minAngle;

        float interval = angleInterval / (offset + this.mChooserItemViewList.size() - 1);
        return this.maxAngle - interval * (offset + index);
    }

    /**
     * 根据角度计算x轴偏离角度
     *
     * @param angle
     * @return
     */
    private int getOffsetX(float angle) {
        if (this.isCircular) {
            return (int) ((this.maxRadius + this.undershoot) * (float) Math.cos(angle));
        }

        return (int) ((getWidth() - getPaddingLeft() - getPaddingRight() - this.mChooserPlusButton.getLeft() - this.mChooserPlusButton.getWidth() / 2) * Math.cos(angle));
    }

    private int getOffsetY(float paramFloat) {
        if (this.isCircular) {
            return -(int) ((this.maxRadius + this.undershoot) * (float) Math.sin(paramFloat)) - this.chooserItemOffsetY;
        }
        return -(int) ((this.mChooserPlusButton.getTop() + this.mChooserPlusButton.getHeight() / 2) * Math.sin(paramFloat)) - this.chooserItemOffsetY;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int allPadding = getPaddingLeft() + getPaddingRight() + getPaddingTop() + getPaddingBottom();
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mChooserPlusButton.getLayoutParams();
        int allMargin = lp.leftMargin + lp.topMargin + lp.rightMargin + lp.bottomMargin;
        int height = bottom - top;
        if (allPadding != this.mAllLayoutPadding || allMargin != this.mAllButtonMargin || height != mHeight) {
            mAllLayoutPadding = allPadding;
            mAllButtonMargin = allMargin;
            mHeight = height;
            getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
        }
    }

    private final AnimatorListenerAdapter mOpenAnimatorListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationEnd(android.animation.Animator animation) {
            super.onAnimationEnd(animation);
            if (mListener != null) {
                mListener.onChooserOpen();
            }
        }
    };
    private final AnimatorListenerAdapter mCloseAnimatorListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationEnd(android.animation.Animator animation) {
            super.onAnimationEnd(animation);
            if (mListener != null) {
                mListener.onChooserClose();
            }
        }
    };

    // 选择某个项目点击后
    private final AnimatorListenerAdapter mChooseAnimatorListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationEnd(android.animation.Animator animation) {
            super.onAnimationEnd(animation);
            mChoosed = false;
            if (mListener != null) {
                mListener.onItemButtonClick(mChooserItemViewList.get(mChoosedIndex).getChooserItem());
            }
            mChoosedIndex = -1;
        }
    };

    private final ViewTreeObserver.OnPreDrawListener mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {

        @Override
        public boolean onPreDraw() {
            ChooserLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
            if (mChooserItemViewList.size() == 0) {
                return true;
            }

            layoutChooserItems();
            return false;
        }
    };

    public interface ChooserLayoutListener {

        void onPlusButtonClick();

        void onChooserOpen();

        void onChooserClose();

        void onItemButtonChoosed();

        void onItemButtonClick(ChooserItemView.ChooserItem chooserItem);
    }
}

package com.ttwishing.chooserlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ChooserItemView extends FrameLayout {

    private ImageView iconImageView;
    private ChooserItem chooserItem;

    public ChooserItemView(Context context) {
        super(context);
        init(context);
    }

    public ChooserItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChooserItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.iconImageView = new ImageView(getContext());
        this.iconImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2);
        lp.gravity = 17;
        addView(this.iconImageView, lp);
        setBackgroundResource(R.drawable.plus_button_selector);
    }

    public ChooserItemView.ChooserItem getChooserItem() {
        return this.chooserItem;
    }

    public View getIconView() {
        return this.iconImageView;
    }

    public void setChooserItem(ChooserItemView.ChooserItem paramChooserItem) {
        this.iconImageView.setImageResource(paramChooserItem.getIconResId());
        this.chooserItem = paramChooserItem;
    }

    public void setIconIndex(int iconIndex) {
        this.chooserItem.setIconIndex(iconIndex);
        this.iconImageView.setImageResource(this.chooserItem.getIconResId());
    }

    public static class ChooserItem {
        private List<Integer> iconResIds;
        private boolean aYa;
        private int index = 0;

        public ChooserItem(int resId, boolean paramBoolean) {
            Integer[] arrayOfInteger = new Integer[1];
            iconResIds = new ArrayList<>();
            iconResIds.add(resId);
            this.aYa = paramBoolean;
        }

        public ChooserItem(List<Integer> iconResIds, boolean paramBoolean) {
            this.iconResIds = iconResIds;
            this.aYa = paramBoolean;
        }

        public boolean EW() {
            return this.aYa;
        }

        public int getIconResId() {
            return this.iconResIds.get(this.index).intValue();
        }

        public void setIconIndex(int index) {
            this.index = index;
        }
    }

}

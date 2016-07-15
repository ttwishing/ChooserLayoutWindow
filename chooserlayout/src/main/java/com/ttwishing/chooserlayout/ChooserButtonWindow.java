package com.ttwishing.chooserlayout;

import java.util.List;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public abstract class ChooserButtonWindow extends SafePopupWindow {

    protected ChooserLayout mChooserLayout;

    private final ChooserLayout.ChooserLayoutListener mChooserLayoutListener = new ChooserLayout.ChooserLayoutListener() {

        @Override
        public void onChooserClose() {

        }

        @Override
        public void onItemButtonChoosed() {

        }

        @Override
        public void onChooserOpen() {

        }

        @Override
        public void onPlusButtonClick() {

        }

        @Override
        public void onItemButtonClick(ChooserItemView.ChooserItem chooserItem) {

        }
    };

    public ChooserButtonWindow(Activity activity, final ViewGroup viewGroup, int layoutId, List<ChooserItemView.ChooserItem> items) {
        super(viewGroup.getContext());
        mChooserLayout = ((ChooserLayout) LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false));
        setContentView(mChooserLayout);
        setWindowLayoutMode(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new BitmapDrawable(viewGroup.getResources()));
        mChooserLayout.setListener(mChooserLayoutListener);
        mChooserLayout.setChooserItems(items);
        setInputMethodMode(1);

        viewGroup.post(new Runnable() {

            @Override
            public void run() {
                //初始化后,一直是要显示的
                showAtLocation(viewGroup, 49, 0, 0);
            }
        });
    }

    public boolean handleBackPressed() {
        if (mChooserLayout == null)
            return false;

        if (mChooserLayout.ismOpened()) {
            mChooserLayout.close();
            return true;
        }
        return false;
    }

    public void handleDestroy() {
        dismiss();
        mChooserLayout = null;
    }
}

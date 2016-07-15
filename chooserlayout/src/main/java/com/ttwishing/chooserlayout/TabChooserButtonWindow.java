package com.ttwishing.chooserlayout;

import android.app.Activity;
import android.view.ViewGroup;

import java.util.List;

public class TabChooserButtonWindow extends ChooserButtonWindow {

    public TabChooserButtonWindow(Activity activity, ViewGroup viewGroup, List<ChooserItemView.ChooserItem> list) {
        super(activity, viewGroup, R.layout.center_chooser_layout, list);
    }

}

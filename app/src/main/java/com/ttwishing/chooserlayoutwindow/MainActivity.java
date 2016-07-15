package com.ttwishing.chooserlayoutwindow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.ttwishing.chooserlayout.ChooserItemView;
import com.ttwishing.chooserlayout.TabChooserButtonWindow;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TabChooserButtonWindow mChooserButtonWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChooserButtonWindow = new TabChooserButtonWindow(this, (ViewGroup) findViewById(R.id.bottom), getChooserItems());
    }

    @Override
    protected void onDestroy() {
        mChooserButtonWindow.handleDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mChooserButtonWindow.handleBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    private static List<ChooserItemView.ChooserItem> getChooserItems() {
        ArrayList list = new ArrayList();
        list.add(new ChooserItemView.ChooserItem(R.drawable.chooser_item_selector, true));
        list.add(new ChooserItemView.ChooserItem(R.drawable.chooser_item_selector, true));
        list.add(new ChooserItemView.ChooserItem(R.drawable.chooser_item_selector, true));
        list.add(new ChooserItemView.ChooserItem(R.drawable.chooser_item_selector, true));
        list.add(new ChooserItemView.ChooserItem(R.drawable.chooser_item_selector, true));
        list.add(new ChooserItemView.ChooserItem(R.drawable.chooser_item_selector, true));
        list.add(new ChooserItemView.ChooserItem(R.drawable.chooser_item_selector, true));
        return list;
    }
}

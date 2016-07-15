package com.ttwishing.chooserlayout;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;

public class SafePopupWindow extends PopupWindow {
	
	public SafePopupWindow(Context context) {
		super(new View(context));
		setSoftInputMode(1);
		setInputMethodMode(2);
	}

	@Override
	public void dismiss() {
		try{
			super.dismiss();
		}catch (Throwable t) {
		}
	}
	
	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
		try{
			super.showAsDropDown(anchor, xoff, yoff, gravity);
		}catch (Throwable t) {
		}
	}
	
	@Override
	public void showAsDropDown(View anchor) {
		try{
			super.showAsDropDown(anchor);
		}catch (Throwable t) {
		}
	}
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		try {
			super.showAtLocation(parent, gravity, x, y);
		} catch (Throwable t) {
		}
	}
	
	@Override
	public void update() {
		try {
			super.update();
		} catch (Throwable t) {
		}
	}
	
	@Override
	public void update(int width, int height) {
		try {
			super.update(width, height);
		} catch (Throwable t) {
		}
	}
	
	@Override
	public void update(View anchor, int xoff, int yoff, int width, int height) {
		try {
			super.update(anchor, xoff, yoff, width, height);
		} catch (Throwable t) {
		}
	}
	
	@Override
	public void update(int x, int y, int width, int height) {
		try {
			super.update(x, y, width, height);
		} catch (Throwable t) {
		}
	}
	
	@Override
	public void update(int x, int y, int width, int height, boolean force) {
		try {
			super.update(x, y, width, height, force);
		} catch (Throwable t) {
		}
	}
	
	@Override
	public void update(View anchor, int width, int height) {
		try {
			super.update(anchor, width, height);
		} catch (Throwable t) {
		}
	}
}
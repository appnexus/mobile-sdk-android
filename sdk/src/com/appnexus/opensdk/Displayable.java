package com.appnexus.opensdk;

import android.view.View;

public interface Displayable {
	public View getView();
	public void fail();
	public boolean failed();
}

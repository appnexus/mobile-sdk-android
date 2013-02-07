package com.appnexus.opensdk;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class InterstitialAdView extends AdView {
	protected ArrayList<Size> allowedSizes;
	protected int backgroundColor=Color.BLACK;
	protected static InterstitialAdView INTERSTITIALADVIEW_TO_USE;
	protected static Queue<Displayable> q = new LinkedList<Displayable>();

	public InterstitialAdView(Context context) {
		super(context);
	}

	public InterstitialAdView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InterstitialAdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public InterstitialAdView(Context context, String placement_id) {
		super(context, placement_id);
	}

	@Override
	protected void setup(Context context, AttributeSet attrs){
		super.setup(context, attrs);
		INTERSTITIALADVIEW_TO_USE=this;
		mAdFetcher.setAutoRefresh(false);
		
		// Get the screen size
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		measuredHeight=dm.heightPixels;
		measuredWidth=dm.widthPixels;
		int h_adjust=0;
		
		Activity a = (Activity)context;
		if(a!=null){
			Rect r = new Rect();
			a.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
			h_adjust+=a.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
			measuredHeight-=h_adjust;
		}
		
		float scale = dm.density;
		measuredHeight=(int)(measuredHeight/scale+0.5f);
		measuredWidth=(int)(measuredWidth/scale+0.5f);
		
		allowedSizes = new ArrayList<Size>();
		
		//Set up the allowed sizes TODO: this will be server-side
		if(new Size(300, 250).fitsIn(measuredWidth, measuredHeight)) allowedSizes.add(new Size(300,250));
		if(new Size(320, 480).fitsIn(measuredWidth, measuredHeight)) allowedSizes.add(new Size(320,480));
		if(new Size(900, 500).fitsIn(measuredWidth, measuredHeight)) allowedSizes.add(new Size(900,500));
		if(new Size(1024, 1024).fitsIn(measuredWidth, measuredHeight)) allowedSizes.add(new Size(1024,1024));
		
	}
	
	@Override
	protected void loadVariablesFromXML(Context context, AttributeSet attrs) {
		TypedArray a = context
				.obtainStyledAttributes(attrs, R.styleable.InterstitialAdView);

		final int N = a.getIndexCount();
		Clog.v(Clog.xmlLogTag, Clog.getString(R.string.found_n_in_xml, N));
		for (int i = 0; i < N; ++i) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.InterstitialAdView_placement_id:
				setPlacementID(a.getString(attr));
				Clog.d(Clog.xmlLogTag,
						Clog.getString(R.string.placement_id, a.getString(attr)));
				break;
			case R.styleable.InterstitialAdView_test:
				Settings.getSettings().test_mode = a.getBoolean(attr, false);
				Clog.d(Clog.xmlLogTag,
						Clog.getString(R.string.xml_set_test,
								Settings.getSettings().test_mode));
				break;
			}
		}
		a.recycle();
	}
	
	@Override
	public void loadAd(){
		if(mAdFetcher!=null){
			//Load an interstitial ad
			mAdFetcher.stop();
			mAdFetcher.start();
		}
	}
	
	@Override
	protected void display(Displayable d){
		InterstitialAdView.q.add(d);
	}
	
	protected void render(){
		Displayable view = InterstitialAdView.q.poll();
		if(view==null) return; //Throw an error
		super.display(view);
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom){
		onFirstLayout();
	}
	
	@Override
	public void setAdWidth(int width){
		Log.w("OPENSDK-INTERFACE", "setAdWidth() called for an interstitial ad."); //TODO clog
	}
	
	@Override
	public void setAdHeight(int height){
		Log.w("OPENSDK-INTERFACE", "setAdHeight() called for an interstitial ad.");//TODO clog
	}
	
	//TODO
	public void setAdListener(AdListener listener){
		
	}
	
	//TODO
	public AdListener getAdListener(){
		return null;
	}
	
	
	//TODO
	public void show(){
		if(!InterstitialAdView.q.isEmpty()){
			Intent i = new Intent(getContext(), AdActivity.class);
			getContext().startActivity(i);
		}
	}
	
	public ArrayList<Size> getAllowedSizes(){
		return allowedSizes;
	}
	
	public void setAllowedSizes(ArrayList<Size> allowed_sizes){
		allowedSizes=allowed_sizes;
	}
	
	public void setBackgroundColor(int color){
		backgroundColor = color;
	}
	
	public int getBackgroundColor(){return backgroundColor;}
	
	
	//TODO?
	public void destroy(){
	
	}
	
	abstract public class AdListener{
		abstract public void onAdLoaded(InterstitialAdView iAdView);
		abstract public void onAdRequestFailed(InterstitialAdView iAdView);
	}
	
	public class Size{
		private int w;
		private int h;
		Size(int w, int h){
			this.w=w;
			this.h=h;
		}
		public int width(){
			return w;
		}
		public int height(){
			return h;
		}
		public boolean fitsIn(int width, int height){
			return h < height && w < width;
		}
	}
}

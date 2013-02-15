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
import android.util.Pair;
import android.view.Window;
import android.view.WindowManager;

public class InterstitialAdView extends AdView {
	protected static final long MAX_AGE = 60000;
	protected ArrayList<Size> allowedSizes;
	protected int backgroundColor=Color.BLACK;
	protected static InterstitialAdView INTERSTITIALADVIEW_TO_USE;
	protected static Queue<Pair<Long, Displayable>> q = new LinkedList<Pair<Long, Displayable>>();
	protected AdListener adListener;

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
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.load_ad_int));
		if(mAdFetcher!=null){
			//Load an interstitial ad
			mAdFetcher.stop();
			mAdFetcher.start();
		}
	}
	
	protected void fail(){
		if(adListener!=null) adListener.onAdRequestFailed(this);
	}
	
	@Override
	protected void display(Displayable d){
		if(adListener!=null) adListener.onAdLoaded(this);
		InterstitialAdView.q.add(new Pair<Long, Displayable>(System.currentTimeMillis(), d));
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom){
		onFirstLayout();
	}
	
	@Override
	public void setAdWidth(int width){
		Clog.w(Clog.publicFunctionsLogTag, Clog.getString(R.string.set_width_int));
	}
	
	@Override
	public void setAdHeight(int height){
		Clog.w(Clog.publicFunctionsLogTag, Clog.getString(R.string.set_height_int));
	}
	
	public void setAdListener(AdListener listener){
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.set_ad_listener));
		adListener=listener;
	}
	
	public AdListener getAdListener(){
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.get_ad_listener));
		return adListener;
	}
	

	public int show(){
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.show_int));
		//Make sure there is an ad to show
		ArrayList<Pair<Long, Displayable>> to_remove = new ArrayList<Pair<Long, Displayable>>();
		long now = System.currentTimeMillis();
		for(Pair<Long, Displayable> p : InterstitialAdView.q){
			if(p==null || p.second == null || now-p.first > InterstitialAdView.MAX_AGE){
				to_remove.add(p);
			}else{
				//We've reached a valid ad, so we can launch the activity.
				break;
			}
		}
		//Before we do anything else, clear the head of the queue of invalid ads
		for(Pair<Long, Displayable> p : to_remove){
			InterstitialAdView.q.remove(p);
		}
		if(!InterstitialAdView.q.isEmpty()){
			Intent i = new Intent(getContext(), AdActivity.class);
			i.putExtra("Time", now);
			i.putExtra("Orientation", getContext().getResources().getConfiguration().orientation);
			getContext().startActivity(i);
			return InterstitialAdView.q.size()-1; // Return the number of ads remaining, less the one we're about to show
		}
		Clog.w(Clog.baseLogTag, Clog.getString(R.string.empty_queue));
		return InterstitialAdView.q.size();
	}
	
	public ArrayList<Size> getAllowedSizes(){
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.get_allowed_sizes));
		return allowedSizes;
	}
	
	public void setAllowedSizes(ArrayList<Size> allowed_sizes){
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.set_allowed_sizes));
		allowedSizes=allowed_sizes;
	}
	
	public void setBackgroundColor(int color){
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.set_bg));
		backgroundColor = color;
	}
	
	public int getBackgroundColor(){
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.get_bg));
		return backgroundColor;}
	
	
	public void destroy(){
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.destroy_int));
		if(this.mAdFetcher!=null) mAdFetcher.stop();
		InterstitialAdView.q=null;
		InterstitialAdView.INTERSTITIALADVIEW_TO_USE=null;
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
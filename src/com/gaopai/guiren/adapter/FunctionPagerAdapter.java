package com.gaopai.guiren.adapter;

import java.util.List;

import android.os.Parcelable;
import android.view.View;

import com.gaopai.guiren.view.ViewPager;

public class FunctionPagerAdapter extends PagerAdapter{

	
	private List<View> views;
	
	
	public FunctionPagerAdapter(List<View> views) {
		this.views = views;
	}


	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub
		( (ViewPager) arg0).removeView(views.get(arg1));
	}


	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.size();
	}


	@Override
	public Object instantiateItem(View arg0, int arg1) {
		// TODO Auto-generated method stub
		if(arg0 == null || views == null || views.get(arg1) == null){
			return null;
		}
		( (ViewPager) arg0).addView(views.get(arg1), 0);
		return views.get(arg1);
	}


	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		
		return arg0 == arg1;
	}


	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub
		
	}

}

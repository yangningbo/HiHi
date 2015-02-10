package com.gaopai.guiren.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.R.drawable;
import com.gaopai.guiren.adapter.FunctionPagerAdapter;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.PreferenceOperateUtils;
import com.gaopai.guiren.utils.SPConst;
import com.gaopai.guiren.view.ViewPager;
import com.gaopai.guiren.view.ViewPager.OnPageChangeListener;
import com.withparadox2.lvscrollpredict.oncescroll.OnceScroll;
import com.withparadox2.lvscrollpredict.oncescroll.OnceScroll.Callback;

/**
 * 展示引导图片界面
 * 
 */
public class GuideActivity extends Activity implements OnPageChangeListener {
	private Context mContext;
	private ViewPager mViewPager;
	private String[] imgArray;
	private int mPosition = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.once_scroll);
		OnceScroll onceScroll = (OnceScroll) findViewById(R.id.once_scroll);
		onceScroll.setCallback(new Callback() {

			@Override
			public void onClick() {
				goActivity();
			}
		});
		// imgArray = getResources().getStringArray(R.array.function_array);
		// initData();
		saveGuideVersion();
	}

	/**
	 * 保存当前版本号
	 * 
	 */
	private void saveGuideVersion() {
		int version = FeatureFunction.getAppVersion(this);
		PreferenceOperateUtils po = new PreferenceOperateUtils(mContext, SPConst.SP_DEFAULT);
		po.setInt(SPConst.KEY_GUIDE_START_PAGE, version);
		// DamiApp.getInstance().getPou().setInt(SPConst.KEY_GUIDE_START_PAGE,
		// version);
	}

	/**
	 * 退出界面
	 * 
	 */
	private void goActivity() {
		setResult(RESULT_OK);
		GuideActivity.this.finish();
	}

	/**
	 * 初始化图片，并绑定到ViewPager上</br> 在最后一张图片上设置跳转监听
	 */
	private void initData() {
		LayoutParams mParams = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		List<View> views = new ArrayList<View>();
		for (int i = 0; i < imgArray.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			iv.setImageResource(getResourceByReflect(imgArray[i]));
			iv.setScaleType(ScaleType.FIT_XY);
			views.add(iv);
			if (i == imgArray.length - 1) { // 在最后一页上点击图片触发的监听，如果不需要则不用编写此监听
				iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						goActivity();
					}
				});
			}
		}
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		FunctionPagerAdapter adpter = new FunctionPagerAdapter(views);
		mViewPager.setAdapter(adpter);
		mViewPager.setCurrentItem(mPosition);
		mViewPager.setOnPageChangeListener(this);
	}

	/**
	 * 通过反射获得图片的resources id
	 * 
	 * @param imageName
	 *            图片名称
	 * @return r_id 资源id
	 */
	public int getResourceByReflect(String imageName) {
		Class<drawable> drawable = R.drawable.class;
		Field field = null;
		int r_id;
		try {
			field = drawable.getField(imageName);
			r_id = field.getInt(field.getName());
		} catch (Exception e) {
			r_id = R.drawable.function_1;
			if (imageName.equals("function_2")) {
				r_id = R.drawable.function_2;
			} else if (imageName.equals("function_3")) {
				r_id = R.drawable.function_3;
			}
			Log.i("ERROR", "PICTURE NOT　FOUND！");
		}
		return r_id;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onPageScrolled(int position, float paramFloat, int paramInt2) {

	}

	@Override
	public void onPageSelected(int position) {
		mPosition = position;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (mViewPager.getRelationX() > 0 && mPosition == imgArray.length - 1 && state == 2) {
			goActivity();
		}
	}
}

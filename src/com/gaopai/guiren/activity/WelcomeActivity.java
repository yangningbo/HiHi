package com.gaopai.guiren.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.gaopai.guiren.R;

/**
 * 欢迎界面，启动程序时进入。</br> 通过线程控制2秒后跳转到MainActivity。
 */
public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		ImageView view = (ImageView) findViewById(R.id.iv_back);
		Animation welcomeAnimation = AnimationUtils.loadAnimation(this, R.anim.welcome_scale);
		view.startAnimation(welcomeAnimation);
		showMainpage();
	}

	public void showMainpage() {

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intentt = new Intent(WelcomeActivity.this,
						MainActivity.class);
				WelcomeActivity.this.startActivity(intentt);
				WelcomeActivity.this.finish();
				
			}
		}, 2000);
	}

	/**
	 * 点击后退键，则退出整个程序System.exit(0);
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			this.finish();
			System.exit(0);
		}
		return super.dispatchKeyEvent(event);
	}

}

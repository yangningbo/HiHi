package com.gaopai.guiren.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.support.DynamicHelper;
import com.gaopai.guiren.support.TextLimitWatcher;
import com.gaopai.guiren.support.DynamicHelper.DyCallback;
import com.gaopai.guiren.support.DynamicHelper.DySoftCallback;
import com.gaopai.guiren.utils.ViewUtil;

public class SpreadDynamicActivity extends BaseActivity {
	
	private ViewGroup layoutDyContent;
	private DynamicHelper dynamicHelper;
	private View dyView;
	private TypeHolder bean;
	
	private EditText etContent;
	private TextView tvWordNum;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_spread_dynamic);
		bean = (TypeHolder) getIntent().getSerializableExtra("bean");
		if (bean == null) {
			SpreadDynamicActivity.this.finish();
			return;
		}
		View v = mTitleBar.addRightButtonView(R.drawable.icon_titlebar_send_dy);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				spreadDy();
			}
		});
		
		etContent = ViewUtil.findViewById(this, R.id.et_dynamic_msg);
		tvWordNum = ViewUtil.findViewById(this, R.id.tv_num_limit);
		etContent.addTextChangedListener(new TextLimitWatcher(tvWordNum, 500));
		dynamicHelper = new DynamicHelper(mContext, DynamicHelper.DY_PROFILE);
		dynamicHelper.setCallback(dynamicCallback);
		
		layoutDyContent.addView(dynamicHelper.getView(dyView, bean));
	}
	
	private void spreadDy() {
		dynamicHelper.spread(bean);
		SpreadDynamicActivity.this.finish();
	}
	
	public static Intent getIntent(Context context, TypeHolder dyHolder) {
		Intent intent = new Intent(context, SpreadDynamicActivity.class);
		intent.putExtra("bean", dyHolder);
		return intent;
	}
	private DyCallback dynamicCallback = new DySoftCallback() {
		@Override
		public void onVoiceStart() {
			// TODO Auto-generated method stub
			bindDyView();
		}

		@Override
		public void onVoiceStop() {
			// TODO Auto-generated method stub
			bindDyView();
		}
	};
	
	private void bindDyView() {
		layoutDyContent.addView(dynamicHelper.getView(dyView, bean));
	}
	
	
}

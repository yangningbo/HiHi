package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.dynamic.DynamicBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.GuestBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.support.DynamicHelper;
import com.gaopai.guiren.support.DynamicHelper.DyCallback;
import com.gaopai.guiren.support.DynamicHelper.DySoftCallback;
import com.gaopai.guiren.support.TextLimitWatcher;
import com.gaopai.guiren.utils.ViewUtil;

public class SpreadDynamicActivity extends BaseActivity {

	private ViewGroup layoutDyContent;
	private DynamicHelper dynamicHelper;
	private View dyView;
	private TypeHolder bean;

	private EditText etContent;
	private TextView tvWordNum;

	public static final int TYPE_SPREAD_FIRST = 0;
	public static final int TYPE_SPREAD_SECOND = 1;

	private int type;

	public final static String KEY_TYPE = "key_type";
	public final static String KEY_BEAN = "key_bean";

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_spread_dynamic);
		bean = (TypeHolder) getIntent().getSerializableExtra(KEY_BEAN);
		if (bean == null) {
			SpreadDynamicActivity.this.finish();
			return;
		}
		type = getIntent().getIntExtra(KEY_TYPE, TYPE_SPREAD_SECOND);
		View v = mTitleBar.addRightButtonView(R.drawable.icon_titlebar_send_dy);
		mTitleBar.setTitleText(R.string.spread_dynamic);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (type == TYPE_SPREAD_SECOND) {
					spreadDy();
				}
			}
		});

		etContent = ViewUtil.findViewById(this, R.id.et_dynamic_msg);
		tvWordNum = ViewUtil.findViewById(this, R.id.tv_num_limit);
		layoutDyContent = ViewUtil.findViewById(this, R.id.layout_profile_dy_content);
		etContent.addTextChangedListener(new TextLimitWatcher(tvWordNum, 500));
		if (type == TYPE_SPREAD_SECOND) {
			dynamicHelper = new DynamicHelper(mContext, DynamicHelper.DY_MY_LIST);
		} else {
			dynamicHelper = new DynamicHelper(mContext, DynamicHelper.DY_PROFILE);
		}
		dynamicHelper.setCallback(dynamicCallback);

		layoutDyContent.addView(dynamicHelper.getView(dyView, bean));
	}

	private void spreadDy() {
		dynamicHelper.spread(bean);
		SpreadDynamicActivity.this.finish();
	}

	public static Intent getIntent(Context context, TypeHolder dyHolder) {
		Intent intent = new Intent(context, SpreadDynamicActivity.class);
		intent.putExtra(KEY_BEAN, dyHolder);
		intent.putExtra(KEY_TYPE, TYPE_SPREAD_SECOND);
		return intent;
	}

	private DyCallback dynamicCallback = new DySoftCallback() {
		@Override
		public void onVoiceStart() {
			bindDyView();
		}

		@Override
		public void onVoiceStop() {
			bindDyView();
		}
	};

	private void bindDyView() {
		layoutDyContent.addView(dynamicHelper.getView(dyView, bean));
	}

	public static TypeHolder getUserSpreadHolder(User user) {
		TypeHolder typeHolder = new TypeHolder();
		typeHolder.jsoncontent = new DynamicBean.JsonContent();
		DynamicBean.JsonContent jsonContent = typeHolder.jsoncontent;

		typeHolder.type = DynamicHelper.TYPE_SPREAD_USER;
		jsonContent.uid = user.uid;
		jsonContent.headsmall = user.headsmall;
		jsonContent.realname = user.realname;
		jsonContent.company = user.company;
		jsonContent.post = user.post;
		jsonContent.integral = user.integral;
		return typeHolder;
	}

	public static TypeHolder getTribeSpreadHolder(Tribe tribe) {
		TypeHolder typeHolder = getBaseTribeSpreadHolder(tribe);
		typeHolder.type = DynamicHelper.TYPE_SPREAD_TRIBE;
		typeHolder.jsoncontent.logo = tribe.logosmall;
		return typeHolder;
	}

	public static TypeHolder getMeetingSpreadHolder(Tribe tribe) {
		TypeHolder typeHolder = getBaseTribeSpreadHolder(tribe);
		typeHolder.type = DynamicHelper.TYPE_SPREAD_MEETING;
		typeHolder.jsoncontent.time = String.valueOf(tribe.start);
		return typeHolder;
	}

	public static TypeHolder getBaseTribeSpreadHolder(Tribe tribe) {
		TypeHolder typeHolder = new TypeHolder();
		typeHolder.jsoncontent = new DynamicBean.JsonContent();
		DynamicBean.JsonContent jsonContent = typeHolder.jsoncontent;

		jsonContent.time = String.valueOf(tribe.start);
		jsonContent.name = tribe.name;
		List<DynamicBean.GuestBean> guestBeans = new ArrayList<DynamicBean.GuestBean>();
		if (tribe.member != null) {
			for (Tribe.Member member : tribe.member) {
				GuestBean guestBean = new GuestBean();
				guestBean.realname = member.realname;
				guestBean.uid = member.uid;
				guestBeans.add(guestBean);
			}
		}
		jsonContent.guest = guestBeans;
		jsonContent.tid = tribe.id;
		return typeHolder;
	}

	public static TypeHolder getWebSpreadHolder(String image, String title, String info, String url) {
		TypeHolder typeHolder = new TypeHolder();
		typeHolder.jsoncontent = new DynamicBean.JsonContent();
		DynamicBean.JsonContent jsonContent = typeHolder.jsoncontent;
		typeHolder.type = DynamicHelper.TYPE_SPREAD_LINK;

		jsonContent.title = title;
		jsonContent.image = image;
		jsonContent.desc = info;
		jsonContent.url = url;
		return typeHolder;
	}

	public static TypeHolder getMsgSpreadHolder(MessageInfo messageInfo) {
		TypeHolder typeHolder = new TypeHolder();
		typeHolder.jsoncontent = new DynamicBean.JsonContent();
		DynamicBean.JsonContent jsonContent = typeHolder.jsoncontent;
		typeHolder.type = DynamicHelper.TYPE_SPREAD_MSG;

		jsonContent.fileType = messageInfo.fileType;
		jsonContent.headImgUrl = messageInfo.headImgUrl;
		jsonContent.displayName = messageInfo.displayname;
		jsonContent.content = messageInfo.content;
		jsonContent.imgUrlS = messageInfo.imgUrlS;
		jsonContent.imgUrlL = messageInfo.imgUrlL;
		jsonContent.voiceTime = messageInfo.voiceTime;
		jsonContent.voiceUrl = messageInfo.voiceUrl;
		return typeHolder;
	}

	public static Intent getTribeIntent(Context context, Tribe tribe) {
		Intent intent = new Intent(context, SpreadDynamicActivity.class);
		TypeHolder typeHolder = getTribeSpreadHolder(tribe);
		typeHolder.title = context.getString(R.string.spread_tribe);
		intent.putExtra(KEY_BEAN, typeHolder);
		intent.putExtra(KEY_TYPE, TYPE_SPREAD_FIRST);
		return intent;
	}

	public static Intent getMeetingIntent(Context context, Tribe tribe) {
		Intent intent = new Intent(context, SpreadDynamicActivity.class);
		TypeHolder typeHolder = getMeetingSpreadHolder(tribe);
		typeHolder.title = context.getString(R.string.spread_meeting);
		intent.putExtra(KEY_BEAN, typeHolder);
		intent.putExtra(KEY_TYPE, TYPE_SPREAD_FIRST);
		return intent;
	}

	public static Intent getWebIntent(Context context, String image, String title, String info, String url) {
		Intent intent = new Intent(context, SpreadDynamicActivity.class);
		TypeHolder typeHolder = getWebSpreadHolder(image, title, info, url);
		typeHolder.title = context.getString(R.string.spread_web);
		intent.putExtra(KEY_BEAN, typeHolder);
		intent.putExtra(KEY_TYPE, TYPE_SPREAD_FIRST);
		return intent;
	}

	public static Intent getUserIntent(Context context, User user) {
		Intent intent = new Intent(context, SpreadDynamicActivity.class);
		TypeHolder typeHolder = getUserSpreadHolder(user);
		typeHolder.title = context.getString(R.string.spread_user);
		intent.putExtra(KEY_BEAN, typeHolder);
		intent.putExtra(KEY_TYPE, TYPE_SPREAD_FIRST);
		return intent;
	}

	public static Intent getMsgIntent(Context context, MessageInfo messageInfo) {
		Intent intent = new Intent(context, SpreadDynamicActivity.class);
		TypeHolder typeHolder = getMsgSpreadHolder(messageInfo);
		typeHolder.title = context.getString(R.string.spread_msg);
		intent.putExtra(KEY_BEAN, typeHolder);
		intent.putExtra(KEY_TYPE, TYPE_SPREAD_FIRST);
		return intent;
	}

}

package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatMessageActivity;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.view.FlowLayout;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends BaseActivity implements OnClickListener {

	private User mUser;
	private User tUser;
	public final static String KEY_USER = "user";
	
	private boolean isSelf;

	private ImageView ivHeader;
	private TextView tvUserName;
	private TextView tvUserInfo;
	private TextView tvFancyCount;

	private TextView tvFollowersCount;
	private TextView tvFansCount;
	private TextView tvMeetingsCount;
	private TextView tvTribesCount;

	private TextView tvRealName;
	private TextView tvCompany;
	private TextView tvPartment;
	private TextView tvJob;

	private TextView tvEmail;
	private TextView tvPhone;
	private TextView tvWeibo;
	private TextView tvWeixin;

	private View layoutEmail;
	private View layoutPhone;
	private View layoutWeibo;
	private View layoutWeixin;

	private View tvReverification;
	private View tvAddTags;

	private View tvRevealAllTags;

	private FlowLayout tagLayout;
	private List<String> tagList = new ArrayList<String>();;
	private List<String> recTagList = new ArrayList<String>();;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_profile);
		tUser = (User) getIntent().getSerializableExtra(KEY_USER);
		mUser = DamiCommon.getLoginResult(this);
		isSelf = tUser.uid.equals(mUser.uid);
		
		initComponent();
		bindView();
		for (int i = 0; i < 10; i++) {
			recTagList.add("推荐" + i);
		}
	}

	private void initComponent() {
		mTitleBar.setTitleText(getString(R.string.profile));
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		View setting = mTitleBar.addRightImageButtonView(android.R.drawable.ic_menu_send);
		setting.setId(R.id.ab_setting);
		setting.setOnClickListener(this);

		ivHeader = (ImageView) findViewById(R.id.iv_header);

		tvUserInfo = (TextView) findViewById(R.id.tv_user_info);
		tvUserName = (TextView) findViewById(R.id.tv_user_name);
		tvFancyCount = (TextView) findViewById(R.id.iv_star_number);

		tvFansCount = (TextView) findViewById(R.id.tv_my_fans_count);
		tvFansCount.setOnClickListener(this);
		tvFollowersCount = (TextView) findViewById(R.id.tv_my_followers_count);
		tvFollowersCount.setOnClickListener(this);
		tvMeetingsCount = (TextView) findViewById(R.id.tv_my_meetings_count);
		tvMeetingsCount.setOnClickListener(this);
		tvTribesCount = (TextView) findViewById(R.id.tv_my_tribes_count);
		tvTribesCount.setOnClickListener(this);

		tvRealName = (TextView) findViewById(R.id.tv_profile_real_name);
		tvCompany = (TextView) findViewById(R.id.tv_profile_company);
		tvPartment = (TextView) findViewById(R.id.tv_profile_partment);
		tvJob = (TextView) findViewById(R.id.tv_profile_job);

		tvEmail = (TextView) findViewById(R.id.tv_profile_email);
		tvPhone = (TextView) findViewById(R.id.tv_profile_phone_num);
		tvWeibo = (TextView) findViewById(R.id.tv_profile_weibo_num);
		tvWeixin = (TextView) findViewById(R.id.tv_profile_weixin_num);

		layoutEmail = findViewById(R.id.layout_profile_email);
		layoutEmail.setOnClickListener(this);
		layoutPhone = findViewById(R.id.layout_profile_phone_num);
		layoutPhone.setOnClickListener(this);
		layoutWeibo = findViewById(R.id.layout_profile_weibo_num);
		layoutWeibo.setOnClickListener(this);
		layoutWeixin = findViewById(R.id.layout_profile_weixin_num);
		layoutWeixin.setOnClickListener(this);

		tvReverification = findViewById(R.id.tv_reverification);
		tvReverification.setOnClickListener(this);
		tvAddTags = findViewById(R.id.tv_add_tags);
		tvAddTags.setOnClickListener(this);

		tagLayout = (FlowLayout) findViewById(R.id.layout_tags);
		tvRevealAllTags = findViewById(R.id.tv_reveal_all_tags);
		tvRevealAllTags.setOnClickListener(this);

		View bottomView = findViewById(R.id.layout_profile_bottom_follow);
		bottomView.setOnClickListener(this);
		bottomView = findViewById(R.id.layout_profile_bottom_comment);
		bottomView.setOnClickListener(this);
		bottomView = findViewById(R.id.layout_profile_bottom_msg);
		bottomView.setOnClickListener(this);
		bottomView = findViewById(R.id.layout_profile_bottom_spread);
		bottomView.setOnClickListener(this);
	}

	private void bindView() {
		tvFancyCount.setText(String.valueOf(mUser.integral));
		tvUserName.setText(mUser.realname);
		tvUserInfo.setText(mUser.company);
		Picasso.with(mContext).load(mUser.headsmall).placeholder(R.drawable.default_header)
				.error(R.drawable.default_header).into(ivHeader);

		tvFollowersCount.setText(String.valueOf(mUser.followers));
		tvFansCount.setText(String.valueOf(mUser.fansers));

		tvRealName.setText(mUser.realname);
		tvCompany.setText(mUser.company);
		tvJob.setText(mUser.post);

		tvEmail.setText(mUser.email);
		tvPhone.setText(mUser.phone);
		// tvWeixin.setText(mUser)
		// tvWeibo.setText(text)

		bindTags(tagLayout, false);
	}

	private void bindTags(FlowLayout taLayoutPara, boolean isWithDelete) {
		taLayoutPara.removeAllViews();
		for (String tag : tagList) {
			if (isWithDelete) {
				taLayoutPara.addView(creatTag(tag), taLayoutPara.getTextLayoutParams());
			} else {
				taLayoutPara.addView(creatTagWithoutDelete(tag), taLayoutPara.getTextLayoutParams());
			}
		}
	}

	public final static int REQUEST_CHANGE_PROFILE = 0;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ab_setting: {
			startActivity(SettingActivity.class);
			break;
		}
		case R.id.tv_reverification:
			startActivity(ReverificationActivity.class);
			break;
		case R.id.layout_profile_email:
			changeContact(ChangeProfileActivity.TYPE_EMAIL, mUser.email);
			break;
		case R.id.layout_profile_phone_num:
			changeContact(ChangeProfileActivity.TYPE_PHONE, mUser.phone);
			break;
		case R.id.layout_profile_weibo_num:
			changeContact(ChangeProfileActivity.TYPE_WEIBO, mUser.weibo);
			break;
		case R.id.layout_profile_weixin_num:
			changeContact(ChangeProfileActivity.TYPE_WEIXIN, mUser.weixin);
			break;
		case R.id.tv_reveal_all_tags:
		case R.id.tv_add_tags:
			showTagsWindow();
			break;
		case R.id.btn_add_tag:
			String str = etTags.getText().toString();
			if (TextUtils.isEmpty(str)) {
				showToast(R.string.input_can_not_be_empty);
				return;
			}
			etTags.setText("");
			flowTagsAdd.addView(creatTag(str), flowTagsAdd.getTextLayoutParams());
			break;
		case R.id.layout_profile_bottom_comment:
			startActivityWithUser(CommentProfileActivity.KEY_USER, CommentProfileActivity.class);
			break;
		case R.id.layout_profile_bottom_follow:
			break;
		case R.id.layout_profile_bottom_msg:
			startActivityWithUser(ChatMessageActivity.KEY_USER, ChatMessageActivity.class);
			break;
		case R.id.layout_profile_bottom_spread:
			break;
		default:
			break;
		}
	}
	
	private void startActivityWithUser(String key, Class clazz) {
		Intent intent = new Intent();
		intent.putExtra(key, tUser);
		intent.setClass(mContext, clazz);
		startActivity(intent);
	}
	

	private void changeContact(int type, String text) {
		if (TextUtils.isEmpty(text)) {
			text = "";
		}
		Intent intent = new Intent(mContext, ChangeProfileActivity.class);
		intent.putExtra(ChangeProfileActivity.KEY_TEXT, text);
		intent.putExtra(ChangeProfileActivity.KEY_TYPE, type);
		startActivityForResult(intent, REQUEST_CHANGE_PROFILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CHANGE_PROFILE) {
				mUser = DamiCommon.getLoginResult(this);
				tvEmail.setText(mUser.email);
				tvPhone.setText(mUser.phone);
				tvWeixin.setText(mUser.weixin);
				tvWeibo.setText(mUser.weibo);
			}
		}
	}

	private View creatTag(String text) {
		ViewGroup v = (ViewGroup) mInflater.inflate(R.layout.btn_send_dynamic_tag, null);
		TextView textView = (TextView) v.findViewById(R.id.tv_tag);
		textView.setText(text);
		Button button = (Button) v.findViewById(R.id.btn_delete_tag);
		button.setOnClickListener(tagDeleteClickListener);
		return v;
	}

	private View creatTagWithoutDelete(String text) {
		ViewGroup v = (ViewGroup) mInflater.inflate(R.layout.btn_send_dynamic_tag, null);
		TextView textView = (TextView) v.findViewById(R.id.tv_tag);
		textView.setText(text);
		v.findViewById(R.id.btn_delete_tag).setVisibility(View.GONE);
		return v;
	}

	private View creatTageWithAction(final String text) {
		ViewGroup v = (ViewGroup) mInflater.inflate(R.layout.btn_send_dynamic_tag, null);
		TextView textView = (TextView) v.findViewById(R.id.tv_tag);
		textView.setText(text);
		v.findViewById(R.id.btn_delete_tag).setVisibility(View.GONE);
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flowTagsAdd.addView(creatTag(text), flowTagsAdd.getTextLayoutParams());
			}
		});
		return v;
	}

	private OnClickListener tagDeleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			flowTagsAdd.removeView((View) v.getParent());
		}
	};

	private FlowLayout flowTagsAdd;
	private EditText etTags;
	private Button btnAddTags;

	private void showTagsWindow() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		View view = mInflater.inflate(R.layout.window_add_tags, null);
		flowTagsAdd = (FlowLayout) view.findViewById(R.id.flow_tags_add);
		setTagTransition(flowTagsAdd);
		FlowLayout flowTagsRec = (FlowLayout) view.findViewById(R.id.flow_tags_recommend);
		etTags = (EditText) view.findViewById(R.id.et_tags);
		btnAddTags = (Button) view.findViewById(R.id.btn_add_tag);
		btnAddTags.setOnClickListener(this);
		bindTags(flowTagsAdd, true);
		for (String tag : recTagList) {
			flowTagsRec.addView(creatTageWithAction(tag), flowTagsRec.getTextLayoutParams());
		}
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		Button btnSave = (Button) view.findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int count = flowTagsAdd.getChildCount();
				tagList.clear();
				for (int i = 0; i < count; i++) {
					tagList.add(((TextView) ((ViewGroup) flowTagsAdd.getChildAt(i)).getChildAt(0)).getText().toString());
				}
				dialog.cancel();
				bindTags(tagLayout, false);
			}
		});
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setBackgroundDrawableResource(R.drawable.transparent);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
		p.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度设置为屏幕的0.6
		p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.65
		dialogWindow.setAttributes(p);

		dialog.show();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setTagTransition(ViewGroup viewGroup) {
		if (Build.VERSION.SDK_INT > 11) {
			LayoutTransition transition = new LayoutTransition();
			setupCustomAnimations(transition);
			viewGroup.setLayoutTransition(transition);
		}
	}

	@SuppressLint("NewApi")
	private void setupCustomAnimations(LayoutTransition mTransitioner) {
		// Changing while Adding
		PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", 0, 1);
		PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 0, 1);
		PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", 0, 1);
		PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 1);
		PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f, 1f);
		PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f, 1f);

		// CHANGE_DISAPPEARING
		Keyframe kf0 = Keyframe.ofFloat(0f, 0f);
		Keyframe kf1 = Keyframe.ofFloat(.9999f, 360f);
		Keyframe kf2 = Keyframe.ofFloat(1f, 0f);
		PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("rotation", kf0, kf1, kf2);
		final ObjectAnimator changeOut = ObjectAnimator.ofPropertyValuesHolder(this, pvhLeft, pvhTop, pvhRight,
				pvhBottom, pvhRotation).setDuration(mTransitioner.getDuration(LayoutTransition.CHANGE_DISAPPEARING));
		mTransitioner.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, changeOut);
		changeOut.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setRotation(0f);
			}
		});

		// APPEARING
		ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "rotationY", 90f, 0f).setDuration(
				mTransitioner.getDuration(LayoutTransition.APPEARING));
		mTransitioner.setAnimator(LayoutTransition.APPEARING, animIn);
		animIn.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setRotationY(0f);
			}
		});

		// DISAPPEARING
		ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "rotationX", 0f, 90f).setDuration(
				mTransitioner.getDuration(LayoutTransition.DISAPPEARING));
		mTransitioner.setAnimator(LayoutTransition.DISAPPEARING, animOut);
		animOut.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setRotationX(0f);
			}
		});

	}
}

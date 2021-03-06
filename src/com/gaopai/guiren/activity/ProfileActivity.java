package com.gaopai.guiren.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatMessageActivity;
import com.gaopai.guiren.bean.TagBean;
import com.gaopai.guiren.bean.TagResultBean;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.User.CommentBean;
import com.gaopai.guiren.bean.User.CommentContent;
import com.gaopai.guiren.bean.User.PrivacyConfig;
import com.gaopai.guiren.bean.User.SpreadBean;
import com.gaopai.guiren.bean.User.ZanBean;
import com.gaopai.guiren.bean.UserInfoBean;
import com.gaopai.guiren.bean.dynamic.ConnectionBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.bean.net.TagResult;
import com.gaopai.guiren.support.CameralHelper;
import com.gaopai.guiren.support.CameralHelper.Option;
import com.gaopai.guiren.support.DynamicHelper;
import com.gaopai.guiren.support.DynamicHelper.DyCallback;
import com.gaopai.guiren.support.DynamicHelper.DySoftCallback;
import com.gaopai.guiren.support.ImageCrop;
import com.gaopai.guiren.support.TagWindowManager;
import com.gaopai.guiren.support.TagWindowManager.TagCallback;
import com.gaopai.guiren.support.comment.CommentProfile;
import com.gaopai.guiren.support.view.AgreeAnimWindow;
import com.gaopai.guiren.support.view.HeadView;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.FlowLayout;
import com.gaopai.guiren.view.LineRelativeLayout;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends BaseActivity implements OnClickListener {

	private User tUser;
	private User mUser;
	public final static String KEY_UID = "user_id";

	private String tuid;
	private boolean isSelf = false;

	private TextView tvUserName;
	private TextView tvUserInfo;

	private TextView tvFollowersCount;
	private TextView tvFansCount;
	private TextView tvMeetingsCount;
	private TextView tvTribesCount;

	private TextView tvRealName;
	private TextView tvCompany;
	private TextView tvIndustry;
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
	private List<TagBean> tagList = new ArrayList<TagBean>();
	private List<TagBean> recTagList = new ArrayList<TagBean>();

	private View layoutBasicProfile;
	private View layoutDyProfile;
	private ViewGroup layoutDyContent;
	private View layoutBottom;
	private View layoutConnection;

	private TextView tvDyMonthYear;
	private TextView tvDyDay;

	private TextView tvDyViewMore;

	private TextView tvBottomFavorite;
	private TextView tvBottomSpread;
	private LinearLayout layoutBottomComment;

	private LineRelativeLayout layoutZanHolder;
	private LineRelativeLayout layoutSpreadHolder;
	private LineRelativeLayout layoutCommentHolder;
	private TextView tvFollowBottom;

	private HeadView layoutHeader;

	private ImageView ivFollow;
	private TagWindowManager tagWindowManager;
	private boolean isShowAllTags = false;

	private DynamicHelper dynamicHelper;
	private CameralHelper cameralHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar(false);
		setAbContentView(R.layout.activity_profile);

		tuid = getIntent().getStringExtra(KEY_UID);
		if (TextUtils.isEmpty(tuid)) {
			Uri data = getIntent().getData();
			tuid = data.toString().substring(data.toString().indexOf("//") + 2);
		}
		if (TextUtils.isEmpty(tuid)) {
			this.finish();
			return;
		}
		mUser = DamiCommon.getLoginResult(this);
		isSelf = mUser.uid.equals(tuid);

		initComponent();
		hideSomeViewsBasedOnUser();

		dynamicHelper = new DynamicHelper(mContext, DynamicHelper.DY_PROFILE);
		dynamicHelper.setCallback(dynamicCallback);
		cameralHelper = new CameralHelper(this);
		cameralHelper.setCallback(callback);
		cameralHelper.setOption(new Option(1, true, ImageCrop.HEADER_WIDTH, ImageCrop.HEADER_HEIGHT));
		tagWindowManager = new TagWindowManager(this, isSelf, tagCallback);
		if (savedInstanceState != null) {
			cameralHelper.retriveUri((Uri) savedInstanceState.getParcelable("uri"));
			cameralHelper.retriveCropPath(savedInstanceState.getString("cropPath"));
			tUser = (User) savedInstanceState.getSerializable("tUser");
			recTagList = (List<TagBean>) savedInstanceState.getSerializable("recTagList");
			cameralHelper.retriveTempPicName(savedInstanceState.getString("tempPic"));
			onGetUserDataSuccess();
		} else {
			addLoadingView();
			showLoadingView();
			getUserInfo();
			getRecTags();
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("uri", cameralHelper.getUri());
		outState.putString("cropPath", cameralHelper.getCropPath());
		outState.putSerializable("tUser", tUser);
		outState.putString("tempPic", cameralHelper.getTempPicName());
		outState.putSerializable("recTagList", (Serializable) recTagList);

	}

	@Override
	protected void registerReceiver(IntentFilter intentFilter) {
		super.registerReceiver(intentFilter);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(MainActivity.ACTION_UPDATE_PROFILE);
	}

	@Override
	protected void onReceive(Intent intent) {
		// TODO Auto-generated method stub
		User user = DamiCommon.getLoginResult(this);
		Logger.d(this, "onReceive   user=null" + (user == null));
		if (user == null) {
			return;
		}
		if (intent != null) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				dynamicHelper.stopPlayVoice();
			} else if (action.equals(MainActivity.ACTION_UPDATE_PROFILE)) {
				if (!isSelf) {
					return;
				}
				tUser = DamiCommon.getLoginResult(this);
				bindProfileView();
			}
		}
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

	public static Intent getIntent(Context context, String uid) {
		Intent intent = new Intent(context, ProfileActivity.class);
		intent.putExtra(KEY_UID, uid);
		return intent;
	}

	private void getUserInfo() {
		DamiInfo.getUserInfo(tuid, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final UserInfoBean data = (UserInfoBean) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null) {
						showContent();
						updateUserInfo(data.data);
						tUser = data.data;
						onGetUserDataSuccess();
					}
				} else {
					showErrorView();
					otherCondition(data.state, ProfileActivity.this);
				}
			}

			@Override
			public void onFailure(Object o) {
				showErrorView();
			}
		});
	}

	private void onGetUserDataSuccess() {
		tUser.realname = User.getUserName(tUser);
		tagWindowManager.setTagList(tUser.tag);
		bindView();
	}

	private void updateUserInfo(User user) {
		if (isSelf) {
			DamiCommon.saveLoginResult(mContext, user);
			sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
		}
	}

	private void showErrorView() {
		showErrorView(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getUserInfo();
				showLoadingView();
			}
		});
	}

	private TagWindowManager.TagCallback tagCallback = new TagCallback() {

		@Override
		public void onSave(String tags) {
			// TODO Auto-generated method stub
			addRemoteTags(tags);
		}
	};

	private void initComponent() {
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);

		layoutHeader = ViewUtil.findViewById(this, R.id.layout_header_mvp);
		if (isSelf) {
			mTitleBar.setTitleText(getString(R.string.my_profile));
		} else {
			mTitleBar.setTitleText(getString(R.string.profile_other));
		}
		layoutHeader.setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.iv_profile_erweima).setOnClickListener(this);

		tvUserInfo = (TextView) findViewById(R.id.tv_user_info);
		tvUserName = (TextView) findViewById(R.id.tv_user_name);

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
		tvIndustry = (TextView) findViewById(R.id.tv_profile_partment);
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

		layoutBasicProfile = findViewById(R.id.layout_basic_profile);
		layoutDyProfile = findViewById(R.id.layout_dynamic_profile);
		layoutBottom = findViewById(R.id.bottom_bar);
		layoutConnection = findViewById(R.id.layout_profile_connection);

		tvDyMonthYear = (TextView) findViewById(R.id.tv_profile_dy_monthyear);
		tvDyDay = (TextView) findViewById(R.id.tv_profile_dy_day);
		tvDyViewMore = (TextView) findViewById(R.id.tv_profile_dy_more);
		tvDyViewMore.setOnClickListener(this);
		layoutDyContent = ViewUtil.findViewById(this, R.id.layout_profile_dy_content);

		tvBottomFavorite = (TextView) findViewById(R.id.tv_bottom_favorite);
		tvBottomFavorite.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
		tvBottomSpread = (TextView) findViewById(R.id.tv_bottom_spread);
		tvBottomSpread.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
		layoutBottomComment = (LinearLayout) findViewById(R.id.layout_bottom_comment);

		layoutZanHolder = (LineRelativeLayout) findViewById(R.id.layout_zan_holder);
		layoutCommentHolder = (LineRelativeLayout) findViewById(R.id.layout_comment_holder);
		layoutSpreadHolder = (LineRelativeLayout) findViewById(R.id.layout_spread_holder);

		tvFollowBottom = ViewUtil.findViewById(this, R.id.tv_profile_bottom_follow);
		ivFollow = ViewUtil.findViewById(this, R.id.iv_profile_follow);
	}

	private void hideSomeViewsBasedOnUser() {
		if (isSelf) {
			layoutBasicProfile.setVisibility(View.VISIBLE);
			layoutBottom.setVisibility(View.GONE);
			layoutDyProfile.setVisibility(View.GONE);
			layoutConnection.setVisibility(View.GONE);
		} else {
			layoutBasicProfile.setVisibility(View.GONE);
			layoutDyProfile.setVisibility(View.VISIBLE);
			layoutBottom.setVisibility(View.VISIBLE);
			layoutConnection.setVisibility(View.VISIBLE);
		}
	}

	private void bindView() {
		if (isSelf) {
			bindProfileView();
		} else {
			bindConnectionView();
		}
		bindTopSectionView();
		bindContactView();
		bindDyView();
		if (tUser.tag != null) {
			tagList = tUser.tag;
		}
		bindUserTags();
		bindBottomDynamicView();
		bindBottomView();
	}

	private void bindTopSectionView() {
		bindHeadView();
		bindUserName();
		tvUserInfo.setText(User.getUserInfo(tUser));
	}

	private void bindHeadView() {
		layoutHeader.setImage(tUser.headsmall);
		if (tUser.bigv == 1) {
			layoutHeader.setMVP(true);
		} else {
			layoutHeader.setMVP(false);
		}
	}

	private void bindUserName() {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		SpannableString name;
		if (tUser.bigv == 1) {
			name = new SpannableString(User.getUserName(tUser) + HeadView.MVP_NAME_STR);
			HeadView.getMvpName(mContext, name);
		} else {
			name = new SpannableString(User.getUserName(tUser));
		}
		MyTextUtils.setTextSize(name, 22);
		MyTextUtils.setTextColor(name, getResources().getColor(R.color.general_text_black));

		SpannableString meiliInfo = new SpannableString("    " + getString(R.string.level_count));
		MyTextUtils.setTextSize(meiliInfo, 16);
		MyTextUtils.setTextColor(meiliInfo, getResources().getColor(R.color.general_text_black));

		SpannableString integra = new SpannableString(String.valueOf(tUser.integral));
		MyTextUtils.setTextSize(integra, 18);
		MyTextUtils.setTextColor(integra, getResources().getColor(R.color.red_dongtai_bg));

		tvUserName.setText(builder.append(name).append(meiliInfo).append(integra));
	}

	private void bindUserTags() {
		// if (tagList != null && tagList.size() > 0) {
		if (tagList != null) {
			if (tagList.size() == 0) {
				bindEmptyTagView();
				return;
			}
			if (tagList.size() > 10 && (!isShowAllTags)) {
				if (isSelf) {
					tagWindowManager.bindTags(tagLayout, false, tagList.subList(0, 9), null);
				} else {
					tagWindowManager.bindTags(tagLayout, false, tagList.subList(0, 9), zanClickListener);
				}
				tvRevealAllTags.setVisibility(View.VISIBLE);
				return;
			}
			if (isSelf) {
				tagWindowManager.bindTags(tagLayout, false, tagList, null);
			} else {
				tagWindowManager.bindTags(tagLayout, false, tagList, zanClickListener);
			}
			tvRevealAllTags.setVisibility(View.GONE);
		} else {
			bindEmptyTagView();
		}
	}

	private void bindEmptyTagView() {
		tagLayout.removeAllViews();
		TextView textView = new TextView(mContext);
		textView.setText(getString(R.string.no_tags));
		tagLayout.addView(textView, tagLayout.getTextLayoutParams());
	}

	private OnClickListener zanClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final TagBean tagBean = (TagBean) v.getTag();
			AgreeAnimWindow.showAnim(v);
			DamiInfo.zanUserTag(mUser.uid, tagBean.id, new SimpleResponseListener(mContext) {
				@Override
				public void onSuccess(Object o) {
					BaseNetBean data = (BaseNetBean) o;
					if (data.state != null && data.state.code == 0) {
						showToast(R.string.zan_success);
						updateZantagList(tagBean.tag);
						bindBottomDynamicView();
					} else {
						otherCondition(data.state, ProfileActivity.this);
					}
				}
			});
		}
	};

	private ZanBean getZanBean() {
		for (ZanBean zanBean : tUser.zantaglist) {
			if (zanBean.uid.equals(mUser.uid)) {
				return zanBean;
			}
		}
		ZanBean zanBean = new ZanBean();
		zanBean.realname = mUser.realname;
		zanBean.uid = mUser.uid;
		zanBean.zantag = "";
		if (tUser.zantaglist == null) {
			tUser.zantaglist = new ArrayList<ZanBean>();
		}
		tUser.zantaglist.add(zanBean);
		return zanBean;
	}

	private void updateZantagList(String tag) {
		ZanBean zanBean = getZanBean();
		zanBean.zantag = zanBean.zantag + "[" + tag + "]";
	}

	private void bindConnectionView() {
		bindUserName();
		tvFollowersCount.setText(String.valueOf(tUser.followers));
		tvFansCount.setText(String.valueOf(tUser.fansers));
		tvMeetingsCount.setText(String.valueOf(tUser.meetingCount));
		tvTribesCount.setText(String.valueOf(tUser.tribeCount));
	}

	private void bindProfileView() {
		tvRealName.setText(tUser.realname);
		tvCompany.setText(tUser.company);
		tvJob.setText(tUser.post);
		tvIndustry.setText(tUser.depa);
	}

	private void bindContactView() {
		tvEmail.setText(tUser.email);
		tvPhone.setText(tUser.phone);
		tvWeixin.setText(tUser.weixin);
		tvWeibo.setText(tUser.weibo);
		restoreTextDrawable(tvEmail);
		restoreTextDrawable(tvPhone);
		restoreTextDrawable(tvWeixin);
		restoreTextDrawable(tvWeibo);

		if (isSelf) {
			return;
		}

		if (!isSelf && isFollowEachOther()) {
			if (TextUtils.isEmpty(mUser.email)) {
				tvEmail.setText(R.string.no_right_see_email);
				removeTextDrawableWithClick(tvEmail);
			}
			if (TextUtils.isEmpty(mUser.phone)) {
				tvPhone.setText(R.string.no_right_see_phone);
				removeTextDrawableWithClick(tvPhone);
			}
			if (TextUtils.isEmpty(mUser.weibo)) {
				tvWeibo.setText(R.string.no_right_see_weibo);
				removeTextDrawableWithClick(tvWeibo);
			}
			if (TextUtils.isEmpty(mUser.weixin)) {
				tvWeixin.setText(R.string.no_right_see_weixin);
				removeTextDrawableWithClick(tvWeixin);
			}
			return;
		}

		PrivacyConfig pc = tUser.privacyconfig;
		if (pc.mail == 0) {
			tvEmail.setText(R.string.you_are_not_allowed_to_see_profile);
			removeTextDrawable(tvEmail);
		} else if (TextUtils.isEmpty(mUser.email)) {
			tvEmail.setText(R.string.no_right_see_email);
			removeTextDrawableWithClick(tvEmail);
		}

		if (pc.phone == 0) {
			tvPhone.setText(R.string.you_are_not_allowed_to_see_profile);
			removeTextDrawable(tvPhone);
		} else if (TextUtils.isEmpty(mUser.phone)) {
			tvPhone.setText(R.string.no_right_see_phone);
			removeTextDrawableWithClick(tvPhone);
		}

		if (pc.wechat == 0) {
			tvWeixin.setText(R.string.you_are_not_allowed_to_see_profile);
			removeTextDrawable(tvWeixin);
		} else if (TextUtils.isEmpty(mUser.weixin)) {
			tvWeixin.setText(R.string.no_right_see_weixin);
			removeTextDrawableWithClick(tvWeixin);
		}

		if (pc.weibo == 0) {
			tvWeibo.setText(R.string.you_are_not_allowed_to_see_profile);
			removeTextDrawable(tvWeibo);
		} else if (TextUtils.isEmpty(mUser.weibo)) {
			tvWeibo.setText(R.string.no_right_see_weibo);
			removeTextDrawableWithClick(tvWeibo);
		}
	}

	private void bindBottomView() {
		if (tUser.isfollow == 0 || tUser.isfollow == 2) {
			ivFollow.setImageResource(R.drawable.icon_profile_follow_normal);
			tvFollowBottom.setText("加关注");
		} else {
			ivFollow.setImageResource(R.drawable.icon_profile_follow_active);
			tvFollowBottom.setText("取消关注");
		}
	}

	private void removeTextDrawable(TextView textView) {
		textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		textView.setCompoundDrawablePadding(0);
		((ViewGroup) textView.getParent()).setOnClickListener(null);
	}

	private void restoreTextDrawable(TextView textView) {
		textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_right_arrow, 0);
		textView.setCompoundDrawablePadding(0);
	}

	private void removeTextDrawableWithClick(TextView textView) {
		textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		textView.setCompoundDrawablePadding(0);
	}

	private void bindBottomDynamicView() {
		// TODO Auto-generated method stub
		boolean isZan = false, isSpread = false, isComment = false;
		layoutZanHolder.setLineHalfOnly(false);
		layoutSpreadHolder.setLineHalfOnly(false);
		layoutCommentHolder.setLineHalf(true);
		if (tUser.zantaglist != null && tUser.zantaglist.size() > 0) {
			isZan = true;
			layoutZanHolder.setVisibility(View.VISIBLE);
			List<ConnectionBean.User> userList = new ArrayList<ConnectionBean.User>();
			for (ZanBean bean : tUser.zantaglist) {
				ConnectionBean.User user = new ConnectionBean.User();
				user.uid = bean.uid;
				user.realname = bean.realname;
				userList.add(user);
			}
			tvBottomFavorite.setText(getZanTagSpan(tUser.zantaglist));
			// tvBottomFavorite.setText(MyTextUtils.addConnectionUserList(userList,
			// "赞过"));
		} else {
			layoutZanHolder.setVisibility(View.GONE);
		}

		if (tUser.kuosanlist != null && tUser.kuosanlist.size() > 0) {
			isSpread = true;
			layoutSpreadHolder.setVisibility(View.VISIBLE);
			List<ConnectionBean.User> userList = new ArrayList<ConnectionBean.User>();
			Set<String> set = new HashSet<String>();
			for (SpreadBean bean : tUser.kuosanlist) {
				// if (set.contains(bean.uid)) {
				// continue;
				// }
				// set.add(bean.uid);
				ConnectionBean.User user = new ConnectionBean.User();
				user.uid = bean.uid;
				user.realname = bean.realname;
				userList.add(user);
			}
			tvBottomSpread.setText(MyTextUtils.getSpannableString(MyTextUtils.addUserSpans(userList), "扩散过"));
		} else {
			layoutSpreadHolder.setVisibility(View.GONE);
		}

		isComment = bindCommentView();

		if (!isComment && isSpread) {
			layoutSpreadHolder.setLineHalf(true);
		}
		if (!isComment && !isSpread && isZan) {
			layoutZanHolder.setLineHalf(true);
		}
	}

	private Spannable getZanTagSpan(List<ZanBean> zanList) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		int gray = getResources().getColor(R.color.general_text_gray);
		for (ZanBean zanBean : zanList) {
			builder.append(MyTextUtils.addSingleUserSpan(zanBean.realname, zanBean.uid));
			builder.append(MyTextUtils.setTextColor("认可了他的", gray));
			builder.append(MyTextUtils.setTextColor(zanBean.zantag, gray));
			builder.append(MyTextUtils.setTextColor("标签\n\n", gray));
		}
		builder.delete(builder.length() - 2, builder.length());
		return builder;
	}

	private View dyView;

	private void bindDyView() {
		TypeHolder bean = tUser.newdyna;
		if (bean == null || bean.jsoncontent == null) {
			layoutDyProfile.setVisibility(View.GONE);
			return;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(bean.time * 1000);

		tvDyDay.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
		tvDyMonthYear.setText(calendar.get(Calendar.YEAR) + "." + (calendar.get(Calendar.MONTH) + 1));
		layoutDyContent.addView(parseDyView(bean, dynamicHelper.getView(dyView, bean)));
	}

	private View parseDyView(TypeHolder typeHolder, View source) {
		View view = source.findViewById(R.id.tv_spread_words);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
		return source;
	}

	private boolean bindCommentView() {
		layoutBottomComment.removeAllViews();
		if (tUser.commentlist != null && tUser.commentlist.size() > 0) {
			layoutCommentHolder.setVisibility(View.VISIBLE);
			for (final CommentBean bean : tUser.commentlist) {
				View view = mInflater.inflate(R.layout.item_general_small_head, null);
				TextView nameView = (TextView) view.findViewById(R.id.tv_title);
				TextView infoView = (TextView) view.findViewById(R.id.tv_info);
				TextView dateView = (TextView) view.findViewById(R.id.tv_date);

				ImageView headerView = (ImageView) view.findViewById(R.id.iv_header);
				nameView.setText(bean.uname);
				if (bean.content != null && bean.content.content != null) {
					infoView.setText(bean.content.content);
				}
				dateView.setText(FeatureFunction.getGeneralTime(bean.addtime * 1000));
				if (!TextUtils.isEmpty(bean.s_path)) {
					Picasso.with(mContext).load(bean.s_path).placeholder(R.drawable.default_header)
							.error(R.drawable.default_header).into(headerView);
				}
				TextView tvDelete = (TextView) view.findViewById(R.id.tv_delete);
				if (isSelf) {
					tvDelete.setVisibility(View.VISIBLE);
					tvDelete.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showDialog(getString(R.string.confirm_delete), "", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									DamiInfo.delComment(bean.id, new SimpleResponseListener(mContext,
											R.string.request_internet_now) {
										@Override
										public void onSuccess(Object o) {
											BaseNetBean data = (BaseNetBean) o;
											if (data.state != null && data.state.code == 0) {
												int len = tUser.commentlist.size();
												for (int i = 0; i < len; i++) {
													CommentBean commentBean = tUser.commentlist.get(i);
													if (commentBean.id.equals(bean.id)) {
														tUser.commentlist.remove(i);
														break;
													}
												}
												bindBottomDynamicView();
											} else {
												otherCondition(data.state, ProfileActivity.this);
											}
										}
									});
								}
							});
						}
					});
				} else {
					tvDelete.setVisibility(View.GONE);
				}
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(ProfileActivity.getIntent(mContext, bean.uid));

					}
				});

				layoutBottomComment.addView(view);
				View lineView = new View(mContext);
				lineView.setBackgroundColor(getResources().getColor(R.color.general_horizon_divider));
				layoutBottomComment.addView(lineView, new LayoutParams(LayoutParams.MATCH_PARENT, 1));
			}
			layoutBottomComment.removeViewAt(layoutBottomComment.getChildCount() - 1);

			return true;
		}
		layoutCommentHolder.setVisibility(View.GONE);
		return false;
	}

	public final static int REQUEST_CHANGE_PROFILE = 0;
	public final static int REQUEST_CHANGE_PROFILE_IN_OTHER_INTERFACE = 3;
	public final static int REQUEST_COMMENT = 1;
	public final static int REQUEST_VERIFY_PROFILE = 2;
	public final static int REQUEST_BIND_PHONE = 4;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_my_fans_count:
		case R.id.tv_my_meetings_count:
		case R.id.tv_my_tribes_count:
		case R.id.tv_my_followers_count:
			if ((!isBeenFollowed()) && (tUser.privacyconfig.renmai == 0)) {
				showToast(R.string.you_are_not_allowed_to_see_profile);
				return;
			}
		}

		PrivacyConfig pc = tUser.privacyconfig;
		switch (v.getId()) {
		case R.id.ab_setting: {
			startActivity(SettingActivity.class);
			break;
		}
		case R.id.tv_my_fans_count: {
			startActivity(ContactActivity.getIntent(mContext, ContactActivity.TYPE_FANS, tUser.uid));
			break;
		}
		case R.id.tv_my_meetings_count: {
			startActivity(MyMeetingActivity.getIntent(mContext, tuid));
			break;
		}
		case R.id.tv_my_tribes_count: {
			startActivity(TribeActivity.getIntent(mContext, tuid));
			break;
		}
		case R.id.tv_my_followers_count: {
			startActivity(ContactActivity.getIntent(mContext, ContactActivity.TYPE_FOLLOWERS, tUser.uid));
			break;
		}
		case R.id.tv_reverification:
			startActivityForResult(ReverificationActivity.class, REQUEST_VERIFY_PROFILE);
			break;
		case R.id.layout_profile_email:
			if (isSelf) {
				changeContact(ChangeProfileActivity.TYPE_EMAIL, tUser.email);
				return;
			}
			if (isFollowEachOther()) {
				if (TextUtils.isEmpty(mUser.email)) {// edit your suck profile
					changeContact(ChangeProfileActivity.TYPE_EMAIL, mUser.email,
							REQUEST_CHANGE_PROFILE_IN_OTHER_INTERFACE);
				} else {
					sendEmail(tUser.email);
				}
			} else {
				if (pc.mail == 0) {
					return;
				}
				if (TextUtils.isEmpty(mUser.email)) {
					changeContact(ChangeProfileActivity.TYPE_EMAIL, mUser.email,
							REQUEST_CHANGE_PROFILE_IN_OTHER_INTERFACE);
					return;
				}
				sendEmail(tUser.email);
			}
			break;
		case R.id.layout_profile_phone_num:
			if (isSelf) {
				// changeContact(ChangeProfileActivity.TYPE_PHONE, tUser.phone);
				bindMyPhone();
				return;
			}
			if (isFollowEachOther()) {
				if (TextUtils.isEmpty(mUser.phone)) {// edit your suck profile
					// changeContact(ChangeProfileActivity.TYPE_PHONE,
					// mUser.phone,
					// REQUEST_CHANGE_PROFILE_IN_OTHER_INTERFACE);
					bindMyPhone();
				} else {
					MyUtils.makePhonecall(mContext, tUser.phone);
				}
			} else {
				if (pc.phone == 0) {
					return;
				}
				if (TextUtils.isEmpty(mUser.phone)) {
					// changeContact(ChangeProfileActivity.TYPE_PHONE,
					// mUser.phone,
					// REQUEST_CHANGE_PROFILE_IN_OTHER_INTERFACE);
					bindMyPhone();
					return;
				}
				MyUtils.makePhonecall(mContext, tUser.phone);
			}
			break;
		case R.id.layout_profile_weibo_num:
			if (isSelf) {
				changeContact(ChangeProfileActivity.TYPE_WEIBO, tUser.weibo);
				return;
			}
			if (isFollowEachOther()) {
				if (TextUtils.isEmpty(mUser.weibo)) {// edit your suck profile
					changeContact(ChangeProfileActivity.TYPE_WEIBO, mUser.weibo,
							REQUEST_CHANGE_PROFILE_IN_OTHER_INTERFACE);
				} else {
					openWeibo();
				}
			} else {
				if (pc.weibo == 0) {
					return;
				}
				if (TextUtils.isEmpty(mUser.weibo)) {// edit your suck profile
					changeContact(ChangeProfileActivity.TYPE_WEIBO, mUser.weibo,
							REQUEST_CHANGE_PROFILE_IN_OTHER_INTERFACE);
					return;
				}
				openWeibo();
			}
			break;
		case R.id.layout_profile_weixin_num:

			if (isSelf) {
				changeContact(ChangeProfileActivity.TYPE_WEIXIN, tUser.weixin);
				return;
			}
			if (isFollowEachOther()) {
				if (TextUtils.isEmpty(mUser.weixin)) {// edit your suck profile
					changeContact(ChangeProfileActivity.TYPE_WEIXIN, mUser.weixin,
							REQUEST_CHANGE_PROFILE_IN_OTHER_INTERFACE);
				} else {
					openWeixin();
				}
			} else {
				if (pc.wechat == 0) {
					return;
				}
				if (TextUtils.isEmpty(mUser.weixin)) {// edit your suck profile
					changeContact(ChangeProfileActivity.TYPE_WEIXIN, mUser.weixin,
							REQUEST_CHANGE_PROFILE_IN_OTHER_INTERFACE);
					return;
				}
				openWeixin();
			}
			break;
		case R.id.tv_reveal_all_tags:
			isShowAllTags = true;
			bindUserTags();
			break;
		case R.id.tv_add_tags:
			tagWindowManager.showTagsWindow();
			break;
		case R.id.layout_profile_bottom_comment: {
			Intent intent = new Intent();
			intent.putExtra(CommentProfile.KEY_USER, tUser);
			intent.putExtra(CommentGeneralActivity.KEY_TYPE, CommentGeneralActivity.TYPE_COMMENT_PROFILE);
			intent.setClass(mContext, CommentGeneralActivity.class);
			startActivityForResult(intent, REQUEST_COMMENT);
			break;
		}
		case R.id.layout_profile_bottom_follow:
			if (isFollow()) {
				if (tUser.uid.equals("1")) {
					showToast(R.string.can_not_unfollow_guiren_secreaty);
					return;
				}
				showCancelFollowDialog();
				return;
			}
			followUser();
			break;
		case R.id.layout_profile_bottom_msg:
			startActivityWithUser(ChatMessageActivity.KEY_USER, ChatMessageActivity.class);
			break;
		case R.id.layout_profile_bottom_spread:
			spreadUser();
			break;
		case R.id.iv_profile_erweima: {
			startActivity(TwoDimensionActivity.getIntent(mContext, tUser));
			break;
		}
		case R.id.tv_profile_dy_more:
			startActivity(MyDynamicActivity.getIntent(mContext, tuid));
			break;
		case R.id.layout_header_mvp:
			if (isSelf) {
				changeHeadImg();
			} else {
				mContext.startActivity(ShowImagesActivity.getIntent(mContext, tUser.headsmall, tUser.headlarge));
			}
			break;
		default:
			break;
		}
	}

	private void sendEmail(final String email) {
		showDialog("确定发送邮件？", null, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent data = new Intent(Intent.ACTION_SENDTO);
				data.setData(Uri.parse("mailto:" + email));
				try {
					startActivity(data);
				} catch (Exception e) {
				}
			}
		});

	}

	private void openWeixin() {
		showDialog("确定打开微信？", null, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
				intent.setAction(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setComponent(cmp);
				try {
					startActivityForResult(intent, 0);
				} catch (Exception e) {
					showToast("请安装微信");
				}
			}
		});

	}

	private void openWeibo() {
		showDialog("确定打开微博？", null, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!TextUtils.isEmpty(tUser.weibo)) {
					String url = "http://m.weibo.cn/n/" + tUser.weibo;
					Intent intent = new Intent(ProfileActivity.this, WebActivity.class);
					intent.putExtra("url", url);
					intent.putExtra("type", 2);
					startActivity(intent);
				}
			}
		});
	}

	private void followUser() {
		DamiInfo.follow(tUser.uid, new SimpleResponseListener(mContext, R.string.request_internet_now) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					if (data.state.msg.equals(getString(R.string.cancel_follow_success))) {
						showToast(R.string.cancel_follow_success);
						if (tUser.isfollow == 3) {
							tUser.isfollow = 2;
						}
						if (tUser.isfollow == 1) {
							tUser.isfollow = 0;
						}
						mUser.followers = mUser.followers - 1;
						sendBroadcast(ContactActivity.getDeleteBroadcastIntent(tuid));
					} else {
						showToast(R.string.follow_success);
						if (tUser.isfollow == 2) {
							tUser.isfollow = 3;
						}
						if (tUser.isfollow == 0) {
							tUser.isfollow = 1;
						}
						mUser.followers = mUser.followers + 1;
						sendBroadcast(ContactActivity.getAddBroadcastIntent(tUser));
					}
					DamiCommon.saveLoginResult(mContext, mUser);

					bindBottomView();
					bindContactView();
				} else {
					otherCondition(data.state, ProfileActivity.this);
				}
			}
		});
	}

	private boolean isFollow() {
		return (tUser.isfollow == 1 || tUser.isfollow == 3);
	}

	private boolean isBeenFollowed() {
		return (tUser.isfollow == 2 || tUser.isfollow == 3);
	}

	private boolean isFollowEachOther() {
		return tUser.isfollow == 3;
	}

	private void spreadUser() {
		startActivity(SpreadDynamicActivity.getUserIntent(mContext, tUser));
	}

	private void startActivityWithUser(String key, Class clazz) {
		Intent intent = new Intent();
		intent.putExtra(key, tUser);
		intent.setClass(mContext, clazz);
		startActivity(intent);
	}

	private void bindMyPhone() {
		startActivityForResult(RegisterActivity.getIntent(mContext, RegisterActivity.TYPE_RE_BIND_PHONE, mUser),
				REQUEST_BIND_PHONE);
	}

	private void changeContact(int type, String text) {
		changeContact(type, text, REQUEST_CHANGE_PROFILE);
	}

	private void changeContact(int type, String text, int request) {
		if (TextUtils.isEmpty(text)) {
			text = "";
		}
		Intent intent = new Intent(mContext, ChangeProfileActivity.class);
		intent.putExtra(ChangeProfileActivity.KEY_TEXT, text);
		intent.putExtra(ChangeProfileActivity.KEY_TYPE, type);
		startActivityForResult(intent, request);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub

		if (resultCode == RESULT_OK) {
			cameralHelper.onActivityResult(requestCode, resultCode, intent);
			if (requestCode == REQUEST_CHANGE_PROFILE) {
				tUser = DamiCommon.getLoginResult(this);
				Logger.d(this, "onActivityResult   user=null" + (tUser == null));
				tvEmail.setText(tUser.email);
				tvPhone.setText(tUser.phone);
				tvWeixin.setText(tUser.weixin);
				tvWeibo.setText(tUser.weibo);
			}
			if (requestCode == REQUEST_CHANGE_PROFILE_IN_OTHER_INTERFACE) {
				mUser = DamiCommon.getLoginResult(mContext);
				// getUserInfo();
				bindContactView();
			}

			if (requestCode == REQUEST_COMMENT) {
				String comment = intent.getStringExtra(CommentProfile.KEY_CONTENT);
				if (TextUtils.isEmpty(comment)) {
					return;
				}
				CommentBean bean = new CommentBean();
				bean.content = new CommentContent();
				bean.content.content = comment;
				bean.uid = mUser.uid;
				bean.uname = mUser.realname;
				bean.s_path = mUser.headsmall;
				bean.addtime = System.currentTimeMillis() / 1000;
				if (tUser.commentlist == null) {
					tUser.commentlist = new ArrayList<CommentBean>();
				}
				tUser.commentlist.add(bean);
				bindBottomDynamicView();
			}

			if (requestCode == REQUEST_VERIFY_PROFILE) {
				tUser = DamiCommon.getLoginResult(this);
				bindProfileView();
				bindContactView();
				bindTopSectionView();
			}

			if (requestCode == REQUEST_BIND_PHONE) {
				tUser = DamiCommon.getLoginResult(this);
				bindContactView();
			}

		}
	}

	private void getRecTags() {
		DamiInfo.getTags("user", new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				TagResult data = (TagResult) o;
				if (data.state != null && data.state.code == 0) {
					recTagList = data.data;
					tagWindowManager.setRecTagList(recTagList);
				} else {
					this.otherCondition(data.state, ProfileActivity.this);
				}
			}
		});
	}

	// store tags in server
	private void addRemoteTags(String tags) {
		Logger.d(this, tags);
		DamiInfo.updateUserTag(tuid, tags, new SimpleResponseListener(mContext, R.string.request_internet_now) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				TagResultBean data = (TagResultBean) o;
				if (data.state != null && data.state.code == 0) {
					tagList.clear();
					if (data.data != null && data.data.size() > 0) {
						tagList.addAll(data.data);
					}
					bindUserTags();
					showToast(R.string.add_tags_success);
				} else {
					otherCondition(data.state, ProfileActivity.this);
				}
			}
		});
	}

	private void showCancelFollowDialog() {
		showDialog(getString(R.string.confirm_cancel_follow), null, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				followUser();
			}
		});
	}

	private void changeHeadImg() {
		headerImage = "";

		cameralHelper.showDefaultSelectDialog(getString(R.string.set_header));
	}

	private String headerImage;
	private CameralHelper.GetImageCallback callback = new CameralHelper.SimpleCallback() {
		@Override
		public void receiveCropPic(String path) {
			Logger.d(this, "pic=" + path);
			DamiInfo.editHeader(path, new SimpleResponseListener(mContext, R.string.upload_header_now) {
				@Override
				public void onSuccess(Object o) {
					UserInfoBean data = (UserInfoBean) o;
					if (data.state != null && data.state.code == 0) {
						tUser = data.data;
						bindHeadView();
						DamiCommon.saveLoginResult(mContext, tUser);
						sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
						showToast(R.string.upload_header_success);
					}
				}
			});
		}
	};

	@Override
	protected void onStop() {
		super.onStop();
		dynamicHelper.stopPlayVoice();
	}
}

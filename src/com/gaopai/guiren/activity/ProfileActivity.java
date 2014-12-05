package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.gaopai.guiren.bean.User.PrivacyConfig;
import com.gaopai.guiren.bean.User.SpreadBean;
import com.gaopai.guiren.bean.User.ZanBean;
import com.gaopai.guiren.bean.UserInfoBean;
import com.gaopai.guiren.bean.dynamic.ConnectionBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.bean.dynamic.NewDynamicBean.JsonContent;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.bean.net.TagResult;
import com.gaopai.guiren.support.DynamicHelper;
import com.gaopai.guiren.support.TagWindowManager;
import com.gaopai.guiren.support.TagWindowManager.TagCallback;
import com.gaopai.guiren.support.comment.CommentProfile;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyTextUtils;
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
	private List<TagBean> tagList = new ArrayList<TagBean>();
	private List<TagBean> recTagList = new ArrayList<TagBean>();

	private View layoutBasicProfile;
	private View layoutDyProfile;
	private ViewGroup layoutDyContent;
	private View layoutBottom;
	private View layoutConnection;

	private TextView tvDyFavoriteCount;
	private TextView tvDySpreadCount;
	private TextView tvDyCommentCount;

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

	private ImageView ivFollow;
	private TagWindowManager tagWindowManager;
	private boolean isShowAllTags = false;

	private DynamicHelper dynamicHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_profile);
		tuid = getIntent().getStringExtra(KEY_UID);
		if (TextUtils.isEmpty(tuid)) {
			Uri data = getIntent().getData();
			tuid = data.toString().substring(data.toString().indexOf("//") + 2);
		}
		if (TextUtils.isEmpty(tuid)) {
			return;
		}
		mUser = DamiCommon.getLoginResult(this);
		isSelf = mUser.uid.equals(tuid);
		initComponent();
		hideSomeViewsBasedOnUser();

		dynamicHelper = new DynamicHelper(mContext, DynamicHelper.DY_PROFILE);
		tagWindowManager = new TagWindowManager(this, isSelf, tagCallback);
		getUserInfo();
		getTags();
	}

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
						tUser = data.data;
						tagWindowManager.setTagList(tUser.tag);
						bindView();
					}
				} else {
					otherCondition(data.state, ProfileActivity.this);
				}
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
		mTitleBar.setTitleText(getString(R.string.profile));
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);

		ivHeader = (ImageView) findViewById(R.id.iv_header);

		ViewUtil.findViewById(this, R.id.iv_profile_erweima).setOnClickListener(this);

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

		layoutBasicProfile = findViewById(R.id.layout_basic_profile);
		layoutDyProfile = findViewById(R.id.layout_dynamic_profile);
		layoutBottom = findViewById(R.id.bottom_bar);
		layoutConnection = findViewById(R.id.layout_profile_connection);

		tvDyFavoriteCount = (TextView) findViewById(R.id.tv_profile_dy_favorite);
		tvDyCommentCount = (TextView) findViewById(R.id.tv_profile_dy_comment);
		tvDySpreadCount = (TextView) findViewById(R.id.tv_profile_dy_spread);

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

		if (!TextUtils.isEmpty(tUser.headsmall)) {
			Picasso.with(mContext).load(tUser.headsmall).placeholder(R.drawable.default_header)
					.error(R.drawable.default_header).into(ivHeader);
		}

		tvFancyCount.setText(String.valueOf(tUser.integral));
		tvUserName.setText(tUser.realname);
		tvUserInfo.setText(tUser.company);

		bindContactView();
		bindDyView();
		if (tUser.tag != null) {
			tagList = tUser.tag;
		}
		bindUserTags();
		bindBottomDynamicView();
		bindBottomView();
	}

	private void bindUserTags() {
		if (tagList != null && tagList.size() > 0) {
			if (tagList.size() > 10 && (!isShowAllTags)) {
				tagWindowManager.bindTags(tagLayout, false, tagList.subList(0, 9), zanClickListener);
				tvRevealAllTags.setVisibility(View.VISIBLE);
				return;
			}
			tagWindowManager.bindTags(tagLayout, false, tagList, zanClickListener);
			tvRevealAllTags.setVisibility(View.GONE);
		}
	}

	private OnClickListener zanClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			TagBean tagBean = (TagBean) v.getTag();
			DamiInfo.zanUserTag(mUser.uid, tagBean.id, new SimpleResponseListener(mContext) {
				@Override
				public void onSuccess(Object o) {
					BaseNetBean data = (BaseNetBean) o;
					if (data.state != null && data.state.code == 0) {
						showToast(R.string.zan_success);
					} else {
						otherCondition(data.state, ProfileActivity.this);
					}
				}
			});
		}
	};

	private void bindConnectionView() {
		tvFancyCount.setText(String.valueOf(tUser.integral));
		tvFollowersCount.setText(String.valueOf(tUser.followers));
		tvFansCount.setText(String.valueOf(tUser.fansers));
		tvMeetingsCount.setText(String.valueOf(tUser.meetingCount));
		tvTribesCount.setText(String.valueOf(tUser.tribeCount));
	}

	private void bindProfileView() {
		tvRealName.setText(tUser.realname);
		tvCompany.setText(tUser.company);
		tvJob.setText(tUser.post);
	}

	private void bindContactView() {
		tvEmail.setText(tUser.email);
		tvPhone.setText(tUser.phone);
		tvWeixin.setText(mUser.weixin);
		tvWeibo.setText(mUser.weibo);
		if (!(isSelf || tUser.isfollow == 1 || tUser.isfollow == 3)) {
			PrivacyConfig pc = tUser.privacyconfig;
			if (pc.mail == 0) {
				tvEmail.setText(R.string.profile_view_after_follow);
			}
			if (pc.phone == 0) {
				tvPhone.setText(R.string.profile_view_after_follow);
			}
			if (pc.wechat == 0) {
				tvWeixin.setText(R.string.profile_view_after_follow);
			}
			if (pc.weibo == 0) {
				tvWeibo.setText(R.string.profile_view_after_follow);
			}
		}
		if (!isSelf) {
			removeTextDrawable(tvEmail);
			removeTextDrawable(tvPhone);
			removeTextDrawable(tvWeixin);
			removeTextDrawable(tvWeibo);
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

	private void bindBottomDynamicView() {
		// TODO Auto-generated method stub
		boolean isZan = false, isSpread = false, isComment = false;
		layoutZanHolder.setLineHalfOnly(false);
		layoutSpreadHolder.setLineHalfOnly(false);
		layoutCommentHolder.setLineHalf(true);
		if (tUser.zantaglist != null && tUser.zantaglist.size() > 0) {
			isZan = true;
			List<ConnectionBean.User> userList = new ArrayList<ConnectionBean.User>();
			for (ZanBean bean : tUser.zantaglist) {
				ConnectionBean.User user = new ConnectionBean.User();
				user.uid = bean.uid;
				user.realname = bean.realname;
				userList.add(user);
			}
			tvBottomFavorite.setText(MyTextUtils.getSpannableString(MyTextUtils.addUserSpans(userList), "赞过"));
//			tvBottomFavorite.setText(MyTextUtils.addConnectionUserList(userList, "赞过"));
		} else {
			layoutZanHolder.setVisibility(View.GONE);
		}

		if (tUser.kuosanlist != null && tUser.kuosanlist.size() > 0) {
			isSpread = true;
			List<ConnectionBean.User> userList = new ArrayList<ConnectionBean.User>();
			for (SpreadBean bean : tUser.kuosanlist) {
				ConnectionBean.User user = new ConnectionBean.User();
				user.uid = bean.uid;
				user.realname = bean.realname;
				userList.add(user);
			}
			tvBottomSpread.setText(MyTextUtils.getSpannableString(MyTextUtils.addUserSpans(userList), "扩散过"));
//			tvBottomSpread.setText(MyTextUtils.addConnectionUserList(userList, "扩散过"));
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

		tvDyFavoriteCount.setText(String.valueOf(bean.totalzan));
		tvDySpreadCount.setText(String.valueOf(bean.totalkuosan));
		tvDyCommentCount.setText(String.valueOf(bean.totalcomment));

		layoutDyContent.addView(dynamicHelper.getView(dyView, bean));
	}

	private boolean bindCommentView() {
		if (tUser.commentlist != null && tUser.commentlist.size() > 0) {
			for (final CommentBean bean : tUser.commentlist) {
				View view = mInflater.inflate(R.layout.item_general, null);
				TextView nameView = (TextView) view.findViewById(R.id.tv_title);
				TextView infoView = (TextView) view.findViewById(R.id.tv_info);
				TextView dateView = (TextView) view.findViewById(R.id.tv_date);

				ImageView headerView = (ImageView) view.findViewById(R.id.iv_header);
				nameView.setText(bean.uname);
				infoView.setText(bean.content.content);
				dateView.setText(FeatureFunction.getGeneralTime(bean.addtime * 1000));
				if (!TextUtils.isEmpty(bean.s_path)) {
					Picasso.with(mContext).load(bean.s_path).placeholder(R.drawable.default_header)
							.error(R.drawable.default_header).into(headerView);
				}

				// if (isSelf) {
				// TextView tvDelete = (TextView)
				// view.findViewById(R.id.tv_delete);
				// tvDelete.setVisibility(View.VISIBLE);
				// tvDelete.setOnClickListener(new OnClickListener() {
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				//
				// }
				// });
				// }
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ab_setting: {
			startActivity(SettingActivity.class);
			break;
		}
		case R.id.tv_my_fans_count: {
			Intent intent = new Intent(mContext, ContactActivity.class);
			intent.putExtra(ContactActivity.KEY_TYPE, ContactActivity.TYPE_FANS);
			intent.putExtra(ContactActivity.KEY_UID, tUser.uid);
			startActivity(intent);
			break;
		}
		// case R.id.tv_my_meetings_count: {
		// Intent intent = new Intent(mContext, ContactActivity.class);
		// intent.putExtra(ContactActivity.KEY_TYPE,
		// ContactActivity.TYPE_FOLLOWERS);
		// intent.putExtra(ContactActivity.KEY_UID, tUser.uid);
		// startActivity(intent);
		// break;
		// }
		// case R.id.tv_my_tribes_count: {
		// Intent intent = new Intent(mContext, ContactActivity.class);
		// intent.putExtra(ContactActivity.KEY_TYPE,
		// ContactActivity.TYPE_FOLLOWERS);
		// intent.putExtra(ContactActivity.KEY_UID, tUser.uid);
		// startActivity(intent);
		// break;
		// }
		case R.id.tv_my_followers_count: {
			Intent intent = new Intent(mContext, ContactActivity.class);
			intent.putExtra(ContactActivity.KEY_TYPE, ContactActivity.TYPE_FOLLOWERS);
			intent.putExtra(ContactActivity.KEY_UID, tUser.uid);
			startActivity(intent);
			break;
		}
		case R.id.tv_reverification:
			startActivity(ReverificationActivity.class);
			break;
		case R.id.layout_profile_email:
			changeContact(ChangeProfileActivity.TYPE_EMAIL, tUser.email);
			break;
		case R.id.layout_profile_phone_num:
			changeContact(ChangeProfileActivity.TYPE_PHONE, tUser.phone);
			break;
		case R.id.layout_profile_weibo_num:
			changeContact(ChangeProfileActivity.TYPE_WEIBO, tUser.weibo);
			break;
		case R.id.layout_profile_weixin_num:
			changeContact(ChangeProfileActivity.TYPE_WEIXIN, tUser.weixin);
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
			startActivity(intent);
			break;
		}
		case R.id.layout_profile_bottom_follow:
			followUser();
			break;
		case R.id.layout_profile_bottom_msg:
			startActivityWithUser(ChatMessageActivity.KEY_USER, ChatMessageActivity.class);
			break;
		case R.id.layout_profile_bottom_spread:
			spreadUser();
			break;
		case R.id.iv_profile_erweima: {
			Intent intent = new Intent(mContext, TwoDimensionActivity.class);
			intent.putExtra("user", tUser);
			startActivity(intent);
			break;
		}
		case R.id.tv_profile_dy_more:
			startActivity(MyDynamicActivity.getIntent(mContext, tuid));
			break;
		default:
			break;
		}
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
					} else {
						showToast(R.string.follow_success);
						if (tUser.isfollow == 2) {
							tUser.isfollow = 3;
						}
						if (tUser.isfollow == 0) {
							tUser.isfollow = 1;
						}
						mUser.followers = mUser.followers + 1;
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

	private void spreadUser() {
		DamiInfo.spreadDynamic(5, tUser.uid, "", "", "", "", new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.spread_success);
				} else {
					otherCondition(data.state, ProfileActivity.this);
				}
			}
		});
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
				tUser = DamiCommon.getLoginResult(this);
				tvEmail.setText(tUser.email);
				tvPhone.setText(tUser.phone);
				tvWeixin.setText(tUser.weixin);
				tvWeibo.setText(tUser.weibo);
			}
		}
	}

	private void getTags() {
		DamiInfo.getTags(new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				TagResult data = (TagResult) o;
				if (data.state != null && data.state.code == 0) {
					recTagList = data.data;
					tagWindowManager.setRecTagList(data.data);
				} else {
					this.otherCondition(data.state, ProfileActivity.this);
				}
			}
		});
	}

	// store tags in remote computer
	private void addRemoteTags(String tags) {
		Logger.d(this, tags);
		if (!TextUtils.isEmpty(tags)) {
			DamiInfo.updateUserTag(tuid, tags, new SimpleResponseListener(mContext, R.string.request_internet_now) {
				@Override
				public void onSuccess(Object o) {
					// TODO Auto-generated method stub
					TagResultBean data = (TagResultBean) o;
					if (data.state != null && data.state.code == 0) {
						tagList.clear();
						tagList.addAll(data.data);
						bindUserTags();
						// tagWindowManager.bindTags(tagLayout, false);
						showToast(R.string.add_tags_success);
					}
				}
			});
		}
	}

}

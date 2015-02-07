package com.gaopai.guiren.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.activity.MeetingDetailActivity;
import com.gaopai.guiren.activity.ProfileActivity;
import com.gaopai.guiren.activity.ShowImagesActivity;
import com.gaopai.guiren.activity.SpreadDynamicActivity;
import com.gaopai.guiren.activity.TribeDetailActivity;
import com.gaopai.guiren.activity.WebActivity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.dynamic.DynamicBean.CommentBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.CommentContetnHolder;
import com.gaopai.guiren.bean.dynamic.DynamicBean.CommnetHolder;
import com.gaopai.guiren.bean.dynamic.DynamicBean.JsonContent;
import com.gaopai.guiren.bean.dynamic.DynamicBean.PicBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.SpreadBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.bean.dynamic.DynamicBean.ZanBean;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.media.MediaUIHeper;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.media.SpeexPlayerWrapper.OnDownLoadCallback;
import com.gaopai.guiren.support.chat.ChatMsgHelper;
import com.gaopai.guiren.support.view.HeadView;
import com.gaopai.guiren.utils.DateUtil;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyTextUtils.SpanUser;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.utils.WeiboTextUrlSpan;
import com.gaopai.guiren.view.FlowLayout;
import com.gaopai.guiren.view.MyGridLayout;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.squareup.picasso.Picasso;

public class DynamicHelper {
	public static final int TYPE_SEND_DYNAMIC = 1;
	public final static int TYPE_SPREAD_MSG = 2;

	public final static int TYPE_SPREAD_MEETING = 3;

	public final static int TYPE_SPREAD_LINK = 6;
	public final static int TYPE_SPREAD_TRIBE = 4;
	public final static int TYPE_SPREAD_USER = 5;

	public final static int TYPE_SPREAD_OTHER_DYNAMIC = 7;

	public final static int DY_LIST = 0;
	public final static int DY_DETAIL = 1;
	public final static int DY_PROFILE = 2;// not show user header, info,
											// comments
	public final static int DY_MY_LIST = 3;// not show action window, comments
											// and so on
	private int mDyKind = 0;

	private Context mContext;
	private User user;
	private LayoutInflater mInflater;
	private boolean mIsDyList = false;// list page or detail page
	private SpeexPlayerWrapper mPlayerWrapper;
	private String palyedMessagTag;

	public DynamicHelper(Context context, int dyKind) {
		mContext = context;
		mIsDyList = (dyKind == DY_LIST || dyKind == DY_MY_LIST);
		mDyKind = dyKind;
		user = DamiCommon.getLoginResult(mContext);
		mInflater = LayoutInflater.from(mContext);
		mPlayerWrapper = new SpeexPlayerWrapper(mContext, new OnDownLoadCallback() {
			@Override
			public void onSuccess(MessageInfo messageInfo) {
				// TODO Auto-generated method stub
				downVoiceSuccess(messageInfo);
			}
		});
		mPlayerWrapper.setPlayCallback(new PlayCallback());
	}

	public void updateUser() {
		if (user == null) {
			return;
		}
		user = DamiCommon.getLoginResult(mContext);
	}

	private void downVoiceSuccess(final MessageInfo msg) {
		if (mPlayerWrapper.getMessageTag().equals(msg.tag)) {
			mPlayerWrapper.start(msg);
			msg.isReadVoice = 1;
			// callback.notifyUpdateView();
		}
	}

	public void stopPlayVoice() {
		if (mPlayerWrapper != null && mPlayerWrapper.isPlay()) {
			mPlayerWrapper.stop();
		}
	}

	private DyCallback callback;

	public void setCallback(DyCallback callback) {
		this.callback = callback;
	}

	public static interface DyCallback {
		public void notifyUpdateView();

		public void onCommentSuccess();

		public void onCommentButtonClick(TypeHolder typeHolder, String name, boolean isShowReply);

		public void onBindComment(TypeHolder typeHolder, ViewHolderCommon holder);

		public void onBindCommenViewBottom(TypeHolder typeHolder, ViewHolderCommon holder, boolean isShowComment,
				boolean isShowSpread, boolean isShowZan);

		public void onDeleteItemSuccess(TypeHolder typeHolder);

		public void onVoiceStart();

		public void onVoiceStop();

	}

	public static class DySoftCallback implements DyCallback {
		@Override
		public void onBindComment(TypeHolder typeHolder, ViewHolderCommon holder) {

		}

		@Override
		public void onBindCommenViewBottom(TypeHolder typeHolder, ViewHolderCommon holder, boolean isShowComment,
				boolean isShowSpread, boolean isShowZan) {
		}

		@Override
		public void onCommentButtonClick(TypeHolder typeHolder, String name, boolean isShowReply) {
		}

		@Override
		public void notifyUpdateView() {
		}

		@Override
		public void onCommentSuccess() {

		}

		@Override
		public void onDeleteItemSuccess(TypeHolder typeHolder) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onVoiceStart() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onVoiceStop() {
			// TODO Auto-generated method stub

		}
	}

	private class PlayCallback extends MediaUIHeper.PlayCallback {

		@Override
		public void onStart() {
			callback.onVoiceStart();
		}

		@Override
		public void onStop(boolean stopAutomatic) {
			callback.onVoiceStop();
		}
	}

	public RecyclerListener getRecyleListener() {
		RecyclerListener recyclerListener = new RecyclerListener() {
			@Override
			public void onMovedToScrapHeap(View view) {
				if (!(view.getTag() instanceof ViewHolderCommon)) {
					return;
				}
				ViewHolderCommon viewHolder = (ViewHolderCommon) view.getTag();
				viewHolder.ivHeader.setImageDrawable(null);
				if (viewHolder instanceof ViewHolderSendDynamic) {
					MyGridLayout gridLayout = ((ViewHolderSendDynamic) viewHolder).gridLayout;
					for (int i = 0; i < 9; i++) {
						((ImageView) gridLayout.getChildAt(i)).setImageDrawable(null);
					}
				} else if (viewHolder instanceof ViewHolderSpreadLink) {
					((ViewHolderSpreadLink) viewHolder).ivHeader1.setImageDrawable(null);
				} else if (viewHolder instanceof ViewHolderSpreadMsg) {
					((ViewHolderSpreadMsg) viewHolder).ivHeader1.setImageDrawable(null);
					((ViewHolderSpreadMsg) viewHolder).ivPic.setImageDrawable(null);
				}
			}
		};
		return recyclerListener;
	}

	public void zanMessage(final TypeHolder typeBean) {
		final List<ZanBean> zanList = typeBean.zanList;
		DamiInfo.zanOperation(DamiCommon.getUid(mContext), 1, typeBean.id, 0, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					if (typeBean.isZan == 0) {
						typeBean.isZan = 1;
						ZanBean zanBean = new ZanBean();
						zanBean.uid = user.uid;
						zanBean.uname = getUserName(user);
						zanList.add(zanBean);
					} else {
						for (ZanBean zanBean : zanList) {
							if (zanBean.uid.equals(user.uid)) {
								zanList.remove(zanBean);
								typeBean.isZan = 0;
								break;
							}
						}
					}
					callback.notifyUpdateView();
				} else {
					otherCondition(data.state, (Activity) mContext);
				}

			}
		});
	}

	public void spread(final TypeHolder typeBean, String spreadInfo) {
		DamiInfo.spreadDynamic(-1, typeBean.id, "", "", "", "", spreadInfo, new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					SpreadBean spreadBean = new SpreadBean();
					spreadBean.uid = user.uid;
					spreadBean.nickname = user.displayName;
					spreadBean.realname = getUserName(user);
					if (typeBean.spread == null) {
						typeBean.spread = new ArrayList<SpreadBean>();
					}
					typeBean.spread.add(spreadBean);
					callback.notifyUpdateView();
					// update dynamic count
					user.dynamicCount = user.dynamicCount + 1;
					DamiCommon.saveLoginResult(mContext, user);
					mContext.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
				} else {
					otherCondition(data.state, (Activity) mContext);
				}
			}
		});
	}

	public void commentMessage(final String content, final TypeHolder typeHolder) {
		// final CommnetHolder commnetHolder = typeHolder.commnetHolder;
		final CommnetHolder commnetHolder = typeHolder.commnetHolder;
		DamiInfo.addComment(commnetHolder.toid, commnetHolder.type, commnetHolder.dataid, content, 0,
				commnetHolder.displayname, commnetHolder.todisplayname, new SimpleResponseListener(mContext) {
					@Override
					public void onSuccess(Object o) {
						// TODO Auto-generated method stub
						BaseNetBean data = (BaseNetBean) o;
						if (data.state != null && data.state.code == 0) {
							CommentBean commentBean = new CommentBean();
							commentBean.content = new CommentContetnHolder();
							commentBean.content.content = content;
							commentBean.toid = commnetHolder.toid;
							commentBean.toname = commnetHolder.todisplayname;
							commentBean.uname = commnetHolder.displayname;
							commentBean.type = commnetHolder.type;
							commentBean.dataid = commnetHolder.dataid;
							commentBean.uid = user.uid;
							if (typeHolder.commentlist == null) {
								typeHolder.commentlist = new ArrayList<CommentBean>();
							}
							typeHolder.commentlist.add(commentBean);
							callback.onCommentSuccess();
						} else {
							otherCondition(data.state, (Activity) mContext);
						}
					}
				});
	}

	private boolean isZan(TypeHolder typeBean) {
		List<ZanBean> zanList = typeBean.zanList;
		if (zanList == null || zanList.size() == 0) {
			return false;
		}
		for (ZanBean zanBean : zanList) {
			if (zanBean.uid.equals(user.uid)) {
				return true;
			}
		}
		return false;
	}

	private PopupWindow actionWindow;
	private int windowHeightPadding;
	private int windowWidthPadding;

	private void showActionWindow(View anchor) {
		// in detail we should use setTag to bind tybeBean to view
		final TypeHolder typeHolder = (TypeHolder) anchor.getTag();
		View v = mInflater.inflate(R.layout.popup_dynamic_action, null);
		Button btnZan = (Button) v.findViewById(R.id.btn_zan);
		Button btnComment = (Button) v.findViewById(R.id.btn_comment);
		Button btnSpread = (Button) v.findViewById(R.id.btn_spread);
		if (!isZan(typeHolder)) {
			typeHolder.isZan = 0;
			btnZan.setText(R.string.zan);
		} else {
			typeHolder.isZan = 1;
			btnZan.setText(R.string.cancel_zan);
		}
		btnComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setCommentHolder(typeHolder);
				callback.onCommentButtonClick(typeHolder, "", false);
				actionWindow.dismiss();
			}
		});
		btnSpread.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mContext.startActivity(SpreadDynamicActivity.getIntent(mContext, typeHolder));
				// spread(typeHolder);
				actionWindow.dismiss();
			}
		});
		btnZan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				zanMessage(typeHolder);
				actionWindow.dismiss();
			}
		});
		actionWindow = new PopupWindow(v, LayoutParams.WRAP_CONTENT, MyUtils.dip2px(mContext, 40));
		actionWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		actionWindow.setAnimationStyle(R.style.window_slide_right);
		actionWindow.setOutsideTouchable(true);
		actionWindow.setFocusable(true);
		ViewUtil.measure(v);
		windowHeightPadding = (v.getMeasuredHeight() + anchor.getMeasuredHeight()) / 2;
		windowWidthPadding = v.getMeasuredWidth() + FeatureFunction.dip2px(mContext, 5);

		if (actionWindow.isShowing()) {
			actionWindow.dismiss();
			return;
		}
		actionWindow.showAsDropDown(anchor, -windowWidthPadding, -windowHeightPadding);
	}

	public static class ViewHolderCommon {
		ImageButton btnDynamicAction;
		public LinearLayout rlDynamicInteractive;
		LinearLayout layoutSpread;
		TextView tvSpread;
		LinearLayout layoutZan;
		TextView tvZan;

		TextView tvDateInfo;

		ImageView ivHeader;
		TextView tvUserName;
		// TextView tvUserInfo;
		// TextView tvAction;
		TextView tvSpreadWords;

		View lineSpread;
		View lineZan;

		public View layoutCoverTop;
		public View layoutCoverTopBottomHolder;
		public LinearLayout layoutComment;
		public TextView tvMoreComment;

		public void initialBottom(ViewHolderCommon viewHolder, View view) {
			viewHolder.tvSpread = (TextView) view.findViewById(R.id.tv_spread);
			viewHolder.tvZan = (TextView) view.findViewById(R.id.tv_zan);
			viewHolder.tvDateInfo = (TextView) view.findViewById(R.id.tv_date_info);
			viewHolder.btnDynamicAction = (ImageButton) view.findViewById(R.id.btn_dynamic_ation);

			if ((Boolean) view.getTag()) { // in list page
				viewHolder.rlDynamicInteractive = (LinearLayout) view.findViewById(R.id.rl_dynamic_interactive);
				viewHolder.tvMoreComment = (TextView) view.findViewById(R.id.tv_more_comment);
				viewHolder.layoutComment = (LinearLayout) view.findViewById(R.id.ll_comment);
			} else {
				viewHolder.layoutCoverTopBottomHolder = view.findViewById(R.id.layout_dynamic_detail_bottom);
			}

			viewHolder.layoutCoverTop = view.findViewById(R.id.view_cover_top);
			viewHolder.ivHeader = (ImageView) view.findViewById(R.id.iv_header);
			viewHolder.tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
			viewHolder.tvUserName.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			viewHolder.tvSpreadWords = (TextView) view.findViewById(R.id.tv_spread_words);
			// viewHolder.tvUserInfo = (TextView)
			// view.findViewById(R.id.tv_user_info);
			// viewHolder.tvAction = (TextView)
			// view.findViewById(R.id.tv_spread_action);

			viewHolder.lineSpread = view.findViewById(R.id.line_spread_bottom);
			viewHolder.lineZan = view.findViewById(R.id.line_zan_bottom);
		}
	}

	static class ViewHolderSendDynamic extends ViewHolderCommon {
		TextView tvContent;
		FlowLayout flTags;
		MyGridLayout gridLayout;
		View layoutDyContent;

		public static ViewHolderSendDynamic getInstance(View view) {
			// TODO Auto-generated method stub
			ViewHolderSendDynamic viewHolder = new ViewHolderSendDynamic();
			viewHolder.initialBottom(viewHolder, view);

			viewHolder.tvContent = (TextView) view.findViewById(R.id.tv_content);
			viewHolder.flTags = (FlowLayout) view.findViewById(R.id.fl_tags);
			viewHolder.gridLayout = (MyGridLayout) view.findViewById(R.id.dynamic_pics_holder);
			viewHolder.layoutDyContent = view.findViewById(R.id.layout_dy_content);

			return viewHolder;
		}
	}

	static class ViewHolderMeeting extends ViewHolderCommon {

		TextView tvMeetingTitle;
		TextView tvMeetingTime;
		TextView tvMeetingGuest;
		View layoutHolder;

		public static ViewHolderMeeting getInstance(View view) {
			// TODO Auto-generated method stub
			ViewHolderMeeting viewHolder = new ViewHolderMeeting();
			viewHolder.initialBottom(viewHolder, view);

			viewHolder.tvMeetingTime = (TextView) view.findViewById(R.id.tv_meeting_time);
			viewHolder.tvMeetingTitle = (TextView) view.findViewById(R.id.tv_meeting_title);
			viewHolder.tvMeetingGuest = (TextView) view.findViewById(R.id.tv_meeting_guest);
			viewHolder.layoutHolder = view.findViewById(R.id.layout_meeting_holder);
			return viewHolder;
		}
	}

	static class ViewHolderSpreadLink extends ViewHolderCommon {

		ImageView ivHeader1;
		TextView tvTitle1;
		TextView tvInfo1;
		View layoutHolder;

		public static ViewHolderSpreadLink getInstance(View view) {
			// TODO Auto-generated method stub
			ViewHolderSpreadLink viewHolder = new ViewHolderSpreadLink();
			viewHolder.initialBottom(viewHolder, view);

			viewHolder.ivHeader1 = (ImageView) view.findViewById(R.id.iv_header1);
			viewHolder.tvTitle1 = (TextView) view.findViewById(R.id.tv_title1);
			viewHolder.tvInfo1 = (TextView) view.findViewById(R.id.tv_info1);
			viewHolder.layoutHolder = view.findViewById(R.id.rl_spread_holder);

			return viewHolder;
		}
	}

	static class ViewHolderSpreadMsg extends ViewHolderCommon {

		ImageView ivHeader1;
		TextView tvUserName1;
		View msgHolder;
		View layoutTextVoice;
		ImageView ivVoice;
		ImageView ivPic;
		TextView tvText;
		TextView tvVoiceLength;

		public static ViewHolderSpreadMsg getInstance(View view) {
			// TODO Auto-generated method stub
			ViewHolderSpreadMsg viewHolder = new ViewHolderSpreadMsg();
			viewHolder.initialBottom(viewHolder, view);

			viewHolder.ivHeader1 = (ImageView) view.findViewById(R.id.iv_header1);
			viewHolder.tvUserName1 = (TextView) view.findViewById(R.id.tv_title1);

			viewHolder.msgHolder = view.findViewById(R.id.ll_dynamic_msg_holder);
			viewHolder.layoutTextVoice = view.findViewById(R.id.layout_msg_text_voice_holder);

			viewHolder.ivVoice = (ImageView) view.findViewById(R.id.iv_chat_voice);
			viewHolder.ivPic = (ImageView) view.findViewById(R.id.iv_chat_photo);
			viewHolder.tvText = (TextView) view.findViewById(R.id.iv_chat_text);
			viewHolder.tvVoiceLength = (TextView) view.findViewById(R.id.tv_chat_voice_time_length);

			return viewHolder;
		}
	}

	private View inflateItemView(int type) {
		View convertView = null;
		switch (type) {
		case TYPE_SEND_DYNAMIC:
		case TYPE_SPREAD_OTHER_DYNAMIC: {
			ViewHolderSendDynamic viewHolder;
			if (mIsDyList) {
				convertView = mInflater.inflate(R.layout.item_dynamic_send, null);
			} else {
				convertView = mInflater.inflate(R.layout.item_dynamic_detail_send, null);
			}
			convertView.setTag(mIsDyList);
			viewHolder = ViewHolderSendDynamic.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
			break;

		case TYPE_SPREAD_MEETING: {
			ViewHolderMeeting viewHolder;
			if (mIsDyList) {
				convertView = mInflater.inflate(R.layout.item_dynamic_meeting, null);
			} else {
				convertView = mInflater.inflate(R.layout.item_dynamic_detail_meeting, null);
			}
			convertView.setTag(mIsDyList);
			viewHolder = ViewHolderMeeting.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
			break;

		case TYPE_SPREAD_MSG: {
			ViewHolderSpreadMsg viewHolder;
			if (mIsDyList) {
				convertView = mInflater.inflate(R.layout.item_dynamic_spread_msg, null);
			} else {
				convertView = mInflater.inflate(R.layout.item_dynamic_detail_spread_msg, null);
			}
			convertView.setTag(mIsDyList);
			viewHolder = ViewHolderSpreadMsg.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
			break;
		case TYPE_SPREAD_LINK:
		case TYPE_SPREAD_TRIBE:
		case TYPE_SPREAD_USER: {
			ViewHolderSpreadLink viewHolder;
			if (mIsDyList) {
				convertView = mInflater.inflate(R.layout.item_dynamic_spread_link, null);
			} else {
				convertView = mInflater.inflate(R.layout.item_dynamic_detail_spread_link, null);
			}
			convertView.setTag(mIsDyList);
			viewHolder = ViewHolderSpreadLink.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
			break;
		default:
			break;
		}
		return convertView;
	}

	public View getView(View convertView, TypeHolder typeBean) {
		Logger.startCountTime();
		try {
			switch (typeBean.type) {
			case TYPE_SPREAD_OTHER_DYNAMIC:
			case TYPE_SEND_DYNAMIC:
				if (shouldInflateNew(convertView)) {
					convertView = inflateItemView(TYPE_SEND_DYNAMIC);
				}
				buildDynamicView((ViewHolderSendDynamic) convertView.getTag(), typeBean);
				break;
			case TYPE_SPREAD_USER:
			case TYPE_SPREAD_TRIBE:
			case TYPE_SPREAD_LINK: {
				if (shouldInflateNew(convertView)) {
					convertView = inflateItemView(TYPE_SPREAD_LINK);
				}
				buildSpreadLinkView((ViewHolderSpreadLink) convertView.getTag(), typeBean);
				break;
			}
			case TYPE_SPREAD_MEETING: {
				if (shouldInflateNew(convertView)) {
					convertView = inflateItemView(TYPE_SPREAD_MEETING);
				}
				buildMeetingView((ViewHolderMeeting) convertView.getTag(), typeBean);
				break;
			}
			case TYPE_SPREAD_MSG:
				if (shouldInflateNew(convertView)) {
					convertView = inflateItemView(TYPE_SPREAD_MSG);
				}
				buildMsgView((ViewHolderSpreadMsg) convertView.getTag(), typeBean);
				break;
			}
		} catch (Exception e) {
			convertView = buildErrorView();
		}
		if (convertView == null) {
			convertView = buildErrorView();
		}
		return convertView;
	}

	private boolean shouldInflateNew(View convertView) {
		return (convertView == null) || (convertView instanceof TextView);
	}

	private void buildMsgView(ViewHolderSpreadMsg viewHolder, final TypeHolder typeBean) {
		// TODO Auto-generated method stub
		buildCommonView(viewHolder, typeBean);
		final JsonContent content = typeBean.jsoncontent;

		notHideViews(viewHolder, content.fileType);
		viewHolder.ivVoice.setLayoutParams(ChatMsgHelper.getVoiceViewLengthParams(viewHolder.ivVoice.getLayoutParams(),
				mContext, content.voiceTime));
		viewHolder.layoutTextVoice.setOnClickListener(null);
		ImageLoaderUtil.displayImage(content.headImgUrl, viewHolder.ivHeader1, R.drawable.default_header);
		viewHolder.tvUserName1.setText(content.displayName);
		switch (content.fileType) {
		case MessageType.TEXT:
			viewHolder.tvText.setText(MyTextUtils.addHttpLinks(content.content));
			viewHolder.tvText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			break;
		case MessageType.PICTURE:
			final String path = content.imgUrlS.trim();
			MessageInfo messageInfo = ChatMsgHelper.creatPicMsg(content.imgUrlS, content.imgUrlL, content.sid);
			ImageLoaderUtil.displayImage(path, viewHolder.ivPic, R.drawable.default_pic);
			if (path.startsWith("http://")) {
				ImageLoaderUtil.displayImage(path, viewHolder.ivPic, R.drawable.default_pic);
			}
			viewHolder.ivPic.setTag(messageInfo);
			viewHolder.ivPic.setOnClickListener(singlePhotoClickListener);
			break;
		case MessageType.VOICE:
			viewHolder.tvVoiceLength.setText(content.voiceTime + "''");
			viewHolder.layoutTextVoice.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					palyedMessagTag = typeBean.id;
					mPlayerWrapper.start(ChatMsgHelper.creatVoiceMsg(content.voiceUrl, typeBean.id));
				}
			});
			AnimationDrawable drawable = (AnimationDrawable) viewHolder.ivVoice.getDrawable();
			if (mPlayerWrapper.isPlay()) {
				Logger.d(this, mPlayerWrapper.getMessageTag() + "   " + typeBean.id);
			}
			if (mPlayerWrapper.isPlay() && mPlayerWrapper.getMessageTag().equals(typeBean.id)) {
				drawable.start();
			} else {
				drawable.stop();
				drawable.selectDrawable(0);
			}
			break;
		}
	}

	// full
	public Spannable parseHeaderText(String name, String uid, String userInfo, String actionInfo) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		int grayColor = mContext.getResources().getColor(R.color.general_text_gray);
		if (!TextUtils.isEmpty(name)) {
			Spannable nameSpannable = MyTextUtils.setTextSize(MyTextUtils.addSingleUserSpan(name, uid), 18);
			builder.append(nameSpannable);
		}
		if (!TextUtils.isEmpty(userInfo)) {
			Spannable userInfoSpannable = MyTextUtils.setTextSize(
					MyTextUtils.setTextColor(" " + userInfo, Color.BLACK), 12);
			builder.append(userInfoSpannable);
		}
		if (!TextUtils.isEmpty(actionInfo)) {
			Spannable actionInfoSpannable = MyTextUtils.setTextSize(
					MyTextUtils.setTextColor("   " + actionInfo, grayColor), 14);
			builder.append(actionInfoSpannable);
		}
		return builder;
	}

	// for new dynamic in ProfileActivity
	private Spannable parseProfileHeaderText(String actionInfo) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		int grayColor = mContext.getResources().getColor(R.color.general_text_gray);
		if (!TextUtils.isEmpty(actionInfo)) {
			Spannable actionInfoSpannable = MyTextUtils
					.setTextSize(MyTextUtils.setTextColor(actionInfo, grayColor), 14);
			builder.append(actionInfoSpannable);
		}
		return builder;
	}

	private String getString(int resId) {
		return mContext.getString(resId);
	}

	private OnClickListener photoClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			List<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
			// messageInfos.add((MessageInfo) v.getTag());
			List<PicBean> list = (List<PicBean>) v.getTag();
			int pos = (int) v.getTag(R.id.dy_photo_position);
			for (PicBean picBean : list) {
				messageInfos.add(ChatMsgHelper.creatPicMsg(picBean.imgUrlL, picBean.imgUrlL, ""));
			}
			Intent intent = new Intent(mContext, ShowImagesActivity.class);
			intent.putExtra("msgList", (Serializable) messageInfos);
			intent.putExtra("position", pos);
			mContext.startActivity(intent);
		}
	};

	private OnClickListener singlePhotoClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			List<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
			messageInfos.add((MessageInfo) v.getTag());
			Intent intent = new Intent(mContext, ShowImagesActivity.class);
			intent.putExtra("msgList", (Serializable) messageInfos);
			intent.putExtra("position", 0);
			mContext.startActivity(intent);
		}
	};

	private void notHideViews(ViewHolderSpreadMsg viewHolder, int which) {
		viewHolder.ivPic.setVisibility(View.GONE);
		viewHolder.tvText.setVisibility(View.GONE);
		viewHolder.tvVoiceLength.setVisibility(View.GONE);
		viewHolder.ivVoice.setVisibility(View.GONE);
		viewHolder.layoutTextVoice.setVisibility(View.VISIBLE);
		switch (which) {
		case MessageType.TEXT:
			viewHolder.tvText.setVisibility(View.VISIBLE);
			break;
		case MessageType.PICTURE:
			viewHolder.layoutTextVoice.setVisibility(View.GONE);
			viewHolder.ivPic.setVisibility(View.VISIBLE);
			break;
		case MessageType.VOICE:
			viewHolder.ivVoice.setVisibility(View.VISIBLE);
			viewHolder.tvVoiceLength.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	private void buildMeetingView(ViewHolderMeeting viewHolder, TypeHolder typeBean) {
		// TODO Auto-generated method stub
		buildCommonView(viewHolder, typeBean);
		final JsonContent jsonContent = typeBean.jsoncontent;

		viewHolder.tvMeetingTitle.setText(jsonContent.name);
		viewHolder.tvMeetingTime.setText(jsonContent.time);
		viewHolder.tvMeetingGuest.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
		// viewHolder.tvMeetingGuest.setText(MyTextUtils.addGuestUserList(jsonContent.guest,
		// "嘉宾："));
		viewHolder.tvMeetingGuest.setText(MyTextUtils.getSpannableString("嘉宾：",
				MyTextUtils.addUserSpans(jsonContent.guest)));

		viewHolder.layoutHolder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, MeetingDetailActivity.class);
				intent.putExtra(MeetingDetailActivity.KEY_MEETING_ID, jsonContent.tid);
				mContext.startActivity(intent);
			}
		});

	}

	private void buildSpreadLinkView(ViewHolderSpreadLink viewHolder, TypeHolder typeBean) {
		// TODO Auto-generated method stub
		buildCommonView(viewHolder, typeBean);
		final JsonContent jsonContent = typeBean.jsoncontent;
		if (jsonContent == null) {
			return;
		}
		switch (typeBean.type) {
		case TYPE_SPREAD_LINK:
			ImageLoaderUtil.displayImage(jsonContent.image, viewHolder.ivHeader1, R.drawable.logo_help);
			viewHolder.tvTitle1.setText(jsonContent.title);
			viewHolder.tvInfo1.setText(jsonContent.desc);
			viewHolder.layoutHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mContext.startActivity(WebActivity.getIntent(mContext, jsonContent.url, getString(R.string.dige)));
				}
			});

			break;
		case TYPE_SPREAD_TRIBE:
			ImageLoaderUtil.displayImage(jsonContent.logo, viewHolder.ivHeader1, R.drawable.default_tribe);
			viewHolder.tvTitle1.setText(jsonContent.name);
			viewHolder.tvInfo1.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			viewHolder.tvInfo1.setText(MyTextUtils.getSpannableString("圈子知名人物：",
					MyTextUtils.addUserSpans(jsonContent.guest)));

			// viewHolder.tvInfo1.setText(MyTextUtils.addGuestUserList(jsonContent.guest,
			// "圈子知名人物："));
			viewHolder.layoutHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, TribeDetailActivity.class);
					intent.putExtra(TribeDetailActivity.KEY_TRIBE_ID, jsonContent.tid);
					mContext.startActivity(intent);
				}
			});
			break;
		case TYPE_SPREAD_USER:
			ImageLoaderUtil.displayImage(jsonContent.headsmall, viewHolder.ivHeader1, R.drawable.default_header);
			viewHolder.tvTitle1.setText(jsonContent.realname);

			viewHolder.tvInfo1.setText(getUserInfoStr(jsonContent.company, jsonContent.post));
			viewHolder.layoutHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, ProfileActivity.class);
					intent.putExtra(ProfileActivity.KEY_UID, jsonContent.uid);
					mContext.startActivity(intent);
				}
			});
			break;
		default:
			break;
		}
	}

	public static String getUserInfoStr(String company, String post) {
		return TextUtils.isEmpty(company) ? (TextUtils.isEmpty(post) ? "" : post) : (TextUtils.isEmpty(post) ? company
				: company + "/" + post);
	}
	
	public View buildErrorView() {
		LinearLayout layout = new LinearLayout(mContext);
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(layoutParams);
		return layout;
	}

	public void buildCommonView(ViewHolderCommon viewHolder, TypeHolder typeBean) {
		int type = typeBean.type;
		String userName = "", userInfo = "", spreadAction, uid;

		if (mDyKind == DY_PROFILE) {
			if (type == TYPE_SEND_DYNAMIC) {
				spreadAction = "发布了一条新动态";
			} else {
				spreadAction = typeBean.title;
			}
			viewHolder.tvUserName.setText(parseProfileHeaderText(spreadAction));
			viewHolder.btnDynamicAction.setVisibility(View.GONE);
			viewHolder.tvDateInfo.setVisibility(View.GONE);
			viewHolder.ivHeader.setVisibility(View.GONE);
			viewHolder.layoutCoverTopBottomHolder.setVisibility(View.GONE);
			return;
		}

		boolean isShowZan = false, isShowComment = false, isShowSpread = false;
		viewHolder.lineZan.setVisibility(View.GONE);
		viewHolder.lineSpread.setVisibility(View.GONE);

		userName = typeBean.realname;
		uid = typeBean.uid;
		userInfo = getUserInfoStr(typeBean.company, typeBean.post);
		if (typeBean.isanonymous == 1) {
			ImageLoaderUtil.displayImage(typeBean.defhead, viewHolder.ivHeader, R.drawable.default_header);
			userInfo = "";
			uid = "-1";
			userName = getString(R.string.no_name);
		} else {
			ImageLoaderUtil.displayImage(typeBean.s_path, viewHolder.ivHeader, R.drawable.default_header);
		}

		if (type == TYPE_SEND_DYNAMIC) {
			spreadAction = "";
		} else {
			spreadAction = typeBean.title;
		}

		if (typeBean.bigv == 1 && typeBean.isanonymous == 0) {
			viewHolder.tvUserName.setText(HeadView.getMvpName(mContext,
					parseHeaderText(userName + HeadView.MVP_NAME_STR, uid, userInfo, spreadAction)));
		} else {
			viewHolder.tvUserName.setText(parseHeaderText(userName, uid, userInfo, spreadAction));
		}

		if (TextUtils.isEmpty(typeBean.speak)) {
			viewHolder.tvSpreadWords.setVisibility(View.GONE);
		} else {
			viewHolder.tvSpreadWords.setVisibility(View.VISIBLE);
			viewHolder.tvSpreadWords.setText(typeBean.speak);
		}

		viewHolder.tvDateInfo.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
		if (typeBean.uid.equals(user.uid)) {
			setDateDeleteSpan(viewHolder.tvDateInfo, typeBean);
		} else {
			viewHolder.tvDateInfo.setText(DateUtil.getCreateTime(Long.valueOf(typeBean.time)));
		}

		// take care of dynamic detail activity, so put it here, not in the
		// scope of type SpreadMsg
		if (typeBean.type == TYPE_SPREAD_MSG) {
			addMsgFromInfo(viewHolder.tvDateInfo, typeBean.jsoncontent);
		}

		if (mDyKind == DY_MY_LIST) {
			viewHolder.rlDynamicInteractive.setVisibility(View.GONE);
			viewHolder.btnDynamicAction.setVisibility(View.GONE);
			return;
		}

		if (typeBean.spread != null && typeBean.spread.size() > 0) {
			isShowSpread = true;
			viewHolder.tvSpread.setVisibility(View.VISIBLE);
			viewHolder.tvSpread.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			viewHolder.tvSpread.setText(MyTextUtils.addUserSpans(typeBean.spread));
		} else {
			viewHolder.tvSpread.setVisibility(View.GONE);
		}

		if (typeBean.zanList != null && typeBean.zanList.size() > 0) {
			isShowZan = true;
			viewHolder.lineSpread.setVisibility(isShowSpread ? View.VISIBLE : View.GONE);
			viewHolder.tvZan.setVisibility(View.VISIBLE);
			viewHolder.tvZan.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			viewHolder.tvZan.setText(MyTextUtils.addUserSpans(getZanUserList(typeBean.zanList)));
		} else {
			viewHolder.tvZan.setVisibility(View.GONE);
		}

		if (typeBean.commentlist != null && typeBean.commentlist.size() > 0) {
			viewHolder.lineZan.setVisibility((isShowSpread || isShowZan) ? View.VISIBLE : View.GONE);
			isShowComment = true;
		}

		callback.onBindCommenViewBottom(typeBean, viewHolder, isShowComment, isShowSpread, isShowZan);

		viewHolder.btnDynamicAction.setTag(typeBean);
		viewHolder.btnDynamicAction.setOnClickListener(moreWindowClickListener);
	}

	private void setDateDeleteSpan(TextView tView, TypeHolder typeBean) {

		tView.setTag(typeBean);
		String date = DateUtil.getCreateTime(Long.valueOf(typeBean.time)) + "   ";
		SpannableString spannableString = new SpannableString(date + "删除");
		spannableString.setSpan(new DeleteSpanClick(typeBean.id, WeiboTextUrlSpan.TYPE_CONNECTION), date.length(),
				spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tView.setText(spannableString);
	}

	private void addMsgFromInfo(TextView textView, JsonContent jsonContent) {
		if (jsonContent.type == 300) {
			textView.setText(MyTextUtils.getSpannableString(textView.getText(), "   来自会议:",
					MyTextUtils.addSingleMeetingSpan(jsonContent.title, jsonContent.to)));
		} else {
			textView.setText(MyTextUtils.getSpannableString(textView.getText(), "   来自圈子:",
					MyTextUtils.addSingleTribeSpan(jsonContent.title, jsonContent.to)));
		}

	}

	public class DeleteSpanClick extends WeiboTextUrlSpan {
		public DeleteSpanClick(String url, int type) {
			super(url, type);
		}

		@Override
		public void onClick(View widget) {
			// TODO Auto-generated method stub
			final TypeHolder typeHolder = (TypeHolder) widget.getTag();
			Logger.d(this, getUrl());
			((BaseActivity) mContext).showDialog(mContext.getString(R.string.confirm_delete), null,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							deleteDynamicItem(typeHolder);
						}
					});
		}
	}

	public void deleteDynamicItem(final TypeHolder typeBean) {
		DamiInfo.delDynamic(typeBean.id, new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					MainActivity.minusDynamic(mContext);
					callback.onDeleteItemSuccess(typeBean);
				} else {
					otherCondition(data.state, (Activity) mContext);
				}

			}
		});
	}

	private List<SpanUser> getZanUserList(List<ZanBean> zanList) {
		List<SpanUser> spanUsers = new ArrayList<SpanUser>();
		for (ZanBean zanBean : zanList) {
			SpanUser spanUser = new SpanUser();
			spanUser.realname = zanBean.uname;
			spanUser.uid = zanBean.uid;
			spanUsers.add(spanUser);
		}
		return spanUsers;
	}

	private OnClickListener moreWindowClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			showActionWindow(v);
		}
	};

	private void buildDynamicView(ViewHolderSendDynamic viewHolder, TypeHolder typeBean) {
		// TODO Auto-generated method stub
		JsonContent jsonContent = typeBean.jsoncontent;
		buildCommonView(viewHolder, typeBean);
		if (TextUtils.isEmpty(jsonContent.content)) {
			viewHolder.tvContent.setVisibility(View.GONE);
		} else {
			viewHolder.tvContent.setVisibility(View.VISIBLE);
			viewHolder.tvContent.setText(MyTextUtils.addHttpLinks(jsonContent.content));
		}

		viewHolder.tvContent.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
		if (typeBean.type == TYPE_SPREAD_OTHER_DYNAMIC) {
			viewHolder.tvContent.setVisibility(View.VISIBLE);
			if (jsonContent.isanonymous == 1) {
				jsonContent.realname = getString(R.string.no_name);
				jsonContent.uid = "-1";
			}

			if (jsonContent.content == null) {
				jsonContent.content = "";
			}
			viewHolder.tvContent.setText(MyTextUtils.getSpannableString(
					MyTextUtils.addSingleUserSpan(jsonContent.realname, jsonContent.uid),
					MyTextUtils.addHttpLinks("：" + jsonContent.content)));
			changeDyHolderState(true, viewHolder);
		} else {
			changeDyHolderState(false, viewHolder);
		}

		buidImageViews(viewHolder.gridLayout, jsonContent.pic);
		viewHolder.flTags.removeAllViews();
		viewHolder.flTags.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(typeBean.tag)) {
			String[] tags = typeBean.tag.split(",");

			if (tags.length > 0) {
				viewHolder.flTags.setVisibility(View.VISIBLE);
				for (String tag : tags) {
					viewHolder.flTags.addView(TagWindowManager.creatTagFlow(tag, null, mInflater, false),
							viewHolder.flTags.getTextLayoutParams());
				}
			}
		}
	}

	private void changeDyHolderState(boolean isSpread, ViewHolderSendDynamic viewHolder) {
		View layoutDyContent = viewHolder.layoutDyContent;
		if (isSpread) {
			layoutDyContent.setBackgroundColor(mContext.getResources().getColor(R.color.general_background_gray));
			int padding = MyUtils.dip2px(mContext, 5);
			layoutDyContent.setPadding(padding, padding, padding, padding);
		} else {
			layoutDyContent.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
			layoutDyContent.setPadding(0, 0, 0, 0);
		}
	}

	private void buidImageViews(MyGridLayout gridLayout, List<PicBean> pics) {
		int count;
		if (pics == null) {
			count = 0;
		} else {
			count = pics.size();
		}
		for (int i = 0; i < 9; i++) {
			ImageView imageView = (ImageView) gridLayout.getChildAt(i);
			if (i < count) {
				imageView.setVisibility(View.VISIBLE);
				ImageLoaderUtil.displayImage(pics.get(i).imgUrlS, imageView, R.drawable.default_pic);
				imageView.setTag(pics);
				imageView.setTag(R.id.dy_photo_position, i);
				imageView.setOnClickListener(photoClickListener);
			} else {
				imageView.setVisibility(View.GONE);
			}

		}
	}

	public void setCommentHolder(TypeHolder typeBean) {
		CommnetHolder commnetHolder = typeBean.commnetHolder;
		commnetHolder.displayname = getUserName(user);
		commnetHolder.dataid = typeBean.id;
		commnetHolder.todisplayname = null;
		commnetHolder.toid = null;
		commnetHolder.type = 1;
	}

	public void setCommentHolderForReply(TypeHolder typeBean, CommentBean commentBean) {
		CommnetHolder commnetHolder = typeBean.commnetHolder;
		commnetHolder.toid = commentBean.uid;
		commnetHolder.todisplayname = commentBean.uname;
		commnetHolder.displayname = getUserName(user);
		commnetHolder.dataid = commentBean.dataid;
		commnetHolder.type = 1;
	}

	public String getUserName(User user) {
		if (!TextUtils.isEmpty(user.realname)) {
			return user.realname;
		}
		if (!TextUtils.isEmpty(user.nickname)) {
			return user.nickname;
		}
		return mContext.getString(R.string.no_name);
	}

	public CharSequence getCommentString(CommentBean commentBean) {
		makeCommentNotNull(commentBean);
		if (commentBean.toid != null && (!commentBean.toid.equals("0"))) {
			return MyTextUtils.getSpannableString(MyTextUtils.addSingleUserSpan(commentBean.uname, commentBean.uid),
					"回复", MyTextUtils.addSingleUserSpan(commentBean.toname, commentBean.toid), ":",
					MyTextUtils.addHttpLinks(commentBean.content.content));
		} else {
			return MyTextUtils.getSpannableString(MyTextUtils.addSingleUserSpan(commentBean.uname, commentBean.uid),
					":", MyTextUtils.addHttpLinks(commentBean.content.content));
		}
	}

	public void makeCommentNotNull(CommentBean commentBean) {
		if (TextUtils.isEmpty(commentBean.toname)) {
			commentBean.toname = mContext.getString(R.string.no_name);
		}
		if (TextUtils.isEmpty(commentBean.uname)) {
			commentBean.uname = mContext.getString(R.string.no_name);
		}
		if (commentBean.content == null) {
			commentBean.content = new CommentContetnHolder();
		}
		if (TextUtils.isEmpty(commentBean.content.content)) {
			commentBean.content.content = "";
		}
	}

	public final static String ACTION_REFRESH_DYNAMIC = "com.gaopai.guiren.intent.action.REFRESH_DYNAMIC";

	public static Intent getDeleteIntent(String id) {
		Intent intent = new Intent(ACTION_REFRESH_DYNAMIC);
		intent.putExtra("id", id);
		return intent;
	}
}

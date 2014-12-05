package com.gaopai.guiren.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.DynamicDetailActivity;
import com.gaopai.guiren.activity.MeetingDetailActivity;
import com.gaopai.guiren.activity.ProfileActivity;
import com.gaopai.guiren.activity.ShowImagesActivity;
import com.gaopai.guiren.activity.TribeDetailActivity;
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
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.FlowLayout;
import com.gaopai.guiren.view.MyGridLayout;
import com.gaopai.guiren.volley.SimpleResponseListener;

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
	public final static int DY_PROFILE = 2;
	public final static int DY_MY_LIST = 3;
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

	private void downVoiceSuccess(final MessageInfo msg) {
		if (mPlayerWrapper.getMessageTag().equals(msg.tag)) {
			mPlayerWrapper.start(msg);
			msg.isReadVoice = 1;
			callback.onDownVoiceSuccess();
		}
	}

	private DyCallback callback;

	public void setCallback(DyCallback callback) {
		this.callback = callback;
	}

	public static interface DyCallback {
		public void onZanSuccess();

		public void onCommentSuccess();

		public void onSpreadSuccess();

		public void onCommentButtonClick(TypeHolder typeHolder, boolean isShowReply);

		public void onBindComment(TypeHolder typeHolder, ViewHolderCommon holder);

		public void onBindCommenViewBottom(TypeHolder typeHolder, ViewHolderCommon holder, boolean isShowComment,
				boolean isShowSpread, boolean isShowZan);

		public void onDownVoiceSuccess();

		public void onVoicePlayStart();

		public void onVoicePlayEnd();
	}

	private class PlayCallback extends MediaUIHeper.PlayCallback {

		@Override
		public void onStart() {
			callback.onVoicePlayStart();
		}

		@Override
		public void onStop(boolean stopAutomatic) {
			// TODO Auto-generated method stub
			callback.onVoicePlayEnd();
		}
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
						zanBean.uname = user.realname;
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
					callback.onZanSuccess();
				} else {
					otherCondition(data.state, (Activity) mContext);
				}

			}
		});
	}

	public void spread(final TypeHolder typeBean) {
		DamiInfo.spreadDynamic(-1, typeBean.id, "", "", "", "", new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					SpreadBean spreadBean = new SpreadBean();
					spreadBean.uid = user.uid;
					spreadBean.nickname = user.displayName;
					spreadBean.realname = user.realname;
					if (typeBean.spread == null) {
						typeBean.spread = new ArrayList<SpreadBean>();
					}
					typeBean.spread.add(spreadBean);
					callback.onSpreadSuccess();
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
							commentBean.uname = user.realname;
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
			btnZan.setText(R.string.zan_cancel);
		}
		btnComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CommnetHolder commnetHolder = typeHolder.commnetHolder;
				commnetHolder.displayname = user.realname;
				commnetHolder.dataid = typeHolder.id;
				commnetHolder.type = 1;// 实名还是匿名？
				callback.onCommentButtonClick(typeHolder, false);
				actionWindow.dismiss();
			}
		});
		btnSpread.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				spread(typeHolder);
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
		actionWindow = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
		TextView tvUserInfo;
		TextView tvAction;

		View lineSpread;
		View lineZan;

		public View layoutCoverTop;
		public View layoutCoverBottom;
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
				viewHolder.layoutCoverBottom = view.findViewById(R.id.view_cover_bottom);
				viewHolder.layoutCoverTopBottomHolder = view.findViewById(R.id.layout_dynamic_detail_bottom);
			}

			viewHolder.layoutCoverTop = view.findViewById(R.id.view_cover_top);
			viewHolder.ivHeader = (ImageView) view.findViewById(R.id.iv_header);
			viewHolder.tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
			viewHolder.tvUserInfo = (TextView) view.findViewById(R.id.tv_user_info);
			viewHolder.tvAction = (TextView) view.findViewById(R.id.tv_spread_action);

			viewHolder.lineSpread = view.findViewById(R.id.line_spread_bottom);
			viewHolder.lineZan = view.findViewById(R.id.line_zan_bottom);
		}
	}

	static class ViewHolderSendDynamic extends ViewHolderCommon {
		TextView tvContent;
		FlowLayout flTags;
		MyGridLayout gridLayout;

		public static ViewHolderSendDynamic getInstance(View view) {
			// TODO Auto-generated method stub
			ViewHolderSendDynamic viewHolder = new ViewHolderSendDynamic();
			viewHolder.initialBottom(viewHolder, view);

			viewHolder.tvContent = (TextView) view.findViewById(R.id.tv_content);
			viewHolder.flTags = (FlowLayout) view.findViewById(R.id.fl_tags);
			viewHolder.gridLayout = (MyGridLayout) view.findViewById(R.id.dynamic_pics_holder);

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
		RelativeLayout msgHolder;
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

			viewHolder.msgHolder = (RelativeLayout) view.findViewById(R.id.ll_dynamic_msg_holder);
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
		// TODO Auto-generated method stub
		// DynamicBean.TypeHolder typeBean = mData.get(position);
		switch (typeBean.type) {

		case TYPE_SPREAD_OTHER_DYNAMIC:
		case TYPE_SEND_DYNAMIC:
			if (convertView == null) {
				convertView = inflateItemView(TYPE_SEND_DYNAMIC);
			}
			buildDynamicView((ViewHolderSendDynamic) convertView.getTag(), typeBean);
			break;
		case TYPE_SPREAD_USER:
		case TYPE_SPREAD_TRIBE:
		case TYPE_SPREAD_LINK: {
			if (convertView == null) {
				convertView = inflateItemView(TYPE_SPREAD_LINK);
			}
			buildSpreadLinkView((ViewHolderSpreadLink) convertView.getTag(), typeBean);
			break;
		}
		case TYPE_SPREAD_MEETING: {
			if (convertView == null) {
				convertView = inflateItemView(TYPE_SPREAD_MEETING);
			}
			buildMeetingView((ViewHolderMeeting) convertView.getTag(), typeBean);
			break;
		}
		case TYPE_SPREAD_MSG:
			if (convertView == null) {
				convertView = inflateItemView(TYPE_SPREAD_MSG);
			}
			buildMsgView((ViewHolderSpreadMsg) convertView.getTag(), typeBean);
			break;
		}
		return convertView;
	}

	private void buildMsgView(ViewHolderSpreadMsg viewHolder, final TypeHolder typeBean) {
		// TODO Auto-generated method stub
		buildCommonView(viewHolder, typeBean);
		final JsonContent content = typeBean.jsoncontent;

		notHideViews(viewHolder, content.fileType);
		viewHolder.ivVoice.setLayoutParams(ChatMsgHelper.getVoiceViewLengthParams(mContext, content.voiceTime));
		viewHolder.msgHolder.setOnClickListener(null);
		if (!TextUtils.isEmpty(content.headImgUrl)) {
			ImageLoaderUtil.displayImage(content.headImgUrl, viewHolder.ivHeader1);
		} else {
			viewHolder.ivHeader1.setImageResource(R.drawable.default_header);
		}
		viewHolder.tvUserName1.setText(content.displayName);
		switch (content.fileType) {
		case MessageType.TEXT:
			viewHolder.tvText.setText(MyTextUtils.addHttpLinks(content.content));
			viewHolder.tvText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			break;
		case MessageType.PICTURE:
			final String path = content.imgUrlS.trim();
			MessageInfo messageInfo = ChatMsgHelper.creatPicMsg(content.imgUrlS, content.imgUrlL, content.sid);
			ImageLoaderUtil.displayImage(path, viewHolder.ivPic);
			if (path.startsWith("http://")) {
				ImageLoaderUtil.displayImage(path, viewHolder.ivPic);
			}
			viewHolder.ivPic.setTag(messageInfo);
			viewHolder.ivPic.setOnClickListener(photoClickListener);
			break;
		case MessageType.VOICE:
			viewHolder.tvVoiceLength.setText(content.voiceTime + "''");
			viewHolder.msgHolder.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					palyedMessagTag = typeBean.id;
					mPlayerWrapper.start(ChatMsgHelper.creatVoiceMsg(content.voiceUrl, typeBean.id));
				}
			});
			AnimationDrawable drawable = (AnimationDrawable) viewHolder.ivVoice.getDrawable();
			if (mPlayerWrapper.isPlay() && mPlayerWrapper.getMessageTag().equals(palyedMessagTag)) {
				drawable.start();
			} else {
				drawable.stop();
				drawable.selectDrawable(0);
			}
			break;
		}
	}

	private OnClickListener photoClickListener = new OnClickListener() {

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
		switch (which) {
		case MessageType.TEXT:
			viewHolder.tvText.setVisibility(View.VISIBLE);
			break;
		case MessageType.PICTURE:
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
			// url
			ImageLoaderUtil.displayImage(jsonContent.image, viewHolder.ivHeader1);
			viewHolder.tvTitle1.setText(jsonContent.title);
			viewHolder.tvInfo1.setText(jsonContent.desc);
			viewHolder.layoutHolder.setOnClickListener(null);

			break;
		case TYPE_SPREAD_TRIBE:
			if (!TextUtils.isEmpty(jsonContent.logo)) {
				ImageLoaderUtil.displayImage(jsonContent.logo, viewHolder.ivHeader1);
			} else {
				viewHolder.ivHeader1.setImageResource(R.drawable.default_tribe);
			}
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
			ImageLoaderUtil.displayImage(jsonContent.headsmall, viewHolder.ivHeader1);
			viewHolder.tvTitle1.setText(jsonContent.realname);
			viewHolder.tvInfo1.setText(jsonContent.post);
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

	public void buildCommonView(ViewHolderCommon viewHolder, TypeHolder typeBean) {
		int type = typeBean.type;

		if (mDyKind == DY_PROFILE) {
			viewHolder.tvAction.setVisibility(View.VISIBLE);
			if (type == TYPE_SEND_DYNAMIC) {
				viewHolder.tvAction.setText("发布了一条新动态");
			} else {
				viewHolder.tvAction.setText(typeBean.title);
			}
			viewHolder.btnDynamicAction.setVisibility(View.GONE);
			viewHolder.tvDateInfo.setVisibility(View.GONE);
			viewHolder.tvUserName.setVisibility(View.GONE);
			viewHolder.tvUserInfo.setVisibility(View.GONE);
			viewHolder.ivHeader.setVisibility(View.GONE);
			viewHolder.layoutCoverTopBottomHolder.setVisibility(View.GONE);
			return;
		}

		boolean isShowZan = false, isShowComment = false, isShowSpread = false;
		viewHolder.lineZan.setVisibility(View.GONE);
		viewHolder.lineSpread.setVisibility(View.GONE);

		if (!TextUtils.isEmpty(typeBean.s_path)) {
			ImageLoaderUtil.displayImage(typeBean.s_path, viewHolder.ivHeader);
		} else {
			viewHolder.ivHeader.setImageResource(R.drawable.default_header);
		}
		viewHolder.tvUserName.setText(typeBean.realname);
		viewHolder.tvUserInfo.setText(typeBean.post);

		if (type == TYPE_SEND_DYNAMIC) {
			viewHolder.tvAction.setVisibility(View.GONE);
		} else {
			viewHolder.tvAction.setVisibility(View.VISIBLE);
			viewHolder.tvAction.setText(typeBean.title);
		}

		viewHolder.tvDateInfo.setText(FeatureFunction.getCreateTime(Long.valueOf(typeBean.time)));
		
		if (mDyKind == DY_MY_LIST) {
			viewHolder.rlDynamicInteractive.setVisibility(View.GONE);
			viewHolder.btnDynamicAction.setVisibility(View.GONE);
			return;
		}

		if (typeBean.spread != null && typeBean.spread.size() > 0) {
			isShowSpread = true;
			viewHolder.tvSpread.setVisibility(View.VISIBLE);
			viewHolder.tvSpread.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			viewHolder.tvSpread.setText(MyTextUtils.addSpreadUserList(typeBean.spread));
			// viewHolder.tvSpread.setText(MyTextUtils.addUserSpans(typeBean.spread));

		} else {
			viewHolder.tvSpread.setVisibility(View.GONE);
		}

		if (typeBean.zanList != null && typeBean.zanList.size() > 0) {
			isShowZan = true;
			viewHolder.lineSpread.setVisibility(isShowSpread ? View.VISIBLE : View.GONE);
			viewHolder.tvZan.setVisibility(View.VISIBLE);
			viewHolder.tvZan.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			viewHolder.tvZan.setText(MyTextUtils.addZanUserList(typeBean.zanList));
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
		viewHolder.tvContent.setText(jsonContent.content);

		viewHolder.gridLayout.removeAllViews();
		if (jsonContent.pic != null) {
			buidImageViews(viewHolder.gridLayout, jsonContent.pic);
		}
		viewHolder.flTags.removeAllViews();
		viewHolder.flTags.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(typeBean.tag)) {
			String[] tags = typeBean.tag.split(",");

			if (tags.length > 0) {
				viewHolder.flTags.setVisibility(View.VISIBLE);
				for (String tag : tags) {
					viewHolder.flTags.addView(creatTagWithoutDelete(tag), viewHolder.flTags.getTextLayoutParams());
				}
			}
		}
	}

	private View creatTagWithoutDelete(String text) {
		ViewGroup v = (ViewGroup) mInflater.inflate(R.layout.btn_send_dynamic_tag, null);
		TextView textView = (TextView) v.findViewById(R.id.tv_tag);
		textView.setText(text);
		v.findViewById(R.id.btn_delete_tag).setVisibility(View.GONE);
		return v;
	}

	private void buidImageViews(MyGridLayout gridLayout, List<PicBean> pics) {
		// TODO Auto-generated method stub
		gridLayout.removeAllViews();
		for (PicBean bean : pics) {
			gridLayout.addView(getImageView(bean.imgUrlS));
		}
	}

	private ImageView getImageView(String url) {
		ImageView imageView = new ImageView(mContext);
		ImageLoaderUtil.displayImage(url, imageView);
		android.view.ViewGroup.LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(lp);
		imageView.setScaleType(ScaleType.CENTER_INSIDE);
		int padding = MyUtils.dip2px(mContext, 5);
		imageView.setPadding(padding, padding, padding, padding);
		return imageView;
	}

	public void setCommentHolder(TypeHolder typeBean) {
		CommnetHolder commnetHolder = typeBean.commnetHolder;
		commnetHolder.displayname = user.realname;
		commnetHolder.dataid = typeBean.id;
		commnetHolder.type = 1;
	}

	public void setCommentHolderForReply(TypeHolder typeBean, CommentBean commentBean) {
		CommnetHolder commnetHolder = typeBean.commnetHolder;
		commnetHolder.toid = commentBean.uid;
		commnetHolder.todisplayname = commentBean.uname;
		commnetHolder.displayname = user.realname;
		commnetHolder.dataid = commentBean.dataid;
		commnetHolder.type = 1;
	}

	public Spannable getCommentString(CommentBean commentBean) {
		makeCommentNotNull(commentBean);
		if (commentBean.toid != null && (!commentBean.toid.equals("0"))) {
			return MyTextUtils.addEmotions(MyTextUtils.getSpannableString(
					MyTextUtils.addSingleUserSpan(commentBean.uname, commentBean.uid), "回复",
					MyTextUtils.addSingleUserSpan(commentBean.toname, commentBean.toid), ":",
					commentBean.content.content));
		} else {
			return MyTextUtils
					.addEmotions(MyTextUtils.getSpannableString(
							MyTextUtils.addSingleUserSpan(commentBean.uname, commentBean.uid), ":",
							commentBean.content.content));
		}
	}

	public void makeCommentNotNull(CommentBean commentBean) {
		if (TextUtils.isEmpty(commentBean.toname)) {
			commentBean.toname = "匿名";
		}
		if (TextUtils.isEmpty(commentBean.uname)) {
			commentBean.uname = "匿名";
		}
	}
}

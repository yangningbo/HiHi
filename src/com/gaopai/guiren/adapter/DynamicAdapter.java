package com.gaopai.guiren.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
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
import com.gaopai.guiren.activity.WebActivity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.dynamic.DynamicBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.CommentBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.CommentContetnHolder;
import com.gaopai.guiren.bean.dynamic.DynamicBean.JsonContent;
import com.gaopai.guiren.bean.dynamic.DynamicBean.PicBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.SpreadBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.bean.dynamic.DynamicBean.ZanBean;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.fragment.DynamicFragment;
import com.gaopai.guiren.media.MediaUIHeper;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.media.SpeexPlayerWrapper.OnDownLoadCallback;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.FlowLayout;
import com.gaopai.guiren.view.MyGridLayout;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class DynamicAdapter extends BaseAdapter {
	private final LayoutInflater mInflater;
	private List<TypeHolder> mData = new ArrayList<TypeHolder>();
	private Context mContext;
	private DynamicFragment mFragment;

	public static final int TYPE_SEND_DYNAMIC = 1;
	public final static int TYPE_SPREAD_MSG = 2;

	public final static int TYPE_SPREAD_MEETING = 3;

	public final static int TYPE_SPREAD_LINK = 6;
	public final static int TYPE_SPREAD_TRIBE = 4;
	public final static int TYPE_SPREAD_USER = 5;

	public final static int TYPE_SPREAD_OTHER_DYNAMIC = 7;

	// public final static int TYPE_JOIN_MEETING = 3;
	// public final static int TYPE_JOIN_TRIBE = 6;
	// public final static int TYPE_MSG_BY_TAG = 1;//精选一条消息

	public void showSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}

	public DynamicAdapter(DynamicFragment fragment) {
		mFragment = fragment;
		mContext = fragment.getActivity();
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPlayerWrapper = new SpeexPlayerWrapper(mContext, new OnDownLoadCallback() {
			@Override
			public void onSuccess(MessageInfo messageInfo) {
				// TODO Auto-generated method stub
				downVoiceSuccess(messageInfo);
			}
		});
		mPlayerWrapper.setPlayCallback(new PlayCallback());
	}

	private class PlayCallback extends MediaUIHeper.PlayCallback {

		@Override
		public void onStart() {
			notifyDataSetChanged();
		}

		@Override
		public void onStop(boolean stopAutomatic) {
			// TODO Auto-generated method stub
			notifyDataSetChanged();
		}
	}

	/**
	 * 下载成功后修改消息状态，更新数据库并播放声音
	 * 
	 * @param msg
	 * @param type
	 */
	private void downVoiceSuccess(final MessageInfo msg) {
		Logger.d(this, "player==null " + (mPlayerWrapper == null) + "   msg==null " + (msg == null));
		if (mPlayerWrapper.getMessageTag().equals(msg.tag)) {
			mPlayerWrapper.start(msg);
			msg.isReadVoice = 1;
			notifyDataSetChanged();
		}
	}

	public void addAll(List<TypeHolder> data) {
		mData.addAll(data);
		notifyDataSetChanged();
	}

	public void clear() {
		mData.clear();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		int type = getItemViewType(position);
		// DynamicBean.TypeHolder typeBean = mData.get(position);
		DynamicBean.TypeHolder typeBean = mData.get(position);
		switch (type + 1) {

		case TYPE_SPREAD_OTHER_DYNAMIC:
		case TYPE_SEND_DYNAMIC:
			if (convertView == null) {
				convertView = inflateItemView(TYPE_SEND_DYNAMIC);
			}
			buildDynamicView((ViewHolderSendDynamic) convertView.getTag(), typeBean, position, type + 1);
			break;
		case TYPE_SPREAD_USER:
		case TYPE_SPREAD_TRIBE:
		case TYPE_SPREAD_LINK: {
			if (convertView == null) {
				convertView = inflateItemView(TYPE_SPREAD_LINK);
			}
			buildSpreadLinkView((ViewHolderSpreadLink) convertView.getTag(), typeBean, position, type + 1);
			break;
		}
		case TYPE_SPREAD_MEETING: {
			if (convertView == null) {
				convertView = inflateItemView(TYPE_SPREAD_MEETING);
			}
			buildMeetingView((ViewHolderMeeting) convertView.getTag(), typeBean, position, type + 1);
			break;
		}
		case TYPE_SPREAD_MSG:
			if (convertView == null) {
				convertView = inflateItemView(TYPE_SPREAD_MSG);
			}
			buildMsgView((ViewHolderSpreadMsg) convertView.getTag(), typeBean, position, type + 1);
			break;
		}
		return convertView;
	}

	private void buildCommonView(ViewHolderCommon viewHolder, TypeHolder typeBean, int position, int type) {
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

		viewHolder.tvDateInfo.setText(FeatureFunction.getCreateTime(Long.valueOf(typeBean.time)) + "     天山上的来客");

		if (typeBean.spread.size() > 0) {
			isShowSpread = true;
			viewHolder.tvSpread.setVisibility(View.VISIBLE);
			viewHolder.tvSpread.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			viewHolder.tvSpread.setText(MyTextUtils.addSpreadUserList(typeBean.spread));
		} else {
			viewHolder.tvSpread.setVisibility(View.GONE);
		}

		if (typeBean.zanList.size() > 0) {
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
			viewHolder.layoutComment.setVisibility(View.VISIBLE);
			buildCommentView(viewHolder.layoutComment, position, typeBean.commentlist);
			isShowComment = true;
		} else {
			viewHolder.layoutComment.setVisibility(View.GONE);
		}

		if (typeBean.totalcomment > 5) {
			viewHolder.tvMoreComment.setVisibility(View.VISIBLE);
			viewHolder.tvMoreComment.setTag(typeBean);
			viewHolder.tvMoreComment.setOnClickListener(moreCommentClickListener);
		} else {
			viewHolder.tvMoreComment.setVisibility(View.GONE);
		}

		if (isShowComment || isShowSpread || isShowZan) {
			viewHolder.rlDynamicInteractive.setVisibility(View.VISIBLE);
		} else {
			viewHolder.rlDynamicInteractive.setVisibility(View.GONE);
		}
		viewHolder.btnDynamicAction.setTag(mData.get(position));
		viewHolder.btnDynamicAction.setOnClickListener(moreWindowClickListener);
	}

	private void buildMeetingView(ViewHolderMeeting viewHolder, TypeHolder typeBean, int position, int type) {
		// TODO Auto-generated method stub
		buildCommonView(viewHolder, typeBean, position, type);
		final JsonContent jsonContent = typeBean.jsoncontent;

		viewHolder.tvMeetingTitle.setText(jsonContent.name);
		viewHolder.tvMeetingTime.setText(jsonContent.time);
		viewHolder.tvMeetingGuest.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
		viewHolder.tvMeetingGuest.setText(MyTextUtils.addGuestUserList(jsonContent.guest, "嘉宾："));
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

	private void buildSpreadLinkView(ViewHolderSpreadLink viewHolder, TypeHolder typeBean, int position, int type) {
		// TODO Auto-generated method stub
		Log.d("tag", "position = " + position);
		buildCommonView(viewHolder, typeBean, position, type);
		final JsonContent jsonContent = typeBean.jsoncontent;

		switch (type) {
		case TYPE_SPREAD_LINK:
			// url
			ImageLoaderUtil.displayImage(jsonContent.image, viewHolder.ivHeader1);
			viewHolder.tvTitle1.setText(jsonContent.title);
			viewHolder.tvInfo1.setText(jsonContent.desc);
			viewHolder.layoutHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, WebActivity.class);
					intent.putExtra("title", jsonContent.title);
					intent.putExtra("url", jsonContent.url);
					mContext.startActivity(intent);
				}
			});

			break;
		case TYPE_SPREAD_TRIBE:
			ImageLoaderUtil.displayImage(jsonContent.headsmall, viewHolder.ivHeader1);
			viewHolder.tvTitle1.setText(jsonContent.name);
			viewHolder.tvInfo1.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			viewHolder.tvInfo1.setText(MyTextUtils.addGuestUserList(jsonContent.guest, "圈子知名人物："));
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
					intent.putExtra(ProfileActivity.KEY_USER_ID, jsonContent.uid);
					mContext.startActivity(intent);
				}
			});
			break;
		default:
			break;
		}
	}

	private void buildDynamicView(ViewHolderSendDynamic viewHolder, TypeHolder typeBean, int position, int type) {
		// TODO Auto-generated method stub
		JsonContent jsonContent = typeBean.jsoncontent;
		buildCommonView(viewHolder, typeBean, position, type);
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

	private SpeexPlayerWrapper mPlayerWrapper;
	private int palyedPosition;

	private void buildMsgView(ViewHolderSpreadMsg viewHolder, final TypeHolder typeBean, final int position, int type) {
		// TODO Auto-generated method stub
		buildCommonView(viewHolder, typeBean, position, type);
		final JsonContent content = typeBean.jsoncontent;

		notHideViews(viewHolder, content.fileType);
		viewHolder.ivVoice.setLayoutParams(getVoiceViewLengthParams(content));
		viewHolder.msgHolder.setOnClickListener(null);
		switch (content.fileType) {
		case MessageType.TEXT:
			viewHolder.tvText.setText(MyTextUtils.addHttpLinks(content.content));
			viewHolder.tvText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			break;
		case MessageType.PICTURE:
			final String path = content.imgUrlS.trim();
			MessageInfo messageInfo = new MessageInfo();
			messageInfo.imgUrlS = content.imgUrlS;
			messageInfo.imgUrlL = content.imgUrlL;
			messageInfo.fileType = MessageType.PICTURE;
			messageInfo.tag = content.sid;
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
					palyedPosition = position;
					MessageInfo messageInfo = new MessageInfo();
					messageInfo.voiceUrl = content.voiceUrl;
					messageInfo.tag = typeBean.id;
					mPlayerWrapper.start(messageInfo);
				}
			});

			AnimationDrawable drawable = (AnimationDrawable) viewHolder.ivVoice.getDrawable();
			if (mPlayerWrapper.isPlay() && position == palyedPosition) {
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

	private RelativeLayout.LayoutParams getVoiceViewLengthParams(JsonContent content) {
		final int MAX_SECOND = 10;
		final int MIN_SECOND = 2;
		int length = content.voiceTime;
		float max = mContext.getResources().getDimension(R.dimen.voice_max_length_comment);
		float min = mContext.getResources().getDimension(R.dimen.voice_min_length_comment);
		int width = (int) min;
		if (length >= MIN_SECOND && length <= MAX_SECOND) {
			width += (length - MIN_SECOND) * (int) ((max - min) / (MAX_SECOND - MIN_SECOND));
		} else if (length > MAX_SECOND) {
			width = (int) max;
		}
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		return lp;
	}

	@Override
	public int getViewTypeCount() {
		return 7;
	}

	@Override
	public int getItemViewType(int position) {
		return mData.get(position).type - 1;
	}

	private View inflateItemView(int type) {
		View convertView = null;
		switch (type) {
		case TYPE_SEND_DYNAMIC:
		case TYPE_SPREAD_OTHER_DYNAMIC: {
			ViewHolderSendDynamic viewHolder;
			convertView = mInflater.inflate(R.layout.item_dynamic_send, null);
			viewHolder = ViewHolderSendDynamic.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
			break;

		case TYPE_SPREAD_MEETING: {
			ViewHolderMeeting viewHolder;
			convertView = mInflater.inflate(R.layout.item_dynamic_meeting, null);
			viewHolder = ViewHolderMeeting.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
			break;

		case TYPE_SPREAD_MSG: {
			ViewHolderSpreadMsg viewHolder;
			convertView = mInflater.inflate(R.layout.item_dynamic_spread_msg, null);
			viewHolder = ViewHolderSpreadMsg.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
			break;
		case TYPE_SPREAD_LINK:
		case TYPE_SPREAD_TRIBE:
		case TYPE_SPREAD_USER: {
			ViewHolderSpreadLink viewHolder;
			convertView = mInflater.inflate(R.layout.item_dynamic_spread_link, null);
			viewHolder = ViewHolderSpreadLink.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
			break;
		default:
			break;
		}
		return convertView;
	}

	private OnClickListener moreCommentClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(mContext, DynamicDetailActivity.class);
			intent.putExtra(DynamicDetailActivity.KEY_TYPEHOLDER, (TypeHolder) v.getTag());
			mContext.startActivity(intent);
		}
	};
	private OnClickListener moreWindowClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			showActionWindow(v);
		}
	};

	static class ViewHolderCommon {
		ImageButton btnDynamicAction;
		LinearLayout rlDynamicInteractive;
		LinearLayout layoutSpread;
		TextView tvSpread;
		LinearLayout layoutZan;
		TextView tvZan;
		LinearLayout layoutComment;
		TextView tvMoreComment;
		TextView tvDateInfo;

		ImageView ivHeader;
		TextView tvUserName;
		TextView tvUserInfo;
		TextView tvAction;

		View lineSpread;
		View lineZan;

		public void initialBottom(ViewHolderCommon viewHolder, View view) {
			viewHolder.tvSpread = (TextView) view.findViewById(R.id.tv_spread);
			viewHolder.tvZan = (TextView) view.findViewById(R.id.tv_zan);
			viewHolder.tvDateInfo = (TextView) view.findViewById(R.id.tv_date_info);
			viewHolder.rlDynamicInteractive = (LinearLayout) view.findViewById(R.id.rl_dynamic_interactive);
			viewHolder.tvMoreComment = (TextView) view.findViewById(R.id.tv_more_comment);
			viewHolder.btnDynamicAction = (ImageButton) view.findViewById(R.id.btn_dynamic_ation);

			viewHolder.layoutComment = (LinearLayout) view.findViewById(R.id.ll_comment);

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

	private int testPosition = -1;

	List<CommentBean> currentCommentBeans = null;

	private String makeNameNotNull(String name) {
		if (TextUtils.isEmpty(name)) {
			return "匿名";
		}
		return name;
	}

	private void buildCommentView(ViewGroup parent, final int position, final List<CommentBean> commentBeans) {
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		parent.removeAllViews();
		// View lineView = new View(mContext);
		// lineView.setBackgroundColor(mContext.getResources().getColor(R.color.dynamic_horizon_divider));
		// parent.addView(lineView, new LayoutParams(LayoutParams.MATCH_PARENT,
		// 1));
		int textPadding = MyUtils.dip2px(mContext, 3);
		for (int i = 0, count = commentBeans.size(); i < count; i++) {
			final CommentBean commentBean = commentBeans.get(i);
			TextView textView = new TextView(mContext);
			textView.setBackgroundResource(R.drawable.selector_text_btn);
			textView.setCompoundDrawablePadding(MyUtils.dip2px(mContext, 5));
			textView.setPadding(textPadding, textPadding, textPadding, textPadding);
			if (i == 0) {
				textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_dynamic_comment, 0, 0, 0);
			} else {
				textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_dynamic_comment_transparent, 0, 0, 0);
			}
			textView.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			commentBean.uname = makeNameNotNull(commentBean.uname);
			commentBean.toname = makeNameNotNull(commentBean.toname);
			textView.setText(MyTextUtils.addUserHttpLinks(commentBean.uname + "回复" + commentBean.toname + "："
					+ commentBean.content.content, commentBean.uname, commentBean.toname, commentBean.uid,
					commentBean.toid));

			// textView.setText(commentBean.uname + "回复" + commentBean.toname +
			// "：" + commentBean.content.content);
			textView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					currentCommentBeans = commentBeans;
					commnetHolder.toid = commentBean.uid;
					commnetHolder.todisplayname = commentBean.uname;
					commnetHolder.displayname = DamiCommon.getLoginResult(mContext).displayName;
					commnetHolder.dataid = commentBean.dataid;
					commnetHolder.type = commentBean.type;
					mFragment.showChatBox(commnetHolder.todisplayname, mData.get(position));
					showSoftKeyboard();
				}
			});
			parent.addView(textView, lp);
		}
	}

	private PopupWindow actionWindow;
	private int windowHeightPadding;
	private int windowWidthPadding;

	private void showActionWindow(View anchor) {
		final TypeHolder typeHolder = (TypeHolder) anchor.getTag();
		View v = mInflater.inflate(R.layout.popup_dynamic_action, null);
		Button btnZan = (Button) v.findViewById(R.id.btn_zan);
		Button btnComment = (Button) v.findViewById(R.id.btn_comment);
		Button btnSpread = (Button) v.findViewById(R.id.btn_spread);
		if (!isZan(typeHolder)) {
			typeHolder.isZan = 0;
			btnZan.setText("赞");
		} else {
			typeHolder.isZan = 1;
			btnZan.setText("取消赞");
		}
		btnComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentCommentBeans = typeHolder.commentlist;
				commnetHolder.toid = typeHolder.uid;
				commnetHolder.todisplayname = typeHolder.realname;
				commnetHolder.displayname = DamiCommon.getLoginResult(mContext).realname;
				commnetHolder.dataid = typeHolder.id;
				commnetHolder.type = 1;// ??????????????????
				mFragment.showChatBox(commnetHolder.todisplayname, typeHolder);
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

	private void spread(final TypeHolder typeBean) {
		DamiInfo.spreadDynamic(1, typeBean.id, "", "", "", "", new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					SpreadBean spreadBean = new SpreadBean();
					spreadBean.uid = DamiCommon.getUid(mContext);
					spreadBean.nickname = DamiCommon.getLoginResult(mContext).displayName;
					spreadBean.realname = DamiCommon.getLoginResult(mContext).realname;
					if (typeBean.spread == null) {
						typeBean.spread = new ArrayList<SpreadBean>();
					}
					typeBean.spread.add(spreadBean);
					notifyDataSetChanged();
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
			if (zanBean.uid.equals(DamiCommon.getLoginResult(mContext).uid)) {
				return true;
			}
		}
		return false;
	}

	private void zanMessage(final TypeHolder typeBean) {
		final List<ZanBean> zanList = typeBean.zanList;
		DamiInfo.zanOperation(DamiCommon.getUid(mContext), 1, typeBean.id, 0, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub]
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					if (typeBean.isZan == 0) {
						ZanBean zanBean = new ZanBean();
						zanBean.uid = DamiCommon.getUid(mContext);
						zanBean.uname = DamiCommon.getLoginResult(mContext).realname;
						typeBean.isZan = 1;
						zanList.add(zanBean);
					} else {
						for (ZanBean zanBean : zanList) {
							if (zanBean.uid.equals(DamiCommon.getLoginResult(mContext).uid)) {
								zanList.remove(zanBean);
								typeBean.isZan = 0;
								break;
							}
						}
					}
					notifyDataSetChanged();
				} else {
					otherCondition(data.state, (Activity) mContext);
				}

			}
		});
	}

	public static class CommnetHolder {
		public String toid;
		public int type;
		public String dataid;
		public String content;
		public String displayname;
		public String todisplayname;
	}

	private CommnetHolder commnetHolder = new CommnetHolder();

	public void commentMessage(final String content, final TypeHolder typeHolder) {
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
							commentBean.uname = DamiCommon.getLoginResult(mContext).realname;
							commentBean.type = commnetHolder.type;
							if (typeHolder.commentlist == null) {
								typeHolder.commentlist = new ArrayList<CommentBean>();
							}
							Log.d("typeholder", "id===" + typeHolder.id);
							typeHolder.commentlist.add(commentBean);
							notifyDataSetChanged();
						}
					}
				});
	}
}

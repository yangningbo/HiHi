package com.gaopai.guiren.adapter;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.R.string;
import com.gaopai.guiren.activity.ShowImagesActivity;
import com.gaopai.guiren.activity.chat.ChatBaseActivity;
import com.gaopai.guiren.activity.chat.ChatMainActivity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageState;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.media.MediaUIHeper;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.utils.DateUtil;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public abstract class BaseChatAdapter extends BaseAdapter {
	protected Context mContext;
	private User mLogin;
	private final LayoutInflater mInflater;
	private DisplayImageOptions options;

	protected List<MessageInfo> mData;

	private static final int TYPE_COUNT = 2;
	private static final int TYPE_RIGHT = 0;
	private static final int TYPE_LEFT = 1;

	public final static int MODEL_VOICE = 0;
	public final static int MODE_TEXT = 1;
	private int mCurrentMode = MODEL_VOICE;

	private SpeexPlayerWrapper mPlayerWrapper;
	// private int palyedPosition = -1;

	private OnClickListener resendClickListener;

	public BaseChatAdapter(Context context, SpeexPlayerWrapper playerWrapper, List<MessageInfo> messageInfos) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mLogin = DamiCommon.getLoginResult(mContext);
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		options = new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnLoading(R.drawable.normal)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		mPlayerWrapper = playerWrapper;
		mPlayerWrapper.setPlayCallback(new PlayCallback());
		mData = messageInfos;
	}

	public void setResendClickListener(OnClickListener listener) {
		resendClickListener = listener;
	}

	public void addAll(List<MessageInfo> o) {
		mData.addAll(0, o);
		notifyDataSetChanged();
	}

	public void addAllAppend(List<MessageInfo> o) {
		mData.addAll(o);
		notifyDataSetChanged();
	}

	public List<MessageInfo> getMessageInfos() {
		return mData;
	}

	public void clear() {
		mData.clear();
		notifyDataSetChanged();
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
	public int getItemViewType(int position) {
		MessageInfo messageInfo = mData.get(position);
		if (messageInfo.from.equals(mLogin.uid)) {
			return TYPE_RIGHT;
		} else {
			return TYPE_LEFT;
		}
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		int type = getItemViewType(position);
		final MessageInfo messageInfo = mData.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflateItemView(convertView, type);
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			try {
				if (TYPE_LEFT == type) {
					viewHolder = (ViewHolderLeft) convertView.getTag();
				} else {
					viewHolder = (ViewHolderRight) convertView.getTag();
				}
			} catch (Exception e) {
				e.printStackTrace();
				convertView = inflateItemView(convertView, type);
				viewHolder = (ViewHolder) convertView.getTag();
			}
		}
		bindView(viewHolder, messageInfo, position);
		return convertView;
	}

	private void bindView(final ViewHolder viewHolder, final MessageInfo messageInfo, final int position) {
		final boolean isMyself = messageInfo.from.equals(mLogin.uid) ? true : false;
		notHideViews(viewHolder, messageInfo.fileType);
		if (messageInfo.fileType == MessageType.LOCAL_ANONY_FALSE) {
			viewHolder.tvChatTime.setText(R.string.tip_change_normal_voice_mode);
			return;
		} else if (messageInfo.fileType == MessageType.LOCAL_ANONY_TRUE) {
			viewHolder.tvChatTime.setText(R.string.tip_change_weired_voice_mode);
			return;
		}
		if (isMyself) {
			View resendView = ((ViewHolderRight) viewHolder).ivResend;
			if (MessageState.STATE_SEND_FAILED == messageInfo.sendState) {
				resendView.setVisibility(View.VISIBLE);
			} else {
				resendView.setVisibility(View.GONE);
			}
			resendView.setTag(messageInfo);
			resendView.setOnClickListener(resendClickListener);
		}
		onBindView(viewHolder, messageInfo);
		displayTime(viewHolder.tvChatTime, position);

		ImageLoaderUtil.displayImage(messageInfo.headImgUrl, viewHolder.ivHead, R.drawable.default_header);

		viewHolder.ivVoice.setLayoutParams(getVoiceViewLengthParams(
				(android.widget.LinearLayout.LayoutParams) viewHolder.ivVoice.getLayoutParams(), messageInfo));
		viewHolder.layoutTextVoiceHolder.setOnClickListener(null);
		switch (messageInfo.fileType) {
		case MessageType.TEXT:
			if (messageInfo.mIsShide == MessageState.MESSAGE_NOT_SHIDE) {
				viewHolder.tvText.setText(MyTextUtils.addHttpLinks(messageInfo.content));
				viewHolder.tvText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			} else {
				viewHolder.tvText.setText(mContext.getString(R.string.shide_msg_prompt));
			}
			break;
		case MessageType.PICTURE:
			final String path = messageInfo.imgUrlS.trim();
			int width = (int) (MyUtils.dip2px(mContext, messageInfo.imgWidth) * 0.7);
			int height = (int) (MyUtils.dip2px(mContext, messageInfo.imgHeight) * 0.7);
			viewHolder.ivPhoto.getLayoutParams().height = height;
			viewHolder.ivPhoto.getLayoutParams().width = width;
			viewHolder.ivPhotoCover.getLayoutParams().height = height;
			viewHolder.ivPhotoCover.getLayoutParams().width = width;
			ImageLoaderUtil.displayImageByProgress(path, viewHolder.ivPhoto, options, viewHolder.wiatProgressBar);
			if (path.startsWith("http://")) {
				ImageLoaderUtil.displayImageByProgress(path, viewHolder.ivPhoto, options, viewHolder.wiatProgressBar);
			} else {
				ImageLoaderUtil.displayImageByProgress("file://" + path, viewHolder.ivPhoto, options,
						viewHolder.wiatProgressBar);
				showWaitProgressBar(messageInfo, viewHolder);
				if (messageInfo.sendState == MessageState.STATE_SEND_FAILED) {
				}
			}
			viewHolder.layoutPicHolder.setTag(R.id.dy_photo_position, position);
			viewHolder.layoutPicHolder.setOnClickListener(photoClickListener);
			break;
		case MessageType.VOICE:
			if (mCurrentMode == MODEL_VOICE) {
				viewHolder.tvVoiceLength.setText(messageInfo.voiceTime + "''");
				showWaitProgressBar(messageInfo, viewHolder);
				viewHolder.layoutTextVoiceHolder.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mPlayerWrapper.start(messageInfo);
					}
				});

			} else {
				notHideViews(viewHolder, MessageType.TEXT);
				viewHolder.wiatProgressBar.setVisibility(View.GONE);
				viewHolder.tvText.setText(messageInfo.content);
				onBindView(viewHolder, messageInfo);
			}
			AnimationDrawable drawable = (AnimationDrawable) viewHolder.ivVoice.getDrawable();
			if (mPlayerWrapper.isPlay() && mPlayerWrapper.getMessageTag().equals(messageInfo.tag)) {
				drawable.start();
			} else {
				drawable.stop();
				drawable.selectDrawable(0);
			}
			break;
		default:
			break;
		}
	}

	protected abstract void onBindView(ViewHolder viewHolder, MessageInfo messageInfo);

	private void showWaitProgressBar(MessageInfo messageInfo, ViewHolder viewHolder) {
		if (MessageState.STATE_SENDING == messageInfo.sendState) {
			viewHolder.wiatProgressBar.setVisibility(View.VISIBLE);
		} else {
			viewHolder.wiatProgressBar.setVisibility(View.GONE);
		}
	}

	private OnClickListener photoClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(mContext, ShowImagesActivity.class);
			intent.putExtra("msgList", (Serializable) mData);
			intent.putExtra("position", (int) v.getTag(R.id.dy_photo_position));
			mContext.startActivity(intent);
		}
	};

	private void displayTime(TextView textView, int position) {
		long beforeTime = 0;
		MessageInfo messageInfo = (MessageInfo) getItem(position);
		if (position != 0) {
			MessageInfo messageInfoBefore = (MessageInfo) getItem(position - 1);
			beforeTime = messageInfoBefore.time;
		}
		if (messageInfo.time - beforeTime < 5 * 60 * 1000) {
			textView.setVisibility(View.GONE);
		} else {
			String time1 = DateUtil.getCreateTime(messageInfo.time);
			if (!TextUtils.isEmpty(time1)) {
				textView.setVisibility(View.VISIBLE);
				textView.setText(time1);
			} else {
				textView.setVisibility(View.GONE);
			}
		}
	}

	private void notHideViews(ViewHolder viewHolder, int which) {
		viewHolder.layoutPicHolder.setVisibility(View.GONE);

		viewHolder.layoutTextVoiceHolder.setVisibility(View.GONE);
		viewHolder.tvText.setVisibility(View.GONE);
		viewHolder.tvVoiceLength.setVisibility(View.GONE);
		viewHolder.ivVoice.setVisibility(View.GONE);
		viewHolder.wiatProgressBar.setVisibility(View.GONE);
		viewHolder.tvUserName.setVisibility(View.VISIBLE);
		viewHolder.tvUserName.setVisibility(View.VISIBLE);
		viewHolder.ivHead.setVisibility(View.VISIBLE);
		viewHolder.msgInfoLayout.setVisibility(View.VISIBLE);
		viewHolder.mCountLayout.setVisibility(View.VISIBLE);

		switch (which) {
		case MessageType.TEXT:
			viewHolder.layoutTextVoiceHolder.setVisibility(View.VISIBLE);
			viewHolder.tvText.setVisibility(View.VISIBLE);
			break;
		case MessageType.PICTURE:
			viewHolder.layoutPicHolder.setVisibility(View.VISIBLE);
			break;
		case MessageType.VOICE:
			viewHolder.layoutTextVoiceHolder.setVisibility(View.VISIBLE);
			viewHolder.ivVoice.setVisibility(View.VISIBLE);
			viewHolder.tvVoiceLength.setVisibility(View.VISIBLE);
			break;
		case MessageType.LOCAL_ANONY_FALSE:
		case MessageType.LOCAL_ANONY_TRUE:
			viewHolder.msgInfoLayout.setVisibility(View.GONE);
			viewHolder.tvUserName.setVisibility(View.GONE);
			viewHolder.tvUserName.setVisibility(View.GONE);
			viewHolder.ivHead.setVisibility(View.GONE);
			viewHolder.mCountLayout.setVisibility(View.GONE);
			viewHolder.tvChatTime.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	private LinearLayout.LayoutParams getVoiceViewLengthParams(LinearLayout.LayoutParams lp, MessageInfo commentInfo) {
		final int MAX_SECOND = 10;
		final int MIN_SECOND = 2;
		int length = commentInfo.voiceTime;
		float max = mContext.getResources().getDimension(R.dimen.voice_max_length_comment);
		float min = mContext.getResources().getDimension(R.dimen.voice_min_length_comment);
		int width = (int) min;
		if (length >= MIN_SECOND && length <= MAX_SECOND) {
			width += (length - MIN_SECOND) * (int) ((max - min) / (MAX_SECOND - MIN_SECOND));
		} else if (length > MAX_SECOND) {
			width = (int) max;
		}
		if (lp == null) {
			lp = new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		} else {
			lp.width = width;
		}
		return lp;
	}

	private View inflateItemView(View convertView, int type) {
		ViewHolder viewHolder;
		if (TYPE_LEFT == type) {
			convertView = mInflater.inflate(R.layout.chat_talk_left_new, null);
			viewHolder = ViewHolderLeft.getInstance(convertView);
			convertView.setTag(viewHolder);
		} else {
			convertView = mInflater.inflate(R.layout.chat_talk_right_new, null);
			viewHolder = ViewHolderRight.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
		return convertView;
	}

	static class ViewHolderLeft extends ViewHolder {

		public static ViewHolderLeft getInstance(View view) {
			ViewHolderLeft viewHolderLeft = new ViewHolderLeft();
			return (ViewHolderLeft) getInstance(view, viewHolderLeft);
		}
	}

	static class ViewHolderRight extends ViewHolder {
		ImageView ivResend;

		public static ViewHolderRight getInstance(View view) {
			ViewHolderRight viewHolderRight = new ViewHolderRight();
			viewHolderRight.ivResend = (ImageView) view.findViewById(R.id.iv_chat_resend_icon);
			return (ViewHolderRight) getInstance(view, viewHolderRight);
		}
	}

	static class ViewHolder {
		int flag = 0; // 1 好友 0 自己
		TextView tvChatTime, tvText, tvVoiceLength, tvUserName;
		ImageView ivHead, ivPhoto, ivPhotoCover, ivVoice, ivZan;
		ProgressBar wiatProgressBar;

		View msgInfoLayout;
		View layoutPicHolder, layoutTextVoiceHolder;

		LinearLayout mCountLayout;
		TextView mFavoriteCountView, mCommentCountView, mAgreeCountView, mMoreCommentBtn;

		public static Object getInstance(View view, ViewHolder holder) {
			holder.msgInfoLayout = view.findViewById(R.id.layout_msg_content);
			holder.tvChatTime = (TextView) view.findViewById(R.id.tv_chat_talk_time);
			holder.tvText = (TextView) view.findViewById(R.id.iv_chat_text);

			holder.ivHead = (ImageView) view.findViewById(R.id.iv_chat_talk_img_head);
			holder.tvUserName = (TextView) view.findViewById(R.id.tv_user_name);

			holder.ivPhoto = (ImageView) view.findViewById(R.id.iv_chat_photo);
			holder.ivPhotoCover = (ImageView) view.findViewById(R.id.iv_chat_photo_cover);
			holder.ivVoice = (ImageView) view.findViewById(R.id.iv_chat_voice);
			holder.tvVoiceLength = (TextView) view.findViewById(R.id.tv_chat_voice_time_length);

			holder.layoutPicHolder = view.findViewById(R.id.layout_msg_pic_holder);
			holder.layoutTextVoiceHolder = view.findViewById(R.id.layout_msg_text_voice_holder);

			holder.wiatProgressBar = (ProgressBar) view.findViewById(R.id.pb_chat_progress);

			holder.mFavoriteCountView = (TextView) view.findViewById(R.id.favoritecount);
			holder.mCommentCountView = (TextView) view.findViewById(R.id.commentcount);
			holder.mAgreeCountView = (TextView) view.findViewById(R.id.agreecount);
			holder.mCountLayout = (LinearLayout) view.findViewById(R.id.countlayout);
			return holder;
		}
	}

	public void setCurrentMode(int mode) {
		mCurrentMode = mode;
		mPlayerWrapper.setCurrentMode(mode);
	}

	public int getCurrentMode() {
		return mCurrentMode;
	}

	private class PlayCallback extends MediaUIHeper.PlayCallback {

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			if (((ChatBaseActivity) mContext).isModeInCall) {
				((ChatMainActivity) mContext).showVoiceModeToastAnimation();
			}
			notifyDataSetChanged();
		}

		@Override
		public void onStop(boolean stopAutomatic) {
			// TODO Auto-generated method stub
			if (stopAutomatic) {
				notifyDataSetChanged();// 通知播放动画
			} else {
				notifyDataSetChanged();
			}
		}
	}

	PopupWindow actionWindow;

	protected void showActionWindow(View anchor, View v, OnClickListener listener) {
		MessageInfo messageInfo = (MessageInfo) anchor.getTag();
		actionWindow = new PopupWindow(v, LayoutParams.WRAP_CONTENT, MyUtils.dip2px(mContext, 40));
		actionWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

		if (messageInfo.from.equals(DamiCommon.getUid(mContext))) {
			actionWindow.setAnimationStyle(R.style.window_slide_right);
		} else {
			actionWindow.setAnimationStyle(R.style.window_slide_left);
		}
		actionWindow.setOutsideTouchable(true);
		actionWindow.setFocusable(true);
		ViewUtil.measure(v);
		int windowHeightPadding = (v.getMeasuredHeight() + anchor.getMeasuredHeight())
				+ FeatureFunction.dip2px(mContext, 5);
		int windowWidthPadding = (anchor.getMeasuredWidth() - v.getMeasuredWidth()) / 2;

		if (actionWindow.isShowing()) {
			actionWindow.dismiss();
			return;
		}
		actionWindow.showAsDropDown(anchor, windowWidthPadding, -windowHeightPadding);
	}

	protected void closePopupWindow() {
		if (actionWindow.isShowing()) {
			actionWindow.dismiss();
			return;
		}
	}

}

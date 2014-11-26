package com.gaopai.guiren.adapter;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.ShowImagesActivity;
import com.gaopai.guiren.activity.chat.ChatBaseActivity;
import com.gaopai.guiren.activity.chat.ChatMainActivity;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageState;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.media.MediaUIHeper;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.MyTextUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public abstract class BaseChatAdapter extends BaseAdapter {
	protected Context mContext;
	private User mLogin;
	private final LayoutInflater mInflater;
	private DisplayImageOptions options;

	private List<MessageInfo> mData;

	private static final int TYPE_COUNT = 2;
	private static final int TYPE_RIGHT = 0;
	private static final int TYPE_LEFT = 1;

	public final static int MODEL_VOICE = 0;
	public final static int MODE_TEXT = 1;
	private int mCurrentMode = MODEL_VOICE;

	private SpeexPlayerWrapper mPlayerWrapper;
	private int palyedPosition = -1;

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
		if (isMyself) {
			View resendView = ((ViewHolderRight) viewHolder).ivResend;
			if (MessageState.STATE_SEND_FAILED == messageInfo.sendState) {
				resendView.setVisibility(View.VISIBLE);
			} else {
				resendView.setVisibility(View.GONE);
			}
			resendView.setTag(messageInfo);
			resendView.setOnClickListener(resendClickListener);
		} else {
			if (messageInfo.fileType == MessageType.VOICE && messageInfo.isReadVoice == MessageState.VOICE_NOT_READED) {
				((ViewHolderLeft) viewHolder).ivVoiceUnread.setVisibility(View.VISIBLE);
			} else {
				((ViewHolderLeft) viewHolder).ivVoiceUnread.setVisibility(View.GONE);
			}
		}
		
		onBindView(viewHolder, messageInfo);
		displayTime(viewHolder.tvChatTime, position);
		
		if (!TextUtils.isEmpty(messageInfo.headImgUrl)) {
			viewHolder.ivHead.setTag(messageInfo.headImgUrl);
			ImageLoaderUtil.displayImage(messageInfo.headImgUrl, viewHolder.ivHead);
		}

		notHideViews(viewHolder, messageInfo.fileType);
		viewHolder.ivVoice.setLayoutParams(getVoiceViewLengthParams(messageInfo));
		viewHolder.msgInfoLayout.setOnClickListener(null);
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
			ImageLoaderUtil.displayImageByProgress(path, viewHolder.ivPhoto, options, viewHolder.wiatProgressBar);
			if (path.startsWith("http://")) {
				viewHolder.wiatProgressBar.setVisibility(View.VISIBLE);
				ImageLoaderUtil.displayImageByProgress(path, viewHolder.ivPhoto, options, viewHolder.wiatProgressBar);
				viewHolder.ivPhoto.setTag(path);

			} else {
				ImageLoaderUtil.displayImageByProgress("file://" + path, viewHolder.ivPhoto, options,
						viewHolder.wiatProgressBar);
				showWaitProgressBar(messageInfo, viewHolder);
				if (messageInfo.sendState == MessageState.STATE_SEND_FAILED) {
				}
			}
			viewHolder.ivPhoto.setTag(position);
			viewHolder.ivPhoto.setOnClickListener(photoClickListener);
			break;
		case MessageType.VOICE:
			if (mCurrentMode == MODEL_VOICE) {
				viewHolder.tvVoiceLength.setText(messageInfo.voiceTime + "''");
				showWaitProgressBar(messageInfo, viewHolder);
				viewHolder.msgInfoLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						palyedPosition = position;
						mPlayerWrapper.start(messageInfo);
					}
				});

			} else {
				notHideViews(viewHolder, MessageType.TEXT);
				viewHolder.wiatProgressBar.setVisibility(View.GONE);
				viewHolder.tvText.setText(messageInfo.content);
			}
			AnimationDrawable drawable = (AnimationDrawable) viewHolder.ivVoice.getDrawable();
			if (mPlayerWrapper.isPlay() && position == palyedPosition) {
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
	
	protected abstract void onBindView(ViewHolder viewHolder, MessageInfo messageInfo) ;

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
			intent.putExtra("position", (int) v.getTag());
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
			textView.setVisibility(View.VISIBLE);
			textView.setText(FeatureFunction.getCreateTime(messageInfo.time));
		}
	}

	private void notHideViews(ViewHolder viewHolder, int which) {
		viewHolder.ivPhoto.setVisibility(View.GONE);
		viewHolder.tvText.setVisibility(View.GONE);
		viewHolder.tvVoiceLength.setVisibility(View.GONE);
		viewHolder.ivVoice.setVisibility(View.GONE);
		viewHolder.wiatProgressBar.setVisibility(View.GONE);
		switch (which) {
		case MessageType.TEXT:
			viewHolder.tvText.setVisibility(View.VISIBLE);
			break;
		case MessageType.PICTURE:
			viewHolder.ivPhoto.setVisibility(View.VISIBLE);
			break;
		case MessageType.VOICE:
			viewHolder.ivVoice.setVisibility(View.VISIBLE);
			viewHolder.tvVoiceLength.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	private RelativeLayout.LayoutParams getVoiceViewLengthParams(MessageInfo commentInfo) {
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
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
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
		ImageView ivVoiceUnread;

		public static ViewHolderLeft getInstance(View view) {
			ViewHolderLeft viewHolderLeft = new ViewHolderLeft();
			viewHolderLeft.ivVoiceUnread = (ImageView) view.findViewById(R.id.iv_unread_voice_icon);
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
		TextView tvChatTime, tvText, tvVoiceLength, tvUserName, tvShide;
		ImageView ivHead, ivPhoto, ivVoice, ivZan;
		ProgressBar wiatProgressBar;

		RelativeLayout msgInfoLayout, msgLayout;
		LinearLayout mCountLayout;
		TextView mFavoriteCountView, mCommentCountView, mAgreeCountView, mMoreCommentBtn;
		
		public static Object getInstance(View view, ViewHolder holder) {
			holder.msgInfoLayout = (RelativeLayout) view.findViewById(R.id.rl_msg_info_holder);
			holder.msgLayout = (RelativeLayout) view.findViewById(R.id.rl_msg_holder);
			holder.tvShide = (TextView) view.findViewById(R.id.tv_shide);
			holder.tvChatTime = (TextView) view.findViewById(R.id.tv_chat_talk_time);
			holder.tvText = (TextView) view.findViewById(R.id.iv_chat_text);

			holder.ivHead = (ImageView) view.findViewById(R.id.iv_chat_talk_img_head);
			holder.ivPhoto = (ImageView) view.findViewById(R.id.iv_chat_photo);
			holder.ivVoice = (ImageView) view.findViewById(R.id.iv_chat_voice);

			holder.wiatProgressBar = (ProgressBar) view.findViewById(R.id.pb_chat_progress);
			holder.tvVoiceLength = (TextView) view.findViewById(R.id.tv_chat_voice_time_length);

			holder.tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
			
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
			if (((ChatBaseActivity)mContext).isModeInCall) {
				((ChatMainActivity) mContext).showVoiceModeToastAnimation();
			}
			notifyDataSetChanged();
		}

		@Override
		public void onStop(boolean stopAutomatic) {
			// TODO Auto-generated method stub
			if (stopAutomatic) {
				int nextPosition = palyedPosition + 1;
				if (nextPosition < getCount()) {
					MessageInfo messageInfo = mData.get(nextPosition);
					if (messageInfo.fileType == MessageType.VOICE
							&& messageInfo.isReadVoice == MessageState.VOICE_NOT_READED) {
						palyedPosition = nextPosition;
						mPlayerWrapper.start(messageInfo);
						return;
					}
				}
				notifyDataSetChanged();// 通知播放动画
			} else {
				notifyDataSetChanged();
			}
		}
	}

}

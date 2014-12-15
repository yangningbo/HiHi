package com.gaopai.guiren.support.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.ShowImagesActivity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.media.MediaUIHeper.PlayCallback;
import com.gaopai.guiren.media.MediaUIHeper;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.media.SpeexPlayerWrapper.OnDownLoadCallback;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyUtils;

public class ChatMsgUIHelper {

	private SpeexPlayerWrapper mPlayerWrapper;
	private Context mContext;

	public ChatMsgUIHelper(Context context) {
		mContext = context;
		mPlayerWrapper = new SpeexPlayerWrapper(mContext, new OnDownLoadCallback() {
			@Override
			public void onSuccess(MessageInfo messageInfo) {
				// TODO Auto-generated method stub
				downVoiceSuccess(messageInfo);
			}
		});
	}
	
	/**
	 * 下载成功后修改消息状态，更新数据库并播放声音
	 * 
	 * @param msg
	 * @param type
	 */
	private void downVoiceSuccess(final MessageInfo msg) {
		if (mPlayerWrapper.getMessageTag().equals(msg.tag)) {
			mPlayerWrapper.start(msg);
//			mAdapter.notifyDataSetChanged();
		}
	}
	
	//should call this before use mPlayerWrapper
	public void setPlayedCallback(PlayCallback playCallback) {
		mPlayerWrapper.setPlayCallback(playCallback);
	}

	public LinearLayout.LayoutParams getVoiceViewLengthParams(LinearLayout.LayoutParams lp, MessageInfo commentInfo) {
		final int MAX_SECOND = 20;
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

	public static class BaseViewHolder {
		public TextView tvText, tvVoiceLength, tvUserName;
		public ImageView ivHead, ivPhoto, ivPhotoCover, ivVoice;
		public ProgressBar wiatProgressBar;
		public View msgInfoLayout;
		public View layoutPicHolder, layoutTextVoiceHolder;

		public static Object getBaseInstance(View view, BaseViewHolder holder) {
			holder.msgInfoLayout = view.findViewById(R.id.layout_msg_content);
			holder.tvText = (TextView) view.findViewById(R.id.iv_chat_text);

			holder.ivHead = (ImageView) view.findViewById(R.id.iv_chat_talk_img_head);
			holder.ivPhoto = (ImageView) view.findViewById(R.id.iv_chat_photo);
			holder.ivPhotoCover = (ImageView) view.findViewById(R.id.iv_chat_photo_cover);
			holder.ivVoice = (ImageView) view.findViewById(R.id.iv_chat_voice);

			holder.layoutPicHolder = view.findViewById(R.id.layout_msg_pic_holder);
			holder.layoutTextVoiceHolder = view.findViewById(R.id.layout_msg_text_voice_holder);

			holder.wiatProgressBar = (ProgressBar) view.findViewById(R.id.pb_chat_progress);
			holder.tvVoiceLength = (TextView) view.findViewById(R.id.tv_chat_voice_time_length);
			holder.tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
			return holder;
		}
	}
	
	public void bindBaseView(BaseViewHolder viewHolder, MessageInfo messageInfo) {
		if (!TextUtils.isEmpty(messageInfo.headImgUrl)) {
			ImageLoaderUtil.displayImage(messageInfo.headImgUrl, viewHolder.ivHead);
		} 
		viewHolder.tvUserName.setText(messageInfo.displayname);
		notHideViews(viewHolder, messageInfo.fileType);
		viewHolder.ivVoice.setLayoutParams(getVoiceViewLengthParams(
				(android.widget.LinearLayout.LayoutParams) viewHolder.ivVoice.getLayoutParams(), messageInfo));
		viewHolder.msgInfoLayout.setOnClickListener(null);
		switch (messageInfo.fileType) {
		case MessageType.TEXT:
			viewHolder.tvText.setText(MyTextUtils.addHttpLinks(messageInfo.content));
			viewHolder.tvText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			break;
		case MessageType.PICTURE:
			final String path = messageInfo.imgUrlS.trim();
			int width = (int) (MyUtils.dip2px(mContext, messageInfo.imgWidth) * 0.7);
			int height = (int) (MyUtils.dip2px(mContext, messageInfo.imgHeight) * 0.7);
			viewHolder.ivPhoto.getLayoutParams().height = height;
			viewHolder.ivPhoto.getLayoutParams().width = width;
			viewHolder.ivPhotoCover.getLayoutParams().height = height;
			viewHolder.ivPhotoCover.getLayoutParams().width = width;

			ImageLoaderUtil.displayImage(path, viewHolder.ivPhoto);
			if (path.startsWith("http://")) {
				ImageLoaderUtil.displayImage(path, viewHolder.ivPhoto);
			}
			viewHolder.ivPhoto.setTag(messageInfo);
			viewHolder.ivPhoto.setOnClickListener(photoClickListener);
			break;
		case MessageType.VOICE:
			viewHolder.tvVoiceLength.setText(messageInfo.voiceTime + "''");
			viewHolder.msgInfoLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					palyedPosition = position;
//					mPlayerWrapper.start(messageInfo);
				}
			});

			AnimationDrawable drawable = (AnimationDrawable) viewHolder.ivVoice.getDrawable();
//			if (mPlayerWrapper.isPlay() && position == palyedPosition) {
//				drawable.start();
//			} else {
//				drawable.stop();
//				drawable.selectDrawable(0);
//			}
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
	
	private void notHideViews(BaseViewHolder viewHolder, int which) {
		viewHolder.layoutPicHolder.setVisibility(View.GONE);
		viewHolder.layoutTextVoiceHolder.setVisibility(View.GONE);
		viewHolder.tvText.setVisibility(View.GONE);
		viewHolder.tvVoiceLength.setVisibility(View.GONE);
		viewHolder.ivVoice.setVisibility(View.GONE);
		viewHolder.wiatProgressBar.setVisibility(View.GONE);
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
		default:
			break;
		}
	}

}

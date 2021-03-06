package com.gaopai.guiren.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatMessageActivity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.media.SpeexPlayerWrapper;

public class PrivateChatAdapter extends BaseChatAdapter implements View.OnClickListener {
	public PrivateChatAdapter(Context context, SpeexPlayerWrapper playerWrapper, List<MessageInfo> messageInfos) {
		super(context, playerWrapper, messageInfos);
	}

	@Override
	protected void onBindView(ViewHolder viewHolder, MessageInfo messageInfo) {
		// TODO Auto-generated method stub
		viewHolder.tvUserName.setVisibility(View.GONE);
		viewHolder.mCountLayout.setVisibility(View.GONE);

		viewHolder.layoutTextVoiceHolder.setTag(messageInfo);
		if (messageInfo.fileType == MessageType.PICTURE) {
			viewHolder.layoutTextVoiceHolder.setOnLongClickListener(null);
			return;
		}
		viewHolder.layoutTextVoiceHolder.setOnLongClickListener(showMoreWindowClickListener);
	}

	private View.OnLongClickListener showMoreWindowClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			showActionWindow(v);
			return true;
		}
	};

	private void showActionWindow(View anchor) {
		MessageInfo messageInfo = (MessageInfo) anchor.getTag();
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		ViewGroup v = (ViewGroup) mInflater.inflate(R.layout.popup_chat_private_action, null);
		
		if (messageInfo.fileType == MessageType.VOICE && mCurrentMode == MODE_VOICE) {
			v.getChildAt(1).setVisibility(View.GONE);
			v.getChildAt(2).setVisibility(View.GONE);
		}

		Button copy = (Button) v.findViewById(R.id.btn_copy);
		copy.setTag(messageInfo.content);
		copy.setOnClickListener(this);

		TextView earPhone = (TextView) v.findViewById(R.id.btn_ear_phone);
		if (((BaseActivity) mContext).isModeInCall) {
			earPhone.setText(R.string.mode_in_speaker);
		} else {
			earPhone.setText(R.string.mode_in_call);
		}
		earPhone.setTag(messageInfo);
		earPhone.setOnClickListener(this);
		showActionWindow(anchor, v, this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_ear_phone:
			getActivity().changePlayMode();
			closePopupWindow();
			break;
		case R.id.btn_copy:
			ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Activity.CLIPBOARD_SERVICE);
			cmb.setText((String) v.getTag());
			getActivity().showToast(R.string.copy_successfull);
			closePopupWindow();
			break;

		default:
			break;
		}

	}

	private ChatMessageActivity getActivity() {
		return (ChatMessageActivity) mContext;
	}

}

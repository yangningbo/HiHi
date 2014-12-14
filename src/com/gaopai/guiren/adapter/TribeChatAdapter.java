package com.gaopai.guiren.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.umeng.socialize.net.t;

public class TribeChatAdapter extends BaseChatAdapter implements View.OnClickListener {

	public TribeChatAdapter(Context context, SpeexPlayerWrapper playerWrapper, List<MessageInfo> messageInfos) {
		super(context, playerWrapper, messageInfos);
	}

	@Override
	protected void onBindView(ViewHolder viewHolder, MessageInfo messageInfo) {
		// TODO Auto-generated method stub
		viewHolder.tvUserName.setText(messageInfo.displayname);
		bindMessageBottomBarView(viewHolder, messageInfo);
	}

	private void bindMessageBottomBarView(ViewHolder viewHolder, final MessageInfo messageInfo) {
		viewHolder.layoutTextVoiceHolder.setTag(messageInfo);
		viewHolder.layoutTextVoiceHolder.setOnLongClickListener(showMoreWindowClickListener);
		
		
		viewHolder.layoutPicHolder.setTag(messageInfo);
		viewHolder.layoutPicHolder.setOnLongClickListener(showMoreWindowClickListener);
		if (messageInfo.agreeCount + messageInfo.commentCount + messageInfo.favoriteCount == 0) {
			viewHolder.mFavoriteCountView.setVisibility(View.GONE);
			viewHolder.mCommentCountView.setVisibility(View.GONE);
			viewHolder.mAgreeCountView.setVisibility(View.GONE);
		} else {
			viewHolder.mFavoriteCountView.setVisibility(View.VISIBLE);
			viewHolder.mCommentCountView.setVisibility(View.VISIBLE);
			viewHolder.mAgreeCountView.setVisibility(View.VISIBLE);
		}
		viewHolder.mFavoriteCountView.setText("已赞" + String.valueOf(messageInfo.agreeCount));
		viewHolder.mCommentCountView.setText("已评" + String.valueOf(messageInfo.commentCount));
		viewHolder.mAgreeCountView.setText("已藏" + String.valueOf(messageInfo.favoriteCount));
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
		View v = mInflater.inflate(R.layout.popup_chat_tribe_action, null);

		Button zan = (Button) v.findViewById(R.id.btn_zan);
		zan.setTag(messageInfo);
		zan.setOnClickListener(this);

		if (messageInfo.isAgree == 1) {
			zan.setText(R.string.cancel_zan);
		} else {
			zan.setText(R.string.zan);
		}

		View earPhone = v.findViewById(R.id.btn_ear_phone);
		earPhone.setTag(messageInfo);
		earPhone.setOnClickListener(this);

		View favorite = v.findViewById(R.id.btn_favorite);
		favorite.setTag(messageInfo);
		favorite.setOnClickListener(this);

		View more = v.findViewById(R.id.btn_more);
		more.setTag(messageInfo);
		more.setOnClickListener(this);
		showActionWindow(anchor, v, this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_more:
			getActivity().showItemLongClickDialog((MessageInfo) v.getTag());
			closePopupWindow();
			break;
		case R.id.btn_ear_phone:
			getActivity().changePlayMode();
			closePopupWindow();
			break;
		case R.id.btn_zan:
			getActivity().zanMessage((MessageInfo) v.getTag());
			closePopupWindow();
			break;
		case R.id.btn_favorite:
			getActivity().favoriteMessage((MessageInfo) v.getTag());
			closePopupWindow();
			break;

		default:
			break;
		}
	}

	private ChatTribeActivity getActivity() {
		return (ChatTribeActivity) mContext;
	}

}

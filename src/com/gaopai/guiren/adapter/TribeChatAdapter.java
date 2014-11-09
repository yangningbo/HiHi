package com.gaopai.guiren.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;

import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.BaseChatAdapter.ViewHolder;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.media.SpeexPlayerWrapper;

public class TribeChatAdapter extends BaseChatAdapter {

	public TribeChatAdapter(Context context, SpeexPlayerWrapper playerWrapper, List<MessageInfo> messageInfos) {
		super(context, playerWrapper, messageInfos);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onBindView(ViewHolder viewHolder, MessageInfo messageInfo) {
		// TODO Auto-generated method stub
		viewHolder.tvUserName.setText(messageInfo.displayname);
		bindMessageBottomBarView(viewHolder, messageInfo);
	}
	
	private void bindMessageBottomBarView(ViewHolder viewHolder, MessageInfo messageInfo) {
		if (messageInfo.favoriteCount != 0) {
			viewHolder.mFavoriteCountLayout.setVisibility(View.VISIBLE);
			viewHolder.mFavoriteCountView.setText(String.valueOf(messageInfo.favoriteCount));
		} else {
			viewHolder.mFavoriteCountLayout.setVisibility(View.GONE);
		}

		if (messageInfo.commentCount != 0) {
			viewHolder.mCommentCountLayout.setVisibility(View.VISIBLE);
			viewHolder.mCommentCountView.setText(String.valueOf(messageInfo.commentCount));
		} else {
			viewHolder.mCommentCountLayout.setVisibility(View.GONE);
		}

		if (messageInfo.agreeCount != 0) {
			viewHolder.mAgreeCountLayout.setVisibility(View.VISIBLE);
			viewHolder.mAgreeCountView.setText(String.valueOf(messageInfo.agreeCount));
			if (messageInfo.isAgree == 0) {
				viewHolder.ivZan.setImageResource(R.drawable.zan_btn);
			} else {
				viewHolder.ivZan.setImageResource(R.drawable.zan_btn_on);
			}
		} else {
			viewHolder.mAgreeCountLayout.setVisibility(View.GONE);
		}
	}
	
	
	
	

}

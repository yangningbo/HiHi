package com.gaopai.guiren.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;

import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.media.SpeexPlayerWrapper;

public class PrivateChatAdapter extends BaseChatAdapter {
	public PrivateChatAdapter(Context context, SpeexPlayerWrapper playerWrapper, List<MessageInfo> messageInfos) {
		super(context, playerWrapper, messageInfos);
	}

	@Override
	protected void onBindView(ViewHolder viewHolder, MessageInfo messageInfo) {
		// TODO Auto-generated method stub
		viewHolder.tvUserName.setVisibility(View.GONE);
		viewHolder.mCountLayout.setVisibility(View.GONE);
	}
	
	
	
}

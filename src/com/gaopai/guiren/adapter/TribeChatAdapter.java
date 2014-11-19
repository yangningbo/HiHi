package com.gaopai.guiren.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.utils.ViewUtil;
import com.umeng.socialize.net.t;

public class TribeChatAdapter extends BaseChatAdapter implements View.OnClickListener {

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

	private void bindMessageBottomBarView(ViewHolder viewHolder, final MessageInfo messageInfo) {
		viewHolder.msgInfoLayout.setTag(messageInfo);
		viewHolder.msgInfoLayout.setOnLongClickListener(showMoreWindowClickListener);
		viewHolder.mFavoriteCountView.setText("已赞"+String.valueOf(messageInfo.favoriteCount));
		viewHolder.mCommentCountView.setText("已评"+String.valueOf(messageInfo.commentCount));
		viewHolder.mAgreeCountView.setText("已藏"+String.valueOf(messageInfo.agreeCount));
	}
	
	private View.OnLongClickListener showMoreWindowClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			showActionWindow(v);
			return true;
		}
	};
	
	PopupWindow actionWindow;
	private void showActionWindow(View anchor) {
		MessageInfo messageInfo = (MessageInfo) anchor.getTag();
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		View v = mInflater.inflate(R.layout.popup_chat_tribe_action, null);
		
		View zan = v.findViewById(R.id.btn_zan);
		zan.setTag(messageInfo);
		zan.setOnClickListener(this);
		
		View earPhone = v.findViewById(R.id.btn_ear_phone);
		earPhone.setTag(messageInfo);
		earPhone.setOnClickListener(this);
		
		View favorite = v.findViewById(R.id.btn_favorite);
		favorite.setTag(messageInfo);
		favorite.setOnClickListener(this);
		
		View more = v.findViewById(R.id.btn_more);
		more.setTag(messageInfo);
		more.setOnClickListener(this);
		
		actionWindow = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		actionWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		actionWindow.setAnimationStyle(R.style.window_slide_right);
		actionWindow.setOutsideTouchable(true);
		actionWindow.setFocusable(true);
		ViewUtil.measure(v);
		int windowHeightPadding = (v.getMeasuredHeight() + anchor.getMeasuredHeight())+FeatureFunction.dip2px(mContext, 5);
		int windowWidthPadding = (anchor.getMeasuredWidth() - v.getMeasuredWidth()) / 2;

		if (actionWindow.isShowing()) {
			actionWindow.dismiss();
			return;
		}
		actionWindow.showAsDropDown(anchor, windowWidthPadding, -windowHeightPadding);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_more:
			getActivity().showItemLongClickDialog((MessageInfo) v.getTag());
			actionWindow.dismiss();
			break;
		case R.id.btn_ear_phone:
			getActivity().changePlayMode();
			actionWindow.dismiss();
			break;
		case R.id.btn_zan:
			getActivity().zanMessage((MessageInfo) v.getTag());
			actionWindow.dismiss();
			break;
		case R.id.btn_favorite:
			getActivity().favoriteMessage((MessageInfo) v.getTag());
			actionWindow.dismiss();
			break;

		default:
			break;
		}
		
	}
	
	private ChatTribeActivity getActivity() {
		return (ChatTribeActivity) mContext;
	}


}

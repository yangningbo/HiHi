package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.ConversationBean;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.MyUtils;

public class NotificationAdapter extends BaseAdapter {
	private final LayoutInflater mInflater;
	private List<ConversationBean> list = new ArrayList<ConversationBean>();
	private Context mContext;

	public NotificationAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public void addAll(List<ConversationBean> o, boolean hasDami) {
		list.clear();
		if (!hasDami) {
			ConversationBean conversationBean = new ConversationBean();
			conversationBean.unreadcount = 0;
			list.add(conversationBean);
		}
		list.addAll(o);
		notifyDataSetChanged();
	}

	public void clear() {
		list.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_notification, null);
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tvInfo = (TextView) convertView.findViewById(R.id.tv_info);
			holder.ivHeader = (ImageView) convertView.findViewById(R.id.iv_header);
			holder.tvMsgCount = (TextView) convertView.findViewById(R.id.tv_message_count);
			holder.ivTitleIcon = (ImageView) convertView.findViewById(R.id.iv_icon_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		ConversationBean bean = list.get(position);
		
		if (bean.unreadcount > 0) {
			holder.tvMsgCount.setVisibility(View.VISIBLE);
			holder.tvMsgCount.setText(String.valueOf(bean.unreadcount));
		} else {
			holder.tvMsgCount.setVisibility(View.GONE);
		}
		
		if (position == 0) {
			holder.ivTitleIcon.setVisibility(View.GONE);
			holder.ivHeader.setImageResource(R.drawable.icon_notification_dami);
			holder.tvName.setText(R.string.dige);
			holder.tvInfo.setText(R.string.dige_info);
			holder.tvInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			holder.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			return convertView;
		}
		
		
		if (!TextUtils.isEmpty(bean.headurl)) {
			ImageLoaderUtil.displayImage(bean.headurl, holder.ivHeader);
		} else {
			holder.ivHeader.setImageResource(R.drawable.default_tribe);
		}
		if (bean.type == 200 || bean.type == 300) {
			holder.ivTitleIcon.setVisibility(View.VISIBLE);
			int drawable = (bean.type == 300) ? R.drawable.icon_notification_meeting
					: R.drawable.icon_notification_tribe;
			holder.ivTitleIcon.setImageResource(drawable);
		} else {
			holder.ivTitleIcon.setVisibility(View.GONE);
		}
		holder.tvName.setText(bean.name);
		if (TextUtils.isEmpty(bean.unfinishinput)) {
			holder.tvInfo.setText(bean.lastmsgcontent);
			holder.tvInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			if (bean.localtype == 1) {
				holder.tvInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_notification_voice, 0, 0, 0);
				holder.tvInfo.setCompoundDrawablePadding(MyUtils.dip2px(mContext, 3));
			} else if (bean.localtype == 2) {
				holder.tvInfo.setText(R.string.picture_scheme);
			}
		} else {
			holder.tvInfo.setText(buildDraft(bean.unfinishinput));
		}
		
		
		return convertView;
	}
	
	private SpannableString buildDraft(String draft) {
		String draftInfo = mContext.getString(R.string.draft_scheme);
		SpannableString spannableString = new SpannableString(draftInfo + draft);
		spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.red_dongtai_bg)), 0,
				draftInfo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannableString;
	}

	private class ViewHolder {
		TextView tvName;
		TextView tvInfo;
		ImageView ivHeader;
		ImageView ivTitleIcon;
		TextView tvMsgCount;
	}
}

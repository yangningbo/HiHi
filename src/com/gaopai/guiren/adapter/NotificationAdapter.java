package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
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
	
	public void buildListWithDami() {
		list.clear();
		list.add(new ConversationBean());
	}

	public void addAll(List<ConversationBean> o) {
		buildListWithDami();
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
		
		if (position == 0) {
			holder.tvMsgCount.setVisibility(View.GONE);
			holder.ivTitleIcon.setVisibility(View.GONE);
			holder.ivHeader.setImageResource(R.drawable.icon_notification_dami);
			holder.tvName.setText("大蜜汇报");
			holder.tvInfo.setText("第一手消息");
			holder.tvInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			holder.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			return convertView;
		}
		
		ConversationBean bean = list.get(position);
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
		holder.tvInfo.setText(bean.lastmsgcontent);
		if (bean.type % 100 == 2) {
			holder.tvInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_notification_voice, 0, 0, 0);
			holder.tvInfo.setCompoundDrawablePadding(MyUtils.dip2px(mContext, 3));
		} else {
			holder.tvInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
		if (bean.unreadcount > 0) {
			holder.tvMsgCount.setVisibility(View.VISIBLE);
			holder.tvMsgCount.setText(String.valueOf(bean.unreadcount));
		} else {
			holder.tvMsgCount.setVisibility(View.GONE);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView tvName;
		TextView tvInfo;
		ImageView ivHeader;
		ImageView ivTitleIcon;
		TextView tvMsgCount;
	}
}

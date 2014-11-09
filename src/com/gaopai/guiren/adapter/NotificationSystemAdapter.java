package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.List;

import u.aly.be;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.ConversationBean;
import com.gaopai.guiren.bean.NotifiyVo;

public class NotificationSystemAdapter extends BaseAdapter {
	private final LayoutInflater mInflater;
	private List<ConversationBean> list = new ArrayList<ConversationBean>();

	public NotificationSystemAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	public void addAll(List<ConversationBean> o) {
		list.clear();
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
			convertView = mInflater.inflate(R.layout.item_general, null);
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tvInfo = (TextView) convertView.findViewById(R.id.tv_info);
			holder.ivHeader = (ImageView) convertView.findViewById(R.id.iv_header);
			holder.tvMsgCount = (TextView) convertView.findViewById(R.id.tv_message_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ConversationBean bean = list.get(position);
		holder.ivHeader.setImageResource(R.drawable.default_tribe);
		holder.tvName.setText(bean.name);
		holder.tvInfo.setText(bean.lastmsgcontent);
		holder.tvMsgCount.setText(String.valueOf(bean.unreadcount));
		return convertView;
	}

	private class ViewHolder {
		TextView tvName;
		TextView tvInfo;
		ImageView ivHeader;
		TextView tvMsgCount;
	}
}

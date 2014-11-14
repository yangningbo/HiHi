package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.share.BaseShareFragment;
import com.gaopai.guiren.activity.share.ShareActivity;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter.Item;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter.Row;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter.Section;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.utils.ImageLoaderUtil;

public class ShareContactAdapter extends CopyOfConnectionAdapter implements Filterable {
	private ShareActivity shareActivity;
	private BaseShareFragment shareFragment;

	public ShareContactAdapter(BaseShareFragment shareFragment) {
		super();
		this.shareFragment = shareFragment;
		shareActivity = (ShareActivity) shareFragment.getActivity();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (getItemViewType(position) == 0) { // Item
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_recommend, parent, false);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			User user = ((Item) getItem(position)).user;
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
			viewHolder.tvUserInfo = (TextView) convertView.findViewById(R.id.tv_recommend);
			viewHolder.ivHeader = (ImageView) convertView.findViewById(R.id.iv_header);
			viewHolder.ivCheck = (ImageView) convertView.findViewById(R.id.btn_add);
			if (shareActivity.userMap.containsKey(user.uid)) {
				viewHolder.ivCheck.setImageResource(R.drawable.rec_people_selected_btn);
			} else {
				viewHolder.ivCheck.setImageResource(R.drawable.rec_people_add_btn);
			}
			viewHolder.tvUserName.setText(user.realname);
			viewHolder.tvUserInfo.setText(user.realname);
			if (!TextUtils.isEmpty(user.headsmall)) {
				ImageLoaderUtil.displayImage(user.headsmall, viewHolder.ivHeader);
			} else {
				viewHolder.ivHeader.setImageResource(R.drawable.default_header);
			}
		} else { // Section
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_connection_section, parent, false);
			}

			Section section = (Section) getItem(position);
			TextView textView = (TextView) convertView.findViewById(R.id.textView1);
			textView.setText(section.text);
		}

		return convertView;
	}

	public void toggle(int position) {
		Item item = ((Item) getItem(position));
		item.isChecked = !item.isChecked;
		notifyDataSetChanged();
	}


}

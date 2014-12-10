package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.utils.ImageLoaderUtil;

public class SearchAdapter extends BaseAdapter {
	
	public SearchAdapter() {
		rows = new ArrayList<Row>();
	}

	public static abstract class Row {
		public int type;
	}

	public static final class Section extends Row {
		public final String text;

		public Section(String text, int type) {
			this.text = text;
			this.type = type;
		}
	}

	public static final class Item extends Row {
		public Object object;

		public Item(Object object, int type) {
			this.type = type;
			this.object = object;
		}
	}

	private List<Row> rows;

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	public void addAll(List<Row> rows) {
		this.rows = rows;
		notifyDataSetChanged();
	}

	public void clear() {
		this.rows.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return rows.size();
	}

	@Override
	public Row getItem(int position) {
		return rows.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (getItemViewType(position) == 1) { // user
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_connection_item, parent, false);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
			viewHolder.tvUserInfo = (TextView) convertView.findViewById(R.id.tv_user_info);
			viewHolder.ivHeader = (ImageView) convertView.findViewById(R.id.iv_header);
			viewHolder.tvFancyCount= (TextView) convertView.findViewById(R.id.tv_user_fancy_count);
			viewHolder.viewFancy= convertView.findViewById(R.id.tv_user_fancy);
			Object object = ((Item) getItem(position)).object;
			if (object instanceof User) {
				User user = (User) object;
				viewHolder.tvFancyCount.setVisibility(View.VISIBLE);
				viewHolder.viewFancy.setVisibility(View.VISIBLE);
				viewHolder.tvFancyCount.setText(String.valueOf(user.integral));
				viewHolder.ivHeader.setImageResource(R.drawable.default_header);
				bindSimpleItemView(viewHolder, user.realname, user.reason, user.headsmall);
			} else if (object instanceof Tribe) {
				viewHolder.tvFancyCount.setVisibility(View.GONE);
				viewHolder.viewFancy.setVisibility(View.GONE);
				Tribe tribe = (Tribe) object;
				viewHolder.ivHeader.setImageResource(R.drawable.default_tribe);
				bindSimpleItemView(viewHolder, tribe.name, tribe.content, tribe.logosmall);
			}
		} else if (getItemViewType(0) == 0) { // Section
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

	private static final class ViewHolder {
		private TextView tvUserName;
		private TextView tvUserInfo;
		private ImageView ivHeader;
		private View viewFancy;
		private TextView tvFancyCount;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return rows.get(position).type;
	}

	private void bindSimpleItemView(ViewHolder viewHolder, String title, String info, String head) {
		viewHolder.tvUserName.setText(title);
		viewHolder.tvUserInfo.setText(info);
		if (!TextUtils.isEmpty(head)) {
			ImageLoaderUtil.displayImage(head, viewHolder.ivHeader);
		} 
	}
}

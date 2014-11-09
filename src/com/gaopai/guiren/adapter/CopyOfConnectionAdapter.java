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
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.widget.indexlist.CharacterParser;

public class CopyOfConnectionAdapter extends BaseAdapter implements SectionIndexer {

	private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static abstract class Row {
	}

	public static final class Section extends Row {
		public final String text;

		public Section(String text) {
			this.text = text;
		}
	}

	public static final class Item extends Row implements Comparable<Item> {
		public User user;
		public String pingYinText;
		public boolean isChecked = false;
		public static final CharacterParser parser = new CharacterParser();

		public Item(User user) {
			this.user = user;
			if (TextUtils.isEmpty(user.realname)) {
				user.realname = "0";
			}
			this.pingYinText = parser.getSelling(user.realname).toUpperCase();
		}

		@Override
		public int compareTo(Item another) {
			// TODO Auto-generated method stub
			return this.pingYinText.compareTo(another.pingYinText);
		}

	}

	private List<Row> rows;

	public CopyOfConnectionAdapter() {
		rows = new ArrayList<Row>();
	}

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
		if (getItemViewType(position) == 0) { // Item
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_connection_item, parent, false);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			User user = ((Item) getItem(position)).user;
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
			viewHolder.tvUserInfo = (TextView) convertView.findViewById(R.id.tv_user_info);
			viewHolder.ivHeader = (ImageView) convertView.findViewById(R.id.iv_header);
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

	protected static final class ViewHolder {
		TextView tvUserName;
		TextView tvUserInfo;
		ImageView ivHeader;
		ImageView ivCheck;
	}

	@Override
	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < getCount(); j++) {
				Row row = getItem(j);
				if (row instanceof Section) {
					if (((Section) row).text.charAt(0) == mSections.charAt(section)) {
						return j;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (getItem(position) instanceof Section) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		String[] sections = new String[mSections.length()];
		for (int i = 0; i < mSections.length(); i++)
			sections[i] = String.valueOf(mSections.charAt(i));
		return sections;
	}

}

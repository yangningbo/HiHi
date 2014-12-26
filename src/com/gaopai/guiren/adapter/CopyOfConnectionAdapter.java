package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.support.view.HeadView;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.widget.indexlist.CharacterParser;

public class CopyOfConnectionAdapter extends BaseAdapter implements SectionIndexer, Filterable {

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
			this.pingYinText = parser.getSelling(User.getUserName(user)).toUpperCase();
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
		mItems = new ArrayList<Item>();
		mRows = new ArrayList<Row>();
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	public void addAll(List<Row> rows) {
		this.rows = rows;
		notifyDataSetChanged();
	}

	public void clear() {
		this.mUserList.clear();
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
			viewHolder.tvFancyCount = (TextView) convertView.findViewById(R.id.tv_user_fancy_count);
			viewHolder.layoutHeader = (HeadView) convertView.findViewById(R.id.layout_header_mvp);
			viewHolder.tvUserName.setText(User.getUserName(user));
			viewHolder.tvUserInfo.setText(user.post);
			
			viewHolder.layoutHeader.setImage(user.headsmall);
			viewHolder.layoutHeader.setMVP(user.bigv == 1);
			
			viewHolder.tvFancyCount.setText(String.valueOf(user.integral));
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
		TextView tvFancyCount;
		HeadView layoutHeader;
	}

	@Override
	public int getPositionForSection(int section) {
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

	private ArrayList<Item> mItems;
	private ArrayList<Row> mRows;
	public List<User> mUserList = new ArrayList<User>();

	public void addAndSort(List<User> userList) {
		mUserList.addAll(userList);
		sortData(mUserList);
	}

	public void removeUser(String uid) {
		for(User user:mUserList) {
			if (uid.equals(user.uid)) {
				mUserList.remove(user);
				sortData(mUserList);
				return;
			}
		}
	}
	
	public void addUser(User user) {
		mUserList.add(user);
		sortData(mUserList);
	}

	public void sortData(List<User> userList) {
		int size = userList.size();
		mItems.clear();
		mRows.clear();
		for (int i = 0; i < size; i++) {
			mItems.add(new Item(userList.get(i)));
		}
		Collections.sort(mItems);
		char character = '0';
		for (int i = 0; i < mItems.size(); i++) {
			Item item = mItems.get(i);
			char first = item.pingYinText.charAt(0);
			if (i == 0 && (first < 'A' || first > 'Z')) {
				mRows.add(new Section("#"));
			}
			if (first >= 'A' && first <= 'Z') {
				if (character != first) {
					mRows.add(new Section(String.valueOf(first)));
					character = first;
				}
			}
			mRows.add(item);
		}
		addAll(mRows);
	}

	private ArrayFilter mFilter;

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

	private class ArrayFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();
			if (prefix.length() == 0) {
				results.values = mUserList;
				results.count = mUserList.size();
				return results;
			}
			List<User> users = new ArrayList<User>();
			for (User user : mUserList) {
				if (user.realname.contains(prefix.toString())) {
					users.add(user);
				}
			}

			results.values = users;
			results.count = users.size();
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			// TODO Auto-generated method stub
			if (results.count > 0) {
				sortData((List<User>) results.values);
			} else {
				notifyDataSetInvalidated();
			}
		}

	}

}

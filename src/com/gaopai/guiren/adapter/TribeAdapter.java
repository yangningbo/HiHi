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

import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.utils.ImageLoaderUtil;

public class TribeAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	public List<Tribe> list = new ArrayList<Tribe>();

	public TribeAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	public void addAll(List<Tribe> o) {
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
			holder.mUserNameTextView = (TextView) convertView
					.findViewById(R.id.tv_title);
			holder.mTimeTextView = (TextView) convertView
					.findViewById(R.id.tv_date);
			holder.mContentTextView = (TextView) convertView
					.findViewById(R.id.tv_info);
			holder.mHeaderView = (ImageView) convertView
					.findViewById(R.id.iv_header);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Tribe tribe = list.get(position);
		if (!TextUtils.isEmpty(tribe.logosmall)) {
			ImageLoaderUtil.displayImage(tribe.logosmall, holder.mHeaderView);
		} else {
			holder.mHeaderView.setImageResource(R.drawable.default_tribe);
		}
		holder.mUserNameTextView.setText(tribe.name);
		holder.mTimeTextView.setVisibility(View.GONE);
		holder.mContentTextView.setText(tribe.content);
		return convertView;
	}

	private class ViewHolder {
		TextView mUserNameTextView;
		TextView mContentTextView;
		TextView mTimeTextView;
		ImageView mHeaderView;
	}

}

package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.support.view.HeadView;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.ViewUtil;

public class RecommendAdapter<T> extends BaseAdapter {

	public static final int RECOMMEND_FRIEND = 0;
	public static final int RECOMMEND_TRIBE = 1;

	private final LayoutInflater mInflater;
	public List<T> mData = new ArrayList<T>();
	private Context mContext;
	private int mType = 0;

	private OnClickListener mAddClickListener;

	public Set<String> choseSet = new HashSet<String>();

	public RecommendAdapter(Context context, int type, OnClickListener addClickListener) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mType = type;
		mAddClickListener = addClickListener;

	}

	public void addIdToChoseSet(String id) {
		if (choseSet.contains(id)) {
			choseSet.remove(id);
			notifyDataSetChanged();
			return;
		}
		choseSet.add(id);
		notifyDataSetChanged();
	}

	public String getChoseIdString() {
		StringBuilder builder = new StringBuilder();
		for (String id : choseSet) {
			builder.append(id);
			builder.append(",");
		}
		String re = builder.toString();
		return re.substring(0, re.length() - 1);
	}

	public int getChosedSize() {
		return choseSet.size();
	}

	public String getAllIdString() {
		if (mData.size() == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (T data : mData) {
			if (mType == RECOMMEND_FRIEND) {
				builder.append(((User) data).uid);
				builder.append(",");
			} else {
				builder.append(((Tribe) data).id);
				builder.append(",");
			}
		}
		String re = builder.toString();
		return re.substring(0, re.length() - 1);
	}

	public void addAll(List<T> o) {
		mData.addAll(o);
		notifyDataSetChanged();
	}

	public void clear() {
		mData.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_recommend, null);
			holder = new ViewHolder();
			holder.tvRecommend = (TextView) convertView.findViewById(R.id.tv_recommend);
			holder.tvRecommendInfo = (TextView) convertView.findViewById(R.id.tv_recommend_info);
			holder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
			holder.ivHeader = (ImageView) convertView.findViewById(R.id.iv_header);
			holder.btnAdd = (ImageButton) convertView.findViewById(R.id.btn_add);
			holder.layoutHeader = ViewUtil.findViewById(convertView, R.id.layout_header_mvp);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (mType == RECOMMEND_FRIEND) {
			User user = (User) getItem(position);
			holder.layoutHeader.setImage(user.headsmall);
			holder.layoutHeader.setMVP(user.bigv == 1);
			holder.tvUserName.setText(user.realname);
			holder.tvRecommend.setText(User.getUserInfo(user));
			holder.tvRecommendInfo.setVisibility(View.VISIBLE);
			holder.tvRecommendInfo.setText("系统为您精选");
			holder.btnAdd.setOnClickListener(mAddClickListener);
			holder.btnAdd.setTag(user);
			if (choseSet.contains(user.uid)) {
				holder.btnAdd.setImageResource(R.drawable.icon_add_rec_has_follow);
			} else {
				holder.btnAdd.setImageResource(R.drawable.icon_add_rec_normal);
			}
		} else if (mType == RECOMMEND_TRIBE) {
			Tribe tribe = (Tribe) getItem(position);
			holder.tvRecommend.setText("系统根据您的标签推荐圈子");
			ImageLoaderUtil.displayImage(tribe.logosmall, holder.ivHeader, R.drawable.default_header);
			holder.tvUserName.setText(tribe.name);
			holder.btnAdd.setOnClickListener(mAddClickListener);
			holder.btnAdd.setTag(tribe);
			if (choseSet.contains(tribe.id)) {
				holder.btnAdd.setImageResource(R.drawable.rec_people_selected_btn);
			} else {
				holder.btnAdd.setImageResource(R.drawable.rec_people_add_btn);
			}
		}
		return convertView;
	}

	final static class ViewHolder {
		TextView tvUserName;
		TextView tvRecommend;
		TextView tvRecommendInfo;
		ImageView ivHeader;
		ImageButton btnAdd;
		HeadView layoutHeader;
	}
}

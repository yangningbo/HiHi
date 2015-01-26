package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.support.view.HeadView;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;

public class RecommendUserAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	public List<User> mData = new ArrayList<User>();
	private Context mContext;

	private OnClickListener mAddClickListener;

	public Set<String> choseSet = new HashSet<String>();

	public RecommendUserAdapter(Context context, OnClickListener addClickListener) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
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
		for (User data : mData) {
			builder.append(((User) data).uid);
			builder.append(",");
		}
		String re = builder.toString();
		return re.substring(0, re.length() - 1);
	}

	public void addAll(List<User> o) {
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
		if (getItemViewType(position) != 0) {
			if (convertView == null) {
				convertView = getBlankView(MyUtils.dip2px(mContext, 25));
			}
			return convertView;
		}
		final ViewHolder holder;
		if (convertView == null) {
		
			convertView = mInflater.inflate(R.layout.item_recommend, null);
			holder = new ViewHolder();
			holder.tvUserInfo = (TextView) convertView.findViewById(R.id.tv_recommend);
			holder.tvRecommendInfo = (TextView) convertView.findViewById(R.id.tv_recommend_info);
			holder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
			holder.ivHeader = (ImageView) convertView.findViewById(R.id.iv_header);
			holder.btnAdd = (ImageButton) convertView.findViewById(R.id.btn_add);
			holder.layoutHeader = ViewUtil.findViewById(convertView, R.id.layout_header_mvp);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		User user = (User) getItem(position);
		holder.layoutHeader.setImage(user.headsmall);
		holder.layoutHeader.setMVP(user.bigv == 1);
		holder.tvUserName.setText(user.realname);
		holder.tvUserInfo.setText(User.getUserInfo(user));
		holder.tvRecommendInfo.setVisibility(View.VISIBLE);
		holder.tvRecommendInfo.setText("系统为您精选");
		holder.btnAdd.setOnClickListener(mAddClickListener);
		holder.btnAdd.setTag(user);
		if (choseSet.contains(user.uid)) {
			holder.btnAdd.setImageResource(R.drawable.icon_add_rec_has_follow);
		} else {
			holder.btnAdd.setImageResource(R.drawable.icon_add_rec_normal);
		}
		return convertView;
	}
	
	private View getBlankView(int height) {
		View v = new View(mContext);
		v.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, height));
		v.setBackgroundColor(mContext.getResources().getColor(R.color.general_background));
		return v;
	}
	
	@Override
	public int getItemViewType(int position) {
		User user = (User) getItem(position);
		if (user.localType == 0) {
			return 0;//user type
		} else {
			return 1;//blank type
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	final static class ViewHolder {
		TextView tvUserName;
		TextView tvUserInfo;
		TextView tvRecommendInfo;
		ImageView ivHeader;
		ImageButton btnAdd;
		HeadView layoutHeader;
	}
	
//	private String getRecommendInfo(User user) {
//		
//	}
}

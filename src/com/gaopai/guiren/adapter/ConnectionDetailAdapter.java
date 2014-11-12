package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.UserInfoActivity;
import com.gaopai.guiren.bean.dynamic.ConnectionBean.TypeHolder;
import com.gaopai.guiren.bean.dynamic.ConnectionBean.User;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.MyTextUtils;

public class ConnectionDetailAdapter extends BaseAdapter {
	private final LayoutInflater mInflater;
	private List<User> mData = new ArrayList<User>();
	private TypeHolder mTypeHolder;
	private Context mContext;

	public ConnectionDetailAdapter(TypeHolder typeHolder, Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTypeHolder = typeHolder;
		mData = typeHolder.jsoncontent.user;
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
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflateItemView();
		}
		buildJoinView((ViewHolderGeneral) convertView.getTag(), mTypeHolder, position);
		return convertView;
	}

	private void buildJoinView(ViewHolderGeneral viewHolder, TypeHolder typeBean, int position) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(typeBean.headsmall)) {
			ImageLoaderUtil.displayImage(typeBean.headsmall, viewHolder.ivHeader);
		} else {
			viewHolder.ivHeader.setImageResource(R.drawable.default_header);
		}
		viewHolder.tvTitle.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
		User user = mData.get(position);
		if (typeBean.type == ConnectionAdapter.TYPE_SOMEONE_FOLLOW_ME) {
			viewHolder.tvTitle.setText(MyTextUtils.addConnectionJoin("", user.realname, user.uid, "关注了你"));
		} else if (typeBean.type == ConnectionAdapter.TYPE_SOMEONE_I_FOLLOW_FOLLOW) {
			viewHolder.tvTitle.setText(MyTextUtils.addConnectionJoin("您的通互相关注好友", typeBean.jsoncontent.realname,
					typeBean.jsoncontent.uid, "关注了", user.realname, user.uid));
		}

		if (!TextUtils.isEmpty(user.headsmall)) {
			ImageLoaderUtil.displayImage(user.headsmall, viewHolder.ivUserHeader);
		} else {
			viewHolder.ivUserHeader.setImageResource(R.drawable.default_header);
		}
		viewHolder.tvUserName.setText(user.realname);
		viewHolder.tvUserInfo.setText(user.company);
		viewHolder.tvDateInfo.setText(FeatureFunction.getHumanReadTime(Long.valueOf(typeBean.addtime)));
		
		viewHolder.rlInfoLayout.setTag(user.uid);
		viewHolder.rlInfoLayout.setOnClickListener(infoClickListener);
	}

	private View inflateItemView() {
		View convertView = null;
		ViewHolderGeneral viewHolder;
		convertView = mInflater.inflate(R.layout.item_connection_general, null);
		viewHolder = ViewHolderGeneral.getInstance(convertView);
		convertView.setTag(viewHolder);
		return convertView;
	}

	static class ViewHolderGeneral {
		ImageView ivHeader;
		TextView tvTitle;
		ImageView ivUserHeader;
		TextView tvUserName;
		TextView tvUserInfo;
		TextView tvDateInfo;
		RelativeLayout rlInfoLayout;

		public static ViewHolderGeneral getInstance(View view) {
			// TODO Auto-generated method stub
			ViewHolderGeneral viewHolder = new ViewHolderGeneral();
			viewHolder.ivHeader = (ImageView) view.findViewById(R.id.iv_header);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
			viewHolder.ivUserHeader = (ImageView) view.findViewById(R.id.iv_user_header);
			viewHolder.tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
			viewHolder.tvUserInfo = (TextView) view.findViewById(R.id.tv_user_info);
			viewHolder.tvDateInfo = (TextView) view.findViewById(R.id.tv_date_info);
			viewHolder.rlInfoLayout = (RelativeLayout) view.findViewById(R.id.rl_info_holder);
			return viewHolder;
		}
	}
	
	private void goToUserActivity(String uid) {
		if (TextUtils.isEmpty(uid)) {
			return;
		}
		Intent intent = new Intent(mContext, UserInfoActivity.class);
		intent.putExtra(UserInfoActivity.KEY_UID, uid);
		mContext.startActivity(intent);
	}
	
	private OnClickListener infoClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			goToUserActivity((String) v.getTag());
		}
	};
}

package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.ConnectionDetailActivity;
import com.gaopai.guiren.activity.ProfileActivity;
import com.gaopai.guiren.activity.UserInfoActivity;
import com.gaopai.guiren.adapter.DynamicAdapter.ViewHolderSendDynamic;
import com.gaopai.guiren.bean.dynamic.ConnectionBean;
import com.gaopai.guiren.bean.dynamic.ConnectionBean.JsonContent;
import com.gaopai.guiren.bean.dynamic.ConnectionBean.TypeHolder;
import com.gaopai.guiren.bean.dynamic.ConnectionBean.User;
import com.gaopai.guiren.db.NewMessageTable;
import com.gaopai.guiren.fragment.ConnectionFragment;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.view.MyGridLayout;

public class ConnectionAdapter extends BaseAdapter {
	private final LayoutInflater mInflater;
	private List<TypeHolder> mData = new ArrayList<TypeHolder>();
	private Context mContext;
	private ConnectionFragment mFragment;

	public final static int TYPE_WEIBO_USER_JOIN = 1;
	public final static int TYPE_PHONE_USER_JOIN = 2;
	public final static int TYPE_SYS_REC_USER = 3;
	public final static int TYPE_SOMEONE_FOLLOW_ME = 4;
	public final static int TYPE_SOMEONE_I_FOLLOW_FOLLOW = 5;
	public final static int TYPE_SOMEONE_JOIN_MY_MEETING = 6;
	public final static int TYPE_SOMEONE_JOIN_MY_TRIBE = 7;

	private static final int TYPE_BE_FRIENDS = 0;
	private static final int TYPE_GENERAL = 1;
	private static final int TYPE_PIC_GENERAL = 2;

	public void showSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}

	public ConnectionAdapter(ConnectionFragment fragment) {
		mFragment = fragment;
		mContext = fragment.getActivity();
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addAll(List<TypeHolder> o) {
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
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		int type = getItemViewType(position);
		TypeHolder typeBean = mData.get(position);
		Log.d("TAG", "type = " + (type + 1));
		switch (type + 1) {
		case TYPE_SOMEONE_JOIN_MY_MEETING:
		case TYPE_SOMEONE_JOIN_MY_TRIBE:
		case TYPE_WEIBO_USER_JOIN:
		case TYPE_PHONE_USER_JOIN:
		case TYPE_SYS_REC_USER: {
			if (convertView == null) {
				convertView = inflateItemView(TYPE_GENERAL);
			}
			buildJoinView((ViewHolderGeneral) convertView.getTag(), typeBean, position, type + 1);
			break;
		}

		case TYPE_SOMEONE_I_FOLLOW_FOLLOW:
		case TYPE_SOMEONE_FOLLOW_ME: {
			if (convertView == null) {
				convertView = inflateItemView(TYPE_PIC_GENERAL);
			}
			buildPicGridView((ViewHolderPicGridGeneral) convertView.getTag(), typeBean, position, type + 1);
			break;
		}
		}
		return convertView;
	}

	private void goToUserActivity(String uid) {
		if (TextUtils.isEmpty(uid)) {
			return;
		}
		Intent intent = new Intent(mContext, ProfileActivity.class);
		intent.putExtra(ProfileActivity.KEY_UID, uid);
		mContext.startActivity(intent);
	}

	private void buildPicGridView(ViewHolderPicGridGeneral viewHolder, TypeHolder typeBean, int position, int type) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(typeBean.headsmall)) {
			ImageLoaderUtil.displayImage(typeBean.headsmall, viewHolder.ivHeader);
		} else {
			viewHolder.ivHeader.setImageResource(R.drawable.default_header);
		}

		JsonContent jsonContent = typeBean.jsoncontent;
		List<User> userList = jsonContent.user;
		viewHolder.tvTitle.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
		if (type == TYPE_SOMEONE_FOLLOW_ME) {
			viewHolder.tvTitle.setText(MyTextUtils.addConnectionUserList(userList, "等" + userList.size() + "人关注了你"));
		} else if (type == TYPE_SOMEONE_I_FOLLOW_FOLLOW) {
			viewHolder.tvTitle.setText(MyTextUtils.addConnectionUserListExtra(userList, jsonContent.realname,
					jsonContent.uid, "您的好友", "关注了", "等" + userList.size() + "人"));
		}
		viewHolder.gridLayout.removeAllViews();
		if (userList != null && userList.size() > 0) {
			int length = userList.size();
			if (userList.size() > 16) {
				length = 16;
			}
			for (int i = 0; i < length; i++) {
				viewHolder.gridLayout.addView(getImageView(userList.get(i).headsmall));
			}
		}
		viewHolder.tvDateInfo.setText(FeatureFunction.getHumanReadTime(Long.valueOf(typeBean.addtime)));
		viewHolder.tvViewDetail.setTag(typeBean);
		viewHolder.tvViewDetail.setOnClickListener(viewDetialClickListener);
		
		viewHolder.gridLayout.setTag(typeBean);
		viewHolder.gridLayout.setOnClickListener(viewDetialClickListener);
	}

	private OnClickListener viewDetialClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra(ConnectionDetailActivity.KEY_TYPE_HOLDER, (TypeHolder) v.getTag());
			intent.setClass(mContext, ConnectionDetailActivity.class);
			mContext.startActivity(intent);
		}
	};

	private void buildJoinView(ViewHolderGeneral viewHolder, TypeHolder typeBean, int position, int type) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(typeBean.headsmall)) {
			ImageLoaderUtil.displayImage(typeBean.headsmall, viewHolder.ivHeader);
		} else {
			viewHolder.ivHeader.setImageResource(R.drawable.default_header);
		}
		JsonContent content = typeBean.jsoncontent;
		viewHolder.tvTitle.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
		if (type == TYPE_WEIBO_USER_JOIN) {
			viewHolder.tvTitle.setText(MyTextUtils
					.addConnectionJoin("您关注的微博用户", content.realname, content.uid, "加入了贵人"));
		} else if (type == TYPE_PHONE_USER_JOIN) {
			viewHolder.tvTitle
					.setText(MyTextUtils.addConnectionJoin("您的通讯录好友", content.realname, content.uid, "加入了贵人"));
		} else if (type == TYPE_SYS_REC_USER) {
			viewHolder.tvTitle.setText(MyTextUtils.addConnectionRec("系统根据您的标签给您推荐了一条人脉信息", content.realname,
					content.uid));
		} else if (type == TYPE_SOMEONE_JOIN_MY_MEETING) {
			viewHolder.tvTitle.setText(MyTextUtils.addConnectionTribe(content.realname, content.uid, "加入了您的会议", "「"
					+ content.roomname + "」", content.roomid, false));
		} else if (type == TYPE_SOMEONE_JOIN_MY_TRIBE) {
			viewHolder.tvTitle.setText(MyTextUtils.addConnectionTribe(content.realname, content.uid, "加入了您的部落", "「"
					+ content.roomname + "」", content.roomid, true));
		} else {
			viewHolder.tvTitle.setText(typeBean.tips);
		}

		if (!TextUtils.isEmpty(typeBean.jsoncontent.headsmall)) {
			ImageLoaderUtil.displayImage(typeBean.jsoncontent.headsmall, viewHolder.ivUserHeader);
		} else {
			viewHolder.ivUserHeader.setImageResource(R.drawable.default_header);
		}
		viewHolder.tvUserName.setText(typeBean.jsoncontent.realname);
		viewHolder.tvUserInfo.setText(typeBean.jsoncontent.company);
		viewHolder.tvDateInfo.setText(FeatureFunction.getHumanReadTime(Long.valueOf(typeBean.addtime)));

		viewHolder.rlInfoLayout.setTag(content.uid);
		viewHolder.rlInfoLayout.setOnClickListener(infoClickListener);
	}

	private OnClickListener infoClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			goToUserActivity((String) v.getTag());
		}
	};

	private ImageView getImageView(String url) {
		ImageView imageView = new ImageView(mContext);
		if (TextUtils.isEmpty(url)) {
			imageView.setImageResource(R.drawable.bg);
		} else {
			ImageLoaderUtil.displayImage(url, imageView);
		}
		android.view.ViewGroup.LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(lp);
		imageView.setScaleType(ScaleType.FIT_XY);
		int padding = MyUtils.dip2px(mContext, 5);
		imageView.setPadding(padding, padding, padding, padding);
		return imageView;
	}

	@Override
	public int getViewTypeCount() {
		return 7;
	}

	@Override
	public int getItemViewType(int position) {
		return mData.get(position).type - 1;
	}

	private View inflateItemView(int type) {
		View convertView = null;
	    if (TYPE_GENERAL == type) {
			ViewHolderGeneral viewHolder;
			convertView = mInflater.inflate(R.layout.item_connection_general, null);
			viewHolder = ViewHolderGeneral.getInstance(convertView);
			convertView.setTag(viewHolder);
		} else if (TYPE_PIC_GENERAL == type) {
			ViewHolderPicGridGeneral viewHolder;
			convertView = mInflater.inflate(R.layout.item_connection_pic_grid_general, null);
			viewHolder = ViewHolderPicGridGeneral.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
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

	static class ViewHolderPicGridGeneral {
		ImageView ivHeader;
		TextView tvTitle;
		MyGridLayout gridLayout;
		TextView tvDateInfo;
		TextView tvViewDetail;

		public static ViewHolderPicGridGeneral getInstance(View view) {
			// TODO Auto-generated method stub
			ViewHolderPicGridGeneral viewHolder = new ViewHolderPicGridGeneral();
			viewHolder.ivHeader = (ImageView) view.findViewById(R.id.iv_header);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
			viewHolder.gridLayout = (MyGridLayout) view.findViewById(R.id.gl_pic);
			viewHolder.tvDateInfo = (TextView) view.findViewById(R.id.tv_date_info);
			viewHolder.tvViewDetail = (TextView) view.findViewById(R.id.tv_view_detail);
			return viewHolder;
		}
	}

	List<ImageView> imageCacheList = new ArrayList<ImageView>();

	public void cacheImage() {
	}

}

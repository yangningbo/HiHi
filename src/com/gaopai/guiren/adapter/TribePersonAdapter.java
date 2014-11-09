package com.gaopai.guiren.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.utils.ImageLoaderUtil;

public class TribePersonAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	private List<User> mData;
	private Context mContext;
	// private ImageLoader mImageLoader;
	private int mType = 0;
	private Handler mHandler;
	private boolean mIsShow = false;

	public TribePersonAdapter(Context context, List<User> data,
			boolean isShowKickBtn) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mData = data;
		// mImageLoader = new ImageLoader();

//		mHandler = handler;
		mIsShow = isShowKickBtn;
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

	// public HashMap<String, Bitmap> getImageBuffer(){
	// return mImageLoader.getImageBuffer();
	// }

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.tribe_person_item, null);
			holder = new ViewHolder();

			holder.mUserNameTextView = (TextView) convertView
					.findViewById(R.id.username);
			holder.mSignTextView = (TextView) convertView
					.findViewById(R.id.sign);
			holder.mHeaderView = (ImageView) convertView
					.findViewById(R.id.header);
			holder.mTimeView = (TextView) convertView.findViewById(R.id.time);
			holder.mLevelLayout = (LinearLayout) convertView
					.findViewById(R.id.levellayout);
			holder.mKickBtn = (Button) convertView.findViewById(R.id.kickbtn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.mKickBtn.setVisibility(View.GONE);
		holder.mTimeView.setVisibility(View.GONE);

		if (mIsShow) {
			holder.mKickBtn.setVisibility(View.VISIBLE);
		}

		User user = mData.get(position);

		if (holder.mLevelLayout.getChildCount() != 0) {
			holder.mLevelLayout.removeAllViews();
		}

		holder.mLevelLayout.setVisibility(View.VISIBLE);
		DamiCommon.showLevel(user.integral, holder.mLevelLayout, 7, 3);

		if (!TextUtils.isEmpty(user.headsmall)) {
			// holder.mHeaderView.setTag(user.mSmallHead);
			// if(mImageLoader.getImageBuffer().get(user.mSmallHead) == null){
			// holder.mHeaderView.setImageBitmap(null);
			// holder.mHeaderView.setImageResource(R.drawable.default_header);
			// }
			//
			// mImageLoader.getBitmap(mContext, holder.mHeaderView, null,
			// user.mSmallHead, 0, false, false);

			ImageLoaderUtil.displayImage(user.headsmall, holder.mHeaderView);

		} else {
			holder.mHeaderView.setImageResource(R.drawable.default_header);
		}

		final int index = position;

		holder.mKickBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Message message = new Message();
//				message.what = PersonListActivity.KICK_PERSON;
//				message.arg1 = index;
//				mHandler.sendMessage(message);
			}
		});

		holder.mUserNameTextView.setText(user.displayName);
		String content = "";
		// if(!TextUtils.isEmpty(user.content)){
		// content = user.content;
		// }else {
		content = user.sign;
		// }
		holder.mSignTextView.setText(content);
		holder.mTimeView.setText(FeatureFunction
				.getCreateTime(user.createtime));

		return convertView;
	}

	final static class ViewHolder {
		TextView mUserNameTextView;
		TextView mSignTextView;
		ImageView mHeaderView;
		TextView mTimeView;
		LinearLayout mLevelLayout;
		Button mKickBtn;

		@Override
		public int hashCode() {
			return this.mUserNameTextView.hashCode() + mSignTextView.hashCode()
					+ mHeaderView.hashCode() + mTimeView.hashCode()
					+ mLevelLayout.hashCode() + mKickBtn.hashCode();
		}
	}

}

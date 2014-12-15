package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.utils.DateUtil;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.view.RoundImageView;

public class MeetingAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	public List<Tribe> mData = new ArrayList<Tribe>();
	private Context mContext;

	public MeetingAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
	}

	public void addAll(List<Tribe> o) {
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_meeting_new, null);
			holder = new ViewHolder();

			holder.mTitleTextView = (TextView) convertView.findViewById(R.id.title);
			holder.mMeetingIcon = (ImageView) convertView.findViewById(R.id.meetingIcon);
			holder.mTimeTextView = (TextView) convertView.findViewById(R.id.time);
			holder.mCountTextView = (TextView) convertView.findViewById(R.id.count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!TextUtils.isEmpty(mData.get(position).logolarge)) {
			ImageLoaderUtil.displayImage(mData.get(position).logolarge, holder.mMeetingIcon);
		} else {
			holder.mMeetingIcon.setImageResource(R.drawable.icon_default_meeting);
		}

		holder.mTitleTextView.setText(mData.get(position).name);
		String time = DateUtil.getCreatTimeFromSeconds(mData.get(position).start, mData.get(position).end);
		holder.mTimeTextView.setText(time);
		String content = mContext.getString(R.string.has_join_start) + mData.get(position).count
				+ mContext.getString(R.string.has_join_end);
		SpannableString mTrendNameSpan;
		String count = mData.get(position).count + "";
		int start = content.indexOf(count);
		mTrendNameSpan = new SpannableString(content);
		mTrendNameSpan.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.meeting_count_color)),
				start, start + count.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		holder.mCountTextView.setText(mTrendNameSpan);
		return convertView;
	}

	final static class ViewHolder {
		TextView mTitleTextView;
		ImageView mMeetingIcon;
		private TextView mTimeTextView;
		private TextView mCountTextView;

		@Override
		public int hashCode() {
			return this.mTitleTextView.hashCode() + mMeetingIcon.hashCode() + mTimeTextView.hashCode()
					+ mCountTextView.hashCode();
		}
	}

}

package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
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

import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.support.DynamicHelper;
import com.gaopai.guiren.support.DynamicHelper.DySoftCallback;
import com.gaopai.guiren.utils.DateUtil;
import com.gaopai.guiren.utils.ImageLoaderUtil;

public class SearchAdapter extends BaseAdapter {

	public final static int TYPE_HEADER = 0;
	public final static int TYPE_USER = 1;
	public final static int TYPE_TRIBE = 2;
	public final static int TYPE_MEETING = 3;
	public final static int TYPE_DYNAMIC = 4;

	private DynamicHelper dynamicHelper;

	private Context mContext;
	private LayoutInflater inflater;

	public SearchAdapter(Context context) {
		rows = new ArrayList<Row>();
		mContext = context;
		dynamicHelper = new DynamicHelper(mContext, DynamicHelper.DY_MY_LIST);
		dynamicHelper.setCallback(new DySoftCallback() {
			@Override
			public void onVoiceStart() {
				super.onVoiceStart();
				notifyDataSetChanged();
			}

			@Override
			public void onVoiceStop() {
				super.onVoiceStop();
				notifyDataSetChanged();
			}
		});
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		int type = getItem(position).type;
		switch (type) {
		case TYPE_HEADER: {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_connection_section, parent, false);
			}

			Section section = (Section) getItem(position);
			TextView textView = (TextView) convertView.findViewById(R.id.textView1);
			textView.setText(section.text);
			break;
		}
		case TYPE_TRIBE:
		case TYPE_USER: {
			if (convertView == null) {
				viewHolder = new ViewHolder();

				convertView = inflater.inflate(R.layout.item_connection_item, parent, false);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
			viewHolder.tvUserInfo = (TextView) convertView.findViewById(R.id.tv_user_info);
			viewHolder.ivHeader = (ImageView) convertView.findViewById(R.id.iv_header);
			viewHolder.tvFancyCount = (TextView) convertView.findViewById(R.id.tv_user_fancy_count);
			viewHolder.viewFancy = convertView.findViewById(R.id.tv_user_fancy);
			Object object = ((Item) getItem(position)).object;
			if (object instanceof User) {
				User user = (User) object;
				viewHolder.tvFancyCount.setVisibility(View.VISIBLE);
				viewHolder.viewFancy.setVisibility(View.VISIBLE);
				viewHolder.tvFancyCount.setText(String.valueOf(user.integral));
				viewHolder.ivHeader.setImageResource(R.drawable.default_header);
				bindSimpleItemView(viewHolder, user.realname, user.post, user.headsmall);
			} else if (object instanceof Tribe) {
				viewHolder.tvFancyCount.setVisibility(View.GONE);
				viewHolder.viewFancy.setVisibility(View.GONE);
				Tribe tribe = (Tribe) object;
				viewHolder.ivHeader.setImageResource(R.drawable.default_tribe);
				bindSimpleItemView(viewHolder, tribe.name, tribe.content, tribe.logosmall);
			}
			break;
		}

		case TYPE_MEETING: {
			Tribe meeting = (Tribe) ((Item) getItem(position)).object;
			MeetingAdapter.ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_meeting_new, null);
				holder = new MeetingAdapter.ViewHolder();

				holder.mTitleTextView = (TextView) convertView.findViewById(R.id.title);
				holder.mMeetingIcon = (ImageView) convertView.findViewById(R.id.meetingIcon);
				holder.mTimeTextView = (TextView) convertView.findViewById(R.id.time);
				holder.mCountTextView = (TextView) convertView.findViewById(R.id.count);
				convertView.setTag(holder);
			} else {
				holder = (MeetingAdapter.ViewHolder) convertView.getTag();
			}
			ImageLoaderUtil.displayImage(meeting.logolarge, holder.mMeetingIcon, R.drawable.icon_default_meeting);

			holder.mTitleTextView.setText(meeting.name);
			String time = DateUtil.getCreatTimeFromSeconds(meeting.start, meeting.end);
			holder.mTimeTextView.setText(time);
			String content = mContext.getString(R.string.has_join_start) + meeting.count
					+ mContext.getString(R.string.has_join_end);
			SpannableString mTrendNameSpan;
			String count = meeting.count + "";
			int start = content.indexOf(count);
			mTrendNameSpan = new SpannableString(content);
			mTrendNameSpan.setSpan(new ForegroundColorSpan(mContext.getResources()
					.getColor(R.color.meeting_count_color)), start, start + count.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.mCountTextView.setText(mTrendNameSpan);
			break;
		}

		case TYPE_DYNAMIC: {
			TypeHolder typeBean = (TypeHolder) ((Item) getItem(position)).object;
			View view = dynamicHelper.getView(convertView, typeBean);
			view.setBackgroundColor(Color.WHITE);
			return view;
		}
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
		return 7;
	}

	@Override
	public int getItemViewType(int position) {
		int type = rows.get(position).type;
		switch (type) {
		case TYPE_HEADER:
			return 0;
		case TYPE_TRIBE:
		case TYPE_USER:
			return 1;
		case TYPE_MEETING:
			return 2;
		case TYPE_DYNAMIC: {
			TypeHolder holder = ((TypeHolder) ((Item) rows.get(position)).object);
			return DynamicAdapter.getDymaicLayoutType(holder.type) + 3;
		}
		default:
			return 0;
		}
	}

	private void bindSimpleItemView(ViewHolder viewHolder, String title, String info, String head) {
		viewHolder.tvUserName.setText(title);
		viewHolder.tvUserInfo.setText(info);
		ImageLoaderUtil.displayImage(head, viewHolder.ivHeader, R.drawable.default_header);
	}

	public void stopPlay() {
		dynamicHelper.stopPlayVoice();
	}
}

package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.NotifiyVo;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.NotifyMessageTable;
import com.gaopai.guiren.db.NotifyRoomTable;
import com.gaopai.guiren.db.NotifyTable;
import com.gaopai.guiren.db.NotifyUserTable;

public class NotifyAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	private List<NotifiyVo> mData = new ArrayList<NotifiyVo>();
	private Context mContext;
	public boolean mIsCancel = false;

	public NotifyAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
	}

	public void addAll(List<NotifiyVo> o) {
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

	// public HashMap<String, Bitmap> getImageBuffer(){
	// return mImageLoader.getImageBuffer();
	// }

	public void setData(List<NotifiyVo> data) {
		mData = data;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.notify_item, null);
			holder = new ViewHolder();

			holder.mContentView = (TextView) convertView.findViewById(R.id.content);
			holder.mTimeView = (TextView) convertView.findViewById(R.id.time);
			holder.mProcessedView = (TextView) convertView.findViewById(R.id.processed);
			holder.mDeleteBtn = (ImageView) convertView.findViewById(R.id.deletebtn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final NotifiyVo notify = mData.get(position);
		holder.mProcessedView.setVisibility(View.GONE);
		holder.mContentView.setText(notify.content);
		if (notify.mReadState == 1) {
			holder.mContentView.setTextColor(mContext.getResources().getColor(R.color.content_gray_color));
		} else {
			holder.mContentView.setTextColor(mContext.getResources().getColor(R.color.one_word_prompt_color));
		}

		if (notify.processed == 1) {
			holder.mProcessedView.setVisibility(View.VISIBLE);
		}

		holder.mTimeView.setText(FeatureFunction.getSecondTime(notify.time));
		holder.mDeleteBtn.setVisibility(mIsCancel ? View.VISIBLE : View.GONE);
		holder.mDeleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getWritableDatabase();
				NotifyTable table = new NotifyTable(dbDatabase);
				NotifyUserTable userTable = new NotifyUserTable(dbDatabase);
				NotifyRoomTable roomTable = new NotifyRoomTable(dbDatabase);
				NotifyMessageTable messageTable = new NotifyMessageTable(dbDatabase);
				if (notify.user != null) {
					userTable.delete(notify.mID, notify.user);
				}

				if (notify.room != null) {
					roomTable.delete(notify.mID, notify.room);
				}

				if (notify.message != null) {
					messageTable.delete(notify.mID, notify.message);
				}

				table.deleteByID(notify);
				mData.remove(position);
				NotifyAdapter.this.notifyDataSetChanged();
			}
		});

		return convertView;
	}

	final static class ViewHolder {
		TextView mContentView;
		TextView mTimeView;
		TextView mProcessedView;
		ImageView mDeleteBtn;

		@Override
		public int hashCode() {
			return this.mContentView.hashCode() + mTimeView.hashCode() + mProcessedView.hashCode()
					+ mDeleteBtn.hashCode();
		}
	}

}

package com.gaopai.guiren.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.share.BaseShareFragment;
import com.gaopai.guiren.activity.share.ShareActivity;
import com.gaopai.guiren.bean.User;

public class ShareContactAdapter extends CopyOfConnectionAdapter implements Filterable {
	private ShareActivity shareActivity;
	private BaseShareFragment shareFragment;

	public ShareContactAdapter(BaseShareFragment shareFragment) {
		super();
		this.shareFragment = shareFragment;
		shareActivity = (ShareActivity) shareFragment.getActivity();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = super.getView(position, convertView, parent);
		if (getItemViewType(position) == 0) { // Item
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			User user = ((Item) getItem(position)).user;
			viewHolder.ivCheck = (ImageView) convertView.findViewById(R.id.btn_add);
			viewHolder.ivCheck.setImageResource(R.drawable.rec_people_selected_btn);
			if (shareActivity.userMap.containsKey(user.uid)) {
				viewHolder.ivCheck.setVisibility(View.VISIBLE);
			} else {
				viewHolder.ivCheck.setVisibility(View.GONE);
			}
		}
		return convertView;
	}

	public void toggle(int position) {
		Item item = ((Item) getItem(position));
		item.isChecked = !item.isChecked;
		notifyDataSetChanged();
	}

}

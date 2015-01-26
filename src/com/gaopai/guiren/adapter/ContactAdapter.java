package com.gaopai.guiren.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.support.view.HeadView;
import com.gaopai.guiren.utils.ViewUtil;

public class ContactAdapter extends CopyOfConnectionAdapter {
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (getItemViewType(position) == 0) { // Item
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_contact_item, parent, false);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			User user = ((Item) getItem(position)).user;
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
			viewHolder.tvUserInfo = (TextView) convertView.findViewById(R.id.tv_user_info);
			viewHolder.ivHeader = (ImageView) convertView.findViewById(R.id.iv_header);
			viewHolder.layoutHeader = (HeadView) convertView.findViewById(R.id.layout_header_mvp);
			viewHolder.btnInvite = ViewUtil.findViewById(convertView, R.id.btn_invite);
			
			viewHolder.tvUserName.setText(User.getUserName(user));
			viewHolder.tvUserInfo.setText(User.getUserInfo(user));
			viewHolder.btnInvite.setTag(user);
			viewHolder.btnInvite.setOnClickListener(inviteClickListener);

			viewHolder.layoutHeader.setImage(user.headsmall);
			viewHolder.layoutHeader.setMVP(user.bigv == 1);
		} else { // Section
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_connection_section, parent, false);
			}

			Section section = (Section) getItem(position);
			TextView textView = (TextView) convertView.findViewById(R.id.textView1);
			textView.setText(section.text);
		}

		return convertView;
	}
	
	private OnClickListener inviteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			User user = (User) v.getTag();
		}
	};

	protected static final class ViewHolder {
		TextView tvUserName;
		TextView tvUserInfo;
		ImageView ivHeader;
		HeadView layoutHeader;
		
		Button btnInvite;
	}
}

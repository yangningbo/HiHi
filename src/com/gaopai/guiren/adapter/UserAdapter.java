package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.utils.ImageLoaderUtil;

public class UserAdapter extends BaseAdapter{

	private final LayoutInflater mInflater;
	private List<User> mData = new ArrayList<User>();
	private Context mContext;
	private int mType = 0;
	
	public UserAdapter(Context context,  int type){
		mInflater = (LayoutInflater)context.getSystemService(
	            Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mType = type;
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
		return position;
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
        final ViewHolder holder;  
        if (convertView==null) {  
        	convertView=mInflater.inflate(R.layout.item_user, null);   
            holder=new ViewHolder();  
              
            holder.mUserNameTextView = (TextView) convertView.findViewById(R.id.username);
            holder.mSignTextView = (TextView) convertView.findViewById(R.id.sign);
            holder.mHeaderView = (ImageView) convertView.findViewById(R.id.header);
            holder.mLevelLayout = (LinearLayout) convertView.findViewById(R.id.levellayout);
			
            convertView.setTag(holder);  
        }else {
        	holder=(ViewHolder) convertView.getTag();  
		}
        
        if(holder.mLevelLayout.getChildCount() != 0){
        	holder.mLevelLayout.removeAllViews();
        }
        
        User user = mData.get(position);
        DamiCommon.showLevel(user.integral, holder.mLevelLayout, 7, 3);
        if(!TextUtils.isEmpty(user.headsmall)){
    		ImageLoaderUtil.displayImage( user.headsmall, holder.mHeaderView);
    	}else {
    		holder.mHeaderView.setImageResource(R.drawable.default_header);
		}
        
        holder.mUserNameTextView.setText(user.realname);
        if(mType == 0){
        	holder.mSignTextView.setText(user.sign);
        }else {
        	holder.mSignTextView.setText(user.company +  " " + user.post);
		}
		
        return convertView;
	}
	
	final static class ViewHolder {  
        TextView mUserNameTextView;  
        TextView mSignTextView;
        ImageView mHeaderView;
        LinearLayout mLevelLayout;
        
        @Override
        public int hashCode() {
			return this.mUserNameTextView.hashCode() + 
					mSignTextView.hashCode() + mHeaderView.hashCode() + mLevelLayout.hashCode();
        }
    } 
	
}

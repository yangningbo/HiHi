package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.dynamic.NewDynamicBean;
import com.gaopai.guiren.bean.dynamic.NewDynamicBean.JsonContent;
import com.gaopai.guiren.bean.dynamic.NewDynamicBean.TypeHolder;
import com.gaopai.guiren.support.DynamicHelper;
import com.gaopai.guiren.utils.DateUtil;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class NewDynamicActivity extends BaseActivity {
	private PullToRefreshListView mListView;
	private MyAdapter mAdapter;
	private List<TypeHolder> dataList = new ArrayList<TypeHolder>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.general_pulltorefresh_listview);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.dynamic_msg);
		mListView = (PullToRefreshListView) findViewById(R.id.listView);
		mListView.getRefreshableView().setVerticalScrollBarEnabled(false);
		mListView.setPullRefreshEnabled(false);
		mListView.setPullLoadEnabled(false);
		mListView.setScrollLoadEnabled(false);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				int pos = position - mListView.getRefreshableView().getHeaderViewsCount();
				Intent intent = new Intent(mContext, DynamicDetailActivity.class);
				intent.putExtra(DynamicDetailActivity.KEY_SID, dataList.get(pos).jsoncontent.sid);
				mContext.startActivity(intent);
			}
		});

		mAdapter = new MyAdapter(mContext);
		mListView.setAdapter(mAdapter);
		mListView.getRefreshableView().setFastScrollEnabled(false);
		getList();
	}

	private void getList() {
		DamiInfo.getNewDynamic(new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				NewDynamicBean data = (NewDynamicBean) o;
				if (data.state != null && data.state.code == 0) {
					dataList.addAll(data.data);
					mAdapter.notifyDataSetChanged();
				} else {
					otherCondition(data.state, NewDynamicActivity.this);
				}
			}
		});

	}

	public class MyAdapter extends BaseAdapter {
		private Context mContext;

		public MyAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_new_dynamic, null);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_title);
				holder.tvInfo = (TextView) convertView.findViewById(R.id.tv_info);
				holder.ivHeader = (ImageView) convertView.findViewById(R.id.iv_header);
				holder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
				holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
				holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			TypeHolder bean = dataList.get(position);
			if (!TextUtils.isEmpty(bean.head)) {
				ImageLoaderUtil.displayImage(bean.head, holder.ivHeader);
			} else {
				holder.ivHeader.setImageResource(R.drawable.default_header);
			}
			holder.tvName.setText(bean.realname);
			holder.tvDate.setText(DateUtil.getCreateTime(bean.addtime));
			JsonContent jsonContent = bean.jsoncontent;
			holder.tvInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			holder.tvInfo.setText("");
			if (bean.type == 1) {
				holder.tvInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_dynamic_spread, 0, 0, 0);
			} else if (bean.type == 2) {
				holder.tvInfo.setText(jsonContent.recontent);
			} else {
				holder.tvInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_dynamic_favourite, 0, 0, 0);
			}

			if (bindImage(jsonContent, holder.ivPic)) {
				holder.tvContent.setVisibility(View.GONE);
			} else {
				holder.tvContent.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(jsonContent.content)) {
					holder.tvContent.setText(jsonContent.content);
				} else {
					bindText(jsonContent, holder.tvContent);
				}
			}

			return convertView;
		}

		private void bindText(JsonContent jsonContent, TextView textView) {
			switch (jsonContent.type) {
			case DynamicHelper.TYPE_SPREAD_MSG:// chat msg
				if (jsonContent.fileType == MessageType.VOICE) {
					textView.setText(R.string.voice_scheme);
				}
				break;
			case DynamicHelper.TYPE_SPREAD_LINK:
				textView.setText(jsonContent.title);
				break;
			case DynamicHelper.TYPE_SPREAD_MEETING:
				textView.setText(jsonContent.name);
				break;
			default:
				break;
			}
		}

		private boolean bindImage(JsonContent jsonContent, ImageView ivPic) {
			ivPic.setVisibility(View.VISIBLE);
			switch (jsonContent.type) {
			case DynamicHelper.TYPE_SPREAD_MSG:// chat msg
				if (!TextUtils.isEmpty(jsonContent.imgUrlS)) {
					ImageLoaderUtil.displayImage(jsonContent.imgUrlS, ivPic);
					return true;
				}
				break;
			case DynamicHelper.TYPE_SPREAD_LINK:
				if (!TextUtils.isEmpty(jsonContent.image)) {
					ImageLoaderUtil.displayImage(jsonContent.image, ivPic);
					return true;
				}
				break;
			case DynamicHelper.TYPE_SEND_DYNAMIC:
			case DynamicHelper.TYPE_SPREAD_OTHER_DYNAMIC:// dynamic
				if (jsonContent.pic != null && jsonContent.pic.size() > 0) {
					String imgaurlString = jsonContent.pic.get(0).imgUrlS;
					if (!TextUtils.isEmpty(imgaurlString)) {
						ivPic.setVisibility(View.VISIBLE);
						ImageLoaderUtil.displayImage(imgaurlString, ivPic);
						return true;
					}
				}
				break;
			case DynamicHelper.TYPE_SPREAD_TRIBE:
			case DynamicHelper.TYPE_SPREAD_MEETING:
				if (!TextUtils.isEmpty(jsonContent.logo)) {
					ImageLoaderUtil.displayImage(jsonContent.logo, ivPic);
					return true;
				}
				break;
			case DynamicHelper.TYPE_SPREAD_USER:
				if (!TextUtils.isEmpty(jsonContent.head)) {
					ImageLoaderUtil.displayImage(jsonContent.head, ivPic);
					return true;
				}
				break;

			default:
				break;
			}
			ivPic.setVisibility(View.GONE);
			return false;
		}

		private class ViewHolder {
			TextView tvName;
			TextView tvInfo;
			ImageView ivHeader;
			ImageView ivPic;
			TextView tvDate;
			TextView tvContent;
		}
	}
}

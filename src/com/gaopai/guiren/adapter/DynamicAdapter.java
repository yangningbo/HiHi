package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.DynamicDetailActivity;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.dynamic.DynamicBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.CommentBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.fragment.DynamicFragment;
import com.gaopai.guiren.support.DynamicHelper;
import com.gaopai.guiren.support.DynamicHelper.DyCallback;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyUtils;

public class DynamicAdapter extends BaseAdapter {
	private final LayoutInflater mInflater;
	private List<TypeHolder> mData = new ArrayList<TypeHolder>();
	private Context mContext;
	private DynamicFragment mFragment;

	private DynamicHelper dynamicHelper;

	public void showSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}

	private User user;

	public DynamicAdapter(DynamicFragment fragment) {
		mFragment = fragment;
		mContext = fragment.getActivity();
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dynamicHelper = new DynamicHelper(mContext, DynamicHelper.DY_LIST);
		dynamicHelper.setCallback(callback);
		user = DamiCommon.getLoginResult(mContext);
	}

	public DynamicAdapter(Activity activity) {
		mContext = activity;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dynamicHelper = new DynamicHelper(mContext, DynamicHelper.DY_MY_LIST);
		dynamicHelper.setCallback(callback);
		user = DamiCommon.getLoginResult(mContext);
	}

	private DynamicHelper.DyCallback callback = new DyCallback() {

		@Override
		public void onCommentButtonClick(TypeHolder typeHolder, String name, boolean isShowReply) {
			// TODO Auto-generated method stub
			mFragment.showChatBox(name, typeHolder, isShowReply);
			showSoftKeyboard();
		}

		@Override
		public void onBindComment(TypeHolder typeHolder, DynamicHelper.ViewHolderCommon holder) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBindCommenViewBottom(TypeHolder typeBean,
				com.gaopai.guiren.support.DynamicHelper.ViewHolderCommon viewHolder, boolean isShowComment,
				boolean isShowSpread, boolean isShowZan) {
			// TODO Auto-generated method stub
			if (isShowComment) {
				viewHolder.layoutComment.setVisibility(View.VISIBLE); // in list
				buildCommentView(viewHolder.layoutComment, typeBean);// in list
			} else {
				viewHolder.layoutComment.setVisibility(View.GONE);
			}
			if (typeBean.totalcomment > 5) {
				viewHolder.tvMoreComment.setVisibility(View.VISIBLE);
				viewHolder.tvMoreComment.setTag(typeBean);
				viewHolder.tvMoreComment.setOnClickListener(moreCommentClickListener);
			} else {
				viewHolder.tvMoreComment.setVisibility(View.GONE);
			}

			if (isShowComment || isShowSpread || isShowZan) {
				viewHolder.rlDynamicInteractive.setVisibility(View.VISIBLE);
			} else {
				viewHolder.rlDynamicInteractive.setVisibility(View.GONE);
			}
		}

		@Override
		public void notifyUpdateView() {
			DynamicAdapter.this.notifyDataSetChanged();
		}

		@Override
		public void onCommentSuccess() {
			DynamicAdapter.this.notifyDataSetChanged();
		}

		@Override
		public void onDeleteItemSuccess(TypeHolder typeHolder) {
			mData.remove(typeHolder);
			DynamicAdapter.this.notifyDataSetChanged();
		}

		@Override
		public void onDeleteItem(String dataid) {
		}

		@Override
		public void onVoiceStart() {
			DynamicAdapter.this.notifyDataSetChanged();
		}

		@Override
		public void onVoiceStop() {
			DynamicAdapter.this.notifyDataSetChanged();
		}

	};

	// put in list
	private void buildCommentView(ViewGroup parent, final TypeHolder typeBean) {
		List<CommentBean> commentBeans = typeBean.commentlist;
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		parent.removeAllViews();
		int textPadding = MyUtils.dip2px(mContext, 5);
		for (int i = 0, count = commentBeans.size(); i < count; i++) {
			final CommentBean commentBean = commentBeans.get(i);
			TextView textView = new TextView(mContext);
			textView.setBackgroundResource(R.drawable.selector_text_btn);
			textView.setCompoundDrawablePadding(MyUtils.dip2px(mContext, 5));
			textView.setPadding(textPadding, textPadding, textPadding, textPadding);
			if (i == 0) {
				textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_dynamic_comment, 0, 0, 0);
			} else {
				textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_dynamic_comment_transparent, 0, 0, 0);
			}
			textView.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			textView.setText(dynamicHelper.getCommentString(commentBean));
			textView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (commentBean.uid.equals(user.uid)) {
						return;
					}
					dynamicHelper.setCommentHolderForReply(typeBean, commentBean);
					callback.onCommentButtonClick(typeBean, commentBean.uname, true);
				}
			});
			parent.addView(textView, lp);
		}
	}

	public DynamicHelper getDynamicHelper() {
		return dynamicHelper;
	}

	public void addAll(List<TypeHolder> data) {
		mData.addAll(data);
		notifyDataSetChanged();
	}

	public void clear() {
		mData.clear();
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
		DynamicBean.TypeHolder typeBean = mData.get(position);
		return dynamicHelper.getView(convertView, typeBean);
	}

	@Override
	public int getViewTypeCount() {
		return 7;
	}

	@Override
	public int getItemViewType(int position) {
		return mData.get(position).type - 1;
	}

	private OnClickListener moreCommentClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			viewDynamicDetail((TypeHolder) v.getTag());
		}
	};

	public void viewDynamicDetail(TypeHolder typeHolder) {
		Intent intent = new Intent(mContext, DynamicDetailActivity.class);
		intent.putExtra(DynamicDetailActivity.KEY_TYPEHOLDER, typeHolder);
		mContext.startActivity(intent);
	}

	public void commentMessage(final String content, final TypeHolder typeHolder) {
		mFragment.hideChatBox();
		dynamicHelper.commentMessage(content, typeHolder);
	}
	
	public void deleteItem(String id) {
		if (mData == null) {
			return;
		}
		if(TextUtils.isEmpty(id)) {
			return;
		}
		for (TypeHolder typeHolder : mData) {
			if (typeHolder.id.equals(id)) {
				mData.remove(typeHolder);
				notifyDataSetChanged();
				return;
			}
		}
	}
	
	public void stopPlayVoice() {
		dynamicHelper.stopPlayVoice();
	}
}

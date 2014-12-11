package com.gaopai.guiren.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.FavoriteList;
import com.gaopai.guiren.bean.FavoriteList.Favorite;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.media.MediaUIHeper;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.media.SpeexPlayerWrapper.OnDownLoadCallback;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class MyFavoriteActivity extends BaseActivity {
	private PullToRefreshListView mListView;
	private MyAdapter mAdapter;

	private int page = 1;
	private boolean isFull = false;
	private List<Favorite> mFavoriteList = new ArrayList<Favorite>();

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.general_pulltorefresh_listview);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText("我的搜藏");
		mListView = (PullToRefreshListView) findViewById(R.id.listView);
		mListView.setPullRefreshEnabled(false); // 下拉刷新
		mListView.setPullLoadEnabled(false);// 上拉刷新，禁止
		mListView.setScrollLoadEnabled(true);// 滑动到底部自动刷新，启用
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getFavoriteList(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				getFavoriteList(false);
			}
		});
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		mListView.doPullRefreshing(true, 50);
		mPlayerWrapper = new SpeexPlayerWrapper(mContext, new OnDownLoadCallback() {
			@Override
			public void onSuccess(MessageInfo messageInfo) {
				// TODO Auto-generated method stub
				downVoiceSuccess(messageInfo);
			}
		});
		mPlayerWrapper.setPlayCallback(new PlayCallback());

		mListView.getRefreshableView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				showItemLongClickDialog(position);
				return false;
			}
		});
	}

	class MyCancelListener extends SimpleResponseListener {
		private int position;

		public MyCancelListener(int pos) {
			super(mContext);
			position = pos;
		}

		@Override
		public void onSuccess(Object o) {
			BaseNetBean data = (BaseNetBean) o;
			if (data.state != null && data.state.code == 0) {
				showToast(R.string.cancel_favorite_success);
				mFavoriteList.remove(position);
				mAdapter.notifyDataSetChanged();
			} else {
				otherCondition(data.state, MyFavoriteActivity.this);
			}
		}
	}

	private void showItemLongClickDialog(final int position) {
		Dialog dialog = new AlertDialog.Builder(this).setTitle(R.string.cancel_favorite)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Favorite favorite = mFavoriteList.get(position);
						MessageInfo messageInfo = favorite.message;
						if (messageInfo.type == 200) {
							DamiInfo.cancleFavoriteMessage(favorite.roomid, messageInfo.id, new MyCancelListener(
									position));
						} else {
							DamiInfo.cancleFavoriteMeetingMessage(favorite.roomid, messageInfo.id,
									new MyCancelListener(position));
						}

					}
				}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				}).create();
		dialog.show();
	}

	/**
	 * 下载成功后修改消息状态，更新数据库并播放声音
	 * 
	 * @param msg
	 * @param type
	 */
	private void downVoiceSuccess(final MessageInfo msg) {
		if (mPlayerWrapper.getMessageTag().equals(msg.tag)) {
			mPlayerWrapper.start(msg);
			mAdapter.notifyDataSetChanged();
		}
	}

	private void getFavoriteList(final boolean isRefresh) {
		String sinceID = "";
		String maxID = "";
		if (isRefresh) {
			if (mFavoriteList != null && mFavoriteList.size() != 0) {
				sinceID = mFavoriteList.get(0).id;
			}
		} else {
			if (mFavoriteList != null && mFavoriteList.size() != 0) {
				maxID = mFavoriteList.get(mFavoriteList.size() - 1).id;
			}
		}
		if (isFull) {
			mListView.setHasMoreData(!isFull);
			return;
		}
		DamiInfo.getFaoviteList(sinceID, maxID, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				final FavoriteList data = (FavoriteList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						mFavoriteList.addAll(data.data);
						mAdapter.notifyDataSetChanged();
					}
					if (data.pageInfo != null) {
						isFull = (data.pageInfo.hasMore == 0);
						if (!isFull) {
							page++;
						}
					}
					mListView.setHasMoreData(!isFull);
				} else {
					otherCondition(data.state, MyFavoriteActivity.this);
				}
			}

			@Override
			public void onFinish() {
				mListView.onPullComplete();
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mFavoriteList.size();
		}

		@Override
		public Favorite getItem(int arg0) {
			return mFavoriteList.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final Favorite favorite = getItem(position);

			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = inflateItemView(convertView);
				viewHolder = (ViewHolder) convertView.getTag();
			} else {
				viewHolder = (ViewHolderLeft) convertView.getTag();
			}
			bindView(viewHolder, favorite, position);
			return convertView;
		}
	}

	private View inflateItemView(View convertView) {
		ViewHolder viewHolder;
		convertView = mInflater.inflate(R.layout.item_favorite_chat_talk, null);
		viewHolder = ViewHolderLeft.getInstance(convertView);
		convertView.setTag(viewHolder);
		return convertView;
	}

	static class ViewHolderLeft extends ViewHolder {
		public static ViewHolderLeft getInstance(View view) {
			ViewHolderLeft viewHolderLeft = new ViewHolderLeft();
			return (ViewHolderLeft) getInstance(view, viewHolderLeft);
		}
	}

	static class ViewHolder {
		int flag = 0; // 1 好友 0 自己
		TextView tvChatTime, tvText, tvVoiceLength, tvUserName;
		ImageView ivHead, ivPhoto, ivVoice, ivZan;
		ProgressBar wiatProgressBar;
		RelativeLayout msgInfoLayout, msgLayout;
		TextView tvRoom;

		public static Object getInstance(View view, ViewHolder holder) {
			holder.msgInfoLayout = (RelativeLayout) view.findViewById(R.id.layout_msg_text_voice_holder);
			holder.msgLayout = (RelativeLayout) view.findViewById(R.id.rl_msg_holder);
			holder.tvChatTime = (TextView) view.findViewById(R.id.tv_chat_talk_time);
			holder.tvText = (TextView) view.findViewById(R.id.iv_chat_text);

			holder.ivHead = (ImageView) view.findViewById(R.id.iv_chat_talk_img_head);
			holder.ivPhoto = (ImageView) view.findViewById(R.id.iv_chat_photo);
			holder.ivVoice = (ImageView) view.findViewById(R.id.iv_chat_voice);

			holder.wiatProgressBar = (ProgressBar) view.findViewById(R.id.pb_chat_progress);
			holder.tvVoiceLength = (TextView) view.findViewById(R.id.tv_chat_voice_time_length);

			holder.tvUserName = (TextView) view.findViewById(R.id.tv_user_name);

			holder.tvRoom = (TextView) view.findViewById(R.id.tv_room);
			return holder;
		}
	}

	private SpeexPlayerWrapper mPlayerWrapper;
	private int palyedPosition;

	private void bindView(ViewHolder viewHolder, final Favorite favorite, final int position) {
		// TODO Auto-generated method stub
		final MessageInfo messageInfo = favorite.message;
		viewHolder.tvRoom.setText(messageInfo.title);
		viewHolder.tvChatTime.setText(FeatureFunction.getCreateTime(favorite.createtime));
		if (!TextUtils.isEmpty(messageInfo.headImgUrl)) {
			viewHolder.ivHead.setTag(messageInfo.headImgUrl);
			ImageLoaderUtil.displayImage(messageInfo.headImgUrl, viewHolder.ivHead);
		}
		viewHolder.tvUserName.setText(messageInfo.displayname);
		notHideViews(viewHolder, messageInfo.fileType);
		viewHolder.ivVoice.setLayoutParams(getVoiceViewLengthParams(messageInfo));
		viewHolder.msgInfoLayout.setOnClickListener(null);
		switch (messageInfo.fileType) {
		case MessageType.TEXT:
			viewHolder.tvText.setText(MyTextUtils.addHttpLinks(messageInfo.content));
			viewHolder.tvText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			break;
		case MessageType.PICTURE:
			final String path = messageInfo.imgUrlS.trim();

			ImageLoaderUtil.displayImage(path, viewHolder.ivPhoto);
			if (path.startsWith("http://")) {
				ImageLoaderUtil.displayImage(path, viewHolder.ivPhoto);
			}
			viewHolder.ivPhoto.setTag(messageInfo);
			viewHolder.ivPhoto.setOnClickListener(photoClickListener);
			break;
		case MessageType.VOICE:
			viewHolder.tvVoiceLength.setText(messageInfo.voiceTime + "''");
			viewHolder.msgInfoLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					palyedPosition = position;
					mPlayerWrapper.start(messageInfo);
				}
			});

			AnimationDrawable drawable = (AnimationDrawable) viewHolder.ivVoice.getDrawable();
			if (mPlayerWrapper.isPlay() && position == palyedPosition) {
				drawable.start();
			} else {
				drawable.stop();
				drawable.selectDrawable(0);
			}
			break;
		}
	}

	private OnClickListener photoClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			List<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
			messageInfos.add((MessageInfo) v.getTag());
			Intent intent = new Intent(mContext, ShowImagesActivity.class);
			intent.putExtra("msgList", (Serializable) messageInfos);
			intent.putExtra("position", 0);
			mContext.startActivity(intent);
		}
	};

	private void notHideViews(ViewHolder viewHolder, int which) {
		viewHolder.ivPhoto.setVisibility(View.GONE);
		viewHolder.tvText.setVisibility(View.GONE);
		viewHolder.tvVoiceLength.setVisibility(View.GONE);
		viewHolder.ivVoice.setVisibility(View.GONE);
		switch (which) {
		case MessageType.TEXT:
			viewHolder.tvText.setVisibility(View.VISIBLE);
			break;
		case MessageType.PICTURE:
			viewHolder.ivPhoto.setVisibility(View.VISIBLE);
			break;
		case MessageType.VOICE:
			viewHolder.ivVoice.setVisibility(View.VISIBLE);
			viewHolder.tvVoiceLength.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	private RelativeLayout.LayoutParams getVoiceViewLengthParams(MessageInfo messageInfo) {
		final int MAX_SECOND = 10;
		final int MIN_SECOND = 2;
		int length = messageInfo.voiceTime;
		float max = mContext.getResources().getDimension(R.dimen.voice_max_length_comment);
		float min = mContext.getResources().getDimension(R.dimen.voice_min_length_comment);
		int width = (int) min;
		if (length >= MIN_SECOND && length <= MAX_SECOND) {
			width += (length - MIN_SECOND) * (int) ((max - min) / (MAX_SECOND - MIN_SECOND));
		} else if (length > MAX_SECOND) {
			width = (int) max;
		}
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		return lp;
	}

	private class PlayCallback extends MediaUIHeper.PlayCallback {

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onStop(boolean stopAutomatic) {
			// TODO Auto-generated method stub
			mAdapter.notifyDataSetChanged();
		}
	}
}

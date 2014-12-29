package com.gaopai.guiren.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import u.aly.be;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.ReportMsgResult;
import com.gaopai.guiren.bean.ReportMsgResult.ReportMsgBean;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.media.MediaUIHeper;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.media.SpeexPlayerWrapper.OnDownLoadCallback;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class ReportMsgActivity extends BaseActivity {
	private PullToRefreshListView mListView;
	private MyAdapter mAdapter;

	private int page = 1;
	private boolean isFull = false;
	private List<ReportMsgBean> mReportList = new ArrayList<ReportMsgBean>();

	public final static int TYPE_TRIBE = 0;
	public final static int TYPE_MEETING = 1;
	public final static String KEY_TYPE = "type";
	private int type;

	public final static String KEY_TID = "tid";
	private String tid;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.general_pulltorefresh_listview);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.report);
		type = getIntent().getIntExtra(KEY_TYPE, TYPE_MEETING);
		tid = getIntent().getStringExtra(KEY_TID);
		mListView = (PullToRefreshListView) findViewById(R.id.listView);
		mListView.setPullRefreshEnabled(false); // 下拉刷新
		mListView.setPullLoadEnabled(false);// 上拉刷新，禁止
		mListView.setScrollLoadEnabled(true);// 滑动到底部自动刷新，启用
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				getReportList(false);
			}
		});
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		mPlayerWrapper = new SpeexPlayerWrapper(mContext, new OnDownLoadCallback() {
			@Override
			public void onSuccess(MessageInfo messageInfo) {
				// TODO Auto-generated method stub
				downVoiceSuccess(messageInfo);
			}
		});
		mPlayerWrapper.setPlayCallback(new PlayCallback());
		getReportList(false);
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
				mReportList.remove(position);
				mAdapter.notifyDataSetChanged();
			} else {
				otherCondition(data.state, ReportMsgActivity.this);
			}
		}
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

	private void getReportList(final boolean isRefresh) {
		if (isFull) {
			mListView.setHasMoreData(!isFull);
			return;
		}
		SimpleResponseListener listener = new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				final ReportMsgResult data = (ReportMsgResult) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						mReportList.addAll(data.data);
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
					otherCondition(data.state, ReportMsgActivity.this);
				}
			}

			@Override
			public void onFinish() {
				mListView.onPullComplete();
			}
		};
		if (type == TYPE_MEETING) {
			DamiInfo.getMeetingReportMsgList(tid, listener);
		} else {
			DamiInfo.getTribeReportMsgList(tid, listener);
		}
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mReportList.size();
		}

		@Override
		public ReportMsgBean getItem(int arg0) {
			return mReportList.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ReportMsgBean favorite = getItem(position);

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
		convertView = mInflater.inflate(R.layout.item_report, null);
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
		TextView tvText, tvVoiceLength, tvUserName, tvShideReason;
		ImageView ivHead, ivPhoto, ivVoice, ivZan;
		ProgressBar wiatProgressBar;
		RelativeLayout msgInfoLayout, msgLayout;
		Button btnErrorReport, btnKikout, btnShide;

		public static Object getInstance(View view, ViewHolder holder) {
			holder.msgInfoLayout = (RelativeLayout) view.findViewById(R.id.layout_msg_text_voice_holder);
			holder.msgLayout = (RelativeLayout) view.findViewById(R.id.rl_msg_holder);
			// holder.tvChatTime = (TextView)
			// view.findViewById(R.id.tv_chat_talk_time);
			holder.tvText = (TextView) view.findViewById(R.id.iv_chat_text);

			holder.ivHead = (ImageView) view.findViewById(R.id.iv_chat_talk_img_head);
			holder.ivPhoto = (ImageView) view.findViewById(R.id.iv_chat_photo);
			holder.ivVoice = (ImageView) view.findViewById(R.id.iv_chat_voice);

			holder.wiatProgressBar = (ProgressBar) view.findViewById(R.id.pb_chat_progress);
			holder.tvVoiceLength = (TextView) view.findViewById(R.id.tv_chat_voice_time_length);

			holder.tvUserName = (TextView) view.findViewById(R.id.tv_user_name);

			holder.tvShideReason = ViewUtil.findViewById(view, R.id.tv_reason);

			holder.btnErrorReport = ViewUtil.findViewById(view, R.id.btn_error_report);
			holder.btnKikout = ViewUtil.findViewById(view, R.id.btn_kick_tribe);
			holder.btnShide = ViewUtil.findViewById(view, R.id.btn_shide_msg);
			return holder;
		}
	}

	private SpeexPlayerWrapper mPlayerWrapper;
	private int palyedPosition;

	private void bindView(ViewHolder viewHolder, final ReportMsgBean favorite, final int position) {
		// TODO Auto-generated method stub
		final MessageInfo messageInfo = favorite.message;
		// viewHolder.tvRoom.setText(messageInfo.title);
		// viewHolder.tvChatTime.setText(FeatureFunction.getCreateTime(favorite.createtime));
		if (!TextUtils.isEmpty(messageInfo.headImgUrl)) {
			viewHolder.ivHead.setTag(messageInfo.headImgUrl);
			ImageLoaderUtil.displayImage(messageInfo.headImgUrl, viewHolder.ivHead, R.drawable.default_header);
		}
		viewHolder.tvUserName.setText(messageInfo.displayname);
		notHideViews(viewHolder, messageInfo.fileType);
		viewHolder.ivVoice.setLayoutParams(getVoiceViewLengthParams(messageInfo));
		viewHolder.msgInfoLayout.setOnClickListener(null);
		viewHolder.tvShideReason.setText(favorite.content);
		viewHolder.btnErrorReport.setTag(favorite);
		viewHolder.btnShide.setTag(favorite);
		viewHolder.btnKikout.setTag(favorite);
		viewHolder.btnErrorReport.setOnClickListener(btnActionListener);
		viewHolder.btnKikout.setOnClickListener(btnActionListener);
		viewHolder.btnShide.setOnClickListener(btnActionListener);

		if (type == TYPE_MEETING) {
			viewHolder.btnKikout.setText(R.string.kick_out_meeting);
		} else {
			viewHolder.btnKikout.setText(R.string.kick_out_tribe);
		}

		switch (messageInfo.fileType) {
		case MessageType.TEXT:
			viewHolder.tvText.setText(MyTextUtils.addHttpLinks(messageInfo.content));
			viewHolder.tvText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			break;
		case MessageType.PICTURE:
			final String path = messageInfo.imgUrlS.trim();

			ImageLoaderUtil.displayImage(path, viewHolder.ivPhoto, R.drawable.default_pic);
			if (path.startsWith("http://")) {
				ImageLoaderUtil.displayImage(path, viewHolder.ivPhoto, R.drawable.default_pic);
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

	private OnClickListener btnActionListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ReportMsgBean bean = (ReportMsgBean) v.getTag();
			switch (v.getId()) {
			case R.id.btn_error_report:
				refuseReport(bean);
				break;
			case R.id.btn_shide_msg:
				agreeReport(bean, false);
				break;
			case R.id.btn_kick_tribe:
				agreeReport(bean, true);
				break;
			default:
				break;
			}
		}
	};

	private void refuseReport(final ReportMsgBean bean) {
		SimpleResponseListener listener = new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.operate_success);
					mReportList.remove(bean);
					mAdapter.notifyDataSetChanged();
				} else {
					otherCondition(data.state, ReportMsgActivity.this);
				}
			}
		};
		if (type == TYPE_MEETING) {
			DamiInfo.refuseMeetingReport(bean.message.id, listener);
		} else {
			DamiInfo.refuseReport(bean.message.id, listener);
		}
	}

	private void agreeReport(final ReportMsgBean bean, boolean isKick) {
		SimpleResponseListener listener = new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					bean.message.mIsShide = 1;
					SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
					MessageTable table = new MessageTable(db);
					table.updateShide(bean.message);
					showToast(R.string.operate_success);
					mReportList.remove(bean);
					mAdapter.notifyDataSetChanged();
				} else {
					otherCondition(data.state, ReportMsgActivity.this);
				}
			}
		};
		String fuid = "";
		if (isKick) {
			fuid = bean.user.uid;
		}
		if (type == TYPE_MEETING) {
			DamiInfo.agreeMeetingReport(bean.message.id, fuid, listener);
		} else {
			DamiInfo.agreeReport(bean.message.id, fuid, listener);
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
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onStop(boolean stopAutomatic) {
			// TODO Auto-generated method stub
			mAdapter.notifyDataSetChanged();
		}
	}
}

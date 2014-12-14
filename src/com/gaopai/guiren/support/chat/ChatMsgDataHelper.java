package com.gaopai.guiren.support.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.AddReasonActivity;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.activity.share.ShareActivity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class ChatMsgDataHelper {
	private int mChatType;
	private Context mContext;
	private Tribe mTribe;

	public ChatMsgDataHelper(Context context, Callback callback, Tribe tribe, int chatType) {
		mContext = context;
		mTribe = tribe;
		mChatType = chatType;
		this.callback = callback;
	}

	public void setTribe(Tribe tribe) {
		mTribe = tribe;
	}

	public static interface Callback {
		public void favoriteMessage(MessageInfo msg);

		public void unFavoriteMessage(MessageInfo msg);

		public void zanMessage(MessageInfo msg);

		public void unZanMessage(MessageInfo msg);

		public void commentMessage(MessageInfo msg);
	}

	private Callback callback;

	public void updateCommentCountToDb(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.updateCommentCount(messageInfo);
	}

	public void updateZanCountToDb(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.updateAgreeCount(messageInfo);
	}

	public void updateFavoriteCountToDb(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.updateFavoriteCount(messageInfo);
	}

	public void favoriteMessage(final MessageInfo messageInfo) {
		SimpleResponseListener listener = new SimpleResponseListener(mContext, getString(R.string.request_internet_now)) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					Toast.makeText(mContext, R.string.favorite_success, Toast.LENGTH_SHORT).show();
					messageInfo.favoriteCount++;
					messageInfo.isfavorite = 1;
					updateFavoriteCountToDb(messageInfo);
					callback.favoriteMessage(messageInfo);
				} else {
					otherCondition(data.state, (Activity) mContext);
				}
			}
		};
		if (mChatType == ChatTribeActivity.CHAT_TYPE_TRIBE) {
			DamiInfo.favoriteMessage(mTribe.id, messageInfo.id, listener);
		} else if (mChatType == ChatTribeActivity.CHAT_TYPE_MEETING) {
			DamiInfo.favoriteMeetingMessage(mTribe.id, messageInfo.id, listener);
		}
	}

	public void unFavoriteMessage(final MessageInfo messageInfo) {
		SimpleResponseListener listener = new SimpleResponseListener(mContext, getString(R.string.request_internet_now)) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					Toast.makeText(mContext, R.string.cancel_favorite_success, Toast.LENGTH_SHORT).show();
					messageInfo.favoriteCount--;
					messageInfo.isfavorite = 0;
					updateFavoriteCountToDb(messageInfo);
					callback.unFavoriteMessage(messageInfo);
				} else {
					otherCondition(data.state, (Activity) mContext);
				}
			}
		};
		if (mChatType == ChatTribeActivity.CHAT_TYPE_TRIBE) {
			DamiInfo.cancleFavoriteMessage(mTribe.id, messageInfo.id, listener);
		} else if (mChatType == ChatTribeActivity.CHAT_TYPE_MEETING) {
			DamiInfo.cancleFavoriteMeetingMessage(mTribe.id, messageInfo.id, listener);
		}
	}

	public void report(final MessageInfo messageInfo, final String content) {
		SimpleResponseListener listener = new SimpleResponseListener(mContext, getString(R.string.request_internet_now)) {
			@Override
			public void onSuccess(Object o) {
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					Toast.makeText(mContext, R.string.report_success, Toast.LENGTH_SHORT).show();
				} else {
					otherCondition(data.state, (Activity) mContext);
				}
			}
		};
		if (mChatType == ChatTribeActivity.CHAT_TYPE_TRIBE) {
			DamiInfo.reportMessage(mTribe.id, messageInfo.id, content, listener);
		} else if (mChatType == ChatTribeActivity.CHAT_TYPE_MEETING) {
			DamiInfo.reportMeetingMessage(mTribe.id, messageInfo.id, content, listener);
		}
	}

	public void zanMessage(final MessageInfo messageInfo) {
		DamiInfo.agreeMessage(mTribe.id, messageInfo.id, new SimpleResponseListener(mContext,
				getString(R.string.request_internet_now)) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					if (data.state.msg.equals("赞成功")) {
						Toast.makeText(mContext, "赞同成功", Toast.LENGTH_SHORT).show();
						messageInfo.isAgree = 1;
						messageInfo.agreeCount++;
						callback.zanMessage(messageInfo);
					} else if (data.state.msg.equals("取消赞成功")) {
						Toast.makeText(mContext, "您已取消赞同", Toast.LENGTH_SHORT).show();
						messageInfo.isAgree = 0;
						messageInfo.agreeCount--;
						callback.unZanMessage(messageInfo);
					}
				} else {
					otherCondition(data.state, (Activity) mContext);
				}
			}
		});
	}

	public void goToRetrweet(MessageInfo msgInfo) {
		Intent intent = new Intent(mContext, ShareActivity.class);
		intent.putExtra(ShareActivity.KEY_TYPE, ShareActivity.TYPE_SHARE);
		intent.putExtra(ShareActivity.KEY_MESSAGE, msgInfo);
		mContext.startActivity(intent);
	}

	public void communicatePeople(MessageInfo messageInfo) {
		Intent intent = new Intent(mContext, AddReasonActivity.class);
		intent.putExtra(AddReasonActivity.KEY_MESSAGEINFO, messageInfo);
		intent.putExtra(AddReasonActivity.KEY_APLLY_TYPE, AddReasonActivity.TYPE_WAHT_COMUNICATION);
		mContext.startActivity(intent);
	}

	public void showReportDialog(final MessageInfo msgInfo) {
		final String[] levelArray = mContext.getResources().getStringArray(R.array.report_message_cause);
		Dialog dialog = new AlertDialog.Builder(mContext).setItems(levelArray, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				report(msgInfo, levelArray[which]);
			}
		}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		}).create();
		dialog.show();
	}

	public void spreadToDy(MessageInfo messageInfo) {
		DamiInfo.spreadDynamic(2, messageInfo.id, "", "", "", "", new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.spread_success);
				} else {
					otherCondition(data.state, (Activity) mContext);
				}
			}
		});
	}

	private String getString(int sid) {
		return mContext.getString(sid);
	}
	
}

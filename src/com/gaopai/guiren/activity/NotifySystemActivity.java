package com.gaopai.guiren.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.NotifyAdapter;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.NotifiyType;
import com.gaopai.guiren.bean.NotifiyVo;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.NotifyTable;
import com.gaopai.guiren.receiver.NotifySystemMessage;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

/**
 * 
 */
public class NotifySystemActivity extends BaseActivity {

	@ViewInject(id = R.id.listView)
	private PullToRefreshListView listView;

	private NotifyAdapter adapter;

	private List<NotifiyVo> mNotifyList;
	private final static int REFUSE_SEEKING_CONTACTS_REQUEST = 1111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_recommend_tribe);
		FinalActivity.initInjectedView(this);
		init();
	}

	private void init() {
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText("系统通知");

		listView.setPullLoadEnabled(false);
		listView.setPullRefreshEnabled(false);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			}
		});
		listView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				mNotifyList.get(position).mReadState = 1;
				SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
				NotifyTable table = new NotifyTable(db);
				table.update(mNotifyList.get(position));
				adapter.notifyDataSetChanged();
				// sendBroadcast(new Intent(HomeTab.REFRESH_NOTIFY_ACTION));
				// mContext.sendBroadcast(new Intent(
				// MainActivity.ACTION_UPDATE_NOTIFY_SESSION_COUNT));
				switch (mNotifyList.get(position).type) {

				case NotifiyType.PASS_INVITE_CODE:
					sendSMS(mNotifyList.get(position).phone, mNotifyList.get(position).code);
					break;

				case NotifiyType.PASS_CREATE_TRIBE:
					Intent tribeSettingIntent = new Intent(mContext, TribeDetailActivity.class);
					tribeSettingIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(tribeSettingIntent);
					break;

				case NotifiyType.APPLY_ADD_TRIBE:
					Intent applyTribeIntent = new Intent(mContext, ApplyListActivity.class);
					applyTribeIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(applyTribeIntent);
					break;

				case NotifiyType.AGREE_ADD_TRIBE:
				case NotifiyType.AGREE_INVITE_ADD_TRIBE:
				case NotifiyType.DISAGREE_ADD_TRIBE:
				case NotifiyType.DISAGREE_INVITE_ADD_TRIBE:
					// Intent intent = new Intent(mContext,
					// ChatMainActivity.class);
					// intent.putExtra(ChatMainActivity.CHAT_TYPE_KEY,
					// BaseChatActivity.TRIBE_CHAT_TYPE);
					// intent.putExtra(ChatMainActivity.TRIBE_EXTRAS,
					// mNotifyList.get(position).mRoom);
					// startActivity(intent);
					break;

				case NotifiyType.INVITE_ADD_TRIBE:
					if (mNotifyList.get(position).processed == 0) {
						showActionDialog(position, 0);
					}
					break;

				case NotifiyType.PASS_CREATE_MEETING:
					Intent meetingDetailIntent = new Intent(mContext, MeetingDetailActivity.class);
					meetingDetailIntent.putExtra(MeetingDetailActivity.KEY_MEETING_ID,
							mNotifyList.get(position).room.id);
					startActivity(meetingDetailIntent);
					break;

				case NotifiyType.APPLY_ADD_MEETING:
					Intent applyMeetingIntent = new Intent(mContext, ApplyListActivity.class);
					applyMeetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					applyMeetingIntent.putExtra("type", 1);
					startActivity(applyMeetingIntent);
					break;

				case NotifiyType.AGREE_ADD_MEETING:
				case NotifiyType.AGREE_INVITE_ADD_MEETING:
				case NotifiyType.REFUSE_ADD_MEETING:
				case NotifiyType.REFUSE_INVITE_ADD_MEETING:
					Intent meetingIntent = new Intent(mContext, MeetingDetailActivity.class);
					meetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(meetingIntent);
					break;

				case NotifiyType.INVITE_ADD_MEETING:
					if (mNotifyList.get(position).processed == 0) {
						showActionDialog(position, 1);
					}
					break;

				case NotifiyType.RECEIVE_REPORT_MSG:
					Intent reportIntent = new Intent(mContext, ReportMsgActivity.class);
					reportIntent.putExtra("id", mNotifyList.get(position).room.id);
					if (mNotifyList.get(position).message.type == 300) {
						reportIntent.putExtra("type", 1);
					}
					startActivity(reportIntent);
					break;

				case NotifiyType.BEEN_REPORTED_AGREE_REPORT_MSG:
					if (mNotifyList.get(position).message.fileType == MessageType.PICTURE) {
						List<MessageInfo> messageList = new ArrayList<MessageInfo>();
						messageList.add(mNotifyList.get(position).message);
						Intent pictureIntent = new Intent(mContext, ShowImagesActivity.class);
						pictureIntent.putExtra("msgList", (Serializable) messageList);
						// pictureIntent.putExtra("imageurl",
						// mNotifyList.get(position).message.imgUrlL);
						startActivity(pictureIntent);
					} else if (mNotifyList.get(position).message.fileType == MessageType.VOICE) {
						// List<MessageInfo> messageList = new
						// ArrayList<MessageInfo>();
						// messageList.add(mNotifyList.get(position).message);
						// Intent playIntent = new Intent(mContext,
						// SequencePlayActivity.class);
						// playIntent.putExtra("msgList", (Serializable)
						// messageList);
						// startActivity(playIntent);
					}
					break;

				case NotifiyType.REFUSE_REPORT_MSG:
					if (mNotifyList.get(position).message.fileType == MessageType.PICTURE) {
						List<MessageInfo> messageList = new ArrayList<MessageInfo>();
						messageList.add(mNotifyList.get(position).message);
						Intent pictureIntent = new Intent(mContext, ShowImagesActivity.class);
						pictureIntent.putExtra("msgList", (Serializable) messageList);
						// pictureIntent.putExtra("imageurl",
						// mNotifyList.get(position).message.imgUrlL);
						startActivity(pictureIntent);
					} else if (mNotifyList.get(position).message.fileType == MessageType.VOICE) {
						// List<MessageInfo> messageList = new
						// ArrayList<MessageInfo>();
						// messageList.add(mNotifyList.get(position).message);
						// Intent playIntent = new Intent(mContext,
						// SequencePlayActivity.class);
						// playIntent.putExtra("msgList", (Serializable)
						// messageList);
						// startActivity(playIntent);
					}
					break;

				case NotifiyType.TRIBE_KICK_OUT:
					Intent tribeKickIntent = new Intent(mContext, TribeDetailActivity.class);
					tribeKickIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(tribeKickIntent);
					break;

				case NotifiyType.MEETING_KICK_OUT:
					Intent meetingKickIntent = new Intent(mContext, MeetingDetailActivity.class);
					meetingKickIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(meetingKickIntent);
					break;

				case NotifiyType.COMMENT_MESSAGE: // 受到评论
				case NotifiyType.MESSAGE_ZAN_YOURS:// 消息被赞
					Intent commentIntent = new Intent(mContext, ChatCommentsActivity.class);
					commentIntent.putExtra(ChatCommentsActivity.INTENT_CHATTYPE_KEY,
							mNotifyList.get(position).message.type);
					commentIntent.putExtra(ChatCommentsActivity.INTENT_TRIBE_KEY, mNotifyList.get(position).room);
					commentIntent.putExtra(ChatCommentsActivity.INTENT_IDENTITY_KEY,
							mNotifyList.get(position).roomuser);
					commentIntent.putExtra(ChatCommentsActivity.INTENT_MESSAGE_KEY, mNotifyList.get(position).message);
					startActivity(commentIntent);
					break;

				case NotifiyType.SEEKING_CONTACTS:
					if (mNotifyList.get(position).processed == 0) {
						showActionDialog(position, 2);
					}
					break;

				case NotifiyType.AGREE_SEEKING_CONTACTS:
					if (mNotifyList.get(position).user != null) {
						Intent privateIntent = new Intent(mContext, ProfileActivity.class);
						privateIntent.putExtra(ProfileActivity.KEY_UID, mNotifyList.get(position).user.uid);
						startActivity(privateIntent);
					}
					break;
				case NotifiyType.APPLY_BECOME_HOST:
					applyMeetingIntent = new Intent(mContext, ApplyListActivity.class);
					applyMeetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					applyMeetingIntent.putExtra("type", 2);
					startActivity(applyMeetingIntent);
					break;
				case NotifiyType.AGREE_BECOME_HOST:
					meetingIntent = new Intent(mContext, MeetingDetailActivity.class);
					meetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(meetingIntent);
					break;
				case NotifiyType.REFUSE_BECOME_HOST:
					meetingIntent = new Intent(mContext, MeetingDetailActivity.class);
					meetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(meetingIntent);
					break;
				case NotifiyType.APPLY_BECOME_GUEST:
					applyMeetingIntent = new Intent(mContext, ApplyListActivity.class);
					applyMeetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					applyMeetingIntent.putExtra("type", 3);
					startActivity(applyMeetingIntent);
					break;
				case NotifiyType.AGREE_BECOME_GUEST:
					meetingIntent = new Intent(mContext, MeetingDetailActivity.class);
					meetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(meetingIntent);
					break;
				case NotifiyType.REFUSE_BECOME_GUEST:
					meetingIntent = new Intent(mContext, MeetingDetailActivity.class);
					meetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(meetingIntent);
					break;
				case NotifiyType.HOST_TO_GUEST:
					meetingIntent = new Intent(mContext, MeetingDetailActivity.class);
					meetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(meetingIntent);
					break;
				case NotifiyType.OTHER_DEAL_APPLY:
					meetingIntent = new Intent(mContext, MeetingDetailActivity.class);
					meetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(meetingIntent);
					break;
				case NotifiyType.BACK_TO_NORMAL:
					meetingIntent = new Intent(mContext, MeetingDetailActivity.class);
					meetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(meetingIntent);
					break;
				case NotifiyType.INVITE_TO_GUEST:
					if (mNotifyList.get(position).processed == 0) {
						showActionDialog(position, 3);
					}
					break;
				case NotifiyType.INVITE_TO_HOST:
					if (mNotifyList.get(position).processed == 0) {
						showActionDialog(position, 4);
					}
					break;
				case NotifiyType.AGREE_OR_REFUSE_HOST:
					meetingIntent = new Intent(mContext, MeetingDetailActivity.class);
					meetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(meetingIntent);
					break;
				case NotifiyType.AGREE_OR_REFUSE_GUEST:
					meetingIntent = new Intent(mContext, MeetingDetailActivity.class);
					meetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(meetingIntent);
					break;
				case NotifiyType.HOSTOR_CHANGE_TIME:
					meetingIntent = new Intent(mContext, MeetingDetailActivity.class);
					meetingIntent.putExtra("id", mNotifyList.get(position).room.id);
					startActivity(meetingIntent);
					break;

				default:
					break;
				}
			}
		});
		adapter = new NotifyAdapter(mContext);
		listView.setAdapter(adapter);
		getDataFromDb();
	}

	private void sendSMS(String telpho, String content) {
		try {
			String smsContent = mContext.getString(R.string.invitation_code_sms_start_prompt) + "\"" + content + "\""
					+ mContext.getString(R.string.invitation_code_sms_end_prompt);
			Uri smsToUri = Uri.parse("smsto:");
			Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
			sendIntent.putExtra("address", telpho); // 电话号码，这行去掉的话，默认就没有电话
			sendIntent.putExtra("sms_body", smsContent);
			sendIntent.setType("vnd.android-dir/mms-sms");
			mContext.startActivity(sendIntent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void getDataFromDb() {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
		NotifyTable table = new NotifyTable(db);
		mNotifyList = table.query();
		adapter.addAll(mNotifyList);
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				String action = intent.getAction();
				if (action.equals(NotifySystemMessage.ACTION_NOTIFY_SYSTEM_MESSAGE)) {
					SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
					NotifyTable table = new NotifyTable(db);
					mNotifyList = table.query();

					if (mNotifyList == null) {
						mNotifyList = new ArrayList<NotifiyVo>();
					}
					adapter.setData(mNotifyList);
					adapter.notifyDataSetChanged();
				}
				// else if (action.equals(NOTIFY_DESTORY_ACTION)) {
				// NotifyActivity.this.finish();
				// }
			}
		}
	};

	private void showActionDialog(final int pos, final int type) {
		String[] array = new String[4];
		array[0] = getString(R.string.agree);
		array[1] = getString(R.string.refuse);
		if (type == 0) {
			array[2] = getString(R.string.tribe_detail);
		} else if (type == 1) {
			array[2] = getString(R.string.meeting_detail);
		} else if (type == 2) {
			array[2] = getString(R.string.user_detail);
		} else if (type == 3) {
			array[2] = getString(R.string.meeting_detail);
		} else if (type == 4) {
			array[2] = getString(R.string.meeting_detail);
		}
		array[3] = getString(R.string.cancel);
		Dialog dialog = new AlertDialog.Builder(mContext).setItems(array, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					agreeJoin(pos, type);
					break;
				case 1:
					if (type != 2) {
						refuseJoin(pos, type);
					} else {
						Intent intent = new Intent(mContext, AddReasonActivity.class);
						intent.putExtra(AddReasonActivity.KEY_APLLY_TYPE, AddReasonActivity.TYPE_REFUSE_COMUNICATION);
						intent.putExtra(AddReasonActivity.KEY_MESSAGEINFO, mNotifyList.get(pos).message);
						startActivityForResult(intent, REFUSE_SEEKING_CONTACTS_REQUEST);
					}
					break;
				case 2:
					if (type == 0) {
						Intent intent = new Intent(mContext, TribeDetailActivity.class);
						intent.putExtra(TribeDetailActivity.KEY_TRIBE_ID, mNotifyList.get(pos).room.id);
						startActivity(intent);
					} else if (type == 1 || type == 3 || type == 4) {
						Intent intent = new Intent(mContext, MeetingDetailActivity.class);
						intent.putExtra(MeetingDetailActivity.KEY_MEETING_ID, mNotifyList.get(pos).room.id);
						startActivity(intent);
					} else if (type == 2) {
						Intent intent = new Intent(mContext, ProfileActivity.class);
						intent.putExtra(ProfileActivity.KEY_UID, mNotifyList.get(pos).user.uid);
						startActivity(intent);
					}
					break;
				case 3:
					break;

				default:
					break;
				}

				dialog.dismiss();
			}
		}).create();
		dialog.show();
	}

	private void agreeJoin(final int pos, final int type) {
		if (type == 0) {
			DamiInfo.agreeInvite(mNotifyList.get(pos).room.id, new MyListener(pos));
		} else if (type == 1) {
			DamiInfo.agreeMeetingInvite(mNotifyList.get(pos).room.id, new MyListener(pos));
		} else if (type == 2) {
			DamiInfo.agreeSeekingContacts(mNotifyList.get(pos).user.uid, mNotifyList.get(pos).message.id,
					new MyListener(pos));
		} else if (type == 3) {// 嘉宾
			DamiInfo.chargeinvite(mNotifyList.get(pos).room.id, "3", "1", new MyListener(pos));
		} else if (type == 4) {// 主持人
			DamiInfo.chargeinvite(mNotifyList.get(pos).room.id, "2", "1", new MyListener(pos));
		}
	}

	class MyListener extends SimpleResponseListener {
		private int postion;

		public MyListener(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		public MyListener(Context context, String progressString) {
			super(context, progressString);
		}

		public MyListener(int pos) {
			super(mContext, getString(R.string.request_internet_now));
			postion = pos;
		}

		@Override
		public void onSuccess(Object o) {
			// TODO Auto-generated method stub
			BaseNetBean data = (BaseNetBean) o;
			if (!(data.state != null && data.state.code == 0)) {
				otherCondition(data.state, NotifySystemActivity.this);
			}
			mNotifyList.get(postion).processed = 1;
			SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
			NotifyTable table = new NotifyTable(db);
			table.update(mNotifyList.get(postion));
		}
	}

	private void refuseJoin(final int pos, final int type) {
		if (type == 0) {
			DamiInfo.refuseInvite(mNotifyList.get(pos).room.id, new MyListener(pos));
		} else if (type == 1) {
			DamiInfo.refuseMeetingInvite(mNotifyList.get(pos).room.id, new MyListener(pos));
		} else if (type == 3) {// 嘉宾
			DamiInfo.chargeinvite(mNotifyList.get(pos).room.id, "3", "0", new MyListener(pos));
		} else if (type == 4) {// 主持人
			DamiInfo.chargeinvite(mNotifyList.get(pos).room.id, "2", "0", new MyListener(pos));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REFUSE_SEEKING_CONTACTS_REQUEST:
			if (resultCode == RESULT_OK) {
				MessageInfo messageInfo = (MessageInfo) data.getSerializableExtra(AddReasonActivity.KEY_MESSAGEINFO);
				String fuid = messageInfo.from;
				String msgId = messageInfo.id;
				if (!TextUtils.isEmpty(fuid) && !TextUtils.isEmpty(msgId)) {
					for (int i = 0; i < mNotifyList.size(); i++) {
						if (mNotifyList.get(i).type == NotifiyType.SEEKING_CONTACTS
								&& mNotifyList.get(i).user.uid.equals(fuid)
								&& mNotifyList.get(i).message.id.equals(msgId)) {
							mNotifyList.get(i).processed = 1;
							adapter.notifyDataSetChanged();
							break;
						}
					}
				}
			}
			break;

		default:
			break;
		}
	}
}

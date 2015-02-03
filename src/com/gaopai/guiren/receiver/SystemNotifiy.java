package com.gaopai.guiren.receiver;

import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.activity.MeetingDetailActivity;
import com.gaopai.guiren.activity.TribeActivity;
import com.gaopai.guiren.activity.TribeDetailActivity;
import com.gaopai.guiren.activity.chat.ChatBaseActivity;
import com.gaopai.guiren.bean.Identity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.NotifiyType;
import com.gaopai.guiren.bean.NotifiyVo;
import com.gaopai.guiren.bean.SNSMessage;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.IdentityTable;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.db.NotifyMessageTable;
import com.gaopai.guiren.db.NotifyRoomTable;
import com.gaopai.guiren.db.NotifyTable;
import com.gaopai.guiren.db.NotifyUserTable;
import com.gaopai.guiren.db.TribeTable;
import com.gaopai.guiren.fragment.MeetingFragment;
import com.gaopai.guiren.service.SnsService;
import com.gaopai.guiren.support.ConversationHelper;
import com.gaopai.guiren.support.NotifyHelper;
import com.gaopai.guiren.utils.Logger;

public class SystemNotifiy extends AbstractNotifiy {
	public static final int NOTION_ID = 1000000023;

	private Context mContext;
	private boolean isSendNotif = true;
	private NotifyHelper mNotifyHelper;

	public SystemNotifiy(SnsService context) {
		super(context);
		mContext = context;
		mNotifyHelper = new NotifyHelper(mContext);
	}

	@Override
	public void notifiy(SNSMessage message) {
		if (message instanceof NotifiyVo) {
			NotifiyVo notifiyVo = (NotifiyVo) message;
			notifiyVo.mID = UUID.randomUUID().toString();
			SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
			NotifyTable notifyTable = new NotifyTable(db);
			NotifyUserTable userTable = new NotifyUserTable(db);
			NotifyRoomTable table = new NotifyRoomTable(db);
			NotifyMessageTable notifyMessageTable = new NotifyMessageTable(db);
			TribeTable tribeTable = new TribeTable(db);
			MessageTable messageTable = new MessageTable(db);
			IdentityTable identityTable = new IdentityTable(db);
			String msg = "";
			switch (notifiyVo.type) {
			case NotifiyType.SYSTEM_MSG:
				msg = mContext.getString(R.string.system_info);

				break;

			case NotifiyType.REAL_VERIFY_PASS:
				User user = DamiCommon.getLoginResult(mContext);
				user.realname = notifiyVo.user.realname;
				user.phone = notifiyVo.user.phone;
				user.post = notifiyVo.user.post;
				user.depa = notifiyVo.user.depa;
				user.company = notifiyVo.user.company;
				user.auth = 1;

				DamiCommon.saveLoginResult(mContext, user);
				mContext.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
				msg = mContext.getString(R.string.pass_real_verify);
				break;

			case NotifiyType.REFUSE_REAL_VERIFY:
				msg = mContext.getString(R.string.unpass_real_verify);
				break;

			case NotifiyType.PASS_INVITE_CODE:
				msg = mContext.getString(R.string.pass_invite_code);
				break;

			case NotifiyType.REFUSE_INVITE_CODE:
				msg = mContext.getString(R.string.unpass_invite_code);
				break;

			case NotifiyType.INTEGRAL_CONTROL:
				msg = notifiyVo.content;
				break;

			case NotifiyType.PASS_CREATE_TRIBE:
				msg = mContext.getString(R.string.pass_create_tribe);
				notifiyVo.room.isjoin = 1;
				tribeTable.insert(notifiyVo.room);
				// mContext.sendBroadcast(new
				// Intent(TribeTab.UPDATE_COUNT_ACTION));
				break;

			case NotifiyType.REFUSE_CREATE_TRIBE:
				msg = mContext.getString(R.string.refuse_create_tribe);
				break;

			case NotifiyType.APPLY_ADD_TRIBE:
				msg = mContext.getString(R.string.apply_add_tribe);
				NotifiyVo notify = notifyTable.query(notifiyVo);
				if (notify != null) {
					notifiyVo.mID = notify.mID;
					if (!TextUtils.isEmpty(notify.message.id)) {
						notifyMessageTable.delete(notify.mID, notify.message);
					}
					if (!TextUtils.isEmpty(notify.room.id)) {
						table.delete(notify.mID, notify.room);
					}
					notifyTable.deleteByID(notify);
				}
				break;

			case NotifiyType.AGREE_ADD_TRIBE:
				msg = mContext.getString(R.string.agree_apply_add_tribe);
				notifiyVo.room.isjoin = 1;
				tribeTable.insert(notifiyVo.room);
				Intent applyTribeIntent = new Intent(TribeDetailActivity.ACTION_AGREE_ADD_TRIBE);
				applyTribeIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(applyTribeIntent);
				// mContext.sendBroadcast(new
				// Intent(TribeTab.UPDATE_COUNT_ACTION));
				break;

			case NotifiyType.DISAGREE_ADD_TRIBE:
				msg = mContext.getString(R.string.disagree_apply_add_tribe);
				break;

			case NotifiyType.INVITE_ADD_TRIBE:
				msg = mContext.getString(R.string.invite_add_tribe);
				break;

			case NotifiyType.AGREE_INVITE_ADD_TRIBE:
				msg = mContext.getString(R.string.agree_invite_add_tribe);
				applyTribeIntent = new Intent(TribeDetailActivity.ACTION_AGREE_ADD_TRIBE);
				applyTribeIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(applyTribeIntent);
				break;

			case NotifiyType.DISAGREE_INVITE_ADD_TRIBE:
				msg = mContext.getString(R.string.disagree_invite_add_tribe);
				break;

			case NotifiyType.PASS_CREATE_MEETING:
				msg = mContext.getString(R.string.pass_create_meeting);
				applyTribeIntent = new Intent(MeetingDetailActivity.ACTION_AGREE_ADD_MEETING);
				applyTribeIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(applyTribeIntent);
				break;

			case NotifiyType.REFUSE_CREATE_MEETING:
				msg = mContext.getString(R.string.refuse_create_meeting);
				break;

			case NotifiyType.TRIBE_KICK_OUT:
				identityTable.delete(notifiyVo.room.id);
				msg = mContext.getString(R.string.you_have_been_kick_out_tribe);
				messageTable.deleteRecord(notifiyVo.room.id);
				Intent kickIntent = new Intent(TribeActivity.ACTION_KICK_TRIBE);
				kickIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(kickIntent);
				break;

			case NotifiyType.APPLY_ADD_MEETING:
				msg = mContext.getString(R.string.apply_add_meeting);
				NotifiyVo notifyMeeting = notifyTable.query(notifiyVo);
				if (notifyMeeting != null) {
					notifiyVo.mID = notifyMeeting.mID;
					if (!TextUtils.isEmpty(notifyMeeting.message.id)) {
						notifyMessageTable.delete(notifyMeeting.mID, notifyMeeting.message);
					}
					if (!TextUtils.isEmpty(notifyMeeting.room.id)) {
						table.delete(notifyMeeting.mID, notifyMeeting.room);
					}
					notifyTable.deleteByID(notifyMeeting);
				}
				break;

			case NotifiyType.AGREE_ADD_MEETING:
				msg = mContext.getString(R.string.agree_apply_add_meeting);
				Intent applyIntent = new Intent(MeetingDetailActivity.ACTION_AGREE_ADD_MEETING);
				applyIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(applyIntent);
				mContext.sendBroadcast(new Intent(MeetingFragment.REFRESH_LIST_ACTION));
				break;

			case NotifiyType.REFUSE_ADD_MEETING:
				msg = mContext.getString(R.string.disagree_apply_add_meeting);
				break;

			case NotifiyType.INVITE_ADD_MEETING:
				msg = mContext.getString(R.string.invite_add_meeting);
				break;

			case NotifiyType.AGREE_INVITE_ADD_MEETING:
				msg = mContext.getString(R.string.agree_invite_add_meeting);
				applyIntent = new Intent(MeetingDetailActivity.ACTION_AGREE_ADD_MEETING);
				applyIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(applyIntent);
				mContext.sendBroadcast(new Intent(MeetingFragment.REFRESH_LIST_ACTION));
				break;

			case NotifiyType.REFUSE_INVITE_ADD_MEETING:
				msg = mContext.getString(R.string.disagree_invite_add_meeting);
				break;

			case NotifiyType.MEETING_KICK_OUT:
				msg = mContext.getString(R.string.you_have_been_kick_out_meeting);
				identityTable.delete(notifiyVo.room.id);
				// ConversationHelper.deleteItemAndUpadte(mContext,
				// notifiyVo.room.id);
				ConversationHelper.deleteChatItemAndUpadte(mContext, notifiyVo.room.id, false);
				Intent kickMeetingIntent = new Intent(TribeActivity.ACTION_KICK_TRIBE);
				kickMeetingIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(kickMeetingIntent);
				break;

			case NotifiyType.COMMENT_MESSAGE:
				msg = mContext.getString(R.string.new_comment);
				NotifiyVo notifyComment = notifyTable.queryComment(notifiyVo);
				if (notifyComment != null) {
					notifiyVo.mID = notifyComment.mID;
					if (!TextUtils.isEmpty(notifyComment.message.id)) {
						notifyMessageTable.delete(notifyComment.mID, notifyComment.message);
					}
					if (!TextUtils.isEmpty(notifyComment.room.id)) {
						table.delete(notifyComment.mID, notifyComment.room);
					}
					notifyTable.deleteByID(notifyComment);
//					if (notifyComment.roomuser != null) {
//						Identity identity = identityTable.query(notifyComment.room.id);
//						if (identity == null) {
//							identityTable.insert(notifyComment.room.id, notifyComment.roomuser);
//						} else {
//							identityTable.update(notifyComment.room.id, notifyComment.roomuser);
//						}
//					}
				}
				break;
			case NotifiyType.RECEIVE_REPORT_MSG:
				msg = mContext.getString(R.string.receive_report_msg);
				NotifiyVo notifyReport = notifyTable.query(notifiyVo);
				if (notifyReport != null) {
					notifiyVo.mID = notifyReport.mID;
					if (!TextUtils.isEmpty(notifyReport.message.id)) {
						notifyMessageTable.delete(notifyReport.mID, notifyReport.message);
					}
					if (!TextUtils.isEmpty(notifyReport.room.id)) {
						table.delete(notifyReport.mID, notifyReport.room);
					}
					notifyTable.deleteByID(notifyReport);
				}

				break;

			case NotifiyType.REPORTED_PERSON_RECEIVE_REPORT_MSG:
				msg = mContext.getString(R.string.agree_reported_msg);
				break;

			case NotifiyType.BEEN_REPORTED_AGREE_REPORT_MSG:
				msg = mContext.getString(R.string.reported_msg);
				break;

			case NotifiyType.REFUSE_REPORT_MSG:
				msg = mContext.getString(R.string.disagree_reported_msg);
				break;

			case NotifiyType.FOLLOW_NOTIFY:
				msg = notifiyVo.content;
				break;

			case NotifiyType.SEEKING_CONTACTS:
				msg = notifiyVo.content;
				break;

			case NotifiyType.AGREE_SEEKING_CONTACTS:
				msg = notifiyVo.content;
				break;

			case NotifiyType.REFUSE_SEEKING_CONTACTS:
				msg = notifiyVo.content;
				break;

			case NotifiyType.ROOM_RECEIVE_REPORT_MSG:
				notifiyVo.message.mIsShide = 1;
				messageTable.updateShide(notifiyVo.message);
				Intent shiedIntent = new Intent(ChatBaseActivity.ACTION_SHIED_MESSAGE);
				shiedIntent.putExtra("message", notifiyVo.message);
				mContext.sendBroadcast(shiedIntent);
				return;

			case NotifiyType.FAVORITE_MESSAGE:
				MessageInfo messageInfo = messageTable.query(notifiyVo.message.tag);
				if (messageInfo != null) {
					messageInfo.favoriteCount++;
					messageTable.updateFavoriteCount(messageInfo);
					Intent favoriteIntent = new Intent(ChatBaseActivity.ACTION_FAVORITE_MESSAGE);
					favoriteIntent.putExtra("message", messageInfo);
					mContext.sendBroadcast(favoriteIntent);
				} else {
					Intent favoriteIntent = new Intent(ChatBaseActivity.ACTION_FAVORITE_MESSAGE);
					favoriteIntent.putExtra("message", notifiyVo.message);
					mContext.sendBroadcast(favoriteIntent);
				}
				return;

			case NotifiyType.UNFAVORITE_MESSAGE:
				if (notifiyVo.user != null && notifiyVo.user.uid.equals(DamiCommon.getUid(mContext))) {
					// do not notify me if I give this action.
					return;
				}
				MessageInfo unfavoriteMessage = messageTable.query(notifiyVo.message.tag);
				if (unfavoriteMessage != null) {
					unfavoriteMessage.favoriteCount--;
					messageTable.updateFavoriteCount(unfavoriteMessage);
					Intent favoriteIntent = new Intent(ChatBaseActivity.ACTION_UNFAVORITE_MESSAGE);
					favoriteIntent.putExtra("message", unfavoriteMessage);
					mContext.sendBroadcast(favoriteIntent);
				} else {
					Intent favoriteIntent = new Intent(ChatBaseActivity.ACTION_UNFAVORITE_MESSAGE);
					favoriteIntent.putExtra("message", notifiyVo.message);
					mContext.sendBroadcast(favoriteIntent);
				}
				return;
			case NotifiyType.MESSAGE_ZAN_ADD: {
				if (notifiyVo.user != null && notifiyVo.user.uid.equals(DamiCommon.getUid(mContext))) {
					// do not notify me if I give this action.
					return;
				}
				messageInfo = messageTable.query(notifiyVo.message.tag);
				if (messageInfo != null) {
					messageInfo.agreeCount++;
					messageTable.updateAgreeCount(messageInfo);
				}
				Intent favoriteIntent = new Intent(ChatBaseActivity.ACTION_ZAN_MESSAGE);
				favoriteIntent.putExtra("notifiyVo", notifiyVo);
				mContext.sendBroadcast(favoriteIntent);
				return;
			}
			case NotifiyType.MESSAGE_ZAN_CANCEL: {
				if (notifiyVo.user != null && notifiyVo.user.uid.equals(DamiCommon.getUid(mContext))) {
					// do not notify me if I give this action.
					return;
				}
				unfavoriteMessage = messageTable.query(notifiyVo.message.tag);
				if (unfavoriteMessage != null) {
					unfavoriteMessage.agreeCount--;
					if (unfavoriteMessage.agreeCount < 0) {
						unfavoriteMessage.agreeCount = 0;
					}
					messageTable.updateAgreeCount(unfavoriteMessage);
				}
				Intent favoriteIntent = new Intent(ChatBaseActivity.ACTION_UNZAN_MESSAGE);
				favoriteIntent.putExtra("notifiyVo", notifiyVo);
				mContext.sendBroadcast(favoriteIntent);
				return;
			}
			case NotifiyType.APPLY_BECOME_HOST:
				msg = mContext.getString(R.string.apply_to_host);
				NotifiyVo notifyMeeting1 = notifyTable.query(notifiyVo);
				if (notifyMeeting1 != null) {
					notifiyVo.mID = notifyMeeting1.mID;
					if (notifyMeeting1.message != null && !TextUtils.isEmpty(notifyMeeting1.message.id)) {
						notifyMessageTable.delete(notifyMeeting1.mID, notifyMeeting1.message);
					}
					if (!TextUtils.isEmpty(notifyMeeting1.room.id)) {
						table.delete(notifyMeeting1.mID, notifyMeeting1.room);
					}
					notifyTable.deleteByID(notifyMeeting1);
				}
				break;
			case NotifiyType.AGREE_BECOME_HOST:
				msg = mContext.getString(R.string.agree_apply_to_host);
				applyIntent = new Intent(MeetingDetailActivity.ACTION_AGREE_ADD_MEETING);
				applyIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(applyIntent);
				break;
			case NotifiyType.REFUSE_BECOME_HOST:
				msg = mContext.getString(R.string.disagree_apply_to_host);
				break;
			case NotifiyType.APPLY_BECOME_GUEST:
				msg = mContext.getString(R.string.apply_to_jiabin);
				NotifiyVo notifyMeeting2 = notifyTable.query(notifiyVo);
				if (notifyMeeting2 != null) {
					notifiyVo.mID = notifyMeeting2.mID;
					if (!TextUtils.isEmpty(notifyMeeting2.message.id)) {
						notifyMessageTable.delete(notifyMeeting2.mID, notifyMeeting2.message);
					}
					if (!TextUtils.isEmpty(notifyMeeting2.room.id)) {
						table.delete(notifyMeeting2.mID, notifyMeeting2.room);
					}
					notifyTable.deleteByID(notifyMeeting2);
				}
				break;
			case NotifiyType.AGREE_BECOME_GUEST:
				msg = mContext.getString(R.string.agree_apply_to_guest);
				applyIntent = new Intent(MeetingDetailActivity.ACTION_AGREE_ADD_MEETING);
				applyIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(applyIntent);
				break;
			case NotifiyType.REFUSE_BECOME_GUEST:
				msg = mContext.getString(R.string.disagree_apply_to_guest);
				break;
			case NotifiyType.HOST_TO_GUEST:
				msg = mContext.getString(R.string.meeting_host_to_guest);
				applyIntent = new Intent(MeetingDetailActivity.ACTION_AGREE_ADD_MEETING);
				applyIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(applyIntent);
				break;
			case NotifiyType.OTHER_DEAL_APPLY:
				msg = mContext.getString(R.string.other_host_deal);
				break;
			case NotifiyType.BACK_TO_NORMAL:
				msg = notifiyVo.content;
				applyIntent = new Intent(MeetingDetailActivity.ACTION_AGREE_ADD_MEETING);
				applyIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(applyIntent);
				break;
			case NotifiyType.INVITE_TO_GUEST:
				msg = notifiyVo.content;
				break;
			case NotifiyType.INVITE_TO_HOST:
				msg = notifiyVo.content;
				break;
			case NotifiyType.AGREE_OR_REFUSE_HOST:
				msg = notifiyVo.content;
				applyIntent = new Intent(MeetingDetailActivity.ACTION_AGREE_ADD_MEETING);
				applyIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(applyIntent);
				break;
			case NotifiyType.AGREE_OR_REFUSE_GUEST:
				msg = notifiyVo.content;
				applyIntent = new Intent(MeetingDetailActivity.ACTION_AGREE_ADD_MEETING);
				applyIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(applyIntent);
				break;
			case NotifiyType.HOSTOR_CHANGE_TIME:
				msg = notifiyVo.content;
				applyIntent = new Intent(MeetingDetailActivity.ACTION_AGREE_ADD_MEETING);
				applyIntent.putExtra("id", notifiyVo.room.id);
				mContext.sendBroadcast(applyIntent);
				break;
			case NotifiyType.HOSTOR_CANCEL_MEETING:
				msg = notifiyVo.content;
				mContext.sendBroadcast(new Intent(MeetingFragment.REFRESH_LIST_ACTION));
				mContext.sendBroadcast(new Intent(MeetingDetailActivity.ACTION_MEETING_CANCEL));
				break;
			case NotifiyType.MESSAGE_ZAN_YOURS:
				msg = notifiyVo.content;
				NotifiyVo notifyComment1 = notifyTable.queryComment(notifiyVo);
				if (notifyComment1 != null) {
					notifiyVo.mID = notifyComment1.mID;
					if (!TextUtils.isEmpty(notifyComment1.message.id)) {
						notifyMessageTable.delete(notifyComment1.mID, notifyComment1.message);
					}
					if (!TextUtils.isEmpty(notifyComment1.room.id)) {
						table.delete(notifyComment1.mID, notifyComment1.room);
					}
					notifyTable.deleteByID(notifyComment1);
				}
//				if (notifiyVo.roomuser != null) {
//					Identity identity = identityTable.query(notifiyVo.room.id);
//					if (identity == null) {
//						identityTable.insert(notifiyVo.room.id, notifiyVo.roomuser);
//					} else {
//						identityTable.update(notifiyVo.room.id, notifiyVo.roomuser);
//					}
//				}
				break;
			default:
				msg = notifiyVo.content;
				break;
			}

			if (!(notifiyVo.user == null) && !TextUtils.isEmpty(notifiyVo.user.uid)) {
				User user = userTable.query(notifiyVo.mID, notifiyVo.user.uid);
				if (user == null) {
					user = notifiyVo.user;
					userTable.insert(notifiyVo.mID, user);
				} else {
					user = notifiyVo.user;
					userTable.update(notifiyVo.mID, user);
				}
			}

			if (!(notifiyVo.room == null) && !TextUtils.isEmpty(notifiyVo.room.id)) {
				Tribe tribe = table.query(notifiyVo.mID, notifiyVo.room.id);
				if (tribe == null) {
					tribe = notifiyVo.room;
					table.insert(notifiyVo.mID, notifiyVo.room);
				} else {
					tribe = notifiyVo.room;
					table.update(notifiyVo.mID, tribe);
				}
			}

			if (!(notifiyVo.message == null) && !TextUtils.isEmpty(notifiyVo.message.id)) {
				MessageInfo messageInfo = notifyMessageTable.query(notifiyVo.mID, notifiyVo.message.id);
				if (messageInfo == null) {
					messageInfo = notifiyVo.message;
					notifyMessageTable.insert(notifiyVo.mID, notifiyVo.message);
				} else {
					messageInfo = notifiyVo.message;
					notifyMessageTable.update(notifiyVo.mID, notifiyVo.message);
				}
			}

			notifyTable.insert(notifiyVo);

			// mContext.sendBroadcast(new
			// Intent(HomeTab.REFRESH_NOTIFY_ACTION));
			// mContext.sendBroadcast(new
			// Intent(MainActivity.ACTION_UPDATE_NOTIFY_SESSION_COUNT));

			mNotifyHelper.notifySystemMessage(msg, notifiyVo);
		}
	}

}

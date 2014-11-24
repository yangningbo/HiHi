package com.gaopai.guiren.support;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyUtils;

public class MessageHelper {
	public final static int TYPE_PERSON = 1;
	public final static int TYPE_TRIBE = 2;
	
	public static interface DeleteCallback {
		public void onStart();
		public void onEnd();
	}
	
	public static class UIHandler extends Handler {
		public UIHandler(DeleteCallback callback) {
			super(Looper.getMainLooper());
			this.callback = callback;
		}
		
		private DeleteCallback callback;
		

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				callback.onStart();
				break;
			case 1:
				callback.onEnd();

			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	}
	

	public static void clearChatCache(final Context context, final String id, final int type, final DeleteCallback callback) {
		final UIHandler handler = new UIHandler(callback);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(0);
				SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
				MessageTable messageTable = new MessageTable(db);
				List<MessageInfo> messageInfos = messageTable.queryDeleteMessageInfos(id, type);
				for (MessageInfo messageInfo : messageInfos) {
					Logger.d(MessageHelper.class, messageInfo.voiceUrl);
					if (messageInfo.fileType == MessageType.VOICE) {
						String url = messageInfo.voiceUrl;
						String fileName = null;
						if (!url.contains("AUDIO_")) {
							fileName = FeatureFunction.generator(url);
						} else {
							fileName = new File(url).getName();
						}
						Logger.d(MessageHelper.class, fileName);
						File file = new File(MyUtils.getAudioPath(context), fileName);
						if (file.exists()) {
							file.delete();
						}
					}
				}
				messageTable.deleteTribe(id);
				handler.sendEmptyMessage(1);
			}
		}).start();
	}
}

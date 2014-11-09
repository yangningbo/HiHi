package com.gaopai.guiren.media;

import com.gaopai.guiren.bean.MessageInfo;

import android.R.integer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MediaUIHeper {
	private final static int MSG_ON_START = 0;
	private final static int MSG_ON_STOP = 1;
	private final static int MSG_ON_RECORD_STOP = 2;
	private final static int MSG_ON_RECORDING = 3;
	private final static int MSG_ON_PLAY_STOP = 4;

	private MediaStateCallback callback;

	public static abstract class RecordCallback implements MediaStateCallback {
		public final static String KEY_TIME = "key_time";
		public final static String KEY_PATH = "key_path";

		public abstract void onRecording(int volume, float time);
		@Override
		public void onStop() {}
		public abstract void onStop(float recordingTime, String path);
	}
	public static  abstract class PlayCallback implements MediaStateCallback {
		public final static String KEY_STOP_AUTO = "key_auto";
		@Override
		public void onStop() {}
		public abstract void onStop(boolean stopAutomatic);
	}

	private Handler mHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ON_START:
				callback.onStart();
				break;
			case MSG_ON_STOP:
				callback.onStop();
				break;
			case MSG_ON_RECORD_STOP:
				((RecordCallback) callback).onStop(
						msg.getData().getFloat(RecordCallback.KEY_TIME), msg
								.getData().getString(RecordCallback.KEY_PATH));
				break;
			case MSG_ON_RECORDING:
				((RecordCallback) callback).onRecording(msg.arg1,
						(Float) msg.obj);
				break;
			case MSG_ON_PLAY_STOP:
				((PlayCallback) callback).onStop((Boolean) msg.obj); 
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public static MediaUIHeper getMediaUIHeper(MediaStateCallback callback) {
		MediaUIHeper uiHelper = new MediaUIHeper();
		uiHelper.callback = callback;
		return uiHelper;
	}

	public void onStart() {
		Message message = mHandler.obtainMessage(MSG_ON_START);
		message.sendToTarget();
	}
	public void onStop() {
		Message message = mHandler.obtainMessage(MSG_ON_STOP);
		message.sendToTarget();
	}
	public void onPlayStop(boolean stopAutomatic) {
		Message message = mHandler.obtainMessage(MSG_ON_PLAY_STOP);
		message.obj = stopAutomatic;
		message.sendToTarget();
	}

	public void onRecordStop(float time, String path) {
		Message message = mHandler.obtainMessage(MSG_ON_RECORD_STOP);
		Bundle bundle = new Bundle();
		bundle.putString(RecordCallback.KEY_PATH, path);
		bundle.putFloat(RecordCallback.KEY_TIME, time);
		message.setData(bundle);
		message.sendToTarget();
	}

	public void onRecording(int volume, float time) {
		Message message = mHandler.obtainMessage(MSG_ON_RECORDING);
		message.arg1 = volume;
		message.obj = time;
		message.sendToTarget();
	}

}

package com.gaopai.guiren.media;

import java.io.File;

import android.content.Context;
import android.widget.Toast;

import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyUtils;

public class SpeexRecorderWrapper {

	public static final int MIN_TIME = 2; // 录音的下线
	public static final int MAX_TIME = 20; // 录音的下线

	private SpeexRecorder recorder;
	private MediaUIHeper.RecordCallback recordCallback;
	private MediaUIHeper uiHeper;
	private Context mContext;

	private float recordTime;
	private String recordPath;
	private boolean isCancelByUser = false;

	// when count down time to zero while releasing the record button, avoid
	// send voice twice
	private boolean hasSend = false;

	public SpeexRecorderWrapper(Context context) {
		mContext = context;
	}

	public void start() {
		if (recorder != null && recorder.isRecording()) {
			return;
		}
		recordPath = new File(MyUtils.getAudioPath(mContext), MyUtils.getAudioName()).getAbsolutePath();
		recorder = new SpeexRecorder(recordPath);
		Thread thread = new Thread(recorder);
		thread.start();
		recorder.setRecording(true);
		Thread recordTimeThread = new Thread(new RecordTimeThread());
		recordTimeThread.start();
		isCancelByUser = false;
		if (recordCallback != null) {
			uiHeper.onStart();
		}
		hasSend = false;
	}

	public void stop() {
		if (hasSend) {
			return;
		}
		recorder.setRecording(false);
		if (recordTime <= MIN_TIME || isCancelByUser) {
			File file = new File(recordPath);
			if (file.exists()) {
				file.delete();
			}
			if (recordTime < SpeexRecorderWrapper.MIN_TIME) {
				Toast.makeText(mContext, mContext.getString(R.string.record_time_too_short), Toast.LENGTH_SHORT).show();
			}
			return;
		}
		hasSend = true;
		if (recordCallback != null) {
			uiHeper.onRecordStop(recordTime, recordPath);
		}
	}

	private class RecordTimeThread implements Runnable {

		@Override
		public void run() {
			recordTime = 0;
			while (recorder.isRecording()) {
				uiHeper.onRecording(getVolume(), recordTime);
				try {
					Thread.sleep(200);
					recordTime += 0.2;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isRecording() {
		return recorder.isRecording();
	}

	public void setRecordallback(MediaUIHeper.RecordCallback callback) {
		recordCallback = callback;
		uiHeper = MediaUIHeper.getMediaUIHeper(callback);
	}

	public int getVolume() {
		return recorder.getVolum();
	}

	public void setCancelByUser(boolean isCancel) {
		isCancelByUser = isCancel;
	}

	public String getRecordPath() {
		return recordPath;
	}

	public float getRecordTime() {
		return recordTime;
	}

}

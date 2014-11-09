package com.gaopai.guiren.media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import android.content.Context;
import android.widget.Toast;

import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.BaseChatAdapter;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageState;
import com.gaopai.guiren.net.Utility;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.volley.AjaxCallBack;
import com.gaopai.guiren.volley.VoiceTask;

public class SpeexPlayerWrapper {
	private SpeexPlayer speexPlayer;
	private MediaUIHeper.PlayCallback playCallback;
	private String url;
	private MessageInfo messageInfo;
	private Context context;

	public SpeexPlayerWrapper(Context context) {
		this.context = context;
	}

	public SpeexPlayerWrapper(Context context, OnDownLoadCallback callback) {
		this.context = context;
		this.downLoadCallback = callback;
	}

	public void start(MessageInfo messageInfo) {
		if (mCurrentModel != BaseChatAdapter.MODEL_VOICE) {
			return;
		}
		this.messageInfo = messageInfo;
		if (messageInfo == null)
			return;
		if (speexPlayer != null && speexPlayer.isPlay()) {
			speexPlayer.stop();
			if (messageInfo.voiceUrl.equals(url)) {
				return;
			}
		}
		url = messageInfo.voiceUrl;
		String fileName = null;
		if (!url.contains("AUDIO_")) {
			fileName = FeatureFunction.generator(url);
		} else {
			fileName = url;
		}
		File file = new File(MyUtils.getAudioPath(context), fileName);
		if (!file.exists()) {
			if (url.startsWith("http://")) {
				messageInfo.sendState = 4;// 开始下载
				down(messageInfo);
				return;
			}
		} else {
			speexPlayer = new SpeexPlayer(file.getAbsolutePath(), messageInfo.samplerate);
			speexPlayer.startPlay(playCallback);
			return;
		}

		if (!new File(url).exists()) {
			Toast.makeText(context, "没有文件...", Toast.LENGTH_SHORT).show();
			return;
		}
		speexPlayer = new SpeexPlayer(url, messageInfo.samplerate);
		speexPlayer.startPlay(playCallback);
	}

	public void stop() {
		if (speexPlayer != null && speexPlayer.isPlay()) {
			speexPlayer.stop();
		}
	}

	public String getMessageTag() {
		return messageInfo.tag;
	}

	private int mCurrentModel = BaseChatAdapter.MODEL_VOICE;

	public void setCurrentMode(int mode) {
		mCurrentModel = mode;
	}

	public void setPlayCallback(MediaUIHeper.PlayCallback callback) {
		playCallback = callback;
	}

	public boolean isPlay() {
		if (speexPlayer != null) {
			return speexPlayer.isPlay();
		}
		return false;
	}

	public static interface OnDownLoadCallback {
		public void onSuccess(MessageInfo messageInfo);
	}

	private OnDownLoadCallback downLoadCallback;
	private List<String> mDownVoiceList = new ArrayList<String>();

	// 下载音频
	private synchronized void down(final MessageInfo msg) {
		if (!FeatureFunction.checkSDCard()) {
			return;
		}
		if (mDownVoiceList.contains(msg.voiceUrl)) {
			Toast.makeText(context, context.getString(R.string.download_voice), Toast.LENGTH_SHORT).show();
			return;
		}
		mDownVoiceList.add(msg.voiceUrl);
		File voicePath = MyUtils.getAudioPath(context);
		String tag = FeatureFunction.generator(msg.voiceUrl);
		String tagName = new File(voicePath, tag).getAbsolutePath();
		HttpGet get = new HttpGet(msg.voiceUrl);
		DefaultHttpClient client = (DefaultHttpClient) Utility.getNewHttpClient(Utility.DEFAULT_TIMEOUT);

		VoiceTask<File> voiceTask = new VoiceTask<File>(client, new SyncBasicHttpContext(new BasicHttpContext()),
				new AjaxCallBack<File>() {
					@Override
					public void onSuccess(File t) {
						super.onSuccess(t);
						mDownVoiceList.remove(msg.voiceUrl);
						downLoadCallback.onSuccess(msg);
					}

					@Override
					public void onFailure(Throwable t, String strMsg) {
						super.onFailure(t, strMsg);
						Toast.makeText(context, context.getString(R.string.download_voice_error) + strMsg,
								Toast.LENGTH_SHORT).show();
						mDownVoiceList.remove(msg.voiceUrl);
					}
				});

		Executor executor = Executors.newFixedThreadPool(5, new ThreadFactory() {
			private final AtomicInteger mCount = new AtomicInteger(1);

			@Override
			public Thread newThread(Runnable r) {
				Thread tread = new Thread(r, "FinalHttp #" + mCount.getAndIncrement());
				tread.setPriority(Thread.NORM_PRIORITY - 1);
				return tread;
			}
		});
		voiceTask.executeOnExecutor(executor, get, tagName);
	}
}

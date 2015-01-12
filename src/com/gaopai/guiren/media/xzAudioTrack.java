package com.gaopai.guiren.media;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class xzAudioTrack {
	private static final String TAG = "xzAudioTrack";
	private AudioTrack mAudioTrack = null;
	private Thread mTrackThread = null;
	private boolean mIsStop = false;
	private boolean mIsActive = false;
	private MediaUIHeper uiHeper;

	private int m_iSampleRate = 8000; // 采样率
	private int m_iChannel = 1; // 通道
	private int m_iBitRate = 16; // 比特率

	private MediaUIHeper.PlayCallback playCallback;

	private void openAudioTrack() {
		// 初始化系统录音
		int config = AudioFormat.CHANNEL_OUT_MONO;
		if (m_iChannel == 2) {
			config = AudioFormat.CHANNEL_OUT_STEREO;
		}

		int bit = AudioFormat.ENCODING_PCM_16BIT;
		if (m_iBitRate == 8) {
			bit = AudioFormat.ENCODING_PCM_8BIT;
		}

		int maxjitter = AudioTrack.getMinBufferSize(m_iSampleRate, config, bit);

		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, m_iSampleRate, config, bit, 2 * maxjitter,
				AudioTrack.MODE_STREAM);

		if (mAudioTrack != null) {
			mAudioTrack.play();
		} else {
			Log.e(TAG, "Open the audio track failed");
		}
		mIsActive = true;
	}
	
	public boolean isPlay() {
		return mIsActive;
	}

	private void closeAudioTrack() {
		if (mAudioTrack != null && mIsActive) {
			mIsActive = false;
			mAudioTrack.stop();
			mAudioTrack.release();
			mAudioTrack = null;
			uiHeper.onPlayStop(false);
		}
	}

	public void init(int sampleRate, int channel, int bitRate) {
		if (sampleRate > 1) {
			m_iSampleRate = sampleRate;
		}

		m_iChannel = channel;
		m_iBitRate = bitRate;
	}

	public xzAudioTrack(int sampleRate, MediaUIHeper.PlayCallback playCallback) {
		init(sampleRate, 1, 16);
		this.playCallback = playCallback;
	}

	public void start(final String filePath) {
		uiHeper = MediaUIHeper.getMediaUIHeper(playCallback);
		uiHeper.onStart();
		closeAudioTrack();
		openAudioTrack();

		mTrackThread = new Thread(new Runnable() {
			@SuppressWarnings("resource")
			@Override
			public void run() {
				byte[] buffer = new byte[1024];
				int readLen = 0;

				try {
					FileInputStream fin = new FileInputStream(filePath);

					do {
						try {
							readLen = fin.read(buffer);
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (mAudioTrack != null) {
							mAudioTrack.write(buffer, 0, readLen);
						}

					} while (readLen > 0 && !mIsStop);

					new Thread(new Runnable() {
						@Override
						public void run() {
							if (!mIsStop) {
								mIsActive = false;
								uiHeper.onPlayStop(true);
							}
						}
					}).start();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		mTrackThread.start();
	}

	public void stop() {
		mIsStop = true;
		try {
			if (mTrackThread != null) {
				mTrackThread.join();
				mTrackThread = null;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		closeAudioTrack();
	}
}

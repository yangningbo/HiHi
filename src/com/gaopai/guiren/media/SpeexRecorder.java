package com.gaopai.guiren.media;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.gauss.speex.encode.SpeexEncoder;

public class SpeexRecorder implements Runnable {

	// private Logger log = LoggerFactory.getLogger(SpeexRecorder.class);
	private volatile boolean isRecording;
	private final Object mutex = new Object();
	private static final int frequency = 8000;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	public static int packagesize = 160;
	private String fileName = null;

	public SpeexRecorder(String fileName) {
		super();
		this.fileName = fileName;
	}

	AudioRecord recordInstance;
	SpeexEncoder encoder;

	@Override
	public void run() {
		synchronized (mutex) {
			while (!this.isRecording) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					throw new IllegalStateException("Wait() interrupted!", e);
				}
			}
		}
		
		SpeexEncoder encoder = new SpeexEncoder(this.fileName);
		Thread encodeThread = new Thread(encoder);
		encoder.setRecording(true);
		encodeThread.start();

		
		

		int bufferRead = 0;
		int bufferSize = AudioRecord.getMinBufferSize(frequency,
				AudioFormat.CHANNEL_IN_MONO, audioEncoding);

		short[] tempBuffer = new short[packagesize];

		recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC,
				frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding,
				bufferSize);

		try {
			recordInstance.startRecording();
		} catch (IllegalStateException e) {
			isRecording = false;
			encoder.setRecording(false);
			e.printStackTrace();
			return;
		}

		byte[] buffer = new byte[bufferSize];

		while (this.isRecording) {
			// log.debug("start to recording.........");
			bufferRead = recordInstance.read(tempBuffer, 0, packagesize);

			Log.d("record", "after = " + bufferRead + "   " + tempBuffer.length);

			// if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
			// throw new IllegalStateException(
			// "read() returned AudioRecord.ERROR_INVALID_OPERATION");
			//
			// } else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
			// throw new IllegalStateException(
			// "read() returned AudioRecord.ERROR_BAD_VALUE");
			// } else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
			// throw new IllegalStateException(
			// "read() returned AudioRecord.ERROR_INVALID_OPERATION");
			// }
			// log.debug("put data into encoder collector....");
			if (bufferRead != AudioRecord.ERROR_INVALID_OPERATION) {
				encoder.putData(tempBuffer, bufferRead);
			}

			int v = 0;
			for (int i = 0; i < tempBuffer.length; i++) {
				v += tempBuffer[i] * tempBuffer[i];
			}
			volum = (Math.abs((int) (v / (float) bufferRead) / 10000) >> 1);
			Log.d("spl", "volume is = " + volum);

		}
		Log.e("spl", "encodeThread  exit");
		// tell encoder to stop.
		encoder.setRecording(false);
		recordInstance.stop();
		recordInstance.release();
		recordInstance = null;
		
	}

	public void setRecording(boolean isRecording) {
		synchronized (mutex) {
			this.isRecording = isRecording;
			if(encoder!=null) {
				encoder.setRecording(false);
			}
			if (this.isRecording) {
				mutex.notify();
			}
		}
	}

	private int volum;
	/**
	 * 
	 * @return
	 */
	public int getVolum() {
		return volum;
	}

	public boolean isRecording() {
		synchronized (mutex) {
			return isRecording;
		}
	}
}

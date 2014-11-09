/**
 * 
 */
package com.gaopai.guiren.media;

import java.io.File;

import com.gauss.speex.encode.SpeexDecoder;

/**
 * @author Gauss
 * 
 */
public class SpeexPlayer {
	private String fileName = null;
	private SpeexDecoder speexdec = null;
	
 

	public SpeexPlayer(String fileName, int sampleRate) {

		this.fileName = fileName;
		try {
			speexdec = new SpeexDecoder(new File(this.fileName), sampleRate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startPlay(MediaUIHeper.PlayCallback callback) {
		RecordPlayThread rpt = new RecordPlayThread(callback);
		Thread th = new Thread(rpt);
		th.start();
	}

	
	public void stop() {
		speexdec.stop();
	}
	
	public boolean isPlay() {
		return speexdec.isPlay();
	}

	class RecordPlayThread extends Thread {
		
		private MediaStateCallback callback;
		
		public RecordPlayThread(MediaStateCallback callback) {
			// TODO Auto-generated constructor stub
			this.callback = callback;
		}

		@Override
		public void run() {
			try {
				if (speexdec != null)
					speexdec.decode(callback);

			} catch (Exception t) {
				t.printStackTrace();
			}
		}
	}
}

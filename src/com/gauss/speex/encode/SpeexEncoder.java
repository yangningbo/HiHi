package com.gauss.speex.encode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gauss.writer.speex.SpeexWriter;

/**
 * 
 * @author Gauss
 * 
 */
public class SpeexEncoder implements Runnable {

	private Logger log = LoggerFactory.getLogger(SpeexEncoder.class);
	private final Object mutex = new Object();
	private Speex speex = new Speex();
	// private long ts;
	public static int encoder_packagesize = 1024;
	private byte[] processedData = new byte[encoder_packagesize];

	List<ReadData> list = null;
	private volatile boolean isRecording;
	private String fileName;

	public SpeexEncoder(String fileName) {
		super();
		speex.init();
		list = Collections.synchronizedList(new LinkedList<ReadData>());
		this.fileName = fileName;

	}

	@Override
	public void run() {
		
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		int getSize = 0;
		
		synchronized (mutex) {
			while (!this.isRecording) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					throw new IllegalStateException("Wait() interrupted!", e);
				}
			}
		}
		SpeexWriter fileWriter = new SpeexWriter(fileName);
		Thread consumerThread = new Thread(fileWriter);
		fileWriter.setRecording(true);
		consumerThread.start();

		while (this.isRecording) {
			
			if (Thread.currentThread().isInterrupted()) { // 时刻检查该线程是否中断
				// 或者使用 if (Thread.interrupted()) {
				break; // 如果线程中断就退出
			}
			
			log.debug(Thread.currentThread().getId()
					+ " ======Size ============ " + list.size());
			if (list.size() == 0) {
				log.debug(Thread.currentThread().getId()
						+ " ======no data need to do encode");
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			if (list.size() > 0) {
				synchronized (mutex) {
					ReadData rawdata = list.remove(0);
					getSize = 0;
					if (rawdata.ready.length > 0) {
						getSize = speex.encode(rawdata.ready, 0, processedData,
								rawdata.size);
						log.info("after encode......................before="
								+ rawdata.size + " after="
								+ processedData.length + " getsize=" + getSize);
					}

				}
				if (getSize > 0) {
					fileWriter.putData(processedData, getSize);
					log.info("............clear....................");
					processedData = new byte[encoder_packagesize];
				}
			}
			
		}
		log.debug("encode thread exit");
		fileWriter.setRecording(false);
		fileWriter = null;
	}

	/**
	 * 
	 * @param data
	 * @param size
	 */
	public void putData(short[] data, int size) {
		ReadData rd = new ReadData();
		synchronized (mutex) {
			rd.size = size;
			System.arraycopy(data, 0, rd.ready, 0, size);
			list.add(rd);
		}
	}

	private void receiveData(short[] originData, short[] receiveData) {
		synchronized (mutex) {
			int size = originData.length + receiveData.length;
			short[] newData = new short[size];
			System.arraycopy(originData, 0, newData, 0, originData.length);
			System.arraycopy(receiveData, 0, newData, originData.length,
					receiveData.length);
			originData = newData;
		}
	}

	public void setRecording(boolean isRecording) {
		synchronized (mutex) {
			this.isRecording = isRecording;
			if (this.isRecording) {
				mutex.notify();
			}
		}
	}

	public boolean isRecording() {
		synchronized (mutex) {
			return isRecording;
		}
	}

	class ReadData {
		private int size;
		private short[] ready = new short[encoder_packagesize];
	}
}

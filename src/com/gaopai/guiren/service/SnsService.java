package com.gaopai.guiren.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.receiver.ConnectivityReceiver;
import com.gaopai.guiren.receiver.NotificationReceiver;
import com.gaopai.guiren.receiver.PhoneStateChangeListener;
import com.gaopai.guiren.service.type.XmppType;
import com.gaopai.guiren.service.type.XmppTypeManager;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.SPConst;

/**
 * 
 * 功能：聊天服务 <br />
 * 日期：2013-5-4<br />
 * 地点：西竹科技<br />
 * 版本：ver 1.0<br />
 * 
 * @since
 */
public class SnsService extends Service {
	/**
	 * 服务意外停止,发出广播.<br/>
	 * 附加参数{@link SnsService#EXTRAS_SERVICE_STOP}
	 * */
	public static final String ACTION_SERVICE_STOP = "com.gaopai.guiren.sns.service.ACTION_SERVICE_STOP";
	/**
	 * 意外停止广播中附加参数 <br />
	 * {@link SnsService#EXTRA_DES3}<br/>
	 * {@link SnsService#EXTRA_SHARED}
	 * */
	public static final String EXTRAS_SERVICE_STOP = "EXTRAS_SERVICE_STOP";
	/** 解密错误 */
	public static final int EXTRA_DES3 = 0xb;
	/** 获取shared参数为空 */
	public static final int EXTRA_SHARED = 0xc;

	/**
	 * 请求服务给出帮助 <br />
	 * 附加参数{@link SnsService#EXTRAS_REQUEST}
	 * */
	public static final String ACTION_REQUEST = "com.gaopai.guiren.sns.service.ACTION_REQUEST";
	/**
	 * 帮助附加参数 {@link SnsService#EXTRA_CONNECT_STATE}<br/>
	 * {@link SnsService#EXTRA_SUBMIT_CONNECT_STATE}
	 * */
	public static final String EXTRAS_REQUEST = "EXTRAS_REQUEST";

	/**
	 * XMPP 连接的状态
	 */
	public static final int EXTRA_CONNECT_STATE = 0xd;
	/** 提交XMPP连接状态到PHP服务器 */
	public static final int EXTRA_SUBMIT_CONNECT_STATE = 0xe;

	/**
	 * 响应广播 <br/>
	 * 附加参数:{@link SnsService#EXTRAS_RESULT}
	 * */
	public static final String ACTION_RESULT = "com.gaopai.guiren.sns.service.ACTION_RESULT";

	/**
	 * 响应附加参数
	 * <p/>
	 * <b> 连接状态 </b> <br/>
	 * {@link XmppManager#XMPP_CONNECT_STATE} <br/>
	 * {@link XmppManager#XMPP_LOGINED_STATE} <br/>
	 * {@link XmppManager#XMPP_LOGINING_STATE}
	 * <p/>
	 * <b> 连接PHP服务器 当请求XMPP连接状态发送到PHP服务器时,只有在XMPP连接成功的状态才向PHP服务器发送状态. <b/>
	 * 
	 */
	public static final String EXTRAS_RESULT = "EXTRAS_RESULT";

	/**
	 * XMPP连接状态变化发送包! {@link SnsService#EXTRAS_CHANGE}
	 */
	public static final String ACTION_CONNECT_CHANGE = "com.gaopai.guiren.sns.service.ACTION_CONNECT_CHANGE";

	/**
	 * XMPP连接状态附加参数.{@link XmppType}
	 */
	public static final String EXTRAS_CHANGE = "extra_change";

	private static final String LOGTAG = "SnsService";

	private boolean serviceRunState = false;

	private TelephonyManager telephonyManager; // 用于观察手机改变的状态
	private BroadcastReceiver notificationReceiver; //
	private BroadcastReceiver connectivityReceiver; // 网络状态监听
	private PhoneStateListener phoneStateListener;
	private ExecutorService executorService;
	private TaskSubmitter taskSubmitter;
	private TaskTracker taskTracker;
	private XmppManager xmppManager;

	private User userInfoVo;

	/** XMPP 状态存放在应用共享区 */
	private XmppTypeManager xmppTypeManager;

	public SnsService() {
		// 通知接收
		notificationReceiver = new NotificationReceiver();

		connectivityReceiver = new ConnectivityReceiver(this);
		phoneStateListener = new PhoneStateChangeListener(this);
		executorService = Executors.newSingleThreadExecutor();
		// 任务提交
		taskSubmitter = new TaskSubmitter(this);
		// 任务跟踪
		taskTracker = new TaskTracker(this);
	}

	public void connect() {
		Log.d(LOGTAG, "connect()...");

		taskSubmitter.submit(new Runnable() {
			@Override
			public void run() {
				SnsService.this.getXmppManager().connect();
			}
		});
	}

	@Override
	public void onCreate() {
		Log.d(LOGTAG, "onCreate()...");
		super.onCreate();
		if (Build.VERSION.SDK_INT < 18)
			startForeground(-1213, new Notification());
		serviceRunState = true;
		xmppTypeManager = new XmppTypeManager(getBaseContext());
		// XMPP 开启状态
		this.saveXmppType(XmppType.XMPP_STATE_START);

		userInfoVo = DamiCommon.getLoginResult(DamiApp.getInstance());
		try {
			xmppManager = new XmppManager(this, String.valueOf(userInfoVo.uid), userInfoVo.password);
		} catch (Exception e) {
			Log.d(LOGTAG, "应用出现错误!", e);
			stopService(new Intent(getBaseContext(), SnsService.class));
		}
		// 监听电话服务
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		taskSubmitter.submit(new Runnable() {
			@Override
			public void run() {
				SnsService.this.start();
			}
		});
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		serviceRunState = false;
		sendBroadcast(new Intent(SnsService.ACTION_SERVICE_STOP));
		try {
			stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d(LOGTAG, "onDestroy()...");
		super.onDestroy();
		// XMPP 服务关闭
		saveXmppType(XmppType.XMPP_STATE_STOP);
		System.gc();
		System.exit(0);
		// Intent intent = new Intent(this, SnsService.class);
		// startService(intent);
	}

	public TaskTracker getTaskTracker() {
		return taskTracker;
	}

	/**
	 * @return the xmppManager
	 */
	public XmppManager getXmppManager() {
		return xmppManager;
	}

	/**
	 * @return the executorService
	 */
	public ExecutorService getExecutorService() {
		return executorService;
	}

	/**
	 * @param taskSubmitter
	 *            the taskSubmitter to set
	 */
	public void setTaskSubmitter(TaskSubmitter taskSubmitter) {
		this.taskSubmitter = taskSubmitter;
	}

	/**
	 * @return the taskSubmitter
	 */
	public TaskSubmitter getTaskSubmitter() {
		return taskSubmitter;
	}

	private void start() {
		Log.d(LOGTAG, "start()...");
		// 注册通知接收器
		registerNotificationReceiver();
		// 注册连接通知接收器
		registerConnectivityReceiver();
		// 连接
		xmppManager.connect();
	}

	private void stop() {
		Log.d(LOGTAG, "stop()...");
		unregisterNotificationReceiver();
		unregisterConnectivityReceiver();
		xmppManager.disconnect();
		executorService.shutdown();
	}

	private void registerNotificationReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(NotificationReceiver.ACTION_SHOW_NOTIFICATION);
		filter.addAction(NotificationReceiver.ACTION_NOTIFICATION_SYSTEM);
		filter.addAction(NotificationReceiver.ACTION_NOTIFICATION_CLEARED);
		registerReceiver(notificationReceiver, filter);
	}

	private void unregisterNotificationReceiver() {
		unregisterReceiver(notificationReceiver);
	}

	private void registerConnectivityReceiver() {
		Log.d(LOGTAG, "registerConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		IntentFilter filter = new IntentFilter();
		// filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);

		// 注册网络发送改变后，接收通知
		filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectivityReceiver, filter);
	}

	private void unregisterConnectivityReceiver() {
		Log.d(LOGTAG, "unregisterConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(connectivityReceiver);
	}

	/**
	 * Class for summiting a new runnable task.
	 */
	public class TaskSubmitter {

		final SnsService SnsService;

		public TaskSubmitter(SnsService SnsService) {
			this.SnsService = SnsService;
		}

		public Future<?> submit(Runnable task) {
			Future<?> result = null;
			if (!SnsService.getExecutorService().isTerminated() && !SnsService.getExecutorService().isShutdown()
					&& task != null) {
				result = SnsService.getExecutorService().submit(task);
			}
			return result;
		}

	}

	/**
	 * Class for monitoring the running task count.
	 */
	public class TaskTracker {

		final SnsService SnsService;

		public int count;

		public TaskTracker(SnsService SnsService) {
			this.SnsService = SnsService;
			this.count = 0;
		}

		public void increase() {
			synchronized (SnsService.getTaskTracker()) {
				SnsService.getTaskTracker().count++;
				Log.d(LOGTAG, "Incremented task count to " + count);
			}
		}

		public void decrease() {
			synchronized (SnsService.getTaskTracker()) {
				SnsService.getTaskTracker().count--;
				Log.d(LOGTAG, "Decremented task count to " + count);
			}
		}

	}

	/**
	 * 保存xmpp状态
	 * 
	 * @param type
	 *            {@link XmppType} 作者:fighter <br />
	 *            创建时间:2013-6-1<br />
	 *            修改时间:<br />
	 */
	public void saveXmppType(String type) {
		Intent intent = new Intent(SnsService.ACTION_CONNECT_CHANGE);
		intent.putExtra(SnsService.EXTRAS_CHANGE, type);
		sendBroadcast(intent);
		xmppTypeManager.saveXmppType(type);
	}

	public boolean isServiceRunState() {
		return serviceRunState;
	}

	public XmppTypeManager getXmppTypeManager() {
		return xmppTypeManager;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(LOGTAG, "onBind()...");
		return null;
	}

	public User getUserInfoVo() {
		return userInfoVo;
	}

	@SuppressLint("NewApi")
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
		restartServiceIntent.setPackage(getPackageName());

		PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1,
				restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000,
				restartServicePendingIntent);
		super.onTaskRemoved(rootIntent);
	}

	private static final String ACTION_KEEPALIVE = "org.devtcg.demo.keepalive.KEEP_ALIVE";
	private static final long KEEP_ALIVE_INTERVAL = 1000 * 60 * 28;

	private void startKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, SnsService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + KEEP_ALIVE_INTERVAL,
				KEEP_ALIVE_INTERVAL, pi);
	}

}

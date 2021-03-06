package com.gaopai.guiren;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.R.integer;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewDebug.IntToString;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request.Method;
import com.gaopai.guiren.activity.ApplyActivity.GetVerifyResult;
import com.gaopai.guiren.activity.InviteFriendActivity.InviteUrlResult;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.bean.BatFollowResult;
import com.gaopai.guiren.bean.CheckUpdateResult;
import com.gaopai.guiren.bean.FavoriteList;
import com.gaopai.guiren.bean.InviteNumResult;
import com.gaopai.guiren.bean.LoginResult;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.MsgConfigResult;
import com.gaopai.guiren.bean.MsgZanListResult;
import com.gaopai.guiren.bean.NewUserList;
import com.gaopai.guiren.bean.PrivacySettingResult;
import com.gaopai.guiren.bean.ReportMsgResult;
import com.gaopai.guiren.bean.TagResultBean;
import com.gaopai.guiren.bean.TribeInfoBean;
import com.gaopai.guiren.bean.TribeList;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserInfoBean;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.bean.dynamic.ConnectionBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean;
import com.gaopai.guiren.bean.dynamic.NewDynamicBean;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.bean.net.ChatMessageBean;
import com.gaopai.guiren.bean.net.IdentitityResult;
import com.gaopai.guiren.bean.net.QueryResult;
import com.gaopai.guiren.bean.net.RecommendAddResult;
import com.gaopai.guiren.bean.net.RegisterResult;
import com.gaopai.guiren.bean.net.SendDynamicResult;
import com.gaopai.guiren.bean.net.SendMessageResult;
import com.gaopai.guiren.bean.net.SimpleStateBean;
import com.gaopai.guiren.bean.net.TagResult;
import com.gaopai.guiren.bean.net.VerificationResult;
import com.gaopai.guiren.net.DamiException;
import com.gaopai.guiren.net.MorePicture;
import com.gaopai.guiren.net.Parameters;
import com.gaopai.guiren.net.Utility;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MD5;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.volley.GsonObj;
import com.gaopai.guiren.volley.IResponseListener;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.gaopai.guiren.volley.UIHelperUtil;
import com.gaopai.guiren.wxapi.WXEntryActivity.TokenBean;
import com.gaopai.guiren.wxapi.WXEntryActivity.WxUserInfo;

public class DamiInfo implements Serializable {
	private static final long serialVersionUID = 1651654562644564L;

//	public static final String HOST = "http://guirenhui.vicp.cc:8081/index.php/";// 外网
	public static final String HOST = "http://192.168.1.239:8081/index.php/";

//	 public static final String HOST = "http://guirenhui.cn/index.php/";

	// public static final String HOST = "http://59.174.108.18:8081/index.php/";

	public static final String SHARE_MEETING = "api/Forward/meeting/mid/";
	public static final String SHARE_TRIBE = "api/Forward/tribe/tid/";

	public static final String SERVER = HOST + "api/";
	public static final int LOAD_SIZE = 20;
	public static final String PRIVATE_KEY = "whjdy2013@#zz";

	public static final int LOGIN_TYPE_NOT_NEED_LOGIN = 0;
	public static final int LOGIN_TYPE_NEED_LOGIN = 1;
	public static final int LOGIN_TYPE_BOTH_OK = 2;

	/**
	 * 网络访问入口函数
	 * 
	 * @param url
	 *            请求的url
	 * @param params
	 *            请求的参数数组
	 * @param httpMethod
	 *            请求的方式
	 * @param loginType
	 *            是否登录 0--不登陆 1--需要登录 2--两种都可以
	 * @return @
	 */
	public static void request(int method, @SuppressWarnings("rawtypes") final Class cls, Map<String, Object> params,
			Object obj, IResponseListener listener, int loginType) {
		params.put("call_id", String.valueOf(System.nanoTime()));
		params.put("device", FeatureFunction.getDeviceID());
		if (!TextUtils.isEmpty(DamiCommon.getToken(DamiApp.getInstance()))) {
			params.put("token", DamiCommon.getToken(DamiApp.getInstance()));
		}
		if (loginType == 1) {
			if (TextUtils.isEmpty(DamiCommon.getUid(DamiApp.getInstance()))) {
				DamiCommon.saveLoginResult(DamiApp.getInstance(), null);
				DamiCommon.setUid("");
				DamiCommon.setToken("");
				Intent toastIntent = new Intent(MainActivity.ACTION_SHOW_TOAST);
				toastIntent.putExtra("toast_msg", DamiApp.getInstance().getString(R.string.please_login));
				DamiApp.getInstance().sendBroadcast(toastIntent);
				FeatureFunction.stopService(DamiApp.getInstance());
				DamiApp.getInstance().sendBroadcast(new Intent(MainActivity.ACTION_LOGIN_OUT));
				return;
			}
		}
		if (loginType == 1) {
			params.put("uid", DamiCommon.getUid(DamiApp.getInstance()));
		} else if (loginType == 2) {
			if (!TextUtils.isEmpty(DamiCommon.getUid(DamiApp.getInstance()))) {
				params.put("uid", DamiCommon.getUid(DamiApp.getInstance()));
			}
		}
		String function = getPort(obj, cls);
		String[] keys = new String[params.size()];
		int m = 0;
		Iterator<Entry<String, Object>> iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iter.next();
			keys[m] = entry.getKey().toString();
			m++;
		}
		Arrays.sort(keys);
		String str = function;
		for (int i = 0; i < keys.length; i++) {
			str = str + keys[i] + params.get(keys[i]);
		}
		str += DamiInfo.PRIVATE_KEY;
		String appsign = new MD5().get32MD5Str(str);
		params.put("sig", appsign);
		UIHelper.reqData(method, cls, params, obj, listener);
	}

	private static String getPort(Object obj, @SuppressWarnings("rawtypes") Class cls) {
		GsonObj gsonObj = null;
		try {
			gsonObj = (GsonObj) cls.newInstance();
			if (obj != null) {
				try {
					Field field = cls.getDeclaredField("obj");
					if (field != null) {
						field.setAccessible(true);
						field.set(gsonObj, obj);
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
			}
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return gsonObj.getInterface();
	}

	/**
	 * 登录接口
	 * 
	 * @param type
	 * @param sex
	 * @param id
	 * @param nickName
	 * @param head
	 */
	public static void getLogin(String type, String sex, String id, String nickName, String head, String password,
			String phone, String version, String os, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (!TextUtils.isEmpty(type)) {
			params.put("type", type);
		}
		if (!TextUtils.isEmpty(sex)) {
			params.put("gender", sex);
		}
		if (!TextUtils.isEmpty(id)) {
			params.put("thirdpartyid", id);
		}
		if (!TextUtils.isEmpty(nickName)) {
			params.put("nickname", nickName);
		}
		if (!TextUtils.isEmpty(password)) {
			params.put("password", password);
		}
		if (!TextUtils.isEmpty(head)) {
			params.put("head", head);
		}
		params.put("infotype", "1");

		if (!TextUtils.isEmpty(phone)) {
			params.put("phone", phone);
		}
		if (!TextUtils.isEmpty(version)) {
			params.put("version", version);
		}
		if (!TextUtils.isEmpty(os)) {
			params.put("os", os);
		}
		request(Method.POST, LoginResult.class, params, 0, listener, 0);
	}

	/**
	 * 隐式登录
	 * 
	 * @return @
	 */
	public static void hiddenLogin(IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("infotype", "1");
		request(Method.GET, LoginResult.class, params, 2, listener, 0);
	}

	/**
	 * 验证贵人码
	 * 
	 * @param code
	 *            贵人码
	 */
	public static void verifyInvitationCode(String code, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		request(Method.POST, LoginResult.class, params, 1, listener, 1);
	}

	/**
	 * 获取推荐部落列表
	 * 
	 */
	public static void getRecommendTribeList(IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		request(Method.GET, TribeList.class, params, 1, listener, 1);
	}

	/**
	 * 获取会议详情
	 * 
	 * @param id
	 *            会议id
	 * @param listener
	 */
	public static void getMeetingDetail(String id, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("meetingid", id);
		request(Method.GET, TribeInfoBean.class, params, TribeInfoBean.TYPE_MEETING_INFO, listener, 2);
	}

	/**
	 * 获取部落详情
	 * 
	 * @param id
	 *            部落id
	 * @param listener
	 */
	public static void getTribeDetail(String id, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tid", id);
		request(Method.GET, TribeInfoBean.class, params, TribeInfoBean.TYPE_TRIBE_INFO, listener, LOGIN_TYPE_NEED_LOGIN);
	}

	/**
	 * 查询
	 * 
	 * @param keyword
	 *            关键字
	 * @param type
	 *            1==只查询人脉 2=＝只查询圈子 3=＝只查询会议 4=＝只查询动态 5=所有
	 * @param typeOrder
	 * @param listener
	 */
	public static void getSearchResult(String keyword, int type, String typeOrder, int page, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", keyword);
		if (type == 5) {
			params.put("type", "");
		} else {
			params.put("type", "" + type);
		}
		params.put("typeorder", typeOrder);
		params.put("page", String.valueOf(page));
		params.put("pageSize", String.valueOf(100));
		request(Method.GET, QueryResult.class, params, 0, listener, LOGIN_TYPE_NOT_NEED_LOGIN);
	}

	public static void getFriendsList(IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fuid", DamiCommon.getUid(DamiApp.getInstance()));
		// request(Method.GET, UserList.class, params, UserList.TYPE_FRIEND,
		// listener, 2);
		request(Method.GET, UserList.class, params, 5, listener, 2);
	}

	public static void getFansList(IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fuid", DamiCommon.getUid(DamiApp.getInstance()));
		// request(Method.GET, UserList.class, params, UserList.TYPE_FRIEND,
		// listener, 2);
		request(Method.GET, UserList.class, params, UserList.TYPE_FRIEND, listener, 2);
	}

	/**
	 * 获取用户详情
	 * 
	 * @param fuid
	 *            用户id
	 * @param listener
	 */
	public static void getUserInfo(String fuid, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (!TextUtils.isEmpty(fuid)) {
			params.put("fuid", fuid);
		}
		params.put("infotype", "1");
		request(Method.GET, UserInfoBean.class, params, 2, listener, 2);
	}

	public static void getSmsCode(String phone, String countryCode, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (!TextUtils.isEmpty(phone)) {
			params.put("phone", phone);
			params.put("countrycode", countryCode);
		}
		request(Method.GET, VerificationResult.class, params, 2, listener, LOGIN_TYPE_NOT_NEED_LOGIN);
	}

	public static void resetPassword(String phone, String password, String code, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (!TextUtils.isEmpty(phone)) {
			params.put("phone", phone);
			params.put("password", password);
			params.put("code", code);
		}
		request(Method.GET, RegisterResult.class, params, 1, listener, LOGIN_TYPE_NOT_NEED_LOGIN);
	}

	public static void register(String realname, String phone, String password, String code, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (!TextUtils.isEmpty(phone)) {
			params.put("phone", phone);
			params.put("password", password);
			params.put("code", code);
		}
		if (!TextUtils.isEmpty(realname)) {
			params.put("realname", realname);
		}
		request(Method.GET, RegisterResult.class, params, 0, listener, LOGIN_TYPE_NOT_NEED_LOGIN);
	}

	public static void requestAddFriend(String fuids, String reason, String from, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (!TextUtils.isEmpty(fuids)) {
			params.put("fid", fuids);
		}
		if (!TextUtils.isEmpty(reason)) {
			params.put("reason", reason);
		}
		if (!TextUtils.isEmpty(from)) {
			params.put("from", from);
		}
		request(Method.GET, RecommendAddResult.class, params, RecommendAddResult.TYPE_ADD_FRIEND, listener,
				LOGIN_TYPE_NEED_LOGIN);
	}

	public static void requestAddTribe(String tids, String reason, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (!TextUtils.isEmpty(tids)) {
			params.put("tid", tids);
		}
		if (!TextUtils.isEmpty(reason)) {
			params.put("content", reason);
		}
		request(Method.GET, RecommendAddResult.class, params, RecommendAddResult.TYPE_ADD_TRIBE, listener,
				LOGIN_TYPE_NEED_LOGIN);
	}

	/**
	 * 获取部落和会议消息列表
	 * 
	 * @param tid
	 *            部落ID
	 * @param maxID
	 *            返回记录ID小于maxID
	 * @param sinceID
	 *            返回记录ID大于sinceID
	 * @return @
	 */

	public static void getMessageList(int type, String tid, String maxID, String sinceID, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (type == ChatTribeActivity.CHAT_TYPE_TRIBE) {
			params.put("tid", tid);
		} else {
			params.put("meetingid", tid);
		}
		params.put("maxID", maxID);
		params.put("sinceID", sinceID);
		Logger.d("sss", "sinceId=" + sinceID + "  maxId=" + maxID);
		params.put("pageSize", String.valueOf(LOAD_SIZE));
		request(Method.GET, ChatMessageBean.class, params, type, listener, 2);
	}

	/**
	 * 获取和某个用户之间的私信列表
	 * 
	 * @param fuid
	 *            必传 用户ID
	 * @param maxID
	 * @param sinceID
	 * @return @
	 */
	public static void getPrivateMessageList(String fuid, String maxID, String sinceID, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fuid", fuid);
		bundle.add("maxID", maxID);
		bundle.add("sinceID", sinceID);
		bundle.add("paegSize", String.valueOf(LOAD_SIZE));
		String url = SERVER + "user/messageList";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, ChatMessageBean.class, listener);
	}

	/** -------------------------- 部落接口 --------------------------- */

	/**
	 * 
	 * @param title
	 *            必传 会议标题
	 * @param pic
	 *            不必传 会议图片
	 * @param industryid
	 *            必传 会议行业ID
	 * @param type
	 *            必传 会议类型 1--公开 2--私密
	 * @param content
	 *            必传 会议提纲
	 * @param start
	 *            必传 会议开始时间
	 * @param end
	 *            必传 会议结束时间
	 * @return @
	 */
	public static void addTribe(String title, String pic, String type, String content, String tag, String password,
			IResponseListener listener) {
		Parameters bundle = new Parameters();
		List<MorePicture> fileList = new ArrayList<MorePicture>();
		if (!TextUtils.isEmpty(pic)) {
			fileList.add(new MorePicture("pic", pic));
			bundle.addPicture("fileList", fileList);
		}
		bundle.add("name", title);
		bundle.add("type", type);
		bundle.add("content", content);
		bundle.add("tag", tag);
		bundle.add("password", password);

		String url = SERVER + "tribe/addTribe";
		request(url, bundle, Utility.HTTPMETHOD_POST, 1, BaseNetBean.class, listener);
	}

	/**
	 * 修改部落
	 * 
	 * @param name
	 *            部落名称
	 * @param pic
	 *            部落LOGO
	 * @param industryid
	 *            部落行业
	 * @param type
	 *            部落类型 1-公开群 2-私密群
	 * @param content
	 *            部落描述
	 * @return
	 * @throws DamiException
	 */
	public static void editTribe(String tid, String name, String pic, String type, String content, String tag,
			String password, IResponseListener listener) {
		Parameters bundle = new Parameters();
		List<MorePicture> fileList = new ArrayList<MorePicture>();
		if (!TextUtils.isEmpty(pic)) {
			fileList.add(new MorePicture("pic", pic));
			bundle.addPicture("fileList", fileList);
		}
		bundle.add("tid", tid);
		bundle.add("name", name);
		bundle.add("type", type);
		bundle.add("content", content);
		bundle.add("tag", tag);
		bundle.add("password", password);

		String url = SERVER + "tribe/updatetribe";
		request(url, bundle, Utility.HTTPMETHOD_POST, 1, BaseNetBean.class, listener);
	}

	/** ++++++++++++++++++++++++++ 会议接口 +++++++++++++++++++++++++++ */

	/**
	 * 
	 * @param title
	 *            必传 会议标题
	 * @param pic
	 *            不必传 会议图片
	 * @param industryid
	 *            必传 会议行业ID
	 * @param type
	 *            必传 会议类型 1--公开 2--私密
	 * @param content
	 *            必传 会议提纲
	 * @param start
	 *            必传 会议开始时间
	 * @param end
	 *            必传 会议结束时间
	 * @return @
	 */
	public static void addMeeting(String title, String pic, String type, String content, long start, long end,
			String password, IResponseListener listener) {
		Parameters bundle = new Parameters();
		List<MorePicture> fileList = new ArrayList<MorePicture>();
		if (!TextUtils.isEmpty(pic)) {
			fileList.add(new MorePicture("pic", pic));
			bundle.addPicture("fileList", fileList);
		}
		bundle.add("title", title);
		bundle.add("type", type);
		bundle.add("content", content);
		bundle.add("start", String.valueOf(start));
		bundle.add("end", String.valueOf(end));
		// bundle.add("tag", tag);
		if (!TextUtils.isEmpty(password)) {
			bundle.add("password", password);
		}

		String url = SERVER + "meeting/addMeeting";
		request(url, bundle, Utility.HTTPMETHOD_POST, 1, BaseNetBean.class, listener);
	}

	/**
	 * 修改会议
	 * 
	 * @param title
	 *            必传 会议标题
	 * @param pic
	 *            不必传 会议图片
	 * @param industryid
	 *            必传 会议行业ID
	 * @param type
	 *            必传 会议类型 1--公开 2--私密
	 * @param content
	 *            必传 会议提纲
	 * @param start
	 *            必传 会议开始时间
	 * @param end
	 *            必传 会议结束时间
	 * @return
	 * @throws DamiException
	 */
	public static void editMeeting(String meetingid, String title, String pic, String type, String content, long start,
			long end, String password, IResponseListener listener) {
		Parameters bundle = new Parameters();
		List<MorePicture> fileList = new ArrayList<MorePicture>();
		if (!TextUtils.isEmpty(pic)) {
			fileList.add(new MorePicture("pic", pic));
			bundle.addPicture("fileList", fileList);
		}
		bundle.add("meetingid", meetingid);
		bundle.add("title", title);
		// bundle.add("industryid", industryid);
		bundle.add("type", type);
		bundle.add("content", content);
		bundle.add("start", String.valueOf(start));
		bundle.add("end", String.valueOf(end));
		if (!TextUtils.isEmpty(password)) {
			bundle.add("password", password);
		}

		String url = SERVER + "meeting/updatemeeting";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 发送消息接口
	 * 
	 * @param messageInfo
	 * @return @
	 */
	public static void sendMessage(MessageInfo messageInfo, IResponseListener listener) {
		Parameters bundle = new Parameters();
		if (messageInfo == null) {
			return;
		}
		bundle.add("type", String.valueOf(messageInfo.type));
		bundle.add("tag", messageInfo.tag);
		bundle.add("to", messageInfo.to);
		bundle.add("from", messageInfo.from);
		bundle.add("fileType", String.valueOf(messageInfo.fileType));
		bundle.add("isanonymity", String.valueOf(messageInfo.isanonymity));
		bundle.add("reisanonymity", String.valueOf(messageInfo.reisanonymity));

		if (!TextUtils.isEmpty(messageInfo.content)) {
			bundle.add("content", messageInfo.content);
		}

		if (messageInfo.fileType == MessageType.PICTURE) {
			if (!TextUtils.isEmpty(messageInfo.imgUrlS)) {
				if (!messageInfo.imgUrlS.startsWith("http://")) {
					List<MorePicture> fileList = new ArrayList<MorePicture>();
					fileList.add(new MorePicture("file_upload", messageInfo.imgUrlS));
					bundle.addPicture("fileList", fileList);
				} else {
					bundle.add("imgUrlS", messageInfo.imgUrlS);
					bundle.add("imgUrlL", messageInfo.imgUrlL);
					bundle.add("imgWidth", messageInfo.imgWidth + "");
					bundle.add("imgHeight", messageInfo.imgHeight + "");
				}
			}
		} else if (messageInfo.fileType == MessageType.VOICE) {
			if (!TextUtils.isEmpty(messageInfo.voiceUrl)) {
				if (!messageInfo.voiceUrl.startsWith("http://")) {
					List<MorePicture> fileList = new ArrayList<MorePicture>();
					fileList.add(new MorePicture("file_upload", messageInfo.voiceUrl));
					bundle.addPicture("fileList", fileList);
				} else {
					bundle.add("voiceUrl", messageInfo.voiceUrl);
				}

			}
		}

		if (!TextUtils.isEmpty(messageInfo.commenterid)) {
			bundle.add("commenterid", messageInfo.commenterid);
		}

		if (!TextUtils.isEmpty(messageInfo.commentername)) {
			bundle.add("commentername", messageInfo.commentername);
		}

		if (!TextUtils.isEmpty(messageInfo.displayname)) {
			bundle.add("displayName", messageInfo.displayname);
		}

		if (!TextUtils.isEmpty(messageInfo.headImgUrl)) {
			bundle.add("headImgUrl", messageInfo.headImgUrl);
		}

		if (!TextUtils.isEmpty(messageInfo.parentid)) {
			bundle.add("parentid", messageInfo.parentid);
		}

		if (!TextUtils.isEmpty(messageInfo.title)) {
			bundle.add("title", messageInfo.title);
		}

		if (!TextUtils.isEmpty(messageInfo.heroid)) {
			bundle.add("heroid", messageInfo.heroid);
		}

		if (!TextUtils.isEmpty(messageInfo.url)) {
			bundle.add("url", messageInfo.url);
		}

		bundle.add("samplerate", messageInfo.samplerate + "");

		bundle.add("voiceTime", String.valueOf(messageInfo.voiceTime));
		String url = SERVER + "user/sendMessage";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, SendMessageResult.class, listener);

	}

	/**
	 * 网络访问入口函数 classic request
	 * 
	 * @param url
	 *            请求的url
	 * @param params
	 *            请求的参数数组
	 * @param httpMethod
	 *            请求的方式
	 * @param loginType
	 *            是否登录 0--不登陆 1--需要登录 2--两种都可以
	 * @return @
	 */
	public static void request(final String url, final Parameters params, final String httpMethod, final int loginType,
			final Class clazz, final IResponseListener listener) {
		final UIHelperUtil uhu = UIHelperUtil.getUIHelperUtil(listener);
		if (!MyUtils.isNetConnected(UIHelperUtil.cxt)) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(UIHelperUtil.cxt, R.string.network_wrong, Toast.LENGTH_SHORT).show();
					uhu.sendFailureMessage(null);
					uhu.sendFinishMessage();
				}
			});
			return;
		}
		new Thread() {
			String rlt = null;

			@Override
			public void run() {
				try {
					uhu.sendStartMessage();
					rlt = Utility.openUrl(url, httpMethod, params, loginType);
					uhu.sendSuccessMessage(JSONObject.parseObject(rlt, clazz));
					uhu.sendFinishMessage();
				} catch (DamiException e) {
					e.printStackTrace();
					uhu.sendTimeOutMessage();
					uhu.sendFailureMessage(null);
					uhu.sendFinishMessage();
				} catch (JSONException e) {
					e.printStackTrace();
					uhu.sendErrorMessage();
					uhu.sendFailureMessage(null);
					uhu.sendFinishMessage();
				}
			}
		}.start();
	}

	/**
	 * 
	 * @param roomId
	 *            房间id或者聊天时对方id
	 * @param notice
	 *            0 不推送， 1 推送
	 * @param listener
	 */
	public static void setNotPush(String roomId, int notice, final IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roomid", roomId + "");
		params.put("notice", notice + "");
		request(Method.GET, SimpleStateBean.class, params, SimpleStateBean.TYPE_SET_NOT_PUSH, listener,
				LOGIN_TYPE_NEED_LOGIN);
	}

	public static void getNewRecFriendsList(int page, int obj, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", String.valueOf(page));
		params.put("pageSize", String.valueOf(LOAD_SIZE));
		request(Method.GET, NewUserList.class, params, obj, listener, LOGIN_TYPE_NEED_LOGIN);
	}

	/**
	 * 处理好友申请
	 * 
	 * @param fid
	 *            对方id
	 * @param isAgree
	 *            同意1，不同意0
	 * @param reason
	 *            不同意的理由
	 * @param listener
	 */
	public static void dealAddUser(String fid, int isAgree, String reason, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fid", fid);
		params.put("isagree", "" + isAgree);
		params.put("reason", reason);
		request(Method.GET, SimpleStateBean.class, params, SimpleStateBean.TYPE_DEAL_ADD_USER, listener,
				LOGIN_TYPE_NEED_LOGIN);
	}

	/**
	 * 获取临时身份
	 * 
	 * @param tid
	 *            //群ID
	 * @return @
	 */
	public static void getIndetity(String tid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", tid);
		String url = SERVER + "user/getIdentity";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, IdentitityResult.class, listener);
	}

	/**
	 * 发布动态
	 * 
	 * @param content
	 *            发布的内容
	 * @param files
	 *            图片，如果为空则content不为空
	 * @param isanonymous
	 *            1匿名，0实名
	 * @param tag
	 */
	public static void sendDynamic(String content, List<MorePicture> fileList, int isanonymous, String tag,
			IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("content", content);
		bundle.add("isanonymous", String.valueOf(isanonymous));
		bundle.add("tag", tag);
		if (fileList != null) {
			bundle.addPicture("fileList", fileList);
		}
		String url = SERVER + "user/addDynamic";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, SendDynamicResult.class, listener);
	}

	/**
	 * 
	 * @param type
	 *            扩散的动态类型： type=1.扩散的是其他人发布的图文动态 type==2扩散了聊天室（会议、圈子、私聊）中的消息
	 *            type=3扩散了会议 type=4扩散了圈子 type=5扩散了人脉 type=6扩散了一个链接(类似微信的扩散了链接)
	 * @param sid
	 *            扩散的实体对象的id； type＝1时sid为动态的id type＝2时sid为聊天室消息id
	 *            type＝3时sid为会议的tid type＝4时sid为圈子的tid type＝5时sid为用户的uid
	 *            type＝6时sid为空
	 * @param title
	 *            Type=6时分享的链接的标题
	 * @param image
	 *            Type ＝6时分享的链接的图片
	 * @param url
	 *            Type＝6时分享的链接的链接地址
	 * @param desc
	 *            Type＝6时分享的链接的副标题
	 * @param listener
	 */
	public static void spreadDynamic(int type, String sid, String title, String image, String url, String desc,
			String speak, IResponseListener listener) {
		Parameters bundle = new Parameters();
		if (type != -1) {
			bundle.add("type", String.valueOf(type));
		}
		if (!TextUtils.isEmpty(sid)) {
			bundle.add("sid", String.valueOf(sid));
		}
		if (!TextUtils.isEmpty(speak)) {
			bundle.add("speak", speak);
		}
		bundle.add("title", title);
		bundle.add("image", image);
		bundle.add("url", url);
		bundle.add("desc", desc);
		String url_ = SERVER + "user/spread";
		request(url_, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	// public static void getMyDynamic(int page, IResponseListener listener) {
	// Parameters bundle = new Parameters();
	// bundle.add("page", String.valueOf(page));
	// bundle.add("my", String.valueOf(1));
	// bundle.add("pageSize", String.valueOf(LOAD_SIZE));
	// String url_ = SERVER + "/user/getDynamic";
	// request(url_, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN,
	// DynamicBean.class, listener);
	// }

	public static void getDynamic(String userid, int page, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("page", String.valueOf(page));
		bundle.add("userid", userid);
		bundle.add("pageSize", "20");
		String url_ = SERVER + "user/getDynamic";
		request(url_, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, DynamicBean.class, listener);
	}

	public static void getRenMainList(int page, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("page", String.valueOf(page));
		bundle.add("pageSize", String.valueOf(LOAD_SIZE));
		String url_ = SERVER + "user/RenMaiList";
		request(url_, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, ConnectionBean.class, listener);
	}

	/**
	 * 申请加入会议
	 * 
	 * @param id
	 *            必传 会议ID
	 * @param content
	 *            必传 参会理由
	 * @return @
	 */
	public static void applyMeeting(String id, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("content", content);

		String url = SERVER + "meeting/apply";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 退出会议
	 * 
	 * @param id
	 * @return @
	 */
	public static void exitMeeting(String id, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		String url = SERVER + "meeting/quit";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 申请成为主持人
	 * 
	 * @param id
	 *            必传 会议ID
	 * @param content
	 *            必传 参会理由
	 * @return @
	 */
	public static void applyhost(String id, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("content", content);

		String url = SERVER + "meeting/applyhost";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 申请成为嘉宾
	 * 
	 * @param id
	 *            必传 会议ID
	 * @param content
	 *            必传 参会理由
	 * @return @
	 */
	public static void applyguest(String id, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("content", content);
		String url = SERVER + "meeting/applyguest";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 恢复为普通会议参与者
	 * 
	 * @param id
	 *            必传 会议ID
	 * @param content
	 *            必传 参会理由
	 * @return @
	 */
	public static void resumeToMeetingJoiner(String id, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("reason", content);

		String url = SERVER + "meeting/resumetomeetingjoiner";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 解散会议
	 * 
	 * @param id
	 *            必传 会议ID
	 * @param content
	 *            必传 参会理由
	 * @return @
	 */
	public static void cancelmeeting(String id, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("reason", content);

		String url = SERVER + "meeting/cancelmeeting";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 获取部落申请人列表
	 * 
	 * @param tid
	 *            部落ID
	 * @return
	 * @throws DamiException
	 */
	public static void getTribeApplyList(String tid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		// bundle.add("page", String.valueOf(page));
		// bundle.add("pageSize", String.valueOf(LOAD_SIZE));
		bundle.add("tid", tid);

		String url = SERVER + "tribe/applyList";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserList.class, listener);
	}

	/**
	 * 赞一条消息
	 * 
	 * @param tid
	 *            部落ID
	 * @param msgID
	 *            消息ID
	 * @return @
	 */
	public static void agreeMessage(String tid, String msgID, int isanonymity, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("roomid", tid);
		bundle.add("msgid", msgID);
		bundle.add("isanonymity", String.valueOf(isanonymity));
		String url = SERVER + "user/agreemessage";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 收藏一条会议消息
	 * 
	 * @param id
	 *            会议ID
	 * @param msgID
	 *            消息ID
	 * @return
	 * @throws DamiException
	 */
	public static void favoriteMeetingMessage(String tid, String msgID, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", tid);
		bundle.add("msgid", msgID);
		String url = SERVER + "meeting/favorite";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 取消会议消息收藏
	 * 
	 * @param tid
	 *            会议ID
	 * @param msgID
	 *            消息ID
	 * @return
	 * @throws DamiException
	 */
	public static void cancleFavoriteMeetingMessage(String tid, String msgID, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", tid);
		bundle.add("msgid", msgID);

		String url = SERVER + "meeting/unfavorite";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 收藏一条消息
	 * 
	 * @param tid
	 *            部落ID
	 * @param msgID
	 *            消息ID
	 * @return @
	 */
	public static void favoriteMessage(String tid, String msgID, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", tid);
		bundle.add("msgid", msgID);

		String url = SERVER + "tribe/favorite";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 取消消息收藏
	 * 
	 * @param tid
	 *            部落ID
	 * @param msgID
	 *            消息ID
	 * @return @
	 */
	public static void cancleFavoriteMessage(String tid, String msgID, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", tid);
		bundle.add("msgid", msgID);

		String url = SERVER + "tribe/unfavorite";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 举报一条消息
	 * 
	 * @param tid
	 *            部落ID
	 * @param msgID
	 *            消息ID
	 * @param content
	 *            举报内容
	 * @return @
	 */
	public static void reportMessage(String tid, String msgID, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", tid);
		bundle.add("msgid", msgID);
		bundle.add("content", content);

		String url = SERVER + "tribe/jubao";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 举报一条会议消息
	 * 
	 * @param id
	 *            会议ID
	 * @param msgID
	 *            消息ID
	 * @param content
	 *            举报内容
	 * @return
	 * @throws DamiException
	 */
	public static void reportMeetingMessage(String tid, String msgID, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", tid);
		bundle.add("msgid", msgID);
		bundle.add("content", content);
		String url = SERVER + "meeting/jubao";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 邀请加入会议
	 * 
	 * @param id
	 *            必传 会议ID
	 * @param fuid
	 *            必传 被邀请用户ID
	 * @return @
	 */
	public static void sendMeetingInvite(String id, String fuid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("fuid", fuid);
		String url = SERVER + "meeting/invite";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 邀请主持人 或 嘉宾
	 * 
	 * @param id
	 *            必传 会议ID
	 * @param fuid
	 *            必传 被邀请用户ID
	 * @param role
	 *            2 主持人 3嘉宾
	 * @return @
	 */
	public static void invitemeeting(String id, String fuid, int role, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("fuid", fuid);
		bundle.add("role", role + "");
		String url = SERVER + "meeting/invitemeeting";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 同意会议邀请
	 * 
	 * @param id
	 * @return @
	 */
	public static void agreeMeetingInvite(String id, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		String url = SERVER + "meeting/agreeInvite";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 拒绝会议邀请
	 * 
	 * @param id
	 * @return @
	 */
	public static void refuseMeetingInvite(String id, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		String url = SERVER + "meeting/disagreeInvite";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 同意加入会议
	 * 
	 * @param id
	 *            会议ID
	 * @return @
	 */
	public static void agreeMeetingJoin(String id, String fuid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("fuid", fuid);
		String url = SERVER + "meeting/agreeApply";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 同意成为主持人
	 * 
	 * @param id
	 *            会议ID
	 * @return @
	 */
	public static void agreeHostApply(String id, String fuid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("fuid", fuid);
		bundle.add("isagree", "1");
		String url = SERVER + "meeting/chargehostapply";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 同意成为嘉宾
	 * 
	 * @param id
	 *            会议ID
	 * @return @
	 */
	public static void agreeGuestApply(String id, String fuid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("fuid", fuid);
		bundle.add("isagree", "1");
		String url = SERVER + "meeting/chargeguestapply";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 拒绝加入会议
	 * 
	 * @param id
	 *            会议ID
	 * @return @
	 */
	public static void refuseMeetingJoin(String id, String fuid, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("fuid", fuid);
		if (!TextUtils.isEmpty(content)) {
			bundle.add("content", content);
		}
		String url = SERVER + "meeting/disagreeApply";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 拒绝成为主持人
	 * 
	 * @param id
	 *            会议ID
	 * @return @
	 */
	public static void refuseHost(String id, String fuid, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("fuid", fuid);
		bundle.add("isagree", "0");
		if (!TextUtils.isEmpty(content)) {
			bundle.add("content", content);
		}
		String url = SERVER + "meeting/chargehostapply";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 拒绝成为嘉宾
	 * 
	 * @param id
	 *            会议ID
	 * @return @
	 */
	public static void refuseGuest(String id, String fuid, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("fuid", fuid);
		bundle.add("isagree", "0");
		if (!TextUtils.isEmpty(content)) {
			bundle.add("content", content);
		}
		String url = SERVER + "meeting/chargeguestapply";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 求交往
	 * 
	 * @param fuid
	 *            被求交往用户ID 必传
	 * @param msgid
	 *            消息ID 必传
	 * @param content
	 *            求交往理由
	 * @return
	 * @throws DamiException
	 */
	public static void seekingContacts(String fuid, String msgid, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fuid", fuid);
		bundle.add("msgid", msgid);
		if (!TextUtils.isEmpty(content)) {
			bundle.add("content", content);
		}
		String url = SERVER + "user/seekingContacts";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 拒绝求交往
	 * 
	 * @param fuid
	 *            被拒绝求交往用户ID 必传
	 * @param msgid
	 *            消息ID 必传
	 * @param content
	 *            拒绝求交往理由
	 * @return
	 * @throws DamiException
	 */
	public static void refuseSeekingContacts(String fuid, String msgid, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fuid", fuid);
		bundle.add("msgid", msgid);
		if (!TextUtils.isEmpty(content)) {
			bundle.add("content", content);
		}
		String url = SERVER + "user/disagree";

		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 同意求交往
	 * 
	 * @param fuid
	 *            被求交往用户ID 必传
	 * @param msgid
	 *            消息ID 必传
	 * @return
	 * @throws DamiException
	 */
	public static void agreeSeekingContacts(String fuid, String msgid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fuid", fuid);
		bundle.add("msgid", msgid);
		String url = SERVER + "user/agree";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 获取会议申请人列表
	 * 
	 * @param id
	 *            会议ID
	 * @return
	 * @throws DamiException
	 */
	public static void getMeetingApplyList(String id, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		String url = SERVER + "meeting/applyList";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserList.class, listener);
	}

	/**
	 * 获取会议主持人申请人列表
	 * 
	 * @param id
	 *            会议ID
	 * @return
	 * @throws DamiException
	 */
	public static void getHostgApplyList(String id, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		String url = SERVER + "meeting/gethostapplylist";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserList.class, listener);
	}

	/**
	 * 获取会议嘉宾申请人列表
	 * 
	 * @param id
	 *            会议ID
	 * @return
	 * @throws DamiException
	 */
	public static void getGuestApplyList(String id, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);

		String url = SERVER + "meeting/getguestapplylist";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserList.class, listener);
	}

	/**
	 * 同意加入部落
	 * 
	 * @param id
	 *            部落ID
	 * @return
	 * @throws DamiException
	 */
	public static void agreeTribeJoin(String id, String fuid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", id);
		bundle.add("fuid", fuid);

		String url = SERVER + "tribe/agreeApply";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 拒绝加入部落
	 * 
	 * @param id
	 *            部落ID
	 * @return
	 * @throws DamiException
	 */
	public static void refuseJoin(String id, String fuid, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", id);
		bundle.add("fuid", fuid);
		if (!TextUtils.isEmpty(content)) {
			bundle.add("content", content);
		}

		String url = SERVER + "tribe/disagreeApply";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 邀请加入部落
	 * 
	 * @param id
	 * @param fuid
	 * @return
	 * @throws DamiException
	 */
	public static void sendInvite(String id, String fuid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", id);
		bundle.add("fuid", fuid);

		String url = SERVER + "tribe/invite";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 同意邀请
	 * 
	 * @param id
	 * @return
	 * @throws DamiException
	 */
	public static void agreeInvite(String id, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", id);

		String url = SERVER + "tribe/agreeInvite";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 拒绝邀请
	 * 
	 * @param id
	 * @return
	 * @throws DamiException
	 */
	public static void refuseInvite(String id, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", id);

		String url = SERVER + "tribe/disagreeInvite";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 添加动态评论 少了一个我的id
	 * 
	 * 
	 * @param isanonymous
	 *            1：匿名 0：实名评论
	 * @param type
	 *            0：用户聊天
	 * 
	 *            1：动态，实名评论 2：会议室 3：圈子 4：聊天室 5：人脉，
	 * @return
	 * @throws DamiException
	 */
	public static void addComment(String toid, int type, String dataid, String content, int isanonymous,
			String displayname, String todisplayname, IResponseListener listener) {
		addProfileComment(toid, type, dataid, content, isanonymous, displayname, todisplayname, "", listener);
	}

	// comment profile set tabletype=2
	public static void addProfileComment(String toid, int type, String dataid, String content, int isanonymous,
			String displayname, String todisplayname, String tabletype, IResponseListener listener) {
		Parameters bundle = new Parameters();
		Logger.d(DamiInfo.class, "   toid=" + toid + "   type=" + type + "   dataid=" + dataid + "   content="
				+ content + "   isanonymous=" + isanonymous + "   displayname=" + displayname + "   todisplayname="
				+ todisplayname + "   tabletype=" + tabletype);
		if (!TextUtils.isEmpty(toid)) {
			bundle.add("toid", toid);
		}
		if (!TextUtils.isEmpty(todisplayname)) {
			bundle.add("todisplayname", todisplayname);
		}
		bundle.add("type", String.valueOf(type));
		bundle.add("dataid", dataid);
		bundle.add("content", forceNotNull(content));
		bundle.add("isanonymous", String.valueOf(isanonymous));
		bundle.add("displayname", forceNotNull(displayname));
		bundle.add("tabletype", tabletype);

		String url = SERVER + "user/addComment";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 添加赞
	 * 
	 * @param id
	 * @return
	 * @throws DamiException
	 */
	public static void zanOperation(String uid, int type, String dataid, int isanonymous, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("uid", uid);
		bundle.add("type", String.valueOf(type));
		bundle.add("dataid", dataid);
		bundle.add("isanonymous", String.valueOf(isanonymous));

		String url = SERVER + "user/zanOperation";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	private static String forceNotNull(String src) {
		if (TextUtils.isEmpty(src)) {
			return "";
		}
		return src;
	}

	/**
	 * 
	 * @param meetingid
	 * @param role
	 *            2代表主持人；3代表嘉宾
	 * @param isagree
	 *            1-同意邀请；0代表不同意
	 * @return
	 * @throws DamiException
	 */
	public static void chargeinvite(String meetingid, String role, String isagree, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", meetingid);
		bundle.add("role", role);
		bundle.add("isagree", isagree);
		String url = SERVER + "meeting/chargeinvite";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 邀请加入部落
	 * 
	 * @param id
	 * @param fuid
	 * @return
	 * @throws DamiException
	 */
	public static void sendInvite(String id, String fuid, SimpleResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", id);
		bundle.add("fuid", fuid);

		String url = SERVER + "tribe/invite";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 邀请加入会议
	 * 
	 * @param id
	 *            必传 会议ID
	 * @param fuid
	 *            必传 被邀请用户ID
	 * @return
	 * @throws DamiException
	 */
	public static void sendMeetingInvite(String id, String fuid, SimpleResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("fuid", fuid);
		String url = SERVER + "meeting/invite";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 邀请主持人 或 嘉宾
	 * 
	 * @param id
	 *            必传 会议ID
	 * @param fuid
	 *            必传 被邀请用户ID
	 * @param role
	 *            2 主持人 3嘉宾
	 * @return
	 * @throws DamiException
	 */
	public static void invitemeeting(String id, String fuid, int role, SimpleResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("fuid", fuid);
		bundle.add("role", role + "");
		String url = SERVER + "meeting/invitemeeting";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 获取邀请时关注列表
	 * 
	 * @param fuid
	 * @return
	 * @throws DamiException
	 */
	public static void getFollowerList2(int page, String tid, String role, String keyword,
			SimpleResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fuid", DamiCommon.getUid(DamiApp.getInstance()));
		if (!TextUtils.isEmpty(keyword)) {
			bundle.add("keyword", keyword);
		}
		bundle.add("tid", tid);
		bundle.add("role", role);
		bundle.add("page", page + "");
		String url = SERVER + "user/followlist";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserList.class, listener);
	}

	/**
	 * 获取邀请时粉丝列表
	 * 
	 * @param fuid
	 * @return
	 * @throws DamiException
	 */
	public static void getFansList2(int page, String tid, String role, String keyword, SimpleResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fuid", DamiCommon.getUid(DamiApp.getInstance()));
		if (!TextUtils.isEmpty(keyword)) {
			bundle.add("keyword", keyword);
		}
		bundle.add("tid", tid);
		bundle.add("role", role);
		bundle.add("page", page + "");

		String url = SERVER + "user/fanslist";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserList.class, listener);
	}

	/**
	 * 重新认证接口
	 * 
	 * @param phone
	 *            手机号
	 * @param realname
	 *            真实姓名
	 * @param company
	 *            公司
	 * @param post
	 *            职位
	 * @return
	 * @throws DamiException
	 */
	// realname true string 真实姓名
	// company true string 公司
	// depa true string 部门
	// post true string 职位

	public static void reAuth(String depa, String realname, String company, String post, String email, String weibo,
			String weixin, IResponseListener listener) {
		Parameters bundle = new Parameters();
		if (!TextUtils.isEmpty(email)) {
			bundle.add("email", email);
		}
		if (!TextUtils.isEmpty(weibo)) {
			bundle.add("weibo", weibo);
		}
		if (!TextUtils.isEmpty(weixin)) {
			bundle.add("weixin", weixin);
		}
		bundle.add("depa", depa);
		bundle.add("realname", realname);
		bundle.add("company", company);
		bundle.add("post", post);
		String url = SERVER + "user/realnameAuth";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 编辑个人资料
	 * 
	 * @param pic
	 *            头像路径
	 * @param sign
	 *            个性签名
	 * @return
	 * @throws DamiException
	 */
	public static void editProfile(String pic, String sign, String email, String weibo, String weixin, String phone,
			IResponseListener listener) {
		Parameters bundle = new Parameters();
		List<MorePicture> fileList = new ArrayList<MorePicture>();
		if (!TextUtils.isEmpty(pic)) {
			fileList.add(new MorePicture("pic", pic));
			bundle.addPicture("fileList", fileList);
		}
		if (!TextUtils.isEmpty(sign)) {
			bundle.add("sign", sign);
		}
		if (!TextUtils.isEmpty(email)) {
			bundle.add("email", email);
		}
		if (!TextUtils.isEmpty(weibo)) {
			bundle.add("weibo", weibo);
		}
		if (!TextUtils.isEmpty(weixin)) {
			bundle.add("weixin", weixin);
		}
		if (!TextUtils.isEmpty(phone)) {
			bundle.add("phone", phone);
		}

		String url = SERVER + "user/edit";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserInfoBean.class, listener);
	}

	public static void editHeader(String pic, IResponseListener listener) {
		editProfile(pic, "", "", "", "", "", listener);
	}

	public static void getFansList(String uid, int page, String keyword, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fuid", uid);
		if (!TextUtils.isEmpty(keyword)) {
			bundle.add("keyword", keyword);
		}
		bundle.add("page", page + "");
		String url = SERVER + "user/fanslist";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserList.class, listener);
	}

	/**
	 * 获取关注列表
	 * 
	 * @param fuid
	 * @return
	 * @throws DamiException
	 */
	public static void getFollowerList(String fuid, int page, String keyword, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fuid", fuid);
		if (!TextUtils.isEmpty(keyword)) {
			bundle.add("keyword", keyword);
		}
		bundle.add("page", page + "");
		String url = SERVER + "user/followlist";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserList.class, listener);
	}

	/**
	 * 获取收藏列表
	 * 
	 * @param sinceID
	 * @param maxID
	 * @return
	 * @throws DamiException
	 */
	public static void getFaoviteList(String sinceID, String maxID, IResponseListener listener) {
		Parameters bundle = new Parameters();
		if (!TextUtils.isEmpty(sinceID)) {
			bundle.add("sinceID", sinceID);
		}
		if (!TextUtils.isEmpty(maxID)) {
			bundle.add("maxID", maxID);
		}
		bundle.add("paegSize", String.valueOf(LOAD_SIZE));
		String url = SERVER + "user/favoriteList";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, FavoriteList.class, listener);
	}

	/**
	 * 添加关注/取消关注
	 * 
	 * @param fuid
	 *            关注用户ID
	 * @return
	 * @throws DamiException
	 */
	public static void follow(String fuid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fuid", fuid);
		String url = SERVER + "user/follow";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 部落用户接收消息设置
	 * 
	 * @param tid
	 *            部落ID
	 * @param type
	 *            接收类型 1--接收消息并提醒 2--不提醒仅显示数目
	 * @return
	 * @throws DamiException
	 */
	public static void setMsgType(String tid, String type, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", tid);
		bundle.add("getmsg", type);
		String url = SERVER + "user/tribeSetting";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 退出部落
	 * 
	 * @param id
	 * @return
	 * @throws DamiException
	 */
	public static void exitTribe(String id, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", id);

		String url = SERVER + "tribe/quit";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 解散部落
	 * 
	 * @param id
	 * @return
	 * @throws DamiException
	 */
	public static void cancelTribe(String id, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", id);

		String url = SERVER + "tribe/disbanded";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 申请加入部落
	 * 
	 * @param tid
	 *            部落ID
	 * @return
	 * @throws DamiException
	 */
	public static void applyTribe(String tid, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", tid);
		if (!TextUtils.isEmpty(content))
			bundle.add("content", content);

		String url = SERVER + "tribe/apply";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 移除部落成员
	 * 
	 * @param id
	 *            部落ID 必传
	 * @param fuid
	 *            被移出用户ID 必传
	 * @return
	 * @throws DamiException
	 */
	public static void kickTribePerson(String id, String fuid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", id);
		bundle.add("fuid", fuid);

		String url = SERVER + "tribe/remove";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 获取部落成员列表
	 * 
	 * @param tid
	 *            部落ID
	 * @return
	 * @throws DamiException
	 */
	public static void getTribeUserList(String tid, int page, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("page", String.valueOf(page));
		bundle.add("pageSize", String.valueOf(LOAD_SIZE));
		bundle.add("tid", tid);

		String url = SERVER + "tribe/tribeUserList";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserList.class, listener);
	}

	/**
	 * 获取会议用户列表
	 * 
	 * @param id
	 *            必传 会议ID
	 * @return
	 * @throws DamiException
	 */
	public static void getMeetingUserList(String id, int page, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("page", String.valueOf(page));
		bundle.add("pageSize", String.valueOf(LOAD_SIZE));
		String url = SERVER + "meeting/meetingUserList";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserList.class, listener);
	}

	/**
	 * 获取会议主持人列表
	 * 
	 * @param id
	 *            必传 会议ID
	 * @return
	 * @throws DamiException
	 */
	public static void gethostsList(String id, int page, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("page", String.valueOf(page));
		bundle.add("pageSize", String.valueOf(LOAD_SIZE));
		String url = SERVER + "meeting/gethosts";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserList.class, listener);
	}

	/**
	 * 获取会议嘉宾列表
	 * 
	 * @param id
	 *            必传 会议ID
	 * @return
	 * @throws DamiException
	 */
	public static void getguestsList(String id, int page, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("page", String.valueOf(page));
		bundle.add("pageSize", String.valueOf(LOAD_SIZE));

		String url = SERVER + "meeting/getguests";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserList.class, listener);
	}

	/**
	 * 获取部落消息的评论列表
	 * 
	 * @param tid
	 * @param maxID
	 * @param sinceID
	 * @return
	 * @throws DamiException
	 */
	public static void getCommentList(String msgid, String maxID, String sinceID, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("msgid", msgid);
		if (!TextUtils.isEmpty(maxID)) {
			bundle.add("maxID", maxID);
		}

		if (!TextUtils.isEmpty(sinceID)) {
			bundle.add("sinceID", sinceID);
		}
		bundle.add("paegSize", String.valueOf(LOAD_SIZE));

		String url = SERVER + "tribe/messageCommentList";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, ChatMessageBean.class, listener);
	}

	/**
	 * 获取部落赞列表
	 * 
	 * @param tid
	 * @param maxID
	 * @param sinceID
	 * @return
	 * @throws DamiException
	 */
	public static void getMessageZanList(String msgid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("msgid", msgid);

		String url = SERVER + "user/messageAgreeList";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, MsgZanListResult.class, listener);
	}

	/**
	 * 添加用户标签
	 * 
	 * @return
	 * @throws DamiException
	 */
	public static void addUserTag(String fid, String tag, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fid", fid);
		bundle.add("tag", tag);
		String url = SERVER + "user/addUserTag";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 更新用户标签
	 * 
	 * @return
	 * @throws DamiException
	 */
	public static void updateUserTag(String fid, String tag, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fid", fid);
		bundle.add("tag", tag);
		String url = SERVER + "user/updateUserTag";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, TagResultBean.class, listener);
	}

	/**
	 * 删除用户标签
	 * 
	 * @return
	 * @throws DamiException
	 */
	public static void delUserTag(String fid, String tag, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fid", fid);
		bundle.add("tag", tag);
		String url = SERVER + "user/delUserTag";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, ChatMessageBean.class, listener);
	}

	/**
	 * 
	 * @return
	 * @throws DamiException
	 */
	public static void zanUserTag(String fid, String tag, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fid", fid);
		bundle.add("tagid", tag);
		String url = SERVER + "user/zanUserTag";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 获取隐私设置 传uid
	 * 
	 * @return
	 * @throws DamiException
	 */
	public static void getPrivacyConfig(IResponseListener listener) {
		Parameters bundle = new Parameters();
		String url = SERVER + "user/GetPrivacyConfig";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, PrivacySettingResult.class, listener);
	}

	/**
	 * 隐私设置 传uid
	 * 
	 * @return
	 * @throws DamiException
	 */
	public static void setPrivacyConfig(int phone, int email, int weixin, int weibo, int renmai,
			IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("lookp", String.valueOf(phone));
		bundle.add("lookm", String.valueOf(email));
		bundle.add("lookw", String.valueOf(weixin));
		bundle.add("lookwb", String.valueOf(weibo));
		bundle.add("lookr", String.valueOf(renmai));
		String url = SERVER + "user/setPrivacyConfig";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 获取消息提醒设置 传uid
	 * 
	 * @return
	 * @throws DamiException
	 */
	public static void getMessageConfig(IResponseListener listener) {
		Parameters bundle = new Parameters();
		String url = SERVER + "user/GetMessageConfig";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, MsgConfigResult.class, listener);
	}

	/**
	 * 消息提醒设置 传uid
	 * 
	 * @return
	 * @throws DamiException
	 */
	public static void setMessageConfig(int dnd, int bh, int bm, int eh, int em, int ring, int shake, int dami,
			IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("dnd", String.valueOf(dnd));
		bundle.add("bh", String.valueOf(bh));
		bundle.add("bm", String.valueOf(bm));
		bundle.add("eh", String.valueOf(eh));
		bundle.add("em", String.valueOf(em));
		bundle.add("ring", String.valueOf(ring));
		bundle.add("shake", String.valueOf(shake));
		bundle.add("dami", String.valueOf(dami));
		String url = SERVER + "user/SetMessageConfig";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 检测更新
	 * 
	 * @param version
	 *            版本号
	 * @return
	 * @throws DamiException
	 */
	public static void checkUpgrade(IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("os", "android");
		bundle.add("version", FeatureFunction.getAppVersionName(DamiApp.getInstance()));
		String url = SERVER + "index/update";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, CheckUpdateResult.class, listener);
	}

	/**
	 * 意见反馈
	 * 
	 * @param content
	 *            反馈内容
	 * @return
	 * @throws DamiException
	 */
	public static void feedback(String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("content", content);
		String url = SERVER + "user/feedback";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 获取举报列表
	 * 
	 * @param tid
	 * @return
	 * @throws DamiException
	 */
	public static void getTribeReportMsgList(String tid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", tid);
		bundle.add("paegSize", "100");
		String url = SERVER + "tribe/jubaoList";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, ReportMsgResult.class, listener);
	}

	/**
	 * 获取会议举报列表
	 * 
	 * @param tid
	 * @return
	 * @throws DamiException
	 */
	public static void getMeetingReportMsgList(String id, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", id);
		bundle.add("paegSize", "100");

		String url = SERVER + "meeting/jubaoList";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, ReportMsgResult.class, listener);
	}

	/**
	 * 同意部落消息举报
	 * 
	 * @param msgid
	 *            消息ID
	 * @param fuid
	 *            被举报用户ID（传入表示从部落中踢出该用户）
	 * @return
	 * @throws DamiException
	 */
	public static void agreeReport(String msgid, String fuid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("msgid", msgid);
		if (!TextUtils.isEmpty(fuid)) {
			bundle.add("fuid", fuid);
		}

		String url = SERVER + "tribe/agreeJubao";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 不同意部落消息举报
	 * 
	 * @param msgid
	 *            消息ID
	 * @return
	 * @throws DamiException
	 */
	public static void refuseReport(String msgid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("msgid", msgid);

		String url = SERVER + "tribe/disagreeJubao";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 同意会议消息举报
	 * 
	 * @param msgid
	 *            消息ID
	 * @param fuid
	 *            被举报用户ID（传入表示从部落中踢出该用户）
	 * @return
	 * @throws DamiException
	 */
	public static void agreeMeetingReport(String msgid, String fuid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("msgid", msgid);
		if (!TextUtils.isEmpty(fuid)) {
			bundle.add("fuid", fuid);
		}

		String url = SERVER + "meeting/agreeJubao";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 不同意会议消息举报
	 * 
	 * @param msgid
	 *            消息ID
	 * @return
	 * @throws DamiException
	 */
	public static void refuseMeetingReport(String msgid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("msgid", msgid);

		String url = SERVER + "meeting/disagreeJubao";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 获取新的动态 有个参数uid
	 * 
	 * @return
	 * @throws DamiException
	 */
	public static void getNewDynamic(IResponseListener listener) {
		Parameters bundle = new Parameters();
		String url = SERVER + "user/getNewDynamic";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, NewDynamicBean.class, listener);
	}

	// 举报用户
	public static void reportUser(String fuid, String content, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fuid", fuid);
		bundle.add("content", content);
		String url = SERVER + "user/ReportUser";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	// 举报动态
	public static void reportDynamic(String dynamicid, String reason, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("dynamicid", dynamicid);
		bundle.add("reason", reason);
		String url = SERVER + "user/reportDynamic";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	// 获取动态详情
	public static void getDynamicDetails(String dynaid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("dynaid", dynaid);
		String url = SERVER + "user/DynamicDetails";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, DynamicBean.DySingleBean.class, listener);
	}

	// 外发注册连接 need uid
	public static void getUserInvitation(IResponseListener listener) {
		Parameters bundle = new Parameters();
		String url = SERVER + "user/UserInvitation";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, InviteUrlResult.class, listener);
	}

	// 成功邀请用户数(/api/user/UserInvitationNum)
	public static void getUserInvitationNum(IResponseListener listener) {
		Parameters bundle = new Parameters();
		String url = SERVER + "user/UserInvitationNum";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, InviteNumResult.class, listener);
	}

	public static void getVerifyResult(IResponseListener listener) {
		Parameters bundle = new Parameters();
		String url = SERVER + "user/UserAuthItem";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, GetVerifyResult.class, listener);
	}

	// 申请加v
	public static void setUserAuthV(IResponseListener listener) {
		Parameters bundle = new Parameters();
		String url = SERVER + "user/UserAuthV";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	public static void joinMeetingByPassword(String meetingid, String password, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("meetingid", meetingid);
		bundle.add("password", password);
		String url = SERVER + "meeting/joinMeetingByPasswd";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	public static void joinTribeByPasswd(String tid, String password, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("tid", tid);
		bundle.add("password", password);
		String url = SERVER + "tribe/joinTribeByPasswd";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	// 删除个人资料界面的评论
	public static void delComment(String id, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("id", id);
		String url = SERVER + "user/delComment";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	// 删除动态
	public static void delDynamic(String id, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("dynaid", id);
		String url = SERVER + "user/delDynamic";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BaseNetBean.class, listener);
	}

	/**
	 * 获取我的部落
	 * 
	 * @param listener
	 */
	public static void getTribeList(String fid, int page, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fuid", fid);
		bundle.add("page", String.valueOf(page));
		String url = SERVER + "user/mytribe";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, TribeList.class, listener);
	}

	/**
	 * 获取会议列表
	 * 
	 * @param type
	 *            必传 1--进行中 2--往期 3--我的
	 * @param page
	 */
	public static void getMeetingList(String fid, int type, int page, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fuid", fid);
		bundle.add("page", String.valueOf(page));
		bundle.add("type", type + "");
		bundle.add("pageSize", String.valueOf(LOAD_SIZE));
		String url = SERVER + "index/meetinglist";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, TribeList.class, listener);
	}

	// 关注多个用户
	public static void followInBat(String fuid, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("fuid", fuid);
		String url = SERVER + "user/batFollow";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, BatFollowResult.class, listener);
	}

	// 绑定手机号码
	public static void bindPhone(String phone, String code, String realname, IResponseListener listener) {
		Parameters bundle = new Parameters();
		bundle.add("phone", phone);
		bundle.add("code", code);
		bundle.add("realname", realname);
		String url = SERVER + "user/bindphone";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, LoginResult.class, listener);
	}

	/**
	 * 获取系统默认标签 type False 标签的分类（可填 user \quanzi\search\dynamic）
	 * 
	 * @param listener
	 */
	public static void getTags(String type, IResponseListener listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("num", "9");
		if (!TextUtils.isEmpty(type)) {
			params.put("type", type);
		}
		request(Method.GET, TagResult.class, params, 0, listener, LOGIN_TYPE_NOT_NEED_LOGIN);
	}

	public static void getRecommendFriendList(IResponseListener listener) {
		Parameters bundle = new Parameters();
		String url = SERVER + "user/recommendfriend";
		request(url, bundle, Utility.HTTPMETHOD_POST, LOGIN_TYPE_NEED_LOGIN, UserList.class, listener);
	}

	public static void getWxAccessToken(String appId, String secret, String code, IResponseListener listener) {
		Parameters params = new Parameters();
		params.add("appid", appId);
		params.add("secret", secret);
		params.add("code", code);
		params.add("grant_type", "authorization_code");
		customRequest("https://api.weixin.qq.com/sns/oauth2/access_token", params, Utility.HTTPMETHOD_GET,
				TokenBean.class, listener);
	}

	public static void getWxUserInfo(String access_token, String openid, IResponseListener listener) {
		Parameters params = new Parameters();
		params.add("access_token", access_token);
		params.add("openid", openid);
		customRequest("https://api.weixin.qq.com/sns/userinfo", params, Utility.HTTPMETHOD_GET, WxUserInfo.class,
				listener);
	}

	public static void customRequest(final String url, final Parameters params, final String httpMethod,
			final Class clazz, final IResponseListener listener) {
		final UIHelperUtil uhu = UIHelperUtil.getUIHelperUtil(listener);
		if (!MyUtils.isNetConnected(UIHelperUtil.cxt)) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(UIHelperUtil.cxt, R.string.network_wrong, Toast.LENGTH_SHORT).show();
					uhu.sendFailureMessage(null);
					uhu.sendFinishMessage();
				}
			});
			return;
		}
		new Thread() {
			String rlt = null;

			@Override
			public void run() {
				try {
					uhu.sendStartMessage();
					rlt = Utility.openUrl(url, httpMethod, params, null);
					uhu.sendSuccessMessage(JSONObject.parseObject(rlt, clazz));
					uhu.sendFinishMessage();
				} catch (DamiException e) {
					e.printStackTrace();
					uhu.sendTimeOutMessage();
					uhu.sendFailureMessage(null);
					uhu.sendFinishMessage();
				} catch (JSONException e) {
					e.printStackTrace();
					uhu.sendErrorMessage();
					uhu.sendFailureMessage(null);
					uhu.sendFinishMessage();
				}
			}
		}.start();
	}
}

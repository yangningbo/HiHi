package com.gaopai.guiren.volley;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.LoginActivity;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.bean.AppState;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.StringUtils;

public abstract class SimpleResponseListener implements IResponseListener {

	private Context mContext;
	private boolean mIsShowProgressbar;
	private String mProgressString;

	public SimpleResponseListener(Context context) {
		mContext = context;
		mIsShowProgressbar = false;
	}

	public SimpleResponseListener(Context context, String progressString) {
		mContext = context;
		mIsShowProgressbar = true;
		mProgressString = progressString;
	}

	public SimpleResponseListener(Context context, int progressString) {
		mContext = context;
		mIsShowProgressbar = true;
		mProgressString = mContext.getResources().getString(progressString);
	}

	@Override
	public void onReqStart() {
		// TODO Auto-generated method stub
		if (mIsShowProgressbar) {
			((BaseActivity) mContext).showProgressDialog(mProgressString);
			Logger.d(this, "showDialog===================.===");
		}
	}

	@Override
	public abstract void onSuccess(Object o);

	@Override
	public void onFailure(Object o) {
		// TODO Auto-generated method stub
		if (mIsShowProgressbar) {
			((BaseActivity) mContext).removeProgressDialog();
		}
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		if (mIsShowProgressbar) {
			((BaseActivity) mContext).removeProgressDialog();
		}
	}

	@Override
	public void onTimeOut() {
		if (mIsShowProgressbar) {
			((BaseActivity) mContext).removeProgressDialog();
		}
		Toast.makeText(mContext, mContext.getResources().getString(R.string.request_timeout), Toast.LENGTH_SHORT)
				.show();
	}

	public void showError(BaseNetBean data) {
		String str;
		if (data.state != null && !StringUtils.isEmpty(data.state.msg)) {
			str = data.state.msg;
			showToast(str);
		} else {
			showToast(R.string.load_error);
		}
	}

	public void showToast(int id) {
		Toast.makeText(mContext, mContext.getString(id), Toast.LENGTH_SHORT).show();
	}

	public void showToast(String str) {
		Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
	}

	public void showoCodeExpiredToast(Activity mActivity) {
		DamiCommon.saveLoginResult(mContext, null);
		DamiCommon.setUid("");
		DamiCommon.setToken("");
		Intent intent = new Intent(mContext, LoginActivity.class);
		mActivity.startActivityForResult(intent, MainActivity.LOGIN_REQUEST);
	}

	public void otherCondition(AppState state, Activity mActivity) {
		if (state != null && state.code == DamiCommon.EXPIRED_CODE) {
			this.showoCodeExpiredToast(mActivity);
		} else {
			String str;
			if (state != null && !StringUtils.isEmpty(state.msg)) {
				str = state.msg;
			} else {
				str = mActivity.getString(R.string.load_error);
			}
			showToast(str);
		}
	}
	

}
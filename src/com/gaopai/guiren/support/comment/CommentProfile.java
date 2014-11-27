package com.gaopai.guiren.support.comment;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class CommentProfile extends IComment {

	public final static String KEY_USER = "user";

	private User mUser = null;
	private User tUser = null;

	@Override
	public void onCreat(final BaseActivity activity) {
		super.onCreat(activity);
		mUser = DamiCommon.getLoginResult(activity);
		tUser = (User) activity.getIntent().getSerializableExtra(KEY_USER);
		setTitle(activity.getString(R.string.comment));
		setBtnText(activity.getString(R.string.send_comment));
		setEditHint(activity.getString(R.string.please_input_comment));
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String string = (String) v.getTag();
				if (TextUtils.isEmpty(editText.getText())) {
					activity.showToast(R.string.input_can_not_be_empty);
					return;
				}
				DamiInfo.addProfileComment("0", 5, tUser.uid, editText.getText().toString(), 0, mUser.displayName,
						tUser.displayName, "2", new SimpleResponseListener(activity, R.string.request_internet_now) {

							@Override
							public void onSuccess(Object o) {
								// TODO Auto-generated method stub
								BaseNetBean data = (BaseNetBean) o;
								if (data.state != null && data.state.code == 0) {
									showToast(R.string.comment_success);
									activity.finish();
								} else {
									otherCondition(data.state, activity);
								}
							}
						});
			}
		});
	}
}

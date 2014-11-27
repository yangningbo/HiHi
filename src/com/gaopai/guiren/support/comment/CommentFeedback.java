package com.gaopai.guiren.support.comment;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.CommentGeneralActivity;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class CommentFeedback extends IComment {

	@Override
	public void onCreat(final BaseActivity activity) {
		super.onCreat(activity);
		setTitle(activity.getString(R.string.feedback));
		setBtnText(activity.getString(R.string.send_feedback));
		setEditHint(activity.getString(R.string.please_input_feedback));
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String string = (String) v.getTag();
				if (TextUtils.isEmpty(editText.getText())) {
					activity.showToast(R.string.input_can_not_be_empty);
					return;
				}
				DamiInfo.feedback(editText.getText().toString(), new SimpleResponseListener(activity,
						R.string.request_internet_now) {

					@Override
					public void onSuccess(Object o) {
						// TODO Auto-generated method stub
						BaseNetBean data = (BaseNetBean) o;
						if (data.state != null && data.state.code == 0) {
							showToast(R.string.feed_success);
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

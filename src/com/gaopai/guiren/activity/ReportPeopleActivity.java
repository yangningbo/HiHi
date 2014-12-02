package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class ReportPeopleActivity extends BaseActivity implements OnClickListener {
	public final static String KEY_UID = "uid";
	private String fid;

	private List<TextView> list = new ArrayList<TextView>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_report_people);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.report);
		fid = getIntent().getStringExtra(KEY_UID);
		ViewUtil.findViewById(this, R.id.btn_report).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_rp_abuse).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_rp_ad).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_rp_politics).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_rp_cheat).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_rp_yellow).setOnClickListener(this);
	}

	public static Intent getIntent(Context context, String uid) {
		Intent intent = new Intent(context, ReportPeopleActivity.class);
		intent.putExtra(KEY_UID, uid);
		return intent;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_report:
			if (list.size() == 0) {
				showToast(R.string.rp_chose_reason);
				return;
			}
			DamiInfo.reportUser(fid, getContent(), new SimpleResponseListener(mContext, R.string.request_internet_now) {
				@Override
				public void onSuccess(Object o) {
					// TODO Auto-generated method stub
					BaseNetBean data = (BaseNetBean) o;
					if (data.state != null && data.state.code == 0) {
						showToast(R.string.report_success);
						ReportPeopleActivity.this.finish();
					}
				}
			});
			break;
		case R.id.tv_rp_abuse:
		case R.id.tv_rp_ad:
		case R.id.tv_rp_politics:
		case R.id.tv_rp_cheat:
		case R.id.tv_rp_yellow:
			switchState((TextView) v);
			break;
		default:
			break;
		}
	}

	private String getContent() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		for (TextView view : list) {
			sb.append(view.getText().toString());
			sb.append(",");
		}
		sb.substring(0, sb.length()-1);
		return sb.toString();
	}

	private void switchState(TextView textView) {
		boolean isSelected = list.contains(textView);
		if (isSelected) {
			list.remove(textView);
		} else {
			list.add(textView);
		}
		if (!isSelected) {
			textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_report_selected, 0);
		} else {
			textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
	}

}

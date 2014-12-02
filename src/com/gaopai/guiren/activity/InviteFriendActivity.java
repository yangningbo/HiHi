package com.gaopai.guiren.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.support.ShareManager;
import com.gaopai.guiren.utils.ViewUtil;

public class InviteFriendActivity extends BaseActivity implements OnClickListener {

	private ShareManager sm;
	public final static int REQUEST_CONTACT = 1991;

	private String url = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_invite_friend);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.invite_friend);
		url = getIntent().getStringExtra("url");
		initComponent();
		sm = new ShareManager(this);
	}

	public static Intent getIntent(Context context, String url) {
		Intent intent = new Intent(context, InviteFriendActivity.class);
		intent.putExtra("url", url);
		return intent;
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		ViewUtil.findViewById(this, R.id.tv_invite_contact).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_invite_qq).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_invite_wechat).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_invite_weibo).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String shareStr = getString(R.string.invite_str_2);
		if (!TextUtils.isEmpty(url)) {
			shareStr = url;
		}
		switch (v.getId()) {
		case R.id.tv_invite_contact:
			startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI),
					REQUEST_CONTACT);
			break;
		case R.id.tv_invite_qq:

			sm.shareQQ(shareStr, getString(R.string.invite_link));
			break;
		case R.id.tv_invite_wechat:
			sm.shareWechat(shareStr, getString(R.string.invite_link));
			break;
		case R.id.tv_invite_weibo:
			sm.shareWeibo(shareStr, getString(R.string.invite_link));
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CONTACT && resultCode == RESULT_OK) {
			ContentResolver reContentResolverol = getContentResolver();
			Uri contactData = data.getData();
			@SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(contactData, null, null, null, null);
			cursor.moveToFirst();
			String contactId = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
			Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
			String phoneNum = "";
			while (phone.moveToNext()) {
				phoneNum = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim()
						.replace(" ", "");
			}
			if (TextUtils.isEmpty(phoneNum)) {
				return;
			}
			Uri uri = Uri.parse("smsto:" + phoneNum);
			Intent it = new Intent(Intent.ACTION_SENDTO, uri);
			String shareStr = getString(R.string.invite_str_1);
			if (!TextUtils.isEmpty(url)) {
				shareStr = url;
			}
			it.putExtra("sms_body", shareStr);
			mContext.startActivity(it);
		}
	}
}

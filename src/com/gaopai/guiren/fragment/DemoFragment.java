package com.gaopai.guiren.fragment;

import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.gaopai.guiren.BaseFragment;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.LoginActivity;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.adapter.UserAdapter;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.utils.StringUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.IResponseListener;

public class DemoFragment extends BaseFragment {

	@ViewInject(id = R.id.listView)
	private PullToRefreshListView mListView;
	private UserAdapter mAdapter;
	private String TAG = DemoFragment.class.getName();

	private View onGoingMeetingBtn;
	private View pastMeetingBtn;
	private View myMeetingBtn;

	@Override
	public void addChildView(ViewGroup contentLayout) {
		// TODO Auto-generated method stub
		if (mView == null) {
			mView = mInflater.inflate(R.layout.fragment_meeting, null);
			contentLayout.addView(mView, layoutParamsFF);
			FinalActivity.initInjectedView(this, mView);
			initView();
		}
	}

	private void initView() {
		addButtonToTitleBar();
		mTitleBar.setTitleTextWithImage("进行中的万朝", android.R.drawable.ic_menu_more);

		LayoutInflater layoutInflater = getActivity().getLayoutInflater();
		ViewGroup dropDownView = (ViewGroup) layoutInflater.inflate(R.layout.titlebar_popup_window, null);
		PopupWindowItemClickListener clickListener = new PopupWindowItemClickListener();
		myMeetingBtn = dropDownView.findViewById(R.id.title_popup_text_my_meeting);
		onGoingMeetingBtn = dropDownView.findViewById(R.id.title_popup_text_ongoing_meeting);
		pastMeetingBtn = dropDownView.findViewById(R.id.title_popup_text_past_meeting);
		myMeetingBtn.setOnClickListener(clickListener);
		onGoingMeetingBtn.setOnClickListener(clickListener);
		pastMeetingBtn.setOnClickListener(clickListener);

		mTitleBar.setTitleTextDropDown(dropDownView);
		

		mListView.setPullLoadEnabled(true);
		mListView.setPullRefreshEnabled(true);
		mListView.setScrollLoadEnabled(false);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				Log.d(TAG, "pulldown");
				// TODO Auto-generated method stub
				getUserList(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				Log.d(TAG, "pull up to");
				getUserList(false);
			}
		});

		mAdapter = new UserAdapter(act, 0);
		mListView.setAdapter(mAdapter);
		mListView.doPullRefreshing(true, 0);
	}

	private class PopupWindowItemClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mTitleBar.closeWindow();
			switch (v.getId()) {
			case R.id.title_popup_text_ongoing_meeting:

				break;

			default:
				break;
			}
		}

	}

	private int page = 1;
	private boolean isFull = false;

	private void getUserList(final boolean isRefresh) {
		if (isRefresh) {
			page = 1;
			isFull = false;
		}
		if (isFull) {
			mListView.setHasMoreData(!isFull);
			return;
		}
		Log.d(TAG, "page=" + page);

		DamiInfo.getFriendsList( new IResponseListener() {

			@Override
			public void onTimeOut() {
				// TODO Auto-generated method stub
				act.showToast(R.string.request_timeout);
			}

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final UserList data = (UserList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						isFull = data.data.size() < 20;// has more page
						if (isRefresh) {
							mAdapter.clear();
						}
						page++;
						mAdapter.addAll(data.data);
					} else {
						isFull = true;
					}
					mListView.setHasMoreData(!isFull);
				} else if (data.state != null && data.state.code == DamiCommon.EXPIRED_CODE) {
					DamiCommon.saveLoginResult(act, null);
					DamiCommon.setUid("");
					DamiCommon.setToken("");
					Intent intent = new Intent(act, LoginActivity.class);
					startActivityForResult(intent, MainActivity.LOGIN_REQUEST);
				} else {
					String str;
					if (data.state != null && !StringUtils.isEmpty(data.state.msg)) {
						str = data.state.msg;
					} else {
						str = getString(R.string.load_error);
					}
					act.showToast(str);
				}

			}

			@Override
			public void onReqStart() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinish() {
				mListView.onPullComplete();
			}

			@Override
			public void onFailure(Object o) {
				// TODO Auto-generated method stub
			}
		});
	}
}

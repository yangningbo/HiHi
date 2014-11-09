package com.gaopai.guiren.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.TribeAdapter;
import com.gaopai.guiren.bean.TribeList;
import com.gaopai.guiren.utils.StringUtils;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.volley.IResponseListener;
import com.gaopai.guiren.volley.SimpleResponseListener;

/**
 * 推荐部落列表，从LoginActiviy中跳转过来
 * 
 */
public class RecommendTribeListActivity extends BaseActivity {

	@ViewInject(id = R.id.listView)
	private PullToRefreshListView listView;

	private TribeAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_recommend_tribe);
		FinalActivity.initInjectedView(this);
		init();
	}

	private void init() {
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.recommend_tribe);

		listView.setPullLoadEnabled(false);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getRecommendList();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			}
		});
		adapter = new TribeAdapter(this);
		listView.setAdapter(adapter);
		listView.doPullRefreshing(true, 50);
	}

	private void getRecommendList() {
		DamiInfo.getRecommendTribeList(new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				TribeList data = (TribeList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						adapter.clear();
						adapter.addAll(data.data);
					}
				} else {
					otherCondition(data.state, RecommendTribeListActivity.this);
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				listView.onPullComplete();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MainActivity.LOGIN_REQUEST:
			if (resultCode == RESULT_OK) {
				getRecommendList();
			}
			break;
		default:
			break;
		}
	}

}

package com.gaopai.guiren.activity;

import android.os.Bundle;
import android.view.View;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.ConnectionDetailAdapter;
import com.gaopai.guiren.bean.dynamic.ConnectionBean.TypeHolder;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;

public class ConnectionDetailActivity extends BaseActivity {

	private PullToRefreshListView mListView;
	private ConnectionDetailAdapter mAdapter;
	public final static String KEY_TYPE_HOLDER = "type_holder";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.general_pulltorefresh_listview);
		mTitleBar.setTitleText("人脉详情");
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);

		mListView = (PullToRefreshListView) findViewById(R.id.listView);
		mListView.getRefreshableView().addHeaderView(new View(mContext));
		mListView.getRefreshableView().setSelector(mContext.getResources().getDrawable(R.color.transparent));
		mListView.getRefreshableView().setVerticalScrollBarEnabled(false);
		mListView.setPullRefreshEnabled(false);
		mListView.setPullLoadEnabled(false);
		mListView.setScrollLoadEnabled(false);

		TypeHolder typeHolder = (TypeHolder) getIntent().getSerializableExtra(KEY_TYPE_HOLDER);
		mAdapter = new ConnectionDetailAdapter(typeHolder, mContext);
		mListView.setAdapter(mAdapter);
		mListView.getRefreshableView().setFastScrollEnabled(false);
	}

}

package com.gaopai.guiren.activity;

import android.os.Bundle;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.adapter.TribeAdapter;
import com.gaopai.guiren.fragment.RecommendFriendFragment;
import com.gaopai.guiren.support.FragmentHelper;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;

public class RecommendActivity extends BaseActivity {

	private PullToRefreshListView listView;

	private TribeAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		FragmentHelper
				.replaceFragment(android.R.id.content, getSupportFragmentManager(), RecommendFriendFragment.class);
	}

	@Override
	public void onBackPressed() {
	}
}

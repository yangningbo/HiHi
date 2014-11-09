package com.gaopai.guiren.activity;

import net.tsz.afinal.FinalActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.TribeAdapter;
import com.gaopai.guiren.fragment.RecommendFriendFragment;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;

public class RecommendActivity extends BaseActivity {

	private PullToRefreshListView listView;

	private TribeAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new RecommendFriendFragment())
				.commit();
	}
}

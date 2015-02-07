package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.AutoCompleteAdapter;
import com.gaopai.guiren.adapter.SearchAdapter;
import com.gaopai.guiren.adapter.SearchAdapter.Item;
import com.gaopai.guiren.adapter.SearchAdapter.Row;
import com.gaopai.guiren.adapter.SearchAdapter.Section;
import com.gaopai.guiren.bean.TagBean;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.dynamic.DynamicBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.bean.net.QueryResult;
import com.gaopai.guiren.bean.net.QueryResult.DataHolder;
import com.gaopai.guiren.bean.net.TagResult;
import com.gaopai.guiren.support.TagWindowManager;
import com.gaopai.guiren.view.FlowLayout;
import com.gaopai.guiren.view.TextFlowLayout;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class SearchActivity extends BaseActivity implements OnClickListener {
	private FlowLayout flowLayout;
	private List<TagBean> tagList = new ArrayList<TagBean>();
	private AutoCompleteTextView etSearch;

	public static final String KEY_SEARCH_ORDER = "order";
	public static final int SEARCH_FRIEND = 0;
	public static final int SEARCH_TRIBE = 1;
	public static final int SEARCH_MEETING = 2;
	public static final int SEARCH_DYNAMIC = 3;
	public static final int SEARCH_ALL = 4;

	private PullToRefreshListView mListView;

	private SearchAdapter mAdapter;
	String[] array = new String[] { "人脉", "圈子", "会议", "动态", "全部" };
	private List<String> popupList = new ArrayList<String>();
	private AutoCompleteAdapter autoAdapter;
	private int fromWhere = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		fromWhere = getIntent().getIntExtra(KEY_SEARCH_ORDER, 0);
		popupList.addAll(Arrays.asList(array));
		changeSearhList(popupList, fromWhere);
		initTitleBar();
		setAbContentView(R.layout.activity_search);
		flowLayout = (FlowLayout) findViewById(R.id.flow_tags);
		mListView = (PullToRefreshListView) findViewById(R.id.listview);
		mAdapter = new SearchAdapter(mContext);
		mListView.setAdapter(mAdapter);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		etSearch = mTitleBar.addSearchEditText();
		autoAdapter = new AutoCompleteAdapter(mContext, popupList);
		etSearch.setAdapter(autoAdapter);
		etSearch.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				mType = getSearchType(autoAdapter.mListdata.get(position));
				initialPageInfo();
				getSearchResult();
			}
		});
		View serchView = mTitleBar.addRightButtonView(R.drawable.selector_titlebar_search);
		serchView.setId(R.id.ab_search);
		serchView.setOnClickListener(this);
		getTags();

		mListView.setScrollLoadEnabled(false);
		mListView.setPullRefreshEnabled(false);
		mListView.setPullLoadEnabled(false);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Row row = mAdapter.getItem(position);
				switch (row.type) {
				case SearchAdapter.TYPE_USER:
					User user = (User) ((Item) row).object;
					startActivity(ProfileActivity.getIntent(mContext, user.uid));
					break;
				case SearchAdapter.TYPE_TRIBE:
					Tribe tribe = (Tribe) ((Item) row).object;
					startActivity(TribeDetailActivity.getIntent(mContext, tribe.id));
					break;
				case SearchAdapter.TYPE_MEETING:
					Tribe meeting = (Tribe) ((Item) row).object;
					startActivity(MeetingDetailActivity.getIntent(mContext, meeting.id));
					break;
				case SearchAdapter.TYPE_DYNAMIC:
					TypeHolder typeHolder =  (TypeHolder) ((Item) row).object;
					startActivity(DynamicDetailActivity.getIntent(mContext, typeHolder.id));
					break;
				default:
					break;
				}
			}
		});
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				getSearchResult();
			}
		});
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ab_search:
			mType = SEARCH_ALL;
			initialPageInfo();
			getSearchResult();
			break;
		default:
			break;
		}

	}

	private class TagOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			etSearch.setText(TagWindowManager.getText((ViewGroup) v));
			etSearch.setSelection(etSearch.getText().length());
			initialPageInfo();
			getSearchResult();
		}
	}

	private void getTags() {
		DamiInfo.getTags("search", new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				TagResult data = (TagResult) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data == null || data.data.size() == 0) {
						return;
					}
					tagList = data.data;
					addTags();
				} else {
					this.otherCondition(data.state, SearchActivity.this);
				}
			}
		});
	}

	private int mType = 4;
	private boolean isFull = false;
	private int page = 1;

	private void initialPageInfo() {
		isFull = false;
		page = 1;
	}

	private void getSearchResult() {
		if (isFull) {
			mListView.setHasMoreData(!isFull);
			return;
		}
		DamiInfo.getSearchResult(etSearch.getText().toString(), mType + 1, String.valueOf(mType + 1), page,
				new SimpleResponseListener(mContext, "正在查询") {
					@Override
					public void onSuccess(Object o) {
						// TODO Auto-generated method stub
						QueryResult data = (QueryResult) o;
						if (data.state != null && data.state.code == 0) {
							flowLayout.setVisibility(View.GONE);
							bindSearchView(data.data);
						} else {
							otherCondition(data.state, SearchActivity.this);
						}
					}
				});
	}

	private void bindSearchView(DataHolder data) {
		List<Row> rowList = new ArrayList<Row>();
		int total = 0;
		if (data.user != null && data.user.size() > 0) {
			rowList.add(new Section("用户", SearchAdapter.TYPE_HEADER));
			for (User user : data.user) {
				rowList.add(new Item(user, SearchAdapter.TYPE_USER));
			}
			total = total + data.user.size();
		}
		if (data.tribe != null && data.tribe.size() > 0) {
			rowList.add(new Section("圈子", SearchAdapter.TYPE_HEADER));
			for (Tribe tribe : data.tribe) {
				tribe.isTribeOrMeeting = true;
				rowList.add(new Item(tribe, SearchAdapter.TYPE_TRIBE));
			}
			total = total + data.tribe.size();

		}
		if (data.meeting != null && data.meeting.size() > 0) {
			rowList.add(new Section("会议", SearchAdapter.TYPE_HEADER));
			for (Tribe tribe : data.meeting) {
				tribe.isTribeOrMeeting = false;
				rowList.add(new Item(tribe, SearchAdapter.TYPE_MEETING));
			}
			total = total + data.meeting.size();

		}
		if (data.dynamic != null && data.dynamic.size() > 0) {
			rowList.add(new Section("动态", SearchAdapter.TYPE_HEADER));
			for (TypeHolder dynamic : data.dynamic) {
				rowList.add(new Item(dynamic, SearchAdapter.TYPE_DYNAMIC));
			}
			total = total + data.dynamic.size();

		}
		mAdapter.addAll(rowList);
		if (total == 0) {
			setEmptyListview();
		}
	}

	private void addTags() {
		TagOnClickListener onClickListener = new TagOnClickListener();
		for (TagBean tag : tagList) {
			// TextView textView = flowLayout.creatTextView(tag.tag);
			flowLayout.addView(TagWindowManager.creatTag(tag.tag, onClickListener, mInflater, false),
					flowLayout.getTextLayoutParams());
		}
	}

	private int getSearchType(String name) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(name)) {
				return i;
			}
		}
		return 0;
	}

	private void changeSearhList(List<String> list, int which) {
		String item = list.remove(which);
		list.add(0, item);
	}

	private void setEmptyListview() {
		TextView tv = new TextView(this);
		tv.setText("未搜到相关信息");
		tv.setGravity(Gravity.CENTER);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addContentView(tv, params);
		mListView.getRefreshableView().setEmptyView(tv);
	}


	@Override
	protected void onPause() {
		super.onPause();
		mAdapter.stopPlay();
	}
	
	
}

package com.gaopai.guiren.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.BaseFragment;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.activity.NewDynamicActivity;
import com.gaopai.guiren.adapter.DynamicAdapter;
import com.gaopai.guiren.bean.dynamic.DynamicBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class DynamicFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.listview)
	private PullToRefreshListView mListView;
	private DynamicAdapter mAdapter;
	private String TAG = DynamicFragment.class.getName();

	@ViewInject(id = R.id.chat_box)
	private View chatBox;
	@ViewInject(id = R.id.chat_box_edit_keyword)
	private EditText etComment;
	@ViewInject(id = R.id.send_text_btn)
	private Button mSendTextBtn;

	@Override
	public void addChildView(ViewGroup contentLayout) {
		// TODO Auto-generated method stub
		if (mView == null) {
			mView = mInflater.inflate(R.layout.fragment_dynamic, null);
			((MainActivity) getActivity()).setBackPressedListener(onBackPressedListener);
			contentLayout.addView(mView, layoutParamsFF);
			FinalActivity.initInjectedView(this, mView);
			initView();
		}
	}

	private void initView() {
		addButtonToTitleBar();
		mTitleBar.setTitleText("动态");

		mListView.setPullLoadEnabled(true);
		mListView.setPullRefreshEnabled(true);
		mListView.setScrollLoadEnabled(false);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				Log.d(TAG, "pulldown");
				getDynamicList(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				Log.d(TAG, "pull up to");
				getDynamicList(false);
			}
		});

		mAdapter = new DynamicAdapter(this);
		mListView.setAdapter(mAdapter);
		// mListView.doPullRefreshing(true, 0);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				int pos = position - mListView.getRefreshableView().getHeaderViewsCount();
				mAdapter.viewDynamicDetail((TypeHolder) mAdapter.getItem(pos));
			}
		});
		mSendTextBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (etComment.getText().length() == 0) {
					((BaseActivity) getActivity()).showToast(R.string.input_can_not_be_empty);
					return;
				}
				String teString = etComment.getText().toString();
				mAdapter.commentMessage(teString, (TypeHolder) etComment.getTag());

			}
		});

		etComment.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					// got focus
				} else {
					hideChatBox();
				}
			}
		});
	}

	private int page = 1;
	private boolean isFull = false;

	private void getDynamicList(final boolean isRefresh) {
		if (isRefresh) {
			page = 1;
			isFull = false;
		}
		if (isFull) {
			mListView.setHasMoreData(!isFull);
			return;
		}
		Log.d(TAG, "page=" + page);

		DamiInfo.getDynamic(page, new SimpleResponseListener(getActivity()) {
			@Override
			public void onSuccess(Object o) {
				final DynamicBean data = (DynamicBean) o;
				if (data.state != null && data.state.code == 0) {
					addHeader(data.state.newalertcount);
					if (data.data != null && data.data.size() > 0) {
						if (isRefresh) {
							mAdapter.clear();
						}
						mAdapter.addAll(data.data);
					}
					if (data.pageInfo != null) {
						isFull = (data.pageInfo.hasMore == 0);
						if (!isFull) {
							page++;
						}
					}
					mListView.setHasMoreData(!isFull);
				} else {
					otherCondition(data.state, getActivity());
				}
			}

			@Override
			public void onFinish() {
				mListView.onPullComplete();
			}
		});
	}

	public void showChatBox(String name, TypeHolder typeHolder) {
		chatBox.setVisibility(View.VISIBLE);
		etComment.requestFocus();
		etComment.setTag(typeHolder);
		etComment.setHint("回复：" + name);
		((MainActivity) getActivity()).hideBottomTab();
	}

	public void hideChatBox() {
		chatBox.setVisibility(View.GONE);
		((MainActivity) getActivity()).showBottomTab();
	}
	
	

	private BackPressedListener onBackPressedListener = new BackPressedListener() {
		@Override
		public boolean onBack() {
			// TODO Auto-generated method stub
			if (chatBox.getVisibility() == View.VISIBLE) {
				hideChatBox();
				return true;
			}
			return false;
		}
	};

	public static interface BackPressedListener {
		public boolean onBack();
	}

	private void addHeader(int newalertcount) {
		ListView listView = mListView.getRefreshableView();
		if (newalertcount == 0) {
			if (listView.getHeaderViewsCount() > 0) {
				listView.removeHeaderView(listView.getChildAt(0));
			}
			return;
		}
		if (listView.getHeaderViewsCount() > 0) {
			listView.removeHeaderView(listView.getChildAt(0));
		}
		TextView textView = new TextView(getActivity());
		textView.setText("您有" + newalertcount + "条新动态");
		textView.setTextColor(getResources().getColor(R.color.general_text_blue));
		textView.setBackgroundResource(R.drawable.selector_btn_round_gray);
		int padding = MyUtils.dip2px(getActivity(), 5);
		textView.setPadding(2 * padding, padding, 2 * padding, padding);
		textView.setGravity(Gravity.CENTER);
		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(NewDynamicActivity.class);
			}
		});

		LinearLayout layout = new LinearLayout(getActivity());
		LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(padding, padding, padding, padding);
		layout.setGravity(Gravity.CENTER);
		layout.addView(textView, lp);
		mListView.getRefreshableView().addHeaderView(layout);
	}

}

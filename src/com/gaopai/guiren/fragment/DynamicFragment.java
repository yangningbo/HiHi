package com.gaopai.guiren.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
import com.gaopai.guiren.support.view.CustomEditText;
import com.gaopai.guiren.utils.Logger;
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
	private CustomEditText etComment;
	@ViewInject(id = R.id.send_text_btn)
	private Button mSendTextBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView == null) {
			mView = mInflater.inflate(R.layout.fragment_dynamic, null);
			((MainActivity) getActivity()).setBackPressedListener(onBackPressedListener);
			FinalActivity.initInjectedView(this, mView);
			initView();
		}
		return mView;
	}

	private void initView() {
		mListView.setPullLoadEnabled(true);
		mListView.setPullRefreshEnabled(true);
		mListView.setScrollLoadEnabled(false);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getDynamicList(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				getDynamicList(false);
			}
		});

		addNewDyHeader();
		mAdapter = new DynamicAdapter(this);
		mListView.setAdapter(mAdapter);
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
		etComment.setBackPressedListener(onBackPressedListener);
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

		DamiInfo.getDynamic(page, new SimpleResponseListener(getActivity()) {
			@Override
			public void onSuccess(Object o) {
				final DynamicBean data = (DynamicBean) o;
				if (data.state != null && data.state.code == 0) {
					setHeaderCount(data.state.newalertcount);
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

	public void showChatBox(String name, TypeHolder typeHolder, boolean showReply) {
		chatBox.setVisibility(View.VISIBLE);
		etComment.requestFocus();
		etComment.setTag(typeHolder);
		if (showReply) {
			etComment.setHint("回复：" + name);
		} else {
			etComment.setHint("请输入评论");
		}
		((MainActivity) getActivity()).hideBottomTab();
	}

	public void hideChatBox() {
		chatBox.setVisibility(View.GONE);
		etComment.setText("");
		hideSoftKeyboard(etComment);
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

	private TextView tvDyCount;

	private void addNewDyHeader() {
		tvDyCount = new TextView(getActivity());

		tvDyCount.setTextColor(getResources().getColor(R.color.general_text_blue));
		tvDyCount.setBackgroundResource(R.drawable.selector_btn_round_gray);
		int padding = MyUtils.dip2px(getActivity(), 5);
		tvDyCount.setPadding(2 * padding, padding, 2 * padding, padding);
		tvDyCount.setGravity(Gravity.CENTER);
		tvDyCount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvDyCount.setVisibility(View.GONE);
				startActivity(NewDynamicActivity.class);
			}
		});
		tvDyCount.setVisibility(View.GONE);
		LinearLayout layout = new LinearLayout(getActivity());
		LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(padding, padding, padding, padding);
		layout.setGravity(Gravity.CENTER);
		layout.addView(tvDyCount, lp);
		mListView.getRefreshableView().addHeaderView(layout);
	}

	private void setHeaderCount(int newalertcount) {
		if (newalertcount == 0) {
			tvDyCount.setVisibility(View.GONE);
			return;
		}
		tvDyCount.setVisibility(View.VISIBLE);
		tvDyCount.setText("您有" + newalertcount + "条新动态");
	}
	

	public void hideSoftKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

}

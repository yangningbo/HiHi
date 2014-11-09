package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;

public class DynamicDetailActivity extends BaseActivity {
	private PullToRefreshListView mListView;
	private View headerView;
	private TextView tvZan;
	private EditText etContent;
	private View chatBox;

	private List<String> testUserList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.fragment_dynamic);
		mTitleBar.setTitleText("动态详情");
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		initComponent();
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		etContent = (EditText) findViewById(R.id.chat_box_edit_keyword);
		chatBox = findViewById(R.id.chat_box);
		mListView = (PullToRefreshListView) findViewById(R.id.listview);
		mListView.getRefreshableView().setDivider(null);
		mListView.getRefreshableView().setSelector(mContext.getResources().getDrawable(R.color.transparent));
		headerView = mInflater.inflate(R.layout.activity_dynamic_detail_header, null);
		mListView.getRefreshableView().addHeaderView(headerView);
		mListView.setAdapter(new MyAdapter(this));

		tvZan = (TextView) headerView.findViewById(R.id.tv_zan);
		tvZan.setOnTouchListener(MyTextUtils.mTextOnTouchListener);

		for (int i = 0; i < 10; i++) {
			testUserList.add("张三");
		}
		tvZan.setText(MyTextUtils.addUserListLinks(testUserList));
	}

	class MyAdapter extends BaseAdapter {
		private List<Integer> mData = new ArrayList<Integer>();

		public MyAdapter(Context context) {
			mContext = context;
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			Random random = new Random(50);
			for (int i = 0; i < 50; i++) {
				mData.add(random.nextInt(5));
			}

		}

		public void addAll(List<Integer> o) {
			mData.addAll(o);
			notifyDataSetChanged();
		}

		public void clear() {
			mData.clear();
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView tvComment;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_dynamic_detail_comment, null);
				tvComment = (TextView) convertView.findViewById(R.id.tv_comment_item);
				convertView.setTag(tvComment);
			} else {
				tvComment = (TextView) convertView.getTag();
			}
			setTextView(tvComment);
			return convertView;
		}

		private void setTextView(TextView textView) {
			textView.setBackgroundResource(R.drawable.selector_gray_blue_btn);
			textView.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			textView.setText(MyTextUtils.addUserHttpLinks("香港回复中国：老子就是不回来，你wawkao", "香港", "中国", "0", "0"));
			textView.setOnClickListener(commentOnClickListener);
		}

		private OnClickListener commentOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showChatBox(v);
				showSoftKeyboard();
			}
		};

		public void showSoftKeyboard() {
			InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		}
	}

	public void showChatBox(View v) {
		chatBox.setVisibility(View.VISIBLE);
		etContent.requestFocus();
		etContent.setText(((TextView) v).getText());
	}

	public void hideChatBox() {
		chatBox.setVisibility(View.GONE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (chatBox.getVisibility() == View.VISIBLE) {
				hideChatBox();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}

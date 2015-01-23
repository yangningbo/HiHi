package com.gaopai.guiren.support.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.gaopai.guiren.R;

public class ChatDetailFixedHeader extends RelativeLayout {
	private View layoutZanCopy;
	private View layoutCommentCopy;

	private int zanOriginTop;
	private int commentOriginTop;

	public ChatDetailFixedHeader(Context context) {
		super(context);
	}

	public ChatDetailFixedHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		layoutCommentCopy = findViewById(R.id.layout_comment_header_copy);
		layoutCommentCopy.setVisibility(View.GONE);
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount,
			View comment, View headerView) {
		if (comment == null) {
			return;
		}
		if (firstVisibleItem == 0) {
			int scrollY = headerView.getTop();
			int zanTop = comment.getTop();
			if (scrollY + zanTop <= 0) {
				layoutCommentCopy.setVisibility(View.VISIBLE);
			} else {
				layoutCommentCopy.setVisibility(View.GONE);
			}
		} else {
			layoutCommentCopy.setVisibility(View.VISIBLE);
		}
	}
}

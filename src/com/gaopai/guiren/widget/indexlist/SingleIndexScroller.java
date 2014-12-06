/*
 * Copyright 2011 woozzu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gaopai.guiren.widget.indexlist;

import com.gaopai.guiren.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.SectionIndexer;

public class SingleIndexScroller extends View {

	private float mIndexbarWidth;
	private float mIndexbarMargin;
	private float mDensity;
	private float mScaledDensity;
	private float mAlphaRate;
	private int mState = STATE_HIDDEN;
	private IndexableListView mListView = null;
	private int mCurrentSection = -1;
	private SectionIndexer mIndexer = null;
	private String[] mSections = null;
	private RectF mIndexbarRect;

	private static final int STATE_HIDDEN = 0;
	private static final int STATE_SHOWN = 2;

	public SingleIndexScroller(Context context) {
		super(context);
		init(context);
	}

	public SingleIndexScroller(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SingleIndexScroller(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private Context mContext;

	private void init(Context context) {
		mContext = context;
		mDensity = context.getResources().getDisplayMetrics().density;
		mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;

		mIndexbarWidth = 20 * mDensity;
		mIndexbarMargin = 10 * mDensity;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		Paint indexbarPaint = new Paint();
		indexbarPaint.setColor(Color.TRANSPARENT);
//		indexbarPaint.setAlpha((int) (64 * mAlphaRate));
		indexbarPaint.setAntiAlias(true);
		canvas.drawRect(mIndexbarRect, indexbarPaint);
		if (mSections != null && mSections.length > 0) {
			Paint indexPaint = new Paint();
			indexPaint.setColor(getResources().getColor(R.color.general_btn_blue_normal));
			indexPaint.setAntiAlias(true);
			indexPaint.setTextSize(12 * mScaledDensity);

			float sectionHeight = (mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.length;
			float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint.ascent())) / 2;
			for (int i = 0; i < mSections.length; i++) {
				float paddingLeft = (mIndexbarWidth - indexPaint.measureText(mSections[i])) / 2;
				canvas.drawText(mSections[i], mIndexbarRect.left + paddingLeft, mIndexbarRect.top + mIndexbarMargin
						+ sectionHeight * i + paddingTop - indexPaint.ascent(), indexPaint);
			}
		}
	}

	private int oldSelection = 0;
	private int newSelection = 0;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		int selection;
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// If down event occurs inside index bar region, start indexing
			setState(STATE_SHOWN);
			mAlphaRate = 0.5f;
			mCurrentSection = getSectionByPoint(ev.getY());
			mListView.setCurrentSection(mCurrentSection);
			newSelection = mIndexer.getPositionForSection(mCurrentSection);
			selection = (newSelection == -1) ? 0 : newSelection;
			oldSelection = selection;
			mListView.setSelection(selection+mListView.getHeaderViewsCount());
			invalidate();
			return true;
		case MotionEvent.ACTION_MOVE:
			mCurrentSection = getSectionByPoint(ev.getY());
			mAlphaRate = 0.5f;
			mListView.setCurrentSection(mCurrentSection);
			newSelection = mIndexer.getPositionForSection(mCurrentSection);
			selection = (newSelection == -1) ? oldSelection : newSelection;
			oldSelection = selection;
			mListView.setSelection(selection+mListView.getHeaderViewsCount());
			return true;
		case MotionEvent.ACTION_UP:
			hide();
			break;
		}

		return false;
	}

	public void hide() {
		if (mState != STATE_HIDDEN) {
			mAlphaRate = 0f;
			mCurrentSection = -1;
			mListView.setCurrentSection(mCurrentSection);
			mListView.invalidate();
			invalidate();
			setState(STATE_HIDDEN);
		}

	}

	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mIndexbarRect == null) {
			mIndexbarRect = new RectF(0, 0, w, h);
		}
	}

	public void setAdapter(Adapter adapter) {
		ListAdapter listAdapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
		if (listAdapter instanceof SectionIndexer) {
			mIndexer = (SectionIndexer) listAdapter;
			mSections = (String[]) mIndexer.getSections();
		}
	}

	public void setListView(IndexableListView listView) {
		mListView = listView;
		setAdapter(listView.getAdapter());
		invalidate();
	}

	private void setState(int state) {

		mState = state;
		switch (mState) {
		case STATE_HIDDEN:
			mAlphaRate = 0;
			break;
		case STATE_SHOWN:
			mAlphaRate = 1;
			break;
		}
	}

	private int getSectionByPoint(float y) {
		if (mSections == null || mSections.length == 0)
			return 0;
		if (y < mIndexbarRect.top + mIndexbarMargin)
			return 0;
		if (y >= mIndexbarRect.top + mIndexbarRect.height() - mIndexbarMargin)
			return mSections.length - 1;
		return (int) ((y - mIndexbarRect.top - mIndexbarMargin) / ((mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.length));
	}
}

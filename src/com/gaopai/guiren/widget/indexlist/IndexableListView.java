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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class IndexableListView extends ListView {

	private boolean mIsFastScrollEnabled = false;
	// private IndexScroller mScroller = null;
	private float mScaledDensity;
	private float mPreviewPadding;
	private float mDensity;
	private int mCurrentSection = -1;
	private String[] mSections = null;
	private SectionIndexer mIndexer = null;
	private int mListViewWidth;
	private int mListViewHeight;

	public IndexableListView(Context context) {
		super(context);
		init(context);
	}

	public IndexableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public IndexableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.setHeaderDividersEnabled(false);
		mDensity = context.getResources().getDisplayMetrics().density;
		mPreviewPadding = 5 * mDensity;
		mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
	}

	@Override
	public boolean isFastScrollEnabled() {
		return mIsFastScrollEnabled;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		drawRetangle(canvas);
	}

	public void setCurrentSection(int section) {
		mCurrentSection = section;
	}

	private void drawRetangle(Canvas canvas) {
		if (mCurrentSection >= 0) {
			Paint previewPaint = new Paint();
			previewPaint.setColor(Color.BLACK);
			previewPaint.setAlpha(96);
			previewPaint.setAntiAlias(true);
			previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));

			Paint previewTextPaint = new Paint();
			previewTextPaint.setColor(Color.WHITE);
			previewTextPaint.setAntiAlias(true);
			previewTextPaint.setTextSize(50 * mScaledDensity);

			float previewTextWidth = previewTextPaint.measureText(mSections[mCurrentSection]);
			float previewSize = 2 * mPreviewPadding + previewTextPaint.descent() - previewTextPaint.ascent();
			RectF previewRect = new RectF((mListViewWidth - previewSize) / 2, (mListViewHeight - previewSize) / 2, (mListViewWidth - previewSize) / 2 + previewSize, (mListViewHeight - previewSize)
					/ 2 + previewSize);

			canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity, previewPaint);
			canvas.drawText(mSections[mCurrentSection], previewRect.left + (previewSize - previewTextWidth) / 2 - 1, previewRect.top + mPreviewPadding - previewTextPaint.ascent() + 1,
					previewTextPaint);
		}
	}


	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		mIndexer = (SectionIndexer) adapter;
		mSections = (String[]) mIndexer.getSections();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mListViewWidth = w;
		mListViewHeight = h;

	}

}

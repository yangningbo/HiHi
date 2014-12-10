/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gaopai.guiren.support.zxing;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.gaopai.guiren.R;
import com.gaopai.guiren.support.zxing.camera.CameraManager;
import com.gaopai.guiren.utils.MyUtils;
import com.google.zxing.ResultPoint;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192, 128, 64 };
	private static final long ANIMATION_DELAY = 20L;
	private static final int OPAQUE = 0xFF;

	private final Paint paint;
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;
	private final int frameColor;
	private final int laserColor;
	private final int resultPointColor;
	private int scannerAlpha;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;

	private Bitmap lineBitmap;
	private Bitmap borderBitmap;
	private Rect lineRect = new Rect();
	private String scanInfo = "将二维码放入框内，即可自动扫描";
	private Paint textPaint;

	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Initialize these once for performance rather than calling them every
		// time in onDraw().
		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		frameColor = resources.getColor(R.color.viewfinder_frame);
		laserColor = resources.getColor(R.color.viewfinder_laser);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		scannerAlpha = 0;
		possibleResultPoints = new HashSet<ResultPoint>(5);

		lineBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_scan_line);
		borderBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_scan_border);
		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setAntiAlias(true);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setTextSize(MyUtils.dip2px(getContext(), 16));
	}

	@Override
	public void onDraw(Canvas canvas) {
		Rect frame = CameraManager.get().getFramingRect();
		if (frame == null) {
			return;
		}
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {

			// Draw a two pixel solid black border inside the framing rect
			// paint.setColor(frameColor);
			// canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top
			// + 2, paint);
			// canvas.drawRect(frame.left, frame.top + 2, frame.left + 2,
			// frame.bottom - 1, paint);
			// canvas.drawRect(frame.right - 1, frame.top, frame.right + 1,
			// frame.bottom - 1, paint);
			// canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1,
			// frame.bottom + 1, paint);
			//
			// ===================add=================
			canvas.drawBitmap(borderBitmap, null, frame, paint);
			if (barLinePosTotal > frame.height()) {
				barLinePosTotal = 0;
			}
			lineRect.top = Math.max(frame.top, frame.top + barLinePosTotal - lineBitmap.getHeight() / 2);
			lineRect.bottom = Math.min(frame.bottom, frame.top + barLinePosTotal + lineBitmap.getHeight() / 2);
			lineRect.left = frame.left;
			lineRect.right = frame.right;
			canvas.drawBitmap(lineBitmap, null, lineRect, paint);
			barLinePosTotal = barLinePosTotal + 8;

			canvas.drawText(scanInfo, frame.centerX(), frame.bottom + MyUtils.dip2px(getContext(), 40), textPaint);
			// ==================add=======================

			// Draw a red "laser scanner" line through the middle to show
			// decoding is active
			// paint.setColor(laserColor);
			// paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
			// scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
			// int middle = frame.height() / 2 + frame.top;
			// canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1,
			// middle + 2, paint);

			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				// for (ResultPoint point : currentPossible) {
				// canvas.drawCircle(frame.left + point.getX(), frame.top +
				// point.getY(), 6.0f, paint);
				// }
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				// for (ResultPoint point : currentLast) {
				// canvas.drawCircle(frame.left + point.getX(), frame.top +
				// point.getY(), 3.0f, paint);
				// }
			}

			// Request another update at the animation interval, but only
			// repaint the laser line,
			// not the entire viewfinder mask.
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
		}
	}

	private int barLinePosTotal = 0;

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}

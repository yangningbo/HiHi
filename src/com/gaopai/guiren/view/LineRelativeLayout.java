package com.gaopai.guiren.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyUtils;

/**
 * @author 
 * draw the vertical line between comments, spreadtext and zantext in profile
 */
public class LineRelativeLayout extends RelativeLayout {
	private boolean isLineHalf = false;
	private Paint linePaint;
	private int linePaddintLeft;
	private int lineHalfHeight;

	public LineRelativeLayout(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	public LineRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}
	public LineRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		setWillNotDraw(false);
		linePaint = new Paint();
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeWidth(2f);
		linePaint.setColor(getResources().getColor(R.color.profile_vertical_divider));
		linePaddintLeft = MyUtils.dip2px(getContext(), 25);
		lineHalfHeight = MyUtils.dip2px(getContext(), 20);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
//		if (isLineHalf) {
//			getChildAt(0).getLayoutParams().height = MyUtils.dip2px(getContext(), 20);
//		} else {
//			getChildAt(0).getLayoutParams().height = h;
//		}
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	public void setLineHalf(boolean isHalf) {
		this.isLineHalf = isHalf;
		invalidate();
	}
	
	public void setLineHalfOnly(boolean isHalf) {
		this.isLineHalf = isHalf;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
//		Log.d("view", "==============="+getHeight() +" ====" + getMeasuredHeight());
		super.onDraw(canvas);
		if (isLineHalf) {
			canvas.drawLine(linePaddintLeft, 0, linePaddintLeft, lineHalfHeight, linePaint);
		} else {
			canvas.drawLine(linePaddintLeft, 0, linePaddintLeft, getHeight(), linePaint);
		}
	}
	
	
	
	

}

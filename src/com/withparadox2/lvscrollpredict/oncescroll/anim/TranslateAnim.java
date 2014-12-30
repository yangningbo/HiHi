package com.withparadox2.lvscrollpredict.oncescroll.anim;

import android.R.integer;
import android.view.View;

public class TranslateAnim extends AnimBase {

	private float xFrom;
	private float yFrom;
	private float xTo;
	private float yTo;

	private int type = 0;// 0 center 1=top/left 2=top/right 3=bottom/left
							// 4=bottom/right

	public TranslateAnim(float sPos, float ePos, float xfrom, float xto, float yfrom, float yto, View v) {
		super(sPos, ePos, v);
		this.yFrom = yfrom;
		this.xFrom = xfrom;
		this.xTo = xto;
		this.yTo = yto;
	}

	public TranslateAnim(float sPos, float ePos, float xfrom, float xto, float yfrom, float yto, View v, int type) {
		super(sPos, ePos, v);
		this.yFrom = yfrom;
		this.xFrom = xfrom;
		this.xTo = xto;
		this.yTo = yto;
		this.type = type;
	}

	@Override
	public void anim(float pos) {

		if (isReadyAnim(pos)) {
			int width = view.getMeasuredWidth();
			int height = view.getMeasuredHeight();
			int t = (int) interpolator(pos, yFrom, yTo);
			int l = (int) interpolator(pos, xFrom, xTo);
			switch (type) {
			case 0:
				view.layout(l - width / 2, t - height / 2, l + width / 2, t + height / 2);
				break;
			case 1:
				view.layout(l, t, l + width, t + height);
				break;
			case 2:
				view.layout(l - width, t, l, t + height);
				break;
			case 3:
				view.layout(l, t - height, l + width, t);
				break;
			case 4:
				view.layout(l - width, t - height, l, t);
				break;
			default:
				break;
			}

		}
	}
}

package com.withparadox2.lvscrollpredict.oncescroll.anim;

import android.view.View;

public class ScaleAnim extends AnimBase {
	public ScaleAnim(float sPos, float ePos, float from, float to, View v) {
		super(sPos, ePos, from, to, v);
	}

	@Override
	public void anim(float pos) {
		// TODO Auto-generated method stub
		if (isReadyAnim(pos)) {
			float value = interpolator(pos);
			view.setScaleX(value);
			view.setScaleY(value);
		}
	}
}

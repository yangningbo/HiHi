package com.withparadox2.lvscrollpredict.oncescroll.anim;

import android.view.View;

public class AlphaAnim extends AnimBase{

	public AlphaAnim(float sPos, float ePos, float from, float to, View v) {
		super(sPos, ePos, from, to, v);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void anim(float pos) {
		if (isReadyAnim(pos)) {
			view.setAlpha(interpolator(pos));
		}
	}

}

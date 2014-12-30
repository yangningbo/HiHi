package com.withparadox2.lvscrollpredict.oncescroll.anim;

import android.view.View;

public abstract class AnimBase {
	public float startPos;
	public float endPos;

	public AnimBase(float sPos, float ePos, View v) {
		this.startPos = sPos;
		this.endPos = ePos;
		this.view = v;
	}

	public boolean isReadyAnim(float pos) {
		return (pos >= startPos) && (pos <= endPos);
	}

	public float from;
	public float to;
	public View view;

	public AnimBase(float sPos, float ePos, float from, float to, View v) {
		this.startPos = sPos;
		this.endPos = ePos;
		this.from = from;
		this.to = to;
		this.view = v;
	}

	public float interpolator(float pos) {
		return from + (pos - startPos) / (endPos - startPos) * (to - from);
	}

	public float interpolator(float pos, float from, float to) {
		return from + (pos - startPos) / (endPos - startPos) * (to - from);
	}

	public abstract void anim(float pos);
}

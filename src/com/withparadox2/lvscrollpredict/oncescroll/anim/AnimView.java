package com.withparadox2.lvscrollpredict.oncescroll.anim;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AnimView extends ImageView {
	public AnimView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AnimView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public AnimView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public List<AnimBase> anims = new ArrayList<AnimBase>();

	public void addAnim(AnimBase anim) {
		anims.add(anim);
	}

	public void addTrAnim(float sPos, float ePos, float xfrom, float xto, float yfrom, float yto) {
		TranslateAnim anim = new TranslateAnim(sPos, ePos, xfrom, xto, yfrom, yto, this);
		anims.add(anim);
	}

	public void addTrAnimTL(float sPos, float ePos, float xfrom, float xto, float yfrom, float yto) {
		TranslateAnim anim = new TranslateAnim(sPos, ePos, xfrom, xto, yfrom, yto, this, 1);
		anims.add(anim);
	}

	public void addTrAnimTR(float sPos, float ePos, float xfrom, float xto, float yfrom, float yto) {
		TranslateAnim anim = new TranslateAnim(sPos, ePos, xfrom, xto, yfrom, yto, this, 2);
		anims.add(anim);
	}

	public void addTrAnimBL(float sPos, float ePos, float xfrom, float xto, float yfrom, float yto) {
		TranslateAnim anim = new TranslateAnim(sPos, ePos, xfrom, xto, yfrom, yto, this, 3);
		anims.add(anim);
	}

	public void addTrAnimBR(float sPos, float ePos, float xfrom, float xto, float yfrom, float yto) {
		TranslateAnim anim = new TranslateAnim(sPos, ePos, xfrom, xto, yfrom, yto, this, 4);
		anims.add(anim);
	}

	public void addScaleAnim(float sPos, float ePos, float from, float to) {
		ScaleAnim anim = new ScaleAnim(sPos, ePos, from, to, this);
		anims.add(anim);
	}

	public void addAlphaAnim(float sPos, float ePos, float from, float to) {
		AlphaAnim anim = new AlphaAnim(sPos, ePos, from, to, this);
		anims.add(anim);
	}

	public void addRotateAnim(float sPos, float ePos, float from, float to) {
		RotateAnim anim = new RotateAnim(sPos, ePos, from, to, this);
		anims.add(anim);
	}

	public void layout(float total) {
		for (AnimBase anim : anims) {
			anim.anim(total);
		}
	}
	

	// 从第一个到第三个 from=1, t0=3
	public void layout(float total, int from, int to) {
		for (int i = from - 1; i < to; i++) {
			anims.get(i).anim(total);
		}
	}

	public void layout(float total, int which) {
		anims.get(which).anim(total);
	}

	public void layoutCR(int l, int t) {
		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		this.layout(l - width / 2, t - height / 2, l + width / 2, t + height / 2);
	}

	public void layoutTL(int l, int t) {
		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		this.layout(l, t, l + width, t + height);
	}

	public void layoutTR(int l, int t) {
		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		this.layout(l - width, t, l, t + height);
	}

	public void layoutBL(int f, int g) {
		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		this.layout(f, g - height, f + width, g);
	}

	public void layoutBR(int l, int t) {
		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		this.layout(l - width, t - height, l, t);
	}
}

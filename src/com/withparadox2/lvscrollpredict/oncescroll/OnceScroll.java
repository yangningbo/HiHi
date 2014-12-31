package com.withparadox2.lvscrollpredict.oncescroll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Scroller;
import android.widget.Toast;

import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.Logger;
import com.withparadox2.lvscrollpredict.oncescroll.anim.AnimView;

public class OnceScroll extends ViewGroup {

	public static interface Callback {
		public void onClick();
	}

	private Callback callback;

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public OnceScroll(Context context) {
		super(context);
		init();
	}

	public OnceScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public OnceScroll(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		gestureDetector = new GestureDetector(getContext(), new MyGestureListener());
		this.setLongClickable(true);
		mScroller = new Scroller(getContext());
		configuration = ViewConfiguration.get(getContext());
		initView();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return true;
	}

	float yTotal;
	float preY;

	private boolean isFling = false;

	ViewConfiguration configuration;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mScroller.forceFinished(true);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			isFling = false;
		}

		boolean re = gestureDetector.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (isFling) {
				return re;
			}
			if (yTotal < 0) {
				yTotal = 0;
			}
			int stage = (int) (yTotal / getSH(0.2f));
			int offSet = (int) (getSH(0.2f) * (stage + 1) - (int) yTotal);
			if (offSet > yTotal) {
				mScroller.startScroll(0, (int) yTotal, 0, (int) (getSH(0.2f) * (stage + 1) - (int) yTotal), 2000);
			} else {
				mScroller.startScroll(0, (int) yTotal, 0, (int) ((int) (getSH(0.2f) * stage) - yTotal), 2000);
			}
			ViewCompat.postInvalidateOnAnimation(this);
		}
		return re;
	}

	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			yTotal += distanceY;
			requestLayout();
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			isFling = true;
			fling((int) -velocityX, (int) velocityY);
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Rect enterRect = new Rect(s4EnterApp.getLeft(), s4EnterApp.getTop(), s4EnterApp.getRight(),
					s4EnterApp.getBottom());
			if (enterRect.contains((int) e.getX(), (int) e.getY())) {
				if (callback != null) {
					callback.onClick();
				}
			}
			return true;
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			yTotal = mScroller.getCurrY();
			ViewCompat.postInvalidateOnAnimation(this);
			requestLayout();
		}
	}

	private void fling(int velocityX, int velocityY) {

		// Before flinging, aborts the current animation.
		mScroller.forceFinished(true);
		// Begins the animation
		Logger.d(this, "fling  ytotal=" + yTotal + "   vel=" + velocityY);
		// mScroller.fling(0, (int) yTotal, velocityX, 2, 0, 0, (int)
		// yTotal+800, (int) yTotal+800);
		// mScroller.startScroll(0, (int) yTotal, 0, 800, 8000);
		if (yTotal < 0) {
			yTotal = 0;
		}
		int stage = (int) (yTotal / getSH(0.2f));
		int duration = (int) (500 + getVelPercent(Math.abs(velocityY)) * 1000);

		if (velocityY < 0) {
			mScroller.startScroll(0, (int) yTotal, 0, (int) (getSH(0.2f) * (stage + 1) - (int) yTotal), duration);
		} else {
			mScroller.startScroll(0, (int) yTotal, 0, (int) ((int) (getSH(0.2f) * stage) - yTotal), duration);
		}
		ViewCompat.postInvalidateOnAnimation(this);
	}

	private float getVelPercent(int velocityY) {
		return 1f - ((float) velocityY - configuration.getScaledMinimumFlingVelocity())
				/ (configuration.getScaledMaximumFlingVelocity() - configuration.getScaledMinimumFlingVelocity());
	}

	LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	private AnimView creatView(int resId) {
		AnimView animView = new AnimView(getContext());
		animView.setImageResource(resId);
		addView(animView, lp);
		return animView;
	}

	private float pageHeight = 0.5f;

	private void initView() {
		gTitle = creatView(R.drawable.icon_title);
		// stage1
		dot1 = creatView(R.drawable.icon_dot);
		dot2 = creatView(R.drawable.icon_dot);
		dot3 = creatView(R.drawable.icon_dot);
		dot4 = creatView(R.drawable.icon_dot);
		dot5 = creatView(R.drawable.icon_dot);
		dot6 = creatView(R.drawable.icon_dot);
		s1Paper = creatView(R.drawable.stage1_paper);
		s1Zixun = creatView(R.drawable.stage1_master_info);
		s1Shangji = creatView(R.drawable.stage1_economic_in_hand);

		// stage2
		s2Earth = creatView(R.drawable.stage2_earth);
		s2Moon = creatView(R.drawable.stage2_moon);
		s2InfoRenmai = creatView(R.drawable.stage2_remai);
		s2InfoTribehasme = creatView(R.drawable.stage2_tribe_has_me);

		s2HeadJiayueting = creatView(R.drawable.stage2_head_jiayueting);
		s2HeadZhouhongyi = creatView(R.drawable.stage2_head_zhouhongyi);
		s2HeadFengxin = creatView(R.drawable.stage2_head_fengxing);
		s2HeadTaoran = creatView(R.drawable.stage2_head_taoran);
		s2HeadLiyu = creatView(R.drawable.stage2_head_liyu);
		s2HeadZhangtao = creatView(R.drawable.stage2_head_zhangtao);

		s2NameJiayueting = creatView(R.drawable.stage2_name_jiayueting);
		s2NameZhouhongyi = creatView(R.drawable.stage2_name_zhouhongyi);
		s2NameFengxin = creatView(R.drawable.stage2_name_fengxing);
		s2NameTaoran = creatView(R.drawable.stage2_name_taoran);
		s2NameLiyu = creatView(R.drawable.stage2_name_liyu);
		s2NameZhangtao = creatView(R.drawable.stage2_name_zhangtao);

		// stage3
		s3Circle = creatView(R.drawable.stage_general_circle);
		s3Border = creatView(R.drawable.stage3_border);
		s3Info1 = creatView(R.drawable.stage3_meeting_online);
		s3Info2 = creatView(R.drawable.stage3_communication_easy);
		s3Teacher = creatView(R.drawable.stage3_teacher);
		s3Stu1 = creatView(R.drawable.stage3_student_1);
		s3Stu3 = creatView(R.drawable.stage3_student_1);
		s3Stu2 = creatView(R.drawable.stage3_student_2);

		// stage4
		s4HeadPart = creatView(R.drawable.stage4_head_part);
		s4HeadFull = creatView(R.drawable.stage4_head_full);
		s4Circle = creatView(R.drawable.stage_general_circle);
		s4EnterApp = creatView(R.drawable.stage4_enter_app);
		s4InfoContact = creatView(R.drawable.stage4_info_use_fake_voice);
		s4InfoDisapper = creatView(R.drawable.stage4_info_disappear_after_talk);
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
	}

	private AnimView gTitle;
	private AnimView dot1;
	private AnimView dot2;
	private AnimView s1Paper;
	private AnimView s1Zixun;
	private AnimView s1Shangji;

	private AnimView dot3;
	private AnimView dot4;

	private AnimView s2Earth;
	private AnimView s2Moon;
	private AnimView s2InfoRenmai;
	private AnimView s2InfoTribehasme;
	private AnimView dot5;
	private AnimView dot6;

	private AnimView s2HeadJiayueting;
	private AnimView s2HeadZhouhongyi;
	private AnimView s2HeadFengxin;
	private AnimView s2HeadTaoran;
	private AnimView s2HeadLiyu;
	private AnimView s2HeadZhangtao;

	private AnimView s2NameJiayueting;
	private AnimView s2NameZhouhongyi;
	private AnimView s2NameFengxin;
	private AnimView s2NameTaoran;
	private AnimView s2NameLiyu;
	private AnimView s2NameZhangtao;

	private AnimView s1Core3;

	private AnimView s3Circle;
	private AnimView s3Stu1;
	private AnimView s3Stu2;
	private AnimView s3Stu3;
	private AnimView s3Teacher;
	private AnimView s3Info1;
	private AnimView s3Info2;
	private AnimView s3Border;

	private AnimView s4Circle;
	private AnimView s4HeadFull;
	private AnimView s4HeadPart;
	private AnimView s4EnterApp;
	private AnimView s4InfoDisapper;
	private AnimView s4InfoContact;

	private boolean isMeasured = false;
	private Scroller mScroller;
	private GestureDetector gestureDetector;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (isMeasured) {
			return;
		}
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			measureChild(view, widthMeasureSpec, heightMeasureSpec);
		}
		isMeasured = true;
		gTitle.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.11f), getH(0.08f));
		gTitle.addScaleAnim(getSH(0f), getSH(0.2f), 0.8f, 1f);
		gTitle.addRotateAnim(getSH(0f), getSH(0.2f), 0.f, 360f);
		gTitle.addRotateAnim(getSH(0.2f), getSH(0.6f), 0.f, 0.f);

		// 入场
		dot1.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(1.2f), getH(0.7f));
		dot2.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(1.3f), getH(0.8f));
		// 过渡
		dot1.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.7f), getH(0.3f));
		dot2.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.8f), getH(0.2f));
		// 出场
		dot1.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.3f), getH(-0.2f));
		dot2.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.2f), getH(-0.3f));

		// 入场
		dot3.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(1.2f), getH(0.7f));
		dot4.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(1.3f), getH(0.8f));
		// 过渡
		dot3.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.7f), getH(0.3f));
		dot4.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.8f), getH(0.2f));
		// 出场
		dot3.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.3f), getH(-0.2f));
		dot4.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.2f), getH(-0.3f));

		// 入场
		dot5.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(1.2f), getH(0.7f));
		dot6.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(1.3f), getH(0.8f));
		// 过渡
		dot5.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.7f), getH(0.3f));
		dot6.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.8f), getH(0.2f));
		// 出场
		dot5.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.3f), getH(-0.2f));
		dot6.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.2f), getH(-0.3f));

		s1Paper.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.5f), getH(-0.2f));

		s1Zixun.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f) + dip2px(93), getW(1.5f), getH(0.5f) - dip2px(82.75f),
				getH(0.8f));
		s1Zixun.addRotateAnim(getSH(0.f), getSH(0.2f), 0.f, 880.f);

		s1Shangji.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f) - dip2px(79), getW(-0.5f), getH(0.5f) + dip2px(84.75f),
				getH(-0.2f));
		s1Shangji.addAlphaAnim(getSH(0.f), getSH(0.2f), 1.f, 0.f);
		s1Shangji.addScaleAnim(getSH(0.f), getSH(0.2f), 1.f, 0.2f);

		s2Earth.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.9f), getH(0.5f));
		s2Earth.addScaleAnim(getSH(0.f), getSH(0.2f), 0.3f, 1f);
		// stage 2
		s2Earth.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.5f), getH(-0.2f));
		s2Earth.addScaleAnim(getSH(0.f), getSH(0.2f), 1.0f, 0f);

		// appear
		s2InfoTribehasme.addTrAnim(getSH(0f), getSH(0.2f), getW(-1.5f), getW(0.5f), getH(1.2f), offCY(119));
		s2InfoRenmai.addTrAnim(getSH(0f), getSH(0.2f), getW(1.5f), offCX(-84), getH(-1.2f), offCY(-108));
		// disappear
		s2InfoTribehasme.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(-1.5f), offCY(119), getH(1.2f));
		s2InfoRenmai.addTrAnim(getSH(0f), getSH(0.2f), offCX(-84), getW(1.5f), offCY(-108), getH(-1.2f));

		// scroll to top
		s2HeadJiayueting.addTrAnimBL(getSH(0.0f), getSH(0.2f), getW(1.5f), offCX(17), getH(0.3f), offCY(-57));
		s2HeadZhouhongyi.addTrAnimTL(getSH(0.0f), getSH(0.2f), getW(1.5f), offCX(57.5f), getH(0.5f), offCY(-52.5f));
		s2HeadFengxin.addTrAnimTL(getSH(0.0f), getSH(0.2f), getW(1.5f), offCX(45.5f), getH(1.2f), offCY(59f));
		s2HeadLiyu.addTrAnimBR(getSH(0.0f), getSH(0.2f), getW(-0.5f), offCX(-60f), getH(0.2f), offCY(-2.5f));
		s2HeadTaoran.addTrAnimTR(getSH(0.0f), getSH(0.2f), getW(-0.5f), offCX(-69f), getH(0.8f), offCY(33f));
		s2HeadZhangtao.addTrAnimBR(getSH(0.0f), getSH(0.2f), getW(0.5f), offCX(84f), getH(-0.5f), offCY(-157.5f));

		s2NameJiayueting.addTrAnimBL(getSH(0.0f), getSH(0.2f), getW(1.5f), offCX(58), getH(0.3f), offCY(-72.5f));
		s2NameZhouhongyi.addTrAnimBL(getSH(0.0f), getSH(0.2f), getW(1.5f), offCX(101), getH(0.5f), offCY(12.5f));
		s2NameLiyu.addTrAnimTR(getSH(0.0f), getSH(0.2f), getW(-0.5f), offCX(-82), getH(0.2f), offCY(0));
		s2NameTaoran.addTrAnimTR(getSH(0.0f), getSH(0.2f), getW(-0.5f), offCX(-53.5f), getH(0.8f), offCY(76.5f));
		s2NameFengxin.addTrAnimTL(getSH(0.0f), getSH(0.2f), getW(1.5f), offCX(73f), getH(1.2f), offCY(86f));
		s2NameZhangtao.addTrAnimBR(getSH(0.0f), getSH(0.2f), getW(0.5f), offCX(90f), getH(-0.5f), offCY(-130));

		s2Moon.addTrAnimTL(getSH(0.0f), getSH(0.2f), getW(0.5f), offCX(82.5f), getH(-1.5f), offCY(-176.5f));

		// scroll to bottom
		s2HeadJiayueting.addTrAnimBL(getSH(0.0f), getSH(0.2f), offCX(17), getW(1.5f), offCY(-57), getH(0.3f));
		s2HeadZhouhongyi.addTrAnimTL(getSH(0.0f), getSH(0.2f), offCX(57.5f), getW(1.5f), offCY(-52.5f), getH(0.5f));
		s2HeadFengxin.addTrAnimTL(getSH(0.0f), getSH(0.2f), offCX(45.5f), getW(1.5f), offCY(59f), getH(1.2f));
		s2HeadLiyu.addTrAnimBR(getSH(0.0f), getSH(0.2f), offCX(-60f), getW(-0.5f), offCY(-2.5f), getH(0.2f));
		s2HeadTaoran.addTrAnimTR(getSH(0.0f), getSH(0.2f), offCX(-69f), getW(-0.5f), offCY(33f), getH(0.8f));
		s2HeadZhangtao.addTrAnimBR(getSH(0.0f), getSH(0.2f), offCX(84f), getW(0.5f), offCY(-157.5f), getH(-0.5f));

		s2NameJiayueting.addTrAnimBL(getSH(0.0f), getSH(0.2f), offCX(58), getW(1.5f), offCY(-72.5f), getH(0.3f));
		s2NameZhouhongyi.addTrAnimBL(getSH(0.0f), getSH(0.2f), offCX(101), getW(1.5f), offCY(12.5f), getH(0.5f));
		s2NameLiyu.addTrAnimTR(getSH(0.0f), getSH(0.2f), offCX(-82), getW(-0.5f), offCY(0), getH(0.2f));
		s2NameTaoran.addTrAnimTR(getSH(0.0f), getSH(0.2f), offCX(-53.5f), getW(-0.5f), offCY(76.5f), getH(0.8f));
		s2NameFengxin.addTrAnimTL(getSH(0.0f), getSH(0.2f), offCX(73f), getW(1.5f), offCY(86f), getH(1.2f));
		s2NameZhangtao.addTrAnimBR(getSH(0.0f), getSH(0.2f), offCX(90f), getW(0.5f), offCY(-130), getH(-0.5f));

		s2Moon.addTrAnimTL(getSH(0.0f), getSH(0.2f), offCX(82.5f), getW(0.5f), offCY(-176.5f), getH(-1.5f));

		s3Circle.addTrAnim(getSH(0.0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(1.2f), getH(0.9f));
		s3Border.addTrAnim(getSH(0.0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(1.2f), getH(0.9f));

		s3Circle.addTrAnim(getSH(0f), getSH(0.15f), getW(0.5f), getW(0.5f), getH(0.9f), getH(0.6f));
		s3Circle.addScaleAnim(getSH(0.f), getSH(0.2f), 1f, 8f);
		s3Circle.addAlphaAnim(getSH(0f), getSH(0.1f), 1, 0);
		s3Circle.addAlphaAnim(getSH(0.1f), getSH(0.2f), 0, 0);

		s3Border.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(0.9f), getH(0.5f));
		s3Border.addScaleAnim(getSH(0.f), getSH(0.2f), 1f, 8f);
		s3Border.addAlphaAnim(getSH(0.1f), getSH(0.2f), 1f, 0f);

		s3Teacher.addTrAnim(getSH(0.0f), getSH(0.2f), getW(0.5f), offCX(43.25f), getH(0.9f), offCY(-72.5f));
		s3Teacher.addScaleAnim(getSH(0.0f), getSH(0.2f), 0f, 1f);

		s3Teacher.addTrAnim(getSH(0.0f), getSH(0.2f), offCX(43.25f), getW(0.5f), offCY(-72.5f), getH(-0.2f));
		s3Teacher.addScaleAnim(getSH(0.0f), getSH(0.2f), 1f, 0f);
		// ===3===
		s3Stu1.addTrAnim(getSH(0f), getSH(0.2f), getW(-1.2f), offCX(-45f), getH(-0.5f), offCY(23));
		s3Stu2.addTrAnim(getSH(0f), getSH(0.2f), getW(1.2f), offCX(0), getH(-0.5f), offCY(35));
		s3Stu3.addTrAnim(getSH(0f), getSH(0.2f), getW(-1.2f), offCX(45), getH(1.3f), offCY(23));
		s3Info1.addTrAnimBR(getSH(0f), getSH(0.2f), getW(1.2f), offCX(-45.5f), getH(-0.5f), offCY(-46.5f));
		s3Info2.addTrAnim(getSH(0f), getSH(0.2f), getW(-1.2f), offCX(0f), getH(-0.3f), offCY(106f));

		s3Stu1.addTrAnim(getSH(0f), getSH(0.2f), offCX(-45f), getW(-1.2f), offCY(23), getH(-0.5f));
		s3Stu2.addTrAnim(getSH(0f), getSH(0.2f), offCX(0), getW(1.2f), offCY(35), getH(-0.5f));
		s3Stu3.addTrAnim(getSH(0f), getSH(0.2f), offCX(45), getW(-1.2f), offCY(23), getH(1.3f));
		s3Info1.addTrAnimBR(getSH(0f), getSH(0.2f), offCX(-45.5f), getW(-0.3f), offCY(-46.5f), getH(-0.5f));
		s3Info2.addTrAnim(getSH(0f), getSH(0.2f), offCX(0f), getW(-1.5f), offCY(106f), getH(-1.2f));

		// ===4=====
		s4HeadFull.addTrAnim(getSH(0.0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(1.2f), getH(0.9f));
		s4HeadFull.addScaleAnim(getSH(0.0f), getSH(0.2f), 0f, 0.20f);
		s4Circle.addTrAnim(getSH(0.0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(1.2f), getH(0.9f));
		/**
		 * 由于缩放旋转在一起，故要把变换点设在view的中点，计算偏移如下 offy = -h/2*sin(16)^2, offx =
		 * -h/2*sin(16)cos(16)
		 */
		s4HeadFull.addTrAnim(getSH(0f), getSH(0.2f), getW(0.5f), offCX(-29.14f), getH(0.9f), offCY(-8.35f));
		s4HeadFull.addScaleAnim(getSH(0.0f), getSH(0.2f), 0.20f, 1f);

		s4HeadFull.addRotateAnim(getSH(0.15f), getSH(0.2f), 16, 0);
		s4HeadFull.addRotateAnim(getSH(0.0f), getSH(0.15f), 16, 16);

		s4HeadPart.addTrAnim(getSH(0.0f), getSH(0.2f), offCX(40), offCX(45), getH(0.9f), offCY(-24));
		s4HeadPart.addAlphaAnim(getSH(0.15f), getSH(0.2f), 0, 1);
		s4HeadPart.addAlphaAnim(getSH(0.0f), getSH(0.15f), 0, 0);

		s4EnterApp.addTrAnim(getSH(0.0f), getSH(0.2f), getW(0.5f), getW(0.5f), getH(1.2f), getH(0.9f));

		s4InfoDisapper.addTrAnim(getSH(0.0f), getSH(0.2f), getW(-0.5f), offCX(0f), getH(1.2f), offCY(125f));
		s4InfoContact.addTrAnimBL(getSH(0.0f), getSH(0.2f), getW(1.5f), offCX(45f), getH(-1.2f), offCY(-110.5f));

		s4Circle.addTrAnim(getSH(0f), getSH(0.15f), getW(0.5f), getW(0.5f), getH(0.9f), getH(0.6f));
		s4Circle.addScaleAnim(getSH(0.f), getSH(0.2f), 1f, 8f);
		s4Circle.addAlphaAnim(getSH(0f), getSH(0.1f), 1, 0);
		s4Circle.addAlphaAnim(getSH(0.1f), getSH(0.2f), 0, 0);

	}

	private void animPeople() {
		s2HeadJiayueting.layoutBL(((int) getW(0.5f) + dip2px(17f)), ((int) getH(0.5f) - dip2px(57f)));
		s2HeadZhouhongyi.layoutTL(offCX(57.5f), offCY(-52.5f));
		s2HeadFengxin.layoutTL(offCX(45.5f), offCY(59f));
		s2HeadLiyu.layoutBR(offCX(-60f), offCY(-2.5f));
		s2HeadTaoran.layoutTR(offCX(-69f), offCY(33f));
		s2HeadZhangtao.layoutBR(offCX(84f), offCY(-157.5f));

		s2NameJiayueting.layoutBL(offCX(58), offCY(-72.5f));
		s2NameZhouhongyi.layoutBL(offCX(101), offCY(12.5f));
		s2NameLiyu.layoutTR(offCX(-82), offCY(0));
		s2NameTaoran.layoutTR(offCX(-53.5f), offCY(76.5f));
		s2NameFengxin.layoutTL(offCX(73f), offCY(86f));
		s2NameZhangtao.layoutBR(offCX(90f), offCY(-130));

		s2HeadJiayueting.setAnimation(creatScaleAnimation(0, 1.0f, 0));
		s2HeadZhouhongyi.setAnimation(creatScaleAnimation(0, 1.0f, 100));
		s2HeadFengxin.setAnimation(creatScaleAnimation(0, 0, 200));
		s2HeadLiyu.setAnimation(creatScaleAnimation(1.0f, 1.0f, 300));
		s2HeadTaoran.setAnimation(creatScaleAnimation(1.0f, 0, 400));
		s2HeadZhangtao.setAnimation(creatScaleAnimation(1.0f, 1.0f, 400));

		s2NameJiayueting.setAnimation(creatTranslateAnimationX(1.2f, 200));
		s2NameZhouhongyi.setAnimation(creatTranslateAnimationX(1.2f, 300));
		s2NameLiyu.setAnimation(creatTranslateAnimationX(-1.2f, 400));
		s2NameTaoran.setAnimation(creatTranslateAnimationX(-1.2f, 500));
		s2NameFengxin.setAnimation(creatTranslateAnimationX(1.2f, 600));
		s2NameZhangtao.setAnimation(creatTranslateAnimationX(1.2f, 500));

		/**
		 * 旋转中心的距离要从view的左上角算起
		 */
		s2Moon.layoutTL(offCX(82.5f), offCY(-176.5f));
		s2Moon.setAnimation(creatMoonRotageAnimation(1080, 0, dip2px(-82.5f), dip2px(176.5f)));
	}

	private void animStage3() {
		s3Stu1.layoutCR(offCX(-45f), offCY(23));
		s3Stu3.layoutCR(offCX(45), offCY(23));
		s3Stu2.layoutCR(offCX(0), offCY(35));
		s3Info1.layoutBR(offCX(-45.5f), offCY(-46.5f));
		s3Info2.layoutCR(offCX(0f), offCY(106f));

		s3Stu1.setAnimation(creatScaleAnimation(0f, 1f, 300));
		s3Stu2.setAnimation(creatScaleAnimation(0.5f, 1f, 0));
		s3Stu3.setAnimation(creatScaleAnimation(1f, 1f, 300));
		s3Info1.setAnimation(creatTranslateAnimationX(-1.2f, 200));
		s3Info2.setAnimation(creatTranslateAnimationY(1.2f, 300));
	}

	@SuppressLint("NewApi")
	private void animStage4() {
		s4HeadFull.setPivotX(s4HeadFull.getMeasuredWidth() / 2);
		s4HeadFull.setPivotY(s4HeadFull.getMeasuredHeight() / 2);
		s4HeadFull.setAnimation(creatRotageAnimation(-16, 0));
		s4HeadPart.layoutCR(offCX(45), offCY(-24));
		s4HeadPart.setAnimation(creatAlphaAnimation(0, 1, 300));

		s4InfoDisapper.layoutCR(offCX(0f), offCY(125f));
		s4InfoDisapper.setAnimation(creatTranslateAnimationY(1.2f, 300));

		s4InfoContact.layoutBL(offCX(45f), offCY(-110.5f));
		s4InfoContact.setAnimation(creatTranslateAnimationX(-1.2f, 200));

		s4EnterApp.layoutCR(offCX(0f), (int) getH(0.9f));
		s4EnterApp.setAnimation(creatTranslateAnimationY(1.2f, 400));
	}

	private int offCX(float off) {
		return (int) getW(0.5f) + dip2px(off);
	}

	private int offCY(float off) {
		return (int) getH(0.5f) + dip2px(off);
	}

	private AlphaAnimation creatAlphaAnimation(float x, float y, int offset) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(x, y);
		alphaAnimation.setStartOffset(offset);
		alphaAnimation.setDuration(500);
		return alphaAnimation;
	}

	private ScaleAnimation creatScaleAnimation(float x, float y, int offset) {
		ScaleAnimation scaleAnimation = creatScaleAnimation(x, y);
		scaleAnimation.setStartOffset(offset);
		return scaleAnimation;
	}

	private ScaleAnimation creatScaleAnimation(float x, float y) {
		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, x,
				ScaleAnimation.RELATIVE_TO_SELF, y);
		scaleAnimation.setDuration(500);
		scaleAnimation.start();
		return scaleAnimation;
	}

	private TranslateAnimation creatTranslateAnimationX(float xf, int offset) {
		TranslateAnimation animation = new TranslateAnimation(ScaleAnimation.RELATIVE_TO_PARENT, xf,
				ScaleAnimation.RELATIVE_TO_SELF, 0f, ScaleAnimation.RELATIVE_TO_SELF, 0f,
				ScaleAnimation.RELATIVE_TO_SELF, 0f);
		animation.setDuration(400);
		animation.setStartOffset(offset);
		return animation;
	}

	private TranslateAnimation creatTranslateAnimationY(float yf, int offset) {
		TranslateAnimation animation = new TranslateAnimation(ScaleAnimation.RELATIVE_TO_SELF, 0f,
				ScaleAnimation.RELATIVE_TO_SELF, 0f, ScaleAnimation.RELATIVE_TO_PARENT, yf,
				ScaleAnimation.RELATIVE_TO_SELF, 0f);
		animation.setDuration(400);
		animation.setStartOffset(offset);
		return animation;
	}

	private RotateAnimation creatRotageAnimation(int degree, int offset) {
		RotateAnimation rotateAnimation = new RotateAnimation(0, degree, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setStartOffset(offset);
		rotateAnimation.setFillAfter(true);
		rotateAnimation.setDuration(500);
		return rotateAnimation;
	}

	private RotateAnimation creatMoonRotageAnimation(int degree, int offset, float x, float y) {
		RotateAnimation rotateAnimation = new RotateAnimation(0, degree, RotateAnimation.ABSOLUTE, x,
				RotateAnimation.ABSOLUTE, y);
		rotateAnimation.setStartOffset(offset);
		rotateAnimation.setFillAfter(true);
		rotateAnimation.setDuration(1000);
		return rotateAnimation;
	}

	public int dip2px(float dpValue) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	private float getW(float percent) {
		return getMeasuredWidth() * percent;
	}

	private float getH(float percent) {
		return getMeasuredHeight() * percent;
	}

	private float getSH(float percent) {
		return getMeasuredHeight() * percent * 10f;
	}

	private void anim(View view) {
		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1);
		scaleAnimation.setDuration(2000);
		view.setAnimation(scaleAnimation);
		scaleAnimation.setStartTime(1000);
		scaleAnimation.start();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		if (yTotal < 0) {
			yTotal = 0;
		}
		if (yTotal > getSH(0.2f) * 3) {
			yTotal = getSH(0.2f) * 3;
		}
		int stage = (int) (yTotal / getSH(0.2f));
		switch (stage) {
		case 0:
			dot1.layout(yTotal, 2);
			dot2.layout(yTotal, 2);
			dot3.layout(yTotal, 1);
			dot4.layout(yTotal, 1);
			dot5.layout(yTotal, 0);
			dot6.layout(yTotal, 0);

			s1Paper.layout(yTotal);
			s1Zixun.layout(yTotal);
			s1Shangji.layout(yTotal);

			s2Earth.layout(yTotal, 1, 2);

			s2InfoTribehasme.layout(yTotal, 0);
			s2InfoRenmai.layout(yTotal, 0);

			s3Circle.layout(yTotal, 0);
			s3Border.layout(yTotal, 0);

			if (!isAnim && (-yTotal + getSH(0.2f)) < getH(0.02f)) {
				animPeople();
				isAnim = true;
			}
			if (isAnim && (yTotal < getH(0.02f))) {
				isAnim = false;
			}

			if (isAnim) {
				s2HeadJiayueting.layout(yTotal, 0);
				s2HeadZhouhongyi.layout(yTotal, 0);
				s2HeadFengxin.layout(yTotal, 0);
				s2HeadLiyu.layout(yTotal, 0);
				s2HeadTaoran.layout(yTotal, 0);
				s2HeadZhangtao.layout(yTotal, 0);

				s2NameJiayueting.layout(yTotal, 0);
				s2NameZhouhongyi.layout(yTotal, 0);
				s2NameLiyu.layout(yTotal, 0);
				s2NameTaoran.layout(yTotal, 0);
				s2NameFengxin.layout(yTotal, 0);
				s2NameZhangtao.layout(yTotal, 0);

				s2Moon.layout(yTotal, 0);
			}
			break;
		case 1:
			float yTotalTemp2 = yTotal - getSH(0.2f);

			s2HeadJiayueting.layout(yTotalTemp2, 1);
			s2HeadZhouhongyi.layout(yTotalTemp2, 1);
			s2HeadFengxin.layout(yTotalTemp2, 1);
			s2HeadLiyu.layout(yTotalTemp2, 1);
			s2HeadTaoran.layout(yTotalTemp2, 1);
			s2HeadZhangtao.layout(yTotalTemp2, 1);

			s2NameJiayueting.layout(yTotalTemp2, 1);
			s2NameZhouhongyi.layout(yTotalTemp2, 1);
			s2NameLiyu.layout(yTotalTemp2, 1);
			s2NameTaoran.layout(yTotalTemp2, 1);
			s2NameFengxin.layout(yTotalTemp2, 1);
			s2NameZhangtao.layout(yTotalTemp2, 1);
			s2Moon.layout(yTotalTemp2, 1);

			dot3.layout(yTotalTemp2, 2);
			dot4.layout(yTotalTemp2, 2);
			dot5.layout(yTotalTemp2, 1);
			dot6.layout(yTotalTemp2, 1);
			dot1.layout(yTotalTemp2, 0);
			dot2.layout(yTotalTemp2, 0);

			s2Earth.layout(yTotalTemp2, 3, 4);

			s2InfoTribehasme.layout(yTotalTemp2, 1);
			s2InfoRenmai.layout(yTotalTemp2, 1);

			s3Border.layout(yTotalTemp2, 2, 4);
			s3Circle.layout(yTotalTemp2, 2, 5);
			s3Teacher.layout(yTotalTemp2, 1, 2);

			if (!isAnim3 && (-yTotalTemp2 + getSH(0.2f)) < getH(0.02f)) {
				animStage3();
				isAnim3 = true;
			}
			if (isAnim3 && (yTotalTemp2 < getH(0.02f))) {
				isAnim3 = false;
			}

			if (isAnim3) {
				s3Stu1.layout(yTotalTemp2, 0);
				s3Stu2.layout(yTotalTemp2, 0);
				s3Stu3.layout(yTotalTemp2, 0);
				s3Info1.layout(yTotalTemp2, 0);
				s3Info2.layout(yTotalTemp2, 0);
			}
			s4Circle.layout(yTotalTemp2, 0);
			s4HeadFull.layout(yTotalTemp2, 1, 2);
			break;
		case 2:
			float yTotalTemp3 = yTotal - 2 * getSH(0.2f);
			s3Stu1.layout(yTotalTemp3, 1);
			s3Stu2.layout(yTotalTemp3, 1);
			s3Stu3.layout(yTotalTemp3, 1);
			s3Info1.layout(yTotalTemp3, 1);
			s3Info2.layout(yTotalTemp3, 1);
			s3Teacher.layout(yTotalTemp3, 3, 4);

			dot5.layout(yTotalTemp3, 2);
			dot6.layout(yTotalTemp3, 2);
			dot1.layout(yTotalTemp3, 1);
			dot2.layout(yTotalTemp3, 1);
			dot3.layout(yTotalTemp3, 0);
			dot4.layout(yTotalTemp3, 0);
			s4Circle.layout(yTotalTemp3, 2, 5);
			s4HeadFull.layout(yTotalTemp3, 3, 4);
			if (!isAnim4 && (-yTotalTemp3 + getSH(0.2f)) < getH(0.02f)) {
				animStage4();
				isAnim4 = true;
			}
			if (isAnim4 && (yTotalTemp3 < getH(0.02f))) {
				isAnim4 = false;
			}
			if (isAnim4) {
				s4HeadFull.layout(yTotalTemp3, 3, 6);
				s4HeadPart.layout(yTotalTemp3);
				s4InfoDisapper.layout(yTotalTemp3);
				s4InfoContact.layout(yTotalTemp3);
				s4EnterApp.layout(yTotalTemp3);
			}
			break;

		default:
			break;
		}

		gTitle.layout(yTotal);
	}

	private boolean isAnim = false;
	private boolean isAnim3 = false;
	private boolean isAnim4 = false;
}

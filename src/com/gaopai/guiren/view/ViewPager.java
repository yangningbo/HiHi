 package com.gaopai.guiren.view;

 
import java.util.ArrayList;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.gaopai.guiren.adapter.PagerAdapter;

 
 public class ViewPager extends ViewGroup
 {
   private ArrayList<ItemInfo> mItems = new ArrayList<ItemInfo>();
   private com.gaopai.guiren.adapter.PagerAdapter mAdapter;
   private int mCurItem;
   private int mRestoredCurItem = -1;
   private Parcelable mRestoredAdapterState = null;
   private ClassLoader mRestoredClassLoader = null;
   private Scroller mScroller;
   private PagerAdapter.DataSetObserver mObserver;
   private int mChildWidthMeasureSpec;
   private int mChildHeightMeasureSpec;
   private boolean mInLayout;
   private boolean mScrollingCacheEnabled;
   private boolean mPopulatePending;
   private boolean mScrolling;
   private boolean mIsBeingDragged;
   private boolean mIsUnableToDrag;
   private int mTouchSlop;
   private float mInitialMotionX;
   private float mLastMotionX;
   private float mLastMotionY;
   private int mActivePointerId = -1;
   private VelocityTracker mVelocityTracker;
   private int mMinimumVelocity;
   private int mMaximumVelocity;
   private OnPageChangeListener mOnPageChangeListener;
   public static final int SCROLL_STATE_IDLE = 0;
   public static final int SCROLL_STATE_DRAGGING = 1;
   public static final int SCROLL_STATE_SETTLING = 2;
   private int mScrollState = 0;
   private int mduration = 500;
   private float mRelationX;
   
//   private static boolean left = false;
//   private static boolean right = false;
//   private static boolean isScrolling = false;
//   private static int lastValue = -1;
 
   public ViewPager(Context context)
   {
     super(context);
     initViewPager();
   }
 
   public ViewPager(Context context, AttributeSet attrs) {
     super(context, attrs);
     initViewPager();
   }
 
   void initViewPager() {
     setWillNotDraw(false);
     this.mScroller = new Scroller(getContext());
     ViewConfiguration configuration = ViewConfiguration.get(getContext());
     this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
     this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
     this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
   }
 
   private void setScrollState(int newState) {
     if (this.mScrollState == newState) {
       return;
     }
 
     this.mScrollState = newState;
     if (this.mOnPageChangeListener != null)
       this.mOnPageChangeListener.onPageScrollStateChanged(newState);
   }
 	public void setDuration(int time){
				this.mduration =time;
			}
   public void setAdapter(PagerAdapter adapter)
   {
     if (this.mAdapter != null) {
       this.mAdapter.setDataSetObserver(null);
     }
 
     this.mAdapter = adapter;
 
     if (this.mAdapter != null) {
       if (this.mObserver == null) {
         this.mObserver = new DataSetObserver();
       }
       this.mAdapter.setDataSetObserver(this.mObserver);
       this.mPopulatePending = false;
       if (this.mRestoredCurItem >= 0) {
         this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
         setCurrentItemInternal(this.mRestoredCurItem, false, true);
         this.mRestoredCurItem = -1;
         this.mRestoredAdapterState = null;
         this.mRestoredClassLoader = null;
       } else {
         populate();
       }
     }
   }
 
   public PagerAdapter getAdapter() {
     return this.mAdapter;
   }
 
   public void setCurrentItem(int item) {
     this.mPopulatePending = false;
     setCurrentItemInternal(item, true, false);
   }
 
   void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
     if ((this.mAdapter == null) || (this.mAdapter.getCount() <= 0)) {
       setScrollingCacheEnabled(false);
       return;
     }
     if ((!always) && (this.mCurItem == item) && (this.mItems.size() != 0)) {
       setScrollingCacheEnabled(false);
       return;
     }
     if (item < 0)
       item = 0;
     else if (item >= this.mAdapter.getCount()) {
       item = this.mAdapter.getCount() - 1;
     }
     if ((item > this.mCurItem + 1) || (item < this.mCurItem - 1))
     {
       for (int i = 0; i < this.mItems.size(); i++) {
         this.mItems.get(i).scrolling = true;
       }
     }
     boolean dispatchSelected = this.mCurItem != item;
     this.mCurItem = item;
     populate();
     if (smoothScroll) {
       smoothScrollTo(getWidth() * item, 0);
       if ((dispatchSelected) && (this.mOnPageChangeListener != null))
         this.mOnPageChangeListener.onPageSelected(item);
     }
     else {
       if ((dispatchSelected) && (this.mOnPageChangeListener != null)) {
         this.mOnPageChangeListener.onPageSelected(item);
       }
       completeScroll();
       scrollTo(getWidth() * item, 0);
     }
   }
 
   public void setOnPageChangeListener(OnPageChangeListener listener) {
     this.mOnPageChangeListener = listener;
   }
 
   void smoothScrollTo(int x, int y)
   {
     if (getChildCount() == 0)
     {
       setScrollingCacheEnabled(false);
       return;
     }
     int sx = getScrollX();
     int sy = getScrollY();
     int dx = x - sx;
     int dy = y - sy;
     if ((dx == 0) && (dy == 0)) {
       completeScroll();
       return;
     }
 
     setScrollingCacheEnabled(true);
     this.mScrolling = true;
     setScrollState(2);
     this.mScroller.startScroll(sx, sy, dx, dy,mduration);
     invalidate();
   }
 
   void addNewItem(int position, int index) {
     ItemInfo ii = new ItemInfo();
     ii.position = position;
     ii.object = this.mAdapter.instantiateItem(this, position);
     if (index < 0)
       this.mItems.add(ii);
     else
       this.mItems.add(index, ii);
   }
 
   void dataSetChanged()
   {
     boolean needPopulate = (this.mItems.isEmpty()) && (this.mAdapter.getCount() > 0);
     int newCurrItem = -1;
 
     for (int i = 0; i < this.mItems.size(); i++) {
       ItemInfo ii = this.mItems.get(i);
       int newPos = this.mAdapter.getItemPosition(ii.object);
 
       if (newPos == -1)
       {
         continue;
       }
       if (newPos == -2) {
         this.mItems.remove(i);
         i--;
         this.mAdapter.destroyItem(this, ii.position, ii.object);
         needPopulate = true;
 
         if (this.mCurItem != ii.position)
           continue;
         newCurrItem = Math.max(0, Math.min(this.mCurItem, this.mAdapter.getCount() - 1));
       }
       else if (ii.position != newPos) {
         if (ii.position == this.mCurItem)
         {
           newCurrItem = newPos;
         }
 
         ii.position = newPos;
         needPopulate = true;
       }
     }
 
     if (newCurrItem >= 0)
     {
       setCurrentItemInternal(newCurrItem, false, true);
       needPopulate = true;
     }
     if (needPopulate) {
       populate();
       requestLayout();
     }
   }
 
   void populate() {
     if (this.mAdapter == null) {
       return;
     }
 
     if (this.mPopulatePending)
     {
       return;
     }
 
     if (getWindowToken() == null) {
       return;
     }
 
     this.mAdapter.startUpdate(this);
 
     int startPos = this.mCurItem > 0 ? this.mCurItem - 1 : this.mCurItem;
     int N = this.mAdapter.getCount();
     int endPos = this.mCurItem < N - 1 ? this.mCurItem + 1 : N - 1;
 
     int lastPos = -1;
     for (int i = 0; i < this.mItems.size(); i++) {
       ItemInfo ii = this.mItems.get(i);
       if (((ii.position < startPos) || (ii.position > endPos)) && (!ii.scrolling))
       {
         this.mItems.remove(i);
         i--;
         if(ii.position < this.mAdapter.getCount()){
        	 this.mAdapter.destroyItem(this, ii.position, ii.object);
         }
       } else if ((lastPos < endPos) && (ii.position > startPos))
       {
         lastPos++;
         if (lastPos < startPos) {
           lastPos = startPos;
         }
         while ((lastPos <= endPos) && (lastPos < ii.position))
         {
           addNewItem(lastPos, i);
           lastPos++;
           i++;
         }
       }
       lastPos = ii.position;
     }
 
     lastPos = this.mItems.size() > 0 ? this.mItems.get(this.mItems.size() - 1).position : -1;
     if (lastPos < endPos) {
       lastPos++;
       lastPos = lastPos > startPos ? lastPos : startPos;
       while (lastPos <= endPos)
       {
         addNewItem(lastPos, -1);
         lastPos++;
       }
 
     }
 
     this.mAdapter.finishUpdate(this);
   }
 
   @Override
public Parcelable onSaveInstanceState()
   {
     Parcelable superState = super.onSaveInstanceState();
     SavedState ss = new SavedState(superState);
     ss.position = this.mCurItem;
     ss.adapterState = this.mAdapter.saveState();
     return ss;
   }
 
   @Override
public void onRestoreInstanceState(Parcelable state)
   {
     if (!(state instanceof SavedState)) {
       super.onRestoreInstanceState(state);
       return;
     }
 
     SavedState ss = (SavedState)state;
     super.onRestoreInstanceState(ss.getSuperState());
 
     if (this.mAdapter != null) {
       this.mAdapter.restoreState(ss.adapterState, ss.loader);
       setCurrentItemInternal(ss.position, false, true);
     } else {
       this.mRestoredCurItem = ss.position;
       this.mRestoredAdapterState = ss.adapterState;
       this.mRestoredClassLoader = ss.loader;
     }
   }
 
   @Override
public void addView(View child, int index, ViewGroup.LayoutParams params)
   {
     if (this.mInLayout) {
       addViewInLayout(child, index, params);
       child.measure(this.mChildWidthMeasureSpec, this.mChildHeightMeasureSpec);
     } else {
       super.addView(child, index, params);
     }
   }
 
   ItemInfo infoForChild(View child)
   {
     for (int i = 0; i < this.mItems.size(); i++) {
       ItemInfo ii = this.mItems.get(i);
       if (this.mAdapter.isViewFromObject(child, ii.object)) {
         return ii;
       }
     }
     return null;
   }
 
   @Override
protected void onAttachedToWindow()
   {
     super.onAttachedToWindow();
     if (this.mAdapter != null)
       populate();
   }
 
   @Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
   {
     setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
 
     this.mChildWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), 1073741824);
 
     this.mChildHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), 1073741824);
 
     this.mInLayout = true;
     populate();
     this.mInLayout = false;
 
     int size = getChildCount();
     for (int i = 0; i < size; i++) {
       View child = getChildAt(i);
       if (child.getVisibility() == 8) {
         continue;
       }
       child.measure(this.mChildWidthMeasureSpec, this.mChildHeightMeasureSpec);
     }
   }
 
   @Override
protected void onSizeChanged(int w, int h, int oldw, int oldh)
   {
     super.onSizeChanged(w, h, oldw, oldh);
 
     int scrollPos = this.mCurItem * w;
     if (scrollPos != getScrollX()) {
       completeScroll();
       scrollTo(scrollPos, getScrollY());
     }
   }
 
   @Override
protected void onLayout(boolean changed, int l, int t, int r, int b)
   {
     this.mInLayout = true;
     populate();
     this.mInLayout = false;
 
     int count = getChildCount();
     int width = r - l;
 
     for (int i = 0; i < count; i++) {
       View child = getChildAt(i);
       ItemInfo ii;
       if ((child.getVisibility() != 8) && ((ii = infoForChild(child)) != null)) {
         int loff = width * ii.position;
         int childLeft = getPaddingLeft() + loff;
         int childTop = getPaddingTop();
 
         child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
       }
     }
   }
 
   @Override
public void computeScroll()
   {
     if ((!this.mScroller.isFinished()) && 
       (this.mScroller.computeScrollOffset()))
     {
       int oldX = getScrollX();
       int oldY = getScrollY();
       int x = this.mScroller.getCurrX();
       int y = this.mScroller.getCurrY();
 
       if ((oldX != x) || (oldY != y)) {
         scrollTo(x, y);
       }
 
       if (this.mOnPageChangeListener != null) {
         int width = getWidth();
         int position = x / width;
         int offsetPixels = x % width;
         float offset = offsetPixels / width;
         this.mOnPageChangeListener.onPageScrolled(position, offset, offsetPixels);
       }
 
       invalidate();
       return;
     }
 
     completeScroll();
   }
 
   private void completeScroll()
   {
     boolean needPopulate;
     if ((needPopulate = this.mScrolling))
     {
       setScrollingCacheEnabled(false);
       this.mScroller.abortAnimation();
       int oldX = getScrollX();
       int oldY = getScrollY();
       int x = this.mScroller.getCurrX();
       int y = this.mScroller.getCurrY();
       if ((oldX != x) || (oldY != y)) {
         scrollTo(x, y);
       }
       setScrollState(0);
     }
     this.mPopulatePending = false;
     this.mScrolling = false;
     for (int i = 0; i < this.mItems.size(); i++) {
       ItemInfo ii = this.mItems.get(i);
       if (ii.scrolling) {
         needPopulate = true;
         ii.scrolling = false;
       }
     }
     if (needPopulate)
       populate();
   }
 
   @Override
public boolean onInterceptTouchEvent(MotionEvent ev)
   {
     int action = ev.getAction() & 0xFF;
 
     if ((action == 3) || (action == 1))
     {
       this.mIsBeingDragged = false;
       this.mIsUnableToDrag = false;
       this.mActivePointerId = -1;
       return false;
     }
 
     if (action != 0) {
       if (this.mIsBeingDragged)
       {
         return true;
       }
       if (this.mIsUnableToDrag)
       {
         return false;
       }
     }
 
     switch (action)
     {
     case 2:
       int activePointerId = this.mActivePointerId;
       if (activePointerId == -1)
       {
         break;
       }
 
       int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
       float x = MotionEventCompat.getX(ev, pointerIndex);
       float dx = x - this.mLastMotionX;
       float xDiff = Math.abs(dx);
       float y = MotionEventCompat.getY(ev, pointerIndex);
       float yDiff = Math.abs(y - this.mLastMotionY);
 
       if ((xDiff > this.mTouchSlop) && (xDiff > yDiff))
       {
         this.mIsBeingDragged = true;
         setScrollState(1);
         this.mLastMotionX = x;
         setScrollingCacheEnabled(true);
       } else {
         if (yDiff <= this.mTouchSlop)
         {
           break;
         }
 
         this.mIsUnableToDrag = true; } break;
     case 0:
       this.mLastMotionX = (this.mInitialMotionX = ev.getX());
       this.mLastMotionY = ev.getY();
       this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
 
       if (this.mScrollState == 2)
       {
         this.mIsBeingDragged = true;
         this.mIsUnableToDrag = false;
         setScrollState(1);
       } else {
         completeScroll();
         this.mIsBeingDragged = false;
         this.mIsUnableToDrag = false;
       }
 
       break;
     case 6:
       onSecondaryPointerUp(ev);
     }
 
     return this.mIsBeingDragged;
   }
 
   @Override
public boolean onTouchEvent(MotionEvent ev)
   {
     if ((ev.getAction() == 0) && (ev.getEdgeFlags() != 0))
     {
       return false;
     }
 
     if ((this.mAdapter == null) || (this.mAdapter.getCount() == 0))
     {
       return false;
     }
 
     if (this.mVelocityTracker == null) {
       this.mVelocityTracker = VelocityTracker.obtain();
     }
     this.mVelocityTracker.addMovement(ev);
 
     int action = ev.getAction();
 
     switch (action & 0xFF)
     {
     case 0:
       completeScroll();
       
       this.mLastMotionX = (this.mInitialMotionX = ev.getX());
       this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
       break;
     case 2:
       if (!this.mIsBeingDragged) {
         int pointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
         float x = MotionEventCompat.getX(ev, pointerIndex);
         mRelationX = x - this.mLastMotionX;
         float xDiff = Math.abs(x - this.mLastMotionX);
         float y = MotionEventCompat.getY(ev, pointerIndex);
         float yDiff = Math.abs(y - this.mLastMotionY);
 
         if ((xDiff > this.mTouchSlop) && (xDiff > yDiff))
         {
           this.mIsBeingDragged = true;
           this.mLastMotionX = x;
           setScrollState(1);
           setScrollingCacheEnabled(true);
         }
       }
       if (!this.mIsBeingDragged)
         break;
       int activePointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
 
       float x = MotionEventCompat.getX(ev, activePointerIndex);
       float deltaX = this.mLastMotionX - x;
       mRelationX = deltaX;
       this.mLastMotionX = x;
       float scrollX = getScrollX() + deltaX;
       int width = getWidth();
 
//       float leftBound = Math.max(0, (this.mCurItem - 1) * width);
//       float rightBound = Math.min(this.mCurItem + 1, this.mAdapter.getCount() - 1) * width;
 
       /*if (scrollX < leftBound)
         scrollX = leftBound;
       else if (scrollX > rightBound) {
         scrollX = rightBound;
       }*/
 
       this.mLastMotionX += scrollX - (int)scrollX;
       scrollTo((int)scrollX, getScrollY());
       if (this.mOnPageChangeListener != null) {
         int position = (int)scrollX / width;
         int positionOffsetPixels = (int)scrollX % width;
         float positionOffset = positionOffsetPixels / width;
         this.mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
       }
       break;
     case 1:
       if (!this.mIsBeingDragged) break;
       VelocityTracker velocityTracker = this.mVelocityTracker;
       velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
       int initialVelocity = (int)VelocityTrackerCompat.getYVelocity(velocityTracker, this.mActivePointerId);
 
       this.mPopulatePending = true;
       if ((Math.abs(initialVelocity) > this.mMinimumVelocity) || (Math.abs(this.mInitialMotionX - this.mLastMotionX) >= getWidth() / 3))
       {
         if (this.mLastMotionX > this.mInitialMotionX)
           setCurrentItemInternal(this.mCurItem - 1, true, true);
         else
           setCurrentItemInternal(this.mCurItem + 1, true, true);
       }
       else {
         setCurrentItemInternal(this.mCurItem, true, true);
       }
 
       this.mActivePointerId = -1;
       endDrag();
       break;
     case 3:
       if (!this.mIsBeingDragged) break;
       setCurrentItemInternal(this.mCurItem, true, true);
       this.mActivePointerId = -1;
       endDrag(); 
       break;
     case 5:
       int index = MotionEventCompat.getActionIndex(ev);
       float x2 = MotionEventCompat.getX(ev, index);
       this.mLastMotionX = x2;
       this.mActivePointerId = MotionEventCompat.getPointerId(ev, index);
       break;
     case 6:
       onSecondaryPointerUp(ev);
       this.mLastMotionX = MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, this.mActivePointerId));
       break;
     case 4:
     }
 
     return true;
   }
 
   private void onSecondaryPointerUp(MotionEvent ev) {
     int pointerIndex = MotionEventCompat.getActionIndex(ev);
     int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
     if (pointerId == this.mActivePointerId)
     {
       int newPointerIndex = pointerIndex == 0 ? 1 : 0;
       this.mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
       this.mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
       if (this.mVelocityTracker != null)
         this.mVelocityTracker.clear();
     }
   }
 
   private void endDrag()
   {
     this.mIsBeingDragged = false;
     this.mIsUnableToDrag = false;
 
     if (this.mVelocityTracker != null) {
       this.mVelocityTracker.recycle();
       this.mVelocityTracker = null;
     }
   }
 
   private void setScrollingCacheEnabled(boolean enabled) {
     if (this.mScrollingCacheEnabled != enabled)
       this.mScrollingCacheEnabled = enabled;
   }
 
   private class DataSetObserver
     implements PagerAdapter.DataSetObserver
   {
     private DataSetObserver()
     {
     }
 
     @Override
	public void onDataSetChanged()
     {
       ViewPager.this.dataSetChanged();
     }
   }
 
   public static class SavedState extends View.BaseSavedState
   {
     int position;
     Parcelable adapterState;
     ClassLoader loader;
public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>()
     {
       @Override
	public ViewPager.SavedState createFromParcel(Parcel in, ClassLoader loader)
       {
         return new ViewPager.SavedState(in, loader);
       }
 
       @Override
	public ViewPager.SavedState[] newArray(int size) {
         return new ViewPager.SavedState[size];
       }
     });
 
     public SavedState(Parcelable superState)
     {
       super(superState);
     }
 
     @Override
	public void writeToParcel(Parcel out, int flags)
     {
       super.writeToParcel(out, flags);
       out.writeInt(this.position);
       out.writeParcelable(this.adapterState, flags);
     }
 
     @Override
	public String toString()
     {
       return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
     }
 
     SavedState(Parcel in, ClassLoader loader)
     {
       super(in);
       if (loader == null) {
         loader = getClass().getClassLoader();
       }
       this.position = in.readInt();
       this.adapterState = in.readParcelable(loader);
       this.loader = loader;
     }
   }
 
   public static class SimpleOnPageChangeListener
     implements ViewPager.OnPageChangeListener
   {
     @Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
     {
//    	 if (isScrolling) {
//    		 if (lastValue > positionOffsetPixels) {
//	    		 right = true;
//	    		 left = false;
//    		 } else if (lastValue < positionOffsetPixels) {
//	    		 right = false;
//	    		 left = true;
//    		 } else if (lastValue == positionOffsetPixels) {
//    			 right = left = false;
//    		 }
//    	 }
//    	 lastValue = positionOffsetPixels;
     }
 
     @Override
	public void onPageSelected(int position)
     {
     }
 
     @Override
	public void onPageScrollStateChanged(int state)
     {
//    	 if (state == 1) {
//    		 isScrolling = true;
//    	 } else {
//    		 isScrolling = false;
//    	 }
//    	 right = left = false;
     }
   }
 
//   public boolean getMoveRight(){
//	   return right;
//   }
//   
//   public boolean getMoveLeft(){
//	   return left;
//   }
   
   public static abstract interface OnPageChangeListener
   {
     public abstract void onPageScrolled(int paramInt1, float paramFloat, int paramInt2);
 
     public abstract void onPageSelected(int paramInt);
 
     public abstract void onPageScrollStateChanged(int paramInt);
   }
 
   static class ItemInfo
   {
     Object object;
     int position;
     boolean scrolling;
   }
   
   public float getRelationX(){
	   return mRelationX;
   }
 }
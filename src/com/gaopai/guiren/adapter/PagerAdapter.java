 package com.gaopai.guiren.adapter;
 
 import android.os.Parcelable;
import android.view.View;
 
 public abstract class PagerAdapter
 {
   private DataSetObserver mObserver;
   public static final int POSITION_UNCHANGED = -1;
   public static final int POSITION_NONE = -2;
 
   public abstract int getCount();
 
   public abstract void startUpdate(View paramView);
 
   public abstract Object instantiateItem(View paramView, int paramInt);
 
   public abstract void destroyItem(View paramView, int paramInt, Object paramObject);
 
   public abstract void finishUpdate(View paramView);
 
   public abstract boolean isViewFromObject(View paramView, Object paramObject);
 
   public abstract Parcelable saveState();
 
   public abstract void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader);
 
   public int getItemPosition(Object object)
   {
     return -1;
   }
 
   public void notifyDataSetChanged()
   {
     if (this.mObserver != null)
       this.mObserver.onDataSetChanged();
   }
 
   public void setDataSetObserver(DataSetObserver observer)
   {
     this.mObserver = observer;
   }
 
   public static abstract interface DataSetObserver
   {
     public abstract void onDataSetChanged();
   }
 }


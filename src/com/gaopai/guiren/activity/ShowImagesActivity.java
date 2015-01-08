package com.gaopai.guiren.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.support.chat.ChatMsgHelper;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.widget.photoview.PhotoView;
import com.gaopai.guiren.widget.photoview.PhotoViewAttacher.OnViewTapListener;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 展示图片的界面，可以左右滑动
 * 
 */
public class ShowImagesActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.pager)
	private ViewPager pager;

	private List<MessageInfo> list = new ArrayList<MessageInfo>();
	private int position;
	@ViewInject(id = R.id.tv_pic_index)
	private TextView tvPicInd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_showimages);
		mContext = this;
		FinalActivity.initInjectedView(this);

		position = getIntent().getIntExtra("position", 0);
		List<MessageInfo> msgList = (List<MessageInfo>) getIntent().getSerializableExtra("msgList");
		if (msgList != null) {
			for (int i = 0; i < msgList.size(); i++) {
				if (msgList.get(i).fileType == MessageType.PICTURE) {
					list.add(msgList.get(i));
					if (position == i) {
						position = list.size() - 1;
					}
				}
			}
		}
		updatePos(position + 1);
		pager.setAdapter(new ImagePagerAdapter());
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				ShowImagesActivity.this.position = position;
				updatePos(position + 1);
			}
		});
		pager.setOffscreenPageLimit(1);
		pager.setCurrentItem(position);

		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.picture);
		mTitleBar.setVisibility(View.GONE);
	}

	private void updatePos(int position) {
		tvPicInd.setText(position + "/" + list.size());
	}

	public static Intent getIntent(Context context, String imgSmall, String imgLarge) {
		List<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
		messageInfos.add(ChatMsgHelper.creatPicMsg(imgSmall, imgLarge, ""));
		Intent intent = new Intent(context, ShowImagesActivity.class);
		intent.putExtra("msgList", (Serializable) messageInfos);
		intent.putExtra("position", 0);
		return intent;
	}

	private HashSet<ViewGroup> unRecycledViews = new HashSet<ViewGroup>();
	private static final String CURRENT_VISIBLE_PAGE = "currentPage";

	private class ImagePagerAdapter extends PagerAdapter {

		private LayoutInflater inflater;

		public ImagePagerAdapter() {
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			if (object instanceof ViewGroup) {
				((ViewPager) container).removeView((View) object);
				unRecycledViews.remove(object);
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View contentView = inflater.inflate(R.layout.item_photoview, view, false);
			handlePage(position, contentView, true);

			((ViewPager) view).addView(contentView, 0);
			unRecycledViews.add((ViewGroup) contentView);
			return contentView;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			View contentView = (View) object;
			if (contentView == null) {
				return;
			}
			contentView.setTag(CURRENT_VISIBLE_PAGE);
			ImageView imageView = (ImageView) contentView.findViewById(R.id.image);
			if (imageView.getDrawable() != null) {
				return;
			}
			handlePage(position, contentView, false);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

	}

	private void handlePage(int position, View contentView, boolean fromInstantiateItem) {
		Logger.d(this, "pos=" + position);
		final PhotoView imageView = (PhotoView) contentView.findViewById(R.id.image);
		imageView.setVisibility(View.INVISIBLE);
		final ProgressBar wait = (ProgressBar) contentView.findViewById(R.id.wait);
		final TextView readError = (TextView) contentView.findViewById(R.id.error);
		if (wait.getVisibility() == View.VISIBLE) {
			return;
		}
		String url = list.get(position).imgUrlL;
		if (TextUtils.isEmpty(url)) {
			url = list.get(position).imgUrlS;
		}
		if (!url.startsWith("http://")) {
			url = "file://" + url;
		}

		ImageLoaderUtil.displayImage(url, imageView, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				readError.setVisibility(View.INVISIBLE);
				wait.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				wait.setVisibility(View.INVISIBLE);
				readError.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				// TODO Auto-generated method stub
				wait.setVisibility(View.INVISIBLE);
				imageView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				wait.setVisibility(View.INVISIBLE);
			}
		});
		imageView.setOnViewTapListener(new OnViewTapListener() {

			@Override
			public void onViewTap(View view, float x, float y) {
				if (mTitleBar.getVisibility() == View.VISIBLE) {
					AlphaAnimation animation = new AlphaAnimation(1.0f, 0f);
					animation.setDuration(500);
					mTitleBar.startAnimation(animation);
					animation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation arg0) {
							// TODO Auto-generated method stub
							mTitleBar.setVisibility(View.GONE);
						}
					});

				} else {
					mTitleBar.setVisibility(View.VISIBLE);
					AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
					animation.setDuration(500);
					mTitleBar.startAnimation(animation);
					animation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation arg0) {
							// TODO Auto-generated method stub
							mTitleBar.setVisibility(View.VISIBLE);
						}
					});
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

	private void savePic(String url) {
		final String savePath = DamiApp.downloadPath + url.substring(url.lastIndexOf("/") + 1, url.length());
		File saveFile = new File(savePath);
		if (saveFile.exists()) {
			showToast(R.string.has_save);
		} else {
			File directory = saveFile.getParentFile();
			if (!directory.exists()) {
				directory.mkdirs();
			}
			boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			if (!sdCardExist) {// 如果不存在SD卡，进行提示
				showToast(R.string.cant_save_sdcard);
				return;
			}
			FinalHttp fh = new FinalHttp();
			fh.download(url, savePath, new AjaxCallBack<File>() {

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					// TODO Auto-generated method stub
					super.onFailure(t, errorNo, strMsg);
					showToast(R.string.save_error);
				}

				@Override
				public void onLoading(long count, long current) {
					// TODO Auto-generated method stub
					super.onLoading(count, current);
				}

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
				}

				@Override
				public void onSuccess(File t) {
					super.onSuccess(t);
					showToast(getResources().getString(R.string.has_save_to_address) + DamiApp.downloadPath);
				}

			});
		}
	}

}
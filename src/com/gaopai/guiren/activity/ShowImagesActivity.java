package com.gaopai.guiren.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
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
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.widget.photoview.PhotoView;
import com.gaopai.guiren.widget.photoview.PhotoViewAttacher.OnViewTapListener;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.socialize.controller.impl.w;

/**
 * 展示图片的界面，可以左右滑动
 * 
 */
@SuppressLint("NewApi")
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
		FinalActivity.initInjectedView(this);
		mContext = this;

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
		if (list.size() == 1) {
			tvPicInd.setVisibility(View.GONE);
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
			PhotoView imageView = (PhotoView) contentView.findViewById(R.id.image);
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
		final PhotoView imageView = (PhotoView) contentView.findViewById(R.id.image);
		imageView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				showMutiDialog(null, new String[] { "保存" }, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						saveImageView(imageView);
					}
				});
				return true;
			}
		});
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
				wait.setVisibility(View.INVISIBLE);
				int width = loadedImage.getWidth();
				int height = loadedImage.getHeight();
				imageView.setVisibility(View.VISIBLE);
				imageView.setMaxScale(10);
				imageView.setMidScale(5);
				if ((height > width ? height / width : width / height) > 10) {
					imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				}
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				wait.setVisibility(View.INVISIBLE);
			}
		});
		imageView.setOnViewTapListener(new OnViewTapListener() {

			@Override
			public void onViewTap(View view, float x, float y) {
				ShowImagesActivity.this.finish();
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

	private void saveImageView(ImageView imageView) {
		Drawable drawable = imageView.getDrawable();
		if (drawable == null || !(drawable instanceof BitmapDrawable)) {
			return;
		}
		BitmapDrawable d = (BitmapDrawable) drawable;
		final Bitmap bitmap = d.getBitmap();
		if (bitmap == null) {
			return;
		}
		final String fileName = System.currentTimeMillis() + ".png";
		final String savePath = DamiApp.downloadPath + "savePic/" + fileName;
		File directory = new File(savePath).getParentFile();
		if (!directory.exists()) {
			directory.mkdirs();
		}
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (!sdCardExist) {
			showToast(R.string.cant_save_sdcard);
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				FileOutputStream fileOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(savePath);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							showToast(getResources().getString(R.string.has_save_to_address) + savePath);
						}
					});
					MediaStore.Images.Media.insertImage(mContext.getContentResolver(), savePath, fileName, null);
					mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
							+ savePath)));

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						if (fileOutputStream != null) {
							fileOutputStream.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
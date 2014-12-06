package com.gaopai.guiren.utils;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.gaopai.guiren.R;
import com.gaopai.guiren.R.string;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class ImageLoaderUtil {

	public static ImageLoader imageLoader;
	public static DisplayImageOptions options;
	public static DisplayImageOptions options_normal;
	public static AnimateFirstDisplayListener animateFirstListener;

	// 缓存路径:sdcard/Android/data/[package_name]/cache
	public static void init(Context context) {

		int memClass = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		int cacheSize = 1024 * 1024 * memClass / 8;
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 3)
				.memoryCacheSize(cacheSize)
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.diskCacheSize(10 * 1024 * 1024)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		imageLoader = ImageLoader.getInstance();
		ImageLoader.getInstance().init(config);

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_tribe)
				.showImageOnLoading(R.drawable.default_tribe)
				.showImageOnFail(R.drawable.default_tribe)
				.resetViewBeforeLoading(false)
				// 设置图片在下载前是否重置，复位
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		options_normal = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.normal)
				.showImageOnLoading(R.drawable.normal)
				.showImageOnFail(R.drawable.normal)
				.resetViewBeforeLoading(false)
				// 设置图片在下载前是否重置，复位
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		animateFirstListener = new AnimateFirstDisplayListener();
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	public static void displayImageDefault(String url, ImageView imageView) {
		imageLoader.displayImage(url, imageView);
	}

	public static void displayImage(String url, ImageView imageView) {
		imageLoader.displayImage(url, imageView, options);
	}
	public static void displayImage(String url, ImageView imageView, int defultImage) {
		if (!TextUtils.isEmpty(url)) {
			imageLoader.displayImage(url, imageView, options);
		} else {
			imageView.setImageResource(defultImage);
		}
	}

	public static void displayImage(String url, ImageView imageView,
			DisplayImageOptions options) {
		imageLoader.displayImage(url, imageView, options);
	}

	public static void displayImage(String url, ImageView imageView,
			ImageLoadingListener listener) {
		imageLoader.displayImage(url, imageView, options, listener);
	}

	public static void displayImageByProgress(String url, ImageView imageView,
			DisplayImageOptions options, final ProgressBar progressbar) {
		imageLoader.displayImage(url, imageView, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						if (progressbar != null)
							progressbar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub
						if (progressbar != null)
							progressbar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						if (progressbar != null)
							progressbar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub
						if (progressbar != null)
							progressbar.setVisibility(View.GONE);
					}
				});
	}

	public static void clearCache() {
		imageLoader.clearDiskCache();
		imageLoader.clearMemoryCache();
	}

}

package com.gaopai.guiren.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * @author 超
 * 
 */

public class LocalPicActivity extends BaseActivity implements OnClickListener {

	public static final String KEY_PIC_SELECT_PATH_LIST = "pic_path_list";

	@ViewInject(id = R.id.gv_pic)
	private GridView gv_pic;
	private List<String> listPath = new ArrayList<String>();
	private ArrayList<String> listGuirenPath = new ArrayList<String>();
	private List<String> listSelectPath = new ArrayList<String>();
	private int width;
	private Animation mLitteAnimation;

	DisplayImageOptions mDefaultOptions2;

	private String mImageFilePath;
	protected ImageLoader mImageLoader = ImageLoader.getInstance();
	public LayoutInflater mInflater;

	private int mPicRequireType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_localpic);
		FinalActivity.initInjectedView(this);

		mDefaultOptions2 = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT).bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
				.build();
		mInflater = LayoutInflater.from(mContext);
		initView();
	}

	/**
	 * 初始化加载View
	 */
	private void initView() {
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText("选择照片");
		View ivComplete = mTitleBar.addRightImageView(R.drawable.icon_titlebar_confirm);
		ivComplete.setId(R.id.ab_complete);
		ivComplete.setOnClickListener(this);

		mPicRequireType = getIntent().getIntExtra(LocalPicPathActivity.KEY_PIC_REQUIRE_TYPE,
				LocalPicPathActivity.PIC_REQUIRE_MUTI);
		listPath = getIntent().getStringArrayListExtra("listPath");

		DisplayMetrics dm = MyUtils.getScreenSize(this);
		int space = MyUtils.dip2px(this, 2);
		width = (int) ((dm.widthPixels - space * 8) / 4.0);

		mLitteAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);

		GridAdapter adapter = new GridAdapter();
		gv_pic.setAdapter(adapter);
		gv_pic.setSelection(adapter.getCount());
	}

	private class GridAdapter extends BaseAdapter {

		AbsListView.LayoutParams param = null;
		FrameLayout.LayoutParams param2 = null;

		@Override
		public int getCount() {
			return listPath.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_pic_select, null, false);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.img_select = (ImageView) convertView.findViewById(R.id.img_select);
				param = new AbsListView.LayoutParams(width, width);
				param2 = new FrameLayout.LayoutParams(width, width);
				convertView.setLayoutParams(param);
				holder.img.setLayoutParams(param2);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			mImageLoader.displayImage("file://" + listPath.get(position), holder.img, mDefaultOptions2);
			// holder.img.setImageBitmap(getBitmap(listPath.get(position)));
			if (!listSelectPath.contains(listPath.get(position))) {
				holder.img_select.setVisibility(View.GONE);
			} else {
				holder.img_select.setVisibility(View.VISIBLE);
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (listSelectPath.contains(listPath.get(position))) {
						listSelectPath.remove(listPath.get(position));
						holder.img_select.setVisibility(View.GONE);
					} else {
						if (mPicRequireType == LocalPicPathActivity.PIC_REQUIRE_SINGLE) {
							if (listSelectPath.size() > 0) {
								showToast("最多选择1张照片");
								return;
							}
						}
						if (listSelectPath.size() > 8) {
							showToast("最多选择9张照片");
							return;
						}
						listSelectPath.add(listPath.get(position));
						holder.img_select.setVisibility(View.VISIBLE);
						holder.img_select.startAnimation(mLitteAnimation);
					}
				}
			});
			return convertView;
		}
	}

	private static class ViewHolder {
		public ImageView img;
		public ImageView img_select;
	}

	private static final int SHOW_PROGRESSBAR = 0;
	private static final int HIDE_PROGRESSBAR = 1;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SHOW_PROGRESSBAR:
				showProgressDialog("正在处理");
				break;
			case HIDE_PROGRESSBAR:
				removeProgressDialog();
				finishActivityWithPic();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void sendMessage(int what) {
		Message message = mHandler.obtainMessage(what);
		message.sendToTarget();
	}

	private void finishActivityWithPic() {
		Intent intent = new Intent();
		intent.putStringArrayListExtra(KEY_PIC_SELECT_PATH_LIST, listGuirenPath);
		setResult(RESULT_OK, intent);
		LocalPicActivity.this.finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ab_complete:
			if (mPicRequireType == LocalPicPathActivity.PIC_REQUIRE_SINGLE) {
				listGuirenPath.add(listSelectPath.get(0));
				finishActivityWithPic();
				return;
			}
			sendMessage(SHOW_PROGRESSBAR);
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < listSelectPath.size(); i++) {
						File file = new File(listSelectPath.get(i));
						mImageFilePath = FeatureFunction.saveTempBitmap(
								FeatureFunction.scalePicture(listSelectPath.get(i)), file.getName());
						listGuirenPath.add(mImageFilePath);
					}
					sendMessage(HIDE_PROGRESSBAR);
				}
			}).start();
			break;
		default:
			break;
		}
	}

}

package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.view.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LocalPicPathActivity extends BaseActivity {

//	public static final int PIC_REQUIRE_SINGLE = 0;// 只要一张图片
//	public static final int PIC_REQUIRE_MUTI = 1;// 最多9张
	
	public static final int REQUEST_CODE_PIC = 1;
	
	public static final String KEY_PIC_REQUIRE_TYPE = "pic_require";

	private int mSelectNum;

	@ViewInject(id = R.id.listview)
	private MyListView listview;

	private List<String> listPath = new ArrayList<String>(); // 遍历sd中图片所在文件夹的目录
	private List<ArrayList<String>> listImagePath = new ArrayList<ArrayList<String>>(); // 遍历sd中图片所在文件夹的目录
	public ArrayList<String> listSelectPath; // 传过来的图片路径地址
	public LayoutInflater mInflater;
	protected ImageLoader mImageLoader = ImageLoader.getInstance();

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			adapter = new ListAdapter();
			listview.setAdapter(adapter);
			removeProgressDialog();
		}
	};

	private ListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_localpath);
		FinalActivity.initInjectedView(this);
		initView();
		mSelectNum = getIntent().getIntExtra(KEY_PIC_REQUIRE_TYPE, 9);
	}

	private void initView() {
		// TODO Auto-generated method stub
		mInflater = LayoutInflater.from(mContext);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText("选择文件夹");
		listview = (MyListView) findViewById(R.id.listview);
		showProgressDialog("正在加载");
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getAllFolders();
				if (listPath != null) {
					for (String value : listPath) {
						listImagePath.add(MyUtils.GetAllImagesFilesPathFromFolder(value));
					}
					myHandler.sendEmptyMessage(0);

				}
			}
		}).start();
	}

	/**
	 * 获取图片文件夹路劲
	 * 
	 * @return
	 */
	public List<String> getAllFolders() {
		listPath = new ArrayList<String>();
		ContentResolver cr = this.getApplication().getContentResolver();
		if (cr == null) {
			return null;
		}
		Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Images.Media.DEFAULT_SORT_ORDER);
		if (null == cursor) {
			return null;
		}

		if (cursor.moveToFirst()) {
			do {
				String url = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
				if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".bmp") || url.endsWith(".png")
						|| url.endsWith(".gif")) {
					listPath.add(url.substring(0, url.lastIndexOf("/")));
				}

			} while (cursor.moveToNext());
		}
		listPath = removeDuplicate(listPath);
		return listPath;
	}

	/**
	 * 删除重复的文件夹
	 */
	public List<String> removeDuplicate(List<String> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).equals(list.get(i))) {
					list.remove(j);
				}
			}
		}
		return list;
	}

	private class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
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
			final String value;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_pic_path, null, false);
				holder.tv = (TextView) convertView.findViewById(R.id.tv_path);
				holder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			value = listPath.get(position);
			holder.tv.setText(value.subSequence(value.lastIndexOf("/") + 1, value.length()) + "("
					+ listImagePath.get(position).size() + ")");
			if (listImagePath.get(position) != null && listImagePath.get(position).size() > 0)
				mImageLoader.displayImage("file://" + listImagePath.get(position).get(0), holder.img_icon);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(LocalPicPathActivity.this, LocalPicActivity.class);
					intent.putStringArrayListExtra("listPath", listImagePath.get(position));
					intent.putExtra(KEY_PIC_REQUIRE_TYPE, mSelectNum);
					startActivityForResult(intent, REQUEST_CODE_PIC);
				}
			});
			return convertView;
		}
	}

	private static class ViewHolder {
		public TextView tv;
		public ImageView img_icon;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			// 如果是直接从相册获取
			case REQUEST_CODE_PIC: // 获取照片
				ArrayList<String> listSelectPath = data
						.getStringArrayListExtra(LocalPicActivity.KEY_PIC_SELECT_PATH_LIST);
				Intent intent = getIntent();
				intent.putStringArrayListExtra(LocalPicActivity.KEY_PIC_SELECT_PATH_LIST, listSelectPath);
				setResult(Activity.RESULT_OK, intent);
				LocalPicPathActivity.this.finish();
				break;
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}

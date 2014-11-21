package com.gaopai.guiren.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.net.SendDynamicResult;
import com.gaopai.guiren.net.MorePicture;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.view.FlowLayout;
import com.gaopai.guiren.view.MyGridLayout;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class SendDynamicMsgActivity extends BaseActivity implements OnClickListener {

	private Button btnAddTags;
	private FlowLayout flowLayout;
	private EditText etTags;
	private TextView tvWordNumLimit;
	private EditText etDynamicMsg;

	private ImageButton btnPhoto;
	private MyGridLayout picGrid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_send_dynamic);
		mTitleBar.setTitleText("发布动态");
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		View v = mTitleBar.addRightButtonView("发布");
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendDynamic();
			}
		});
		initViews();
	}

	private void initViews() {
		// TODO Auto-generated method stub
		btnAddTags = (Button) findViewById(R.id.btn_add_tag);
		btnAddTags.setOnClickListener(this);
		flowLayout = (FlowLayout) findViewById(R.id.flow_tags);
		setTagTransition();
		etTags = (EditText) findViewById(R.id.et_tags);
		tvWordNumLimit = (TextView) findViewById(R.id.tv_num_limit);
		etDynamicMsg = (EditText) findViewById(R.id.et_dynamic_msg);
		etDynamicMsg.addTextChangedListener(numLimitWatcher);
		etDynamicMsg.clearFocus();

		btnPhoto = (ImageButton) findViewById(R.id.btn_camera);
		btnPhoto.setOnClickListener(this);
		picGrid = (MyGridLayout) findViewById(R.id.gl_pic);
	}

	private TextWatcher numLimitWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			int nSelStart = 0;
			int nSelEnd = 0;
			boolean nOverMaxLength = false;

			nSelStart = etDynamicMsg.getSelectionStart();
			nSelEnd = etDynamicMsg.getSelectionEnd();
			nOverMaxLength = (s.length() > 20) ? true : false;
			if (nOverMaxLength) {
				s.delete(nSelStart - 1, nSelEnd);
				etDynamicMsg.setTextKeepState(s);
			}
			tvWordNumLimit.setText("还能输入" + (500 - s.length()) + "字");
		}
	};

	private void sendDynamic() {
		List<MorePicture> fileList = new ArrayList<MorePicture>();
		for (String pic : picList) {
			if (!TextUtils.isEmpty(pic)) {
				fileList.add(new MorePicture("pic", pic));
			}
		}
		String tags = "";
		List<String> tagList = getTagList();
		if (tagList.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (String tag : tagList) {
				builder.append(tag).append(",");
			}
			builder.substring(0, builder.length() - 1);
			tags = builder.toString();
		}

		DamiInfo.sendDynamic(etDynamicMsg.getText().toString(), fileList, 0, tags,
				new SimpleResponseListener(mContext, "正在发送") {

					@Override
					public void onSuccess(Object o) {
						// TODO Auto-generated method stub
						SendDynamicResult data = (SendDynamicResult) o;
						if (data.state != null && data.state.code == 0) {
							showToast("发送成功！");
						} else {
							otherCondition(data.state, SendDynamicMsgActivity.this);
						}
					}
				});
	}

	private List<String> getTagList() {
		List<String> tagList = new ArrayList<String>();
		int count = flowLayout.getChildCount();
		for (int j = 0; j < count; j++) {
			tagList.add(((TextView) ((ViewGroup) flowLayout.getChildAt(j)).getChildAt(0)).getText().toString());
		}
		return tagList;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_add_tag:
			String str = etTags.getText().toString();
			if (TextUtils.isEmpty(str)) {
				showToast(R.string.input_can_not_be_empty);
				return;
			}
			etTags.setText("");
			flowLayout.addView(creatTag(str), flowLayout.getTextLayoutParams());
			break;
		case R.id.btn_camera:
			showMoreWindow();
			break;
		default:
			break;
		}
	}

	private View creatTag(String text) {
		ViewGroup v = (ViewGroup) mInflater.inflate(R.layout.btn_send_dynamic_tag_without_streach, null);
		TextView textView = (TextView) v.findViewById(R.id.tv_tag);
		textView.setText(text);
		Button button = (Button) v.findViewById(R.id.btn_delete_tag);
		button.setOnClickListener(tagDeleteClickListener);
		return v;
	}

	private OnClickListener tagDeleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			flowLayout.removeView((View) v.getParent());
		}
	};

	PopupWindow moreWindow;

	private void showMoreWindow() {
		LinearLayout linearLayout = new LinearLayout(mContext);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		Button btnCamera = new Button(mContext);
		btnCamera.setText("相机");
		btnCamera.setGravity(Gravity.CENTER);
		btnCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnCameraAction();
				hideMoreWindow();
			}
		});
		linearLayout.addView(btnCamera, new LayoutParams(LayoutParams.MATCH_PARENT, MyUtils.dip2px(mContext, 50)));
		Button btnPhoto = new Button(mContext);
		btnPhoto.setGravity(Gravity.CENTER);
		btnPhoto.setText("相册");
		btnPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnPhotoAction();
				hideMoreWindow();
			}
		});
		linearLayout.addView(btnPhoto, new LayoutParams(LayoutParams.MATCH_PARENT, MyUtils.dip2px(mContext, 50)));
		if (moreWindow == null) {
			moreWindow = new PopupWindow(linearLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		moreWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		moreWindow.setAnimationStyle(R.style.window_bottom_animation);
		moreWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
	}

	private void hideMoreWindow() {
		if (moreWindow != null && moreWindow.isShowing()) {
			moreWindow.dismiss();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setTagTransition() {
		if (Build.VERSION.SDK_INT > 11) {
			LayoutTransition transition = new LayoutTransition();
			setupCustomAnimations(transition);
			flowLayout.setLayoutTransition(transition);
		}
	}

	@SuppressLint("NewApi")
	private void setupCustomAnimations(LayoutTransition mTransitioner) {
		// Changing while Adding
		PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", 0, 1);
		PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 0, 1);
		PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", 0, 1);
		PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 1);
		PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f, 1f);
		PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f, 1f);

		// CHANGE_DISAPPEARING
		Keyframe kf0 = Keyframe.ofFloat(0f, 0f);
		Keyframe kf1 = Keyframe.ofFloat(.9999f, 360f);
		Keyframe kf2 = Keyframe.ofFloat(1f, 0f);
		PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("rotation", kf0, kf1, kf2);
		final ObjectAnimator changeOut = ObjectAnimator.ofPropertyValuesHolder(this, pvhLeft, pvhTop, pvhRight,
				pvhBottom, pvhRotation).setDuration(mTransitioner.getDuration(LayoutTransition.CHANGE_DISAPPEARING));
		mTransitioner.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, changeOut);
		changeOut.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setRotation(0f);
			}
		});

		// APPEARING
		ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "rotationY", 90f, 0f).setDuration(
				mTransitioner.getDuration(LayoutTransition.APPEARING));
		mTransitioner.setAnimator(LayoutTransition.APPEARING, animIn);
		animIn.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setRotationY(0f);
			}
		});

		// DISAPPEARING
		ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "rotationX", 0f, 90f).setDuration(
				mTransitioner.getDuration(LayoutTransition.DISAPPEARING));
		mTransitioner.setAnimator(LayoutTransition.DISAPPEARING, animOut);
		animOut.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setRotationX(0f);
			}
		});

	}

	protected void btnCameraAction() {
		getImageFromCamera();
	}

	protected void btnPhotoAction() {
		getImageFromGallery();
	}

	static final int REQUEST_GET_IMAGE_BY_CAMERA = 1002;
	static final int REQUEST_ROTATE_IMAGE = 1003;
	static final int REQUEST_GET_URI = 101;
	public static final int REQUEST_GET_BITMAP = 124;
	public static final int REQUEST_GET_BITMAP_LIST = 125;
	private String TEMP_FILE_NAME = "header.jpg";

	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		TEMP_FILE_NAME = FeatureFunction.getPhotoFileName();

		if (FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)) {
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY,
					FeatureFunction.getPhotoFileName());
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, REQUEST_GET_IMAGE_BY_CAMERA);
		}
	}

	private void getImageFromGallery() {
		Intent intent = new Intent();
		intent.putExtra(LocalPicPathActivity.KEY_PIC_REQUIRE_TYPE, LocalPicPathActivity.PIC_REQUIRE_MUTI);
		intent.setClass(mContext, LocalPicPathActivity.class);
		startActivityForResult(intent, REQUEST_GET_BITMAP_LIST);
	}

	private List<String> picList = new ArrayList<String>();

	private void addPicture(String file) {
		picList.add(file);
		picGrid.addView(getImageView(file), picGrid.getChildCount() - 1);
	}

	private ImageView getImageView(String url) {
		ImageView imageView = new ImageView(mContext);
		Bitmap bitmap = BitmapFactory.decodeFile(url);
		imageView.setImageBitmap(bitmap);
		android.view.ViewGroup.LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(lp);
		imageView.setScaleType(ScaleType.FIT_XY);
		int padding = MyUtils.dip2px(mContext, 5);
		imageView.setPadding(padding, padding, padding, padding);
		return imageView;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_GET_IMAGE_BY_CAMERA:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Uri uri = data.getData();
					if (!TextUtils.isEmpty(uri.getAuthority())) {
						Cursor cursor = getContentResolver().query(uri, new String[] { MediaColumns.DATA }, null, null,
								null);
						if (null == cursor) {
							return;
						}
						cursor.moveToFirst();
						String path = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
						String extension = path.substring(path.lastIndexOf("."), path.length());
						if (FeatureFunction.isPic(extension)) {
							// sendPicFile(MessageType.PICTURE, path);
						}
					}
					return;
				} else {
					// Here if we give the uri, we need to read it
					String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY
							+ TEMP_FILE_NAME;
					String extension = path.substring(path.indexOf("."), path.length());
					if (FeatureFunction.isPic(extension)) {
						Intent intent = new Intent();
						intent.putExtra(RotateImageActivity.KEY_IMAGE_PATH, path);
						intent.setClass(SendDynamicMsgActivity.this, RotateImageActivity.class);
						startActivityForResult(intent, REQUEST_ROTATE_IMAGE);
					}
				}

			}
			break;
		case REQUEST_ROTATE_IMAGE:
			if (resultCode == RESULT_OK) {
				String path = data.getStringExtra(RotateImageActivity.KEY_IMAGE_PATH);
				if (!TextUtils.isEmpty(path)) {
					// sendPicFile(MessageType.PICTURE, path);
					addPicture(path);
				}
			}
			break;

		case REQUEST_GET_BITMAP_LIST:
			if (resultCode == RESULT_OK) {
				List<String> pathList = data.getStringArrayListExtra(LocalPicActivity.KEY_PIC_SELECT_PATH_LIST);
				for (String path : pathList) {
					if (!TextUtils.isEmpty(path)) {
						// sendPicFile(MessageType.PICTURE, path);
						addPicture(path);
					}
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}

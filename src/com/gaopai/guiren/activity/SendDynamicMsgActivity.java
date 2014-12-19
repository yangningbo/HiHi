package com.gaopai.guiren.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.TagBean;
import com.gaopai.guiren.bean.net.SendDynamicResult;
import com.gaopai.guiren.bean.net.TagResult;
import com.gaopai.guiren.net.MorePicture;
import com.gaopai.guiren.support.TagWindowManager;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.FlowLayout;
import com.gaopai.guiren.view.MyGridLayout;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class SendDynamicMsgActivity extends BaseActivity implements OnClickListener {

	private Button btnAddTags;
	private FlowLayout flowLayout;
	private FlowLayout flowTagsRec;
	private EditText etTags;
	private TextView tvWordNumLimit;
	private EditText etDynamicMsg;

	private ImageButton btnPhoto;
	private MyGridLayout picGrid;

	private List<TagBean> recTagList = new ArrayList<TagBean>();
	private TextView tvUseRealName;
	private int isHideName = 0;
	private boolean isSending = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_send_dynamic);
		mTitleBar.setTitleText(R.string.send_dynamic);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showExitDialog();
			}
		});
		View v = mTitleBar.addRightButtonView(R.drawable.icon_titlebar_send_dy);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				if (isSending) {
//					return;
//				}
				sendDynamic();
			}
		});
		initViews();
		getTags();
	}

	@Override
	public void onBackPressed() {
		showExitDialog();
	}

	private void initViews() {
		// TODO Auto-generated method stub
		btnAddTags = (Button) findViewById(R.id.btn_add_tag);
		btnAddTags.setOnClickListener(this);
		flowLayout = (FlowLayout) findViewById(R.id.flow_tags);
		flowTagsRec = (FlowLayout) findViewById(R.id.flow_tags_recommend);
		TagWindowManager.setTagTransition(flowLayout, mContext);
		etTags = (EditText) findViewById(R.id.et_tags);
		tvWordNumLimit = (TextView) findViewById(R.id.tv_num_limit);
		etDynamicMsg = (EditText) findViewById(R.id.et_dynamic_msg);
		etDynamicMsg.addTextChangedListener(numLimitWatcher);
		etDynamicMsg.clearFocus();

		btnPhoto = (ImageButton) findViewById(R.id.btn_camera);
		btnPhoto.setOnClickListener(this);
		picGrid = (MyGridLayout) findViewById(R.id.gl_pic);

		tvUseRealName = ViewUtil.findViewById(this, R.id.tv_send_dy_realname);
		tvUseRealName.setOnClickListener(this);
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
			nOverMaxLength = (s.length() > 500) ? true : false;
			if (nOverMaxLength) {
				s.delete(nSelStart - 1, nSelEnd);
				etDynamicMsg.setTextKeepState(s);
			}
			tvWordNumLimit.setText("还能输入" + (500 - s.length()) + "字");
		}
	};

	private void getTags() {
		DamiInfo.getTags(new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				TagResult data = (TagResult) o;
				if (data.state != null && data.state.code == 0) {
					for (TagBean tag : data.data) {
						flowTagsRec.addView(
								TagWindowManager.creatTageWithAction(tag.tag, addRecTagListener, mInflater),
								flowTagsRec.getTextLayoutParams());
					}
				} else {
					this.otherCondition(data.state, SendDynamicMsgActivity.this);
				}
			}
		});
	}

	private OnClickListener addRecTagListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String text = (String) v.getTag();
			if (!checkIsTagInList(text)) {
				flowLayout.addView(TagWindowManager.creatTagWithoutStrech(text, tagDeleteClickListener, mInflater),
						flowLayout.getTextLayoutParams());
			}
		}
	};

	private boolean checkIsTagInList(String tag) {
		for (int i = 0, count = flowLayout.getChildCount(); i < count; i++) {
			String str = ((TextView) ((ViewGroup) flowLayout.getChildAt(i)).getChildAt(0)).getText().toString();
			if (str.equals(tag)) {
				showToast(R.string.tag_exist);
				return true;
			}
		}
		return false;
	}

	private void sendDynamic() {
		List<MorePicture> fileList = new ArrayList<MorePicture>();
		int i = 0;
		for (String pic : picList) {
			if (!TextUtils.isEmpty(pic)) {
				fileList.add(new MorePicture("image[" + i + "]", pic));
			}
			i++;
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
		if (fileList.size() == 0 && TextUtils.isEmpty(etDynamicMsg.getText().toString())) {
			showToast(R.string.input_can_not_be_empty);
			return;
		}
		isSending = true;

		DamiInfo.sendDynamic(etDynamicMsg.getText().toString(), fileList, isHideName, tags, new SimpleResponseListener(
				mContext, R.string.send_now) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				SendDynamicResult data = (SendDynamicResult) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.send_success);
					SendDynamicMsgActivity.this.finish();
				} else {
					otherCondition(data.state, SendDynamicMsgActivity.this);
				}
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				isSending = false;
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
			flowLayout.addView(TagWindowManager.creatTagWithoutStrech(str, tagDeleteClickListener, mInflater),
					flowLayout.getTextLayoutParams());
			break;
		case R.id.btn_camera:
			// showMoreWindow();
			showChosePicDialog();
			break;
		case R.id.tv_send_dy_realname:
			isHideName = 1 - isHideName;
			changeIsHideName(isHideName);
			break;
		default:
			break;
		}
	}

	private void changeIsHideName(int isHide) {
		int drawbale = (isHide == 0) ? R.drawable.icon_send_dy_real_name : R.drawable.icon_send_dy_nick_name;
		tvUseRealName.setCompoundDrawablesWithIntrinsicBounds(drawbale, 0, 0, 0);
		if (isHide == 0) {
			tvUseRealName.setText(R.string.send_user_real_name);
		} else {
			tvUseRealName.setText(R.string.send_user_nick_name);
		}
	}

	private OnClickListener tagDeleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			flowLayout.removeView((View) v.getParent());
		}
	};

	public void showChosePicDialog() {
		String[] array = new String[2];
		array[0] = getString(R.string.camera);
		array[1] = getString(R.string.gallery);
		AlertDialog dialog = new AlertDialog.Builder(mContext).setItems(array, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (which == 0) {
					btnCameraAction();
				} else {
					btnPhotoAction();
				}
			}
		}).create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.show();
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

		// TEMP_FILE_NAME = FeatureFunction.getPhotoFileName();

		if (FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)) {
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY,
					TEMP_FILE_NAME);
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
		if (picList.size() >= 9) {
			showToast(R.string.nine_picture_at_most);
			return;
		}
		picList.add(file);
		picGrid.addView(getImageView(file), picGrid.getChildCount() - 1);
	}

	private ImageView getImageView(final String url) {
		ImageView imageView = new ImageView(mContext);
		Bitmap bitmap = BitmapFactory.decodeFile(url);
		imageView.setImageBitmap(bitmap);
		android.view.ViewGroup.LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(lp);
		imageView.setScaleType(ScaleType.FIT_XY);
		int padding = MyUtils.dip2px(mContext, 5);
		imageView.setPadding(padding, padding, padding, padding);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				showMutiDialog(null, new String[]{getString(R.string.delete)}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						picGrid.removeView(v);
						picList.remove(url);
					}
				});
			}
		});
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

	private void showExitDialog() {
		showDialog(getString(R.string.confirm_cancel_send_dynamic), null, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SendDynamicMsgActivity.this.finish();
			}
		});
	}
}

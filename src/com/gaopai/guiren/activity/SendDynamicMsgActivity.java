package com.gaopai.guiren.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.TagBean;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.SendDynamicResult;
import com.gaopai.guiren.bean.net.TagResult;
import com.gaopai.guiren.net.MorePicture;
import com.gaopai.guiren.support.CameralHelper;
import com.gaopai.guiren.support.TagWindowManager;
import com.gaopai.guiren.support.TextLimitWatcher;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.SPConst;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.FlowLayout;
import com.gaopai.guiren.view.MyGridLayout;
import com.gaopai.guiren.volley.SimpleResponseListener;

/**
 * The boss owe us 100 yuan(the monetary unit of China) in Mid-Autumn Festival!
 */
public class SendDynamicMsgActivity extends BaseActivity implements OnClickListener {

	private Button btnAddTags;
	private FlowLayout flowLayout;
	private FlowLayout flowTagsRec;
	private EditText etTags;
	private TextView tvWordNumLimit;
	private EditText etDynamicMsg;

	private ImageButton btnPhoto;
	private MyGridLayout picGrid;

	private TextView tvUseRealName;
	private int isHideName = 0;

	private CameralHelper cameralHelper;

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
				showExitDialog();
			}
		});
		View v = mTitleBar.addRightButtonView(R.drawable.icon_titlebar_send_dy);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendDynamic();
			}
		});
		initViews();
		cameralHelper = new CameralHelper(this);
		cameralHelper.setCallback(callback);
		getTags();
		showGuideDialog_chat(tvUseRealName);

		if (savedInstanceState != null) {
			picList = (List<String>) savedInstanceState.getSerializable("picList");
			setImageFromPictureList(picList);
			List<String> tagList = (List<String>) savedInstanceState.getSerializable("tagList");
			setTagFromList(tagList);
			cameralHelper.retriveTempPicName(savedInstanceState.getString("tempPic"));
			isHideName = savedInstanceState.getInt("isHideName");
			changeIsHideName(isHideName);
		}
		System.gc();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("picList", (Serializable) picList);
		outState.putSerializable("tagList", (Serializable) getTagList());
		outState.putString("tempPic", cameralHelper.getTempPicName());
		outState.putInt("isHideName", isHideName);
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
		etTags.setFilters(MyTextUtils.tagEditFilters);
		tvWordNumLimit = (TextView) findViewById(R.id.tv_num_limit);
		etDynamicMsg = (EditText) findViewById(R.id.et_dynamic_msg);
		etDynamicMsg.addTextChangedListener(new TextLimitWatcher(tvWordNumLimit, 500));

		btnPhoto = (ImageButton) findViewById(R.id.btn_camera);
		btnPhoto.setOnClickListener(this);
		picGrid = (MyGridLayout) findViewById(R.id.gl_pic);

		tvUseRealName = ViewUtil.findViewById(this, R.id.tv_send_dy_realname);
		tvUseRealName.setOnClickListener(this);
	}

	private void getTags() {
		DamiInfo.getTags("dynamic", new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				TagResult data = (TagResult) o;
				if (data.state != null && data.state.code == 0) {
					for (TagBean tag : data.data) {
						flowTagsRec.addView(TagWindowManager.creatTag(tag.tag, addRecTagListener, mInflater, false),
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
				flowLayout.addView(TagWindowManager.creatTag(text, tagDeleteClickListener, mInflater, true),
						flowLayout.getTextLayoutParams());
			}
		}
	};

	private boolean checkIsTagInList(String tag) {
		for (int i = 0, count = flowLayout.getChildCount(); i < count; i++) {
			String str = TagWindowManager.getText((ViewGroup) flowLayout.getChildAt(i));
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

		DamiInfo.sendDynamic(etDynamicMsg.getText().toString().trim(), fileList, isHideName, tags,
				new SimpleResponseListener(mContext, R.string.send_now) {

					@Override
					public void onSuccess(Object o) {
						// TODO Auto-generated method stub
						SendDynamicResult data = (SendDynamicResult) o;
						if (data.state != null && data.state.code == 0) {
							showToast(R.string.send_success);

							User user = DamiCommon.getLoginResult(mContext);
							user.dynamicCount = user.dynamicCount + 1;
							DamiCommon.saveLoginResult(mContext, user);
							mContext.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));

							SendDynamicMsgActivity.this.finish();
						} else {
							otherCondition(data.state, SendDynamicMsgActivity.this);
						}
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
					}
				});
	}

	private List<String> getTagList() {
		List<String> tagList = new ArrayList<String>();
		int count = flowLayout.getChildCount();
		for (int j = 0; j < count; j++) {
			tagList.add(TagWindowManager.getText((ViewGroup) flowLayout.getChildAt(j)));
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
			if (!checkIsTagInList(str)) {
				flowLayout.addView(TagWindowManager.creatTag(str, tagDeleteClickListener, mInflater, true),
						flowLayout.getTextLayoutParams());
			}
			etTags.setText("");
			break;
		case R.id.btn_camera:
			// showMoreWindow();
			if (picList.size() >= 9) {
				showToast(R.string.nine_pic_is_max);
				return;
			}
			cameralHelper.setCallback(callback);
			cameralHelper.showDefaultSelectDialog(null);

			break;
		case R.id.tv_send_dy_realname:
			isHideName = 1 - isHideName;
			changeIsHideName(isHideName);
			break;
		default:
			break;
		}
	}

	private void setTagFromList(List<String> tagList) {
		flowLayout.removeAllViews();
		for (String string : tagList) {
			flowLayout.addView(TagWindowManager.creatTag(string, tagDeleteClickListener, mInflater, true),
					flowLayout.getTextLayoutParams());
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
			flowLayout.removeView((View) v.getParent());
		}
	};

	private List<String> picList = new ArrayList<String>();

	private void addPicture(String file) {
		if (picList.size() >= 9) {
			showToast(R.string.nine_picture_at_most);
			return;
		}
		picList.add(file);
		picGrid.addView(getImageView(file), picGrid.getChildCount() - 1);
	}

	private void setImageFromPictureList(List<String> picList) {
		if (picList == null) {
			return;
		}
		for (String path : picList) {
			picGrid.addView(getImageView(path), picGrid.getChildCount() - 1);
		}
	}

	private ImageView getImageView(final String url) {
		ImageView imageView = new ImageView(mContext);
		imageView.setImageBitmap(FeatureFunction.decodeSampledBitmap(url, 200, 200));
		android.view.ViewGroup.LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(lp);
		imageView.setScaleType(ScaleType.FIT_XY);
		int padding = MyUtils.dip2px(mContext, 5);
		imageView.setPadding(padding, padding, padding, padding);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				showMutiDialog(null, new String[] { getString(R.string.delete) },
						new DialogInterface.OnClickListener() {
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

	private CameralHelper.GetImageCallback callback = new CameralHelper.SimpleCallback() {
		@Override
		public void receiveOriginPicList(List<String> pathList) {
			for (String path : pathList) {
				if (!TextUtils.isEmpty(path)) {
					addPicture(path);
				}
			}
		}

		@Override
		public void receiveOriginPic(String path) {
			addPicture(path);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			cameralHelper.onActivityResult(requestCode, resultCode, data);
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

	protected void showGuideDialog_chat(View targetView) {
		boolean result = DamiApp.getInstance().getPou().getBoolean(SPConst.KEY_GUIDE_USE_REAL_NAME, true);
		if (!result) {
			return;
		}
		View mDialogView = View.inflate(this, R.layout.dialog_guide, null);
		RelativeLayout rl = (RelativeLayout) mDialogView.findViewById(R.id.rl);
		final Dialog dialog = new Dialog(this, R.style.dialog_middle);
		dialog.setContentView(mDialogView);
		dialog.setCancelable(false);
		ImageView iv_tip1 = new ImageView(this);
		iv_tip1.setImageResource(R.drawable.icon_guide_send_dynamic_realname);
		rl.addView(iv_tip1);
		getLocation(targetView, iv_tip1, 1);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		lp.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;

		dialogWindow.setAttributes(lp);
		dialog.setCanceledOnTouchOutside(true);
		rl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dialog.dismiss();
				return false;
			}
		});
		dialog.show();
		DamiApp.getInstance().getPou().setBoolean(SPConst.KEY_GUIDE_USE_REAL_NAME, false);
	}
}

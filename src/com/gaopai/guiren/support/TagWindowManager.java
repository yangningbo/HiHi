package com.gaopai.guiren.support;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.TagBean;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.view.FlowLayout;

public class TagWindowManager implements OnClickListener {

	private List<TagBean> tagList = new ArrayList<TagBean>();
	private List<TagBean> recTagList = new ArrayList<TagBean>();

	private BaseActivity mContext;
	private LayoutInflater mInflater;

	private FlowLayout flowTagsAdd;
	private EditText etTags;
	private Button btnAddTags;

	private boolean isSelf = false;// true add tag with a delete button, false
									// without a delete button

	public TagWindowManager(BaseActivity context, boolean isSelf, TagCallback callback) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		this.isSelf = isSelf;
		tagCallback = callback;
	}

	public static interface TagCallback {
		public void onSave(String tags);
	}

	private TagCallback tagCallback;

	public void setRecTagList(List<TagBean> recList) {
		this.recTagList = recList;
	}

	public void setIsSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}

	public void setTagList(List<TagBean> tagList) {
		if (tagList == null) {
			return;
		}
		this.tagList = tagList;
	}

	public void showTagsWindow() {
		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = mInflater.inflate(R.layout.popup_window_add_tags, null);
		flowTagsAdd = (FlowLayout) view.findViewById(R.id.flow_tags_add);
		setTagTransition(flowTagsAdd, mContext);
		FlowLayout flowTagsRec = (FlowLayout) view.findViewById(R.id.flow_tags_recommend);
		etTags = (EditText) view.findViewById(R.id.et_tags);
		btnAddTags = (Button) view.findViewById(R.id.btn_add_tag);
		btnAddTags.setOnClickListener(this);
		if (isSelf) {
			bindTags(flowTagsAdd, true);
		} else {
			bindTags(flowTagsAdd, false);
		}
		for (TagBean tag : recTagList) {
			flowTagsRec.addView(creatTageWithDefaultAction(tag.tag), flowTagsRec.getTextLayoutParams());
		}
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		Button btnSave = (Button) view.findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int count = flowTagsAdd.getChildCount();
				StringBuilder tagStringBuilder = new StringBuilder();
				tagList.clear();
				for (int i = 0; i < count; i++) {
					String str = ((TextView) ((ViewGroup) flowTagsAdd.getChildAt(i)).getChildAt(0)).getText()
							.toString();
					TagBean tagBean = new TagBean();
					tagBean.tag = str;
					tagList.add(tagBean);
					tagStringBuilder.append(str).append(",");
				}
				tagStringBuilder.substring(0, tagStringBuilder.length() - 1);
				tagCallback.onSave(tagStringBuilder.toString());
				dialog.cancel();
			}
		});
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setBackgroundDrawableResource(R.drawable.transparent);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		WindowManager m = mContext.getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
		p.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度设置为屏幕的0.6
		p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.65
		dialogWindow.setAttributes(p);

		dialog.show();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void setTagTransition(ViewGroup viewGroup, Context context) {
		if (Build.VERSION.SDK_INT > 11) {
			LayoutTransition transition = new LayoutTransition();
			setupCustomAnimations(transition, context);
			viewGroup.setLayoutTransition(transition);
		}
	}

	@SuppressLint("NewApi")
	private static void setupCustomAnimations(LayoutTransition mTransitioner, Context context) {
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
		final ObjectAnimator changeOut = ObjectAnimator.ofPropertyValuesHolder(context, pvhLeft, pvhTop, pvhRight,
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_add_tag:
			String str = etTags.getText().toString();
			if (TextUtils.isEmpty(str)) {
				mContext.showToast(R.string.input_can_not_be_empty);
				return;
			}
			etTags.setText("");
			if (!checkIsTagInList(str)) {
				flowTagsAdd.addView(creatTagWithDeleteDefault(str), flowTagsAdd.getTextLayoutParams());
			}
			break;
		default:
			break;
		}
	}

	private boolean checkIsTagInList(String tag) {
		for (int i = 0, count = flowTagsAdd.getChildCount(); i < count; i++) {
			String str = ((TextView) ((ViewGroup) flowTagsAdd.getChildAt(i)).getChildAt(0)).getText().toString();
			if (str.equals(tag)) {
				mContext.showToast(R.string.tag_exist);
				return true;
			}
		}
		return false;
	}

	public void bindTags(FlowLayout taLayoutPara, boolean isWithDelete) {
		bindTags(taLayoutPara, isWithDelete, tagList, null);
	}

	public void bindTags(FlowLayout taLayoutPara, boolean isWithDelete, List<TagBean> tagList, OnClickListener listener) {
		taLayoutPara.removeAllViews();
		if (tagList == null) {
			tagList = new ArrayList<TagBean>();
		}
		for (TagBean tag : tagList) {
			if (isWithDelete) {
				taLayoutPara.addView(creatTagWithDeleteDefault(tag.tag), taLayoutPara.getTextLayoutParams());
			} else {
				View view = creatTageWithAction(tag.tag, listener, mInflater);
				view.setTag(tag);
				taLayoutPara.addView(view, taLayoutPara.getTextLayoutParams());
			}
		}
	}

	private View creatTagWithDeleteDefault(String text) {
		return creatTagWithDelete(text, tagDeleteClickListener, mInflater);
	}

	public static View creatTagWithDelete(String text, OnClickListener listener, LayoutInflater inflater) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.btn_send_dynamic_tag, null);
		TextView textView = (TextView) v.findViewById(R.id.tv_tag);
		textView.setText(text);
		Button button = (Button) v.findViewById(R.id.btn_delete_tag);
		button.setOnClickListener(listener);
		return v;
	}

	public static View creatTagWithoutStrech(String text, OnClickListener listener, LayoutInflater inflater) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.btn_send_dynamic_tag_without_streach, null);
		TextView textView = (TextView) v.findViewById(R.id.tv_tag);
		textView.setText(text);
		Button button = (Button) v.findViewById(R.id.btn_delete_tag);
		button.setOnClickListener(listener);
		return v;
	}

	private View creatTagWithoutDelete(String text) {
		return creatTageWithAction(text, null, mInflater);
	}

	public OnClickListener addRecClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String text = (String) v.getTag();
			if (!checkIsTagInList(text)) {
				flowTagsAdd.addView(creatTagWithDeleteDefault(text), flowTagsAdd.getTextLayoutParams());
			}
		}
	};

	private View creatTageWithDefaultAction(final String text) {
		return creatTageWithAction(text, addRecClickListener, mInflater);
	}

	public static View creatTageWithAction(final String text, OnClickListener listener, LayoutInflater mInflater) {
		ViewGroup v = (ViewGroup) mInflater.inflate(R.layout.btn_send_dynamic_tag, null);
		TextView textView = (TextView) v.findViewById(R.id.tv_tag);
		textView.setText(text);
		v.findViewById(R.id.btn_delete_tag).setVisibility(View.GONE);
		v.setTag(text);
		v.setOnClickListener(listener);
		return v;
	}

	private OnClickListener tagDeleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			flowTagsAdd.removeView((View) v.getParent());
		}
	};

}

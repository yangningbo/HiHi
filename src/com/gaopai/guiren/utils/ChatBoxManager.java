package com.gaopai.guiren.utils;

import com.gaopai.guiren.R;
import com.gaopai.guiren.view.ChatGridLayout;
import com.gaopai.guiren.widget.emotion.EmotionPicker;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class ChatBoxManager {
	private EditText mContentEdit;
	private Button mSwitchVoiceTextBtn;
	private EmotionPicker mEmotionPicker;
	private ChatGridLayout mChatGridLayout;
	private Button mEmotionBtn;
	private Button mVoiceSendBtn;
	private Activity mContext;

	public ChatBoxManager(Activity context, EditText editText, Button switchVoiceButton, EmotionPicker emotionPicker,
			ChatGridLayout chatGridLayout, Button emotionBtn, Button voiceSendBtn) {
		mContentEdit = editText;
		mSwitchVoiceTextBtn = switchVoiceButton;
		mEmotionBtn = emotionBtn;
		mEmotionPicker = emotionPicker;
		mChatGridLayout = chatGridLayout;
		mContext = context;
		mVoiceSendBtn = voiceSendBtn;
	}
	
	public void hideEmotion() {
		mEmotionPicker.hide(mContext);
		mEmotionBtn.setBackgroundResource(R.drawable.chatting_biaoqing_btn_normal);
	}

	public void showEmotion() {
		mChatGridLayout.hide(mContext);
		mEmotionPicker.show(mContext);
		mEmotionBtn.setBackgroundResource(R.drawable.chatting_biaoqing_btn_enable);
	}

	public void emotionClick() {
		if (mEmotionPicker.getVisibility() == View.VISIBLE) {
			hideEmotion();
		} else {
			showEmotion();
		}
	}

	public void addGridClick() {
		if (mChatGridLayout.getVisibility() == View.VISIBLE) {
			hideAddGrid();
		} else {
			showAddGrid();
		}
	}
	
	public void hideAll() {
		hideAddGrid();
		hideEmotion();
		hideSoftKeyboard();
	}
	
	public void editClick() {
		hideAddGrid();
		hideEmotion();
	}
 
	public void showAddGrid() {
		hideEmotion();
		mChatGridLayout.show(mContext);
	}

	public void hideAddGrid() {
		if (mChatGridLayout.getVisibility() == View.VISIBLE) {
			mChatGridLayout.hide(mContext);
		}
	}

	public void hideSoftKeyboard() {
		hideSoftKeyboard(mContext.getCurrentFocus());
	}

	public void hideSoftKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public void showSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContentEdit.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}

	public void switchTextVoice() {
		boolean sendVoice = (mContentEdit.getVisibility() == View.VISIBLE);
		if (sendVoice) {// true 文字
			switchToVoice();
		} else {
			switchToText(true);
		}
	}
	public void switchToVoice() {
		mSwitchVoiceTextBtn.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn_normal);
		mEmotionBtn.setVisibility(View.GONE);
		mContentEdit.setVisibility(View.GONE);
		mVoiceSendBtn.setVisibility(View.VISIBLE);
		hideEmotion();
		hideAddGrid();
		hideSoftKeyboard();
	}
	public void switchToText(boolean showKeyboard) {
		mSwitchVoiceTextBtn.setBackgroundResource(R.drawable.chatting_setmode_voice_btn_normal);
		mVoiceSendBtn.setVisibility(View.GONE);
		mEmotionBtn.setVisibility(View.VISIBLE);
		mContentEdit.setVisibility(View.VISIBLE);
		mContentEdit.setFocusable(true);
		mContentEdit.setFocusableInTouchMode(true);
		mContentEdit.requestFocus();
		hideAddGrid();
		hideEmotion();
		if (showKeyboard) {
			showSoftKeyboard();
		}
	}

}
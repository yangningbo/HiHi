package com.gaopai.guiren.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.R;

public class RecordDialog {
	
	private ImageView recordView;
	private Dialog dialog = null;
	private ImageView mDialogBackground;
	private TextView tvInfo;
	
	private Context context;
	public RecordDialog(Context context) {
		this.context = context;
		intDialog();
	}
	
	private void intDialog(){
		dialog = new Dialog(context, R.style.DialogPrompt);
		dialog.setContentView(R.layout.chat_voice_dialog);
		recordView = (ImageView) dialog.findViewById(R.id.iv_dialog_record);
		tvInfo = (TextView) dialog.findViewById(R.id.tv_record_info);
		mDialogBackground = (ImageView) dialog.findViewById(R.id.chat_voice);
	}
	
	public void showDialog() {
		if(dialog.isShowing()){
			dialog.cancel();
		}
		showRecordView();
		dialog.show();
	}
	
	public void cancelDialog() {
		if(dialog.isShowing()){
			dialog.cancel();
		}
	}
	
	public void setDialogImg(double amplitude) {
		Log.d("ss", "volume="+amplitude);
		int index = 100;
		if (0 <= amplitude && amplitude < index) {
			mDialogBackground.setImageResource(R.drawable.amp1);
			
		} else if (index <= amplitude && amplitude < index * 2) {
			mDialogBackground.setImageResource(R.drawable.amp2);
		
		} else if (index * 2 <= amplitude && amplitude < index * 3) {
			mDialogBackground.setImageResource(R.drawable.amp3);
		} else if (index * 3 <= amplitude && amplitude < index * 4) {
			mDialogBackground.setImageResource(R.drawable.amp4);
		} else if (index * 4 <= amplitude && amplitude < index * 5) {
			mDialogBackground.setImageResource(R.drawable.amp5);
		} else if (index * 5 <= amplitude && amplitude < index * 6) {
			mDialogBackground.setImageResource(R.drawable.amp6);
		} else if (index * 6 <= amplitude) {
			mDialogBackground.setImageResource(R.drawable.amp7);
		}
	}
//	public void setDialogImg(double amplitude) {
//		Log.d("ss", "volume="+amplitude);
//		int index = 2333;
//		if (0 <= amplitude && amplitude < index) {
//			mDialogBackground.setImageResource(R.drawable.amp1);
//		} else if (index <= amplitude && amplitude < index * 2) {
//			mDialogBackground.setImageResource(R.drawable.amp2);
//		} else if (index * 2 <= amplitude && amplitude < index * 3) {
//			mDialogBackground.setImageResource(R.drawable.amp2);
//		} else if (index * 3 <= amplitude && amplitude < index * 4) {
//			mDialogBackground.setImageResource(R.drawable.amp3);
//		} else if (index * 4 <= amplitude && amplitude < index * 5) {
//			mDialogBackground.setImageResource(R.drawable.amp3);
//		} else if (index * 5 <= amplitude && amplitude < index * 6) {
//			mDialogBackground.setImageResource(R.drawable.amp4);
//		} else if (index * 6 <= amplitude && amplitude < index * 7) {
//			mDialogBackground.setImageResource(R.drawable.amp4);
//		} else if (index * 7 <= amplitude && amplitude < index * 8) {
//			mDialogBackground.setImageResource(R.drawable.amp5);
//		} else if (index * 8 <= amplitude && amplitude < index * 9) {
//			mDialogBackground.setImageResource(R.drawable.amp5);
//		} else if (index * 9 <= amplitude && amplitude < index * 10) {
//			mDialogBackground.setImageResource(R.drawable.amp6);
//		} else if (index * 10 <= amplitude && amplitude < index * 11) {
//			mDialogBackground.setImageResource(R.drawable.amp6);
//		} else if (index * 11 <= amplitude && amplitude < index * 12) {
//			mDialogBackground.setImageResource(R.drawable.amp7);
//		}
//	}
	
	public void showCancalView() {
		mDialogBackground.setVisibility(View.GONE);
		recordView.setImageResource(R.drawable.icon_rec_dialog_recylebin);
		tvInfo.setText(R.string.remove_cancel_voice);
		tvInfo.setBackgroundResource(R.drawable.icon_dialog_rec_text_background);
	}
	
	public void showRecordView() {
		mDialogBackground.setVisibility(View.VISIBLE);
		tvInfo.setText(R.string.move_up_cancel_voice);
		tvInfo.setBackgroundColor(Color.TRANSPARENT);
		recordView.setImageResource(R.drawable.icon_rec_dialog_record);
	}
	
	public boolean isShowing() {
		return dialog.isShowing();
	}
	
	public View getDialogView () {
		return dialog.getWindow().findViewById(R.id.dialog_view);
	}
	
	private Rect dialogRect = new Rect();
	private int[] voicePos = new int[2];

	public boolean isPositionInDialog(int x, int y, View v) {
		int[] dialogPos = new int[2];
		View view = getDialogView();
		view.getLocationOnScreen(dialogPos);
		dialogRect.set(dialogPos[0], dialogPos[1], dialogPos[0] + view.getWidth(), dialogPos[1] + view.getHeight());
		v.getLocationOnScreen(voicePos);
		return dialogRect.contains(voicePos[0] + x, voicePos[1] + y);
	}

}

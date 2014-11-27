package com.gaopai.guiren.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.support.comment.CommentFeedback;
import com.gaopai.guiren.support.comment.CommentProfile;
import com.gaopai.guiren.support.comment.IComment;

public class CommentGeneralActivity extends BaseActivity {
	
	public static String KEY_TYPE = "type";
	public final static int TYPE_COMMENT_PROFILE = 0;
	public final static int TYPE_FEED_BACK = 1;
	

	private EditText etText;
	private Button btnComment;
	private IComment commenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stubs
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_comment_profile);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		etText = (EditText) findViewById(R.id.et_change_profile);
		btnComment = (Button) findViewById(R.id.btn_send);
		
		commenter = getCommenter();
		commenter.onCreat(this);
		commenter.setEdittext(etText);
		
		mTitleBar.setTitleText(commenter.getTitle());
		btnComment.setText(commenter.getBtnText());
		btnComment.setOnClickListener(commenter.getClickListener());
		etText.setHint(commenter.getHint());
	}
	
	private IComment getCommenter() {
		int type = getIntent().getIntExtra(KEY_TYPE, 0);
		if (type == TYPE_COMMENT_PROFILE) {
			return new CommentProfile();
		} else {
			return new CommentFeedback();
		}
	}
}

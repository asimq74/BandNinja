package com.asimq.artists.bandninja.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asimq.artists.bandninja.MusicItemsListFragment.OnMainActivityInteractionListener;
import com.asimq.artists.bandninja.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DrawerHeaderView extends LinearLayout {

	@BindView(R.id.addressText)
	TextView addressText;
	@BindView(R.id.nameTxt)
	TextView nameTxt;
	@BindView(R.id.profileImageView)
	ImageView profileImage;
	@BindView(R.id.searchByArtistEditView)
	CustomEditText searchByArtistEditTextView;
	@BindView(R.id.search_layout)
	View searchLayout;

	public DrawerHeaderView(Context context) {
		super(context);
	}

	public DrawerHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrawerHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public DrawerHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void bindTo(String name, String address) {
		hideOrSetText(this.nameTxt, name);
		hideOrSetText(this.addressText, address);
	}

	private void hideOrSetText(TextView tv, String text) {
		if (text == null || text.equals(""))
			tv.setVisibility(View.GONE);
		else
			tv.setText(text);
	}

	public void hideSearchLayout() {
		searchLayout.setVisibility(View.GONE);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.bind(this);
	}

	public void setUpSearchByArtistView(OnMainActivityInteractionListener onMainActivityInteractionListener) {
		searchByArtistEditTextView.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_close_clear_cancel, 0, android.R.drawable.ic_menu_search, 0);
		searchByArtistEditTextView.setDrawableClickListener(target -> {
			switch (target) {
				case RIGHT:
					onMainActivityInteractionListener.onSearchedForArtistName(searchByArtistEditTextView.getText().toString());
					searchByArtistEditTextView.getText().clear();
					onMainActivityInteractionListener.closeNavigationDrawer();
					break;
				case LEFT:
					searchByArtistEditTextView.getText().clear();
					onMainActivityInteractionListener.hideKeyboard();
					break;
				default:
					break;
			}
		});
		searchByArtistEditTextView.setOnKeyListener((v, keyCode, event) -> {
			// If the event is a key-down event on the "enter" button
			if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
					(keyCode == KeyEvent.KEYCODE_ENTER)) {
				onMainActivityInteractionListener.onSearchedForArtistName(searchByArtistEditTextView.getText().toString());
				searchByArtistEditTextView.getText().clear();
				onMainActivityInteractionListener.closeNavigationDrawer();
				return true;
			}
			return false;
		});
	}
}

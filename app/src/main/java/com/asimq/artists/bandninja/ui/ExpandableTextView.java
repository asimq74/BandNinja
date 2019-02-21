package com.asimq.artists.bandninja.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;

import com.asimq.artists.bandninja.R;
import com.asimq.artists.bandninja.utils.Util;

public class ExpandableTextView extends AppCompatTextView {

	private static final int DEFAULT_TRIM_LENGTH = 200;
	private static final String ELLIPSIS = ".....";
	private BufferType bufferType;
	private CharSequence originalText;
	private boolean trim = true;
	private int trimLength;
	private CharSequence trimmedText;

	public ExpandableTextView(Context context) {
		this(context, null);
	}

	public ExpandableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
		this.trimLength = typedArray.getInt(R.styleable.ExpandableTextView_trimLength, DEFAULT_TRIM_LENGTH);
		typedArray.recycle();

		setOnClickListener(v -> {
			trim = !trim;
			setText();
			requestFocusFromTouch();
		});
	}

	private CharSequence getDisplayableText() {
		return trim ? trimmedText : originalText;
	}

	public int getTrimLength() {
		return trimLength;
	}

	private CharSequence getTrimmedText() {
		if (originalText != null && originalText.length() > trimLength) {
			final String spannableText = getContext().getResources().getString(R.string.clickForMore);
			final SpannableStringBuilder thisCharacterSequence = new SpannableStringBuilder(originalText, 0, trimLength + 1)
					.append(ELLIPSIS).append("\n").append(spannableText);
			SpannableStringBuilder ssb = new SpannableStringBuilder(thisCharacterSequence);

			final String characterSequenceString = thisCharacterSequence.toString();
			if (characterSequenceString.contains(spannableText)) {

				ssb.setSpan(new Util.MySpannable(false) {
					@Override
					public void onClick(View widget) {// do nothing
					}
				}, characterSequenceString.indexOf(spannableText), characterSequenceString.indexOf(spannableText)
						+ spannableText.length(), 0);

			}
			return ssb;
		} else {
			return originalText;
		}
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		originalText = text.toString().trim();
		trimmedText = getTrimmedText();
		bufferType = type;
		setText();
	}

	private void setText() {
		super.setText(getDisplayableText(), bufferType);
	}

	public void setTrimLength(int trimLength) {
		this.trimLength = trimLength;
		trimmedText = getTrimmedText();
		setText();
	}
}

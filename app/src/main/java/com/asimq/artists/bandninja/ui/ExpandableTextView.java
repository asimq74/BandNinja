package com.asimq.artists.bandninja.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import com.asimq.artists.bandninja.R;
import com.asimq.artists.bandninja.utils.Util;
import com.asimq.artists.bandninja.utils.Util.MySpannable;

public class ExpandableTextView extends AppCompatTextView {
	private static final int DEFAULT_TRIM_LENGTH = 200;
	private static final String ELLIPSIS = ".....";

	private CharSequence originalText;
	private CharSequence trimmedText;
	private BufferType bufferType;
	private boolean trim = true;
	private int trimLength;

	public ExpandableTextView(Context context) {
		this(context, null);
	}

	public ExpandableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
		this.trimLength = typedArray.getInt(R.styleable.ExpandableTextView_trimLength, DEFAULT_TRIM_LENGTH);
		typedArray.recycle();

		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				trim = !trim;
				setText();
				requestFocusFromTouch();
			}
		});
	}

	private void setText() {
		super.setText(getDisplayableText(), bufferType);
	}

	private CharSequence getDisplayableText() {
		return trim ? trimmedText : originalText;
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		originalText = text.toString().trim();
		trimmedText = getTrimmedText(text);
		bufferType = type;
		setText();
	}

	private CharSequence getTrimmedText(CharSequence text) {
		if (originalText != null && originalText.length() > trimLength) {
			final String spannableText = getContext().getResources().getString(R.string.clickForMore);
			final SpannableStringBuilder thisCharacterSequence = new SpannableStringBuilder(originalText, 0, trimLength + 1)
					.append(ELLIPSIS).append("\n").append(spannableText);
			SpannableStringBuilder ssb = new SpannableStringBuilder(thisCharacterSequence);

			final String characterSequenceString = thisCharacterSequence.toString();
			if (characterSequenceString.contains(spannableText)) {

				ssb.setSpan(new Util.MySpannable(false) {
					@Override
					public void onClick(View widget) {
					}
				}, characterSequenceString.indexOf(spannableText), characterSequenceString.indexOf(spannableText)
						+ spannableText.length(), 0);

			}
			return ssb;
		} else {
			return originalText;
		}
	}

	public CharSequence getOriginalText() {
		return originalText;
	}

	public void setTrimLength(int trimLength) {
		this.trimLength = trimLength;
		trimmedText = getTrimmedText(originalText);
		setText();
	}

	public static class MySpannable extends ClickableSpan {

		private boolean isUnderline = true;

		/**
		 * Constructor
		 */
		public MySpannable(boolean isUnderline) {
			this.isUnderline = isUnderline;
		}

		@Override
		public void onClick(View widget) {

		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setUnderlineText(isUnderline);
			ds.setColor(Color.parseColor("#1b76d3"));
		}
	}

	public int getTrimLength() {
		return trimLength;
	}
}

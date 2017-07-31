package com.lordcard.ui.view;

import com.warrior.lord.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class NumberIconView extends View {
	private int num;
	private int gap = 0;
	private int[] numSplits = new int[0];
	private static String TAG = "NumberIconView";

	public NumberIconView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setNum(int num) {
		this.num = num;
		int len = String.valueOf(num).length();
		numSplits = new int[len];
		for (int i = 0; i < len; i++) {
			numSplits[len - 1 - i] = num % 10;
			num /= 10;
		}
		requestLayout();
	}

	public int getNum() {
		return num;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int height = getHeight();
		float offsetX = 0, offsetY = 0;
		for (int i = 0; i < numSplits.length; i++) {
			Drawable d = getResources().getDrawable(R.drawable.score_0 + numSplits[i]);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			canvas.translate(offsetX, offsetY);
			d.draw(canvas);
			canvas.translate(-offsetX, -offsetY);
			offsetX += d.getIntrinsicWidth() + gap;
			offsetY = (height - d.getIntrinsicHeight()) / 2f;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredWidth = 0;
		int measuredHeight = 0;
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (widthMode == MeasureSpec.EXACTLY) {
			measuredWidth = widthSize;
		} else {
			for (int i = 0; i < numSplits.length; i++) {
				Drawable d = getResources().getDrawable(R.drawable.score_0 + numSplits[i]);
				measuredWidth += i == numSplits.length - 1 ? d.getIntrinsicWidth() : d.getIntrinsicWidth() + gap;
			}
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			measuredHeight = heightSize;
		} else {
			for (int i = 0; i < numSplits.length; i++) {
				Drawable d = getResources().getDrawable(R.drawable.score_0 + numSplits[i]);
				measuredHeight = Math.max(measuredHeight, d.getIntrinsicHeight());
			}
		}
		setMeasuredDimension(measuredWidth, measuredHeight);
	}
}

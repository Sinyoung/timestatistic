package maximsblog.blogspot.com.timestatistic;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import maximsblog.blogspot.com.timestatistic.ColorPickerDialog.ColorPickerView;
import maximsblog.blogspot.com.timestatistic.R;

public class ColorPickerDialog extends Dialog implements android.view.View.OnClickListener {

	public interface OnColorChangedListener {
		void colorChanged(int color );
	}

	private OnColorChangedListener mListener;
	private int mInitialColor;
	private ColorPickerView mColorPickerView;

	public static class ColorPickerView extends View {
		private Paint mPaint;
		private Paint mCenterPaint;
		private Paint mRadialPaint;
		private final int[] mRadialColors;
		private OnColorChangedListener mListener;

		private Paint mGradientPaint;
		private int[] mLinearColors;
		
		public int getColor() {
			return mCenterPaint.getColor();
		}

		ColorPickerView(Context c, OnColorChangedListener l, int color) {
			super(c);
			mListener = l;
			mRadialColors = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF,
					0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };
			Shader s = new SweepGradient(0, 0, mRadialColors, null);

			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setShader(s);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(64);

			mLinearColors = getColors(color);
			Shader shader = new LinearGradient(0, 0, CENTER_X * 2, 0,
					mLinearColors, null, Shader.TileMode.CLAMP);

			mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mGradientPaint.setStyle(Paint.Style.STROKE);
			mGradientPaint.setShader(shader);
			mGradientPaint.setStrokeWidth(64);

			mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mCenterPaint.setColor(color);
			mCenterPaint.setStrokeWidth(12);

			mRadialPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mRadialPaint.setColor(color);
			mRadialPaint.setStrokeWidth(12);
		}

		private int[] getColors(int color) {
			if (color == Color.BLACK || color == Color.WHITE) {
				return new int[] { Color.BLACK, Color.WHITE };
			}
			return new int[] { Color.BLACK, color, Color.WHITE };
		}

		private boolean mTrackingCenter;
		private boolean mHighlightCenter;
		private boolean mTrackingLinGradient;

		@Override
		protected void onDraw(Canvas canvas) {
			float r = CENTER_X - mPaint.getStrokeWidth() * 0.5f;

			canvas.translate(CENTER_X, CENTER_X);

			canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
			canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);

			if (mTrackingCenter) {
				int color = mCenterPaint.getColor();
				mCenterPaint.setStyle(Paint.Style.STROKE);

				if (mHighlightCenter) {
					mCenterPaint.setAlpha(0xFF);
				} else {
					mCenterPaint.setAlpha(0x80);
				}
				canvas.drawCircle(0, 0,
						CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
						mCenterPaint);

				mCenterPaint.setStyle(Paint.Style.FILL);
				mCenterPaint.setColor(color);
			}

			int color = mRadialPaint.getColor();
			mLinearColors = getColors(color);
			Shader shader = new LinearGradient(0, 0, CENTER_X * 2, 0,
					mLinearColors, null, Shader.TileMode.CLAMP);
			mGradientPaint.setShader(shader);

			canvas.translate(-CENTER_X, 0);
			canvas.drawLine(0, r + 64, CENTER_X * 2, r + 64, mGradientPaint);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(CENTER_X * 2, CENTER_Y * 2 + 70);
		}

		private static final int CENTER_X = 200;
		private static final int CENTER_Y = 200;
		private static final int CENTER_RADIUS = 64;

		private int ave(int s, int d, float p) {
			return s + java.lang.Math.round(p * (d - s));
		}

		private int interpColor(int colors[], float unit) {
			if (unit <= 0) {
				return colors[0];
			}
			if (unit >= 1) {
				return colors[colors.length - 1];
			}

			float p = unit * (colors.length - 1);
			int i = (int) p;
			p -= i;

			// now p is just the fractional part [0...1) and i is the index
			int c0 = colors[i];
			int c1 = colors[i + 1];
			int a = ave(Color.alpha(c0), Color.alpha(c1), p);
			int r = ave(Color.red(c0), Color.red(c1), p);
			int g = ave(Color.green(c0), Color.green(c1), p);
			int b = ave(Color.blue(c0), Color.blue(c1), p);

			return Color.argb(a, r, g, b);
		}

		private static final float PI = 3.1415926f;

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX() - CENTER_X;
			float y = event.getY() - CENTER_Y;
			boolean inCenter = Math.sqrt(x * x + y * y) <= CENTER_RADIUS;
			boolean outOfRadialGradient = y > CENTER_X;

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mTrackingCenter = inCenter;
				mTrackingLinGradient = outOfRadialGradient;
				if (inCenter) {
					mHighlightCenter = true;
					invalidate();
					break;
				}
			case MotionEvent.ACTION_MOVE:
				if (mTrackingCenter) {
					if (mHighlightCenter != inCenter) {
						mHighlightCenter = inCenter;
						invalidate();
					}
				} else if (mTrackingLinGradient) {
					float unit = Math.max(0,
							Math.min(CENTER_X * 2, x + CENTER_X))
							/ (CENTER_X * 2);
					mCenterPaint.setColor(interpColor(mLinearColors, unit));
					invalidate();
				} else {
					float angle = (float) Math.atan2(y, x);
					// need to turn angle [-PI ... PI] into unit [0....1]
					float unit = angle / (2 * PI);
					if (unit < 0) {
						unit += 1;
					}
					int color = interpColor(mRadialColors, unit);
					mCenterPaint.setColor(color);
					mRadialPaint.setColor(color);
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mTrackingCenter) {
					if (inCenter) {
						mListener.colorChanged(mCenterPaint.getColor());
					}
					mTrackingCenter = false; // so we draw w/o halo
					invalidate();
				}
				break;
			}
			return true;
		}
	}

	public ColorPickerDialog(Context context, OnColorChangedListener listener,
			int initialColor) {
		super(context);

		mListener = listener;
		mInitialColor = initialColor;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OnColorChangedListener l = new OnColorChangedListener() {
			public void colorChanged(int color) {
				mListener.colorChanged(color);
				dismiss();
			}
		};
		setTitle(R.string.change_color);
		LinearLayout mainLayout = new LinearLayout(getContext());
		mainLayout.setGravity(Gravity.CENTER);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mColorPickerView = new ColorPickerView(getContext(), l, mInitialColor);
		mainLayout.addView(mColorPickerView);
		View v = this.getLayoutInflater().inflate(R.layout.color_picker_dialog, null, false);
		Button mOkButton = (Button)v.findViewById(R.id.ok_btn);
		Button mCancelButton = (Button)v.findViewById(R.id.cancel_btn);
		mOkButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);
		mainLayout.addView(v);
		setContentView(mainLayout);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id) {
		case R.id.ok_btn:
			int color = mColorPickerView.getColor();
			mListener.colorChanged(color);
			dismiss();
			break;
		case R.id.cancel_btn:
			dismiss();
			break;
		}
	}
}
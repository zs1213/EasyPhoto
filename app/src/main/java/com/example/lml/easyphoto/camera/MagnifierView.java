package com.example.lml.easyphoto.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.example.lml.easyphoto.R;

public class MagnifierView extends View {
	private static final int canvasop = 31;
	private static final float magnifyRatio = 2.0F;
	private Bitmap background;
	private boolean draw = false;
	private final Bitmap lens;
	private int lensWinH;
	private int lensWinW;
	private MapView mapView;
	private final Bitmap mask;
	private int vertOffset = 20;
	private float x;
	private float y;

	public MagnifierView(Context paramContext, MapView paramMapView) {
		super(paramContext);
		this.mapView = paramMapView;
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inPurgeable = true;
		this.lens = BitmapFactory.decodeResource(getResources(),
				R.drawable.magnify_lens, localOptions);
		this.lensWinW = this.lens.getWidth();
		this.lensWinH = this.lens.getHeight();
		this.mask = Bitmap.createBitmap(this.lensWinW, this.lensWinH,
				Bitmap.Config.ARGB_8888);
		Paint localPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		localPaint.setColor(Color.RED);
		new Canvas(this.mask).drawOval(
				new RectF(8.0F, 8.0F, this.lens.getWidth() - 9, this.lens
						.getHeight() - 9), localPaint);
	}

	public void hide() {
		this.draw = false;
		try {
			this.background.recycle();
			invalidate();
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDraw(Canvas paramCanvas) {
		if (!this.draw)
			return;
		try {
			Paint localPaint = new Paint();
			localPaint.setFilterBitmap(false);
			localPaint.setStyle(Paint.Style.FILL);
			int i = paramCanvas.saveLayer(2.0F + this.x, 2.0F + this.y, this.x
					+ this.lens.getWidth() - 2.0F,
					this.y + this.lens.getHeight() - 2.0F, null, Canvas.ALL_SAVE_FLAG);
			paramCanvas.translate(this.x, this.y);

			if ((this.background != null) && (!this.background.isRecycled()))
				paramCanvas.drawBitmap(this.background, 0.0F, 0.0F, localPaint);
			localPaint.setXfermode(new PorterDuffXfermode(
					PorterDuff.Mode.DST_IN));
			paramCanvas.drawBitmap(this.mask, 0.0F, 0.0F, localPaint);
			localPaint.setXfermode(null);
			paramCanvas.drawBitmap(this.lens, 0.0F, 0.0F, localPaint);
			paramCanvas.restoreToCount(i);
		} catch (Exception e) {
		}
	}

	public void prepareDrawingCacheAt(float paramFloat1, float paramFloat2) {
		float f1 = this.lensWinW / 2.0F;
		float f2 = this.lensWinH / 2.0F;
		Bitmap localBitmap = getBitmap(mapView);
		try {
			if (localBitmap != null) {
				this.draw = true;
				this.background = Bitmap.createScaledBitmap(localBitmap,
						(int) (2.0F * localBitmap.getWidth()),
						(int) (2.0F * localBitmap.getHeight()), false);
				this.x = (paramFloat1 - this.lensWinW / 2);
				this.y = (paramFloat2 - this.vertOffset - this.lensWinH);
				invalidate();
			}
		} catch (Exception e) {
		}
	}
	public Bitmap getBitmap(View view){
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		if (Build.VERSION.SDK_INT >= 11) {
			view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
			view.layout((int) view.getX(), (int) view.getY(), (int) view.getX() + view.getMeasuredWidth(), (int) view.getY() + view.getMeasuredHeight());
		} else {
			view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
			view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		}
		view.draw(canvas);
		return bitmap;
	}
}

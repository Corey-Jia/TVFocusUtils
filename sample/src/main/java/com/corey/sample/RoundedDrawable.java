/*
* Copyright (C) 2015 Vincent Mi
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.corey.sample;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.ImageView.ScaleType;

public class RoundedDrawable extends Drawable {

    public static final String TAG = "RoundedDrawable";
    public static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    private final RectF mBounds = new RectF();
    private final RectF mDrawableRect = new RectF();
    private final RectF mBitmapRect = new RectF();
    private final Bitmap mBitmap;
    private final Paint mBitmapPaint;
    private final int mBitmapWidth;
    private final int mBitmapHeight;
    private final RectF mBorderRect = new RectF();
    private final Paint mBorderPaint;
    private final Matrix mShaderMatrix = new Matrix();

    private BitmapShader mBitmapShader;
    private Shader.TileMode mTileModeX = Shader.TileMode.CLAMP;
    private Shader.TileMode mTileModeY = Shader.TileMode.CLAMP;
    private boolean mRebuildShader = true;

    private float mCornerRadius = 0;
    private boolean mOval = false;
    private float mBorderWidth = 0;
    private ColorStateList mBorderColor = ColorStateList
            .valueOf(DEFAULT_BORDER_COLOR);
    private ScaleType mScaleType = ScaleType.FIT_CENTER;

    public RoundedDrawable(Bitmap bitmap) {
        this.mBitmap = bitmap;

        this.mBitmapWidth = bitmap.getWidth();
        this.mBitmapHeight = bitmap.getHeight();
        this.mBitmapRect.set(0, 0, this.mBitmapWidth, this.mBitmapHeight);

        this.mBitmapPaint = new Paint();
        this.mBitmapPaint.setStyle(Paint.Style.FILL);
        this.mBitmapPaint.setAntiAlias(true);

        this.mBorderPaint = new Paint();
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
        this.mBorderPaint.setAntiAlias(true);
        this.mBorderPaint.setColor(this.mBorderColor.getColorForState(
                this.getState(), DEFAULT_BORDER_COLOR));
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
    }

    public static RoundedDrawable fromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            return new RoundedDrawable(bitmap);
        } else {
            return null;
        }
    }

    public static Drawable fromDrawable(Drawable drawable) {
        if (drawable != null) {
            if (drawable instanceof RoundedDrawable) {
                // just return if it's already a RoundedDrawable
                return drawable;
            } else if (drawable instanceof LayerDrawable) {
                LayerDrawable ld = (LayerDrawable) drawable;
                int num = ld.getNumberOfLayers();

                // loop through layers to and change to RoundedDrawables if
                // possible
                for (int i = 0; i < num; i++) {
                    Drawable d = ld.getDrawable(i);
                    ld.setDrawableByLayerId(ld.getId(i), fromDrawable(d));
                }
                return ld;
            }

            // try to get a bitmap from the drawable and
            Bitmap bm = drawableToBitmap(drawable);
            if (bm != null) {
                return new RoundedDrawable(bm);
            }
        }
        return drawable;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap;
        int width = Math.max(drawable.getIntrinsicWidth(), 2);
        int height = Math.max(drawable.getIntrinsicHeight(), 2);
        try {
            bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }

        return bitmap;
    }

    public Bitmap getSourceBitmap() {
        return this.mBitmap;
    }

    @Override
    public boolean isStateful() {
        return this.mBorderColor.isStateful();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        int newColor = this.mBorderColor.getColorForState(state, 0);
        if (this.mBorderPaint.getColor() != newColor) {
            this.mBorderPaint.setColor(newColor);
            return true;
        } else {
            return super.onStateChange(state);
        }
    }

    private void updateShaderMatrix() {
        float scale;
        float dx;
        float dy;

        switch (this.mScaleType) {
        case CENTER:
            this.mBorderRect.set(this.mBounds);
            this.mBorderRect.inset((this.mBorderWidth) / 2,
                    (this.mBorderWidth) / 2);

            this.mShaderMatrix.reset();
            this.mShaderMatrix
                    .setTranslate(
                            (int) ((this.mBorderRect.width() - this.mBitmapWidth) * 0.5f + 0.5f),
                            (int) ((this.mBorderRect.height() - this.mBitmapHeight) * 0.5f + 0.5f));
            break;

        case CENTER_CROP:
            this.mBorderRect.set(this.mBounds);
            this.mBorderRect.inset((this.mBorderWidth) / 2,
                    (this.mBorderWidth) / 2);

            this.mShaderMatrix.reset();

            dx = 0;
            dy = 0;

            if (this.mBitmapWidth * this.mBorderRect.height() > this.mBorderRect
                    .width() * this.mBitmapHeight) {
                scale = this.mBorderRect.height() / this.mBitmapHeight;
                dx = (this.mBorderRect.width() - this.mBitmapWidth * scale) * 0.5f;
            } else {
                scale = this.mBorderRect.width() / this.mBitmapWidth;
                dy = (this.mBorderRect.height() - this.mBitmapHeight * scale) * 0.5f;
            }

            this.mShaderMatrix.setScale(scale, scale);
            this.mShaderMatrix.postTranslate((int) (dx + 0.5f)
                    + this.mBorderWidth, (int) (dy + 0.5f) + this.mBorderWidth);
            break;

        case CENTER_INSIDE:
            this.mShaderMatrix.reset();

            if (this.mBitmapWidth <= this.mBounds.width()
                    && this.mBitmapHeight <= this.mBounds.height()) {
                scale = 1.0f;
            } else {
                scale = Math.min(this.mBounds.width() / this.mBitmapWidth,
                        this.mBounds.height() / this.mBitmapHeight);
            }

            dx = (int) ((this.mBounds.width() - this.mBitmapWidth * scale) * 0.5f + 0.5f);
            dy = (int) ((this.mBounds.height() - this.mBitmapHeight * scale) * 0.5f + 0.5f);

            this.mShaderMatrix.setScale(scale, scale);
            this.mShaderMatrix.postTranslate(dx, dy);

            this.mBorderRect.set(this.mBitmapRect);
            this.mShaderMatrix.mapRect(this.mBorderRect);
            this.mBorderRect.inset((this.mBorderWidth) / 2,
                    (this.mBorderWidth) / 2);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect,
                    this.mBorderRect, Matrix.ScaleToFit.FILL);
            break;

        default:
        case FIT_CENTER:
            this.mBorderRect.set(this.mBitmapRect);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBounds,
                    Matrix.ScaleToFit.CENTER);
            this.mShaderMatrix.mapRect(this.mBorderRect);
            this.mBorderRect.inset((this.mBorderWidth) / 2,
                    (this.mBorderWidth) / 2);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect,
                    this.mBorderRect, Matrix.ScaleToFit.FILL);
            break;

        case FIT_END:
            this.mBorderRect.set(this.mBitmapRect);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBounds,
                    Matrix.ScaleToFit.END);
            this.mShaderMatrix.mapRect(this.mBorderRect);
            this.mBorderRect.inset((this.mBorderWidth) / 2,
                    (this.mBorderWidth) / 2);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect,
                    this.mBorderRect, Matrix.ScaleToFit.FILL);
            break;

        case FIT_START:
            this.mBorderRect.set(this.mBitmapRect);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBounds,
                    Matrix.ScaleToFit.START);
            this.mShaderMatrix.mapRect(this.mBorderRect);
            this.mBorderRect.inset((this.mBorderWidth) / 2,
                    (this.mBorderWidth) / 2);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect,
                    this.mBorderRect, Matrix.ScaleToFit.FILL);
            break;

        case FIT_XY:
            this.mBorderRect.set(this.mBounds);
            this.mBorderRect.inset((this.mBorderWidth) / 2,
                    (this.mBorderWidth) / 2);
            this.mShaderMatrix.reset();
            this.mShaderMatrix.setRectToRect(this.mBitmapRect,
                    this.mBorderRect, Matrix.ScaleToFit.FILL);
            break;
        }

        this.mDrawableRect.set(this.mBorderRect);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        this.mBounds.set(bounds);

        this.updateShaderMatrix();
    }

    @Override
    public void draw(Canvas canvas) {
        if (this.mRebuildShader) {
            this.mBitmapShader = new BitmapShader(this.mBitmap,
                    this.mTileModeX, this.mTileModeY);
            if (this.mTileModeX == Shader.TileMode.CLAMP
                    && this.mTileModeY == Shader.TileMode.CLAMP) {
                this.mBitmapShader.setLocalMatrix(this.mShaderMatrix);
            }
            this.mBitmapPaint.setShader(this.mBitmapShader);
            this.mRebuildShader = false;
        }

        if (this.mOval) {
            if (this.mBorderWidth > 0) {
                canvas.drawOval(this.mDrawableRect, this.mBitmapPaint);
                canvas.drawOval(this.mBorderRect, this.mBorderPaint);
            } else {
                canvas.drawOval(this.mDrawableRect, this.mBitmapPaint);
            }
        } else {
            if (this.mBorderWidth > 0) {
                canvas.drawRoundRect(this.mDrawableRect,
                        Math.max(this.mCornerRadius, 0),
                        Math.max(this.mCornerRadius, 0), this.mBitmapPaint);
                canvas.drawRoundRect(this.mBorderRect, this.mCornerRadius,
                        this.mCornerRadius, this.mBorderPaint);
            } else {
                canvas.drawRoundRect(this.mDrawableRect, this.mCornerRadius,
                        this.mCornerRadius, this.mBitmapPaint);
            }
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        this.mBitmapPaint.setAlpha(alpha);
        this.invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        this.mBitmapPaint.setColorFilter(cf);
        this.invalidateSelf();
    }

    @Override
    public void setDither(boolean dither) {
        this.mBitmapPaint.setDither(dither);
        this.invalidateSelf();
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        this.mBitmapPaint.setFilterBitmap(filter);
        this.invalidateSelf();
    }

    @Override
    public int getIntrinsicWidth() {
        return this.mBitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return this.mBitmapHeight;
    }

    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    public RoundedDrawable setCornerRadius(float radius) {
        this.mCornerRadius = radius;
        return this;
    }

    public float getBorderWidth() {
        return this.mBorderWidth;
    }

    public RoundedDrawable setBorderWidth(float width) {
        this.mBorderWidth = width;
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
        return this;
    }

    public int getBorderColor() {
        return this.mBorderColor.getDefaultColor();
    }

    public RoundedDrawable setBorderColor(int color) {
        return this.setBorderColor(ColorStateList.valueOf(color));
    }

    public ColorStateList getBorderColors() {
        return this.mBorderColor;
    }

    public RoundedDrawable setBorderColor(ColorStateList colors) {
        this.mBorderColor = colors != null ? colors : ColorStateList.valueOf(0);
        this.mBorderPaint.setColor(this.mBorderColor.getColorForState(
                this.getState(), DEFAULT_BORDER_COLOR));
        return this;
    }

    public boolean isOval() {
        return this.mOval;
    }

    public RoundedDrawable setOval(boolean oval) {
        this.mOval = oval;
        return this;
    }

    public ScaleType getScaleType() {
        return this.mScaleType;
    }

    public RoundedDrawable setScaleType(ScaleType scaleType) {
        if (scaleType == null) {
            scaleType = ScaleType.FIT_CENTER;
        }
        if (this.mScaleType != scaleType) {
            this.mScaleType = scaleType;
            this.updateShaderMatrix();
        }
        return this;
    }

    public Shader.TileMode getTileModeX() {
        return this.mTileModeX;
    }

    public RoundedDrawable setTileModeX(Shader.TileMode tileModeX) {
        if (this.mTileModeX != tileModeX) {
            this.mTileModeX = tileModeX;
            this.mRebuildShader = true;
            this.invalidateSelf();
        }
        return this;
    }

    public Shader.TileMode getTileModeY() {
        return this.mTileModeY;
    }

    public RoundedDrawable setTileModeY(Shader.TileMode tileModeY) {
        if (this.mTileModeY != tileModeY) {
            this.mTileModeY = tileModeY;
            this.mRebuildShader = true;
            this.invalidateSelf();
        }
        return this;
    }

    public Bitmap toBitmap() {
        return drawableToBitmap(this);
    }
}

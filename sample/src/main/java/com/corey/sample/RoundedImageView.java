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

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
public class RoundedImageView extends ImageView {

    // Constants for tile mode attributes
    private static final int TILE_MODE_UNDEFINED = -2;
    private static final int TILE_MODE_CLAMP = 0;
    private static final int TILE_MODE_REPEAT = 1;
    private static final int TILE_MODE_MIRROR = 2;

    public static final String TAG = "RoundedImageView";
    public static final float DEFAULT_RADIUS = 0f;
    public static final float DEFAULT_BORDER_WIDTH = 0f;
    public static final Shader.TileMode DEFAULT_TILE_MODE = Shader.TileMode.CLAMP;
    private static final ScaleType[] SCALE_TYPES = { ScaleType.MATRIX,
            ScaleType.FIT_XY, ScaleType.FIT_START, ScaleType.FIT_CENTER,
            ScaleType.FIT_END, ScaleType.CENTER, ScaleType.CENTER_CROP,
            ScaleType.CENTER_INSIDE };

    private float cornerRadius = DEFAULT_RADIUS;
    private float borderWidth = DEFAULT_BORDER_WIDTH;
    private ColorStateList borderColor = ColorStateList
            .valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
    private boolean isOval = false;
    private boolean mutateBackground = false;
    private Shader.TileMode tileModeX = DEFAULT_TILE_MODE;
    private Shader.TileMode tileModeY = DEFAULT_TILE_MODE;

    private ColorFilter mColorFilter = null;
    private boolean mHasColorFilter = false;
    private boolean mColorMod = false;

    private int mResource;
    private Drawable mDrawable;
    private Drawable mBackgroundDrawable;

    private ScaleType mScaleType;

    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RoundedImageView, defStyle, 0);

        int index = a
                .getInt(R.styleable.RoundedImageView_android_scaleType, -1);
        if (index >= 0) {
            this.setScaleType(SCALE_TYPES[index]);
        } else {
            // default scaletype to FIT_CENTER
            this.setScaleType(ScaleType.FIT_CENTER);
        }

        this.cornerRadius = a.getDimensionPixelSize(
                R.styleable.RoundedImageView_riv_corner_radius, -1);
        this.borderWidth = a.getDimensionPixelSize(
                R.styleable.RoundedImageView_riv_border_width, -1);

        // don't allow negative values for radius and border
        if (this.cornerRadius < 0) {
            this.cornerRadius = DEFAULT_RADIUS;
        }
        if (this.borderWidth < 0) {
            this.borderWidth = DEFAULT_BORDER_WIDTH;
        }

        this.borderColor = a
                .getColorStateList(R.styleable.RoundedImageView_riv_border_color);
        if (this.borderColor == null) {
            this.borderColor = ColorStateList
                    .valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
        }

        this.mutateBackground = a.getBoolean(
                R.styleable.RoundedImageView_riv_mutate_background, false);
        this.isOval = a
                .getBoolean(R.styleable.RoundedImageView_riv_oval, false);

        final int tileMode = a
                .getInt(R.styleable.RoundedImageView_riv_tile_mode,
                        TILE_MODE_UNDEFINED);
        if (tileMode != TILE_MODE_UNDEFINED) {
            this.setTileModeX(parseTileMode(tileMode));
            this.setTileModeY(parseTileMode(tileMode));
        }

        final int tileModeX = a.getInt(
                R.styleable.RoundedImageView_riv_tile_mode_x,
                TILE_MODE_UNDEFINED);
        if (tileModeX != TILE_MODE_UNDEFINED) {
            this.setTileModeX(parseTileMode(tileModeX));
        }

        final int tileModeY = a.getInt(
                R.styleable.RoundedImageView_riv_tile_mode_y,
                TILE_MODE_UNDEFINED);
        if (tileModeY != TILE_MODE_UNDEFINED) {
            this.setTileModeY(parseTileMode(tileModeY));
        }

        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(true);

        a.recycle();
    }

    private static Shader.TileMode parseTileMode(int tileMode) {
        switch (tileMode) {
        case TILE_MODE_CLAMP:
            return Shader.TileMode.CLAMP;
        case TILE_MODE_REPEAT:
            return Shader.TileMode.REPEAT;
        case TILE_MODE_MIRROR:
            return Shader.TileMode.MIRROR;
        default:
            return null;
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.invalidate();
    }

    /**
     * Return the current scale type in use by this ImageView.
     * @attr ref android.R.styleable#ImageView_scaleType
     * @see ScaleType
     */
    @Override
    public ScaleType getScaleType() {
        return this.mScaleType;
    }

    /**
     * Controls how the image should be resized or moved to match the size
     * of this ImageView.
     * @param scaleType
     *            The desired scaling mode.
     * @attr ref android.R.styleable#ImageView_scaleType
     */
    @Override
    public void setScaleType(ScaleType scaleType) {
        assert scaleType != null;

        if (this.mScaleType != scaleType) {
            this.mScaleType = scaleType;

            switch (scaleType) {
            case CENTER:
            case CENTER_CROP:
            case CENTER_INSIDE:
            case FIT_CENTER:
            case FIT_START:
            case FIT_END:
            case FIT_XY:
                super.setScaleType(ScaleType.FIT_XY);
                break;
            default:
                super.setScaleType(scaleType);
                break;
            }

            this.updateDrawableAttrs();
            this.updateBackgroundDrawableAttrs(false);
            this.invalidate();
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        this.mResource = 0;
        this.mDrawable = RoundedDrawable.fromDrawable(drawable);
        this.updateDrawableAttrs();
        super.setImageDrawable(this.mDrawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        this.mResource = 0;
        this.mDrawable = RoundedDrawable.fromBitmap(bm);
        this.updateDrawableAttrs();
        super.setImageDrawable(this.mDrawable);
    }

    @Override
    public void setImageResource(int resId) {
        if (this.mResource != resId) {
            this.mResource = resId;
            this.mDrawable = this.resolveResource();
            this.updateDrawableAttrs();
            super.setImageDrawable(this.mDrawable);
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        this.setImageDrawable(this.getDrawable());
    }

    private Drawable resolveResource() {
        Resources rsrc = this.getResources();
        if (rsrc == null) {
            return null;
        }

        Drawable d = null;

        if (this.mResource != 0) {
            try {
                d = rsrc.getDrawable(this.mResource);
            } catch (Exception e) {
                Log.w(TAG, "Unable to find resource: " + this.mResource, e);
                // Don't try again.
                this.mResource = 0;
            }
        }
        return RoundedDrawable.fromDrawable(d);
    }

    @Override
    public void setBackground(Drawable background) {
        this.setBackgroundDrawable(background);
    }

    private void updateDrawableAttrs() {
        this.updateAttrs(this.mDrawable);
    }

    private void updateBackgroundDrawableAttrs(boolean convert) {
        if (this.mutateBackground) {
            if (convert) {
                this.mBackgroundDrawable = RoundedDrawable
                        .fromDrawable(this.mBackgroundDrawable);
            }
            this.updateAttrs(this.mBackgroundDrawable);
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (this.mColorFilter != cf) {
            this.mColorFilter = cf;
            this.mHasColorFilter = true;
            this.mColorMod = true;
            this.applyColorMod();
            this.invalidate();
        }
    }

    private void applyColorMod() {
        // Only mutate and apply when modifications have occurred. This should
        // not reset the mColorMod flag, since these filters need to be
        // re-applied if the Drawable is changed.
        if (this.mDrawable != null && this.mColorMod) {
            this.mDrawable = this.mDrawable.mutate();
            if (this.mHasColorFilter) {
                this.mDrawable.setColorFilter(this.mColorFilter);
            }
            // TODO: support, eventually...
            // mDrawable.setXfermode(mXfermode);
            // mDrawable.setAlpha(mAlpha * mViewAlphaScale >> 8);
        }
    }

    private void updateAttrs(Drawable drawable) {
        if (drawable == null) {
            return;
        }

        if (drawable instanceof RoundedDrawable) {
            ((RoundedDrawable) drawable).setScaleType(this.mScaleType)
                    .setCornerRadius(this.cornerRadius)
                    .setBorderWidth(this.borderWidth)
                    .setBorderColor(this.borderColor).setOval(this.isOval)
                    .setTileModeX(this.tileModeX).setTileModeY(this.tileModeY);
            this.applyColorMod();
        } else if (drawable instanceof LayerDrawable) {
            // loop through layers to and set drawable attrs
            LayerDrawable ld = ((LayerDrawable) drawable);
            for (int i = 0, layers = ld.getNumberOfLayers(); i < layers; i++) {
                this.updateAttrs(ld.getDrawable(i));
            }
        }
    }

    @Override
    @Deprecated
    public void setBackgroundDrawable(Drawable background) {
        this.mBackgroundDrawable = background;
        this.updateBackgroundDrawableAttrs(true);
        super.setBackgroundDrawable(this.mBackgroundDrawable);
    }

    public float getCornerRadius() {
        return this.cornerRadius;
    }

    public void setCornerRadiusDimen(int resId) {
        this.setCornerRadius(this.getResources().getDimension(resId));
    }

    public void setCornerRadius(float radius) {
        if (this.cornerRadius == radius) {
            return;
        }

        this.cornerRadius = radius;
        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(false);
        this.invalidate();
    }

    public float getBorderWidth() {
        return this.borderWidth;
    }

    public void setBorderWidth(int resId) {
        this.setBorderWidth(this.getResources().getDimension(resId));
    }

    public void setBorderWidth(float width) {
        if (this.borderWidth == width) {
            return;
        }

        this.borderWidth = width;
        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(false);
        this.invalidate();
    }

    public int getBorderColor() {
        return this.borderColor.getDefaultColor();
    }

    public void setBorderColor(int color) {
        this.setBorderColor(ColorStateList.valueOf(color));
    }

    public ColorStateList getBorderColors() {
        return this.borderColor;
    }

    public void setBorderColor(ColorStateList colors) {
        if (this.borderColor.equals(colors)) {
            return;
        }

        this.borderColor = (colors != null) ? colors : ColorStateList
                .valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(false);
        if (this.borderWidth > 0) {
            this.invalidate();
        }
    }

    public boolean isOval() {
        return this.isOval;
    }

    public void setOval(boolean oval) {
        this.isOval = oval;
        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(false);
        this.invalidate();
    }

    public Shader.TileMode getTileModeX() {
        return this.tileModeX;
    }

    public void setTileModeX(Shader.TileMode tileModeX) {
        if (this.tileModeX == tileModeX) {
            return;
        }

        this.tileModeX = tileModeX;
        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(false);
        this.invalidate();
    }

    public Shader.TileMode getTileModeY() {
        return this.tileModeY;
    }

    public void setTileModeY(Shader.TileMode tileModeY) {
        if (this.tileModeY == tileModeY) {
            return;
        }

        this.tileModeY = tileModeY;
        this.updateDrawableAttrs();
        this.updateBackgroundDrawableAttrs(false);
        this.invalidate();
    }

    public boolean mutatesBackground() {
        return this.mutateBackground;
    }

    public void mutateBackground(boolean mutate) {
        if (this.mutateBackground == mutate) {
            return;
        }

        this.mutateBackground = mutate;
        this.updateBackgroundDrawableAttrs(true);
        this.invalidate();
    }
}

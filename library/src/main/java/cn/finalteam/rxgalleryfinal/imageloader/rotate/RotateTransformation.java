package cn.finalteam.rxgalleryfinal.imageloader.rotate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * Created by pengjianbo  Dujinyang on 2016/8/16 0016.
 */
public class RotateTransformation extends BitmapTransformation {

    private float rotateRotationAngle = 0f;

    public RotateTransformation(float rotateRotationAngle) {

        this.rotateRotationAngle = rotateRotationAngle;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateRotationAngle);
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }

    public String getId() {
        return "rotate" + rotateRotationAngle;
    }
}

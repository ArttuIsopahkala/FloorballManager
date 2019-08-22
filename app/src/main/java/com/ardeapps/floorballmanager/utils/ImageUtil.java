package com.ardeapps.floorballmanager.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.ardeapps.floorballmanager.AppRes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {
    private static String TAG = "ImageUtil";

    public static RoundedBitmapDrawable getRoundedDrawable(Bitmap bitmap) {
        int dimension = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);

        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(AppRes.getContext().getResources(), output);
        dr.setAntiAlias(true);
        dr.setCornerRadius(Math.max(output.getWidth(), output.getHeight()) / 2.0f);
        return dr;
    }

    public static Bitmap getSquarePicture(Bitmap bitmap) {
        int dimension = Math.min(bitmap.getWidth(), bitmap.getHeight());
        return ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    private static String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = AppRes.getContext().getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static int getImageRotationTag(Uri photoUri) throws IOException {
        // Decides image url for camera or gallery
        String path = photoUri.getPath();
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = AppRes.getContext().getContentResolver().query(photoUri, proj, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        ExifInterface ei = new ExifInterface(path);
        return ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
    }

    public static Bitmap scaleImageForUpload(Uri photoUri) {
        try {
            Context context = AppRes.getContext();
            int MAX_IMAGE_DIMENSION = 480;
            InputStream is = context.getContentResolver().openInputStream(photoUri);
            if (is == null) {
                return null;
            }
            BitmapFactory.Options dbo = new BitmapFactory.Options();
            dbo.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, dbo);
            is.close();

            int orientation = 0;
            int orientation_tag = getImageRotationTag(photoUri);
            switch (orientation_tag) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;
                    break;
            }

            int rotatedWidth, rotatedHeight;
            if (orientation_tag == ExifInterface.ORIENTATION_ROTATE_90 || orientation_tag == ExifInterface.ORIENTATION_ROTATE_270) {
                rotatedWidth = dbo.outWidth;
                rotatedHeight = dbo.outHeight;
            } else {
                rotatedWidth = dbo.outWidth;
                rotatedHeight = dbo.outHeight;
            }

            Bitmap srcBitmap = null;
            is = context.getContentResolver().openInputStream(photoUri);
            if (is != null) {
                if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
                    float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
                    float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
                    float maxRatio = Math.max(widthRatio, heightRatio);

                    // Create the bitmap from file
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = (int) maxRatio;
                    srcBitmap = BitmapFactory.decodeStream(is, null, options);
                } else {
                    srcBitmap = BitmapFactory.decodeStream(is);
                }
                is.close();
            }

            if (srcBitmap == null) {
                return null;
            }

            if (orientation > 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);

                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                        srcBitmap.getHeight(), matrix, true);
            }

            srcBitmap = getSquarePicture(srcBitmap);

            String type = context.getContentResolver().getType(photoUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (type == null) {
                srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            } else if (type.equals("image/png")) {
                srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
                srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }

            byte[] bMapArray = baos.toByteArray();
            baos.close();
            return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
        } catch (IOException e) {
            return null;
        }
    }

    public static Bitmap getDrawableAsBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void fadeImageIn(ImageView view) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(300);
        view.startAnimation(fadeIn);
    }

    public static String getBitmapAsBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap getBase64AsBitmap(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

}

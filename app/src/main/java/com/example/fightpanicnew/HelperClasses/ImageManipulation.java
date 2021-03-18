/*
Pomoćne klase i biblioteke za razne manipulacije bitmapima, drawablesima i ostalo. Kompresije slika, mjenjanje veličine, učitavanje
pomoću Picassa i Glide-a.
*/

package com.example.fightpanicnew.HelperClasses;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.fightpanicnew.Entity.User;
import com.example.fightpanicnew.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ImageManipulation {

    public static String saveToInternalStorage(Bitmap bitmapImage, Context context, User user) {
        String pictureName = user.getUsername() + "profilePicture";

        byte[] pictureNameByte = new byte[0];
        try {
            pictureNameByte = pictureName.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String base64EncodedPictureName = android.util.Base64.encodeToString(pictureNameByte, Base64.NO_WRAP);
        String random = UUID.randomUUID().toString();
        String tmp = android.util.Base64.encodeToString(random.getBytes(), Base64.NO_WRAP);
        String finalPictureName = base64EncodedPictureName + tmp;

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, finalPictureName + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(fos).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return myPath.toString();
    }

    public static boolean loadImageFromStoragePicassoInvalidate(String path, ImageView view) {

        Picasso.get().invalidate("file://" + path);
        Picasso.get().load("file://" + path).placeholder(R.drawable.ic_person).fit().centerCrop().into(view);

        return true;
    }

    public static void loadImageFromStoragePicasso(String path, ImageView view) {
        Picasso.get().load("file://" + path).placeholder(R.drawable.ic_person).fit().centerCrop().into(view);
    }

    public static String saveToInternalStorage(String content, Context context) {

        byte[] b = Base64.decode(content, Base64.DEFAULT);
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(b, 0, b.length);

        String pictureName = UUID.randomUUID().toString() + "profilePicture";

        byte[] pictureNameByte = new byte[0];
        try {
            pictureNameByte = pictureName.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String base64EncodedPictureName = android.util.Base64.encodeToString(pictureNameByte, Base64.NO_WRAP);
        String random = UUID.randomUUID().toString();
        String tmp = android.util.Base64.encodeToString(random.getBytes(), Base64.NO_WRAP);
        String finalPictureName = base64EncodedPictureName + tmp;

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, finalPictureName + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(fos).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return myPath.toString();
    }

    public static String saveToInternalStorageChat(String content, Context context) {

        byte[] b = Base64.decode(content, Base64.DEFAULT);

        Bitmap bitmapImage = decodeSampledBitmap(b, 500, 500);

        String pictureName = UUID.randomUUID().toString() + "profilePicture";

        byte[] pictureNameByte = new byte[0];
        try {
            pictureNameByte = pictureName.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String base64EncodedPictureName = android.util.Base64.encodeToString(pictureNameByte, Base64.NO_WRAP);
        String random = UUID.randomUUID().toString();
        String tmp = android.util.Base64.encodeToString(random.getBytes(), Base64.NO_WRAP);
        String finalPictureName = base64EncodedPictureName + tmp;

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, finalPictureName + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 75, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(fos).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return myPath.toString();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmap(byte[] b, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(b, 0, b.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(b, 0, b.length, options);
    }

    public static String saveToInternalStorage(Bitmap bitmapImage, Context context) {

        String pictureName = UUID.randomUUID().toString() + "profilePicture";

        byte[] pictureNameByte = new byte[0];
        try {
            pictureNameByte = pictureName.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String base64EncodedPictureName = android.util.Base64.encodeToString(pictureNameByte, Base64.NO_WRAP);
        String random = UUID.randomUUID().toString();
        String tmp = android.util.Base64.encodeToString(random.getBytes(), Base64.NO_WRAP);
        String finalPictureName = base64EncodedPictureName + tmp;

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, finalPictureName + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 75, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(fos).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return myPath.toString();
    }

    public static String saveToInternalStorage(Bitmap bitmapImage, Context context, String finalPictureName) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, finalPictureName + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(fos).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return myPath.toString();
    }

    public static boolean saveToInternalStorageOverwrite(Bitmap bitmapImage, Context context, String finalPictureName) {

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, finalPictureName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(fos).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static String compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
        return stream.toByteArray();
    }

    public static Bitmap getBitmapFromDrawable(Drawable drawable, Context context) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof RoundedBitmapDrawable) {
            return ((RoundedBitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (drawable instanceof VectorDrawable) {
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.imageFormatNotSupported), Toast.LENGTH_SHORT).show();
            return null;
        }
        return null;
    }

    public static Bitmap getAvatarFromUrl(Context context, String url) {

        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context).asBitmap().load(url).transform(new CircleCrop()).submit(100, 100).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

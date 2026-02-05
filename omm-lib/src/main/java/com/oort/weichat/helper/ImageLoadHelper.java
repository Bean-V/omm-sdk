package com.oort.weichat.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.oort.weichat.db.dao.UserAvatarDao;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

;

public class ImageLoadHelper {
    private static final String TAG = "ImageLoadHelper";

    public static void loadBitmapDontAnimateWithPlaceHolder(
            Context ctx,
            String url,
            @DrawableRes int placeholder,
            @DrawableRes int error,
            BitmapSuccessCallback onSuccess,
            ImageFailedCallback onError
    ) {
        loadBitmap(ctx, url, placeholder, error, false,
                true, false, false, onSuccess, onError);
    }

    public static void loadBitmapDontAnimate(
            Context ctx,
            String url,
            BitmapSuccessCallback onSuccess,
            ImageFailedCallback onError
    ) {
        loadBitmap(ctx, url, null, null, false,
                true, false, false, onSuccess, onError);
    }

    public static void loadBitmapCenterCropDontAnimate(
            Context ctx,
            String url,
            BitmapSuccessCallback onSuccess,
            ImageFailedCallback onError
    ) {
        loadBitmap(ctx, url, null, null, true,
                true, false, false, onSuccess, onError);
    }


    public static void loadBitmapCodeAnimate(
            Context ctx,
            String url,
            BitmapSuccessCallback onSuccess,
            ImageFailedCallback onError
    ) {
        loadBitmap(ctx, url, null, null, true,
                true, true, true, onSuccess, onError);
    }


    public static void loadBitmapCenterCropDontAnimateWithError(
            Context ctx,
            String url,
            @DrawableRes int error,
            BitmapSuccessCallback onSuccess,
            ImageFailedCallback onError
    ) {
        loadBitmap(ctx, url, null, error, true,
                true, false, false, onSuccess, onError);
    }

    private static void loadBitmap(
            Context ctx,
            String url,
            @DrawableRes Integer placeholder,
            @DrawableRes Integer error,
            boolean centerCrop,
            boolean dontAnimate,
            boolean skipMemoryCache,
            boolean skipDiskCache,
            BitmapSuccessCallback onSuccess,
            ImageFailedCallback onError
    ) {

        RequestOptions options = new RequestOptions();

        if (placeholder != null) {
            options.placeholder(placeholder);
        }
        if (error != null) {
            options.error(error);
        }
        if (centerCrop) {
            options.centerCrop();
        }
        if (dontAnimate) {
            options.dontAnimate();
        }
        if (skipMemoryCache) {
            options.skipMemoryCache(true);
        }
        if (skipDiskCache) {
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
        }



        Glide.with(ctx).asBitmap()
                .load(url)
                .apply(options)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        onSuccess.onSuccess(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Exception e = new Exception("fail");
                        onError.onFailed(e);
                    }
                });

    }

    public static void loadImageDontAnimateWithPlaceholder(
            Context ctx,
            String url,
            @DrawableRes Integer placeholder,
            DrawableSuccessCallback onSuccess,
            ImageFailedCallback onError
    ) {
        loadImage(ctx, url, placeholder, null, null, true,
                onSuccess, onError);
    }

    public static void loadImageSignatureDontAnimateWithPlaceHolder(
            Context ctx,
            String url,
            @DrawableRes int placeholder,
            String signature,
            DrawableSuccessCallback onSuccess,
            ImageFailedCallback onError
    ) {
        loadImage(ctx, url, placeholder, null, signature, true,
                onSuccess, onError);
    }

    private static void loadImage(
            Context ctx,
            String url,
            @DrawableRes Integer placeholder,
            @DrawableRes Integer error,
            String signature,
            boolean dontAnimate,
            DrawableSuccessCallback onSuccess,
            ImageFailedCallback onError
    ) {


        RequestOptions options = new RequestOptions();

        if (placeholder != null) {
            options.placeholder(placeholder);
        }
        if (error != null) {
            options.error(error);
        }
        if (signature != null) {
            options.signature(new ObjectKey(signature));
        }
        if (dontAnimate) {
            options.dontAnimate();
        }
        Glide.with(ctx)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        onError.onFailed(e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        onSuccess.onSuccess(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Exception e = new Exception("fail");
                        onError.onFailed(e);
                    }
                });
    }

    @WorkerThread
    public static Bitmap getBitmapCenterCrop(
            Context ctx,
            String url,
            String userId,
            int width,
            int height
    ) throws ExecutionException, InterruptedException {

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        RequestBuilder builder = Glide.with(ctx).asBitmap()
                .load(url)
                .apply(options)
                .signature((new ObjectKey(UserAvatarDao.getInstance().getUpdateTime(userId))))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                });
        return (Bitmap) builder.submit(width, height).get();
    }

    public static void showImageDontAnimateWithPlaceHolder(
            Context ctx,
            String url,
            @DrawableRes int placeholder,
            @DrawableRes int error,
            ImageView view
    ) {
        showImage(ctx, url, placeholder, error, null, null, null, false,
                false, true, view);
    }

    public static void showImageDontAnimateWithError(
            Context ctx,
            String url,
            @DrawableRes int error,
            ImageView view
    ) {
        showImage(ctx, url, null, error, null, null, null, false,
                false, true, view);
    }

    public static void showFileCenterCropWithSizePlaceHolder(
            Context ctx,
            File file,
            @DrawableRes int placeholder,
            @DrawableRes int error,
            int width,
            int height,
            ImageView view
    ) {
        showImage(ctx, file, placeholder, error, width, height, null, true,
                false, false, view);
    }

    public static void showFileCenterCropWithSizeError(
            Context ctx,
            File file,
            @DrawableRes int error,
            int width,
            int height,
            ImageView view
    ) {
        showImage(ctx, file, null, error, width, height, null, true,
                false, false, view);
    }

    public static void showImageWithPlaceHolder(
            Context ctx,
            String url,
            @DrawableRes int placeholder,
            @DrawableRes int error,
            ImageView view
    ) {
        showImage(ctx, url, placeholder, error, null, null, null, false,
                false, false, view);
    }

    public static void showImageSignature(
            Context ctx,
            String url,
            @DrawableRes int error,
            String signature,
            ImageView view
    ) {
        showImage(ctx, url, null, error, null, null, signature, false,
                false, false, view);
    }

    public static void showFileWithError(
            Context ctx,
            File file,
            @DrawableRes int error,
            ImageView view
    ) {
        showImage(ctx, file, null, error, null, null, null,
                false, false, false, view);
    }

    public static void showImageWithError(
            Context ctx,
            String url,
            @DrawableRes int error,
            ImageView view
    ) {
        showImage(ctx, url, null, error, null, null, null, false,
                false, false, view);
    }

    public static void showImageCenterCropWithSize(
            Context ctx,
            String url,
            int width,
            int height,
            ImageView view
    ) {
        showImage(ctx, url, null, null, width, height, null, true,
                false, false, view);
    }

    public static void showImageWithSize(
            Context ctx,
            String url,
            int width,
            int height,
            ImageView view
    ) {
        showImage(ctx, url, null, null, width, height, null, false,
                false, false, view);
    }

    public static void showImageWithSizeError(
            Context ctx,
            String url,
            @DrawableRes int error,
            int width,
            int height,
            ImageView view
    ) {
        showImage(ctx, url, null, error, width, height, null, false,
                false, false, view);
    }

    public static void showImageWithSizePlaceHolder(
            Context ctx,
            String url,
            @DrawableRes int placeholder,
            @DrawableRes int error,
            int width,
            int height,
            ImageView view
    ) {
        showImage(ctx, url, placeholder, error, width, height, null, false,
                false, false, view);
    }

    public static void showImageCenterCrop(
            Context ctx,
            String url,
            @DrawableRes int placeholder,
            @DrawableRes int error,
            ImageView view
    ) {
        showImage(ctx, url, placeholder, error, null, null, null,
                true, false, false, view);
    }

    private static void showImage(
            Context ctx,
            Object model,
            @DrawableRes Integer placeholder,
            @DrawableRes Integer error,
            Integer width,
            Integer height,
            String signature,
            boolean centerCrop,
            boolean crossFade,
            boolean dontAnimate,
            ImageView view
    ) {

        RequestOptions options = new RequestOptions();

        if (placeholder != null) {
            options.placeholder(placeholder);
        }
        if (error != null) {
            options.error(error);
        }
        if (width != null && height != null) {
            options.override(width, height);
        }
        if (signature != null) {
            options.signature(new ObjectKey(signature));
        }
        if (centerCrop) {
            options.centerCrop();
        }
        if (crossFade) {
            //ptions.transform(withCrossFade());//crossFade();
        }
        if (dontAnimate) {
            options.dontAnimate();
        }

        RequestBuilder request = Glide.with(ctx)
                .load(model)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                       // view.setImageResource(error);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                });

        request.into(view);
    }

    public static void showFile(
            Context ctx,
            File file,
            ImageView view
    ) {
        showImage(ctx, file, null, null, null, null, null,
                false, false, false, view);
    }

    public static void showGif(
            Context ctx,
            String url,
            ImageView view
    ) {
        showGif(ctx, url, null, null, view);
    }

    private static void showGif(
            Context ctx,
            String url,
            @DrawableRes Integer placeholder,
            @DrawableRes Integer error,
            ImageView view
    ) {


        RequestOptions options = new RequestOptions();
        if (placeholder != null) {
            options.placeholder(placeholder);
        }
        if (error != null) {
            options.error(error);
        }


        Glide.with(ctx).asGif()
                .load(url)
                .apply(options)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(view);
    }

    public static void showGifWithPlaceHolder(
            Context ctx,
            String url,
            @DrawableRes int placeholder,
            @DrawableRes int error,
            ImageView view
    ) {
        showGif(ctx, url, placeholder, error, view);
    }

    public static void showGifWithError(
            Context ctx,
            String url,
            @DrawableRes int error,
            ImageView view
    ) {
        showGif(ctx, url, null, error, view);
    }

    public static void loadFile(
            Context ctx,
            String url,
            FileSuccessCallback onSuccess
    ) {

        RequestOptions options = new RequestOptions();

        RequestBuilder request = Glide.with(ctx)
                        .asDrawable()
                .encodeFormat(Bitmap.CompressFormat.JPEG)
                .load(url)

                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                });
        request.downloadOnly(new CustomTarget<File>(){


            @Override
            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {

                if(resource.getAbsolutePath().endsWith(".0")){
                    File file = new File(resource.getParent() + "/" + UUID.randomUUID() + ".jpg");
                    resource.renameTo(file);
                    onSuccess.onSuccess(file);
                }else {

                    onSuccess.onSuccess(resource);
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    public static void showUriCrossFade(
            Context ctx,
            Uri uri,
            @DrawableRes int error,
            ImageView view
    ) {
        showImage(ctx, uri, null, error, null, null,
                null, false, true, false, view);
    }

    public static void showImage(
            Context ctx,
            String url,
            ImageView view
    ) {
        showImage(ctx, url, null, null, null, null,
                null, false, false, false, view);
    }

    public static void showImageWithoutAnimate(
            Context ctx,
            String url,
            @DrawableRes int placeholder,
            @DrawableRes int error,
            ImageView view
    ) {
        showImage(ctx, url, placeholder, error, null, null, null, false,
                false, true, view);
    }

    public static void loadBitmapWithoutCache(
            Context ctx,
            String url,
            BitmapSuccessCallback onSuccess,
            ImageFailedCallback onError
    ) {
        loadBitmap(ctx, url, null, null, false,
                false, true, true, onSuccess, onError);
    }

    public interface DrawableSuccessCallback {
        void onSuccess(Drawable d);
    }

    public interface BitmapSuccessCallback {
        void onSuccess(Bitmap b);
    }

    public interface ImageFailedCallback {
        void onFailed(Exception e);
    }

    public interface FileSuccessCallback {
        void onSuccess(File f);
    }
}

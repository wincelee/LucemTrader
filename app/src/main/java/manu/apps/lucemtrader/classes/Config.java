package manu.apps.lucemtrader.classes;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import manu.apps.lucemtrader.R;

public class Config {

    public static final String FIREBASE_INSTANCE_URL = "https://lucemtrader-default-rtdb.europe-west1.firebasedatabase.app";

    public CharSequence menuIconWithText(Drawable drawable, String title) {

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        SpannableString spannableString = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    public String numberFormatter(double d) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        return decimalFormat.format(d);
    }

    public static  void swipeRefreshLayoutColorScheme(Context context, SwipeRefreshLayout swipeRefreshLayout) {

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.primaryLightColor),
                ContextCompat.getColor(context, R.color.secondaryLightColor),
                ContextCompat.getColor(context, R.color.primaryDarkColor));

    }

    public String dateFormatter(String dateToFormat, String formatType) {

        SimpleDateFormat dateParser;

        if (dateToFormat.length() == 10) {

            dateParser = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

        } else if (dateToFormat.length() == 16) {

            dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK);

        } else if (dateToFormat.length() == 19) {

            dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);

        } else {

            dateParser = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

        }

        SimpleDateFormat dateFormatter;

        if (formatType.equalsIgnoreCase("investorsFormat")){

            dateFormatter =  new SimpleDateFormat("dd MMM, yy", Locale.UK);

        }else if (formatType.equalsIgnoreCase("cryptoFormat")){

            dateFormatter =  new SimpleDateFormat("hh:mm a - dd MMM, yy", Locale.UK);

        } else {

            dateFormatter = new SimpleDateFormat("yyyy, MMM d, EEE", Locale.UK);
        }


        Date date = new Date();

        try {

            date = dateParser.parse(dateToFormat);

        } catch (ParseException e) {

            e.printStackTrace();

        }

        assert date != null;

        return dateFormatter.format(date);


    }

    public void hideFabOnScrollDown(NestedScrollView nestedScrollView,
                                    FloatingActionButton fabOne,
                                    FloatingActionButton fabTwo,
                                    FloatingActionButton fabThree) {

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (scrollY > oldScrollY) {
                // scrolling down
                fabOne.setAlpha(0f);
                fabTwo.setAlpha(0f);
                fabThree.setAlpha(0f);
            }
            else if (scrollY < oldScrollY) {
                // scrolling up
                fabOne.setAlpha(0f);
                fabTwo.setAlpha(0f);
                fabThree.setAlpha(0f);
            }

        });
    }

    public static void hideFabOnRecyclerViewScrollDown(Context context, RecyclerView recyclerView,
                                                       FloatingActionButton fabOne,
                                                       FloatingActionButton fabTwo,
                                                       FloatingActionButton fabThree) {

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in_tv);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                // RecyclerView has stopped scrolling
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    fabOne.setAlpha(1f);
                    fabTwo.setAlpha(1f);
                    fabThree.setAlpha(1f);

                    fabOne.startAnimation(animation);
                    fabTwo.startAnimation(animation);
                    fabThree.startAnimation(animation);

                }

                super.onScrollStateChanged(recyclerView, newState);
            }

        });
    }

    public static String returnUserType(Context context) {

        HashMap<String, String> user = new SessionManager(context).getUserDetails();

        return user.get(SessionManager.USERTYPE);

    }

    public static void clearUserType(Context context){

        SessionManager sessionManager = new SessionManager(context);

        sessionManager.editor.clear();
        sessionManager.editor.commit();

    }

    public static void showSnackBar(Context context, String message, int anchorId, int duration) {

        Snackbar snackbar = Snackbar.make(((Activity)context).getWindow().getDecorView().getRootView(), message, duration);
        View snackView = snackbar.getView();

        snackView.setBackground(ContextCompat.getDrawable(context,R.drawable.snackbar_background));

        TextView textView = snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(15);
        textView.setTextColor(context.getResources().getColor(android.R.color.white));

        if (anchorId != 0){
            snackbar.setAnchorView(anchorId);
        }
        snackbar.show();
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap bitmap;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest / radius;
            bitmap = Bitmap.createScaledBitmap(bmp,
                    (int) (bmp.getWidth() / factor),
                    (int) (bmp.getHeight() / factor), false);
        } else {
            bitmap = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final String color = "#BAB399";
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(color));
        canvas.drawCircle((radius >> 1) + 0.7f, (radius >> 1) + 0.7f,
                (radius >> 1) + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static String returnFileExtension(Uri uri, Context context){

        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    public static Uri returnUri(Context context, Bitmap bitmap, String title, String description) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, description));
    }

    public static void setImageWithGlide(Context context, ImageView imageView, String imageUrl){

        Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .error(R.drawable.ic_image_not_found)
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                .into(imageView);

    }




}

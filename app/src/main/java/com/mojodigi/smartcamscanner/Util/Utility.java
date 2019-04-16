package com.mojodigi.smartcamscanner.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.R;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static java.util.Calendar.getInstance;

public class Utility {




    static boolean fileStatus = false;
    public static void setActivityTitle(Context ctx, String title) {

        // this function works fine but not being used  now because another function
        //setActivityTitle2 provides custom layout in action bar  which enable  high level  of customization in user Interface


        //((AppCompatActivity)ctx).getSupportActionBar().setTitle(title);
        // ((AppCompatActivity)ctx).getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>"+title+"</font>"));
       // Typeface tf = typeFace_adobe_caslonpro_Regular(ctx);
        SpannableString s = new SpannableString(title);


        //s.setSpan(new RelativeSizeSpan(2f), 0,s.length(), 0); // set size
        s.setSpan(new ForegroundColorSpan(ctx.getResources().getColor(R.color.white)), 0, s.length(), 0);// set color
       //s.setSpan(new CustomTypefaceSpan("", tf), 0, s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) ctx).getSupportActionBar();

        actionBar.setTitle(s);


        actionBar.setDisplayHomeAsUpEnabled(true);

        try {
            final Drawable upArrow = ctx.getResources().getDrawable(R.drawable.abc_ic_ab_back_material);

            //upArrow.setColorFilterctx(ctx.getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
            upArrow.setColorFilter(ctx.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);


        } catch (Exception e) {

        }



    }
    public static Date longToDate(Long l) {
        Date d = new Date(l);
        return d;
    }

    public static void dispToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static String getFileName()
    {
        //SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = df.format(getInstance().getTime());
        return date;
    }


    public static boolean checkOrCreateParentDirectory()
    {
        String path = Constants.parentfolder;
        File file=new File(path);
        if(file.exists())
            return file.exists();
        else return  file.mkdir();


    }
    public static void createDirectory()
    {
        //String path = Environment.getExternalStorageDirectory() + "/" + Constants.passDir;
    }

    public static void hideKeyboard(Activity activity) {
        try {
            View v = activity.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null && v != null;
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception e)
        {

        }
    }

    //check if a string contains only white spaces;
    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static Typeface typeFace_Gotham_Bold(Context ctx) {

        return Typeface.createFromAsset(ctx.getAssets(), "gothambold.otf");
    }
    public static Typeface typeFace_calibri(Context ctx) {

        return Typeface.createFromAsset(ctx.getAssets(), "calibri.ttf");
    }

    public static void setCustomizeSeachBar(Context mcontext, android.support.v7.widget.SearchView searchView) {
        ImageView searchIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        ImageView crossIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);

        searchIcon.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.search_black));
        crossIcon.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.cross_black));

        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        searchEditText.setTextColor(mcontext.getResources().getColor(R.color.black));

        searchEditText.setSelection(0);

        searchEditText.setHintTextColor(mcontext.getResources().getColor(R.color.black));
        searchEditText.setTypeface(Utility.typeFace_calibri(mcontext));
        Utility.setCursorColor(searchEditText, mcontext.getResources().getColor(R.color.black));


    }

    public static String humanReadableByteCount(long bytes, boolean si) {

        // read this function

        //int unit = si ? 1000 : 1024;
        int unit = 1024;
        if (bytes < unit) return bytes + " Byte";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "KMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);


    }


    public static void setCursorColor(EditText view, @ColorInt int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }

    public static void showKeyboard(Activity activity) {
        try {

            View v = activity.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null && v != null;
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        }catch (Exception e)
        {

        }
    }


    public static String LongToDate(String longV) {

        try {
            long input = Long.parseLong(longV.trim());
            Date date = new Date(input * 1000); // *1000 gives accurate date otherwise returns 1970
            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setCalendar(cal);
            cal.setTime(date);
            return sdf.format(date);
        }catch (Exception e)
        {
            return "";
        }
    }

    public static String LongToDate(Long date) {
        Date Date = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(Date);
        return formattedDate;
    }



    public static boolean IsNotEmpty(EditText view) {
        if (view.getText().length() > 0)
            return true;
        else
            return false;

    }
    public static String getFileExtensionfromPath(String path) {
        try {
            File file = new File(path);
            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            return extension;
        } catch (Exception e) {
            return " ";
        }
    }

    public static void RunMediaScan(Context context, File fileName) {
        MediaScannerConnection.scanFile(
                context, new String[]{fileName.getPath()}, null,
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        System.out.println("acn connected");
                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                        System.out.println("scan completed");
                    }
                });
    }



}

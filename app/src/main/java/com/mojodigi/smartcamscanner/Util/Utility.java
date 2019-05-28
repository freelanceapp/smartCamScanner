package com.mojodigi.smartcamscanner.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mojodigi.smartcamscanner.Activity_File_List;
import com.mojodigi.smartcamscanner.Class.Icons;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Model.fileModel;
import com.mojodigi.smartcamscanner.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import static java.util.Calendar.getInstance;

public class Utility {

    private static final String INTERNAL_VOLUME = "internal";
    public static final String EXTERNAL_VOLUME = "external";

    private static final String EMULATED_STORAGE_SOURCE = System.getenv("EMULATED_STORAGE_SOURCE");
    private static final String EMULATED_STORAGE_TARGET = System.getenv("EMULATED_STORAGE_TARGET");
    private static final String EXTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");


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
    public static void dispToast(Context ctx, int resourceId) {
        Toast.makeText(ctx, ctx.getResources().getString(resourceId), Toast.LENGTH_SHORT).show();
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



    public static void DispDetailsDialog( Context mContext, fileModel fileProperty )
    {

        if(fileProperty.getFilePath() !=null)
        {

            String[] splitPath = fileProperty.getFilePath().split("/");
            String fName = splitPath[splitPath.length - 1];

            Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.dialog_file_property);
            // Set dialog title

            TextView FileName = dialog.findViewById(R.id.FileName);
            TextView FilePath = dialog.findViewById(R.id.FilePath);
            TextView FileSize = dialog.findViewById(R.id.FileSize);
            TextView FileDate = dialog.findViewById(R.id.FileDate);
            TextView Resolution = dialog.findViewById(R.id.Resolution);
            TextView resltxt=dialog.findViewById(R.id.resltxt);

            TextView Oreintation = dialog.findViewById(R.id.ort);
            TextView oreinttxt=dialog.findViewById(R.id.oreinttxt);

            Oreintation.setVisibility(View.GONE);
            oreinttxt.setVisibility(View.GONE);
            resltxt.setVisibility(View.GONE);
            Resolution.setVisibility(View.GONE);




            FileName.setText(fName);
            FilePath.setText(fileProperty.getFilePath());
            FileSize.setText(fileProperty.getFileSize());
            FileDate.setText(fileProperty.getFileModifiedDate());



            dialog.show();
        }
    }
    public static void multiFileDetailsDlg(Context ctx, String totalSize, int fileCount) {

        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.multifiledetais_dialog);
        // Set dialog title

        TextView FileNum = dialog.findViewById(R.id.FileNum);
        TextView FileSize = dialog.findViewById(R.id.FileSizem);
        TextView close = dialog.findViewById(R.id.close);
        FileNum.setText(String.valueOf(fileCount));
        FileSize.setText(totalSize);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public static  String calcSelectFileSize(ArrayList<fileModel> fileList)
    {
        long totalSize=0;

        for(int i=0;i<fileList.size();i++)
        {
            fileModel m =  fileList.get(i);
            File f= new File(m.getFilePath());
            totalSize+=f.length();
        }

        return  Utility.humanReadableByteCount(totalSize,true);
    }


    public static void OpenFileWithNoughtAndAll(String name,Context ctx,String authority)
    {
        try {
            Uri uri = null;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(name.toLowerCase());
            String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                //uri = Uri.fromFile(file);

                uri=fileToContentUri(ctx,file);
                if(uri==null)uri=Uri.fromFile(file);

                if (uri != null) {
                    if (extension.equalsIgnoreCase("") || mimetype == null) {
                        // if there is no extension or there is no definite mimetype, still try to open the file
                        intent.setDataAndType(uri, "text/*");
                    } else {
                        intent.setDataAndType(uri, mimetype);
                    }
                    // custom message for the intent
                    //ctx.startActivity(Intent.createChooser(intent, "Choose an Application:"));  // does not show just one and always options
                    ctx.startActivity(intent);

                }
            } else {

                // in case of Android N and above Uri will be  made through provider written in Manifest file;
                //uri = FileProvider.getUriForFile(ctx, authority, file);
                uri=fileToContentUri(ctx,file);
                if(uri==null)
                    uri = FileProvider.getUriForFile(ctx, authority, file);
                if (uri != null) {
                    if (extension.equalsIgnoreCase("") || mimetype == null) {
                        // if there is no extension or there is no definite mimetype, still try to open the file
                        intent.setDataAndType(uri, "text/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    } else {
                        intent.setDataAndType(uri, mimetype);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    // custom message for the intent
                    //ctx. startActivity(Intent.createChooser(intent, "Choose an Application:")); // does not show just one and always options
                    ctx.startActivity(intent);
                }
                //

            }
        }catch (ActivityNotFoundException e)
        {
            Toast.makeText(ctx, "Application Not Found ", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            String str=e.getMessage();
            System.out.println(""+str);
        }


    }
    public static Uri fileToContentUri(Context context, File file) {
        // Normalize the path to ensure media search
        final String normalizedPath = normalizeMediaPath(file.getAbsolutePath());

        // Check in external and internal storages
        Uri uri = fileToContentUri(context, normalizedPath, file.isDirectory(), EXTERNAL_VOLUME);
        if (uri != null) {
            return uri;
        }
        uri = fileToContentUri(context, normalizedPath, file.isDirectory(), INTERNAL_VOLUME);
        if (uri != null) {
            return uri;
        }
        return null;
    }
    private static Uri fileToContentUri(Context context, String path, boolean isDirectory, String volume) {
        final String where = MediaStore.MediaColumns.DATA + " = ?";
        Uri baseUri;
        String[] projection;
        int mimeType = Icons.getTypeOfFile(path, isDirectory);

        switch (mimeType) {
            case Icons.IMAGE:
                baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            case Icons.VIDEO:
                baseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            case Icons.AUDIO:
                baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            default:
                baseUri = MediaStore.Files.getContentUri(volume);
                projection = new String[]{BaseColumns._ID, MediaStore.Files.FileColumns.MEDIA_TYPE};
        }

        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(baseUri, projection, where, new String[]{path}, null);
        try {
            if (c != null && c.moveToNext()) {
                boolean isValid = false;
                if (mimeType == Icons.IMAGE || mimeType == Icons.VIDEO || mimeType == Icons.AUDIO  || mimeType== Icons.PDF || mimeType==Icons.APK || mimeType==Icons.DOCUMENTS
                        || mimeType==Icons.GIF || mimeType==Icons.PRESENTATION|| mimeType==Icons.SPREADSHEETS || mimeType==Icons.TEXT) {
                    isValid = true;
                } else {
                    int type = c.getInt(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
                    isValid = type != 0;
                }

                if (isValid) {
                    // Do not force to use content uri for no media files
                    long id = c.getLong(c.getColumnIndexOrThrow(BaseColumns._ID));
                    return Uri.withAppendedPath(baseUri, String.valueOf(id));
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }
    public static String normalizeMediaPath(String path) {
        // Retrieve all the paths and check that we have this environment vars
        if (TextUtils.isEmpty(EMULATED_STORAGE_SOURCE) ||
                TextUtils.isEmpty(EMULATED_STORAGE_TARGET) ||
                TextUtils.isEmpty(EXTERNAL_STORAGE)) {
            return path;
        }

        // We need to convert EMULATED_STORAGE_SOURCE -> EMULATED_STORAGE_TARGET
        if (path.startsWith(EMULATED_STORAGE_SOURCE)) {
            path = path.replace(EMULATED_STORAGE_SOURCE, EMULATED_STORAGE_TARGET);
        }
        return path;
    }


    public static boolean isManualPasswordSet()
    {
        // boolean status=false;
        try {
            String path = Environment.getExternalStorageDirectory() + "/" + Constants.passDir+"/"+Constants.passwordFileDes;
            File f = new File(path);
            return f.exists();
        }catch (Exception e)
        {
            return  false;
        }

    }

    public static boolean createOrFindAppDirectory()
    {
        boolean status = false;

        if(checkOrCreateParentDirectory())
        {

            //String path  = Environment.getExternalStorageDirectory() + "/" + Constants.privateFiles;
            String path  =   Constants.hiddenFilesFolder;


            File f = new File(path);
            if (!f.exists()) {
                if (f.mkdir())
                    status = true;
                else
                    status = false;
            }
            else
                status = true;


        }
        return  status;

    }

    public static String getEncryptFileName(String filePath)
    {

        filePath =  Constants.hiddenFilesFolder +"/" + new File(filePath).getName() + ".des";
        return  filePath;
    }

    public static String setDecryptFilePath()
    {

        return  Constants.hiddenFilesFolder+"/";
    }

    public static  int  createPasswordFile(Context ctx,String userPassword) {
        try {

            String path = Environment.getExternalStorageDirectory() + "/" + Constants.passDir;

            File f = new File(path);
            if (!f.exists()) {
                if (f.mkdir()) {
                    String cPath=path+"/"+Constants.passwordFile;
                    String data = userPassword;
                    FileOutputStream out = new FileOutputStream(cPath);
                    out.write(data.getBytes());
                    out.close();
                    File file=new File(cPath);

                    if (file.exists())
                    {
                        try {
                            FileInputStream inFile = new FileInputStream(file);
                            FileOutputStream outFile = new FileOutputStream(path+"/"+Constants.passwordFileDes);

                            String password = Constants.encryptionPassword;
                            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
                            // SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");  //in java
                            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");  //in android
                            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);




                            byte[] salt = new byte[8];
                            Random random = new Random();
                            random.nextBytes(salt);

                            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
                            //Cipher cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");  //in java
                            Cipher cipher = Cipher.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");  // in android
                            cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParameterSpec);
                            outFile.write(salt);

                            byte[] input = new byte[64];
                            int bytesRead;
                            while ((bytesRead = inFile.read(input)) != -1) {
                                byte[] output = cipher.update(input, 0, bytesRead);
                                if (output != null)
                                    outFile.write(output);
                            }

                            byte[] output = cipher.doFinal();
                            if (output != null)
                                outFile.write(output);

                            inFile.close();
                            outFile.flush();
                            outFile.close();

                            // delete  the  temporary file;
                            if(file.exists()) {
                                file.delete();
                                Utility.RunMediaScan(ctx,file);
                            }

                        }
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (InvalidAlgorithmParameterException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        }

                        return 1;

                    }


                } else {

                    Utility.dispToast(ctx, ctx.getResources().getString(R.string.filenotcreated)); // remove this message
                    return 0;
                }
            } else {
                Utility.dispToast(ctx, ctx.getResources().getString(R.string.password_create_error));
                return 0;
            }
        }
        catch (Exception e)
        {
            return  0;
        }
        return  0;
    }


    public static  String readPasswordFile()
    {
        String path = Environment.getExternalStorageDirectory() + "/" + Constants.passDir+"/"+Constants.passwordFileDes;
        try {
            String password = Constants.encryptionPassword;
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            // SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");  //in java
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");  //in android
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
            File inputFile=new File(path);
            if(inputFile.exists())
            {
                FileInputStream fis = new FileInputStream(inputFile);

                byte[] salt = new byte[8];
                fis.read(salt);

                PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);

                //Cipher cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");  //in java
                Cipher cipher = Cipher.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");  // in android
                cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParameterSpec);

                //FileOutputStream fos = new FileOutputStream(outputFile);
                // FileOutputStream fos = new FileOutputStream("G:\\EncryptTest\\image\\Takendra_decrypted.jpg");
                byte[] in = new byte[64];
                int read;
                while ((read = fis.read(in)) != -1) {
                    byte[] output = cipher.update(in, 0, read);
                    // if (output != null)
                    // fos.write(output);
                }

                byte[] output = cipher.doFinal();
                if (output != null) {
                    // fos.write(output);
                    String s = new String(output);
                    return s;
                }

                fis.close();
                // fos.flush();
                //fos.close();
                // Utility.RunMediaScan(ctx,outputFile);
            }

        }catch (InvalidKeyException e)
        {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }



        return  "";
    }


    public  static String getString(Context mContext,int resourceKey)
    {
        try {
            return mContext.getResources().getString(resourceKey);
        }catch (Exception e)
        {
            // in case invalid key  is passed;
            return "Resource key not found";
        }
    }



}

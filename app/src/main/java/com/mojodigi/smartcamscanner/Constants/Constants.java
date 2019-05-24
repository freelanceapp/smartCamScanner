package com.mojodigi.smartcamscanner.Constants;

import android.graphics.Bitmap;
import android.os.Environment;

public class Constants {


    public static final int TYPE_PDF=1;
    public static final int TYPE_JPG=2;

    public static  final String privacyUrl="http://mojodigitech.com/privacy-policy-for-smart-scanner/";

    public static final String parentfolder=Environment.getExternalStorageDirectory()+"/SmartScanner/";

    public static final  String pdfFolderName = parentfolder+"Pdfs";
    public static final  String imageFolderName = parentfolder+"Images";
    public static final  String allFilesFolder = parentfolder+"All files";
    public static final  String hiddenFilesFolder = parentfolder+"Hidden files";



    //private  module  vars
    public static  String passDir=".mojo_Digi";
    public static String passwordFile="scanner.txt";
    public static  String passwordFileDes="scanner.des";
    public static String encryptionPassword="x!23ghjt@#";
    //private  module  vars

  public static final String hiddenFileExtension="des";
    public static boolean isAsyncOperationStopped=false;

    public static final String IntentfilePath="path";

    public static   Bitmap imageBitmap=null;

    public static final String pdfExtension = ".pdf";
    public static final String tempDirectory = "temp";
    public static final String pdfDirectory = "/PDFfiles/";

    public static final String PATH_SEPERATOR = "/";
    public static final String IMAGE_SCALE_TYPE_ASPECT_RATIO = "maintain_aspect_ratio";

    public static final String PG_NUM_STYLE_PAGE_X_OF_N = "pg_num_style_page_x_of_n";
    public static final String PG_NUM_STYLE_X_OF_N = "pg_num_style_x_of_n";
    public static final String STORAGE_LOCATION = "storage_location";
    public static final String DEFAULT_PAGE_SIZE_TEXT = "DefaultPageSize";
    public static final String DEFAULT_PAGE_SIZE = "A4";


    public static String folderPath="folderpath";


    public static final int REQUST_ALL_FILE=1;
    public static final int REQUST_RECENT_FILE=2;
    public static final int REQUST_PDF_FILE=3;
    public static final int REQUST_IMAGES_FILE=4;


    public static String QrData="QrData";
    public static boolean callUpdateMethod;

    public static String reoderData="data";
    public static boolean isFromORderActivity;
}

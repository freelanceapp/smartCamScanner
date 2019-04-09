package com.mojodigi.smartcamscanner.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.mojodigi.smartcamscanner.Util.Utility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class pdfGenerateAsyncTask extends AsyncTask<Void,Void,Boolean> {


    Context mContex;

    public  ArrayList<String> mImagesUri = new ArrayList<>();
   String outputPath;
   String inputPath;
    public pdfGenerateAsyncTask(Context mContex,String inputPath,String outputPath, ArrayList<String> mImagesUri)
    {
        this.mContex=mContex;
        this.mImagesUri=mImagesUri;
        this.inputPath=inputPath;
        this.outputPath=outputPath;
    }
    @Override
    protected Boolean doInBackground(Void... voids) {

        return  addImagesToPdf(inputPath, outputPath, mImagesUri);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean)
            Utility.dispToast(mContex,"file created" );
    }

    public boolean addImagesToPdf(String inputPath, String output, ArrayList<String> imagesUri) {
        try {
            PdfReader reader = new PdfReader(inputPath);
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(output));
            Rectangle documentRect = document.getPageSize();
            document.open();

            int numOfPages = reader.getNumberOfPages();
            PdfContentByte cb = writer.getDirectContent();
            PdfImportedPage importedPage;
            for (int page = 1; page <= numOfPages; page++) {
                importedPage = writer.getImportedPage(reader, page);
                document.newPage();
                cb.addTemplate(importedPage, 0, 0);
            }

            for (int i = 0; i < imagesUri.size(); i++) {
                document.newPage();
                Image image = Image.getInstance(imagesUri.get(i));
                image.setBorder(0);
                float pageWidth = document.getPageSize().getWidth(); // - (mMarginLeft + mMarginRight);
                float pageHeight = document.getPageSize().getHeight(); // - (mMarginBottom + mMarginTop);
                image.scaleToFit(pageWidth, pageHeight);
                image.setAbsolutePosition(
                        (documentRect.getWidth() - image.getScaledWidth()) / 2,
                        (documentRect.getHeight() - image.getScaledHeight()) / 2);
                document.add(image);
            }

            document.close();

           /* getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction, v -> mFileUtils.openFile(output)).show();
            new DatabaseHelper(mContext).insertRecord(output, mContext.getString(R.string.created));*/

            return true;
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            //showSnackbar(mContext, R.string.remove_pages_error);
            return false;
        }
    }
}

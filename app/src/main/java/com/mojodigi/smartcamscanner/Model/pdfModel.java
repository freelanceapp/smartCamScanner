package com.mojodigi.smartcamscanner.Model;

public class pdfModel
{


    String fileName;
    String fileSize;
    String fileModifiedDate;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    String  filePath;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileModifiedDate() {
        return fileModifiedDate;
    }

    public void setFileModifiedDate(String fileModifiedDate) {
        this.fileModifiedDate = fileModifiedDate;
    }
}

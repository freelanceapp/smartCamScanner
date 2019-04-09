package com.mojodigi.smartcamscanner.Model;

public class fileModel
{


    String fileName;
    String fileSize;
    String fileModifiedDate;

    public boolean getIsImgs() {
        return isImgs;
    }

    public void setIsImgs(boolean imgs) {
        isImgs = imgs;
    }

    boolean isImgs;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    String filePath;

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

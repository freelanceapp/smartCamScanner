package com.mojodigi.smartcamscanner.Interfaces;

public interface OnPDFCreatedInterface {
    void onPDFCreationStarted();
    void onPDFCreated(boolean success, String path);
}

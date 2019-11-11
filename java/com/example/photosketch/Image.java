package com.example.photosketch;

import android.graphics.Bitmap;
import android.net.Uri;

public class Image {
    private Uri imageResourceID;
    private boolean isConverted;
    private boolean isSelect = false;

    public Image(Uri imageResourceID, boolean isConverted) {
        this.imageResourceID = imageResourceID;
        this.isConverted = isConverted;
    }

    public Uri getImageResourceID() {
        return imageResourceID;
    }

    public void setImageResourceID(Uri imageResourceID) {
        this.imageResourceID = imageResourceID;
    }

    public boolean isConverted() {
        return isConverted;
    }

    public void setConverted(boolean converted) {
        isConverted = converted;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}

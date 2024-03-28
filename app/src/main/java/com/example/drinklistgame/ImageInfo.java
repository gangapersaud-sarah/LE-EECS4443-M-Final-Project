package com.example.drinklistgame;
public class ImageInfo {
    private int imageId;
    private int imageResource;

    public ImageInfo(int imageId, int imageResource) {
        this.imageId = imageId;
        this.imageResource = imageResource;
    }

    public int getImageId() {
        return imageId;
    }

    public int getImageResource() {
        return imageResource;
    }
}
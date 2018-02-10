package com.universe.yz.admin.widget;

import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory;

public class Image {
    public int height;
    public int width;
    public String url;
    public FileBitmapDecoderFactory fileBitmapDecoderFactory;

    public Image(String url, int width, int height,FileBitmapDecoderFactory fileBitmapDecoderFactory) {
        this.height = height;
        this.width = width;
        this.url = url;
        this.fileBitmapDecoderFactory = fileBitmapDecoderFactory;
    }
    public Image(String url,FileBitmapDecoderFactory fileBitmapDecoderFactory) {
        this.height = height;
        this.width = width;
        this.url = url;
        this.fileBitmapDecoderFactory = fileBitmapDecoderFactory;
    }
}
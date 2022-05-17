package com.example.lml.easyphoto.camera;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by czl on 2016/5/25.
 */
public class LoadLocalImageUtil {
    private LoadLocalImageUtil() {
    }

    private static LoadLocalImageUtil instance = null;

    public static synchronized LoadLocalImageUtil getInstance() {
        if (instance == null) {
            instance = new LoadLocalImageUtil();
        }
        return instance;
    }

    /**
     * 从内存卡中异步加载本地图片
     *
     * @param uri
     * @param imageView
     */
    public void displayFromSDCard(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage("file://" + uri, imageView);
    }
    /**
     * 从内存卡中异步加载本地图片
     *
     * @param uri
     * @param imageView
     */
    public void displayFromSDCardYj(String uri, ImageView imageView, DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage("file://" + uri, imageView, options);
    }
    /**
     * 从assets文件夹中异步加载图片
     *
     * @param imageName
     *            图片名称，带后缀的，例如：1.png
     * @param imageView
     */
    public void dispalyFromAssets(String imageName, ImageView imageView) {
        ImageLoader.getInstance().displayImage("assets://" + imageName,
                imageView);
    }

    /**
     * 从drawable中异步加载本地图片
     *
     * @param imageId
     * @param imageView
     */
    public void displayFromDrawable(int imageId, ImageView imageView) {
        ImageLoader.getInstance().displayImage("drawable://" + imageId,
                imageView);
    }

    /**
     * 从内容提提供者中抓取图片
     */
    public void displayFromContent(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage("content://" + uri, imageView);
    }
    /**
     * 从网络中抓取图片
     */
    public void displayFromHttp(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView);
    }

}

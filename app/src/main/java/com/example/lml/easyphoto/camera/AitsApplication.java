package com.example.lml.easyphoto.camera;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;

import androidx.multidex.MultiDex;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class AitsApplication extends Application {
	static final String TAG = "AitsApplication";

  	private static AitsApplication application;
	public static AitsApplication getCustomApplication() {
		return application;
	}
	private SharedPreferences preferences;
	private String strCity;
	private String gisPath;

	public String getGisPath() {
		return gisPath;
	}

	public void setGisPath(String gisPath) {
		this.gisPath = gisPath;
	}
	private String userId;
	/**
	 * 是否验证通过
	 */
	private boolean isAuthorised;

	private static AitsApplication instance ;
	public static AitsApplication getInstance(){
		return instance ;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(getApplicationContext())
				.memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
				.discCacheExtraOptions(480, 800, Bitmap.CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)/设置缓存的详细信息，最好不要设置这个
				.threadPoolSize(3)//线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
				.memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileCount(100) //缓存的文件数量
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(new BaseImageDownloader(getApplicationContext(), 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
				.writeDebugLogs() // Remove for release app
				.build();//开始构建
		ImageLoader.getInstance().init(config);//全局初始化此配置
		instance = this ;
		init();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
	}

	private void init(){
		preferences = getSharedPreferences(TAG, MODE_PRIVATE);
		userId = preferences.getString("userId", "");
		isAuthorised = preferences.getBoolean("isAuthorised", false);
	}

	public boolean savePrefs(){
		Editor editor = preferences.edit();
		editor.putString("userId", userId);
		return editor.commit();
	}

	public void clearPrefs(){

		Editor editor = preferences.edit();
		userId="";
		editor.clear();
		editor.commit();
		savePrefs();
 	}
	public boolean isAuthorised() {
		return isAuthorised;
	}

	public void setAuthorised(boolean isAuthorised) {
		this.isAuthorised = isAuthorised;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}
package com.example.lml.easyphoto.banner;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.example.lml.easyphoto.R;

import java.util.ArrayList;

public class BannerFragment extends Fragment {

	private Handler mHandler=new Handler();
	private boolean mIsDragging;
	private static final int BANNER_COUNT = 100000;
	private static final long AUTO_SCROOL = 2000;
	private int[] mImageRes=new int[]{
		R.mipmap.banner_01,
		R.mipmap.banner_02,
		R.mipmap.banner_03,
	};
	private int[] mImageViewID=new int[]{
			R.id.pointView1,
			R.id.pointView2,
			R.id.pointView3,
	};
	private ArrayList<View> ListImageView=new ArrayList<View>();
	private ViewPager mViewPager;
	private Runnable mRun;

	public BannerFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View layout = inflater.inflate(R.layout.fragment_banner, container, false);
		 //初始化Itdi
		 initImgItdi(layout); 
		 initViewPager(layout);
		 return layout;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		autoScrollBanner();
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//移除线程
		mHandler.removeCallbacks(mRun);
	}
	//自动滚动BANNER
	private void autoScrollBanner() {
		
		
		mRun=new Runnable() {
			
			public void run() {
				//判断用户是否在拖动ViewPager
				if (!mIsDragging) {
					int currentItem = mViewPager.getCurrentItem();
					mViewPager.setCurrentItem(++currentItem);
				}
				mHandler.postDelayed(this, AUTO_SCROOL);
			}
		};
		mHandler.postDelayed(mRun, AUTO_SCROOL);
	}

	private void initViewPager(View layout) {
		mViewPager = (ViewPager) layout.findViewById(R.id.viewPager1);
		mViewPager.setOnPageChangeListener(new PageChangeListenerImpl());
		 FragmentManager fm = getChildFragmentManager();
		 BannerAdapter bannerAdapter=new BannerAdapter(fm);
		 mViewPager.setAdapter(bannerAdapter);
		 mViewPager.setCurrentItem(BANNER_COUNT/2);
	}

	private void initImgItdi(View layout) {
		for (int i = 0; i < mImageViewID.length; i++) {
			View  imageView = (View) layout.findViewById(mImageViewID[i]);
			if (i==0) {
				imageView.setEnabled(true);
			}else {
				imageView.setEnabled(false);
			}
			ListImageView.add(imageView);
		}
	}
	class BannerAdapter extends FragmentPagerAdapter {

		

		public BannerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
				Fragment fragment=new ImageFragment(mImageRes[position%mImageViewID.length]);
			return fragment;
		}

		@Override
		public int getCount() {
			return BANNER_COUNT;
		}
		
	}
	class PageChangeListenerImpl implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (ViewPager.SCROLL_STATE_DRAGGING==arg0) {
				mIsDragging=true;
			}else {
				mIsDragging=false;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < mImageViewID.length; i++) {
				View imageView = ListImageView.get(i);
				if ((position%mImageViewID.length)==i) {
//					imageView.setImageResource(R.drawable.banner_selected);
					imageView.setEnabled(true);
				}else {
//					imageView.setImageResource(R.drawable.banner_normal);
					imageView.setEnabled(false);
				}
			}
		}
		
	}

}

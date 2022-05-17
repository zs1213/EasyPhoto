package com.example.lml.easyphoto.banner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.lml.easyphoto.R;


@SuppressLint("ValidFragment")
public class ImageFragment extends Fragment implements OnClickListener {
	private int imageId;

	public ImageFragment(int imageId) {
		this.imageId=imageId;
	}

	public ImageFragment() {
	}
	@Override

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_image, container,
				false);
		ImageView mImageView = (ImageView) layout.findViewById(R.id.imageView1);
		mImageView.setImageResource(imageId);
		mImageView.setOnClickListener(this);
		return layout;

	}

	@Override
	public void onClick(View v) {

	}
}

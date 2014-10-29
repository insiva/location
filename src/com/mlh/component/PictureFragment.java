package com.mlh.component;

import com.mlh.R;
import com.mlh.activity.CameraActivity;
import com.mlh.database.DaoFactory;
import com.mlh.model.Picture;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Matteo
 *一个展示照片的Fragment，在拍照完毕后启用
 */
@SuppressLint("NewApi")
public class PictureFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

	private long Guid;
	private Picture mPic;
	private TextView tvAddress;
	private ImageView ivPicture;
	private boolean AfterCapture;
	private Button btnP, btnR, btnN;
	private CameraActivity actCamera;

	public PictureFragment(long g, boolean ac) {
		this.Guid = g;
		this.mPic = DaoFactory.getPictureDaoInstance().getById(this.Guid);//new Picture(this.Guid, false);
		//this.Pic.Upload();
		this.AfterCapture = ac;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_picture, container,
				false);
		this.tvAddress = (TextView) rootView.findViewById(R.id.tvAddress);
		this.ivPicture = (ImageView) rootView.findViewById(R.id.ivPicture);
		this.btnN = (Button) rootView.findViewById(R.id.btnNextPic);
		this.btnR = (Button) rootView.findViewById(R.id.btnRawPic);
		this.btnP = (Button) rootView.findViewById(R.id.btnPrePic);
		this.actCamera = (CameraActivity) this.getActivity();
		this.ivPicture.setImageURI(this.mPic.getUri());
		this.tvAddress.setText(this.mPic.getLocation().getAddress());
		this.SetButtons();
		return rootView;
	}

	private void SetButtons() {
		if (this.AfterCapture) {
			this.btnP.setText(R.string.RawPicture);
			this.btnP.setVisibility(View.VISIBLE);
			this.btnR.setVisibility(View.INVISIBLE);
			this.btnN.setText(R.string.ContinueCapture);
			this.btnN.setVisibility(View.VISIBLE);
			this.btnN.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					actCamera.continueCapture();
				}

			});

			this.btnP.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(mPic.getUri(), "image/*");
					startActivity(intent);
				}

			});
		} else {
			this.btnP.setText(R.string.PrePicture);
			this.btnP.setVisibility(View.VISIBLE);
			this.btnR.setVisibility(View.VISIBLE);
			this.btnN.setText(R.string.NextPicture);
			this.btnN.setVisibility(View.VISIBLE);
		}
	}

}

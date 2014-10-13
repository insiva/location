package com.matteo.matteolocationhistory;

import com.matteo.matteolocationhistory.model.Conf;
import com.matteo.matteolocationhistory.model.Picture;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link PictureFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link PictureFragment#newInstance} factory method to create an instance of
 * this fragment.
 *
 */
@SuppressLint("NewApi")
public class PictureFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

	private int Guid;
	private Picture Pic;
	private TextView tvAddress;
	private ImageView ivPicture;
	private boolean AfterCapture;
	private Button btnP, btnR, btnN;
	private SystemCameraActivity actCamera;

	public PictureFragment(int g, boolean ac) {
		this.Guid = g;
		this.Pic = new Picture(this.Guid, false);
		this.Pic.Upload();
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
		this.actCamera = (SystemCameraActivity) this.getActivity();
		this.ivPicture.setImageURI(this.Pic.GetUri());
		this.tvAddress.setText(this.Pic.Loc.Address);
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
					actCamera.ContinueCapture();
				}

			});

			this.btnP.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Pic.GetUri(), "image/*");
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

package com.mlh.activity;

import com.mlh.R;
import com.mlh.model.Album;
import com.mlh.model.Picture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * @author Matteo
 *相册Activity
 */
@SuppressLint("InflateParams")
public class AlbumActivity extends Activity implements OnGestureListener,OnClickListener {
	private GestureDetector detector;
	private ViewFlipper flipper;
	private LayoutInflater inflater;
	private TextView tvAddress, tvDate, tvIndex;
	private Button btnPre, btnNext;
	Intent getMainActivity = null;
	int count = 1;
	private int CurrentIndex = 5;
	private Album mAlbum;

	private ImagePic AIP, BIP, CIP;

	@SuppressLint({ "NewApi", "InflateParams" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.init();
		this.setPicView(0);
	}
	
	@SuppressWarnings("deprecation")
	private void init(){
		this.CurrentIndex = this.getIntent().getExtras()
				.getInt(Album.ALBUM_INDEX);
		this.mAlbum = this.getIntent().getExtras().getParcelable(Album.ALBUM);
		this.inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.activity_album, null);
		setContentView(layout);
		flipper = (ViewFlipper) findViewById(R.id.view_flipper);
		detector = new GestureDetector(this);

		View v = this.inflater.inflate(R.layout.layout_switch_pic_item, null);
		ImageView iv = (ImageView) v.findViewById(R.id.ivPic);
		ProgressBar pb = (ProgressBar) v.findViewById(R.id.pbWait);
		this.AIP = new ImagePic(v, iv, pb);
		v = this.inflater.inflate(R.layout.layout_switch_pic_item, null);
		iv = (ImageView) v.findViewById(R.id.ivPic);
		pb = (ProgressBar) v.findViewById(R.id.pbWait);
		this.BIP = new ImagePic(v, iv, pb);

		this.tvAddress = (TextView) this.findViewById(R.id.tvAddress);
		this.tvDate = (TextView) this.findViewById(R.id.tvDate);
		this.tvIndex = (TextView) this.findViewById(R.id.tvIndex);
		this.btnNext = (Button) this.findViewById(R.id.btnNextPic);
		this.btnPre = (Button) this.findViewById(R.id.btnPrePic);
		this.flipper.setDisplayedChild(0);

		this.btnNext.setOnClickListener(this);
		this.btnPre.setOnClickListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.detector.onTouchEvent(event);
	}

	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float arg2,
			float arg3) {
		if (e1.getX() - e2.getX() > 5) {
			this.moveNext();
			return true;
		} else if (e1.getX() - e2.getX() < -5) {
			this.movePrevious();
			return true;
		}
		return true;
	}

	private void movePrevious() {
		if (this.CurrentIndex == 0) {
			return;
		}
		this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_right_in));
		this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_right_out));
		this.setPicView(-1);
		this.flipper.showPrevious();
	}

	private void moveNext() {
		if (this.CurrentIndex == (this.mAlbum.size() - 1)) {
			return;
		}
		this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_in));
		this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_out));
		this.setPicView(1);
		this.flipper.showNext();
	}

	private void setPicView(int d) {
		this.CurrentIndex = this.CurrentIndex + d;
		ImagePic OIP = null;
		if (this.CIP == this.AIP) {
			OIP = this.BIP;
		} else {
			OIP = this.AIP;
		}
		OIP.recyclePic();
		this.flipper.removeView(OIP.V);
		Picture pic = this.mAlbum.get(this.CurrentIndex);
		OIP.showWait();
		OIP.setPicture(pic);
		this.CIP = OIP;
		if (d > 0)// 向右
		{
			this.flipper.addView(OIP.V, 1);
		} else if (d < 0) {// 向左
			this.flipper.addView(OIP.V, 0);
		} else {
			this.AIP.IV.setImageResource(R.drawable.wait);
			this.flipper.addView(this.AIP.V);
			this.flipper.addView(this.BIP.V);
		}
		this.loadPictureBegin();
		this.tvAddress.setText(this.CIP.P.getLocation().getAddress());
		this.tvDate.setText(this.CIP.P.getCreateTimeStr());
		this.tvIndex
				.setText((this.CurrentIndex + 1) + "/" + this.mAlbum.size());
	}

	private void loadPictureBegin() {
		new ImageLoadTask().execute();
	}

	private void loadPictureEnd() {
		this.CIP.IV.setImageBitmap(this.CIP.P
				.getImage(Picture.MEDIUM_PICTURE_FLAG));
		this.CIP.hideWait();
	}

	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public class ImagePic {
		public View V;
		public ImageView IV;
		public Picture P = null;
		public ProgressBar PB;

		public ImagePic(View v, ImageView iv, Picture p) {
			this.V = v;
			this.IV = iv;
			this.P = p;
		}

		public ImagePic(View v, ImageView iv, ProgressBar pb) {
			this.V = v;
			this.IV = iv;
			this.PB = pb;
		}

		public void setPicture(Picture p) {
			this.P = p;
		}

		public void recyclePic() {
			if (this.P == null) {
				return;
			}
			this.P.recycleImage(Picture.MEDIUM_PICTURE_FLAG);
		}

		public void showWait() {
			this.IV.setVisibility(View.INVISIBLE);
			this.PB.setVisibility(View.VISIBLE);
		}

		public void hideWait() {
			this.IV.setVisibility(View.VISIBLE);
			this.PB.setVisibility(View.INVISIBLE);
		}
	}

	public class ImageLoadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (CIP.P.getImage(Picture.MEDIUM_PICTURE_FLAG) == null) {
				CIP.P.fetchImage(Picture.MEDIUM_PICTURE_FLAG);
			}
			publishProgress();
			return null;
		}

		@Override
		public void onProgressUpdate(Void... voids) {

			if (isCancelled())
				return;
			// 更新UI
			loadPictureEnd();

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btnNextPic:
			this.moveNext();
			break;
		case R.id.btnPrePic:
			this.movePrevious();
			break;
		}
	}
}

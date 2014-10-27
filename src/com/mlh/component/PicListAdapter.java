package com.mlh.component;

import com.mlh.R;
import com.mlh.model.Album;
import com.mlh.model.Picture;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class PicListAdapter extends BaseAdapter {
	private LayoutInflater lifPicItem;
	private Album mAlbum = null;
	//private RefreshListView rlvPic;

	public PicListAdapter(Context context,RefreshListView v) {
		this.lifPicItem = LayoutInflater.from(context);
		this.mAlbum =new Album();
		//this.rlvPic=v;
	}
	
	public Album getAlbum()
	{
		return this.mAlbum;
	}

	public void readXML(String xs)  {
		this.mAlbum.readXml(xs);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.mAlbum.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		PictureItemHolder hdPic;
		if (convertView == null) {
			hdPic = new PictureItemHolder(this);
			convertView = this.lifPicItem.inflate(R.layout.layout_pic_item,
					null);
			hdPic.ivPic = (ImageView) convertView.findViewById(R.id.ivPicture);
			// hdPic.ivPic.setImageResource(R.drawable.testpic);
			hdPic.tvAddress = (TextView) convertView
					.findViewById(R.id.tvAddress);
			hdPic.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			convertView.setTag(hdPic);
		} else {
			hdPic = (PictureItemHolder) convertView.getTag();
		}
		hdPic.setValues(position);

		return convertView;
	}
	
	public void loadImage(){
		new ImageLoadTask().execute();
	}

	public static class PictureItemHolder {

		public TextView tvAddress, tvTime;
		public ImageView ivPic;
		public PicListAdapter pla;

		public PictureItemHolder(PicListAdapter l) {
			this.pla=l;
		}

		public void setValues(int p) {
			Bitmap bm=this.pla.getAlbum().get(p).getImage(Picture.SMALL_PICTURE_FLAG);
			if ( bm!= null) {
				this.ivPic.setImageBitmap(bm);
			}
			this.tvAddress.setText(p+":"+this.pla.getAlbum().get(p).getLocation().getAddress());
			this.tvTime.setText(this.pla.getAlbum().get(p).getCreateTimeStr());
		}
	}

	public class ImageLoadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			for (Picture pic:mAlbum) {
				pic.fetchImage(Picture.SMALL_PICTURE_FLAG);
				publishProgress();
			}
			return null;
		}

		@Override
		public void onProgressUpdate(Void... voids) {

			if (isCancelled())
				return;
			// ¸üÐÂUI
			notifyDataSetChanged();

		}
	}
}

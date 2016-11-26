package com.ali.entertaiment;
import android.content.*;
import android.database.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class YoutubeList extends ListView
{
	Handler H = new Handler();
	BroadcastReceiver br;
	List<Video> mListVideo;
	Cursor mCursor;
	
	Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

	private YoutubeAdapter adapter;
	public YoutubeList(Context c,AttributeSet a){
		super(c,a);
		br = new BroadcastReceiver(){

			@Override
			public void onReceive(Context p1, Intent p2)
			{
				recycleOnMeasure();
			}
			
		};
		
		mListVideo = new ArrayList<Video>();
		adapter = new YoutubeAdapter(c,mListVideo);
		this.setAdapter(adapter);
	}
	
	private void prepareYoutubeList()
	{
		adapter.notifyDataSetChanged();
		int idCol,idTit,idDur,colDate,colAutor;
		Bitmap thumb = null;
		long id = 0,duration = 0;
		String title = "unknown",date;
		mCursor = getContext().getContentResolver().query(videoUri,null,null,null,null);
		if(mCursor!=null&&mCursor.moveToFirst()){
			idCol = mCursor.getColumnIndex(MediaStore.Video.Media._ID);
			idTit = mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
			idDur = mCursor.getColumnIndex(MediaStore.Video.Media.DURATION);
			colDate = mCursor.getColumnIndex(MediaStore.Video.Media.DATA);
			do{
			id = mCursor.getLong(idCol);
			duration = mCursor.getLong(idDur);
			date = mCursor.getString(colDate);
			thumb = MediaStore.Video.Thumbnails.getThumbnail(getContext().getContentResolver(),id,MediaStore.Video.Thumbnails.MINI_KIND,new BitmapFactory.Options());
			title = mCursor.getString(idTit);
				mListVideo.add(new Video(title,duration,id,thumb,date));
				Collections.sort(mListVideo, new Comparator<Video>(){

						@Override
						public int compare(Video p1, Video p2)
						{
							// TODO: Implement this method
							return p1.getTitle().compareTo(p2.getTitle());
						}


					});
			}while(mCursor.moveToNext());
			mCursor.close();
		}
	}

	@Override
	protected void onAttachedToWindow()
	{
		// TODO: Implement this method
		super.onAttachedToWindow();
		IntentFilter ih = new IntentFilter(Intent.ACTION_MEDIA_BAD_REMOVAL);
		ih.addAction(Intent.ACTION_TIME_TICK);
		ih.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
		ih.addAction(Intent.ACTION_MEDIA_CHECKING);
		ih.addAction(Intent.ACTION_MEDIA_EJECT);
		ih.addAction(Intent.ACTION_MEDIA_MOUNTED);
		ih.addAction(Intent.ACTION_MEDIA_REMOVED);
		ih.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		ih.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		ih.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		ih.addAction(Intent.ACTION_MEDIA_SHARED);
		getContext().registerReceiver(br,ih);
		prepareYoutubeList();
	}
	

	@Override
	protected void onDetachedFromWindow()
	{
		// TODO: Implement this method
		super.onDetachedFromWindow();
		getContext().unregisterReceiver(br);
	}
	
	public int resId(String s,String ss){
		return getResources().getIdentifier(s,ss,getContext().getPackageName());
	}
}

package com.ali.entertaiment;
import android.content.*;
import android.content.res.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.SeekBar.*;
import java.util.concurrent.*;
import android.graphics.drawable.*;

public class YoutubeVideo extends FrameLayout
{
	int curentPosition;
	VideoViewCompat mVid;
	TextView cur,tot;
	SeekBar mSeek;
	long mDur;
	ImageView play,rr,ff;
	String uriVideo;
	private LayoutInflater LayInf;
	Runnable mRun = new Runnable(){

		@Override
		public void run()
		{
			cur.setText(Video.Utils.convertToString(mVid.getCurrentPosition()));
			tot.setText(Video.Utils.convertToString(mVid.getDuration()));
			mSeek.setProgress(mVid.getCurrentPosition()*mSeek.getMax()/mVid.getDuration());
			mH.postDelayed(this,200);
		}
	};
	MediaController mController;
	private BroadcastReceiver mReceiver;

	private Handler mH;
	public YoutubeVideo(Context c,AttributeSet a){
		super(c,a);
		mH = new Handler();
		LayInf = LayoutInflater.from(c);
		prepareVideo();
		mReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context p1, Intent p2)
			{
				if(p2.getAction().equals("playVideo")){
					uriVideo = p2.getStringExtra("video");
					mDur = p2.getLongExtra("dur",0);
					SharedPreferences.Editor shs = p1.getSharedPreferences("statusBarVideo",0).edit();
					shs.putString("uriVideo",uriVideo);
					shs.putLong("durVideo",mDur);
					shs.apply();
					shs.commit();
					playCurrentVideo();
				}
			}
			
		};
	}

	public void seekTo(final int p2)
	{
		mVid.seekTo(p2);
	}
	public boolean isPlaying(){
		if(mVid!=null){
			return mVid.isPlaying();
		}
		return false;
	}
	private void playCurrentVideo()
	{
		SharedPreferences sh = getContext().getSharedPreferences("statusBarVideo",0);
		final String r = uriVideo = sh.getString("uriVideo",null);
		final long l = mDur = sh.getLong("durVideo",0);
		if(uriVideo!=null){
			mVid.setVideoURI(Uri.parse(r));
			mVid.start();
			play.setImageResource(resId("ic_media_pause","drawable"));
		}
	}
	private void prepareVideo()
	{
		//android.R.drawable.ic_menu_more
		final View v = LayInf.inflate(resId("video_view","layout"),null,false);
		cur = (TextView) v.findViewWithTag("currentTime");
		tot = (TextView) v.findViewWithTag("totalTime");
		mVid = (VideoViewCompat) v.findViewWithTag("video");
		rr = (ImageView) v.findViewWithTag("rewind");
		ff = (ImageView) v.findViewWithTag("fastfoward");
		play = (ImageView) v.findViewWithTag("play");
		mSeek = (SeekBar) v.findViewWithTag("seek");
		this.addView(v);
		mVid.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

				@Override
				public void onCompletion(MediaPlayer p1)
				{
					p1.stop();
					play.setImageResource(resId("ic_media_play","drawable"));
				}
			});
		play.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					getParent().requestDisallowInterceptTouchEvent(true);
					if(mVid.isPlaying()){
						mVid.pause();
					}else{
						mVid.start();
					}
					if(mVid.isPlaying()){
						play.setImageResource(resId("ic_media_pause","drawable"));
					}else{
						play.setImageResource(resId("ic_media_play","drawable"));
					}
				}
			});
		rr.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if(mVid.getCurrentPosition()>0){
						mVid.seekTo(mVid.getCurrentPosition()-2000);
					}
				}

				
			});
		ff.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if(mVid.getCurrentPosition()>0){
						mVid.seekTo(mVid.getCurrentPosition()+2000);
					}
				}
		});
		mSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

				@Override
				public void onProgressChanged(SeekBar p1,final int p2, boolean p3)
				{
					mH.removeCallbacks(mRun);
					if(p3&&mVid.canSeekBackward()&&mVid.canSeekForward()){
						mVid.seekTo(mVid.getDuration()*p2/mSeek.getMax());
					}
				}

				@Override
				public void onStartTrackingTouch(SeekBar p1)
				{
					mH.removeCallbacks(mRun);
				}

				@Override
				public void onStopTrackingTouch(SeekBar p1)
				{
					mH.post(mRun);
				}
			});
	}

	@Override
	protected void onConfigurationChanged(Configuration p1)
	{
		prepareVideo();
		super.onConfigurationChanged(p1);
	}
	
	private int resId(String p0, String p1)
	{
		return getResources().getIdentifier(p0,p1,getContext().getPackageName());
	}

	@Override
	protected void onAttachedToWindow()
	{
		// TODO: Implement this method
		super.onAttachedToWindow();
		IntentFilter ig = new IntentFilter("playVideo");
		ig.addAction(Intent.ACTION_TIME_CHANGED);
		ig.addAction(Intent.ACTION_TIME_TICK);
		getContext().registerReceiver(mReceiver,ig);
		playCurrentVideo();
		mH.postDelayed(mRun,1000);
	}

	@Override
	protected void onDetachedFromWindow()
	{
		// TODO: Implement this method
		super.onDetachedFromWindow();
		getContext().unregisterReceiver(mReceiver);
		mH.removeCallbacks(mRun);
	}
}

package com.ali.entertaiment;
import android.content.*;
import android.graphics.*;
import android.media.MediaPlayer.*;
import android.media.*;
import android.util.*;
import android.view.*;
import android.view.TextureView.*;
import android.widget.MediaController.*;
import java.util.*;
import android.net.*;
import java.io.*;
import android.widget.*;
import android.view.accessibility.*;

public class VideoViewCompat extends TextureView implements
SurfaceTextureListener,MediaPlayerControl
{
	Map<String,String> mHeader;
	MediaPlayer mMediaPlayer=null;
	private Context mCtx;
	private Surface mSur;
	private Uri mVideoUri;
	static int STATE_ERR = -1,STATE_IDLE = 0,
	STATE_PREPARING=1,STATE_PREPARED = 2,STATE_PLAYING = 3,
	STATE_PAUSED = 4,STATE_PLAYBACK_COMPLETE = 5;
	int mCurrentState = STATE_IDLE,mTargetState = STATE_IDLE,
	mDur,mVidH,mVidW;
	OnPreparedListener mPrepared = new OnPreparedListener(){

		@Override
		public void onPrepared(MediaPlayer p1)
		{
			mCurrentState = STATE_PREPARED;
			Metadata m = p1.getMetadata(MediaPlayer.METADATA_ALL,MediaPlayer.BYPASS_METADATA_FILTER);
			if(m!=null){
				mCanPause = !m.has(Metadata.PAUSE_AVAILABLE)||m.getBoolean(Metadata.PAUSE_AVAILABLE);
				mCanSeekBack = !m.has(Metadata.SEEK_BACKWARD_AVAILABLE)||m.getBoolean(Metadata.SEEK_BACKWARD_AVAILABLE);
				mCanSeekFow = !m.has(Metadata.SEEK_FORWARD_AVAILABLE)||m.getBoolean(Metadata.SEEK_FORWARD_AVAILABLE);
			}else{
				mCanSeekFow = mCanPause = mCanSeekBack = true;
			}
			int seekToPos = mSeekWhenPrepared;
			if(seekToPos!=0){
				seekTo(seekToPos);
			}
			if(mTargetState==STATE_PLAYING){
				start();
			}
		}
	};
	OnCompletionListener mCompletion = new OnCompletionListener(){

		@Override
		public void onCompletion(MediaPlayer p1)
		{
			mCurrentState = STATE_PLAYBACK_COMPLETE;
			mTargetState = STATE_PLAYBACK_COMPLETE;
			p1.stop();
		}
	};
	OnErrorListener mErr = new OnErrorListener(){

		@Override
		public boolean onError(MediaPlayer p1, int p2, int p3)
		{
			mCurrentState=mTargetState = STATE_ERR;
			Toast.makeText(getContext(),"ERROR PLAYING VIDEO WITH MESSAGE: "+String.valueOf(p2)+" and "+String.valueOf(p3),500).show();
			return true;
		}
	};
	OnBufferingUpdateListener mBuff = new OnBufferingUpdateListener(){

		@Override
		public void onBufferingUpdate(MediaPlayer p1, int p2)
		{
			mCurrentBuffer = p2;
		}
	};
	OnVideoSizeChangedListener mVidSizeChanged = new OnVideoSizeChangedListener(){

		@Override
		public void onVideoSizeChanged(MediaPlayer p1, int p2, int p3)
		{
			mVidH = p1.getVideoHeight();
			mVidW = p1.getVideoWidth();
		}
	};
	int mSeekWhenPrepared,mCurrentBuffer;
	boolean mCanPause,mCanSeekBack,mCanSeekFow;
	public VideoViewCompat(Context c,AttributeSet a){
		super(c,a);
		mCtx = c;
		initViewControll();
	}
	
	public void resume(){
		openVideo();
	}
	public void setOnPreparedListener(OnPreparedListener l){
		mPrepared = l;
	}

	public void setOnErrorListener(OnErrorListener l){
		mErr = l;
	}
	
	public void setOnCompletionListener(OnCompletionListener l){
		mCompletion = l;
	}
	
	private void openVideo()
	{
		if(mVideoUri==null||mSur==null){
			return;
		}
		Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);
		release(false);
		try{
		    mMediaPlayer = new MediaPlayer();
		    mMediaPlayer.setOnBufferingUpdateListener(mBuff);
			mMediaPlayer.setDataSource(mCtx, mVideoUri, mHeader);
			mMediaPlayer.setOnPreparedListener(mPrepared);
			mMediaPlayer.setOnCompletionListener(mCompletion);
			mMediaPlayer.setOnVideoSizeChangedListener(mVidSizeChanged);
			mDur = -1;
			mCurrentBuffer = 0;
			mMediaPlayer.setSurface(mSur);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.prepareAsync();
			mCurrentState = STATE_PREPARING;
		}
		catch (IOException e)
		{
			mCurrentState = STATE_ERR;
			mTargetState = STATE_ERR;
			mErr.onError(mMediaPlayer,MediaPlayer.MEDIA_ERROR_TIMED_OUT,MediaPlayer.MEDIA_ERROR_UNKNOWN);
			Toast.makeText(getContext(),"Error while playing video please click other video",200).show();
		}
		catch (SecurityException e)
		{
			mCurrentState = STATE_ERR;
			mTargetState = STATE_ERR;
			mErr.onError(mMediaPlayer,MediaPlayer.MEDIA_ERROR_TIMED_OUT,MediaPlayer.MEDIA_ERROR_UNKNOWN);
			Toast.makeText(getContext(),"Error while playing video please click other video",200).show();
		}
		catch (IllegalArgumentException e)
		{
			mCurrentState = STATE_ERR;
			mTargetState = STATE_ERR;
			mErr.onError(mMediaPlayer,MediaPlayer.MEDIA_ERROR_TIMED_OUT,0);
			Toast.makeText(getContext(),"Error while playing video please click other video",200).show();
			return;
		}
		catch (IllegalStateException e)
		{
			mCurrentState = STATE_ERR;
			mTargetState = STATE_ERR;
			mErr.onError(mMediaPlayer,MediaPlayer.MEDIA_ERROR_TIMED_OUT,0);
			Toast.makeText(getContext(),"Error while playing video please click other video",200).show();
			return;
		}
	}
	
	public void setVideoPath(String path){
		setVideoURI(Uri.parse(path));
	}
	
	public void suspend(){
		release(false);
	}

	public void setVideoURI(Uri parse)
	{
		setVideoURI(parse,null);
	}

	private void setVideoURI(Uri parse, Map<String, String> mHeader)
	{
		mVideoUri = parse;
		this.mHeader = mHeader;
		mSeekWhenPrepared = 0;
		openVideo();
		requestLayout();
		invalidate();
	}

	private void initViewControll()
	{
		this.setSurfaceTextureListener(this);
		mMediaPlayer = new MediaPlayer();
		mCurrentState = STATE_IDLE;
		mTargetState = STATE_IDLE;
	}
	@Override
	public boolean canPause()
	{
		// TODO: Implement this method
		return mCanPause;
	}

	@Override
	public boolean canSeekBackward()
	{
		// TODO: Implement this method
		return mCanSeekBack;
	}

	@Override
	public boolean canSeekForward()
	{
		// TODO: Implement this method
		return mCanSeekFow;
	}

	@Override
	public int getAudioSessionId()
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int getBufferPercentage()
	{
		if(mMediaPlayer!=null){
			return mCurrentBuffer;
		}
		return 0;
	}

	@Override
	public int getCurrentPosition()
	{
		// TODO: Implement this method
		return isInState()?mMediaPlayer.getCurrentPosition():0;
	}

	@Override
	public int getDuration()
	{
		if(isInState()){
			if(mDur>0){
				return mDur;
			}
			mDur = mMediaPlayer.getDuration();
			return mDur;
		}
		mDur = -1;
		return mDur;
	}

	@Override
	public boolean isPlaying()
	{
		// TODO: Implement this method
		return mMediaPlayer.isPlaying()&&isInState();
	}

	@Override
	public void pause()
	{
		if(isInState()){
			if(mMediaPlayer.isPlaying()){
				mMediaPlayer.pause();
				mCurrentState = STATE_PAUSED;
			}
		}
		mTargetState = STATE_PAUSED;
		if(mMediaPlayer==null){
			Toast.makeText(getContext(),"Error while playing video please click other video",200).show();
		}
	}

	void release(boolean b){
		if(mMediaPlayer!=null){
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer=null;
			mCurrentState = STATE_IDLE;
			if(b){
				mTargetState = STATE_IDLE;
			}
		}
	}
	@Override
	public void seekTo(int p1)
	{
		if(isInState()){
			mMediaPlayer.seekTo(p1);
			mSeekWhenPrepared = 0;
		}else{
			mSeekWhenPrepared = p1;
		}
	}
	
	public void stopPlayback(){
		if(mMediaPlayer!=null){
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer=null;
			mCurrentState = STATE_IDLE;
			mTargetState = STATE_IDLE;
		}
	}

	@Override
	public void start()
	{
		if(isInState()){
			mMediaPlayer.start();
			mCurrentState = STATE_PLAYING;
		}
		mTargetState = STATE_PLAYING;
	}

	@Override
	public void onInitializeAccessibilityEvent(AccessibilityEvent p1)
	{
		// TODO: Implement this method
		super.onInitializeAccessibilityEvent(p1);
		p1.setClassName("VideoViewCompat");
	}

	@Override
	public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo p1)
	{
		// TODO: Implement this method
		super.onInitializeAccessibilityNodeInfo(p1);
		p1.setClassName("VideoViewCompat");
	}
	
	public MediaPlayer getMediaPlayer(){
		if(mMediaPlayer!=null){
			return mMediaPlayer;
		}
		return null;
	}

	private boolean isInState()
	{
		// TODO: Implement this method
		return (mMediaPlayer!=null&&mCurrentState!=STATE_IDLE
		&&mCurrentState!=STATE_PREPARING&&mCurrentState!=STATE_ERR);
	}


	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture p1, int p2, int p3)
	{
		mSur = new Surface(p1);
		openVideo();
		
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture p1)
	{
		release(true);
		return true;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture p1, int p2, int p3)
	{
		
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture p1)
	{
	}

}

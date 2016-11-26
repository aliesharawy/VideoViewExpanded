package com.ali.entertaiment;
import android.content.*;
import android.util.*;
import android.view.*;
import android.support.v4.widget.*;
import android.support.v4.view.*;
import android.view.InputQueue.*;

public class YoutubeLayout extends ViewGroup
{
	ViewDragHelper mDragHelp;
	View mVideo,mList;
	float mInitilMX,mInitialMY,mDragOffset;
	int mTop,mDragRange;
	@Override
	protected void onLayout(boolean p1, int l, int t, int r, int b)
	{
		mDragRange = getHeight()-mVideo.getHeight();
		mVideo.layout(
		0,
		mTop,
		r,
		mTop+mVideo.getMeasuredHeight());
		mList.layout(
		0,
		mTop+mVideo.getMeasuredHeight(),
		r,
		mTop+b);
	}
	public YoutubeLayout(Context c,AttributeSet a){
		super(c,a,0);
		mDragHelp = ViewDragHelper.create(this,1f,new MyCallback());
	}
	void maximaze(){
		smoothSlideTo(0f);
	}

	@Override
	protected void onFinishInflate()
	{
		mVideo = findViewWithTag("video");
		mList = findViewWithTag("list");
	}

	
	private boolean smoothSlideTo(float p0)
	{
		int topB = getPaddingTop();
		int y = (int) (topB+p0*mDragRange);
		if(mDragHelp.smoothSlideViewTo(mVideo,mVideo.getLeft(),y)){
			ViewCompat.postInvalidateOnAnimation(this);
			return true;
		}
		return false;
	}
	class MyCallback extends ViewDragHelper.Callback
	{

		@Override
		public boolean tryCaptureView(View p1, int p2)
		{
			// TODO: Implement this method
			return p1==mVideo;
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy)
		{
			mTop = top;
			mDragOffset = (float)(top/mDragRange);
			mVideo.setPivotX(mVideo.getWidth());
			mVideo.setPivotY(mVideo.getHeight());
			mVideo.setScaleX(1-mDragOffset/2);
			mVideo.setScaleY(1-mDragOffset/2);
			mList.setAlpha(1-mDragOffset);
			requestLayout();
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel)
		{
			int Top = getPaddingTop();
			if(yvel>0||(yvel==0&&mDragOffset>0.5f)){
				Top+=mDragRange;
			}
			mDragHelp.settleCapturedViewAt(releasedChild.getLeft(),Top);
		}

		@Override
		public int getViewVerticalDragRange(View child)
		{
			// TODO: Implement this method
			return mDragRange;
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy)
		{
			int topBound = getPaddingTop(),
			bottomBound = getHeight()-mVideo.getHeight()-mVideo.getPaddingBottom(),
			newTop = Math.min(Math.max(top,topBound),bottomBound);
			return newTop;
		}
	}

	@Override
	public void computeScroll()
	{
		if(mDragHelp.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent p1)
	{
		int ac = MotionEventCompat.getActionMasked(p1);
		if(ac!=MotionEvent.ACTION_DOWN){
			mDragHelp.cancel();
			return super.onInterceptTouchEvent(p1);
		}
		if(ac==MotionEvent.ACTION_UP&&ac==MotionEvent.ACTION_CANCEL){
			mDragHelp.cancel();
			return false;
		}
		final float x = p1.getX(),
		y = p1.getY();
		boolean tap = false;
		switch(ac){
			case MotionEvent.ACTION_DOWN:
				mInitialMY = y;
				mInitilMX = x;
				break;
			case MotionEvent.ACTION_MOVE:
				final float ady = Math.abs(y-mInitialMY),
				adx = Math.abs(x-mInitilMX);
				int slop = mDragHelp.getTouchSlop();
				if(ady>slop&&adx>ady){
					mDragHelp.cancel();
					return false;
				}
				return mDragHelp.shouldInterceptTouchEvent(p1)||tap;
		}
		return false; 
	}

	@Override
	public boolean onTouchEvent(MotionEvent p1)
	{
		mDragHelp.processTouchEvent(p1);
		final int act = p1.getAction();
		float x = p1.getX(),
		y = p1.getY();
		boolean isViewUnder = mDragHelp.isViewUnder(mVideo,(int)x,(int)y);
		switch(act){
			case MotionEvent.ACTION_DOWN:{
				mInitialMY = y;
				mInitilMX = x;
				break;
				}
			case MotionEvent.ACTION_UP:{
				final float dx = x-mInitilMX,
				dy = y-mInitialMY;
				int tc = mDragHelp.getTouchSlop();
				if(dx*dx+dy*dy<tc*tc&&isViewUnder){
					if(mDragOffset==0){
						smoothSlideTo(1f);
					}else{
						smoothSlideTo(0f);
					}
				}
				break;
			}
		}
		return isViewHit(mVideo,(int)x,(int)y)||isViewHit(mList,(int)x,(int)y);
	}

	private boolean isViewHit(View o, int x, int y)
	{
		int viewLoc[] = new int[2];
		o.getLocationOnScreen(viewLoc);
		int parentLoc[] = new int[2];
		this.getLocationOnScreen(parentLoc);
		int scX = parentLoc[0]+x,
		scY = parentLoc[1]+y;
		return scX>=viewLoc[0]&&scX<viewLoc[0]+o.getWidth()&&
		scY>=viewLoc[1]&&scY<viewLoc[1]+o.getHeight();
	}

	@Override
	protected void onMeasure(int p1, int p2)
	{
		measureChildren(p1,p2);
		int maxW = MeasureSpec.getSize(p1),
		maxH = MeasureSpec.getSize(p2);
		setMeasuredDimension(resolveSizeAndState(maxW,p1,0),
		resolveSizeAndState(maxH,p2,0));
	}
	
}

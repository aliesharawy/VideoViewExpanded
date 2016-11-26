package com.ali.entertaiment;

import android.content.*;
import android.graphics.*;
import java.util.concurrent.*;

public class Video
{
	public String autor,dateCre;

	public void setThumbnail(Bitmap thumbnail)
	{
		Thumbnail = thumbnail;
	}

	public Bitmap getThumbnail()
	{
		return Thumbnail;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setDuration(long duration)
	{
		this.duration = duration;
	}

	public long getDuration()
	{
		return duration;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public long getId()
	{
		return id;
	}

	public Bitmap Thumbnail;
	public String title;
	public long duration,id;
	public Video(String _title,long _dur,long _id,Bitmap thumb,String f){
		title = _title;
		duration = _dur;
		id = _id;
		Thumbnail = thumb;
		this.dateCre = f;
	}
	public static class Utils
	{

		public static int resId(Context c,String p0, String p1)
		{
			// TODO: Implement this method
			return c.getResources().getIdentifier(p0,p1,c.getPackageName());
		}
		public static String convertToString(int d){
			String ddkkmmss = String.format("%02d %02d:%02d:%02d",TimeUnit.MILLISECONDS.toDays(d),TimeUnit.MILLISECONDS.toHours(d)-TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(d)),TimeUnit.MILLISECONDS.toMinutes(d)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(d)),
											TimeUnit.MILLISECONDS.toSeconds(d)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(d))),
				kkmmss = String.format("%02d:%02d:%02d",TimeUnit.MILLISECONDS.toHours(d),TimeUnit.MILLISECONDS.toMinutes(d)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(d)),
									   TimeUnit.MILLISECONDS.toSeconds(d)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(d))),
				mmss = String.format("%02d:%02d",TimeUnit.MILLISECONDS.toMinutes(d)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(d)),
									 TimeUnit.MILLISECONDS.toSeconds(d)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(d)));
			return d>=86400000?ddkkmmss:(d>=3600000?kkmmss:mmss);
		}
	}
}

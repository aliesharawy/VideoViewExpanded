package com.ali.entertaiment;


import android.content.*;
import android.provider.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.ali.entertaiment.*;
import java.util.*;

public class YoutubeAdapter extends BaseAdapter
	{

		private List<Video> myList;

	private LayoutInflater mLayIn;

	private Context mCtx;
		YoutubeAdapter(Context c,List<Video> mList){
			myList = mList;
			mCtx = c;
			mLayIn = LayoutInflater.from(c);
			notifyDataSetChanged();
		}

		@Override
		public int getCount()
		{
			return myList.size();
		}

		@Override
		public Object getItem(int p1)
		{
			
			return myList.get(p1);
		}

		@Override
		public long getItemId(int p1)
		{
			// TODO: Implement this method
			return 0;
		}

		@Override
		public View getView(final int p1, View p2, ViewGroup p3)
		{
			ViewHolder h = null;
			if(p2==null){
			h = new ViewHolder();
			p2 = mLayIn.inflate(Video.Utils.resId(mCtx,"item_video","layout"),p3,false);
			h.Thumb = (ImageView) p2.findViewById(Video.Utils.resId(mCtx,"thumb","id"));
			h.title = (TextView) p2.findViewById(Video.Utils.resId(mCtx,"title","id"));
			h.duration = (TextView) p2.findViewById(Video.Utils.resId(mCtx,"duration","id"));
			h.date = (TextView) p2.findViewById(Video.Utils.resId(mCtx,"dateCre","id"));
			p2.setTag(h);
			}else{
				h = (ViewHolder) p2.getTag();
			}
			Video vid = myList.get(p1);
			h.date.setText(vid.dateCre);
			h.Thumb.setImageBitmap(vid.Thumbnail);
			h.title.setText(vid.title);
			h.duration.setText(Video.Utils.convertToString((int)vid.duration));
			p2.setClickable(true);
			p2.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View p)
					{
						Video vid = myList.get(p1);
						String s  = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,vid.id).toString();
						Intent i = new Intent("playVideo");
						i.putExtra("video",s);
						i.putExtra("dur",vid.getDuration());
						p.getContext().sendBroadcast(i);
					}
				});
			return p2;
		}
		
		static class ViewHolder{
			TextView title,duration,date;
			ImageView Thumb;
		}
	}

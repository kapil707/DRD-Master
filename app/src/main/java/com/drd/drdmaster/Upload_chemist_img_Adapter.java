package com.drd.drdmaster;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Upload_chemist_img_Adapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<Upload_chemist_img_get_or_set> movieItems;

    public Upload_chemist_img_Adapter(Context context, List<Upload_chemist_img_get_or_set> arraylist)
    {
        this.context = context;
        this.movieItems = arraylist;
    }
 
    @Override
	public int getCount() {
		return movieItems.size();
	}
    
   
    @Override
	public View getView(int position, View view, ViewGroup parent) 
    {
		// TODO Auto-generated method stub
       
        LayoutInflater abc = ((Activity) context).getLayoutInflater();
		View itemView = abc.inflate(R.layout.upload_chemist_img_item, null,true);
		final Upload_chemist_img_get_or_set m = movieItems.get(position);

        LinearLayout upload_chemist_img_LinearLayout = (LinearLayout) itemView.findViewById(R.id.upload_chemist_img_LinearLayout);
        TextView upload_time = (TextView) itemView.findViewById(R.id.upload_time);
        ImageView upload_image = itemView.findViewById(R.id.upload_image);

        LinearLayout upload_chemist_img_LinearLayout1 = (LinearLayout) itemView.findViewById(R.id.upload_chemist_img_LinearLayout1);
        TextView upload_time1 = (TextView) itemView.findViewById(R.id.upload_time1);
        ImageView upload_image1 = itemView.findViewById(R.id.upload_image1);

        int intid = 0;
        intid = Integer.valueOf(m.intid());
        if(intid%2==0)
        {
            upload_chemist_img_LinearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            upload_chemist_img_LinearLayout1.setVisibility(View.VISIBLE);
        }

        upload_time.setText(m.time());
        Picasso.with(context).load(m.image()).into(upload_image);

        upload_time1.setText(m.time());
        Picasso.with(context).load(m.image()).into(upload_image1);

        return itemView;
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
}
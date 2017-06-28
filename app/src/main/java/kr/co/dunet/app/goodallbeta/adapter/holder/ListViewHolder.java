package kr.co.dunet.app.goodallbeta.adapter.holder;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.dunet.app.goodallbeta.R;

/**
 * Created by hanbit on 2017-06-08.
 */

public class ListViewHolder extends RecyclerView.ViewHolder{

     private TextView roomName;
     private TextView roomDate;
     private TextView roomDescription;
     private ImageView roomCreateImage;


    public ListViewHolder(View view) {
        super(view);
        roomName = (TextView) view.findViewById(R.id.room_name);
        roomDate = (TextView) view.findViewById(R.id.room_date);
        roomDescription = (TextView) view.findViewById(R.id.room_description);
        roomCreateImage = (ImageView)view.findViewById(R.id.room_create_image);
    }

    public void setText(ArrayList<ListData> items , int position){
            roomName.setText(items.get(position).roomName);
            roomDate.setText(items.get(position).roomDate);
            roomDescription.setText(items.get(position).roomDescription);
    }
    public void setImageView(ArrayList<Bitmap> bitmap){

        roomCreateImage.setImageBitmap(bitmap.get(1));
    }

}

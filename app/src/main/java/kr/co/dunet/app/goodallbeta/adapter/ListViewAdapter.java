package kr.co.dunet.app.goodallbeta.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.co.dunet.app.goodallbeta.R;
import kr.co.dunet.app.goodallbeta.adapter.holder.ListData;
import kr.co.dunet.app.goodallbeta.adapter.holder.ListViewHolder;

/**
 * Created by hanbit on 2017-06-07.
 */

public class ListViewAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private ArrayList<ListData> items;
    private ArrayList<ListData> itemList;
//    Context context;

    public ListViewAdapter(ArrayList<ListData> items ) {
        if (items == null) {
            throw new IllegalArgumentException("list must not null");
        }
//        this.context = context;
        this.items = items;
        this.itemList = new ArrayList<>();
        this.itemList.addAll(items);


        Log.d("item 들옴? ", items.toString());
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_list, parent, false);
        ListViewHolder holder = new ListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {

        holder.setText(items, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

//    public void filter(String charText){
//        charText = charText.toLowerCase(Locale.getDefault());
//        itemList.clear();
//        if(charText.length() == 0){
//            itemList.addAll(items);
//        }else{
//            for(ListData listdata : itemList){
//                String name = context.getResources().getString();
//                if(name.toLowerCase().contains(charText)){
//                    itemList.add(list);
//                }
//            }
//        }
//    }
}

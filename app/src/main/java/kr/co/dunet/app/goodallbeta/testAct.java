package kr.co.dunet.app.goodallbeta;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.dunet.app.goodallbeta.adapter.holder.ListData;

public class testAct extends AppCompatActivity {

    @BindView(R.id.recycleView)
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        ArrayList<ListData> list = new ArrayList<>();
        list.add(new ListData("a", "b" , "c"));
        list.add(new ListData("b", "c" , "d"));
        list.add(new ListData("c", "d" , "e"));
//        layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);

//        ListViewAdapter adapter = new ListViewAdapter(list);
//        recyclerView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();

    }
}

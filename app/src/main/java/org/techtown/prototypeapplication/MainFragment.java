package org.techtown.prototypeapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.techtown.prototypeapplication.VO.Data;
import org.techtown.prototypeapplication.VO.SharedObject;

import java.util.Arrays;
import java.util.List;



public class MainFragment extends Fragment {
    SharedObject shared=SharedObject.getInstance();
    private RecyclerAdapter adapter;
    private RoomListFragment roomListFragment;
    public MainFragment(RoomListFragment roomListFragment){

        this.roomListFragment =roomListFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerAdapter((MainActivity)getActivity(),roomListFragment);

        recyclerView.setAdapter(adapter);
        getData();
//        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View v, int pos) {
//                Log.i("LatteTest","inFragment: "+pos);
////                shared.put("position: "+pos);
//            }
//        });

        // Inflate the layout for this fragment
        return rootView;
    }
    public void getData(){
        List<String> listTitle = Arrays.asList("국화", "사막");
        List<String> listContent = Arrays.asList(
                "이 꽃은 국화입니다.",
                "여기는 사막입니다."

        );
        List<Integer> listResId = Arrays.asList(
                R.drawable.latteroom_logo_white,
                R.drawable.latteroom_logo_white

        );
        for (int i = 0; i < listTitle.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            Data data = new Data();
            data.setTitle(listTitle.get(i));
            data.setContent(listContent.get(i));
            data.setResId(listResId.get(i));

            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }
}

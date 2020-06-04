package org.techtown.prototypeapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.techtown.prototypeapplication.VO.Data;
import org.techtown.prototypeapplication.VO.SharedObject;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private ArrayList<Data> listData = new ArrayList<>();
    private OnItemClickListener mListener = null;
    private SharedObject shared = SharedObject.getInstance();
    private RoomListFragment roomListFragment;
    private MainActivity activity;
    public RecyclerAdapter( MainActivity activity,RoomListFragment roomListFragment){

        this.activity = activity;
        this.roomListFragment = roomListFragment;
    }

    public interface OnItemClickListener{
        void onItemClick(View v,int pos);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(Data data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textView1;
        private TextView textView2;
        private ImageView imageView;

        ItemViewHolder(View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        Log.i("LatteTest","RecyclerAdapter: "+pos);
                        shared.put("position: "+pos);
                        activity.replaceFragment(roomListFragment);

                    }
                }
            });
        }

        void onBind(Data data) {
            textView1.setText(data.getTitle());
            textView2.setText(data.getContent());
            imageView.setImageResource(data.getResId());
        }


    }

}

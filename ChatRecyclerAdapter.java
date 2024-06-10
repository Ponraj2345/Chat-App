package com.example.comeback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>{

    private Context context;
    private ArrayList<MessageModel> datas=new ArrayList<>();

    public ChatRecyclerAdapter(Context context, ArrayList<MessageModel> datas) {
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.rec_chat_layout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
           if(datas.get(position).getSenderId().equals(datas.get(position).getCurrentUserId())){
               holder.rightCard.setVisibility(View.GONE);
               holder.leftCard.setVisibility(View.VISIBLE);
               holder.tvSend.setText(datas.get(position).getMessage());
           }
           else{
               holder.leftCard.setVisibility(View.GONE);
               holder.rightCard.setVisibility(View.VISIBLE);
               holder.tvReceive.setText(datas.get(position).getMessage());
           }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView leftCard,rightCard;
        TextView tvSend,tvReceive;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leftCard= itemView.findViewById(R.id.left_side_card);
            rightCard=itemView.findViewById(R.id.right_side_card);
            tvSend=itemView.findViewById(R.id.sender_msg);
            tvReceive=itemView.findViewById(R.id.receiver_msg);
        }
    }
}

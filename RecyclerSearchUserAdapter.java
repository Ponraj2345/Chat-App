package com.example.comeback;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class RecyclerSearchUserAdapter extends RecyclerView.Adapter<RecyclerSearchUserAdapter.ViewHolder>{
    private ArrayList<SearchModelClass> datas=new ArrayList<>();
    private Context context;
    private SearchUserSelectedListener listener;

    public RecyclerSearchUserAdapter(ArrayList<SearchModelClass> datas, Context context,SearchUserSelectedListener listener) {
        this.datas = datas;
        this.context = context;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.rec_search_user_layout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvUserName.setText(datas.get(position).getUsername());
        FirebaseStorage.getInstance().getReference().child("pic.jpg").child(datas.get(position).getUserId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
              Glide.with(context).load(uri).error(R.drawable.baseline_person_24).into(holder.imgProfilePic);
            }
        });
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSearchUserSelected(datas.get(holder.getAdapterPosition()).getUserId());
            }
        });
        //Glide.with(context).load(datas.get(position).getImageUri()).into(holder.imgProfilePic);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfilePic;
        TextView tvUserName;
        CardView parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfilePic=itemView.findViewById(R.id.img_search_pic);
            tvUserName=itemView.findViewById(R.id.tv_search_username);
            parentLayout=itemView.findViewById(R.id.search_user_whole_layout);
        }
    }
}

package com.example.comeback;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RecentChatRecyclerAdapter extends RecyclerView.Adapter<RecentChatRecyclerAdapter.ViewHolder>{

    private ArrayList<ChatRoomModel> datas;
    private Context context;
    private RecentChatUserSelected recentChatUserSelected;

    public RecentChatRecyclerAdapter(ArrayList<ChatRoomModel> datas, Context context,RecentChatUserSelected recentChatUserSelected) {
        this.datas = datas;
        this.context = context;
        this.recentChatUserSelected=recentChatUserSelected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.rec_layout_recent_chat,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
           List<String> ids=datas.get(position).getUserIds();
           if(ids.get(0).equals(FirebaseAuth.getInstance().getUid())){
               FirebaseFirestore.getInstance().collection("user details").document(ids.get(1))
                       .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                               if(task.isSuccessful()){
                                   DocumentSnapshot snapshot=task.getResult();
                                   String username=snapshot.getString("username");
                                   holder.tvRecentUsername.setText(username);
                               }
                           }
                       });
           }

           else{
               FirebaseFirestore.getInstance().collection("user details").document(ids.get(0))
                       .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                               if(task.isSuccessful()){
                                   DocumentSnapshot snapshot=task.getResult();
                                   String username=snapshot.getString("username");
                                   holder.tvRecentUsername.setText(username);
                               }
                           }
                       });
           }
           if(datas.get(position).getLastMessageSender().equals(FirebaseAuth.getInstance().getUid())){
               holder.tvLastMessage.setText("You: "+datas.get(position).getLastMessage());
           }
           else
               holder.tvLastMessage.setText(datas.get(position).getLastMessage());
           if(!datas.get(position).getLastMessage().isEmpty())
             holder.tvSenttime.setText(convertToTime(datas.get(position).getTime()));
           holder.recentWholeLayout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(ids.get(0).equals(FirebaseAuth.getInstance().getUid()))
                       recentChatUserSelected.onRecentUserSelected(ids.get(1));
                   else
                       recentChatUserSelected.onRecentUserSelected(ids.get(0));

               }
           });
    }

    @Override
    public int getItemCount() {

        return datas.size();
    }

    public static String convertToTime(Timestamp t){
        Date date =t.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault()); // Set to your desired time zone
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView recentWholeLayout;
        TextView tvRecentUsername,tvLastMessage,tvSenttime;
        ImageView imgRecentPic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recentWholeLayout=itemView.findViewById(R.id.recent_chat_whole_layour);
            tvLastMessage=itemView.findViewById(R.id.tv_last_msg);
            tvRecentUsername=itemView.findViewById(R.id.tv_recent_username);
            tvSenttime=itemView.findViewById(R.id.tv_recent_time);
            imgRecentPic=itemView.findViewById(R.id.img_recent_pic);

        }
    }
}

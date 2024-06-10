package com.example.comeback;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    ImageButton backArrow, sendMessage;
    EditText etMessage;
    String chatRoomId;
    ChatRoomModel model;
    ArrayList<MessageModel> datas;
    ImageView imgProfPic;
    String uid;
    TextView tvUsername;
    RecyclerView chatRecycler;
    ChatRecyclerAdapter adapter;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        uid = getIntent().getStringExtra("uid");
        auth = FirebaseAuth.getInstance();
        chatRoomId = getChatRoomId(auth.getUid(), uid);
        DocumentReference chatRoomReference = FirebaseFirestore.getInstance().collection("chat room").document(chatRoomId);
        chatRoomReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.getResult().exists()) {
                    DocumentReference dr = FirebaseFirestore.getInstance().collection("chat room").document(chatRoomId);
                    model = new ChatRoomModel(chatRoomId, "", Arrays.asList(auth.getUid(),uid),  "", Timestamp.now());
                    dr.set(model);
                } else {
                    model = new ChatRoomModel(chatRoomId, "", Arrays.asList(auth.getUid(),uid), "", Timestamp.now());
                    chatRoomReference.set(model);
                }
            }
        });


        backArrow = findViewById(R.id.img_back_arr);
        etMessage = findViewById(R.id.chat_box_et);
        imgProfPic = findViewById(R.id.chat_profile_pic);
        tvUsername = findViewById(R.id.chat_tv_username);
        chatRecycler = findViewById(R.id.chat_recylcer);
        sendMessage = findViewById(R.id.btn_send_message);

        datas = new ArrayList<>();

        DocumentReference reference = FirebaseFirestore.getInstance().collection("user details").document(uid);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    tvUsername.setText(snapshot.getString("username"));
                }
            }
        });

        FirebaseStorage.getInstance().getReference().child("pic.jpg").child(uid).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(ChatActivity.this).load(uri).error(R.drawable.baseline_person_24).into(imgProfPic);
                            }
                        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ChatActivity.this, HomeActivity.class);
                i.putExtra("uid",uid);
                startActivity(i);
                finish();
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = etMessage.getText().toString().trim();
                if(message.isEmpty())
                    return;
                setSendMessage(message);
            }
        });
        setChatRecycler();
    }

    private String getChatRoomId(String user1, String user2) {
        if (user1.hashCode() < user2.hashCode())
            return user1 + "_" + user2;
        else
            return user2 + "_" + user1;
    }

    void setSendMessage(String message) {
        model.setLastMessage(message);
        model.setLastMessageSender(auth.getUid());

        DocumentReference chatRoomReference = FirebaseFirestore.getInstance().collection("chat room").document(chatRoomId);
        chatRoomReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    chatRoomReference.set(model);
                }

            }
        });

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("chat room").document(chatRoomId)
                .collection("chats");
        HashMap chatMap = new HashMap();
        chatMap.put("message", message);
        chatMap.put("sender id", auth.getUid());
        chatMap.put("time", Timestamp.now());
        collectionReference.add(chatMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful())
                    etMessage.setText("");
            }
        });
    }

    void setChatRecycler() {
        adapter = new ChatRecyclerAdapter(this, datas);
        chatRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        chatRecycler.setLayoutManager(layoutManager);
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("chat room").document(chatRoomId).collection("chats");
        Query query = collectionReference.orderBy("time", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                datas.clear();
                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        String message = snapshot.getString("message");
                        String userId = snapshot.getString("sender id");
                        datas.add(new MessageModel(message, userId, auth.getUid(),""));
                    }

                    adapter.notifyDataSetChanged();
                    chatRecycler.scrollToPosition(datas.size() - 1);
                }
            }

        });
    }

}
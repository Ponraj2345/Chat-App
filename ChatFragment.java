package com.example.comeback;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment implements RecentChatUserSelected{

    RecyclerView recentChatRecycler;
    RecentChatRecyclerAdapter recentAdapter;
    ArrayList<ChatRoomModel> models;
    FirebaseAuth auth;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_chat, container, false);
        recentChatRecycler=v.findViewById(R.id.recent_chat_recycler);
        models=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        setRecentChatRecycler();
        return v;
    }

    void setRecentChatRecycler(){
        recentAdapter=new RecentChatRecyclerAdapter(models,getContext(),this);
        Query query=FirebaseFirestore.getInstance().collection("chat room")
                .whereArrayContains("userIds",auth.getUid()).orderBy("time");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                models.clear();
                if(value!=null){
                    for(DocumentSnapshot snapshot:value.getDocuments()){
                        String lastmsg = snapshot.getString("lastMessage");
                        String lastmsgSenderId = snapshot.getString("lastMessageSender");
                        Timestamp time = snapshot.getTimestamp("time");
                        String chatRoomid = snapshot.getString("chatRoomId");
                        List<String> list = (List<String>) snapshot.get("userIds");
                        models.add(new ChatRoomModel(chatRoomid,lastmsg,list,lastmsgSenderId,time));
                    }
                    recentAdapter.notifyDataSetChanged();
                }
            }
        });
        recentChatRecycler.setAdapter(recentAdapter);
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        recentChatRecycler.setLayoutManager(manager);
    }

    @Override
    public void onRecentUserSelected(String userid) {
          Intent intent=new Intent(getContext(), ChatActivity.class);
          intent.putExtra("uid",userid);
          startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        setRecentChatRecycler();
       // recentAdapter.notifyDataSetChanged();
    }
}
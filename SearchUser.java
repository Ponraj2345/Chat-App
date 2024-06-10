package com.example.comeback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchUser extends AppCompatActivity implements SearchUserSelectedListener{


    SearchView searchViewSearchUser;
    private boolean isQueryExecuted = false;
    ProgressBar pgRecycler;
    RecyclerView recyclerView;
    ImageButton imgBackArr;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ArrayList<SearchModelClass> datas=new ArrayList<>();
        RecyclerSearchUserAdapter adapter=new RecyclerSearchUserAdapter(datas,this,this);
        pgRecycler=findViewById(R.id.pg_recycler);
        imgBackArr=findViewById(R.id.img_back_arr);
        searchViewSearchUser=findViewById(R.id.search_view_srch_user);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        auth=FirebaseAuth.getInstance();
        CollectionReference collectionReference= FirebaseFirestore.getInstance().collection("user details");

        imgBackArr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchUser.this,HomeActivity.class));
                finish();
            }
        });

        searchViewSearchUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Check if the query has already been executed
                if (!isQueryExecuted) {
                    Query firebaseQuery = collectionReference.whereGreaterThanOrEqualTo("username", query.trim());
                    firebaseQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot != null || !documentSnapshot.isEmpty()) {
                                    for (DocumentSnapshot snapshot : documentSnapshot.getDocuments()) {
                                        String userId = snapshot.getString("uid");
                                        String username = snapshot.getString("username");
                                        if (userId.equals(auth.getUid())) {
                                            username += " (Me)";
                                        }
                                        datas.add(new SearchModelClass(username, userId));
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                pgRecycler.setVisibility(View.GONE);
                            }
                            isQueryExecuted = true; // Set flag to true after executing the query
                        }
                    });
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pgRecycler.setVisibility(View.VISIBLE);
                isQueryExecuted = false; // Reset flag when the query text changes
                return false;
            }
        });


    }

    @Override
    public void onSearchUserSelected(String userid) {
       Intent chatIntent=new Intent(SearchUser.this, ChatActivity.class);
       chatIntent.putExtra("uid",userid);
       startActivity(chatIntent);
       finish();
    }
}
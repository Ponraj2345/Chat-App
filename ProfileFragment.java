package com.example.comeback;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import javax.xml.transform.Result;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth auth;
    Button btnSave, btnLogout;
    EditText etUserName;
    TextView tvUserPhno;
    ImageView imgProfilePic;
    TextInputLayout tipUserAbout;
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        auth=FirebaseAuth.getInstance();
        btnLogout=view.findViewById(R.id.btn_logout);
        btnSave=view.findViewById(R.id.btn_save);
        tvUserPhno=view.findViewById(R.id.tv_user_phno);
        etUserName=view.findViewById(R.id.et_profile_username);
        imgProfilePic=view.findViewById(R.id.img_profile_pic);
        tipUserAbout=view.findViewById(R.id.tip_user_about);

            StorageReference picReference=FirebaseStorage.getInstance().getReference().child("pic.jpg").child(auth.getUid());
            picReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getContext())
                            .load(uri)
                            .error(R.drawable.baseline_person_24)
                            .into(imgProfilePic);
                }
            });

        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pic=new Intent(Intent.ACTION_PICK);
                pic.setType("image/*");
                startActivityForResult(pic,1);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getContext(),LoginPhoneNumberActivity.class));
                if(getActivity()!=null)
                  getActivity().finish();
            }
        });

        DocumentReference reference=FirebaseFirestore.getInstance().collection("user details").document(auth.getUid());
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot=task.getResult();
                etUserName.setText(snapshot.getString("username"));
                tvUserPhno.setText(snapshot.getString("phone number"));
                tipUserAbout.getEditText().setText(snapshot.getString("user about"));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap map=new HashMap<>();
                map.put("username",etUserName.getText().toString());
                map.put("user about",tipUserAbout.getEditText().getText().toString());
                FirebaseFirestore.getInstance().collection("user details").document(auth.getUid())
                        .update(map).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                     if(task.isSuccessful())
                                         Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                                     else
                                         Toast.makeText(getContext(), "failed to save", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_CANCELED){
            Toast.makeText(getContext(), "failed to choose image", Toast.LENGTH_SHORT).show();
        } else if (requestCode==1 && resultCode==RESULT_OK) {
            Uri picUri = data.getData();
            imgProfilePic.setImageURI(picUri);
            FirebaseStorage.getInstance().getReference().child("pic.jpg").child(auth.getUid()).putFile(picUri);
        }
    }
}
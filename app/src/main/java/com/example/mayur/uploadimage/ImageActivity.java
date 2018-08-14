package com.example.mayur.uploadimage;

import android.app.DownloadManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener{

    private RecyclerView mrecyclerview;
    private ImageAdapter mAdapter;
    private DatabaseReference mDatabaseRef;

    private FirebaseDatabase mDatabase;

    private FirebaseStorage mStorage;

    private ImageView imageView;

    private ValueEventListener mDBListener;

    private ProgressBar mProgressCircle;

    private StorageReference fileRef;

    private List<Upload> mUploads;

    private FirebaseAuth mAuth;

    long  queueid;

    private DownloadManager dm;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String userID;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mrecyclerview=findViewById(R.id.recycler_view);
        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle=findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();
        mAdapter = new ImageAdapter(ImageActivity.this,mUploads);
        mrecyclerview.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(ImageActivity.this);

       /* mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef= mDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
*/


        mStorage= FirebaseStorage.getInstance();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        //get firebase user
       final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    Upload upload = postSnapshot.getValue(Upload.class);

                    upload.setmKey(postSnapshot.getKey());

                   mUploads.add(upload);
                }

                mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // if we set rule to false for read operation in Firebase Storage

                Toast.makeText(ImageActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onItemClick(int position) {

        Toast.makeText(this,"Click OK " + position,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDownload(int position) {

        Toast.makeText(this,"Download Click OK " + position,Toast.LENGTH_SHORT).show();
        dm = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://uploadimage-bcb40.firebaseio.com/uploads"));

         queueid= dm.enqueue(request);


         //https://www.youtube.com/watch?v=atZRWb6_QRs    video for download

    }



    @Override
    public void onDelete(int position) {

        //Toast.makeText(this,"Delete Click OK " + position,Toast.LENGTH_SHORT).show();

        Upload selectedItem = mUploads.get(position);
        final String selectedKey= selectedItem.getmKey();
        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageurl());

        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mDatabaseRef.child(selectedKey).removeValue();

                Toast.makeText(ImageActivity.this,"Item Deleted",Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override

    protected  void onDestroy(){

        mDatabaseRef.removeEventListener(mDBListener);

        super.onDestroy();

    }
}

package com.example.mayur.uploadimage;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MainActivity extends AppCompatActivity {

    Button chooseImg, uploadImg;
    EditText imageName;
    TextView showUploads,preview,selectImage;
    ImageView imgView,signOut;
    int PICK_IMAGE_REQUEST = 111;
    private FirebaseAuth auth;
    Uri filePath;
    ProgressDialog pd;


    private StorageReference mStorageRef;

    private DatabaseReference mDatabaseRef;

    private String uploadId;

    private StorageTask mUploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth=FirebaseAuth.getInstance();

        chooseImg = (Button) findViewById(R.id.chooseImg);
        uploadImg = (Button) findViewById(R.id.uploadImg);
        imgView = (ImageView) findViewById(R.id.imgView);
        imageName = findViewById(R.id.imagename);
        showUploads = findViewById(R.id.showupload);
        selectImage=findViewById(R.id.select);
        preview=findViewById(R.id.preview);

        signOut = findViewById(R.id.btnsignut);


        imageName.setVisibility(View.GONE);
        preview.setVisibility(View.GONE);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading Image ..");

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");


        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

                imgView.setVisibility(View.VISIBLE);
                preview.setVisibility(View.VISIBLE);
                imageName.setVisibility(View.GONE);
                showUploads.setVisibility(View.VISIBLE);
                selectImage.setVisibility(View.VISIBLE);

            }
        });


        showUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openImageActivity();
            }
        });

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mUploadTask != null && mUploadTask.isInProgress()){

                    imgView.setVisibility(View.VISIBLE);
                    preview.setVisibility(View.VISIBLE);
                    imageName.setVisibility(View.INVISIBLE);

                    Toast.makeText(MainActivity.this,"Uploading is InProgress",Toast.LENGTH_SHORT).show();

                } else {

                    uploadFile();
                }

            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    auth.signOut();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            selectImage.setVisibility(View.INVISIBLE);
            imageName.setVisibility(View.VISIBLE);


           /* Picasso.with(this).load(filePath).into((Target) filePath);*/

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getfileExtension(Uri uri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile() {

        if (filePath != null) {

            StorageReference filereference = mStorageRef.child("uploads/" + System.currentTimeMillis()
                    + "." + getfileExtension(filePath));

            mUploadTask=filereference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            pd.dismiss();
                            Toast.makeText(MainActivity.this, "Upload successfully", Toast.LENGTH_SHORT).show();
                            Upload upload = new Upload(imageName.getText().toString().trim(),

                                    taskSnapshot.getDownloadUrl().toString());

                            String uploadId = mDatabaseRef.push().getKey();

                            mDatabaseRef.child(uploadId).setValue(upload);

                            imgView.setVisibility(View.GONE);
                            selectImage.setVisibility(View.INVISIBLE);
                            preview.setVisibility(View.GONE);
                            imageName.setVisibility(View.GONE);
                            showUploads.setVisibility(View.VISIBLE);

                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                           /* double progress = (100/0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getBytesTransferred());
                            mProgress.setProgress((int)progress);
                            */

                            pd.show();

                           /* Toast.makeText(MainActivity.this, "Uploading Image", Toast.LENGTH_SHORT).show();
*/
                        }
                    });


        } else {

            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();


        }

        }

        private  void openImageActivity (){

        Intent intent = new Intent(this,ImageActivity.class);

        startActivity(intent);


        }


}
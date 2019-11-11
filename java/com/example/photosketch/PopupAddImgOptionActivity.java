package com.example.photosketch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PopupAddImgOptionActivity extends Activity {

    private Button galleryButton;
    private Button cameraButton;

    private ImageInputOutputHandler imgHandler = new ImageInputOutputHandler();

    public static final int CAMERA_REQUEST = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_add_img_option);

        popupMenu();
        init();
    }

    public void init(){
        galleryButton = findViewById(R.id.gallery);
        cameraButton = findViewById(R.id.camera);

        setGalleryButton(galleryButton);
        setCameraButton(cameraButton);
    }

    // Select image from gallery
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){

                    Uri pickedImage = data.getData();
                    InputStream is = null;
                    try {
                        is = getApplicationContext().getContentResolver().openInputStream(pickedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    Bitmap bitmap = imgHandler.decodeSampledBitmapFromUri(getApplicationContext() ,pickedImage);
                   // Bitmap bitmap = BitmapFactory.decodeStream(is);

//                    Bitmap bitmap = null;
//                    try {
//                        bitmap = Picasso.get().load(pickedImage).resize(499, 499).get();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    imgHandler.saveToInternalStorage(bitmap, getApplicationContext());

                    Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                    startActivity(intent);
                }
                break;
            case CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK)
                {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    imgHandler.saveToInternalStorage(bitmap, getApplicationContext());

                    Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    public void popupMenu(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.4), (int)(height*.087));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y =  500;

        getWindow().setAttributes((params));
        getWindow().setElevation(100 * getApplicationContext().getResources().getDisplayMetrics().density);
    }


    public void setGalleryButton(Button button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                final int ACTIVITY_SELECT_IMAGE = 1;
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
            }
        });
    }

    public void setCameraButton(Button button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }


}

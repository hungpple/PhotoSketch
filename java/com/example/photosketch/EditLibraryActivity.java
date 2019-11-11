package com.example.photosketch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditLibraryActivity extends AppCompatActivity implements ImageAdapter.onClickListener{

    private ImageButton backButton;
    private ImageButton deleteButton;
    private static int numberOfImageEachRow = 3;

    private List<Image> imageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private ImageInputOutputHandler imgHandler = new ImageInputOutputHandler();
    private List<Image> selectedImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_library);

        Bundle extras = getIntent().getExtras();
        if( extras != null)
        {
            if(extras.getInt("numOfImage") > 0){
                numberOfImageEachRow = extras.getInt("numOfImage");
            } else{
                numberOfImageEachRow = 3;
            }
        }

        //RecyclerView
        adapter = new ImageAdapter(imageList, this);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfImageEachRow));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        init();
    }

    public void init(){
        backButton = findViewById(R.id.backButton);
        deleteButton = findViewById(R.id.deleteButton);
        ArrayList<Uri> imgUriList = imgHandler.imageList(EditLibraryActivity.this);

        if(imageList.size() <= 0) {
            for (Uri imgUri : imgUriList) {
                imageList.add(new Image(imgUri, false));
            }
        }

        setBackButton(backButton);
        setDeleteButton(deleteButton);
    }

    // SettingActivity up buttons
    public void setBackButton(ImageButton button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                startActivity(intent);
            }
        });
    }

    // SettingActivity up buttons
    public void setDeleteButton(ImageButton button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                for(Image img : selectedImages){
                    Uri uri = img.getImageResourceID();
                    File file = new File(uri.getPath());
                        if(file.exists()){
                            try {
                                file.getCanonicalFile().delete();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(file.exists()){
                                getApplicationContext().deleteFile(file.getName());
                            }
                        }
                }
                selectedImages.clear();
                Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                startActivity(intent);
            }

        });
    }
    @Override
    public void onClick(int position) {
        Image image = imageList.get(position);

        if(image.isSelect()){
            selectedImages.remove(image);
            image.setSelect(false);
        } else {
            selectedImages.add(imageList.get(position));
            image.setSelect(true);
        }
        adapter.addTickToSelectedImage(position);
    }
}

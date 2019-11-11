package com.example.photosketch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.devs.sketchimage.SketchImage;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;

public class LibraryActivity extends AppCompatActivity implements ImageAdapter.onClickListener {

    private static final String IMAGE_KEY = "image";
    private ImageInputOutputHandler imgHandler = new ImageInputOutputHandler();

    private ImageButton addImgButton;
    private ImageButton settingButton;
    private ImageButton editButton;

    private static int numberOfImageEachRow = 3;

    // imageList static
    private  List<Image> imageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private ActionMenuView menu;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

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
        setupToolBar();
        init();
    }


    // Turn off blurry
    @Override
    protected void onPostResume() {
        Blurry.delete((ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0));
        super.onPostResume();
    }

    private void setupToolBar() {
        Toolbar t = findViewById(R.id.toolbar);
        menu = t.findViewById(R.id.menu);
        menu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });

        setSupportActionBar(t);
        getSupportActionBar().setTitle(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // use amvMenu here
        inflater.inflate(R.menu.menu, this.menu.getMenu());
        MenuItem item = this.menu.getMenu().findItem(R.id.home);
        item.setVisible(false);
        item = this.menu.getMenu().findItem(R.id.share);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        switch (item.getItemId()) {
            case R.id.menu:
                Toast.makeText(this, "Review", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), PopupMenuActivity.class);
                startActivity(intent);
                Blurry.with(LibraryActivity.this).radius(5).sampling(2).onto(viewGroup);
                return true;
            default:
                Blurry.delete((ViewGroup) ((ViewGroup) this
                        .findViewById(android.R.id.content)).getChildAt(0));
                return super.onOptionsItemSelected(item);
        }

    }

    public void init(){
        addImgButton = findViewById(R.id.addButton);
        settingButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.backButton);
        mProgressDialog = new ProgressDialog(LibraryActivity.this);
       // loadSomeThingDemo();
        new LoadingImageAsync().execute();

        // Set menu button
        setSettingButton(settingButton);
        setAddImgButtonButton(addImgButton);
        setEditButton(editButton);
    }

    public void setSettingButton(ImageButton button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setEditButton(ImageButton button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), EditLibraryActivity.class);
                intent.putExtra("numOfImage", numberOfImageEachRow);
                startActivity(intent);
            }
        });
    }

    public void setAddImgButtonButton(ImageButton button) {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            // TODO
            Intent intent = new Intent(getApplicationContext(), PopupAddImgOptionActivity.class);
            startActivity(intent);
            Blurry.with(LibraryActivity.this).radius(5).sampling(2).onto(viewGroup);
            }
        });
    }


    @Override
    public void onClick(int position) {
        Toast.makeText(getApplicationContext(), "SELECT", Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        Uri uri = imageList.get(position).getImageResourceID();
        String stringUri;
        stringUri = uri.toString();
        bundle.putString(IMAGE_KEY, stringUri);
        Intent intent = new Intent(this, ShowImageActivity.class);
        intent.putExtra(IMAGE_KEY, stringUri);
        startActivity(intent);
    }

    // Code for progress bar
    private class LoadingImageAsync extends AsyncTask<Integer, Integer, String> {
        protected void onPreExecute() {
            mProgressDialog.setMessage("Loading Images ...");
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Integer... params)
        {
            ArrayList<Uri> imgUriList = imgHandler.imageList(getApplicationContext());
            if(imageList.size() <= 0) {
                for (Uri imgUri : imgUriList) {
                    imageList.add(new Image(imgUri, false));
                }
            }
            return "FINISH";
        }

        @Override
        protected void onPostExecute(String s) {
            mProgressDialog.dismiss();
            adapter.notifyDataSetChanged();
        }
    }


    // use for testing
    public void loadSomeThingDemo(){
        //Initialize imagelist for testing

        Uri uri1 = Uri.parse("android.resource://com.example.photosketch/drawable/img1");
        Uri uri2 = Uri.parse("android.resource://com.example.photosketch/drawable/img2");
        Uri uri3 = Uri.parse("android.resource://com.example.photosketch/drawable/img3");
        Uri uri4 = Uri.parse("android.resource://com.example.photosketch/drawable/img4");
        Uri uri5 = Uri.parse("android.resource://com.example.photosketch/drawable/img5");
        Uri uri6 = Uri.parse("android.resource://com.example.photosketch/drawable/img6");
        Uri uri7 = Uri.parse("android.resource://com.example.photosketch/drawable/img7");
        Uri uri8 = Uri.parse("android.resource://com.example.photosketch/drawable/img8");

        Bitmap bm1 = imgHandler.decodeSampledBitmapFromResource(getResources(), R.drawable.img1, 499, 499);
        Bitmap bm2 = imgHandler.decodeSampledBitmapFromResource(getResources(), R.drawable.img2, 499, 499);
        Bitmap bm3 = imgHandler.decodeSampledBitmapFromResource(getResources(), R.drawable.img3, 499, 499);
        Bitmap bm4 = imgHandler.decodeSampledBitmapFromResource(getResources(), R.drawable.img4, 499, 499);
        Bitmap bm5 = imgHandler.decodeSampledBitmapFromResource(getResources(), R.drawable.img5, 499, 499);
        Bitmap bm6 = imgHandler.decodeSampledBitmapFromResource(getResources(), R.drawable.img6, 499, 499);
        Bitmap bm7 = imgHandler.decodeSampledBitmapFromResource(getResources(), R.drawable.img7, 499, 499);
        Bitmap bm8 = imgHandler.decodeSampledBitmapFromResource(getResources(), R.drawable.img8, 499, 499);

        imgHandler.saveToInternalStorage(bm1, getApplicationContext());
        imgHandler.saveToInternalStorage(bm2, getApplicationContext());
        imgHandler.saveToInternalStorage(bm3, getApplicationContext());
        imgHandler.saveToInternalStorage(bm4, getApplicationContext());
        imgHandler.saveToInternalStorage(bm5, getApplicationContext());
        imgHandler.saveToInternalStorage(bm6, getApplicationContext());
        imgHandler.saveToInternalStorage(bm7, getApplicationContext());
        imgHandler.saveToInternalStorage(bm8, getApplicationContext());


        ArrayList<Uri> imgUriList = imgHandler.imageList(getApplicationContext());

        for(Uri imgUri : imgUriList){
            imageList.add(new Image(imgUri, false));
        }


        adapter.notifyDataSetChanged();
    }
}

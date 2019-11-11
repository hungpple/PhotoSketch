package com.example.photosketch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;


public class ShowImageActivity extends AppCompatActivity {


    public ImageView image;
    private ActionMenuView menu;
    private ActionMenuView menuRight;

    public Uri uri;

    private ViewPager mViewPager;
    public ProgressBar pb;

   // static{ System.loadLibrary("opencv_java4"); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        pb = findViewById(R.id.pb);
        setupToolBar();
        init();

        //Get the bundle
        Bundle extras = getIntent().getExtras();
        uri = Uri.parse(extras.getString("image"));
        InputStream is = null;
        try {
            is = getApplicationContext().getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        image.setImageBitmap(bitmap);
        mViewPager = findViewById(R.id.container);

        setupViewPager(mViewPager);
        setViewPager(1);
    }


    // ViewPager for apdapting fragments
    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ShowImageBeforeSketchFragment(), "Fragment1");
        adapter.addFragment(new ShowImageInSketchFragment(), "Fragment2");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
    }


    //---------------------- Tool bar-------------------------
    private void setupToolBar() {
        Toolbar t = findViewById(R.id.toolbar);
        menu = t.findViewById(R.id.menu);
        menuRight = t.findViewById(R.id.menu_right);
        menu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });
        menuRight.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
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
        MenuInflater inflater2 = getMenuInflater();
        // use amvMenu here
        inflater.inflate(R.menu.menu, this.menu.getMenu());
        inflater2.inflate(R.menu.menu, this.menuRight.getMenu());

        MenuItem item = this.menuRight.getMenu().findItem(R.id.home);
        item.setVisible(false);
        item = this.menuRight.getMenu().findItem(R.id.menu);
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
                Blurry.with(ShowImageActivity.this).radius(5).sampling(2).onto(viewGroup);
                return true;
            case R.id.home:
                Toast.makeText(this, "Go To LibraryActivity", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), LibraryActivity.class);
                startActivity(intent);
                return true;
            case R.id.share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String fullmsg = "I LOVE YOU";
                shareIntent.putExtra(Intent.EXTRA_TEXT, fullmsg);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent,"Share Using"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Turn off blurry
    @Override
    protected void onPostResume() {
        Blurry.delete((ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0));
        super.onPostResume();
    }
    //------------------- End of setting up toolbar------------

    public void init(){
        image = findViewById(R.id.image);
    }
}

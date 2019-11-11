package com.example.photosketch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.devs.sketchimage.SketchImage;
import com.squareup.picasso.Picasso;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.filter2D;
import static org.opencv.photo.Photo.edgePreservingFilter;


public class ShowImageInSketchFragment extends Fragment implements ImageAdapter.onClickListener{
    private Button nextButton;
    private ImageButton origin2GrayButton;
    private ImageButton origin2SketchButton;
    private ImageButton origin2ColorButton;
    private ImageButton origin2SoftButton;
    private ImageButton origin2SoftColorButton;
    private SketchImage sketchImage;
    private View thumbView;

    private int level;
    private static String filter = "NONE";


    private Mat mat;
    //private ProgressBar pb;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_show_image_in_sketch, container, false);

        thumbView = LayoutInflater.from(getContext()).inflate(R.layout.custom_layout_seekbar, null, false);
        ((ShowImageActivity)getActivity()).setViewPager(1);
        initializeUI(view);
        return view;
    }

    private void initializeUI(final View view ) {
        nextButton = view.findViewById(R.id.nextButton);

        origin2GrayButton = view.findViewById(R.id.origin2Gray);
        origin2SketchButton = view.findViewById(R.id.origin2Sketch);
        origin2ColorButton = view.findViewById(R.id.origin2SketchColor);
        origin2SoftButton = view.findViewById(R.id.origin2Soft);
        origin2SoftColorButton =view.findViewById(R.id.origin2SoftColor);

        Picasso.get().load(R.drawable.otogray).fit().centerInside().into(origin2GrayButton);
        Picasso.get().load(R.drawable.otosketch).fit().centerInside().into(origin2SketchButton);
        Picasso.get().load(R.drawable.otocolor).fit().centerInside().into(origin2ColorButton);
        Picasso.get().load(R.drawable.otosoft).fit().centerInside().into(origin2SoftButton);
        Picasso.get().load(R.drawable.otocolorsoft).fit().centerInside().into(origin2SoftColorButton);

        toGray(origin2GrayButton);
        toSketch(origin2SketchButton);
        toColorSketch(origin2ColorButton);
        toSoftSketch(origin2SoftButton);
        toColorSoftSketch(origin2SoftColorButton);

        final SeekBar seekBar = view.findViewById(R.id.seekBar);
        seekBar.setProgress(100);
        seekBar.setThumb(getThumb(100));
        level = seekBar.getProgress();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // You can have your own calculation for progress
                seekBar.setThumb(getThumb(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                level = seekBar.getProgress();
                new PreviewImageAsync().execute();
            }
        });

        setNextButton(nextButton);
    }


    public Drawable getThumb(int progress) {
        ((TextView) thumbView.findViewById(R.id.tvProgress)).setText(progress + "");

        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        thumbView.layout(0, 0, thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight());
        thumbView.draw(canvas);

        return new BitmapDrawable(getResources(), bitmap);
    }

    public void setNextButton(Button button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String stringUri;
                stringUri = ((ShowImageActivity)getActivity()).uri.toString();

                Intent intent = new Intent(getActivity(), ShowImageAfterSketchActivity.class);
                intent.putExtra("image", stringUri);
                intent.putExtra("filter", filter);
                intent.putExtra("level", level);
                startActivity(intent);
            }
        });
    }

    public void toGray(ImageButton button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                new PreviewImageAsync().execute();
                filter = "ORIGINAL_TO_GRAY";
            }
        });
    }

    public void toSketch(ImageButton button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                new PreviewImageAsync().execute();
                filter = "ORIGINAL_TO_SKETCH";
            }
        });
    }

    public void toColorSketch(ImageButton button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                new PreviewImageAsync().execute();
                filter = "ORIGINAL_TO_COLORED_SKETCH";
            }
        });
    }

    public void toSoftSketch(ImageButton button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                new PreviewImageAsync().execute();
                filter = "ORIGINAL_TO_SOFT_SKETCH";
            }
        });
    }

    public void toColorSoftSketch(ImageButton button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                new PreviewImageAsync().execute();
                filter = "ORIGINAL_TO_SOFT_COLOR_SKETCH";
            }
        });
    }

    @Override
    public void onClick(int position) {

    }

    // Code for progress bar
    private class PreviewImageAsync extends AsyncTask<Integer, Integer, Bitmap> {
        protected void onPreExecute() {
            ((ShowImageActivity)getActivity()).pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Integer... params)
        {
            Uri imageURI = ((ShowImageActivity)getActivity()).uri;
            InputStream is = null;
            try {
                is = getContext().getContentResolver().openInputStream(imageURI);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(is);
            sketchImage = new SketchImage.Builder(getContext(), bitmap).build();
            Bitmap img ;
            // Sketch image
            if(filter.equals("ORIGINAL_TO_GRAY")){
                img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_GRAY, level);
            }else if(filter.equals("ORIGINAL_TO_SKETCH")){
                img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_SKETCH, level);
                sketchImage = new SketchImage.Builder(getContext(), img).build();
                if(level > 60){
                    img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_GRAY, 100);
                }else {
                    img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_GRAY, level);
                }
                if(level >0 ){
                    img = applyPencilTexture(img);
                }
            }else if(filter.equals("ORIGINAL_TO_COLORED_SKETCH")){
                img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_COLORED_SKETCH, level);
            }else if(filter.equals("ORIGINAL_TO_SOFT_SKETCH")){
                img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_SOFT_SKETCH, level);
                sketchImage = new SketchImage.Builder(getContext(), img).build();
                if(level > 60){
                    img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_GRAY, 100);
                }else {
                    img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_GRAY, level);
                }
            }else if(filter.equals("ORIGINAL_TO_SOFT_COLOR_SKETCH")){
                img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_SOFT_COLOR_SKETCH, level);
            } else {
                img = bitmap;
            }
            return img;
        }

        @Override
        protected void onPostExecute(Bitmap img) {
            ((ShowImageActivity)getActivity()).pb.setVisibility(View.INVISIBLE);
            ImageView imageView = ((ShowImageActivity)getActivity()).image;
            imageView.setImageBitmap(img);
        }
    }

    public Bitmap applyPencilTexture(Bitmap image)
    {
        try
        {
            Bitmap pencilTexture = Picasso.get().load(R.drawable.texture2).resize(image.getWidth(), image.getHeight()).get();
            pencilTexture = makeTransparent(pencilTexture, 85);
            Bitmap bmOverlay = Bitmap.createBitmap(image.getWidth(), image.getHeight(),  image.getConfig());
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(image, 0, 0, null);
            canvas.drawBitmap(pencilTexture, 0, 0, null);
            return bmOverlay;
        } catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap makeTransparent(Bitmap src, int value) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap transBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(transBitmap);
        canvas.drawARGB(0, 0, 0, 0);
        // config paint
        final Paint paint = new Paint();
        paint.setAlpha(value);
        canvas.drawBitmap(src, 0, 0, paint);
        return transBitmap;
    }
}

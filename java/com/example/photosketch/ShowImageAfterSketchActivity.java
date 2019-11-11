package com.example.photosketch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.devs.sketchimage.SketchImage;
import com.squareup.picasso.Picasso;


import org.opencv.android.BaseLoaderCallback;
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
import java.nio.IntBuffer;

import jp.wasabeef.blurry.Blurry;

public class ShowImageAfterSketchActivity extends AppCompatActivity {
    private Button saveButton;
    private Button shareButton;
    private ImageView image;
    private ActionMenuView menu;
    private ActionMenuView menuRight;
    private SketchImage sketchImage;
    private Uri uri;
    private String filter;
    private int level;
    private ImageInputOutputHandler imageHandler = new ImageInputOutputHandler();
    private Bitmap img;
    private boolean save = true;

    private ProgressDialog mProgressDialog;
    private ImageInputOutputHandler imgHandler;


    private Mat mat;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            OpenCVLoader.initDebug();
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    mat=new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_img_after_sketch);

        setupToolBar();
        init();
        mProgressDialog = new ProgressDialog(ShowImageAfterSketchActivity.this);
        new SketchImageAsync().execute();
        ////com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F22/ORIGINAL/NONE/598135787

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
                Blurry.with(ShowImageAfterSketchActivity.this).radius(5).sampling(2).onto(viewGroup);
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
    //------------------- End of setting up toolbar------------


    // Turn off blurry
    @Override
    protected void onPostResume() {
        Blurry.delete((ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0));

        super.onPostResume();
    }


    public void init(){
        saveButton = findViewById(R.id.saveButton);
        shareButton = findViewById(R.id.shareButton);
        image = findViewById(R.id.image);

        setSaveButton(saveButton);
        setShareButton(shareButton);
    }

    public void setSaveButton(Button button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                mProgressDialog = new ProgressDialog(ShowImageAfterSketchActivity.this);
                new SavingImageAsync().execute();

            }
        });
    }

    public void setShareButton(Button button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String fullmsg = "I LOVE YOU";
                shareIntent.putExtra(Intent.EXTRA_TEXT, fullmsg);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent,"Share Using"));
            }
        });
    }

    // Code for progress bar
    private class SketchImageAsync extends AsyncTask<Integer, Integer, Bitmap> {
        protected void onPreExecute() {
            OpenCVLoader.initDebug();
            mat=new Mat();
            mProgressDialog.setMessage("Sketching Image ...");
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(Integer... params)
        {
            Bundle extras = getIntent().getExtras();


            uri = Uri.parse(extras.getString("image"));
            filter = extras.getString("filter");
            level = extras.getInt("level");

            InputStream is = null;
            try {
                is = getApplicationContext().getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(is);

            sketchImage = new SketchImage.Builder(getApplicationContext(), bitmap).build();

            if(filter.equals("ORIGINAL_TO_GRAY")){
                img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_GRAY, level);
            }else if(filter.equals("ORIGINAL_TO_SKETCH")){
                img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_SKETCH, level);
                sketchImage = new SketchImage.Builder(getApplicationContext(), img).build();
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
                sketchImage = new SketchImage.Builder(getApplicationContext(), img).build();
                if(level > 60){
                    img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_GRAY, 100);
                }else {
                    img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_GRAY, level);
                }
            }else if(filter.equals("ORIGINAL_TO_SOFT_COLOR_SKETCH")){
                img = sketchImage.getImageAs(SketchImage.ORIGINAL_TO_SOFT_COLOR_SKETCH, level);
            } else {
                img = bitmap;
                save = false;
            }


//
//            Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//            Utils.bitmapToMat(bmp32, mat);
//
//            doSobel(mat);
//
//            Bitmap bmtest = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(mat, bmtest);

          // Try adding new filter layer


         //Bitmap filterTest = checkMatrix(bitmap);
        //    Bitmap filterTest = grayscale(bitmap);
           // filterTest = toInverted(filterTest);
           // filterTest = invertImage(filterTest);

        //  Bitmap  tryimg = colorDodgeBlend(bitmap, filterTest);


        //  Bitmap test = checkMatrix(bitmap);

        /*  Bitmap imgg = toGrayscale(bitmap);




          Bitmap imggg = toInverted(imgg);
          Bitmap imgggg = toBlur(imggg);
          img = blend(imgggg, imgg);
          img = applyPencilTexture(img);*/
          //return img = filterTest;
            return img;
        }

        @Override
        protected void onPostExecute(Bitmap img) {
            mProgressDialog.dismiss();
            image.setImageBitmap(img);
        }
    }


    private class SavingImageAsync extends AsyncTask<Integer, Integer, Intent> {
        protected void onPreExecute() {
            mProgressDialog.setMessage("Saving Image ...");
            mProgressDialog.show();
        }

        @Override
        protected Intent doInBackground(Integer... params)
        {
            Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
            if(save){
                imageHandler.saveToInternalStorage(img, getApplicationContext());
            }
            startActivity(intent);
            return intent;
        }

        @Override
        protected void onPostExecute(Intent intent) {}
    }



    // This part is for report

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();

        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public Bitmap checkMatrix(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();

        //grayscaling matrix
//        ColorMatrix cm = new ColorMatrix(new float[]{
//                0.3f, 0.59f, 0.11f, 0, 0,
//                0.3f, 0.59f, 0.11f, 0, 0,
//                0.3f, 0.59f, 0.11f, 0, 0,
//                0,    0,    0, 1, 0});
// Luminosity grayscale
        ColorMatrix cm = new ColorMatrix(new float[]{
                0.21f, 0.72f, 0.07f, 0, 0,
                0.21f, 0.72f, 0.07f, 0, 0,
                0.21f, 0.72f, 0.07f, 0, 0,
                0,    0,    0, 1, 0});



//        ColorMatrix cm = new ColorMatrix(new float[]{
//                1, 2, 1, 0, 255,
//                2, 4, 2, 0, 255,
//                1, 2, 1, 0, 255,
//                0,    0,    0, 1, 0});

//                ColorMatrix cm = new ColorMatrix(new float[]{
//                1, 0, 0, 0, 0,
//                0, 1, 0, 0, 0,
//                0, 0, 1, 0, 0,
//                0, 0, 0, 1, 0});
        ColorFilter colorFilter = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(colorFilter);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bitmap;
    }


    public Bitmap grayscale(Bitmap source) {
        Bitmap base = source.copy(Bitmap.Config.ARGB_8888, true);
        // Int Buffer for Bottom Layer
        IntBuffer imgBuffer = IntBuffer.allocate(base.getWidth() * base.getHeight());
        base.copyPixelsToBuffer(imgBuffer);
        imgBuffer.rewind();
        // Int Buffer for output
        IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
        buffOut.rewind();

        while (buffOut.position() < buffOut.limit()) {

            int srcimg = imgBuffer.get();
            int redValueFilter = Color.red(srcimg);
            int greenValueFilter = Color.green(srcimg);
            int blueValueFilter = Color.blue(srcimg);

            int grayValue =(int)(redValueFilter * 0.3 + greenValueFilter * 0.59 + blueValueFilter * 0.11);
            // int grayValue =(int)Math.max(redValueFilter, Math.max(greenValueFilter ,blueValueFilter));
//            int greenValueFinal = (int)(redValueFilter * 0.3 + greenValueFilter * 0.59 + blueValueFilter * 0.11);
//            int blueValueFinal = (int)(redValueFilter * 0.3 + greenValueFilter * 0.59 + blueValueFilter * 0.11);

            int pixel = Color.argb(255, grayValue, grayValue, grayValue);
            buffOut.put(pixel);
        }
        buffOut.rewind();
        base.copyPixelsFromBuffer(buffOut);
        return base;
    }

    public Bitmap invertImage(Bitmap source) {
        Bitmap base = source.copy(Bitmap.Config.ARGB_8888, true);
        // Int Buffer for Bottom Layer
        IntBuffer imgBuffer = IntBuffer.allocate(base.getWidth() * base.getHeight());
        base.copyPixelsToBuffer(imgBuffer);
        imgBuffer.rewind();
        // Int Buffer for output
        IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
        buffOut.rewind();

        while (buffOut.position() < buffOut.limit()) {

            int srcimg = imgBuffer.get();
            int redValueFilter = Color.red(srcimg);
            int greenValueFilter = Color.green(srcimg);
            int blueValueFilter = Color.blue(srcimg);

            int invertedRed = 255 - redValueFilter;
            int invertedGreen = 255 - greenValueFilter;
            int invertedBlue = 255 - blueValueFilter;


            // int grayValue =(int)Math.max(redValueFilter, Math.max(greenValueFilter ,blueValueFilter));
//            int greenValueFinal = (int)(redValueFilter * 0.3 + greenValueFilter * 0.59 + blueValueFilter * 0.11);
//            int blueValueFinal = (int)(redValueFilter * 0.3 + greenValueFilter * 0.59 + blueValueFilter * 0.11);

            int pixel = Color.argb(255, invertedRed, invertedGreen, invertedBlue);
            buffOut.put(pixel);
        }
        buffOut.rewind();
        base.copyPixelsFromBuffer(buffOut);
        return base;
    }


    public Bitmap toInverted(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();

        ColorMatrix cm = new ColorMatrix(new float[]{
                -1, 0, 0, 0, 255,
                0, -1, 0, 0, 255,
                0, 0, -1, 0, 255,
                0, 0, 0, 1, 0});
        ColorFilter colorFilter = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(colorFilter);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bitmap;
    }

    public Bitmap toBlur(Bitmap input) {
        try {
            RenderScript rsScript = RenderScript.create(getApplicationContext());
            Allocation alloc = Allocation.createFromBitmap(rsScript, input);

            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript, Element.U8_4(rsScript));
            blur.setRadius(25);
            blur.setInput(alloc);

            Bitmap result = Bitmap.createBitmap(input.getWidth(),
                    input.getHeight(), Bitmap.Config.ARGB_8888);
            Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);

            blur.forEach(outAlloc);
            outAlloc.copyTo(result);

            rsScript.destroy();
            return result;
        } catch (Exception e) {
            // TODO: handle exception
            return input;
        }
    }


    public Bitmap colorDodgeBlend(Bitmap source, Bitmap layer) {
        Bitmap base = source.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap blend = layer.copy(Bitmap.Config.ARGB_8888, false);
        // Int Buffer for Bottom Layer
        IntBuffer buffBase = IntBuffer.allocate(base.getWidth() * base.getHeight());
        base.copyPixelsToBuffer(buffBase);
        buffBase.rewind();
        // Int Buffer for Top Layer
        IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth() * blend.getHeight());
        blend.copyPixelsToBuffer(buffBlend);
        buffBlend.rewind();
        // Int Buffer for output
        IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
        buffOut.rewind();

        while (buffOut.position() < buffOut.limit()) {
            int filterInt = buffBlend.get();
            int srcInt = buffBase.get();
            int redValueFilter = Color.red(filterInt);
            int greenValueFilter = Color.green(filterInt);
            int blueValueFilter = Color.blue(filterInt);
            int redValueSrc = Color.red(srcInt);
            int greenValueSrc = Color.green(srcInt);
            int blueValueSrc = Color.blue(srcInt);
            int redValueFinal = colordodge(redValueFilter, redValueSrc);
            int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
            int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);

            int pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal);
            buffOut.put(pixel);
        }
        buffOut.rewind();
        base.copyPixelsFromBuffer(buffOut);
        blend.recycle();
        return base;
    }

    private int colordodge(int in1, int in2) {
        float image = (float) in2;  // bottom layer
        float mask = (float) in1;   // top layer
        if(image == 255){
            return (int) image;
        } else {
            return (int) Math.min(255, (((long) mask << 8) / (255 - image)));
        }
    }


    public Bitmap blend(Bitmap source, Bitmap layer) {
        Bitmap base = source.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap blend = layer.copy(Bitmap.Config.ARGB_8888, false);
        // Int Buffer for Bottom Layer
        IntBuffer buffBase = IntBuffer.allocate(base.getWidth() * base.getHeight());
        base.copyPixelsToBuffer(buffBase);
        buffBase.rewind();
        // Int Buffer for Top Layer
        IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth() * blend.getHeight());
        blend.copyPixelsToBuffer(buffBlend);
        buffBlend.rewind();
        // Int Buffer for output
        IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
        buffOut.rewind();

        while (buffOut.position() < buffOut.limit()) {
            int filterInt = buffBlend.get();
            int srcInt = buffBase.get();
            int redValueFilter = Color.red(filterInt);
            int greenValueFilter = Color.green(filterInt);
            int blueValueFilter = Color.blue(filterInt);
            int redValueSrc = Color.red(srcInt);
            int greenValueSrc = Color.green(srcInt);
            int blueValueSrc = Color.blue(srcInt);
            int redValueFinal = b(redValueFilter, redValueSrc);
            int greenValueFinal = b(greenValueFilter, greenValueSrc);
            int blueValueFinal = b(blueValueFilter, blueValueSrc);

            int pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal);
            buffOut.put(pixel);
        }
        buffOut.rewind();
        base.copyPixelsFromBuffer(buffOut);
        blend.recycle();
        return base;
    }

    private int b(int in1, int in2) {
        float image = (float) in2;  // bottom layer
        float mask = (float) in1;   // top layer
        if(image == 255){
            return (int) image;
        } else {
            return (int) Math.min(255, mask / (1-image)   );
        }
    }


    // Add pencil texture to the image
    public Bitmap applyPencilTexture(Bitmap image)
    {
        try
        {
            Bitmap pencilTexture = Picasso.get().load(R.drawable.texture2).resize(image.getWidth(), image.getHeight()).get();
            pencilTexture = makeTransparent(pencilTexture, 65);
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


    public Bitmap sobelOperator(Bitmap src, int value) {
        float[][] kernelx = {{-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}};


        float[][] kernely = {{-1, -2, -1},
                {0,  0,  0},
                {1,  2,  1}};
        Bitmap b = src;
        int width = b.getWidth();
        int height = b.getHeight();
        int stride = b.getRowBytes();
        for(int x=0;x<b.getWidth();x++) {
            for (int y = 0; y < b.getHeight(); y++) {
                int pixel = b.getPixel(x, y);
                // you have the source pixel, now transform it and write to destination
            }
        }

        return null;
    }


    private Mat doSobel(Mat frame) {
        // init
        Mat grayImage = new Mat();
        Mat detectedEdges = new Mat();
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;
        Mat grad_x = new Mat();
        Mat grad_y = new Mat();
        Mat abs_grad_x = new Mat();
        Mat abs_grad_y = new Mat();

        // reduce noise with a 3x3 kernel
        Imgproc.GaussianBlur(frame, frame, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);

        // convert to grayscale
        Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Gradient X
        // Imgproc.Sobel(grayImage, grad_x, ddepth, 1, 0, 3, scale,
        // this.threshold.getValue(), Core.BORDER_DEFAULT );
        Imgproc.Sobel(grayImage, grad_x, ddepth, 1, 0);
        Core.convertScaleAbs(grad_x, abs_grad_x);

        // Gradient Y
        // Imgproc.Sobel(grayImage, grad_y, ddepth, 0, 1, 3, scale,
        // this.threshold.getValue(), Core.BORDER_DEFAULT );
        Imgproc.Sobel(grayImage, grad_y, ddepth, 0, 1);
        Core.convertScaleAbs(grad_y, abs_grad_y);

        // Total Gradient (approximate)
        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, detectedEdges);
        // Core.addWeighted(grad_x, 0.5, grad_y, 0.5, 0, detectedEdges);

        return detectedEdges;

    }
}

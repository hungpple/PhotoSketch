package com.example.photosketch;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.*;



public class ImageInputOutputHandler {
    private static final String FILE_NAME = "images.txt";


    // Save images to internal storage
    public void saveToInternalStorage(Bitmap bitmapImage, Context context){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/user/0/com.example.photosketch/app_images/
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        // Create image file
        String name = generateFileName();
        if(fileExists(directory, name) == true){
            name = generateFileName();
            Toast.makeText(context, "FILE IS EXIST", Toast.LENGTH_SHORT).show();
        }
        writeToTextFile(name, context);
        File mypath = new File(directory,name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //return directory.getAbsolutePath();
    }

    private static boolean fileExists(File path, String filename){
        return new File(path, filename).exists();
    }

    //generate filename
    private String generateFileName() {
        String fileType = "png";
        String fileName = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(8), fileType);
        return fileName;
    }

    // Load image into Bitmap
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    // convert image from drawable to bitmap
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public Bitmap decodeSampledBitmapFromUri(Context context, Uri uri) {
        InputStream is = null;
        try {
            is = new BufferedInputStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            is.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        bitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.3), (int)(bitmap.getHeight()*0.3), true);

        return bitmap;
    }

    private void writeToTextFile(String fileName, Context context){
        String text = fileName + "\n";
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FILE_NAME, context. MODE_APPEND));
            outputStreamWriter.write(text);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public ArrayList<Uri> imageList(Context context){
        // Pull out image from folder
        ArrayList<Uri> uriList = new ArrayList<>();
        String diirectory = "/data/user/0/com.example.photosketch/app_images/";
        ArrayList<String> imgNames = readFromTextFile(context);
        for(String name : imgNames){
            File dir = new File(diirectory);
            // Add Uri if exist
            if(fileExists(dir, name)){
                String path = diirectory + name;
                File f = new File(path);  //
                Uri imageUri = Uri.fromFile(f);
                uriList.add(imageUri);
            } else { // Delete image file name thats not existed

                    removeTextfromFile("/data/data/com.example.photosketch/files/images.txt",name);

            }
        }
        return uriList;
    }

    public void removeTextfromFile(String file, String lineToRemove) {
        try {
            File inFile = new File(file);

            if (!inFile.isFile()) {
                System.out.println("Parameter is not an existing file");
                return;
            }

            //Construct the new file that will later be renamed to the original filename.
            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(file));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            //Read from the original file and write to the new
            //unless content matches data to be removed.
            while ((line = br.readLine()) != null) {

                if (!line.trim().equals(lineToRemove)) {

                    pw.println(line);
                    pw.flush();
                }
            }
            pw.close();
            br.close();

            //Delete the original file
            if (!inFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            //Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inFile))
                System.out.println("Could not rename file");

        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<String> readFromTextFile(Context context){
        ArrayList<String> imgNames = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(FILE_NAME)));
            String line;

            while ((line = reader.readLine()) != null) {
                imgNames.add(line);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return imgNames;
    }
}

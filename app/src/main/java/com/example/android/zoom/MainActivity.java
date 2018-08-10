package com.example.android.zoom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.ColorMatrix;
import android.graphics.Color;
import android.widget.Toast;
import android.graphics.Canvas;
import android.graphics.Matrix;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private Button btn1;
    private ImageView imageview;
    // private static final String IMAGE_DIRECTORY = "/zoom";
    private int GALLERY = 1, CAMERA = 2;
    private ActivityInference activityInference;
    private Bitmap imagen;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();

        btn = (Button) findViewById(R.id.btn);
        btn1 = (Button) findViewById(R.id.btn1);
        imageview = (ImageView) findViewById(R.id.iv);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applySuperResolution();
            }
        });

        activityInference = new ActivityInference(getApplicationContext());
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Selecciona acción");
        String[] pictureDialogItems = {
                "Coger foto de la galería",
                "Sacar foto [BUGGEADO]"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    Toast.makeText(MainActivity.this, "¡Imagen cargada!", Toast.LENGTH_SHORT).show();
                    imageview.setImageBitmap(bitmap);
                    btn1.setVisibility(View.VISIBLE);
                    imagen = bitmap;

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            // TODO: No funciona cuando es con la cámara
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(thumbnail);
            Toast.makeText(MainActivity.this, "¡Imagen cargada!", Toast.LENGTH_SHORT).show();
        }
    }

    public void applySuperResolution() {
//        // http://aqibsaeed.github.io/2017-05-02-deploying-tensorflow-model-andorid-device-human-activity-recognition/
//        int[] imagenOriginal = new int[500*500*3];
//        int[] resizedArray = new int [1000*1000*3];
//
//        // Convierto el Bitmap con la imagen en un array de int
//        imagen.getPixels(imagenOriginal, 0, 500, 0, 0, 500, 500);
//        //Hago el escalado
//        Bitmap resized = Bitmap.createScaledBitmap(imagen, 1000, 1000, true);
//        resized.getPixels(resizedArray, 0, 1000, 0, 0, 1000, 1000);
//
//        int[] result1 = new int[3];
//        int [] y = new int [500 * 500];
//        int [] u = new int [1000*1000];
//        int [] v = new int [1000*1000];
//        // De la imagen original me quedo con el canal y
//        for (int i = 0; i < imagenOriginal.length; i++){
//            result1 = convertRGB2YUV(imagenOriginal[i]);
//            y[i] = result1[0];
//        }
//        // De la imagen escalada me quedo con los canales u y v
//        for (int i = 0; i < resizedArray.length; i++){
//            result1 = convertRGB2YUV(resizedArray[i]);
//            u[i] = result1[1];
//            v[i] = result1[2];
//        }
//        // Aplico el modelo al canal y
//        int[] results = activityInference.getActivityProb(y);
//
//        // Junto los canales
//        int[] result3 = new int [1000*1000*3];
//        for (int i = 0; i < 1000; i++){
//            result3[i] = results[i];
//            result3[i+1] = u[i];
//            result3[i+2] = v[i];
//
//        }
//
//        // Lo convierto a bitmap y lo guardo
//        Bitmap bitmap_result = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
//        bitmap_result.setPixels(result3, 0, 1000, 0, 0, 1000, 1000);
//        saveImage(bitmap_result);

        // BLANCO Y NEGRO

        int[] imagenOriginal = new int[500 * 500];
        imagen.getPixels(imagenOriginal, 0, 500, 0, 0, 500, 500);
        Bitmap resized = Bitmap.createScaledBitmap(imagen, 1000, 1000, true);

        long tInicio = System.currentTimeMillis();

        int[] results = activityInference.getActivityProb(imagenOriginal);

        long tFinal = System.currentTimeMillis();
        long tDiferencia = tFinal - tInicio;
        Log.i("Runtime", "Tiempo de ejecucion: " + tDiferencia + "ms");

        Bitmap bitmap_result = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
        bitmap_result.setPixels(results, 0, 1000, 0, 0, 1000, 1000);
        Bitmap scaled_image = overlay(bitmap_result, resized);
        saveImage(scaled_image);

    }

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();

        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
            //     Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            applySuperResolution();
        }
    }

}
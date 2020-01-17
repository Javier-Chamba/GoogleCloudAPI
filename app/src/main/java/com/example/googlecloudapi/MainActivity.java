package com.example.googlecloudapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{


    private ImageView mPhotoImageView;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public Vision vision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        mPhotoImageView = findViewById(R.id.imageView2);


        Vision.Builder visionBuilder = new Vision.Builder(new NetHttpTransport(),
                new AndroidJsonFactory(),  null);
        visionBuilder.setVisionRequestInitializer(new VisionRequestInitializer("AIzaSyCV_ADpVQp5_K1CQ98gc6KeOVq5p1sjqKQ"));
        vision = visionBuilder.build();

        checkCameraPermission();

    }


    public void btnTexto(View view){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ImageView imagen=(ImageView)findViewById(R.id.imageView2);
                BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                bitmap = scaleBitmapDown(bitmap, 1200);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] imageInByte = stream.toByteArray();

                //1.Paso
                Image inputImage = new Image();
                inputImage.encodeContent(imageInByte);

                //2.Feature
                Feature desiredFeature = new Feature();
                desiredFeature.setType("TEXT_DETECTION");

                //3.Arma la solicitud(es)
                AnnotateImageRequest request = new AnnotateImageRequest();
                request.setImage(inputImage);
                request.setFeatures(Arrays.asList(desiredFeature));
                BatchAnnotateImagesRequest batchRequest =  new BatchAnnotateImagesRequest();
                batchRequest.setRequests(Arrays.asList(request));


                //4. Asignamos al control VisionBuilder la solicitud
                try {
                    Vision.Images.Annotate annotateRequest  = vision.images().annotate(batchRequest);

                    //5. Enviamos la solicitud y obtenemos la respuesta
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse batchResponse  = annotateRequest.execute();


                    //6. Obtener la respuesta
                    TextAnnotation text = batchResponse.getResponses().get(0).getFullTextAnnotation();
                    String message="";
                    if(text!=null){
                        message=text.getText();
                    }else{
                        message="No hay texto";
                    }
                    final String result = message;

                    //Asignar la respuesta a la UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView imageDetail = (TextView)findViewById(R.id.textView2);
                            imageDetail.setText(result);
                        }
                    });

                }catch(IOException e) {
                    e.getStackTrace();
                }





            }
        });

    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;
        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }


    public void btnRostro(View view){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ImageView imagen=(ImageView)findViewById(R.id.imageView2);
                BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                bitmap = scaleBitmapDown(bitmap, 1200);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] imageInByte = stream.toByteArray();

                //1.Paso
                Image inputImage = new Image();
                inputImage.encodeContent(imageInByte);

                //2.Feature
                Feature desiredFeature = new Feature();
                desiredFeature.setType("FACE_DETECTION");

                //3.Arma la solicitud(es)
                AnnotateImageRequest request = new AnnotateImageRequest();
                request.setImage(inputImage);
                request.setFeatures(Arrays.asList(desiredFeature));
                BatchAnnotateImagesRequest batchRequest =  new BatchAnnotateImagesRequest();
                batchRequest.setRequests(Arrays.asList(request));


                //4. Asignamos al control VisionBuilder la solicitud
                try {
                    Vision.Images.Annotate annotateRequest  = vision.images().annotate(batchRequest);

                    //5. Enviamos la solicitud y obtenemos la respuesta
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse batchResponse  = annotateRequest.execute();


                    //6. Obtener la respuesta

                    List<FaceAnnotation> faces = batchResponse.getResponses().get(0).getFaceAnnotations();
                    String likelihoods = "";
                    String message="";
                    if(faces!=null) {
                    int numberOfFaces = faces.size();

                        for (int i = 0; i < numberOfFaces; i++) {
                            likelihoods += "\nEs " + faces.get(i).getJoyLikelihood() + " esa cara " + i + " es feliz";
                        }
                        message = "Esta foto tiene " + numberOfFaces + " cara(s)" + likelihoods;
                    }else{
                        message="No hay cara(s)";
                    }

                    final String result = message.toString();

                    //Asignar la respuesta a la UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView imageDetail = (TextView)findViewById(R.id.textView);
                            imageDetail.setText(result);
                        }
                    });

                }catch(IOException e) {
                    e.getStackTrace();
                }





            }
        });

    }


    public void btnObjeto(View view){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ImageView imagen=(ImageView)findViewById(R.id.imageView2);
                BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                bitmap = scaleBitmapDown(bitmap, 1200);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] imageInByte = stream.toByteArray();

                //1.Paso
                Image inputImage = new Image();
                inputImage.encodeContent(imageInByte);

                //2.Feature
                Feature desiredFeature = new Feature();
                desiredFeature.setType("LABEL_DETECTION");

                //3.Arma la solicitud(es)
                AnnotateImageRequest request = new AnnotateImageRequest();
                request.setImage(inputImage);
                request.setFeatures(Arrays.asList(desiredFeature));
                BatchAnnotateImagesRequest batchRequest =  new BatchAnnotateImagesRequest();
                batchRequest.setRequests(Arrays.asList(request));


                //4. Asignamos al control VisionBuilder la solicitud
                try {
                    Vision.Images.Annotate annotateRequest  = vision.images().annotate(batchRequest);

                    //5. Enviamos la solicitud y obtenemos la respuesta
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse batchResponse  = annotateRequest.execute();


                    //6. Obtener la respuesta
                    StringBuilder message = new StringBuilder("Encontré estas cosas:\n");
                    List<EntityAnnotation> labels = batchResponse.getResponses().get(0).getLabelAnnotations();
                    if (labels != null) {
                        for (EntityAnnotation label : labels) {
                            message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
                            message.append("\n");
                        }
                    } else {
                        message.append("No hay objeto(s)");
                    }
                    final String result = message.toString();

                    //Asignar la respuesta a la UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView imageDetail = (TextView)findViewById(R.id.textView3);
                            imageDetail.setText(result);
                        }
                    });

                }catch(IOException e) {
                    e.getStackTrace();
                }





            }
        });

    }


    public void btnCamera(View v){
        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras(); // Aquí es null
                Bitmap bitmap = (Bitmap) extras.get("data");
                mPhotoImageView.setImageBitmap(bitmap);
            }


    }

    private void checkCameraPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para la camara!.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 225);
        } else {
            Log.i("Mensaje", "Tienes permiso para usar la camara.");
        }
    }


}

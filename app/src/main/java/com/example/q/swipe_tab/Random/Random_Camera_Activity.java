package com.example.q.swipe_tab.Random;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.swipe_tab.AddEvent.AddActivity;
import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;


import com.example.q.swipe_tab.R;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class Random_Camera_Activity extends AppCompatActivity implements View.OnClickListener {
    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;

    private final String apiEndpoint = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0";
    private final String subscriptionKey = "753900f20b72468da5188a95f81b64ac";
    private final FaceServiceClient faceServiceClient = new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    Toolbar mytoolbar;
    ImageView img;
    Button recog_btn;

    String filepath;
    Uri imageuri;

    Bitmap target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_camera);

        Intent from = getIntent();
        filepath = from.getStringExtra("filepath");
        imageuri = Uri.parse(from.getStringExtra("imageuri"));

        detectionProgressDialog = new ProgressDialog(this);

        img = findViewById(R.id.img_view);
        recog_btn = findViewById(R.id.recog_btn);
        File imgfile = new File(filepath);
        mytoolbar = findViewById(R.id.add_toolbar);
        setSupportActionBar(mytoolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mytoolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        if(imgfile.exists()){
            Bitmap bm = BitmapFactory.decodeFile(imgfile.getAbsolutePath());
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(filepath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            target = rotateBitmap(bm, orientation);
            img.setImageBitmap(target);
            recog_btn.setOnClickListener(this);
        }else{
            Toast.makeText(getApplicationContext(), "사진 촬영에서 오류가 발생했습니다. 재시도해주시길 바랍니다", Toast.LENGTH_SHORT).show();
        }
    }

    // Detect faces by uploading a face image.
    // Frame faces after detection.
    private void detectAndFrame(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    String exceptionMessage = "";

                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    null          // returnFaceAttributes:
                                /* new FaceServiceClient.FaceAttributeType[] {
                                    FaceServiceClient.FaceAttributeType.Age,
                                    FaceServiceClient.FaceAttributeType.Gender }
                                */
                            );
                            if (result == null){
                                publishProgress(
                                        "Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress(String.format(
                                    "Detection Finished. %d face(s) detected",
                                    result.length));
                            return result;
                        } catch (Exception e) {
                            exceptionMessage = String.format(
                                    "Detection failed: %s", e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        detectionProgressDialog.show();
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {
                        detectionProgressDialog.setMessage(progress[0]);
                    }
                    @Override
                    protected void onPostExecute(Face[] result) {
                        //detectionProgressDialog.dismiss();
                        detectionProgressDialog.hide();
                        if (result == null || result.length == 0){
                            Toast.makeText(getApplicationContext(), "아무도 사람얼굴로\n인식되지 않았습니다", Toast.LENGTH_SHORT).show();
                            recog_btn.setText("결과 발표!");
                            recog_btn.setClickable(false);
                            return;
                        }
                        if(!exceptionMessage.equals("")){
                            showError(exceptionMessage);
                        }
                        ImageView imageView = findViewById(R.id.img_view);
                        imageView.setImageBitmap(
                                drawFaceRectanglesOnBitmap(imageBitmap, result));
                        imageBitmap.recycle();
                        recog_btn.setText("결과 발표!");
                        recog_btn.setClickable(false);
                    }
                };

        detectTask.execute(inputStream);
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("다시 인식버튼을 눌러주시길 바랍니다")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }})
                .create().show();
    }

    private static Bitmap drawFaceRectanglesOnBitmap(Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(10);


        Paint specialpaint = new Paint();
        specialpaint.setAntiAlias(true);
        specialpaint.setStyle(Paint.Style.STROKE);
        specialpaint.setColor(Color.RED);
        specialpaint.setStrokeWidth(10);

        if (faces != null) {
            if(faces.length > 0) {
                int lucky_number = new Random().nextInt(faces.length);
                int count = 0;
                for (Face face : faces) {
                    FaceRectangle faceRectangle = face.faceRectangle;
                    if (lucky_number == count) {
                        canvas.drawRect(
                                faceRectangle.left,
                                faceRectangle.top,
                                faceRectangle.left + faceRectangle.width,
                                faceRectangle.top + faceRectangle.height,
                                specialpaint);
                    } else {
                        canvas.drawRect(
                                faceRectangle.left,
                                faceRectangle.top,
                                faceRectangle.left + faceRectangle.width,
                                faceRectangle.top + faceRectangle.height,
                                paint);
                    }
                    count++;
                }
            }
        }
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), uri);
                ImageView imageView = findViewById(R.id.img_view);
                imageView.setImageBitmap(bitmap);

                // Uncomment
                detectAndFrame(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recog_btn:
                detectAndFrame(target);
                break;
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

 package com.example.pddc.ui.activities;

 import android.Manifest;
 import android.annotation.SuppressLint;
 import android.app.Activity;
 import android.content.ContentValues;
 import android.content.Intent;
 import android.content.pm.PackageManager;
 import android.graphics.Bitmap;
 import android.graphics.drawable.BitmapDrawable;
 import android.net.Uri;
 import android.os.Bundle;
 import android.provider.MediaStore;
 import android.view.View;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.activity.result.ActivityResultLauncher;
 import androidx.activity.result.contract.ActivityResultContracts;
 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.core.app.ActivityCompat;
 import androidx.core.content.FileProvider;

 import com.example.pddc.R;
 import com.example.pddc.databinding.ActivityDetectionsBinding;

 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.text.SimpleDateFormat;
 import java.util.Base64;
 import java.util.Calendar;
 import java.util.Locale;

 import okhttp3.Call;
 import okhttp3.Callback;
 import okhttp3.FormBody;
 import okhttp3.OkHttpClient;
 import okhttp3.Request;
 import okhttp3.RequestBody;
 import okhttp3.Response;

 public class Detections extends AppCompatActivity {

     ActivityDetectionsBinding binding;
     ActivityResultLauncher<Uri> takePictureLauncher;
     Uri imageUri;


     private static final String AI_MODEL_API_URL = "https://my-ai-model-api-url/detect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetectionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageUri = createUri();
        registerPictureLauncher();


        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(Detections.this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        });

        //Displaying Real Date
        TextView tv_Day = findViewById(R.id.tvDay);
        TextView tv_Date = findViewById(R.id.tvDate);

        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);//Current day

        String dayOfWeekString = getDayOfWeekString(dayOfWeek);//Current date

        //Date format
        java.lang.String dateFormat = "d MMMM, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        String dateString = simpleDateFormat.format(Calendar.getInstance().getTime());

        //Updating day and date.
        tv_Day.setText(dayOfWeekString);
        tv_Date.setText(dateString);

        binding.btnUpload.setOnClickListener(View -> openImagePicker());

        binding.btnScan.setOnClickListener(View -> scanImageForDiseases());
    }
    private Uri createUri() {
        File imageFile = new File(getApplicationContext().getFilesDir(), "camera_photos.jpg");
        return FileProvider.getUriForFile(
                getApplicationContext(),
                "com.example.pddc.cameraProvider",
                imageFile
        );
    }

    private void registerPictureLauncher() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                o -> {
                    try {
                        if (o) {
                            binding.ivDetectionImage.setImageURI(null);
                            binding.ivDetectionImage.setImageURI(imageUri);
                        }
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
        );
    }

    private void openImagePicker() {
        if (ActivityCompat.checkSelfPermission(Detections.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Detections.this,
                    new String[]{Manifest.permission.CAMERA}, 3);
            return;
        }

        if (ActivityCompat.checkSelfPermission(Detections.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Detections.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }

        Intent garellyIntent = new Intent(Intent.ACTION_PICK);
        garellyIntent.setType("image/*");

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new ContentValues());
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        Intent chooserIntent = Intent.createChooser(garellyIntent, "Choose an action");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        startActivityForResult(chooserIntent, 2);

    }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
             if (data != null && data.getData() != null) {
                 imageUri = data.getData();
             }
             if (imageUri != null) {
                 try {
                     Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                     binding.ivDetectionImage.setImageBitmap(null);
                     binding.ivDetectionImage.setImageBitmap(bitmap);
                 } catch (Exception e) {
                     e.getStackTrace();
                 }
             } else {
                 takePictureLauncher.launch(null);
             }
         }
     }

     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if (requestCode == 3) {
             if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 openImagePicker();
             } else {
                 Toast.makeText(this, "Permission DENIED!", Toast.LENGTH_SHORT).show();
             }
         }

         if (requestCode == 1) {
             if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 openImagePicker();
             } else {
                 Toast.makeText(this, "Permission DENIED!", Toast.LENGTH_SHORT).show();
             }
         }
     }

     private void scanImageForDiseases() {
        if (binding.ivDetectionImage.getDrawable() == null) {
            Toast.makeText(this, "No image uploaded!", Toast.LENGTH_SHORT).show();
        } else {
            binding.btnUpload.setEnabled(false);
            binding.btnScan.setEnabled(false);
            binding.pbScanning.setVisibility(View.VISIBLE);
            Bitmap bitmap = ((BitmapDrawable) binding.ivDetectionImage.getDrawable()).getBitmap();
            String base64Image = convertBitmapToBase64(bitmap);

            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("image", base64Image)
                    .build();

            Request request = new Request.Builder()
                    .url(AI_MODEL_API_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(Detections.this, "Failed to connect to the Model", Toast.LENGTH_SHORT).show();
                        binding.pbScanning.setVisibility(View.GONE);
                        binding.btnUpload.setEnabled(true);
                        binding.btnScan.setEnabled(true);
                    });

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        final String result = response.body().string();
                        runOnUiThread(() -> handleAPIResponse(result));
                    }
                }

                private void handleAPIResponse(String response) {
                    switch (response.trim().toLowerCase()) {
                        case "coccidiosis":
                            binding.pbScanning.setVisibility(View.GONE);
                            binding.tvDetectionResponse.setVisibility(View.VISIBLE);
                            binding.tvDetectionResponse.setText(R.string.coccidiosis_is_detected);
                            binding.btnUpload.setEnabled(true);
                            binding.btnScan.setEnabled(true);
                            break;
                        case "avian":
                            binding.pbScanning.setVisibility(View.GONE);
                            binding.tvDetectionResponse.setVisibility(View.VISIBLE);
                            binding.tvDetectionResponse.setText(R.string.avian_is_detected);
                            binding.btnUpload.setEnabled(true);
                            binding.btnScan.setEnabled(true);
                            break;
                        case "no_disease":
                            binding.pbScanning.setVisibility(View.GONE);
                            binding.tvDetectionResponse.setVisibility(View.VISIBLE);
                            binding.tvDetectionResponse.setText(R.string.no_disease_is_detected);
                            binding.btnUpload.setEnabled(true);
                            binding.btnScan.setEnabled(true);
                            break;
                        default:
                            binding.pbScanning.setVisibility(View.GONE);
                            binding.tvDetectionResponse.setVisibility(View.VISIBLE);
                            binding.tvDetectionResponse.setText(R.string.unable_to_detect);
                            binding.btnUpload.setEnabled(true);
                            binding.btnScan.setEnabled(true);
                            break;
                    }
                }
            });
        }
     }


     @SuppressLint("NewApi")
     private String convertBitmapToBase64(Bitmap bitmap) {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
         byte[] byteArray = byteArrayOutputStream.toByteArray();
         return Base64.getEncoder().encodeToString(byteArray);
     }

     /** Handling Real Date **/
     private String getDayOfWeekString(int dayOfWeek) {
         String[] daysOfWeek = {"Sunday,","Monday,","Tuesday,","Wednesday,","Thursday,","Friday,","Saturday,"};
         return daysOfWeek[dayOfWeek -1];
     }
}
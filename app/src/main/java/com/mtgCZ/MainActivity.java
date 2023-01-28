package com.mtgCZ;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.mtgCZ.model.CRCard;
import com.mtgCZ.model.ScryfallCard;
import com.mtgCZ.network.RetrofitClientInstanceCR;
import com.mtgCZ.network.RetrofitClientInstanceScryfall;
import com.mtgCZ.service.MtgSearchService;
import com.mtgCZ.service.ScryfallNameSearch;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
//        wakeUpBe();

        if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.QUERY_ALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) )
        {
            requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.QUERY_ALL_PACKAGES,
                            Manifest.permission.INTERNET,
                            Manifest.permission.CAMERA},
                    PERMISSIONS_MULTIPLE_REQUEST);
        } else {
            initButtons();
        }
    }

    private void wakeUpBe() {
        MtgSearchService mtgSearchService = RetrofitClientInstanceCR.getRetrofitInstance().create(MtgSearchService.class);
        Call<String> call = mtgSearchService.ping();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("err", "PING OK");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("err", "PING FAIL");
            }
        });
    }

    private void initButtons() {
        Button captureButton = findViewById(R.id.btn_take_picture);
        captureButton.setOnClickListener(
                v -> {
                    dispatchTakePictureIntent();
                }
        );

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.mtgcz.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
//        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            Bitmap photo = (Bitmap) data.getExtras().get(MediaStore.EXTRA_OUTPUT);
//            firebaseDetectText(photo);
//            ImageView image = (ImageView) findViewById(R.id.imageView1);
//            image.setImageBitmap(photo);
            Bitmap photo = setPic();
            firebaseDetectText(photo);
        }
    }

    private Bitmap setPic() {
        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        imageView.setImageBitmap(rotatedBitmap);

        return rotatedBitmap;
    }

    private void firebaseDetectText(Bitmap photo) {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(photo);
        FirebaseVisionTextRecognizer cloudTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        Task<FirebaseVisionText> result =
                cloudTextRecognizer.processImage(firebaseVisionImage)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                // Task completed successfully
                                Log.d("info", "-----------------------------------------------");
                                Log.d("info", "RESULT camera: " + firebaseVisionText.toString());
                                Log.d("info", "RESULT camera: " + firebaseVisionText.getText());
                                Log.d("info", "-----------------------------------------------");
                                TextView result = (TextView) findViewById(R.id.result);
                                ListView cardList = findViewById(R.id.cardList);
                                cardList.setAdapter(null);
                                result.setText(firebaseVisionText.getText());
                                if (firebaseVisionText.getTextBlocks().size() > 0) {
                                    fixBrokenCardName(firebaseVisionText.getTextBlocks().get(0).getText());
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        Log.d("err", "ERROR camera: " + e.getMessage());
                                    }
                                });
    }

    private void callBEPriceSearch(String cardName) {
        MtgSearchService mtgSearchService = RetrofitClientInstanceCR.getRetrofitInstance().create(MtgSearchService.class);
        Call<List<CRCard>> call = mtgSearchService.getCard(cardName);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        call.enqueue(new Callback<List<CRCard>>() {
            @Override
            public void onResponse(Call<List<CRCard>> call, Response<List<CRCard>> response) {
                for (CRCard card : response.body()) {
                    Log.d("find", card.getName());
                }
                setCardList(response.body());
            }

            @Override
            public void onFailure(Call<List<CRCard>> call, Throwable t) {
                Log.d("err", t.getMessage());
            }
        });
    }

    private void fixBrokenCardName (String cardName) {
        ScryfallNameSearch scryfallNameSearch = RetrofitClientInstanceScryfall.getRetrofitInstance().create(ScryfallNameSearch.class);
        Call<ScryfallCard> call = scryfallNameSearch.findCardNameFromFuzzyText(cardName);
        call.enqueue(new Callback<ScryfallCard>() {
            @Override
            public void onResponse(Call<ScryfallCard> call, Response<ScryfallCard> response) {
                if(response.body() != null)
                    callBEPriceSearch(response.body().getName());
                else
                    Log.d("err", "ERROR EMPTY BODY");
            }

            @Override
            public void onFailure(Call<ScryfallCard> call, Throwable t) {
                Log.d("err", t.getMessage());
            }
        });
    }

    private void setCardList(List<CRCard> cards) {
        ListView myList = (ListView) findViewById(R.id.cardList);
        ArrayList<String> formattedCards = new ArrayList<>();
        for (CRCard card : cards) {
            formattedCards.add(" " + card.getName() + " - " + card.getStock() + "ks - " + card.getPrice() + "Kč - " + Math.floor(80*(card.getPrice()/100.0f)));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.card_listview, formattedCards);
        myList.setAdapter(adapter);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_MULTIPLE_REQUEST) {
            if (grantResults.length > 0) {
                initButtons();
            }
        }
    }
}
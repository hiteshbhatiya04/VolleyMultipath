package in.vnurture.volleymultipath;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_SETTINGS;

public class MainActivity extends AppCompatActivity {

    TextView txt_pdf_upload,edit_circular_date;
    Button btn_upload_circular;
    private static final int STORAGE_PERMISSION_CODE = 123;
    Uri selectedFileUri;
    String trustee_id="7",str_cdate="",sangh_id ="1",str_title,str_desc,currentdate,selecteddate,circular_date="";
    SimpleDateFormat simpleDateFormat,inputFormat,outputFormat;
    Date cdate=null;
    ImageView imageView;
    Calendar calendar;
    Bitmap myBitmap;
    Uri picUri;
    String profile_img="";
    EditText edit_circular_title,edit_circular_des;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermission()) {

            requestPermission();

        } else {
            //Toast.makeText(ChapterListActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }

        inputFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        outputFormat = new SimpleDateFormat("dd MMM yyyy",Locale.US);

        txt_pdf_upload=(TextView)findViewById(R.id.txt_pdf_upload);
        btn_upload_circular=(Button)findViewById(R.id.btn_upload_circular);
        edit_circular_title=(EditText)findViewById(R.id.edit_circular_title);
        edit_circular_date=(TextView)findViewById(R.id.edit_circular_date);
        edit_circular_des=(EditText)findViewById(R.id.edit_circular_des);
        imageView=(ImageView)findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), 111);

            }
        });

        edit_circular_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR);
                mMonth=calendar.get(Calendar.MONTH);
                mDay=calendar.get(Calendar.DAY_OF_MONTH);
                currentdate = simpleDateFormat.format(calendar.getTime());
                showDate(mYear,mMonth,mDay,R.style.DatePickerSpinner);
            }
        });

        txt_pdf_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Pdf"),200);
            }
        });

        btn_upload_circular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_title=edit_circular_title.getText().toString();
                str_desc =edit_circular_des.getText().toString();

                if (TextUtils.isEmpty(str_title))
                {
                    edit_circular_title.setError("Enter Title");
                    edit_circular_title.requestFocus();
                }
                else if (TextUtils.isEmpty(circular_date))
                {
                    edit_circular_date.setError("Enter Circular Date");
                    edit_circular_date.requestFocus();
                }
                else if (TextUtils.isEmpty(str_desc))
                {
                    edit_circular_des.setError("Enter Description");
                    edit_circular_des.requestFocus();
                }
                else {

                    String UPLOAD_URL = "url";

                    try {
                        String path = FilePath.getPath(MainActivity.this, selectedFileUri);
                        String uploadId = UUID.randomUUID().toString();
                        //Creating a multi part request
                        new MultipartUploadRequest(MainActivity.this, uploadId, UPLOAD_URL)
                                .addFileToUpload(path, "upload_doc_file") //Adding file
                                .addParameter("karobari_trustee", trustee_id)
                                .addParameter("upload_doc_date", circular_date)
                                .addParameter("upload_doc_titile", str_title)
                                .addParameter("sangh_id", sangh_id)
                                .addParameter("upload_doc_des", str_desc)//Adding text parameter to the request
                                .setNotificationConfig(new UploadNotificationConfig())
                                .setMaxRetries(2)
                                .startUpload();
                        Log.d("uploadtrustee data", "trusty id:");
                        //Starting the upload
                        Toast.makeText(MainActivity.this, "Submitted...", Toast.LENGTH_SHORT).show();
                        onBackPressed();


                    } catch (Exception exc) {
                        Toast.makeText(MainActivity.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE, CAMERA,WRITE_SETTINGS,CALL_PHONE}, 120);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 200){
                selectedFileUri=data.getData();
                txt_pdf_upload.setText(String.valueOf(selectedFileUri));
                //Toast.makeText(this, ""+textView_pdf, Toast.LENGTH_SHORT).show();
            }
            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);
                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                    myBitmap = rotateImageIfRequired(myBitmap,MainActivity.this,picUri);
                    myBitmap = getResizedBitmap(myBitmap, 500);
                    imageView.setImageBitmap(myBitmap);
                    //m_mybitmap = String.valueOf(Utils.setImage(myBitmap));
                    profile_img = Utils.setImage(myBitmap).replace("\n", "");
                    //Toast.makeText(this, ""+profile_img, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                picUri = getPickImageResultUri(data);
                if (imageView != null) {
                    imageView.setImageBitmap(myBitmap);
                }
                imageView.setImageBitmap(myBitmap);
            }
        }
    }

    private void showDate(int mYear, int mMonth, int mDay, int datePickerSpinner) {

        new SpinnerDatePickerDialogBuilder()
                .context(MainActivity.this)
                .callback(new com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int month=monthOfYear+1;
                        String formattedMonth = "" + month;
                        String formattedDayOfMonth = "" + dayOfMonth;
                        if(month < 10){

                            formattedMonth = "0" + month;
                        }
                        if(dayOfMonth < 10){

                            formattedDayOfMonth = "0" + dayOfMonth;
                        }
                        selecteddate = year + "-" + formattedMonth + "-" +  formattedDayOfMonth;

                        //Toast.makeText(EditProfile1.this, ""+selecteddate, Toast.LENGTH_SHORT).show();
                        try {
                            cdate = inputFormat.parse(selecteddate);
                            str_cdate = outputFormat.format(cdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        circular_date=selecteddate;
                        edit_circular_date.setText(str_cdate);
                    }
                })
                .spinnerTheme(datePickerSpinner)
                .minDate(mYear,mMonth,mDay)
                .defaultDate(mYear, mMonth, mDay)
                .build()
                .show();
    }


    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }
        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }
    /**
     * Get URI to image received from capture by camera.
     */

    private static Bitmap rotateImageIfRequired(Bitmap img, Context context, Uri selectedImage) throws IOException {
        if (selectedImage.getScheme().equals("content")) {
            String[] projection = { MediaStore.Images.ImageColumns.ORIENTATION };
            Cursor c = context.getContentResolver().query(selectedImage, projection, null, null, null);
            if (c.moveToFirst()) {
                final int rotation = c.getInt(0);
                c.close();
                return rotateImage(img, rotation);
            }
            return img;
        }
        else
        {
            ExifInterface ei = new ExifInterface(selectedImage.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        }
    }
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }
    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        //img.recycle();
        return rotatedImg;
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

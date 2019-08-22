package com.ardeapps.floorballmanager.dialogFragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.BuildConfig;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.ardeapps.floorballmanager.services.FragmentListeners.MY_PERMISSION_ACCESS_READ_EXTERNAL_STORAGE;
import static com.ardeapps.floorballmanager.services.FragmentListeners.MY_PERMISSION_ACCESS_TAKING_PICTURE;

public class SelectPictureDialogFragment extends DialogFragment {

    private static final int TAKE_PICTURE = 0;
    private static final int SELECT_PICTURE = 1;
    SelectPictureDialogCloseListener mListener = null;
    EditText urlText;
    Button galleryButton;
    Button defaultButton;
    Button cancelButton;
    Button uploadButton;
    Button cameraButton;

    private String mCurrentPhotoPath;

    public void setListener(SelectPictureDialogCloseListener l) {
        mListener = l;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentListeners.getInstance().setPermissionHandledListener(new FragmentListeners.PermissionHandledListener() {
            @Override
            public void onPermissionGranted(int MY_PERMISSION) {
                switch (MY_PERMISSION) {
                    case MY_PERMISSION_ACCESS_TAKING_PICTURE:
                        startCameraWithPermissionChecks();
                        break;
                    case MY_PERMISSION_ACCESS_READ_EXTERNAL_STORAGE:
                        openGalleryWithPermissionChecks();
                        break;
                }
            }

            @Override
            public void onPermissionDenied(int MY_PERMISSION) {
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_select_picture, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        urlText = v.findViewById(R.id.urlText);
        cameraButton = v.findViewById(R.id.cameraButton);
        galleryButton = v.findViewById(R.id.galleryButton);
        defaultButton = v.findViewById(R.id.defaultButton);
        cancelButton = v.findViewById(R.id.cancelButton);
        uploadButton = v.findViewById(R.id.uploadButton);

        galleryButton.setOnClickListener(v15 -> openGalleryWithPermissionChecks());

        defaultButton.setOnClickListener(v14 -> mListener.onDefaultSelected());

        cameraButton.setOnClickListener(v13 -> startCameraWithPermissionChecks());

        uploadButton.setOnClickListener(v12 -> {
            String url = urlText.getText().toString();
            if (!StringUtils.isEmptyString(url)) {
                new DownloadImageTask().execute(url);
            }
        });

        cancelButton.setOnClickListener(v1 -> mListener.onCancelClick());

        return v;
    }

    private void openGalleryWithPermissionChecks() {
        if (ContextCompat.checkSelfPermission(AppRes.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AppRes.getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSION_ACCESS_READ_EXTERNAL_STORAGE);
        } else {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");
            startActivityForResult(pickIntent, SELECT_PICTURE);
        }
    }

    private void startCameraWithPermissionChecks() {
        if (ContextCompat.checkSelfPermission(AppRes.getContext(), Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(AppRes.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(AppRes.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AppRes.getActivity(), new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSION_ACCESS_TAKING_PICTURE);
        } else {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePicture.resolveActivity(AppRes.getContext().getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    return;
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(AppRes.getContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePicture, TAKE_PICTURE);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,
                ".png",
                storageDir
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_PICTURE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Bitmap selectedPicture = ImageUtil.scaleImageForUpload(uri);
                    mListener.onPictureSelected(selectedPicture);
                }
                break;
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    Bitmap selectedPicture = ImageUtil.scaleImageForUpload(imageUri);
                    mListener.onPictureSelected(selectedPicture);
                }
                break;
            default:
                break;
        }
    }

    public interface SelectPictureDialogCloseListener {
        void onPictureSelected(Bitmap logo);

        void onDefaultSelected();

        void onCancelClick();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        public DownloadImageTask() {
        }

        protected Bitmap doInBackground(String... urls) {
            String displayUrl = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(displayUrl);
                URLConnection connection = url.openConnection();
                String contentType = connection.getHeaderField("Content-Type");
                boolean image = contentType.startsWith("image/");
                if (image) {
                    InputStream in = url.openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                }
            } catch (Exception e) {
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                mListener.onPictureSelected(result);
            } else {
                Logger.toast(getString(R.string.select_picture_error));
            }
        }
    }
}

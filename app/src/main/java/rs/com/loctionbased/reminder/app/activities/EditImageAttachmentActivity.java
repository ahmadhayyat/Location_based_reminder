package rs.com.loctionbased.reminder.app.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.transitionseverywhere.Rotate;
import com.transitionseverywhere.TransitionManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.SharedClass;
import rs.com.loctionbased.reminder.enums.TapTargetSequenceType;
import rs.com.loctionbased.reminder.model.attachment.ImageAttachment;
import rs.com.loctionbased.reminder.util.FileUtil;
import rs.com.loctionbased.reminder.util.ImageUtil;
import rs.com.loctionbased.reminder.util.PermissionUtil;
import rs.com.loctionbased.reminder.util.SnackbarUtil;
import rs.com.loctionbased.reminder.util.TapTargetSequenceUtil;

public class EditImageAttachmentActivity extends AppCompatActivity implements View.OnClickListener {


    //CONSTS
    private static final int REQUEST_TAKE_PICTURE_PERMISSION = 239;
    private static String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String TAG = EditImageAttachmentActivity.class.getSimpleName();
    public static final String IMAGE_FILE_EXTENSION = ".jpg";
    private static final int IMAGE_COMPRESSION_PERCENTAGE = 30;
    private static final int THUMBNAIL_COMPRESSION_PERCENTAGE = 60;
    private static final int REQUEST_IMAGE_CAPTURE = 123;
    private static final int REQUEST_PICK_IMAGE_GALLERY = 124;


    public static final String IMAGE_ATTACHMENT_EXTRA = "IMAGE_ATTACHMENT_EXTRA";
    public static final String HOLDER_POSITION_EXTRA = "HOLDER_POSITION_EXTRA";
    public static final String EDITING_ATTACHMENT_EXTRA = "EDITING_ATTACHMENT_EXTRA";
    public static final int EDIT_IMAGE_ATTACHMENT_REQUEST_CODE = 82;

    //DATA
    private int mRotation;
    private boolean mEditingExistingImageAttachment;
    private ImageAttachment mImageAttachment;
    private int mHolderPosition;
    private Bitmap mImageBackup;

    //UI
    private RelativeLayout mContainer;
    private ImageView mImage;
    private FloatingActionButton mCrop;
    private FloatingActionButton mRotate;
    private FloatingActionButton mCamera;
    private Button mOk;
    private Button mCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image_attachment);

        mContainer = (RelativeLayout) findViewById(R.id.activity_edit_image_attachment_container);
        mImage = (ImageView) findViewById(R.id.activity_edit_image_attachment_image);
        mCrop = (FloatingActionButton) findViewById(R.id.activity_edit_image_attachment_crop);
        mRotate = (FloatingActionButton) findViewById(R.id.activity_edit_image_attachment_rotate);
        mCamera = (FloatingActionButton) findViewById(R.id.activity_edit_image_attachment_camera);
        mOk = (Button) findViewById(R.id.activity_edit_image_attachment_ok);
        mCancel = (Button) findViewById(R.id.activity_edit_image_attachment_cancel);

        if (savedInstanceState != null) {
            mImageAttachment = (ImageAttachment) savedInstanceState.getSerializable(IMAGE_ATTACHMENT_EXTRA);
            mHolderPosition = savedInstanceState.getInt(HOLDER_POSITION_EXTRA);
            mEditingExistingImageAttachment = savedInstanceState.getBoolean(EDITING_ATTACHMENT_EXTRA);

            mImageBackup = ImageUtil.getBitmap(new File(FileUtil.getImageAttachmentDir(this), mImageAttachment.getImageFilename()));
            if (mImageBackup == null) {  //If jpeg was deleted from device, use thumbnail
                mImageBackup = ImageUtil.getBitmap(mImageAttachment.getThumbnail());
                saveThumbnailAsImageFile(mImageBackup);
            }

            mImage.setImageBitmap(mImageBackup);
            showTapTargetSequence();
        } else {

            if (getIntent().hasExtra(HOLDER_POSITION_EXTRA) && getIntent().hasExtra(IMAGE_ATTACHMENT_EXTRA)) {
                mHolderPosition = getIntent().getIntExtra(HOLDER_POSITION_EXTRA, -1);
                mImageAttachment = (ImageAttachment) getIntent().getSerializableExtra(IMAGE_ATTACHMENT_EXTRA);

                if (mImageAttachment.getImageFilename() != null && !mImageAttachment.getImageFilename().isEmpty()) {
                    mImageBackup = ImageUtil.getBitmap(new File(FileUtil.getImageAttachmentDir(this), mImageAttachment.getImageFilename()));
                    if (mImageBackup == null) {  //IF image was deleted from device
                        mImageBackup = ImageUtil.getBitmap(mImageAttachment.getThumbnail());
                        saveThumbnailAsImageFile(mImageBackup);
                    }

                    mEditingExistingImageAttachment = true;
                    mImage.setImageBitmap(mImageBackup);
                    showTapTargetSequence();
                } else {
                    mImageAttachment = new ImageAttachment();
                    mImageAttachment.setImageFilename(UUID.randomUUID().toString() + IMAGE_FILE_EXTENSION);
                    handleImageCapture();
                }
            } else {
                BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                };
                Log.e(TAG, "Missing HOLDER_POSITION_EXTRA and/or IMAGE_ATTACHMENT_EXTRA parameters in EditImageAttachmentActivity.");
                SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, callback);
                finish();
            }

        }


        mRotation = 0;
        mCrop.setOnClickListener(this);
        mRotate.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    private void showTapTargetSequence() {
        TapTargetSequenceUtil.showTapTargetSequenceFor(this, TapTargetSequenceType.EDIT_IMAGE_ATTACHMENT_ACTIVITY);
    }


    private void saveThumbnailAsImageFile(Bitmap thumbnail) {
        File imageFile = new File(FileUtil.getImageAttachmentDir(this), mImageAttachment.getImageFilename());
        try {
            ImageUtil.saveBitmapAsJpeg(imageFile, thumbnail, IMAGE_COMPRESSION_PERCENTAGE);
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_edit_image_attachment_snackbar_error_image_deleted_from_device, SnackbarUtil.SnackbarDuration.LONG, null);

        } catch (IOException e) {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    setResult(RESULT_CANCELED);
                    finish();
                }
            };
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, callback);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        applyPendingRotation();
        outState.putSerializable(IMAGE_ATTACHMENT_EXTRA, mImageAttachment);
        outState.putInt(HOLDER_POSITION_EXTRA, mHolderPosition);
        outState.putBoolean(EDITING_ATTACHMENT_EXTRA, mEditingExistingImageAttachment);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.activity_edit_image_attachment_crop:
                applyPendingRotation();
                File imageFile = new File(FileUtil.getImageAttachmentDir(this), mImageAttachment.getImageFilename());
                Uri imageUri = FileProvider.getUriForFile(this, "rs.com.razasaeed.memorizeme.fileprovider", imageFile);
                CropImage.activity(imageUri).setAllowFlipping(false).setAllowRotation(false).start(this);
                break;

            case R.id.activity_edit_image_attachment_rotate:
                mRotation += 90;

                TransitionManager.beginDelayedTransition(mContainer, new Rotate());
                mImage.setRotation(mRotation);
                break;

            case R.id.activity_edit_image_attachment_camera:
                handleImageCapture();
                break;

            case R.id.activity_edit_image_attachment_ok:
                applyPendingRotation();
                updateImageAttachmentThumbnail();

                Intent returnData = new Intent();
                returnData.putExtra(HOLDER_POSITION_EXTRA, mHolderPosition);
                returnData.putExtra(IMAGE_ATTACHMENT_EXTRA, mImageAttachment);
                setResult(RESULT_OK, returnData);
                finish();
                break;

            case R.id.activity_edit_image_attachment_cancel:
                if (mEditingExistingImageAttachment)
                    restoreImageFromBackup();
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    private void applyPendingRotation() {
        if (mRotation != 0) {
            mRotation = mRotation % 360;
            if (mRotation < 0) mRotation += 360;

            File imageFile = new File(FileUtil.getImageAttachmentDir(this), mImageAttachment.getImageFilename());
            Bitmap imageBitmap = ImageUtil.getBitmap(imageFile);

            Matrix matrix = new Matrix();
            matrix.postRotate(mRotation);
            imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);

            try {
                ImageUtil.saveBitmapAsJpeg(imageFile, imageBitmap, IMAGE_COMPRESSION_PERCENTAGE);
            } catch (IOException e) {
                SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_edit_image_attachment_snackbar_error_could_not_rotate, SnackbarUtil.SnackbarDuration.LONG, null);
            }

            mImage.setImageBitmap(imageBitmap);
            mImage.setRotation(0);

            mRotation = 0;
        }
    }

    private void restoreImageFromBackup() {
        File imageFile = new File(FileUtil.getImageAttachmentDir(this), mImageAttachment.getImageFilename());
        try {
            ImageUtil.saveBitmapAsJpeg(imageFile, mImageBackup, IMAGE_COMPRESSION_PERCENTAGE);
        } catch (IOException e) {
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_edit_image_attachment_snackbar_error_could_not_rotate, SnackbarUtil.SnackbarDuration.LONG, null);
        }
    }


    private void handleImageCapture() {
        String[] nonGrantedPermissions = PermissionUtil.checkIfPermissionsAreGranted(this, permissions);

        if (nonGrantedPermissions == null)
            dispatchTakePictureIntent();
        else
            ActivityCompat.requestPermissions(this, nonGrantedPermissions, REQUEST_TAKE_PICTURE_PERMISSION);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_TAKE_PICTURE_PERMISSION:
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                handleImageCapture();
                            }
                        };
                        SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.NOTICE, R.string.activity_edit_image_attachment_snackbar_error_no_permissions, SnackbarUtil.SnackbarDuration.SHORT, callback);
                        return;
                    }
                }

                dispatchTakePictureIntent();
                break;
        }
    }


    private void dispatchTakePictureIntent() {

        File imageAttachmentDir = FileUtil.getImageAttachmentDir(this);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            FileUtil.createDirIfNotExists(imageAttachmentDir);
        } catch (IOException ex) {
            Log.e(TAG, "Error while creating the image directory");
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, null);
        }

        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {

            File imageAttachmentFile;
            try {
                imageAttachmentFile = FileUtil.createNewFileIfNotExistsInDir(imageAttachmentDir, mImageAttachment.getImageFilename());
            } catch (IOException ex) {
                Log.e(TAG, "Error while creating the image");
                SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, null);
                imageAttachmentFile = null;
            }

            Uri imageUri;
            if (imageAttachmentFile != null) {
                try {
                    imageUri = FileProvider.getUriForFile(this, "rs.com.razasaeed.memorizeme.fileprovider", imageAttachmentFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                    List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        this.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }

                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "There was a problem with the image");
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, null);
                }

            } else {
                Log.e(TAG, "There was a problem loading or creating the file for the image");
                SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, null);
            }
        } else {
            Log.e(TAG, getResources().getString(R.string.activity_edit_image_attachment_snackbar_error_no_camera_installed));
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_edit_image_attachment_snackbar_error_no_camera_installed, SnackbarUtil.SnackbarDuration.LONG, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            checkExifAndFixImageRotation();
            Bitmap newImage = ImageUtil.getBitmap(new File(FileUtil.getImageAttachmentDir(this), mImageAttachment.getImageFilename()));
            mImage.setImageBitmap(newImage);
            showTapTargetSequence();

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap newImage = ImageUtil.getBitmap(result.getUri(), this);
                    mImage.setImageBitmap(newImage);
                    ImageUtil.saveBitmapAsJpeg(new File(FileUtil.getImageAttachmentDir(this), mImageAttachment.getImageFilename()), newImage, IMAGE_COMPRESSION_PERCENTAGE);
                } catch (IOException e) {
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_edit_image_attachment_snackbar_error_loading_cropped_image, SnackbarUtil.SnackbarDuration.LONG, null);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, getResources().getString(R.string.activity_edit_image_attachment_snackbar_error_loading_cropped_image) + result.getError().toString());
                SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_edit_image_attachment_snackbar_error_loading_cropped_image, SnackbarUtil.SnackbarDuration.LONG, null);
            }
        } else if (requestCode == REQUEST_PICK_IMAGE_GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(imageUri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            SharedClass.picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap newImage = ImageUtil.getBitmap(new File(FileUtil.getImageAttachmentDir(this), mImageAttachment.getImageFilename()));
            mImage.setImageBitmap(newImage);
            showTapTargetSequence();

            mImage.setImageURI(imageUri);
        }

    }

    private void checkExifAndFixImageRotation() {
        try {
            ExifInterface ei = new ExifInterface(new File(FileUtil.getImageAttachmentDir(this), mImageAttachment.getImageFilename()).getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    mRotation = 90;
                    applyPendingRotation();
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    mRotation = 180;
                    applyPendingRotation();
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    mRotation = 270;
                    applyPendingRotation();
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                    //Good!

                default:
                    break;
            }
        } catch (IOException e) {
        }
    }


    private void updateImageAttachmentThumbnail() {
        try {
            Bitmap thumbnail = ImageUtil.getBitmap(new File(FileUtil.getImageAttachmentDir(this), mImageAttachment.getImageFilename()));
            thumbnail = ImageUtil.scaleBitmap(thumbnail, 480);
            byte[] thumbnailBytes = ImageUtil.toCompressedByteArray(thumbnail, THUMBNAIL_COMPRESSION_PERCENTAGE);
            mImageAttachment.setThumbnail(thumbnailBytes);
        } catch (Exception e) {
            Log.e(TAG, "There was a problem updating the thumbnail");
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, null);
        }
    }

}
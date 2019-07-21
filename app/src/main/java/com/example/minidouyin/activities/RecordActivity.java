package com.example.minidouyin.activities;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.minidouyin.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * @author: puppy
 * @Date: 2019/7/19
 * @Time: 21:37
 */

public class RecordActivity extends Activity {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Camera.Parameters parameters;

    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int rotationDegree = 0;
    private int recLen = 0;
    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;
    private float oldDist = 1f;

    private boolean isRecording = false;
    private String videoPath;
    private Uri onPauseUri;
    private Uri videoUri;

    private ImageView img_Record;
    private ImageView img_Switch;
    private ImageView img_Back;
    private ProgressBar progressBar;

    private Handler mHandler= new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRecording){
                if(recLen<20) {
                    recLen++;
                    progressBar.setProgress(recLen * 5);
                    mHandler.postDelayed(this, 500);
                } else {
                    recLen = 0;
                    progressBar.setProgress(0);
                    img_Switch.setEnabled(true);
                    releaseMediaRecorder();
                    videoUri =Uri.fromFile(new File(videoPath));
                    viewVideo(videoUri);
                    img_Record.setImageResource(R.mipmap.outline_radio_button_checked_white_48);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_record);
        releaseCameraAndPreview();

        mSurfaceView = findViewById(R.id.img);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mCamera = getCamera(CAMERA_TYPE);
                rotationDegree = getCameraDisplayOrientation(CAMERA_TYPE);
                mCamera.setDisplayOrientation(rotationDegree);
                try {
                    startPreview(holder);
                } catch (Exception e){
                    e.printStackTrace();
                    releaseCameraAndPreview();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (holder.getSurface() == null) {
                    return;
                }
                try {
                    mCamera.stopPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    parameters = mCamera.getParameters();
                    List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
                    Camera.Size optimalSize = getOptimalPreviewSize(sizes, width, height);
                    parameters.setPreviewSize(optimalSize.width, optimalSize.height);
                    mCamera.setParameters(parameters);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                startPreview(holder);

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                releaseMediaRecorder();
                releaseCameraAndPreview();
            }
        });

        img_Record = findViewById(R.id.img_Record);
        img_Switch = findViewById(R.id.img_Switch);
        img_Back = findViewById(R.id.img_Back);
        progressBar = findViewById(R.id.progressBar);

        img_Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    recLen = 0;
                    progressBar.setProgress(0);
                    img_Switch.setEnabled(true);
                    releaseMediaRecorder();
                    videoUri =Uri.fromFile(new File(videoPath));
                    viewVideo(videoUri);
                    img_Record.setImageResource(R.mipmap.outline_radio_button_checked_white_48);
                } else {
                    if (prepareVideoRecorder()) {
                        isRecording = true;
                        img_Switch.setEnabled(false);
                        img_Record.setImageResource(R.mipmap.round_check_circle_white_48);
                        mHandler.postDelayed(runnable,0);
                    } else {
                        releaseMediaRecorder();
                    }
                }
            }
        });

        img_Record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        img_Record.setAlpha(0.5f);
                        break;
                    } case MotionEvent.ACTION_UP: {
                        img_Record.setAlpha(1f);
                        break;
                    }
                }
                return false;
            }
        });

        img_Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCamera == null) {
                    return;
                }
                if (CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_FRONT;
                } else if (CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_FRONT){
                    CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                mCamera = getCamera(CAMERA_TYPE);
                startPreview(mSurfaceHolder);
            }
        });

        img_Switch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        img_Switch.setAlpha(0.5f);
                        break;
                    } case MotionEvent.ACTION_UP: {
                        img_Switch.setAlpha(1f);
                        break;
                    }
                }
                return false;
            }
        });

        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecordActivity.this,HomeActivity.class));
            }
        });

        img_Back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        img_Back.setAlpha(0.5f);
                        break;
                    } case MotionEvent.ACTION_UP: {
                        img_Back.setAlpha(1f);
                        break;
                    }
                }
                return false;
            }
        });
    }


    //TODO
    private static float getFingerDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void cameraZoom(boolean isZoomIn, Camera camera) {
        parameters = camera.getParameters();
        if (parameters.isZoomSupported()) {
            int maxZoom = parameters.getMaxZoom();
            int currentZoom = parameters.getZoom();
            if (isZoomIn && currentZoom < maxZoom) {
                currentZoom+=2;
                if (currentZoom > maxZoom) {
                    currentZoom = maxZoom;
                }
            } else if (currentZoom > 0) {
                currentZoom-=2;
                if (currentZoom < 0) {
                    currentZoom = 0;
                }
            }
            parameters.setZoom(currentZoom);
            camera.setParameters(parameters);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            return true;
        } else {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = getFingerDistance(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float newDist = getFingerDistance(event);
                    if (newDist > oldDist) {
                        cameraZoom(true, mCamera);
                    } else if (newDist < oldDist) {
                        cameraZoom(false, mCamera);
                    }
                    oldDist = newDist;
                    break;
            }
        }
        return true;
    }

    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        Camera cam = Camera.open(position);

        rotationDegree = getCameraDisplayOrientation(position);
        cam.setDisplayOrientation(rotationDegree);

        parameters = cam.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            Camera.Parameters mParameters = cam.getParameters();
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            cam.setParameters(mParameters);
        }
        return cam;
    }

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }

    private void releaseCameraAndPreview() {
        if(mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void startPreview(SurfaceHolder holder) {
        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private MediaRecorder mMediaRecorder;

    private boolean prepareVideoRecorder() {
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        switch (CAMERA_TYPE) {
            case Camera.CameraInfo.CAMERA_FACING_BACK:
                rotationDegree = 90;
                mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
                break;
            case Camera.CameraInfo.CAMERA_FACING_FRONT:
                rotationDegree = 270;
                mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
                break;
            default:
                rotationDegree = 0;
                break;
        }

        onPauseUri = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_VIDEO));
        videoPath = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();
        mMediaRecorder.setOutputFile(videoPath);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);

        try{
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (Exception e){
            releaseMediaRecorder();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if(mMediaRecorder == null){
            return;
        }
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();
        isRecording = false;

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(videoPath))));
    }

    private void viewVideo(Uri uri){
        String videoUriPath =uri.toString();
        Intent intent = new Intent();
        intent.setClass(RecordActivity.this,PostActivity.class);
        intent.putExtra("videoUri",videoUriPath);
        startActivity(intent);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(onPauseUri);
        this.sendBroadcast(intent);
    }
}
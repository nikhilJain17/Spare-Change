package njain.com.sparechange;

import android.app.ActionBar;
import android.app.Notification;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {

    private JavaCameraView mCameraView; // handler for the camera
    private OpenCVLoader mOpenCVLoader; // load opencv boi

    // Listener anonymous class, handles callbacks
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {

            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("MainActivity", "Loaded OpenCV successfully!");
                    mCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                }
            } // end of switch

        } // end of onManagerConnected

        @Override
        public void onPackageInstall(int operation, InstallCallbackInterface callback) {
            super.onPackageInstall(operation, callback);
        } // end of onPackageInstall

    }; // end of mLoaderCallback




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mCameraView = (JavaCameraView) findViewById(R.id.CameraView);
        mCameraView.setVisibility(SurfaceView.VISIBLE);
        mCameraView.setCvCameraViewListener(this);

    } // end of onCreate

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback); // init opencv

        // hide the cheeky actionbar
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


    } // end of onResume

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraView != null)
            mCameraView.disableView();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        // Mat tempMat = new Mat();
        // tempMat.release() <-- java has auto garbage collection but c does not
    }

    @Override
    public void onCameraViewStopped() {
        // tempMat.release()
    }

    @Override // when camera delivers a frame
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        // DO NOT INSTANTIATE MAT'S HERE! This is called everytime the camera delivers a frame.

        return inputFrame.rgba();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Log.i("MainActivity", "Touched");


        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Toast.makeText(this, "Analyzing", Toast.LENGTH_SHORT).show();
        } // end of switch

        return true;
    }
} // end of MainActivity

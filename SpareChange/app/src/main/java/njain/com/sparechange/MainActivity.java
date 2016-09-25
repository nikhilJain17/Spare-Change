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
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {

    private JavaCameraView mCameraView; // handler for the camera
    private OpenCVLoader mOpenCVLoader; // load opencv boi

    private Mat tempMat;

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
        mCameraView.setOnTouchListener(this);


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
        tempMat = new Mat();
        // Mat tempMat = new Mat();
        // tempMat.release() <-- java has auto garbage collection but c does not
    }

    @Override
    public void onCameraViewStopped() {
         tempMat.release();
    }

    @Override // when camera delivers a frame
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        // DO NOT INSTANTIATE MAT'S HERE! This is called everytime the camera delivers a frame.

        tempMat = inputFrame.gray();
        Mat circles = new Mat();

        // edge detection!
        Imgproc.Canny(tempMat, tempMat, 100, 100);
        Imgproc.HoughCircles(tempMat, circles, Imgproc.HOUGH_GRADIENT, 69, 69);

        int iCannyUpperThreshold = 100;
        int iMinRadius = 20;
        int iMaxRadius = 400;
        int iAccumulator = 300;

        Imgproc.HoughCircles(tempMat, circles, Imgproc.CV_HOUGH_GRADIENT,
                2.0, tempMat.rows() / 8, iCannyUpperThreshold, iAccumulator,
                iMinRadius, iMaxRadius);

        if (circles.cols() > 0)
            for (int x = 0; x < circles.cols(); x++)
            {
                double vCircle[] = circles.get(0,x);

                if (vCircle == null)
                    break;

                Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
                int radius = (int)Math.round(vCircle[2]);
                Log.i("PointyDick", "xy" + pt.toString());
                Log.i("PointyDick", "radius" + Integer.toString(radius));
                // draw the found circle
                Imgproc.circle(tempMat, pt, radius, new Scalar(0,255,0), 10);
                Imgproc.circle(tempMat, pt, 3, new Scalar(0, 0, 255), 10);
            }

//        if (circleMat != null)
//            Imgproc.circle(tempMat, new Point(150, 150), 69, new Scalar(100, 100, 100));

//        Imgproc.circle(tempMat, , , Imgproc.COLOR_BGR2BGR555);
//        Imgproc.dilate(tempMat, tempMat, tempMat)


        // find contours in the edge map?

//        tempMat.inv();

        return tempMat;
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

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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {

    private JavaCameraView mCameraView; // handler for the camera
    private OpenCVLoader mOpenCVLoader; // load opencv boi

    private Mat tempMat, circles; // holds images from opencv

    private int radius, secondRadius; //holeRadius = 0;
    private double smallestDiff = 1000;
    private String whichOne;

    private int iCannyUpperThreshold = 100;
    private int iMinRadius = 40;
    private int iMaxRadius = 400;
    private double iAccumulator = 100;

    private static double[] RATIOS;


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

        RATIOS = new double[12];

        RATIOS[0] = 0.781893;
        RATIOS[1] = 1 / RATIOS[0];
        RATIOS[2] = 1.06145251;
        RATIOS[3] = 1 / RATIOS[2];
        RATIOS[4] = 0.89622642;
        RATIOS[5] = 1 / RATIOS[4];
        RATIOS[6] = 0.87242798;
        RATIOS[7] = 1 / RATIOS[6];
        RATIOS[8] = 1.18435754;
        RATIOS[9] = 1 / RATIOS[8];
        RATIOS[10] = 0.73662551;
        RATIOS[11] = 1 / RATIOS[10];


    } // end of onCreate

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback); // init opencv

        // hide the cheeky actionbar
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mCameraView.setMaxFrameSize(600, 600);
//        mCameraView.se

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
        circles = new Mat();


        // Mat tempMat = new Mat();
        // tempMat.release() <-- java has auto garbage collection but c does not
    }

    @Override
    public void onCameraViewStopped() {
         tempMat.release();
        circles.release();
    }

    @Override // when camera delivers a frame
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        // DO NOT INSTANTIATE MAT'S HERE! This is called everytime the camera delivers a frame.

        tempMat = inputFrame.gray();

        // edge detection!
        Imgproc.medianBlur(tempMat, tempMat, 5);
//        Imgproc.GaussianBlur(tempMat, tempMat, new Size(9, 9), 2, 2);
        Imgproc.Canny(tempMat, tempMat, 100, 100);
//        Imgproc.HoughCircles(tempMat, circles, Imgproc.HOUGH_GRADIENT, 69, 69);
//        Imgproc.So

//
        Imgproc.HoughCircles(tempMat, circles, Imgproc.CV_HOUGH_GRADIENT,
                2.0, 40, iCannyUpperThreshold, iAccumulator,
                iMinRadius, iMaxRadius);

        if (circles.cols() > 0)
            for (int x = 0; x < circles.cols() ; x++) {


                double vCircle[] = circles.get(0,x);
//                double vCircle2[] = circles.get(0, x + 1);

                if (vCircle == null)
                    break;

                Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));

                radius = (int) Math.round(vCircle[2]);
//                secondRadius = (int) Math.round(vCircle2[2]);

                Log.d("FoundCircle", Integer.toString(radius));

//                double ratio = radius / secondRadius;
//                double smallestDiff = 1000;
//
//                for (int i = 0; i < RATIOS.length; i++) {
//                    if (RATIOS[i] - ratio < smallestDiff) {
//                        whichOne = Integer.toString(i);
//                        smallestDiff = RATIOS[i] - ratio;
//                    }
//                }

//                Log.d("PointyDick", whichOne);

                Log.i("PointyDick", "xy" + pt.toString());
                Log.i("PointyDick", "radius" + Integer.toString(radius));
                // draw the found circle
                Imgproc.circle(tempMat, pt, radius, new Scalar(255,0,0), 10); // circle
                Imgproc.circle(tempMat, pt, 3, new Scalar(255, 0, 0), 10);    // center of circle



            } // end of first for




        return tempMat;
    } // end of onCameraFrame

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Log.i("MainActivity", "Touched");

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Toast.makeText(this, whichOne, Toast.LENGTH_SHORT).show();
        } // end of switch

        return true;
    }
} // end of MainActivity

package njain.com.sparechange;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class OpenCVActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {

    private JavaCameraView mCameraView; // handler for the camera
    private OpenCVLoader mOpenCVLoader; // load opencv boi

    private Mat tempMat, circles; // holds images from opencv

    private static final double DIME_RADIUS = 41; // 39-41
    private static final double PENNY_RADIUS = 44; // 42-44
    private static final double NICKEL_RADIUS = 48; // 45-48
    private static final double QUARTER_RADIUS = 49; // 51-54

    private int radius;
    private double[] radii;

    private int iCannyUpperThreshold = 100;
    private int iMinRadius = 20;
    private int iMaxRadius = 400;
    private double iAccumulator = 140;

    private int quarter = 0;
    private int dime = 0;
    private int nick = 0;
    private int pennie = 0;

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
        setContentView(R.layout.activity_opencv);

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

        mCameraView.setMaxFrameSize(600, 600);
//        mCameraView.se

    } // end of onResumeH

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

////        Imgproc.threshold()
//        double otsuThreshold = Imgproc.threshold(tempMat, circles, 0, 255, Imgproc.THRESH_BINARY_INV);
//        double highThresh = Math.abs(otsuThreshold);
//        double lowThres = Math.abs(0.5 * otsuThreshold);
//
//        Log.i("Threshold", Double.toString(highThresh) + " " + Double.toString(lowThres));
//
////        Imgproc.HoughCircles();
//
//        Imgproc.HoughCircles(tempMat, circles, Imgproc.CV_HOUGH_GRADIENT,
//                1.66, 15, lowThres, highThresh,
//                iMinRadius, iMaxRadius);

        Imgproc.HoughCircles(tempMat, circles, Imgproc.CV_HOUGH_GRADIENT,
                2.0, 40, iCannyUpperThreshold, iAccumulator,
                iMinRadius, iMaxRadius);


        if (circles.cols() > 0) {

            // to store the radii
            radii = new double[circles.cols()];

            Log.i("How Many",Integer.toString(circles.cols()));

            for (int x = 0; x < circles.cols(); x++) {

                double vCircle[] = circles.get(0, x);
//                double vCircle2[] = circles.get(0, x + 1);

                if (vCircle == null)
                    break;

                Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));

                radius = (int) Math.round(vCircle[2]);
                radii[x] = radius;

                // draw the found circle
                Imgproc.circle(tempMat, pt, radius, new Scalar(255, 0, 0), 10); // circle
                Imgproc.circle(tempMat, pt, 3, new Scalar(255, 0, 0), 10);    // center of circle


            } // end of first for


        }

        return tempMat;
    } // end of onCameraFrame

    private void sendDataToServer(int quarters, int dimes, int nickels, int pennies) {

        String url = "http://5fa3c2ff.ngrok.io/change/" + Integer.toString(quarters)
               + "/" + Integer.toString(dimes) + "/"
                + Integer.toString(nickels) + "/" + Integer.toString(pennies);

        HttpResponse response = null;
        try {
            // Create http client object to send request to server
            HttpClient client = new DefaultHttpClient();
            // Create URL string
            // Create Request to server and get response
            HttpGet httpget= new HttpGet();
            httpget.setURI(new URI(url));
            response = client.execute(httpget);
//            response.
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Toast.makeText(OpenCVActivity.this, "Analyzing", Toast.LENGTH_SHORT).show();

        switch (event.getAction()) {

            case MotionEvent.ACTION_UP: {

                quarter = 0;
                dime = 0;
                nick = 0;
                pennie = 0;

                for (double radicalthot : radii) {

                    Log.i("Pesarvarmulthu",Double.toString(radicalthot));

                    if (radicalthot <= DIME_RADIUS) {
                        Log.i("Pesarvarmulthu", "dime");
                        dime++;
                    }
                    else if (radicalthot <= PENNY_RADIUS && radicalthot > DIME_RADIUS) {
                        Log.i("Pesarvarmulthu", "penny");
                        pennie++;
                    }
                    else if (radicalthot <= NICKEL_RADIUS && radicalthot > PENNY_RADIUS) {
                        Log.i("Pesarvarmulthu", "nickel");
                        nick++;
                    }
                    else if (radicalthot >= QUARTER_RADIUS) {
                        Log.i("Pesarvarmulthu", "quarter");
                        quarter++;
                    }

                    SendGet get = new SendGet();
                    get.execute();

                }




            } // end of case

        } // end of switch

        return true;
    }

    // Do not read under any sircumstances
    private class SendGet extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            sendDataToServer(quarter, dime, nick, pennie);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    } // end of SendGet

} // end of MainActivity

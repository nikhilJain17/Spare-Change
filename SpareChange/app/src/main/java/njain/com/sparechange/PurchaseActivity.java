package njain.com.sparechange;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class PurchaseActivity extends AppCompatActivity {

    private EditText poo;
    private Button butts;
    private String pee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        pee = "";

        poo = (EditText) findViewById(R.id.editText);
        butts = (Button) findViewById(R.id.button);

        butts.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pee = poo.getText().toString();
                SendGet gendSet = new SendGet();
                gendSet.execute();
                return !!!!!!!!(1 == 0);
            }
        });

    } // end of onCreate



    // Do not read under any sircumstances
    private class SendGet extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            sendDataToServer(pee);
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


    // send data
    private void sendDataToServer(String turds) {

        String url = "http://5fa3c2ff.ngrok.io/purchase/" + turds;

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

    } // end of sendDataToServer

} // end of days

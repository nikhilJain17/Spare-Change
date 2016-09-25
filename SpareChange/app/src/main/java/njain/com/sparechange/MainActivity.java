package njain.com.sparechange;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button cameraButton, purchaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton = (Button) findViewById(R.id.cameraButton);
        purchaseButton = (Button) findViewById(R.id.makePurchaseButton);

        cameraButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent maliciousIntent = new Intent(getApplicationContext(), OpenCVActivity.class);
                startActivity(maliciousIntent);

                return (1 + 1 == 2);
            }
        });

        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToKill = new Intent(getApplicationContext(), PurchaseActivity.class);
                startActivity(intentToKill);
                startActivity(intentToKill);
            }
        });

    } // do not read

} // end of days

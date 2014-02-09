package org.hairyhearts;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;
import com.baasbox.android.SaveMode;
import com.baasbox.android.json.JsonObject;

public class SuccessActivity extends Activity {
    private static final String TAG = "SuccessActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_success);

        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();

        String id = bundle.getString("msgID");

        RequestToken res = BaasDocument.fetch("message", id, new BaasHandler<BaasDocument>() {

            @Override
            public void handle(BaasResult<BaasDocument> res) {
                try {
                    BaasDocument doc = res.get();

                    String buffer;

                    buffer = "from: " + doc.getString("from");
                    buffer += "\nto: " + doc.getString("to");
                    buffer += "\nmsg: " + doc.getString("msg");

                    TextView tv = (TextView) findViewById(R.id.textView1);
                    tv.setText(buffer);
                } catch (BaasException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

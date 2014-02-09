package org.hairyhearts;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.SaveMode;
import com.baasbox.android.json.JsonObject;

public class SuccessActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_success);

        findViewById(R.id.sendBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String from = BaasUser.current().getName();
        String to = ((EditText) findViewById(R.id.to)).getText().toString();
        String msg = ((EditText) findViewById(R.id.message)).getText().toString();

        new SendMessage(from, to, msg).execute();
    }

    public class SendMessage extends AsyncTask<Void, Void, String> {
        private String from;
        private String to;
        private String msg;

        SendMessage(String from, String to, String msg) {
            this.from = from;
            this.to = to;
            this.msg = msg;
        }

        @Override
        protected String doInBackground(Void... params) {
            BaasDocument doc = new BaasDocument("message");
            doc.putString("from", from);
            doc.putString("to", to);
            doc.putString("msg", msg);
            doc.saveSync(SaveMode.IGNORE_VERSION);

            return doc.getId();
        }

        @Override
        protected void onPostExecute(String id) {
            BaasUser.withUserName(to).send(new JsonObject().putString("message", id), new BaasHandler<Void>() {
                @Override
                public void handle(BaasResult<Void> voidBaasResult) {
                    Toast.makeText(SuccessActivity.this, "Sent message", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}

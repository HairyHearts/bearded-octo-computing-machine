package org.hairyhearts;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMService extends IntentService {
    private static final String TAG = "GCMService";

    public GCMService() {
        super("GCMService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String id = extras.getString("message");

                BaasResult<BaasDocument> res = BaasDocument.fetchSync("message", id);

                BaasDocument doc;
                try {
                    doc = res.get();

                    Log.d(TAG, "from: " + doc.getString("from"));
                    Log.d(TAG, "to: " + doc.getString("to"));
                    Log.d(TAG, "msg: " + doc.getString("msg"));
                } catch (BaasException e) {
                    e.printStackTrace();
                }
            }
        }

        GCMReceiver.completeWakefulIntent(intent);
    }
}

package org.hairyhearts;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasException;
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

                    String from = doc.getString("from");
                    
                    Intent i = new Intent(this, SuccessActivity.class); //TODO aggiornare activity per decryptare
                    i.putExtra("msgID", id);
                    
                    PendingIntent pendingIntent =
                            PendingIntent.getActivity(
                            this,
                            0,
                            i,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        );                                                          
                    
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_notify_chat) //TODO aggiornare icona
                        .setContentTitle("New crypted message")
                        .setContentText(from)                    
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                    
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);                   
                    manager.notify(0, builder.build());                    
                    
                } catch (BaasException e) {
                    e.printStackTrace();
                }
            }
        }

        GCMReceiver.completeWakefulIntent(intent);
    }
}

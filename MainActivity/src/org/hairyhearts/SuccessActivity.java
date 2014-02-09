package org.hairyhearts;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;
import com.baasbox.android.SaveMode;
import com.baasbox.android.json.JsonObject;
import com.gracenote.mmid.MobileSDK.GNConfig;
import com.gracenote.mmid.MobileSDK.GNOperations;
import com.gracenote.mmid.MobileSDK.GNSearchResponse;
import com.gracenote.mmid.MobileSDK.GNSearchResult;
import com.gracenote.mmid.MobileSDK.GNSearchResultReady;

public class SuccessActivity extends Activity {
    private static final String TAG = "SuccessActivity";


	private GNConfig config;
	private TextView message;
	private Button fp_button;

	RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_success);


		config = GNConfig.init("7486464-12AF0CC1BCE8C9726F6ADC0F77D3AF6D",this.getApplicationContext());
		fp_button = (Button) findViewById(R.id.encodeButton);
		fp_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				RecognizeFromMic task = new RecognizeFromMic();
				task.doFingerprint();
				findViewById(R.id.progressBarEncodeButton).setVisibility(View.VISIBLE);

			}
		});

		queue = Volley.newRequestQueue(this);
		
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

                    TextView tv = (TextView) findViewById(R.id.messageTextView);
                    tv.setText(buffer);
                } catch (BaasException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    
    

	class RecognizeFromMic implements GNSearchResultReady {

		public void GNResultReady(GNSearchResult result) {
			//key_textview = (TextView) findViewById(R.id.key);
			message = (EditText) findViewById(R.id.messageTextView);

			if (result.isFingerprintSearchNoMatchStatus()) {
				//song_info.setText("no match");
			} else {
				GNSearchResponse response = result.getBestResponse();
				//song_info.setText(response.getTrackTitle() + " by " + response.getArtist());

				String trackTitle, trackArtist;
				try {
					trackTitle = URLEncoder.encode(response.getTrackTitle() , "utf-8");
					trackArtist = URLEncoder.encode(response.getArtist(), "utf-8");

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					trackTitle = "empty";
					trackArtist = "empty";
				}


				String url = "http://api.musixmatch.com/ws/1.1/track.search?" +
						"apikey=40b6339efb4fd3c2d3dfd5eb73854362" +
						"&q_track= "  +
						trackTitle   +
						"&q_artist=" + 
						trackArtist ;



				final String url_snippet = "http://api.musixmatch.com/ws/1.1/track.snippet.get?" +
						"apikey=40b6339efb4fd3c2d3dfd5eb73854362" +
						"&track_id= ";

				String snippet = "nulla";

				JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						//Log.i("HairyHearts", " Response " + response.toString());
						//song_info.setText("Response => "+response.toString());
						try {
							String trackid = response.getJSONObject("message").getJSONObject("body")
									.getJSONArray("track_list").getJSONObject(0).getJSONObject("track")
									.getString("track_id");
							//song_info.setText("Track ID => "+ trackid);


							JsonObjectRequest jsObjRequestSnippet = new JsonObjectRequest(Request.Method.GET, url_snippet + trackid, null, new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									// TODO Auto-generated method stub
									//Log.i("HairyHearts", " Response " + response.toString());
									//song_info.setText("Response => "+response.toString());
									String snippet;
									try {
										snippet = response.getJSONObject("message").getJSONObject("body")
												.getJSONObject("snippet")
												.getString("snippet_body");
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										snippet = "Nulla";
									}
									//key_textview.setText("Key => "+ snippet);

									String msgToDecode = message.getText().toString();
									String msg = "";
									try {
										Coding encoder = new Coding(snippet);
										msg = encoder.decrypt(msgToDecode);
										message.setText(msg);

									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										message.setText("Error");


									}

								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									// TODO Auto-generated method stub

								}
							});

							queue.add(jsObjRequestSnippet);


						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.i("HairyHearts", " Track Id " + "nulla");

						}

						findViewById(R.id.progressBarEncodeButton).setVisibility(View.GONE);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});



				queue.add(jsObjRequest);

			}
		}
		public void doFingerprint() {
			GNOperations.recognizeMIDStreamFromMic(this,config);

		}
	}

}

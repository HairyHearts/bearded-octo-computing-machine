package org.hairyhearts;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.SaveMode;
import com.baasbox.android.json.JsonObject;
import com.gracenote.mmid.MobileSDK.GNConfig;
import com.gracenote.mmid.MobileSDK.GNOperations;
import com.gracenote.mmid.MobileSDK.GNSearchResponse;
import com.gracenote.mmid.MobileSDK.GNSearchResult;
import com.gracenote.mmid.MobileSDK.GNSearchResultReady;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ComposeFragment extends Fragment {

	private ArrayList<String> userList = new ArrayList<String>();
	private SharedPreferences sharedPreferences;
	private View rootView;
	private TextView messageTextView;

	private MediaRecorder myRecorder;
	private MediaPlayer myPlayer;
	private String outputFile = null;
	private boolean recording = false;
	private ProgressBar progressBarMicrofono;
	private ImageButton micButton;
	private ImageButton playButton;
	private ImageButton stopButton;
	private ImageButton encodeButton;
	private TextView encodeTextView;
	private AutoCompleteTextView receiverTextView;
	private LinearLayout bodyMessage;
	private LinearLayout sendBodyMessage;
	private ImageButton sendbutton;
	private static TextView criptedKey;
	private static TextView keySongTextView;

	private ProgressBar progressBar;

	private GNConfig config;
	RequestQueue queue;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		config = GNConfig.init("7486464-12AF0CC1BCE8C9726F6ADC0F77D3AF6D",getActivity().getApplicationContext());
		queue = Volley.newRequestQueue(getActivity());


		rootView = inflater.inflate(R.layout.compose_fragment, container, false);
		receiverTextView = (AutoCompleteTextView) rootView.findViewById(R.id.receiver);

		Bundle args = getArguments();
		if (args == null) {
		} else {
			if (args.containsKey("USERNAME"))
				receiverTextView.setText(args.getString("USERNAME"));
		}

		messageTextView = (TextView) rootView.findViewById(R.id.messageTextView);
		bodyMessage = (LinearLayout) rootView.findViewById(R.id.bodyMessage);
		sendBodyMessage = (LinearLayout) rootView.findViewById(R.id.sendBodyMessage);
		sendbutton = (ImageButton) rootView.findViewById(R.id.SendMessageButton);
		sharedPreferences = getActivity().getSharedPreferences(ContactsFragment.class.getSimpleName(), Context.MODE_PRIVATE);
		userList = getUserList();

		criptedKey = (TextView)rootView.findViewById(R.id.criptedMessageTextView);
		keySongTextView	 = (TextView)rootView.findViewById(R.id.keySongTextView);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, userList);
		receiverTextView.setAdapter(adapter);
		setHasOptionsMenu(true);
		encodeTextView = (TextView) rootView.findViewById(R.id.encodeTextView);
		encodeButton = (ImageButton) rootView.findViewById(R.id.encodeButton);
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarEncodeButton);

		micButton = (ImageButton) rootView.findViewById(R.id.recButton);
		progressBarMicrofono = (ProgressBar) rootView.findViewById(R.id.progressBarMicButton);
		playButton = (ImageButton) rootView.findViewById(R.id.playButton);
		stopButton = (ImageButton) rootView.findViewById(R.id.StopButton);

		messageTextView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				int flag = View.INVISIBLE;
				if (s.length() > 0) {
					flag = View.VISIBLE;

				}
				encodeButton.setVisibility(flag);
				encodeTextView.setVisibility(flag);
			}
		});

		sendbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sendMessage();

			}
		});




		encodeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				RecognizeFromMic task = new RecognizeFromMic();
				task.doFingerprint();
				progressBar.setVisibility(View.VISIBLE);

			}
		});
		/*
		encodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("tag", "This'll run 6 seconds later");
				progressBar.setVisibility(View.VISIBLE);
				new android.os.Handler().postDelayed(new Runnable() {
					public void run() {
						Log.d("tag", "This'll run 6 seconds later");
						progressBar.setVisibility(View.INVISIBLE);



						if(true){
							String keyPassword = "lo strano percorso che ognuno di noi..bbla blabvla";
							String messaggioCriptata = "w3323nkwebd98ay3eookqhd9y871hbkjabdouasgdoijwdy3jjnqwdbjhvasd";
							ComposeFragment.setSendBodyMessage(keyPassword,messaggioCriptata);
							encodeButton.setVisibility(View.GONE);
							encodeTextView.setVisibility(View.GONE);
							sendbutton.setVisibility(View.VISIBLE);
						}else{
							sendbutton.setVisibility(View.GONE);
							encodeButton.setVisibility(View.VISIBLE);
							encodeTextView.setVisibility(View.VISIBLE);
							ComposeFragment.setSendBodyMessage("non found","");
							Toast.makeText(getActivity(), "Song not recoinaized, try again", Toast.LENGTH_LONG).show();
						}

						bodyMessage.setVisibility(View.GONE);
						sendBodyMessage.setVisibility(View.VISIBLE);
					}


				}, 6000);

			}
		}); */



		playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				play();
			}
		});
		stopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stop();
			}
		});
		micButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				start();
			}
		});

		outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/locksound.3gpp";
		myRecorder = new MediaRecorder();
		myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myRecorder.setOutputFile(outputFile);

		return rootView;
	}

	protected static void setSendBodyMessage(String keyPassword, String messaggioCriptata) {
		if(messaggioCriptata.length()>30)
			messaggioCriptata= messaggioCriptata.substring(0,25)+"(...)";
		if(keyPassword.length()>30)
			keyPassword= keyPassword.substring(0,25)+"(...)";
		keySongTextView.setText(keyPassword);
		criptedKey.setText(messaggioCriptata);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.compose, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	private ArrayList<String> getUserList() {
		int numberOfUsers = sharedPreferences.getInt(AppConstant._NUMBER_OF_USERS, 0);
		if (numberOfUsers > 0) {
			for (int i = 0; i < numberOfUsers; i++) {
				userList.add(sharedPreferences.getString(AppConstant._USERNAME_PREFIX + i, "empty"));
			}
		}
		return userList;
	}

	private void start() {

		if (!recording) {
			recording = true;
			playButton.setVisibility(View.GONE);
			stopButton.setVisibility(View.GONE);
			progressBarMicrofono.setVisibility(View.VISIBLE);
			try {
				myRecorder = new MediaRecorder();
				myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
				myRecorder.setOutputFile(outputFile);
				myRecorder.prepare();
				myRecorder.start();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Toast.makeText(getActivity(), "Start recording...", Toast.LENGTH_SHORT).show();
		} else {
			progressBarMicrofono.setVisibility(View.GONE);
			// micButton.setVisibility(View.GONE);
			encodeButton.setVisibility(View.VISIBLE);
			encodeTextView.setVisibility(View.VISIBLE);
			playButton.setVisibility(View.VISIBLE);
			stopButton.setVisibility(View.VISIBLE);
			recording = false;
			try {

				myRecorder.stop();
				myRecorder.release();
				myRecorder = null;
				Toast.makeText(getActivity(), "Stop recording...", Toast.LENGTH_SHORT).show();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}

	private void stop() {
		try {
			myPlayer.stop();
			myPlayer.release();
			myPlayer = null;
			Toast.makeText(getActivity(), "Stop ...", Toast.LENGTH_SHORT).show();
		} catch (IllegalStateException e) {
			// it is called before start()
			e.printStackTrace();
		} catch (RuntimeException e) {
			// no valid audio/video data has been received
			e.printStackTrace();
		}
	}

	private void play() {
		try {
			myPlayer = new MediaPlayer();
			myPlayer.setDataSource(outputFile);
			myPlayer.prepare();
			myPlayer.start();

			Toast.makeText(getActivity(), "Start play the recording...", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void actionSendText(Context context) {
		// enable
		((Activity) context).findViewById(R.id.messageTextView).setVisibility(View.VISIBLE);
		((Activity) context).findViewById(R.id.encodeButton).setVisibility(View.VISIBLE);
		((Activity) context).findViewById(R.id.encodeTextView).setVisibility(View.VISIBLE);

		// disable

	}

	public void actionSendAudio(Context context) {
		// enable

		// disable
		((Activity) context).findViewById(R.id.messageTextView).setVisibility(View.GONE);
		((Activity) context).findViewById(R.id.encodeButton).setVisibility(View.GONE);
		((Activity) context).findViewById(R.id.encodeTextView).setVisibility(View.GONE);
	}

	public void actionSendVideo(Context context) {
		// enable

		// disable
		((Activity) context).findViewById(R.id.messageTextView).setVisibility(View.GONE);

	}

	public void actionSendImage(Context context) {
		// enable

		// disable
		((Activity) context).findViewById(R.id.messageTextView).setVisibility(View.GONE);
	}


	class RecognizeFromMic implements GNSearchResultReady {

		public void GNResultReady(GNSearchResult result) {

			if (result.isFingerprintSearchNoMatchStatus()) {
				//song_info.setText("no match");
				progressBar.setVisibility(View.GONE);

			} else {
				GNSearchResponse response = result.getBestResponse();
				Log.d("RecognizeFromMic", "artist: " + response.getArtist());

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
						Log.i("HairyHearts", " Response " + response.toString());
						//song_info.setText("Response => "+response.toString());
						try {
							String trackid = response.getJSONObject("message").getJSONObject("body")
									.getJSONArray("track_list").getJSONObject(0).getJSONObject("track")
									.getString("track_id");
							//	song_info.setText("Track ID => "+ trackid);


							JsonObjectRequest jsObjRequestSnippet = new JsonObjectRequest(Request.Method.GET, url_snippet + trackid, null, 
									new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									// TODO Auto-generated method stub
									Log.i("HairyHearts", " Response " + response.toString());
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
										progressBar.setVisibility(View.GONE);

									}



									String msgToDecode = messageTextView.getText().toString();
									String msgEnc = "";
									try {
										Coding encoder = new Coding(snippet);
										msgEnc = encoder.encrypt(msgToDecode);
										ComposeFragment.setSendBodyMessage(snippet,msgEnc);

										encodeButton.setVisibility(View.GONE);
										encodeTextView.setVisibility(View.GONE);
										sendbutton.setVisibility(View.VISIBLE);
										progressBar.setVisibility(View.GONE);


										bodyMessage.setVisibility(View.GONE);
										sendBodyMessage.setVisibility(View.VISIBLE);
										Log.i("HHearts", " Decoded " + encoder.decrypt(msgEnc));

									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										criptedKey.setText("Error");
										progressBar.setVisibility(View.GONE);


									}

								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									// TODO Auto-generated method stub
									progressBar.setVisibility(View.GONE);
									Log.i("HHearts", " onErrorResponse");

								}
							});

							queue.add(jsObjRequestSnippet);


						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.i("HairyHearts", " Track Id " + "nulla");

						}

						//progressBar.setVisibility(View.GONE);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("HHearts", " onErrorResponse: " + error.getMessage());

					}
				});



				queue.add(jsObjRequest);

			}
		}
		public void doFingerprint() {
			GNOperations.recognizeMIDStreamFromMic(this,config);

		}
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
				public void handle(BaasResult<Void> res) {
					progressBar.setVisibility(View.GONE);
                    if ( res.isFailed() ){
                    	Log.e("ERROR","Error",res.error());
                    } else {
                    	Log.d("ERROR","OK");
                    }
					Toast.makeText(getActivity(), "Sent message", Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	private void sendMessage() {
		String from = BaasUser.current().getName();
		String to = receiverTextView.getText().toString();
		String msg = criptedKey.getText().toString();
		progressBar.setVisibility(View.VISIBLE);

		new SendMessage(from, to, msg).execute();
	}

}

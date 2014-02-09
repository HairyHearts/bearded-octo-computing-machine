package org.hairyhearts;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarEncodeButton);

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
		});
		
		

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

}

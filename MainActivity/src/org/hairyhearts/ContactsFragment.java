package org.hairyhearts;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsFragment extends Fragment {

	private static final String TAG = null;
	private ArrayList<String> userList = new ArrayList<String>();
	private View rootView;
	private SharedPreferences sharedPreferences;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_contacts, container, false);

		ListView l = (ListView) rootView.findViewById(R.id.listview);

		sharedPreferences = getActivity().getSharedPreferences(ContactsFragment.class.getSimpleName(), Context.MODE_PRIVATE);
		int numberOfUsers = sharedPreferences.getInt(AppConstant._NUMBER_OF_USERS, 0);

		userList = getUserList(numberOfUsers);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, userList);
		l.setAdapter(adapter);
		l.setOnItemClickListener(contactClick);
		setHasOptionsMenu(true);
		return rootView;
	}

	private ArrayList<String> getUserList(int numberOfUsers) {
		if (numberOfUsers > 0) {
			for (int i = 0; i < numberOfUsers; i++) {
				userList.add(sharedPreferences.getString(AppConstant._USERNAME_PREFIX  + i, "empty"));
			}
		}
		return userList;
	}

	private OnItemClickListener contactClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
			MainActivity thisActivity = (MainActivity) getActivity();
			thisActivity.openComposeWithContact(sharedPreferences.getString(AppConstant._USERNAME_PREFIX+paramInt, ""));
		}
	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d("Does", "get called");
		menu.clear();
		inflater.inflate(R.menu.contacts, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	
	public void addContactDialog(final Context context) {
final EditText input = new EditText(context);
		
		new AlertDialog.Builder(context)
	    .setTitle("Add new contact")
	    .setMessage("Insert username")
	    .setView(input)
	    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            Editable username = input.getText(); 
	            addContact(context,username.toString());
	        }
	    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            // Do nothing.
	        }
	    }).show();
		
	}
	
	public void addContact(Context context,String username) {
		
		
		sharedPreferences = context.getSharedPreferences(ContactsFragment.class.getSimpleName(), Context.MODE_PRIVATE);
		Editor sharedPreferencesEditor = sharedPreferences.edit();
		int numberOfUsers = sharedPreferences.getInt(AppConstant._NUMBER_OF_USERS, 0);
		Log.e(TAG, "numberOfUsers : "+numberOfUsers);
		userList = getUserList(numberOfUsers);
		
		
		sharedPreferencesEditor.putString(AppConstant._USERNAME_PREFIX + numberOfUsers, username );
		Log.e(TAG, "numberOfUsers : "+numberOfUsers);

		userList.add(username);
		numberOfUsers++;
		sharedPreferencesEditor.putInt(AppConstant._NUMBER_OF_USERS,  numberOfUsers);
		
		sharedPreferencesEditor.commit();

		ListView l = (ListView) ((Activity) context).findViewById(R.id.listview);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(((Activity) context), android.R.layout.simple_list_item_1, userList);
		l.setAdapter(adapter);

		Toast.makeText(context, "add new message", Toast.LENGTH_LONG).show();
		
	}

}

package com.example.android.navigationdrawerexample;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsFragment extends Fragment {


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_contacts, container,
				false);

		ListView l = (ListView) rootView.findViewById(R.id.listview);
		String[] values = new String[] { "Vincenzo", "Carles", "AlessandroM",
				"AlessandroF", "Giovanni" };
		ArrayAdapter<String> adapter =

		new ArrayAdapter<String>(getActivity(),	android.R.layout.simple_list_item_1, values);
		l.setAdapter(adapter);
		l.setOnItemClickListener(contactClick);
		setHasOptionsMenu(true);
		return rootView;
	}

	private OnItemClickListener contactClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> paramAdapterView,
				View paramView, int paramInt, long paramLong) {
			Toast.makeText(getActivity(), "go to send new message",	Toast.LENGTH_LONG).show();
			
		}
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.contacts, menu);
	    super.onCreateOptionsMenu(menu,inflater);
	}

}

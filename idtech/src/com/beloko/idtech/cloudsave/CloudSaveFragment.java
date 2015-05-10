package com.beloko.idtech.cloudsave;

import com.beloko.idtech.R;
import com.beloko.idtech.R.id;
import com.beloko.idtech.R.layout;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CloudSaveFragment extends Fragment{
	String LOG = "OnlineFragment";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.fragment_cloud_save, null);
		Button facebook = (Button)mainView.findViewById(R.id.save_button);
		facebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CloudSaveActivity.class);
				//EditText editText = (EditText) findViewById(R.id.edit_message);
				//String message = editText.getText().toString();
				//intent.putExtra(EXTRA_MESSAGE, message);
				startActivity(intent);
			}
		});

		Button youtube = (Button)mainView.findViewById(R.id.load_button);
		youtube.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		return mainView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {

		super.onHiddenChanged(hidden);
	}




}

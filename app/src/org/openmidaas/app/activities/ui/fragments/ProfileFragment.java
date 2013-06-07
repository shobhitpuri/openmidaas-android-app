package org.openmidaas.app.activities.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.openmidaas.app.R;
public class ProfileFragment extends Fragment {
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.profile_fragment, container, false);
		
		return view;
	}
}

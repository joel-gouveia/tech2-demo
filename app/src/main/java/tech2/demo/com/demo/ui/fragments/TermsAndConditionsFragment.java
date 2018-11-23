package tech2.demo.com.demo.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tech2.demo.com.demo.R;
import tech2.demo.com.demo.ui.activities.AdminActivity;
import tech2.demo.com.demo.ui.activities.MealsActivity;

public class TermsAndConditionsFragment extends Fragment {
    public TermsAndConditionsFragment() {
        // Required empty public constructor
    }

    public static TermsAndConditionsFragment newInstance() {
        return new TermsAndConditionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(MealsActivity.mFab != null){
            MealsActivity.mFab.setVisibility(View.GONE);
            MealsActivity.mToolbar.setTitle(getActivity().getResources().getString(R.string.fragment_terms_toolbar_title));
        } else {
            AdminActivity.mFab.setVisibility(View.GONE);
            AdminActivity.mToolbar.setTitle(getActivity().getResources().getString(R.string.fragment_terms_toolbar_title));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}

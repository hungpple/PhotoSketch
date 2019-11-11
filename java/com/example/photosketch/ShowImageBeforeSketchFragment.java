package com.example.photosketch;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ShowImageBeforeSketchFragment extends Fragment {
    private Button nextButton;
    private Button sketchButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_show_image_before_sketch, container, false);
        initializeUI(view);
        ((ShowImageActivity)getActivity()).setViewPager(0);
        return view;
    }

    private void initializeUI(final View view ) {
        sketchButton = view.findViewById(R.id.sketchButton);

        setSketchButton(sketchButton);
    }

    public void setSketchButton(Button button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                ((ShowImageActivity)getActivity()).setViewPager(1);

            }
        });
    }
}

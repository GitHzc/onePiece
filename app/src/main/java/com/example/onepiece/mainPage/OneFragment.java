package com.example.onepiece.mainPage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.example.onepiece.R;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public class OneFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.one_fragment, container, false);
        final ToggleButton toggleButton = view.findViewById(R.id.love_button);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleButton.setBackgroundResource(isChecked? R.drawable.love_red : R.drawable.love_gray);
            }
        });
        return view;
    }
}

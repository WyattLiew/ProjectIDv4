package com.example.weijunn.project01;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class tab1_main extends Fragment implements View.OnClickListener{

    private CardView pendingCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab1_main, container, false);


        pendingCard =(CardView)rootView.findViewById(R.id.pendingCard);
        pendingCard.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.pendingCard:
                intent = new Intent(getActivity(), projectEditor.class);
                startActivity(intent);
                break;
                default:
                    break;
        }

    }
}

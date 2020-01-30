package com.ardeapps.floorballmanager.tacticBoard;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.utils.Logger;

import java.io.File;
import java.util.ArrayList;

public class TacticGalleryDialogFragment extends DialogFragment {

    GridView galleryGrid;
    Button cancelButton;
    GalleryGridAdapter adapter;

    private ArrayList<GalleryItem> items = new ArrayList<>();

    public void update() {
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new GalleryGridAdapter(AppRes.getActivity());
        adapter.setListener(new GalleryGridAdapter.Listener() {
            @Override
            public void onShare() {
                Logger.toast("ON SHARE");
            }

            @Override
            public void onSelect() {
                Logger.toast("ON SELECT");
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_tactic_gallery, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        galleryGrid = v.findViewById(R.id.galleryGrid);
        cancelButton = v.findViewById(R.id.cancelButton);

        galleryGrid.setAdapter(adapter);


        Logger.log("EXTERNAL DIR: " + StorageHelper.isExternalStorageWritable());
        int idx = 1;
        if(StorageHelper.isExternalStorageWritable()) {
            for(File file : StorageHelper.getExternalFiles()) {
                Logger.log(file.getName());
                items.add(new GalleryItem("VIDEO NRO " + ++idx, file));
            }
        } else {
            for(File file : StorageHelper.getInternalFiles()) {
                Logger.log(file.getName());
                items.add(new GalleryItem("VIDEO NRO " + ++idx, file));
            }
        }

        update();

        cancelButton.setOnClickListener(v1 -> {
            dismiss();
        });

        return v;
    }

    // This sets dialog full screen
    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setLayout(width, height);
        }
    }
}

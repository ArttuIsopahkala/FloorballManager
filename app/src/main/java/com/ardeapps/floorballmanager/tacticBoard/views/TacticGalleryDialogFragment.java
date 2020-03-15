package com.ardeapps.floorballmanager.tacticBoard.views;

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
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.tacticBoard.media.GalleryGridAdapter;
import com.ardeapps.floorballmanager.tacticBoard.objects.GalleryItem;
import com.ardeapps.floorballmanager.tacticBoard.utils.StorageHelper;
import com.ardeapps.floorballmanager.utils.Logger;

import java.io.File;
import java.util.ArrayList;

public class TacticGalleryDialogFragment extends DialogFragment {

    GridView galleryGrid;
    Button cancelButton;
    GalleryGridAdapter adapter;
    TextView noSavingsText;

    public void update() {
        ArrayList<GalleryItem> items = new ArrayList<>();
        for(File file : StorageHelper.getStorageFiles()) {
            Logger.log(file.getName());
            items.add(new GalleryItem(file.getName(), file));
        }

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

            @Override
            public void onDelete() {
                Logger.toast("ON DELETE");
                update();
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
        noSavingsText = v.findViewById(R.id.noSavingsText);

        galleryGrid.setAdapter(adapter);
        galleryGrid.setEmptyView(noSavingsText);

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

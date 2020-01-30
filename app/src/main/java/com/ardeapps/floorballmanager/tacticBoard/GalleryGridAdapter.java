package com.ardeapps.floorballmanager.tacticBoard;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.views.IconView;

import java.io.File;
import java.util.ArrayList;

public class GalleryGridAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public Listener mListener = null;
    private ArrayList<GalleryItem> items = new ArrayList<>();

    public GalleryGridAdapter(Context ctx) { // Activity
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    public void setItems(ArrayList<GalleryItem> teams) {
        this.items = teams;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        final Holder holder = new Holder();
        if (v == null) {
            v = inflater.inflate(R.layout.grid_item_gallery, null);
        }

        holder.selectIcon = v.findViewById(R.id.selectIcon);
        holder.shareIcon = v.findViewById(R.id.shareIcon);
        holder.removeIcon = v.findViewById(R.id.removeIcon);
        holder.videoView = v.findViewById(R.id.previewImage);

        final GalleryItem item = items.get(position);

        String videoFilePath = item.file.getAbsolutePath();
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoFilePath, MediaStore.Images.Thumbnails.MINI_KIND);
        holder.videoView.setImageBitmap(thumbnail);

        holder.shareIcon.setOnClickListener(v1 -> mListener.onShare());
        holder.selectIcon.setOnClickListener(v1 -> mListener.onSelect());
        holder.removeIcon.setOnClickListener(v1 -> {
            File file = new File(videoFilePath);
            file.delete();
        });
        //holder.nameText.setText(team.getName());

        return v;
    }

    public interface Listener {
        void onShare();
        void onSelect();
    }

    public class Holder {
        TextView nameText;
        ImageView videoView;
        IconView removeIcon;
        IconView shareIcon;
        IconView selectIcon;
    }

}

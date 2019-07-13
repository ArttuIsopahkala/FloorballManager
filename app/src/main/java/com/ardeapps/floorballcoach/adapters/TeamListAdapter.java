package com.ardeapps.floorballcoach.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.objects.Team;
import com.ardeapps.floorballcoach.utils.ImageUtil;
import com.ardeapps.floorballcoach.views.IconView;

import java.util.ArrayList;

public class TeamListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public Listener mListener = null;
    private ArrayList<Team> teams = new ArrayList<>();

    private Context ctx;
    private boolean adapterFirstTime;


    public TeamListAdapter(Context ctx) { // Activity
        this.ctx = ctx;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListener(Listener l) {
        mListener = l;
    }


    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    @Override
    public int getCount() {
        return teams.size();
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
            v = inflater.inflate(R.layout.list_item_team, null);
            adapterFirstTime = true;
        }

        holder.logoImage = v.findViewById(R.id.logoImage);
        holder.nameText = v.findViewById(R.id.nameText);
        holder.teamContainer = v.findViewById(R.id.teamContainer);
        holder.editIcon = v.findViewById(R.id.editIcon);

        final Team team = teams.get(position);

        if(team.getLogo() != null) {
            holder.logoImage.setImageBitmap(ImageUtil.getSquarePicture(team.getLogo()));
        }

        holder.nameText.setText(team.getName());

        holder.teamContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTeamSelected(team);
            }
        });

        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEditTeam(team);
            }
        });

        adapterFirstTime = false;
        return v;
    }

    public interface Listener {
        void onTeamSelected(Team team);
        void onEditTeam(Team team);
    }

    public class Holder {
        ImageView logoImage;
        TextView nameText;
        RelativeLayout teamContainer;
        IconView editIcon;
    }
}

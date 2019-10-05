package com.ardeapps.floorballmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.objects.Team;
import com.ardeapps.floorballmanager.objects.UserRequest;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.views.IconView;

import java.util.ArrayList;
import java.util.List;

public class TeamListAdapter extends BaseAdapter {

    public enum Type {
        SELECT,
        JOIN
    }

    private static LayoutInflater inflater = null;
    public Listener mListener = null;
    private ArrayList<Team> teams = new ArrayList<>();
    private Type type;

    public TeamListAdapter(Context ctx, Type type) { // Activity
        this.type = type;
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
        }

        holder.selectIcon = v.findViewById(R.id.selectIcon);
        holder.joinButton = v.findViewById(R.id.joinButton);
        holder.logoImage = v.findViewById(R.id.logoImage);
        holder.nameText = v.findViewById(R.id.nameText);
        holder.teamContainer = v.findViewById(R.id.teamContainer);

        final Team team = teams.get(position);
        if (team.getLogo() != null) {
            holder.logoImage.setImageBitmap(ImageUtil.getSquarePicture(team.getLogo()));
        } else {
            holder.logoImage.setImageResource(R.drawable.default_logo);
        }

        holder.nameText.setText(team.getName());

        if(type == Type.SELECT) {
            holder.selectIcon.setVisibility(View.VISIBLE);
            holder.joinButton.setVisibility(View.GONE);
            holder.teamContainer.setOnClickListener(v1 -> mListener.onTeamSelected(team));
        } else {
            holder.selectIcon.setVisibility(View.GONE);

            boolean showJoinButton = true;
            // Show if user is not joined or sent request to team
            List<String> userJoinedTeams = AppRes.getInstance().getUser().getTeamIds();
            if (userJoinedTeams.contains(team.getTeamId())) {
                showJoinButton = false;
            }
            for(UserRequest userRequest : AppRes.getInstance().getUserRequests().values()) {
                if(userRequest.getTeamId().equals(team.getTeamId())) {
                    showJoinButton = false;
                    break;
                }
            }
            if(showJoinButton) {
                holder.joinButton.setVisibility(View.VISIBLE);
                holder.joinButton.setOnClickListener(v2 -> mListener.onTeamSelected(team));
            } else {
                holder.joinButton.setVisibility(View.GONE);
            }
        }

        return v;
    }

    public interface Listener {
        void onTeamSelected(Team team);
    }

    public class Holder {
        ImageView logoImage;
        TextView nameText;
        RelativeLayout teamContainer;
        IconView selectIcon;
        Button joinButton;
    }

}

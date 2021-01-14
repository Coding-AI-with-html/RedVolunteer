package com.redvolunteer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.redvolunteer.R;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.User;
import com.redvolunteer.viewmodels.HelpRequestViewModel;

import java.util.List;

public class UserRequestHelpWallAdapter extends BaseAdapter {


    /**
     * Showed Request
     * @return
     */
    private List<RequestHelp> requestHelpList;

    /**
     * System context
     * @return
     */
    private Context mContext;

    /**
     * Current Logged User
     * @return
     */
    private User mCurrentUser;


    public UserRequestHelpWallAdapter(List<RequestHelp> requestHelpList, Context mContext, User mCurrentUser) {
        this.requestHelpList = requestHelpList;
        this.mContext = mContext;
        this.mCurrentUser = mCurrentUser;
    }

    public void setRequests(List<RequestHelp> requests){
        if(!this.requestHelpList.equals(requests))
            this.requestHelpList = requests;
    }

    @Override
    public int getCount() {
        return requestHelpList.size();
    }

    @Override
    public Object getItem(int position) {
        if(position >= requestHelpList.size())
            throw new IllegalArgumentException(position + "exceeds " + requestHelpList.size());
        return requestHelpList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View Conview, ViewGroup parent) {


        if(Conview == null){
            Conview = LayoutInflater.from(mContext).inflate(R.layout.myrequest_list_item,parent, false);
        }

        RequestHelpViewHolder holder = (RequestHelpViewHolder) Conview.getTag();
        if(holder == null){
            holder = new RequestHelpViewHolder(Conview);
            Conview.setTag(holder);
        }

        RequestHelp reqHelp = requestHelpList.get(position);


        holder.title.setText(reqHelp.getName());
        holder.location.setText(reqHelp.getRequestLocation().getName());

        return Conview;
    }


    private static class RequestHelpViewHolder {

        private TextView title;
        private TextView location;


        RequestHelpViewHolder(View row) {
            this.title = (TextView) row.findViewById(R.id.myrequest_title);
            this.location = (TextView) row.findViewById(R.id.myrequest_location);
        }
    }
}

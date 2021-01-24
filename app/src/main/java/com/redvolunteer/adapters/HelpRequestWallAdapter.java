package com.redvolunteer.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.redvolunteer.R;
import com.redvolunteer.UserDetailsActivity;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.utils.imagemarshalling.ImageBase64Marshaller;
import com.redvolunteer.utils.persistence.ExtraLabels;

import java.util.List;

/**
* This Class is responsible to populate HelpRequest in the selected listView
 */
public class HelpRequestWallAdapter extends BaseAdapter {
    private static final String TAG = "HelpRequestWallAdapter";

    private List<RequestHelp> helpList;
    private Context context;

    public HelpRequestWallAdapter(List<RequestHelp> helpList, Context context) {
        this.helpList = helpList;
        this.context = context;
    }

    public void setHelpList(List<RequestHelp> requestHelps){
        if(!this.helpList.equals(requestHelps)){
            this.helpList = requestHelps;
        }
    }

    @Override
    public int getCount() {
        return helpList.size();
    }

    @Override
    public RequestHelp getItem(int position) {

        if(position >= helpList.size())
            throw  new IllegalArgumentException(position + "exceeds" + helpList.size());
        return helpList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertedview, ViewGroup parent) {


        if(convertedview == null){
            convertedview = LayoutInflater.from(context).inflate(R.layout.mainview_help_request_list, parent, false);
        }
        HelpRequestViewHolder holder = (HelpRequestViewHolder) convertedview.getTag();
        if(holder == null){
            holder = new HelpRequestViewHolder(convertedview);
            convertedview.setTag(holder);
        }

        final RequestHelp reqHelp = helpList.get(position);
        holder.mRequestTitle.setText(reqHelp.getName());
        holder.mReqLocation.setText(reqHelp.getRequestLocation().getName());
        holder.mUserImage.setImageBitmap(ImageBase64Marshaller.decode64BitmapString(reqHelp.getHelpRequestCreator().getPhoto()));
        holder.mRequestor.setText(reqHelp.getHelpRequestCreator().getName());

        View.OnClickListener showUserClicked = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showUserDetails = new Intent(context, UserDetailsActivity.class);
                showUserDetails.putExtra(ExtraLabels.USER_ID, reqHelp.getHelpRequestCreatorID());

                context.startActivity(showUserDetails);
            }
        };

        holder.mUserImage.setOnClickListener(showUserClicked);
        holder.mRequestor.setOnClickListener(showUserClicked);

        return convertedview;
    }


    private static class HelpRequestViewHolder {
        private ImageView mUserImage;
        private TextView mRequestTitle;
        private TextView mReqLocation;
        private TextView mRequestor;

        public HelpRequestViewHolder(View row) {


            this.mUserImage = (CircularImageView) row.findViewById(R.id.request_user_image);
            this.mRequestTitle = (TextView) row.findViewById(R.id.request_title);
            this.mReqLocation = (TextView) row.findViewById(R.id.request_location);
            this.mRequestor = (TextView) row.findViewById(R.id.request_creator);
        }
    }



}

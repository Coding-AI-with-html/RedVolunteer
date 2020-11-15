package com.redvolunteer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.redvolunteer.R;
import com.redvolunteer.pojo.RequestHelp;

import java.util.List;

/**
* This Class is responsible to populate HelpRequest in the selected listView
 */
public class HelpRequestWallAdapter extends BaseAdapter {

    private List<RequestHelp> helpList;
    private Context context;

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
    public Object getItem(int position) {

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

        if(convertedview ==null){
            convertedview = LayoutInflater.from(parent.getContext()).inflate(R.layout.mainview_help_request_list, parent, false);
        }


























        return null;
    }








}

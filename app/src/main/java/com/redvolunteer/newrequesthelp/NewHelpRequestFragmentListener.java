package com.redvolunteer.newrequesthelp;

import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;
import com.redvolunteer.pojo.User;

public interface NewHelpRequestFragmentListener {

    void secondFragment(RequestHelp HelpRequest);

    RequestHelp getHelpRequest();

    void finish(RequestHelp newHelpRequest);

    User getHelpRequestCreator();

    RequestLocation getHelpRequestCreatorLocation();

}

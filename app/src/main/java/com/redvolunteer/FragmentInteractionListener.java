package com.redvolunteer;

import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.MessageViewModel;
import com.redvolunteer.viewmodels.UserViewModel;

public interface FragmentInteractionListener {

    /**
     *Simple User ViewModel getter
     */

    UserViewModel getUserViewModel();

    HelpRequestViewModel getHelpRequestViewModel();

    MessageViewModel getMessageViewModel();


}

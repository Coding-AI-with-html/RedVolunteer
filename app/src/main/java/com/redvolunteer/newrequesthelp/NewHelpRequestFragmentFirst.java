package com.redvolunteer.newrequesthelp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.redvolunteer.R;
import com.redvolunteer.pojo.RequestHelp;

public class NewHelpRequestFragmentFirst extends Fragment {

    /**
     * Activity Reference
     */
    private NewHelpRequestFragmentListener mListener;


    /**
     * Layout
     */
    private EditText mHelpRequestName;
    private EditText mHelpRequestDescription;


    /**
     * To check if all fields are filled
     */
    private boolean nameSelected = false;

    public NewHelpRequestFragmentFirst(){
        //Required an empty constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        super.onViewCreated(view, savedInstanceState);

    }

    /**
     * Binds UI view to fields
     */

    private void bind(final View view){
        ImageView mbackButton = (ImageView) view.findViewById(R.id.new_request_cancel_button);
        mHelpRequestName = (EditText) view.findViewById(R.id.new_request_name);
        mHelpRequestDescription = (EditText) view.findViewById(R.id.new_request_description);
        Button moveForward = (Button) view.findViewById(R.id.new_request_button_move_forward);

        mbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        mHelpRequestName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nameSelected = mHelpRequestName.getText().length() > 0;
            }
        });

        moveForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isFormFilled() && isDescWrited()) {
                    RequestHelp requestHelpNext = new RequestHelp();
                    requestHelpNext.setName(mHelpRequestName.getText().toString());
                    requestHelpNext.setDescription(mHelpRequestDescription.getText().toString());

                    mListener.secondFragment(requestHelpNext);
                } else {

                    showRetrieveErrorPopupDialog();
                }
            }
        });
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    //inflate layout for this fragment
    return inflater.inflate(R.layout.fragment_new_help_request_page_one, container, false);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof NewHelpRequestFragmentListener){
            mListener = (NewHelpRequestFragmentListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NewHelPRequestFragment");
        }
}

    /**
     * Returns true if the form is filled, false otherwise
     */
    private boolean isFormFilled() {
        boolean isFilled = false;
        if (nameSelected) {
            isFilled = true;
        }
        return isFilled;
    }
    private boolean isDescWrited(){
        return mHelpRequestDescription.getText().length() >0;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * It shows an error popup
     */
    private void showRetrieveErrorPopupDialog() {

        // there was an error, show a popup message
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.form_not_completely_filled_popup)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, null);
        AlertDialog alert = builder.create();
        alert.show();
    }

}

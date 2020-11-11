package com.redvolunteer.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.redvolunteer.FragmentInteractionListener;
import com.redvolunteer.R;
import com.redvolunteer.ViewModels.UserViewModel;
import com.redvolunteer.pojo.User;

import java.util.Calendar;

import Utils.NetworkCheker;

public class ProfileFragment extends Fragment {


    /**
     * Constants for #onAcitivityResult
     * @param view
     * @param savedInstanceState
     */

    private static final int REQUES_IMAGE_CAPTURE = 200;
    private static final int RESULT_OK = -1;

    /**
     * Reference to the activity
     * @param view
     * @param savedInstanceState
     */
    private FragmentInteractionListener mFragListener;

    /**
     * User View Model
     * @param view
     * @param savedInstanceState
     */
    UserViewModel mUserViewModel;

    /**
     * User showed in this layout
     * @param view
     * @param savedInstanceState
     */
    private User mShowedUSer;

    /**
     * Layout components
     */
    private ImageView mUserPic;
    private TextView mUserName;
    private TextView mUserSurname;
    private EditText userBio;
    private ImageView mEditButton;
    private  ImageView mEditBirthDate;
    private LinearLayout mActionModifyButton;
    private ImageView mEditPhotoIndicator;

    /**
     * Backup for old data
     */

    private String tmpOldPicture;
    private long tmpOldBirthDate;
    private String tpmOldBiogaraphy;

    public ProfileFragment() {
        //required to make empty constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * it bind UI layout to properties
     */
    private void bind(View view) {

        setupTolbar(view);

        mUserPic = (ImageView) view.findViewById(R.id.profile_user_pic);
        mUserName = (TextView) view.findViewById(R.id.user_name);
        mUserSurname = (TextView) view.findViewById(R.id.user_nameSurname);
        userBio = (EditText) view.findViewById(R.id.user_bio);
        mEditButton = (ImageView) view.findViewById(R.id.edit_profile_button);
        mEditBirthDate = (ImageView) view.findViewById(R.id.modify_birthdate_btn);
        mEditPhotoIndicator = (ImageView) view.findViewById(R.id.image_modify_indicator);

        mActionModifyButton = (LinearLayout) view.findViewById(R.id.profile_modify_actions);
        Button mAcceptModify = (Button) view.findViewById(R.id.profile_accept_modification_btn);
        Button mDeclineModify = (Button) view.findViewById(R.id.profile_discard_modification_btn);

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userBio.setEnabled(true);

                mEditBirthDate.setVisibility(View.VISIBLE);
                mActionModifyButton.setVisibility(View.VISIBLE);
                mEditButton.setVisibility(View.VISIBLE);
                mEditPhotoIndicator.setVisibility(View.VISIBLE);


                //copy the old info to perform rollback
                tpmOldBiogaraphy = mShowedUSer.getBiography();
                tmpOldBirthDate = mShowedUSer.getBirthDate();
                tmpOldPicture = mShowedUSer.getPhoto();

                mUserPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if(takePictureIntent.resolveActivity(getContext().getPackageManager()) != null){
                            startActivityForResult(takePictureIntent, REQUES_IMAGE_CAPTURE);
                        }

                    }
                });

            }
        });

        mDeclineModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resetInvisibleMoficationComponents();

                mShowedUSer.setBiography(tpmOldBiogaraphy);
                mShowedUSer.setPhoto(tmpOldPicture);
                mShowedUSer.setBirthDate(tmpOldBirthDate);

                fillFragments();


            }
        });

        mEditBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //underaged user are not allowed
                final long HEIGHTEEN_YEAR_AGO = 568025136000L;

                Calendar cal = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH,month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        mShowedUSer.setBirthDate(cal.getTime().getTime());

                        fillFragments();
                    }
                },   cal
                .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - HEIGHTEEN_YEAR_AGO);
                datePickerDialog.show();



            }
        });

        mAcceptModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(NetworkCheker.getInstance().isNetworkAvailable(getContext())) {
                    resetInvisibleModificationComponents();

                    if (userBio.getText().toString().length() > 0) {


                        mShowedUSer.setBiography(userBio.getText().toString());
                        mUserViewModel.UpdateUser(mShowedUSer);
                    } else {
                        Toast.makeText(getContext(), "You forgot your biography", Toast.LENGTH_LONG).show();
                    }
                }
                    else {
                        Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
                    }

            }
        });

    }

    /**
    it modifys the layout
     */

    private void resetInvisibleModificationComponents(){

        mEditBirthDate.setVisibility(View.GONE);
        mActionModifyButton.setVisibility(View.GONE);
        mEditButton.setVisibility(View.VISIBLE);
        mEditPhotoIndicator.setVisibility(View.GONE);
        userBio.setEnabled(false);

    }






}


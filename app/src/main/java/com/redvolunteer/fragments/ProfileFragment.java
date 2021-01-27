package com.redvolunteer.fragments;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.redvolunteer.FragmentInteractionListener;
import com.redvolunteer.R;
import com.redvolunteer.utils.calendar.CalendarFormatter;
import com.redvolunteer.utils.imagemarshalling.ImageBase64Marshaller;
import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.UserViewModel;
import com.redvolunteer.pojo.User;

import java.util.Calendar;
import java.util.HashMap;

import com.redvolunteer.LoginAndRegister.Login;
import com.redvolunteer.utils.NetworkCheker;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    /**
     * Constants for #onAcitivityResult
     * @param view
     * @param savedInstanceState
     */

    private static final int REQUEST_IMAGE_CAPTURE = 200;

    //private static final int RESULT_OK = -1;

    private static final int IMAGE_REQUEST = 1;
    private Uri imgUrl;
    private StorageTask uploadTask;
    StorageReference storageReference;
    DatabaseReference DataReference;
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
    private UserViewModel mUserViewModel;


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
    private TextView mBirthDate;
    private EditText userBio;
    private ImageView mEditButton;
    private  ImageView mEditBirthDate;
    private LinearLayout mActionModifyButton;
    private ImageView mEditPhotoIndicator;
    private HelpRequestViewModel mHelpRequestViewModel;

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
        bind(view);
        fillFragments();
        super.onViewCreated(view, savedInstanceState);

    }

    /**
     * it bind UI layout to properties
     */
    private void bind(View view) {

        setupTolbar(view);


        mUserPic = (ImageView) view.findViewById(R.id.profile_user_pic);
        mUserName = (TextView) view.findViewById(R.id.user_name);
        mBirthDate = (TextView) view.findViewById(R.id.birth_date);
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
                tmpOldBirthDate = mShowedUSer.getBirthDay();
                tmpOldPicture = mShowedUSer.getPhoto();

                mUserPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, IMAGE_REQUEST);

                    }
                });

            }
        });

        mDeclineModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resetInvisibleModificationComponents();

                mShowedUSer.setBiography(tpmOldBiogaraphy);
                mShowedUSer.setPhoto(tmpOldPicture);
                mShowedUSer.setBirthDay(tmpOldBirthDate);

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

                        mShowedUSer.setBirthDay(cal.getTime().getTime());

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
                        Toast.makeText(getContext(), getString(R.string.forgot_biography), Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.no_internet_popup_label), Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    /**
     it modify's the layout
     */

    private void resetInvisibleModificationComponents(){

        mEditBirthDate.setVisibility(View.GONE);
        mActionModifyButton.setVisibility(View.GONE);
        mEditButton.setVisibility(View.VISIBLE);
        mEditPhotoIndicator.setVisibility(View.GONE);
        userBio.setEnabled(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mUserViewModel = mFragListener.getUserViewModel();
        //retrieve the user from the local store
        mShowedUSer = mUserViewModel.retrieveCachedUser();
        this.mHelpRequestViewModel = mFragListener.getHelpRequestViewModel();

    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    /**
     * loads UI with selected user
     */

    private void fillFragments(){
        if (mShowedUSer != null) {

            mUserName.setText(mShowedUSer.getName());

            // if the user has not specified his birth date
            if (mShowedUSer.getBirthDay() == 0) {
                mBirthDate.setText(R.string.alert_no_age);
            } else {
                mBirthDate.setText(CalendarFormatter.getDate(mShowedUSer.getBirthDay()));
            }

            userBio.setText(mShowedUSer.getBiography());
        }



    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        DataReference = FirebaseDatabase.getInstance().getReference("Help_Seekers").child(mShowedUSer.getId());

        DataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User usr = snapshot.getValue(User.class);
                if(usr.getPhoto().equals("default_photo")){
                    mUserPic.setImageResource(R.drawable.ic_default_profile);

                } else {
                    Glide.with(getContext()).load(usr.getPhoto()).into(mUserPic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentInteractionListener){
            mFragListener = (FragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    +" must implement FragInteractionListener");

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragListener = null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void setupTolbar(View layout){

        layout.findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserViewModel.signOut();
                startActivity( new Intent(getContext(), Login.class));
                getActivity().finish();
            }
        });


    }
    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);

    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
    private void uploadImage(){
        final ProgressDialog Pg = new ProgressDialog(getContext());
        Pg.setMessage("Keliama");
        Pg.show();
        if(imgUrl != null){
            final StorageReference fileRef = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imgUrl));

            uploadTask = fileRef.putFile(imgUrl);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){

                        Uri downlaodUrl = task.getResult();
                        String mUri = downlaodUrl.toString();


                        DataReference = FirebaseDatabase.getInstance().getReference("Help_Seekers").child(mShowedUSer.getId());

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("photo", mUri);
                        DataReference.updateChildren(map);

                        Pg.dismiss();



                    } else {
                        Toast.makeText(getContext(), "Klaida, prasome meginti veliau", Toast.LENGTH_SHORT).show();
                        Pg.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Pg.dismiss();
                }
            });

        } else {
            Toast.makeText(getContext(), "Nepasirinka nuotraukos!", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //result of image capture


        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imgUrl = data.getData();

            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Kelimas progrese", Toast.LENGTH_SHORT).show();

            } else {
                uploadImage();
                mShowedUSer.setPhoto(imgUrl.toString());
            }
        }

    }

}


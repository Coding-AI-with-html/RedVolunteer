package com.redvolunteer.LoginAndRegister;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.MainActivity;
import com.redvolunteer.R;
import com.redvolunteer.RedVolunteerApplication;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.utils.ValidateUtils;
import com.redvolunteer.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

public class RegisterX extends AppCompatActivity {

    private static final String TAG = "RegisterX";

    /**
     * Facebook register
     */
    private Button fbRegister;

    /**
     * Handle callback
     */
    private CallbackManager fbCallBackManager;

    /**
     * Firebase authentication manager
     */
    private FirebaseAuth mAuth;
    /**
     * Handle to register with WM
     */
    private UserViewModel mUserViewModel;
    //firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase Fdata;
    private DatabaseReference DataRefs;
    private FirebaseAuth.AuthStateListener mAuthListener;

    /**
     * Checking inputs
     */
    ValidateUtils inputChecker;

    /**
     * layout
     */
    private Button register;
    private Button login;
    private Button addVolunter;
    private EditText mName;
    private EditText mSurname;
    private Spinner mGender;
    private EditText mEmail;
    private EditText mBirthday;
    private EditText mPassword;
    private EditText phoneNumber;
    private ProgressBar progBar;

    /**
     * DatePicker for birthday
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    /**
     * it simple is the popup spinner
     */
    private ProgressDialog popupProgDialog;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!NetworkCheker.getInstance().isNetworkAvailable(this)){
            Toast.makeText(RegisterX.this, RegisterX.this.getString(R.string.no_internet_popup_label), Toast.LENGTH_LONG).show();
        }
        this.mUserViewModel = ((RedVolunteerApplication) getApplicationContext()).getUserViewModel();

        setupFacebookRegister();
        setupFirebaseAuth();

    setContentView(R.layout.register_helpseeker);


        bindLayoutComponents();
    this.bindFacebookButton();


    }

    /**
     * Binding layout components
     */
    private void bindLayoutComponents(){

        mName = (EditText) findViewById(R.id.Name_register);
        mSurname = (EditText) findViewById(R.id.Surname_register);
        mEmail = (EditText) findViewById(R.id.Email_register);
        mBirthday = (EditText) findViewById(R.id.RegisterX_birth_date);
        mGender = (Spinner) findViewById(R.id.registerX_gender);
        mPassword = (EditText) findViewById(R.id.registerX_password);
        phoneNumber =(EditText) findViewById(R.id.registerX_phone);
        register = (Button) findViewById(R.id.register_helpseeker);
        login = (Button) findViewById(R.id.go_to_login);
        addVolunter = (Button) findViewById(R.id.register_volunteer);
        progBar = (ProgressBar) findViewById(R.id.helpseeker_register_progressbar);

        final Spinner spinner =(Spinner) findViewById(R.id.registerX_gender);
                //gender selector
        String[] genders = new String[]{
                "Pasirinkite lyti",
                "Vyras",
                "Moteris",
                "Kita"
        };

        final List<String> plantsList = new ArrayList<>(Arrays.asList(genders));

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, plantsList) {
            @Override
            public boolean isEnabled(int position) {

                if(position == 0){
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView GendertextView = (TextView) view;

                if(position == 0){
                    GendertextView.setText("");
                }
                else {
                }
                return view;
            }
        };
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String selectedTextItem = (String) parent.getItemAtPosition(position);

                if(position >0){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
        addVolunter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterVolunteer.class));
            }
        });
        mBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterX.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month +1;

                String date = year + " - " + month + " - "+ day + " ";
                mBirthday.setHint(date);
            }
        };
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterNewHelpSeeker();
            }
        });


    }

    private void setupFirebaseAuth(){
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Register without Facebook
     */

    private void RegisterNewHelpSeeker(){
                String name = mName.getText().toString().trim();
                String surname = mSurname.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String phoneNum = phoneNumber.getText().toString().trim();
                String gender = mGender.getSelectedItem().toString();
                String birthDate = mBirthday.getHint().toString();



                if(ValidateUtils.isEmpty(name) ) {
                    Toast.makeText(getApplicationContext(), "Pamirsote irasyti savo varda!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //REGEX SURNAME INPUT
                if(ValidateUtils.isEmpty(surname)){
                    Toast.makeText(getApplicationContext(), "Pamirsote irasyti savo pavarde", Toast.LENGTH_SHORT).show();
                    return;
                }


                //REGEX EMAIL INPUT
                if(ValidateUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Pamirsote irasyti savo e-pasta!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //REGEX PASSWORD

                //REGEX PHONE-NUMBER
                if(ValidateUtils.isNumeric(phoneNum)){
                    if(ValidateUtils.isPhone(phoneNum)){
                        return;
                    }
                }
                if(gender.equals("Pasirinkite lyti")){
                    Toast.makeText(getApplicationContext(), "Turi pasirinkti lyti!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(birthDate.equals("Pasirinkite gimimo data")){
                    Toast.makeText(getApplicationContext(), "Pasirinkite gimimo data", Toast.LENGTH_SHORT).show();
                    return;
                }

                progBar.setVisibility(View.VISIBLE);


                Fdata = FirebaseDatabase.getInstance();

                DataRefs = Fdata.getReference();

                mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterX.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(!task.isSuccessful()){
                                    Log.d(TAG, "onDataChange: Founded match");
                                    progBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Tokia paskyra jau yra, prisijunkite!", Toast.LENGTH_SHORT).show();
                                } else {

                                    progBar.setVisibility(View.GONE);
                                    String userUID = mFirebaseAuth.getCurrentUser().getUid();

                                    DataRefs.child("Help_Seekers").child(userUID).child(userUID.toString()).push();

                                    Fdata.getReference("Help_Seekers"+"/"+userUID.toString()).setValue(userUID.toString());
                                    Fdata.getReference("Help_Seekers"+"/"+userUID.toString()+"/Name").setValue(name);
                                    Fdata.getReference("Help_Seekers"+"/"+userUID.toString()+"/Surname").setValue(surname);
                                    Fdata.getReference("Help_Seekers"+"/"+userUID.toString()+"/Email").setValue(email);
                                    Fdata.getReference("Help_Seekers"+"/"+userUID.toString()+"/Phone_Number").setValue(phoneNum);
                                    Fdata.getReference("Help_Seekers"+"/"+userUID.toString()+"/Gender").setValue(gender);
                                    Fdata.getReference("Help_Seekers"+"/"+userUID.toString()+"/BirthDay").setValue(birthDate);
                                    Fdata.getReference("AllUsers"+"/"+userUID.toString()).setValue("Help_Seeker");

                                    startMainActivity();
                                    finish();
                                }





                            }

                        });
            }

            private boolean checkIfUserAlreadyExist(String email, DataSnapshot dataSnapshot){

        String userUID = mFirebaseAuth.getCurrentUser().getUid();
        User user = new User();
        userUID = mFirebaseAuth.getCurrentUser().getUid();
        for(DataSnapshot ds: dataSnapshot.child(userUID).getChildren()){
            user.setEmail(ds.getValue(User.class).getEmail());
            if(user.getEmail().equals(email))
                return true;
        }
        return false;
    }







    private void setupFacebookRegister(){

        FacebookSdk.sdkInitialize(getApplicationContext());
        fbCallBackManager  = CallbackManager.Factory.create();
    }

    private void bindFacebookButton(){

        fbRegister = (Button) findViewById(R.id.facebook_register_btn_help);


        LoginManager.getInstance().registerCallback(fbCallBackManager, new FacebookRegisterRequestCallBack());

        fbRegister.setOnClickListener(new View.OnClickListener() {

            final List<String> requestPermissions = Arrays.asList(
                    "public_profile",
                    "email"
            );

            @Override
            public void onClick(View view) {

                if(NetworkCheker.getInstance().isNetworkAvailable(getApplicationContext())){
                    LoginManager.getInstance().logInWithReadPermissions(RegisterX.this, requestPermissions);
                } else {
                    showNoInternetConnectionToast();
                }
            }
        });
    }


    private class FacebookRegisterRequestCallBack implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult registerResult) {
            showWhaitSpinner();
            handleFacebookAccesTokenForFirebase(registerResult.getAccessToken());
        }

        @Override
        public void onCancel() {
            stopSpinner();
            Toast.makeText(getApplicationContext(), R.string.facebook_cancel_string, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(FacebookException error) {
            stopSpinner();
            Log.d(TAG, "onError: " + error);
            Toast.makeText(getApplicationContext(), R.string.facebook_login_error_string, Toast.LENGTH_LONG).show();
        }
    }

    private void handleFacebookAccesTokenForFirebase(AccessToken token){

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        buildFirebaseUser(credential);
    }

    private void buildFirebaseUser(final AuthCredential credential){

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            mUserViewModel.retireveUSerFromRemoteStore().subscribe(new Consumer<User>() {
                                @Override
                                public void accept(User user) throws Exception {

                                    startMainActivity();
                                }
                            });
                        } else {
                            stopSpinner();
                            Toast.makeText(getApplicationContext(), R.string.facebook_error_credential_string, Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void startMainActivity(){

        stopSpinner();

        if(this.mUserViewModel.isAuth()){
            startActivity(new Intent(this, MainActivity.class));

        } else {
            Toast.makeText(this, "E-pasto klaida", Toast.LENGTH_LONG).show();
        }
    }

    private void showWhaitSpinner(){

        popupProgDialog = ProgressDialog.show(this, "", getString(R.string.loading_text), true);
    }

    private void stopSpinner(){
        if(popupProgDialog != null)

            popupProgDialog.dismiss();
        popupProgDialog = null;

    }

    private void showNoInternetConnectionToast(){

        stopSpinner();

        Toast.makeText(getApplicationContext(), R.string.no_internet_popup_label, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        showWhaitSpinner();
        super.onActivityResult(requestCode, resultCode, data);
        // facebook request has NO USER DEFINED CODEs
        fbCallBackManager.onActivityResult(requestCode, resultCode, data);
    }

}

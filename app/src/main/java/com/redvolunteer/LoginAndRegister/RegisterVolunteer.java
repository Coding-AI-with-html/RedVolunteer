package com.redvolunteer.LoginAndRegister;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import io.reactivex.functions.Consumer;

public class RegisterVolunteer extends AppCompatActivity {

    private static final String TAG = "RegisterVolunteer";

    /**
     * Facebook register
     */
    private Button fbRegister;

    private CallbackManager fbCallBackManager;

    /**
     * Handle to register with WM
     */
    private UserViewModel mUserViewModelVolunteer;

    /**
    Firebase
     */
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
    private Button registerVolunteer;
    private Button login;
    private Button addHelpSeeker;
    private EditText mNameVolunteer;
    private EditText mSurnameVolunteer;
    private Spinner mGenderVolunteer;
    private EditText mEmailVolunteer;
    private EditText mBirthdayVolunteer;
    private EditText mPasswordVolunteer;
    private EditText phoneNumberVolunteer;
    private ProgressBar progBarV;
    /**
     * DatePicker for birthday
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    /**
     * it simple is the popup spinner
     */
    private ProgressDialog popupProgDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!NetworkCheker.getInstance().isNetworkAvailable(this)){
            Toast.makeText(RegisterVolunteer.this, RegisterVolunteer.this.getString(R.string.no_internet_popup_label), Toast.LENGTH_LONG).show();
        }
        this.mUserViewModelVolunteer = ((RedVolunteerApplication) getApplication()).getUserViewModelVolunteer();

        setupFirebaseAuth();
        setupFacebookRegister();

        setContentView(R.layout.register_volunteer);


        bindLayoutComponents();
        this.bindFacebookButton();
    }

    private void bindLayoutComponents(){

        mNameVolunteer = (EditText) findViewById(R.id.Name_register_Volunteer);
        mSurnameVolunteer = (EditText) findViewById(R.id.Surname_register_Volunteer);
        mEmailVolunteer = (EditText) findViewById(R.id.Email_register_Volunteer);
        mPasswordVolunteer = (EditText) findViewById(R.id.Volunteer_password);
        mGenderVolunteer = (Spinner) findViewById(R.id.Volunteer_gender);
        mBirthdayVolunteer = (EditText) findViewById(R.id.Volunteer_birth_date);
        phoneNumberVolunteer = (EditText) findViewById(R.id.Volunteer_phone);
        registerVolunteer = (Button) findViewById(R.id.register_V);
        login = (Button) findViewById(R.id.go_to_loginV);
        addHelpSeeker = (Button) findViewById(R.id.register_helpseeker_from_volunteer);
        progBarV = (ProgressBar) findViewById(R.id.Volunteer_register_progressbar);




        final Spinner spinner =(Spinner) findViewById(R.id.Volunteer_gender);
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
        registerVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterNewVolunteer();
            }
        });
        addHelpSeeker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterX.class));
            }
        });
        mBirthdayVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterVolunteer.this,
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
                mBirthdayVolunteer.setHint(date);
            }
        };
    }

    private void setupFirebaseAuth(){
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void setupFacebookRegister(){

        FacebookSdk.sdkInitialize(getApplicationContext());
        fbCallBackManager  = CallbackManager.Factory.create();
    }

   private void bindFacebookButton(){

        fbRegister = (Button) findViewById(R.id.facebook_register_btn);

        LoginManager.getInstance().registerCallback(fbCallBackManager, new FacebookRegisterRequestCallBack());


       fbRegister.setOnClickListener(new View.OnClickListener() {

           final List<String> requestPermissions = Arrays.asList(
                   "public_profile",
                   "email"
           );

           @Override
           public void onClick(View view) {

               if(NetworkCheker.getInstance().isNetworkAvailable(getApplicationContext())){
                   LoginManager.getInstance().logInWithReadPermissions(RegisterVolunteer.this, requestPermissions);
               } else {
                   ShowNoInternetConnectionToast();
               }
           }
       });
    }

    private void RegisterNewVolunteer(){

        String Name = mNameVolunteer.getText().toString().trim();
        String Surname = mSurnameVolunteer.getText().toString().trim();
        String email = mEmailVolunteer.getText().toString().trim();
        String password = mPasswordVolunteer.getText().toString().trim();
        String PhoneNum = phoneNumberVolunteer.getText().toString().trim();
        String Gender = mGenderVolunteer.getSelectedItem().toString();
        String BirthDate = mBirthdayVolunteer.getHint().toString();

        if(ValidateUtils.isEmpty(Name) ) {
            Toast.makeText(getApplicationContext(), "Pamirsote irasyti savo varda!", Toast.LENGTH_SHORT).show();
            return;
        }
        //REGEX SURNAME INPUT
        if(ValidateUtils.isEmpty(Surname)){
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
        if(ValidateUtils.isNumeric(PhoneNum)){
            if(ValidateUtils.isPhone(PhoneNum)){
                return;
            }
        }
        if(Gender.equals("Pasirinkite lyti")){
            Toast.makeText(getApplicationContext(), "Turi pasirinkti lyti!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(BirthDate.equals("Pasirinkite gimimo data")){
            Toast.makeText(getApplicationContext(), "Pasirinkite gimimo data", Toast.LENGTH_SHORT).show();
            return;
        }

        progBarV.setVisibility(View.VISIBLE);
        Fdata = FirebaseDatabase.getInstance();
        DataRefs = Fdata.getReference().child("Volunteers");

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterVolunteer.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful()){
                            Log.d(TAG, "onComplete: "+ task.getException().getMessage());
                            progBarV.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Tokia paskyra jau yra, prisijunkite!", Toast.LENGTH_SHORT).show();

                        } else {

                            progBarV.setVisibility(View.GONE);
                            String userID = mFirebaseAuth.getCurrentUser().getUid();

                            DataRefs.child(userID).child(userID.toString()).push();

                            Fdata.getReference("Volunteers"+"/"+userID.toString()).setValue(userID.toString());
                            Fdata.getReference("Volunteers"+"/"+userID.toString()+"/Name").setValue(Name);
                            Fdata.getReference("Volunteers"+"/"+userID.toString()+"/Surname").setValue(Surname);
                            Fdata.getReference("Volunteers"+"/"+userID.toString()+"/Email").setValue(email);
                            Fdata.getReference("Volunteers"+"/"+userID.toString()+"/Phone_Number").setValue(PhoneNum);
                            Fdata.getReference("Volunteers"+"/"+userID.toString()+"/Gender").setValue(Gender);
                            Fdata.getReference("Volunteers"+"/"+userID.toString()+"/BirthDay").setValue(BirthDate);
                            Fdata.getReference("AllUsers"+"/"+userID.toString()).setValue("Volunteer");

                            startActivity(new Intent(RegisterVolunteer.this, MainActivity.class));
                            finish();
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
            Toast.makeText(getApplicationContext(), R.string.facebook_login_error_string, Toast.LENGTH_LONG).show();
            Log.d(TAG, "onError: " + error);
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

                            mUserViewModelVolunteer.retireveUSerFromRemoteStore().subscribe(new Consumer<User>() {
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

        if(this.mUserViewModelVolunteer.isAuth()){
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

    private void ShowNoInternetConnectionToast(){

        stopSpinner();

        Toast.makeText(getApplicationContext(), R.string.no_internet_popup_label, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        showWhaitSpinner();
        super.onActivityResult(requestCode, resultCode, data);

        fbCallBackManager.onActivityResult(requestCode, resultCode, data);
    }
}

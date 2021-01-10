package com.redvolunteer.LoginAndRegister;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.redvolunteer.R;
import com.redvolunteer.pojo.User;

public class FirebaseStuff {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private  String userUID;
    private Context mContext;
    private FirebaseDatabase FData;
    private DatabaseReference DataRefs;



    public  FirebaseStuff(Context context){
        FData = FirebaseDatabase.getInstance();
        DataRefs = FData.getReference();
        mAuth = FirebaseAuth.getInstance();
        mContext = context;
        if(mAuth.getCurrentUser() != null){
            userUID = mAuth.getCurrentUser().getUid();
        }
    }


    //method for sign up new user
    public void registerNEwUser(final String email, String password, final String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //send verification email
                            sentVerification();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        userUID = mAuth.getCurrentUser().getUid();
    }
    public void sentVerification(){
        FirebaseUser Fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(Fuser != null){
            Fuser.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            } else {
                                Toast.makeText(mContext, "Couldint sent email verification", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }



    public void addNewUser(String Name,String Surname,String Email, String biography, long Phone_Number, String Gender){

       // User FUser = new User(userUID, Name, Surname, Email, biography, Phone_Number, Gender);

        DataRefs.child(mContext.getString(R.string.database_Help_seekers))
                .child(userUID);
                //.setValue(FUser);

    }
}

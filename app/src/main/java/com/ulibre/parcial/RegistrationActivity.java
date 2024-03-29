
package com.ulibre.parcial;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private AutoCompleteTextView username, email, password;
    private Button signup;
    private TextView signin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeGUI();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String inputName = username.getText().toString().trim();
                final String inputPw = password.getText().toString().trim();
                final String inputEmail = email.getText().toString().trim();

                if(validateInput(inputName, inputPw, inputEmail))
                         registerUser(inputName, inputPw, inputEmail);

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });



    }


    private void initializeGUI(){

        username = findViewById(R.id.atvUsernameReg);
        email =  findViewById(R.id.atvEmailReg);
        password = findViewById(R.id.atvPasswordReg);
        signin = findViewById(R.id.tvSignIn);
        signup = findViewById(R.id.btnSignUp);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }


    /**
     * Registra el usuario con Firebase
     * @param inputName
     * @param inputPw
     * @param inputEmail
     */
    private void registerUser(final String inputName, final String inputPw, String inputEmail) {

        progressDialog.setMessage("Verificando...");
        progressDialog.show();


            firebaseAuth.createUserWithEmailAndPassword(inputEmail,inputPw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        sendUserData(inputName, inputPw);
                        Toast.makeText(RegistrationActivity.this,"Has sido registrado exitosamente.",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
                    }
                    else{

                        progressDialog.dismiss();
                        Toast.makeText(RegistrationActivity.this,"El correo ya existe " + ( task.getException().getMessage() ),Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }


    /**
     * Envía la información a la DB
     * @param username
     * @param password
     */
    private void sendUserData(String username, String password){

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference users = firebaseDatabase.getReference("users");
        UserProfile user = new UserProfile(username, password);
        users.push().setValue(user);

    }

    /***
     * Valida los campos
     * @param inName
     * @param inPw
     * @param inEmail
     * @return
     */
    private boolean validateInput(String inName, String inPw, String inEmail){

        if(inName.isEmpty()){
            username.setError("Nombre de usuario vacío");
            return false;
        }
        if(inPw.isEmpty()){
            password.setError("Contraseña vacía");
            return false;
        }
        if(inEmail.isEmpty()){
            email.setError("Correo vacío");
            return false;
        }

        return true;
    }


}

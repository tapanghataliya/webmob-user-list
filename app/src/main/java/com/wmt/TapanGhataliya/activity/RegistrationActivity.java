package com.wmt.TapanGhataliya.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wmt.TapanGhataliya.MainActivity;
import com.wmt.TapanGhataliya.R;
import com.wmt.TapanGhataliya.controller.Global;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;

    EditText edt_userName, edt_emailId, edt_password, edt_confirmPassword;
    LinearLayout lyl_registration;
    String EDT_USERNAME, EDT_EMAIL, EDT_PASSWORD, EDT_CPASSWORD;
    RelativeLayout progressBar;
    Global global;
    boolean isRegistration = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        Initilization();
    }

    private void Initilization() {

        sharedPreferences = getSharedPreferences(Global.MyApplication, MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(RegistrationActivity.this);

        edt_userName = (EditText) findViewById(R.id.edt_userName);
        edt_emailId = (EditText) findViewById(R.id.edt_emailId);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_confirmPassword = (EditText) findViewById(R.id.edt_confirmPassword);

        progressBar = (RelativeLayout) findViewById(R.id.progressBar);
        lyl_registration = (LinearLayout) findViewById(R.id.lyl_registration);
        lyl_registration.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.lyl_registration:

                Validation();
                break;
        }
    }

    private void Validation() {

        EDT_USERNAME = edt_userName.getText().toString().trim();
        EDT_EMAIL = edt_emailId.getText().toString().trim();
        EDT_PASSWORD = edt_password.getText().toString().trim();
        EDT_CPASSWORD = edt_confirmPassword.getText().toString().trim();
        int length = edt_password.getText().length();

        if (EDT_USERNAME.equalsIgnoreCase("")) {

            edt_userName.setFocusable(true);
            edt_userName.setError("Field is Required");

        } else if (EDT_EMAIL.equalsIgnoreCase("")) {

            edt_userName.setError(null);
            edt_emailId.setFocusable(true);
            edt_emailId.setError("Field is Required");

        } else if (!EDT_EMAIL.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {

            Toast.makeText(RegistrationActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            edt_emailId.setError("Invalid Email Address");

        } else if (EDT_PASSWORD.equalsIgnoreCase("")) {

            edt_emailId.setError(null);
            edt_password.setFocusable(true);
            edt_password.setError("Field is Required");

        } else if (length < 6) {

            Toast.makeText(RegistrationActivity.this, "Please enter valid 6 digit pincode", Toast.LENGTH_SHORT).show();

        } else if (EDT_CPASSWORD.equalsIgnoreCase("")) {

            edt_password.setError(null);
            edt_confirmPassword.setFocusable(true);
            edt_confirmPassword.setError("Field is Required");

        } else if (!EDT_PASSWORD.equals(EDT_CPASSWORD)) {

            Toast.makeText(RegistrationActivity.this, "Password Not matching", Toast.LENGTH_SHORT).show();

        } else {

            getRegistration(EDT_USERNAME, EDT_EMAIL, EDT_PASSWORD);
        }
    }

    private void getRegistration(String e_username, String e_email, String e_password) {

        progressBar.setVisibility(View.VISIBLE);
        JSONObject obj = new JSONObject();
        try {

            obj.put("username", e_username);
            obj.put("email", e_email);
            obj.put("password", e_password);

        } catch (JSONException e) {

            e.printStackTrace();
        }
        Log.i("REGISTRATION_OBJ", obj.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Global.BASE_URL + "register", obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                Log.i("REGISTRATION_RESPONSE", response.toString());
                // TODO Auto-generated method stub
                try {

                    String META = response.getString("meta");
                    Log.i("REGISTRATIONL_NAME", META);

                    JSONObject jsonobject = response.getJSONObject("meta");

                    String STATUS = jsonobject.getString("status");
                    String MESSAGE = jsonobject.getString("message");

                    if (STATUS.equalsIgnoreCase("ok")){

                        Toast.makeText(RegistrationActivity.this,MESSAGE,Toast.LENGTH_SHORT).show();
                        JSONObject json_Data = response.getJSONObject("data");
                        Log.i("REGI_json_Data", json_Data.toString());

                        JSONObject json_User = json_Data.getJSONObject("user");
                        Log.i("REGI_json_User", json_Data.toString());

                        String U_NAME = json_User.getString("username");
                        String U_EMAIL = json_User.getString("email");
                        String U_PIC = json_User.getString("profile_pic");
                        String U_ID = json_User.getString("id");

                        Log.i("REGISTRATIONL_U_NAME", U_NAME);
                        Log.i("REGISTRATIONL_U_EMAIL", U_EMAIL);
                        Log.i("REGISTRATIONL_U_PIC", U_PIC);
                        Log.i("REGISTRATIONL_U_ID", U_ID);

                        JSONObject json_Token = json_Data.getJSONObject("token");
                        Log.i("REGI_json_User", json_Data.toString());

                        String U_BEARER = json_Token.getString("type");
                        String U_TOKEN = json_Token.getString("token");
                        String U_R_TOKEN = json_Token.getString("refreshToken");

                        Log.i("REGISTRATIONL_U_BEARER", U_BEARER);
                        Log.i("REGISTRATIONL_U_TOKEN", U_TOKEN);
                        Log.i("REGISTRATIONL_U_R_TOKEN", U_R_TOKEN);

                        Intent i_registration = new Intent(RegistrationActivity.this, MainActivity.class);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(global.id, U_ID);
                        editor.putString(global.username, U_NAME);
                        editor.putString(global.email, U_EMAIL);
                        editor.putString(global.token, U_TOKEN);
                        editor.putBoolean(global.CURRENT_USER_LOGIN, isRegistration);
                        editor.apply();
                        startActivity(i_registration);
                        finish();

                    } else {

                        Toast.makeText(RegistrationActivity.this,MESSAGE,Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                progressBar.setVisibility(View.GONE);
                NetworkResponse networkResponse = error.networkResponse;

                if (networkResponse != null && networkResponse.statusCode == 404) {
                    Toast.makeText(RegistrationActivity.this, "No Record Found.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(RegistrationActivity.this, "Time Out,Network is slow.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(RegistrationActivity.this, "Please Check Your Internet Connection.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(RegistrationActivity.this, "Authentication Error.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(RegistrationActivity.this, "Server Not Connected.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(RegistrationActivity.this, "Network Error.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(RegistrationActivity.this, "Parse Error.", Toast.LENGTH_SHORT).show();
                }

                Log.i("error --> ", error.toString());
            }

        });
        requestQueue.getCache().clear();
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }
}

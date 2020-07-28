package com.example.facebookapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String EMAIL = "email";
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL, "public_profile"));


        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e("TAG", "onSuccess: ");
                        loadUserInfo(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.e("TAG", "onCancel:");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e("TAG", "onError: ");
                    }
                });

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Log.e("TAG", "Access Token " + accessToken);
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            Log.e("TAG", "onCreate: isloggedin");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        // loadUserInfo(AccessToken.getCurrentAccessToken());
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                Log.e("TAG", "onCurrentAccessTokenChanged: null");
            }

        }
    };

    private void loadUserInfo(AccessToken newAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {


                Profile profile = Profile.getCurrentProfile();

                if (profile != null) {

                    try {
                        String first_name = profile.getFirstName();
                        String last_name = profile.getLastName();
                        String id = object.getString("id");
                        String likes = object.getString("likes");

                        Log.e("TAG", "onCompleted: " + first_name + " " + last_name);
                        Log.e("TAG", "LIKES: " + likes );

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name, last_name, email, id, likes");
        request.setParameters(parameters);
        request.executeAsync();

    }
}
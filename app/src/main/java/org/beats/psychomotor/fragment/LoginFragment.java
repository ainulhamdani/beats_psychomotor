package org.beats.psychomotor.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.beats.psychomotor.R;
import org.beats.psychomotor.activity.MainActivity;

/**
 * Created by Dani on 19/02/2018.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    private String username;
    private Button loginButton;
    private EditText editText;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_login, container, false);
        loginButton = (Button) rootView.findViewById(R.id.login_button);
        editText = (EditText) rootView.findViewById(R.id.userid);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = editText.getText().toString();
                login();
            }
        });
        return rootView;
    }

    private void login(){
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.shared_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.USERNAME),username);
        editor.commit();
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}

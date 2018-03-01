package org.beats.psychomotor.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.beats.psychomotor.R;
import org.beats.psychomotor.activity.LoginActivity;
import org.beats.psychomotor.activity.MainActivity;
import org.beats.psychomotor.miscellaneous.ResizableButton;
import org.beats.psychomotor.room.AppDb;
import org.beats.psychomotor.room.AssessmentData;
import org.beats.psychomotor.room.BlockData;
import org.beats.psychomotor.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Dani on 20/02/2018.
 */

public class ClientServerFragment extends Fragment {
    private static final String TAG = "HostFragment";

    private String username;
    private boolean doubleBackToExitPressedOnce = false;

    public static AppDb appDb;

    ResizableButton syncButton;
    ProgressBar syncProgress;
    ProgressBar syncProgressBar;

    private boolean isSyncing = false;

    public ClientServerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDb = ((MainActivity)getActivity()).getAppDb();
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.shared_key), Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.USERNAME),"");
        readDb();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.clientserver, container, false);
        TextView userName = (TextView)rootView.findViewById(R.id.cs_user);
        userName.setText("Welcome "+username);
        Button host = (Button) rootView.findViewById(R.id.be_host);
        Button join = (Button) rootView.findViewById(R.id.be_client);
        Button changeUser = (Button) rootView.findViewById(R.id.changeuser);
        ResizableButton backButton = (ResizableButton) rootView.findViewById(R.id.cs_back);
        syncButton = (ResizableButton) rootView.findViewById(R.id.sync);
        syncProgress = (ProgressBar) rootView.findViewById(R.id.progress);
        syncProgressBar = (ProgressBar) rootView.findViewById(R.id.progressbar);
        changeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getActivity().getSharedPreferences(
                        getString(R.string.shared_key), Context.MODE_PRIVATE);
                sharedPref.edit().remove(getString(R.string.USERNAME)).commit();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new HostFragment()).addToBackStack(HostFragment.class.getName())
                        .commit();
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new ClientFragment()).addToBackStack(ClientFragment.class.getName())
                        .commit();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (doubleBackToExitPressedOnce) {
                    getActivity().finish();
                    return;
                }

                doubleBackToExitPressedOnce = true;
                Toast.makeText(getActivity(), "Please click EXIT again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            }
        });
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSyncing = true;
                syncButton.setVisibility(View.GONE);
                syncProgress.setVisibility(View.VISIBLE);
                syncProgressBar.setVisibility(View.VISIBLE);
                syncDb();
            }
        });
        return rootView;
    }

    private void syncDb(){
        Log.d(TAG, "syncDb: sync started");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                List<BlockData> blocks = appDb.blockDataDao().getAllUnsync();
                List<AssessmentData> assessmentDatas = appDb.assessmentDataDao().getAllUnsync();
                int i = 0;
                syncProgressBar.setMax(blocks.size()+assessmentDatas.size());
                syncProgressBar.setProgress(i);
                for(AssessmentData assessmentData:assessmentDatas){
                    if(!this.isCancelled()){
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("assessment_id",assessmentData.getAssessmentId());
                            obj.put("task_id",assessmentData.getTaskId());
                            obj.put("time_start",assessmentData.getStart());
                            obj.put("time_end",assessmentData.getEnd());
                            Log.d(TAG, "assessment: send "+obj.toString());
                            int resCode = sendPost(obj, Constants.Url.assessment_url);
                            Log.d(TAG, "doInBackground: resCode = "+resCode);
                            if(resCode!=201){
                                Toast.makeText(getActivity(), "Sync failed", Toast.LENGTH_SHORT).show();
                                isSyncing = false;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toggleSyncButton();
                                    }
                                });
                                this.cancel(true);
                            }else{
                                assessmentData.setSynced(1);
                                appDb.assessmentDataDao().updateAssessment(assessmentData);
                                syncProgressBar.setProgress(++i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            isSyncing = false;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Sync failed", Toast.LENGTH_SHORT).show();
                                    toggleSyncButton();
                                }
                            });
                            this.cancel(true);
                        }
                    }
                }
                for(BlockData block : blocks){
                    if(!this.isCancelled()){
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("assessment_id",block.getAssessmentId());
                            obj.put("task_id",block.getTaskId());
                            obj.put("username",block.getUsername());
                            obj.put("color",block.getColor());
                            obj.put("pos_x",block.getPosX());
                            obj.put("pos_y",block.getPosY());
                            obj.put("timestamp",block.getTimestamp());
                            Log.d(TAG, "block: send "+obj.toString());
                            int resCode = sendPost(obj, Constants.Url.block_url);
                            Log.d(TAG, "doInBackground: resCode = "+resCode);
                            if(resCode!=201){
                                Toast.makeText(getActivity(), "Sync failed", Toast.LENGTH_SHORT).show();
                                isSyncing = false;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toggleSyncButton();
                                    }
                                });
                                this.cancel(true);
                            }else{
                                block.setSynced(1);
                                appDb.blockDataDao().updateBlock(block);
                                syncProgressBar.setProgress(++i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            isSyncing = false;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Sync failed", Toast.LENGTH_SHORT).show();
                                    toggleSyncButton();
                                }
                            });
                            this.cancel(true);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getActivity(), "Sync success", Toast.LENGTH_SHORT).show();
                isSyncing = false;
                toggleSyncButton();
            }
        }.execute();
    }

    public int sendPost(final JSONObject jsonParam, final String urlAdress) throws IOException {
        int respondCode;
        URL url = new URL(urlAdress);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept","application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
        os.writeBytes(jsonParam.toString());

        os.flush();
        os.close();

        respondCode = conn.getResponseCode();
        Log.d(TAG, "sendPost: msg: "+conn.getResponseMessage());

        conn.disconnect();
        return respondCode;
    }

    private void toggleSyncButton(){
        if(isSyncing){
            syncButton.setVisibility(View.GONE);
            syncProgress.setVisibility(View.VISIBLE);
            syncProgressBar.setVisibility(View.VISIBLE);
        }else{
            syncButton.setVisibility(View.VISIBLE);
            syncProgress.setVisibility(View.GONE);
            syncProgressBar.setVisibility(View.GONE);
        }
    }

    private void readDb(){
        Log.d(TAG, "readDb: start");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.d(TAG, "doInBackground: start read db");
                List<BlockData> blocks = appDb.blockDataDao().getAll();
                List<AssessmentData> assessmentDatas = appDb.assessmentDataDao().getAll();
                for(AssessmentData assessmentData:assessmentDatas){
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("assessment_id",assessmentData.getAssessmentId());
                        obj.put("task_id",assessmentData.getTaskId());
                        obj.put("username",assessmentData.getStart());
                        obj.put("color",assessmentData.getEnd());
                        obj.put("synced",assessmentData.getSynced());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "assessment: "+obj.toString());
                }
                for(BlockData block : blocks){
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("assessment_id",block.getAssessmentId());
                        obj.put("task_id",block.getTaskId());
                        obj.put("username",block.getUsername());
                        obj.put("color",block.getColor());
                        obj.put("pos_x",block.getPosX());
                        obj.put("pos_y",block.getPosY());
                        obj.put("timestamp",block.getTimestamp());
                        obj.put("synced",block.getSynced());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "block: "+obj.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.d(TAG, "onPostExecute: data saved");
            }
        }.execute();
    }
}

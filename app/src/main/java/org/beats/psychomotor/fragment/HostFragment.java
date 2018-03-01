package org.beats.psychomotor.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.beats.psychomotor.R;
import org.beats.psychomotor.activity.MainActivity;
import org.beats.psychomotor.adapter.ClientAdapter;
import org.beats.psychomotor.connection.ServerConnectionTest;
import org.beats.psychomotor.connection.ServerConnectionThread;
import org.beats.psychomotor.model.Assessment;
import org.beats.psychomotor.model.Block;
import org.beats.psychomotor.room.AppDb;
import org.beats.psychomotor.room.AssessmentData;
import org.beats.psychomotor.room.BlockData;
import org.beats.psychomotor.utils.Constants;
import org.beats.psychomotor.utils.IPAddress;
import org.beats.psychomotor.utils.ServerHandler;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Dani on 20/02/2018.
 */

//TODO make list client updated when client disconnect

public class HostFragment extends Fragment {
    private static final String TAG = "HostFragment";

    public static ServerHandler serverHandler;

    SharedPreferences sharedPref;

    private String username;
    private Button startButton;
    private Button cancelButton;
    public static ArrayList<String> deviceList = new ArrayList();
    public static ClientAdapter mAdapter;
    public static Assessment gameObject;

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private RecyclerView mUserList;
    protected RecyclerView.LayoutManager mLayoutManager;

    NetworkInfo mWifi;
    private String myIP;
    ServerConnectionThread startServerThread;
    ServerConnectionTest startServerTest;

    public static AppDb appDb;

    public static String assessmentId;
    EditText assessmentText;
    public static String taskId;
    Spinner taskList;

    public HostFragment() {
    }


    protected LayoutManagerType mCurrentLayoutManagerType;
    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDb = ((MainActivity)getActivity()).getAppDb();
        deviceList = new ArrayList();
        serverHandler = new ServerHandler();
        sharedPref = getActivity().getSharedPreferences(
                getString(R.string.shared_key), Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.USERNAME),"");
        deviceList.add(username);
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(mWifi.isConnected()){
            myIP = IPAddress.getIPAddress(true);
            Log.d(TAG, "onCreate: myIP : "+myIP);
            startServerThread = new ServerConnectionThread();
            startServerTest = new ServerConnectionTest();
        }
        else{
            myIP = IPAddress.getIPAddress(true);
            if (!myIP.equals("")){
                Log.d(TAG, "onCreate: myIP : "+myIP);
                startServerThread = new ServerConnectionThread();
                startServerTest = new ServerConnectionTest();
            }else{
                Toast.makeText(getActivity(), "Not connected to the network", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        TextView userName = (TextView)rootView.findViewById(R.id.h_user);
        assessmentText = (EditText)rootView.findViewById(R.id.assessmenttext);
        taskList = (Spinner) rootView.findViewById(R.id.tasklist);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.task_id, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String task_id = sharedPref.getString(getString(R.string.TASK_ID),"");
        if(!task_id.equals("")){
            final int _taskId = Integer.parseInt(task_id);
            taskList.setAdapter(adapter);
            taskList.post(new Runnable() {
                @Override
                public void run() {
                    taskList.setSelection(_taskId,true);
                }
            });
            taskList.animate();
            int _taskId2 = _taskId+1;
            taskId = String.valueOf(_taskId2);
        }else{
            taskList.setAdapter(adapter);
        }
        String _assessmentId = sharedPref.getString(getString(R.string.ASSESSMENT_ID),"");
        if(!_assessmentId.equals("")){
            assessmentText.setText(_assessmentId);
        }
        mUserList = (RecyclerView) rootView.findViewById(R.id.listview);
        userName.setText("Welcome "+username);
        startButton = (Button) rootView.findViewById(R.id.start);
        cancelButton = (Button) rootView.findViewById(R.id.h_cancel);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assessmentId = assessmentText.getText().toString();
                if(assessmentId.equals("")){
                    Toast.makeText(getActivity(), "Please fill the Assessment ID", Toast.LENGTH_LONG).show();
                    return;
                }

                taskId = taskList.getSelectedItem().toString();

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.ASSESSMENT_ID),assessmentId);
                editor.putString(getString(R.string.TASK_ID),taskId);
                editor.commit();
                gameObject = new Assessment(deviceList,"Psychomotor");
                gameObject.senderUsername = String.valueOf(Constants.NEW_GAME);
                Bundle bundle = new Bundle();
                bundle.putSerializable("game",gameObject);
                AssessmentFragment touch = new AssessmentFragment();
                touch.setArguments(bundle);
                ServerHandler.sendToAll(gameObject);
                saveStartAssessment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, touch).addToBackStack(AssessmentFragment.class.getName())
                        .commit();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assessmentId = "";
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove(getString(R.string.ASSESSMENT_ID));
                editor.remove(getString(R.string.TASK_ID));
                editor.commit();
                try {
                    if(startServerThread!=null) startServerThread.serverSocket.close();
                    if(startServerTest!=null) startServerTest.testSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getFragmentManager().popBackStack();
            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        mAdapter = new ClientAdapter(deviceList);
        mUserList.setAdapter(mAdapter);
        if(startServerThread!=null){
            if(!startServerThread.isAlive()){
                startServerThread.start();
            }
        }
        if(startServerTest!=null){
            if(!startServerTest.isAlive()){
                startServerTest.start();
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        if (mUserList.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mUserList.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mUserList.setLayoutManager(mLayoutManager);
        mUserList.scrollToPosition(scrollPosition);
    }

    public static String getColor(int color){
        if(color==Constants.Color.YELLOW){
            return "yellow";
        }else if(color==Constants.Color.WHITE){
            return "white";
        }else if(color==Constants.Color.RED){
            return "red";
        }else if(color==Constants.Color.BLUE){
            return "blue";
        }
        return "null";
    }

    public static void saveStartAssessment(){
        final AssessmentData assessmentData = new AssessmentData();
        assessmentData.setAssessmentId(assessmentId);
        assessmentData.setTaskId(taskId);
        assessmentData.setStart((new Timestamp(System.currentTimeMillis())).toString());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                appDb.assessmentDataDao().insertAll(assessmentData);
                return null;
            }
        }.execute();
    }

    public static void saveEndAssessment(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                AssessmentData assessmentData = appDb.assessmentDataDao().getAssessment(assessmentId,taskId);
                assessmentData.setEnd((new Timestamp(System.currentTimeMillis())).toString());
                appDb.assessmentDataDao().updateAssessment(assessmentData);
                return null;
            }
        }.execute();
    }

    public static void saveBlock(Block block){
        final BlockData blockData = new BlockData();
        blockData.setAssessmentId(assessmentId);
        blockData.setTaskId(taskId);
        blockData.setUsername(block.getSenderUsername());
        blockData.setColor(getColor(block.getColor()));
        blockData.setPosX(block.getX());
        blockData.setPosY(block.getY());
        blockData.setTimestamp((new Timestamp(System.currentTimeMillis())).toString());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                appDb.blockDataDao().insertAll(blockData);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.d(TAG, "onPostExecute: data saved");
            }
        }.execute();
    }

}

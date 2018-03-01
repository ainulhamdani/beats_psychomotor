package org.beats.psychomotor.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.beats.psychomotor.R;
import org.beats.psychomotor.activity.LoginActivity;
import org.beats.psychomotor.activity.MainActivity;
import org.beats.psychomotor.connection.ClientConnectionThread;
import org.beats.psychomotor.miscellaneous.ResizableButton;
import org.beats.psychomotor.model.Assessment;
import org.beats.psychomotor.utils.ClientHandler;
import org.beats.psychomotor.utils.IPAddress;
import org.beats.psychomotor.utils.WifiHelper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Dani on 20/02/2018.
 */

public class ClientFragment extends Fragment {
    private static final String TAG = "HostFragment";

    public static String username;
    public static ClientHandler clientHandler;
    public static TextView hostName;
    public static Assessment gameObject;
    public static FragmentActivity thisActivity;
    private ListView listViewIp;
    ArrayList<String> ipList;
    ArrayAdapter<String> adapter;

    private String myIP;
    protected ScanIpTask scanIpTask;
    public static Button scan;
    public static Button stopScan;
    public static Button disconnect;

    NetworkInfo mWifi;

    static ClientConnectionThread clientConnect;

    public ClientFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.shared_key), Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.USERNAME),"");
        thisActivity = getActivity();
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(mWifi.isConnected()){
            myIP = IPAddress.getIPAddress(true);
            Log.d(TAG, "onCreate: myIP : "+myIP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.client_fragment, container, false);
        TextView userName = (TextView)rootView.findViewById(R.id.c_user);
        listViewIp = (ListView)rootView.findViewById(R.id.listviewip);
        listViewIp.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String destIP = listViewIp.getItemAtPosition(i).toString();
                Log.d(TAG, "onItemClick: "+destIP);
                try {
                    stopScanIP();
                    connect(destIP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        ipList = new ArrayList();
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, android.R.id.text1, ipList);
        listViewIp.setAdapter(adapter);
        userName.setText("Welcome "+username);
        hostName = (TextView)rootView.findViewById(R.id.hostname);
        scan = (Button) rootView.findViewById(R.id.scanip);
        stopScan = (Button) rootView.findViewById(R.id.stopscanip);
        disconnect = (Button) rootView.findViewById(R.id.disconnect);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanIP();
            }
        });
        stopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopScanIP();
            }
        });
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        });
        ResizableButton backButton = (ResizableButton) rootView.findViewById(R.id.c_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopScanIP();
                disconnect();
                getFragmentManager().popBackStack();
            }
        });

        return rootView;
    }

    public static void goToTouchFragment(){
        Bundle bundle = new Bundle();
        bundle.putSerializable("game",gameObject);
        AssessmentFragment touch = new AssessmentFragment();
        touch.setArguments(bundle);
        thisActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, touch).addToBackStack(AssessmentFragment.class.getName())
                .commit();
    }

    private void scanIP(){
        scanIpTask = new ScanIpTask();
        scanIpTask.execute();
        scan.setVisibility(View.GONE);
        stopScan.setVisibility(View.VISIBLE);
    }

    private void stopScanIP(){
        if(scanIpTask!=null){
            scanIpTask.isCancelled = true;
            scanIpTask.cancel(true);
        }
        stopScan.setVisibility(View.GONE);
        scan.setVisibility(View.VISIBLE);
    }

    private void connect(String destIP) throws InterruptedException {
        if (clientHandler == null) {
            clientHandler = new ClientHandler();
        }
        if(clientConnect == null){
            clientConnect = new ClientConnectionThread(username,destIP);
            clientConnect.start();
            clientConnect.join();
            if(clientConnect.serverStarted){
                Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "Can't connect, check host", Toast.LENGTH_SHORT).show();
            }
        }else if(clientConnect.socket.isClosed()) {
            clientConnect = new ClientConnectionThread(username,destIP);
            clientConnect.start();
            clientConnect.join();
            if(clientConnect.serverStarted){
                Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "Can't connect, check host", Toast.LENGTH_SHORT).show();
            }
        }else if(!clientConnect.socket.isConnected()){
            Toast.makeText(getActivity(), "Can't connect, check host", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(), "Already connected", Toast.LENGTH_SHORT).show();
        }

    }

    public static void disconnect(){
        Log.d(TAG, "disconnect: try to disconnect");
        if(clientConnect!=null){
            Log.d(TAG, "disconnect: clientConnect not null");
            clientConnect.disconnect();
        }
        clientHandler = null;
        hostName.setText("disconnected");
        stopScan.setVisibility(View.GONE);
        disconnect.setVisibility(View.GONE);
        scan.setVisibility(View.VISIBLE);
    }

    private class ScanIpTask extends AsyncTask<Void, String, Void> {

        private String subnet;
        static final int lower = 1;
        static final int upper = 1;
        static final int timeout = 500;
        public boolean isCancelled = false;

        @Override
        protected void onPreExecute() {
            ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mWifi.isConnected()) {
                // Do whatever
                myIP = IPAddress.getIPAddress(true);
                subnet = myIP.substring(0,myIP.lastIndexOf(".")).concat(".");

                ipList.clear();
                adapter.notifyDataSetInvalidated();
//                Toast.makeText(getActivity(), "Scanning...", Toast.LENGTH_LONG).show();
            }else{
                ipList.clear();
                adapter.notifyDataSetInvalidated();
                Toast.makeText(getActivity(), "Wifi not connected", Toast.LENGTH_LONG).show();
                isCancelled = true;
            }

        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = lower; i <= upper; i++) {
                String host = subnet + i;
                if(!isCancelled){
                    try {
                        InetAddress inetAddress = InetAddress.getByName(host);
                        Log.d(TAG, "doInBackground: checking "+host);
                        if (inetAddress.isReachable(timeout)){
                            Log.d(TAG, "doInBackground: "+host+" portIsOpen "+portIsOpen(host,23233,5000));
                            if(portIsOpen(host,23233,5000)){
                                Log.d(TAG, "doInBackground: port "+host+" is open");
                                publishProgress(host);
                            }
                        }

                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.d(TAG, "doInBackground: canceled");
                    break;
                }

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            ipList.add(values[0]);
            adapter.notifyDataSetInvalidated();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            stopScan.setVisibility(View.GONE);
            scan.setVisibility(View.VISIBLE);
//            if(!isCancelled)Toast.makeText(getActivity(), "Done", Toast.LENGTH_LONG).show();
        }
    }

    public boolean portIsOpen(String ip, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}

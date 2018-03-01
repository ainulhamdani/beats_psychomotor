package org.beats.psychomotor.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import org.beats.psychomotor.R;
import org.beats.psychomotor.TouchView;
import org.beats.psychomotor.dialog.TimeoutDialog;
import org.beats.psychomotor.model.Assessment;
import org.beats.psychomotor.utils.ClientHandler;
import org.beats.psychomotor.utils.ServerHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created by Dani on 20/02/2018.
 */

public class TouchFragment extends Fragment {
    private static final String TAG = "TouchFragment";

    private String username;
    public static TouchView paintView;
    public static Assessment gameObject;
    public static ClientHandler clientHandler;
    public static ServerHandler serverHandler;
    private TextView timer;
    private final long time = 60000*30;
    private final String FORMAT = "%02d:%02d:%02d";
    TimeoutDialog timeoutDialog = new TimeoutDialog();


    public TouchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle bundle = getArguments();
        gameObject = (Assessment)bundle.getSerializable("game");
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.shared_key), Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.USERNAME),"");
        clientHandler = ClientFragment.clientHandler;
        serverHandler = HostFragment.serverHandler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.touch, container, false);
        paintView = (TouchView)rootView.findViewById(R.id.paint_view);
        TextView tvCoordinates = (TextView)rootView.findViewById(R.id.tv_coordinates);
        timer = (TextView)rootView.findViewById(R.id.timer);
        paintView.setTextView(tvCoordinates);
        paintView.setOnTouchListener(touchListener);
        new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText(""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                paintView.setOnTouchListener(null);
                timer.setText("done!");
                showNoticeDialog();
            }

        }.start();

        timeoutDialog = new TimeoutDialog();
        timeoutDialog.setCancelable(false);
        timeoutDialog.setListener(new TimeoutDialog.TimeoutDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                getFragmentManager().popBackStack();
            }
        });
        return rootView;
    }

    private final View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    float mX = motionEvent.getX();
                    float mY = motionEvent.getY();
                    paintView.setCoordinates(mX,mY);
                    String msg = getJsonCoordinates(mX,mY);
                    gameObject.setCoordinate(msg);
                    sendMessage();
            }
            return true;
        }
    };

    private String getJsonCoordinates(float x,float y){
        JSONObject json = new JSONObject();
        try {
            json.put("x", x);
            json.put("y", y);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public static void updateCoordinate(){
        String input = gameObject.getCoordinate();
        try {
            JSONObject json = new JSONObject(input);
            double xD = (double) json.get("x");
            double yD = (double) json.get("y");
            float x = (float)xD;
            float y = (float)yD;
            paintView.setCoordinates(x,y);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(){
        if(clientHandler!=null){
            clientHandler.sendToServer(gameObject);
        }
        if(serverHandler!=null){
            serverHandler.sendToAll(gameObject);
        }
    }

    public void showNoticeDialog() {
        timeoutDialog.show(getFragmentManager(), "NoticeDialogFragment");
    }
}

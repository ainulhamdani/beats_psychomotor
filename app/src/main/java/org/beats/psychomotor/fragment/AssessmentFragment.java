package org.beats.psychomotor.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.beats.psychomotor.R;
import org.beats.psychomotor.dialog.TimeoutDialog;
import org.beats.psychomotor.model.Assessment;
import org.beats.psychomotor.model.Block;
import org.beats.psychomotor.model.Client;
import org.beats.psychomotor.model.ClientFinish;
import org.beats.psychomotor.utils.ClientHandler;
import org.beats.psychomotor.utils.Constants;
import org.beats.psychomotor.utils.ServerHandler;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dani on 25/02/2018.
 */

public class AssessmentFragment extends Fragment {
    private static final String TAG = "AssessmentFragment";

    private String username;
    public static Assessment gameObject;
    public static ClientHandler clientHandler;
    public static ServerHandler serverHandler;
    private TextView timer;
    private final long time = (60000*2)+1000;
    private final String FORMAT = "%02d:%02d:%02d";
    TimeoutDialog timeoutDialog = new TimeoutDialog();

    private static TextView blocks[][] = new TextView[10][10];
    private TextView showColor;
    private TextView chooseWhite;
    private TextView chooseRed;
    private TextView chooseBlue;
    private TextView chooseYellow;

    private Button finish;
    private boolean isFinish;

    private int color = Constants.Color.WHITE;

    public static FragmentActivity thisActivity;

    public static CountDownTimer countDownTimer;

    public AssessmentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = getActivity();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle bundle = getArguments();
        gameObject = (Assessment)bundle.getSerializable("game");
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.shared_key), Context.MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.USERNAME),"");
        clientHandler = ClientFragment.clientHandler;
        serverHandler = HostFragment.serverHandler;
        isFinish = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.assessment, container, false);
        timer = (TextView)rootView.findViewById(R.id.c_timer);
        showColor = (TextView)rootView.findViewById(R.id.cnow);
        chooseWhite = (TextView)rootView.findViewById(R.id.cwhite);
        chooseRed = (TextView)rootView.findViewById(R.id.cred);
        chooseBlue = (TextView)rootView.findViewById(R.id.cblue);
        chooseYellow = (TextView)rootView.findViewById(R.id.cyellow);
        finish = (Button)rootView.findViewById(R.id.finish);

        setOnclicks(rootView);

        countDownTimer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText(""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                Log.d(TAG, "onTick: time now "+millisUntilFinished);
                if(millisUntilFinished<=61000&&millisUntilFinished>60000){
                    MediaPlayer.create(getContext(), R.raw.m1).start();
                }
                if(millisUntilFinished<=31000&&millisUntilFinished>30000){
                    MediaPlayer.create(getContext(), R.raw.s30).start();
                }
                if(millisUntilFinished<=11000&&millisUntilFinished>10000){
                    MediaPlayer.create(getContext(), R.raw.s10).start();
                }
                if(millisUntilFinished<=10000&&millisUntilFinished>9000){
                    MediaPlayer.create(getContext(), R.raw.s9).start();
                }
                if(millisUntilFinished<=9000&&millisUntilFinished>8000){
                    MediaPlayer.create(getContext(), R.raw.s8).start();
                }
                if(millisUntilFinished<=8000&&millisUntilFinished>7000){
                    MediaPlayer.create(getContext(), R.raw.s7).start();
                }
                if(millisUntilFinished<=7000&&millisUntilFinished>6000){
                    MediaPlayer.create(getContext(), R.raw.s6).start();
                }
                if(millisUntilFinished<=6000&&millisUntilFinished>5000){
                    MediaPlayer.create(getContext(), R.raw.s5).start();
                }
                if(millisUntilFinished<=5000&&millisUntilFinished>4000){
                    MediaPlayer.create(getContext(), R.raw.s4).start();
                }
                if(millisUntilFinished<=4000&&millisUntilFinished>3000){
                    MediaPlayer.create(getContext(), R.raw.s3).start();
                }
                if(millisUntilFinished<=3000&&millisUntilFinished>2000){
                    MediaPlayer.create(getContext(), R.raw.s2).start();
                }
                if(millisUntilFinished<=2000&&millisUntilFinished>1000){
                    MediaPlayer.create(getContext(), R.raw.s1).start();
                }
            }

            public void onFinish() {
//                paintView.setOnTouchListener(null);
                MediaPlayer.create(getContext(), R.raw.end).start();
                timer.setText("done!");
                HostFragment.saveEndAssessment();
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
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFinish){
                    setFinish(username,false);
                    finish.setBackgroundColor(Color.parseColor("#FF0000"));
                    isFinish = false;
                }else{
                    setFinish(username,true);
                    finish.setBackgroundColor(Color.parseColor("#00FF00"));
                    isFinish = true;
                }
            }
        });
        MediaPlayer.create(getContext(), R.raw.start).start();

        return rootView;
    }

    public void showNoticeDialog() {
        timeoutDialog.show(getFragmentManager(), "NoticeDialogFragment");
    }

    public void initializeBlocks(View rootView){
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                final int x = i;
                final int y = j;
                final String id = "c".concat(String.valueOf(i).concat(String.valueOf(j)));
                int resID = getResources().getIdentifier(id, "id", getActivity().getPackageName());
                blocks[i][j] = (TextView)rootView.findViewById(resID);
                blocks[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Drawable col = getResources().getDrawable(color);
                        blocks[x][y].setBackground(col);
                        sendMessage(new Block(username,id,x,y,color));
                    }
                });
            }
        }
    }

    public void setOnclicks(View rootView){

        chooseBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = Constants.Color.BLUE;
                showColor.setBackground(getResources().getDrawable(color));
            }
        });
        chooseRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = Constants.Color.RED;
                showColor.setBackground(getResources().getDrawable(color));
            }
        });
        chooseWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = Constants.Color.WHITE;
                showColor.setBackground(getResources().getDrawable(color));
            }
        });
        chooseYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = Constants.Color.YELLOW;
                showColor.setBackground(getResources().getDrawable(color));
            }
        });

        initializeBlocks(rootView);

    }

    public static void setFinish(String username, boolean isFinish){
        if(clientHandler!=null){
            clientHandler.sendToServer(new ClientFinish(username,isFinish));
        }
        if(serverHandler!=null){
            if(isFinish){
                gameObject.getClient(username).isFinished(true);
            }else{
                gameObject.getClient(username).isFinished(false);
            }
            endSession();
        }
    }

    public static void updateBlock(Block block){
        Drawable col = thisActivity.getResources().getDrawable(block.getColor());
        blocks[block.getX()][block.getY()].setBackground(col);
    }

    public static void sendMessage(Object object){
        if(clientHandler!=null){
            clientHandler.sendToServer(object);
        }
        if(serverHandler!=null){
            HostFragment.saveBlock((Block)object);
            serverHandler.sendToAll(object);
        }
    }

    public static void endSession(){
        if(isAllFinish()){
            countDownTimer.cancel();
            HostFragment.saveEndAssessment();
            serverHandler.sendToAll("finish");
            thisActivity.getSupportFragmentManager().popBackStack();
        }
    }

    public static void endClientSession(){
        countDownTimer.cancel();
        thisActivity.getSupportFragmentManager().popBackStack();

    }

    public static boolean isAllFinish(){
        ArrayList<Client> clients = gameObject.getClients();
        for(Client client: clients) {
        }
        for(Client client: clients){
            if (!client.isFinish){
                return false;
            }
        }
        return true;
    }

}

package io.fenogy.clouddialer;

import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import android.os.PowerManager;
import android.telecom.PhoneAccountHandle;
import android.telephony.SubscriptionManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.PermissionChecker;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static io.fenogy.clouddialer.AgentHome.REQUEST_PERMISSION;
import static io.fenogy.clouddialer.App.CHANNEL_ID;


public class AgentService extends Service {

    public static String state ="";
    Firebase reference1, reference2;
    CallStateDetails mCallStateDetails;
    public static final String BROADCAST_ACTION = "io.fenogy.clouddialer.agentservice";
    Intent intent;
    public static long duration;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);

        String input = intent.getStringExtra("inputExtra");
        //x = intent.get
        Intent notificationIntent = new Intent(this, AgentHome.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Call Agent Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.green)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        mCallStateDetails = new CallStateDetails();

        mCallStateDetails.setAgent(UserDetails.username);
        mCallStateDetails.setPresence("true");
        mCallStateDetails.setDialedNumber("");
        mCallStateDetails.setDuration("");
        mCallStateDetails.setTimeRemaining("");
        mCallStateDetails.setState("available");
        state = mCallStateDetails.getState();
        updateStateToFirebase(mCallStateDetails);

        Firebase.setAndroidContext(this);
        reference1 = new Firebase(Constants.FIREBASE_URL + "/agentStatus/");

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //this gives only the updated child:user1_admin
                Map map = dataSnapshot.getValue(Map.class);
                String key = dataSnapshot.getKey();
                StringTokenizer st = new StringTokenizer(key);
                String agentName = st.nextToken("_");

                //check if the update is intended for the current agent
                if(agentName.equals(UserDetails.username)){

                    //we create the csd object from the received data
                    CallStateDetails updatedCsd = new CallStateDetails();
                    updatedCsd.setAgent(agentName);
                    updatedCsd.setPresence(map.get("presence").toString());
                    updatedCsd.setDialedNumber(map.get("dialedNumber").toString());
                    updatedCsd.setDuration(map.get("duration").toString());
                    updatedCsd.setTimeRemaining(map.get("timeRemaining").toString());
                    updatedCsd.setState(map.get("state").toString());
                    state = updatedCsd.getState();
                    mCallStateDetails = updatedCsd;
                    state = mCallStateDetails.getState();
//
//                    agentStatus.setText(updatedCsd.getAgent()  + " "+updatedCsd.getState());
//                    addMessageBox(dataSnapshot.toString(), 1);

                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                //this gives only the updated child:user1_admin
                Map map = dataSnapshot.getValue(Map.class);
                String key = dataSnapshot.getKey();
                StringTokenizer st = new StringTokenizer(key);
                String agentName = st.nextToken("_");

                //check if the update is intended for the current agent
                if(agentName.equals(UserDetails.username)){



//                    //we create the csd object from the received data
                    CallStateDetails updatedCsd = new CallStateDetails();
                    updatedCsd.setAgent(agentName);
                    updatedCsd.setPresence(map.get("presence").toString());
                    updatedCsd.setDialedNumber(map.get("dialedNumber").toString());
                    updatedCsd.setDuration(map.get("duration").toString());
                    updatedCsd.setTimeRemaining(map.get("timeRemaining").toString());
                    updatedCsd.setState(map.get("state").toString());
                    mCallStateDetails = updatedCsd;
                    state = mCallStateDetails.getState();
//                    agentStatus.setText(updatedCsd.getAgent()  + " "+updatedCsd.getState());
//
//                    addMessageBox(updatedCsd.getAll(), 1);

                    if(mCallStateDetails.getState().equals(Constants.CALL)){

                        //if(mCallStateDetails.getDialedNumber())
                        makeCall();

                    }

                    if(mCallStateDetails.getState().equals(Constants.DISCONNECT)){

                        //if(mCallStateDetails.getDialedNumber())
                        disconnectCall();

                    }
                    if(mCallStateDetails.getState().equals(Constants.WAKEUP)){
                        wakeAgentHomeRunning(getApplicationContext());
                    }


                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
//        Intent dialogIntent = new Intent(this, MyActivity.class);
//        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //This flag is a must
//        startActivity(dialogIntent);

        //do heavy work on a background thread
        //stopSelf();

        //return START_NOT_STICKY;
        //return START_REDELIVER_INTENT; //intent data can be refreshed


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        return START_NOT_STICKY;
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        String input = intent.getStringExtra("inputExtra");
//        //x = intent.get
//        Intent notificationIntent = new Intent(this, AgentHome.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, 0);
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Call Agent Service")
//                .setContentText(input)
//                .setSmallIcon(R.drawable.green)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        startForeground(1, notification);
//
//        mCallStateDetails = new CallStateDetails();
//
//        mCallStateDetails.setAgent(UserDetails.username);
//        mCallStateDetails.setPresence("true");
//        mCallStateDetails.setDialedNumber("");
//        mCallStateDetails.setDuration("");
//        mCallStateDetails.setTimeRemaining("");
//        mCallStateDetails.setState("available");
//        state = mCallStateDetails.getState();
//        updateStateToFirebase(mCallStateDetails);
//
//        Firebase.setAndroidContext(this);
//        reference1 = new Firebase(Constants.FIREBASE_URL + "/agentStatus/");
//
//        reference1.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                //this gives only the updated child:user1_admin
//                Map map = dataSnapshot.getValue(Map.class);
//                String key = dataSnapshot.getKey();
//                StringTokenizer st = new StringTokenizer(key);
//                String agentName = st.nextToken("_");
//
//                //check if the update is intended for the current agent
//                if(agentName.equals(UserDetails.username)){
//
//                    //we create the csd object from the received data
//                    CallStateDetails updatedCsd = new CallStateDetails();
//                    updatedCsd.setAgent(agentName);
//                    updatedCsd.setPresence(map.get("presence").toString());
//                    updatedCsd.setDialedNumber(map.get("dialedNumber").toString());
//                    updatedCsd.setDuration(map.get("duration").toString());
//                    updatedCsd.setTimeRemaining(map.get("timeRemaining").toString());
//                    updatedCsd.setState(map.get("state").toString());
//                    state = updatedCsd.getState();
////
////                    agentStatus.setText(updatedCsd.getAgent()  + " "+updatedCsd.getState());
////                    addMessageBox(dataSnapshot.toString(), 1);
//
//                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
//
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                //this gives only the updated child:user1_admin
//                Map map = dataSnapshot.getValue(Map.class);
//                String key = dataSnapshot.getKey();
//                StringTokenizer st = new StringTokenizer(key);
//                String agentName = st.nextToken("_");
//
//                //check if the update is intended for the current agent
//                if(agentName.equals(UserDetails.username)){
//
//
//
////                    //we create the csd object from the received data
//                    CallStateDetails updatedCsd = new CallStateDetails();
//                    updatedCsd.setAgent(agentName);
//                    updatedCsd.setPresence(map.get("presence").toString());
//                    updatedCsd.setDialedNumber(map.get("dialedNumber").toString());
//                    updatedCsd.setDuration(map.get("duration").toString());
//                    updatedCsd.setTimeRemaining(map.get("timeRemaining").toString());
//                    updatedCsd.setState(map.get("state").toString());
//                    mCallStateDetails = updatedCsd;
//                    state = mCallStateDetails.getState();
////                    agentStatus.setText(updatedCsd.getAgent()  + " "+updatedCsd.getState());
////
////                    addMessageBox(updatedCsd.getAll(), 1);
//
//                    if(mCallStateDetails.getState().equals(Constants.CALL)){
//
//                        //if(mCallStateDetails.getDialedNumber())
//                        makeCall();
//
//                    }
//
//                    if(mCallStateDetails.getState().equals(Constants.DISCONNECT)){
//
//                        //if(mCallStateDetails.getDialedNumber())
//                        disconnectCall();
//
//                    }
//                    if(mCallStateDetails.getState().equals(Constants.WAKEUP)){
//                        wakeAgentHomeRunning(getApplicationContext());
//                    }
//
//
//                }
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
////        Intent dialogIntent = new Intent(this, MyActivity.class);
////        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //This flag is a must
////        startActivity(dialogIntent);
//
//        //do heavy work on a background thread
//        //stopSelf();
//
//        return START_NOT_STICKY;
//        //return START_REDELIVER_INTENT; //intent data can be refreshed
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CallStateDetails updatedCsd = new CallStateDetails();
        updatedCsd.setAgent(UserDetails.username);
        updatedCsd.setPresence("false");
        updatedCsd.setDialedNumber("");
        updatedCsd.setDuration("");
        updatedCsd.setTimeRemaining("");
        updatedCsd.setState(Constants.REGISTERED);
        mCallStateDetails = updatedCsd;
        state = mCallStateDetails.getState();
        updateStateToFirebase(updatedCsd);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void updateStateToFirebase(CallStateDetails csd){

        Map<String, String> map = new HashMap<String, String>();
        map.put("presence", csd.getPresence());
        map.put("dialedNumber", csd.getDialedNumber());
        map.put("duration", "");
        map.put("state", csd.getState());
        map.put("timeRemaining", "");

        Firebase.setAndroidContext(this);
        reference2 = new Firebase(Constants.FIREBASE_URL + "/agentStatus/");
        //reference1.push().child(UserDetails.username +"_Status").setValue(map);
        //reference1.push().setValue(map);
        reference2.child( csd.getAgent() + "_" + "admin").setValue(map);

        //addMessageBox(mCallStateDetails.getAll(), 0);
        //agentStatus.setText(mCallStateDetails.getState());
    }

    public void makeCall(){

//        Intent intent = new Intent(getApplicationContext(), AgentHome.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        getApplicationContext().startActivity(intent);
//

        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,1000);
        PowerManager.WakeLock screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "CallAgent:OnRequest");
        screenLock.acquire();


        //makeCallDirect();
//later
        screenLock.release();
        try{
            Thread.sleep(800);
        }catch(Exception e){

        }

        intent.putExtra("username", mCallStateDetails.getAgent());
        intent.putExtra("number", mCallStateDetails.getDialedNumber());
        intent.putExtra("duration", mCallStateDetails.getDuration());
        intent.putExtra("state", mCallStateDetails.getState());
        sendBroadcast(intent);
    }

    private void wakeAgentHomeRunning(Context ctx){

        if(!isRunning(ctx)){

              UserDetails.username = mCallStateDetails.getAgent();
//            CallStateDetails updatedCsd = new CallStateDetails();
//            updatedCsd.setAgent(UserDetails.username);
//            updatedCsd.setPresence("true");
//            updatedCsd.setDialedNumber("");
//            updatedCsd.setDuration("");
//            updatedCsd.setTimeRemaining("");
//            updatedCsd.setState(Constants.REGISTERED);
//            mCallStateDetails = updatedCsd;
//            state = mCallStateDetails.getState();
//            updateStateToFirebase(updatedCsd);
            //intent.putExtra("username", mCallStateDetails.getAgent());
            startActivity(new Intent(AgentService.this, AgentHome.class));
        }


    }
    public boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }
//    private final void makeCallDirect() {
//
//        if (PermissionChecker.checkSelfPermission(AgentService.this, "android.permission.CALL_PHONE") == REQUEST_PERMISSION) {
//
//            int oddEven = 0;
//
//            if(mCallStateDetails.getDuration() != null && mCallStateDetails.getDuration() != "" ){
//
//                try{
//
//                    oddEven = Integer.getInteger(mCallStateDetails.getDuration()) % 2;
//
//
//                }catch(Exception e){
//
//
//                }
//
//            }
//
//            final Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCallStateDetails.getDialedNumber()));
//            //final int simSlotIndex = 1; //Second sim slot
//            final int simSlotIndex = oddEven; //sim slot by odd even duration
//
//            try {
//                final Method getSubIdMethod = SubscriptionManager.class.getDeclaredMethod("getSubId", int.class);
//                getSubIdMethod.setAccessible(true);
//                final long subIdForSlot = ((long[]) getSubIdMethod.invoke(SubscriptionManager.class, simSlotIndex))[0];
//
//                final ComponentName componentName = new ComponentName("com.android.phone", "com.android.services.telephony.TelephonyConnectionService");
//                final PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(componentName, String.valueOf(subIdForSlot));
//                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandle);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            if(mCallStateDetails.getDuration() != null && mCallStateDetails.getDuration() != "" ){
//
//                try {
//                    duration = Long.parseLong(mCallStateDetails.getDuration());
//                }catch(Exception e){
//
//                }
//            }else{
//                duration =30;
//            }
//            mCallStateDetails.setState(Constants.ATTEMPT);
//            updateStateToFirebase(mCallStateDetails);
//            startActivity(intent);
//        }
//
//    }
    public void disconnectCall(){

        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 50);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,500);
//        PowerManager.WakeLock screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
//                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "CallAgent:OnRequest");
//        screenLock.acquire();
//
////later
//        screenLock.release();
        try{
            Thread.sleep(100);
        }catch(Exception e){

        }
        intent.putExtra("username", mCallStateDetails.getAgent());
        intent.putExtra("number", mCallStateDetails.getDialedNumber());
        intent.putExtra("duration", mCallStateDetails.getDuration());
        //intent.putExtra("state", mCallStateDetails.getState());
        intent.putExtra("state", Constants.DISCONNECT);
        sendBroadcast(intent);
    }



}

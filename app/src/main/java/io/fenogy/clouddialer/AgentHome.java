package io.fenogy.clouddialer;

import android.app.Activity;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

//public class AgentHome extends AppCompatActivity
public class AgentHome extends AppCompatActivity {

    static final int REQUEST_PERMISSION = 0;

    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    ImageView stopButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    TextView agentStatus;
    CallStateDetails mCallStateDetails;
    private final Handler handler = new Handler();

    public static long duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_home);

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        agentStatus = (TextView)findViewById(R.id.tvStatusAgent);
        stopButton = (ImageView)findViewById(R.id.imageViewStopService);
        agentStatus.setText(UserDetails.username +" Available");

        mCallStateDetails = new CallStateDetails();

        mCallStateDetails.setAgent(UserDetails.username);
        mCallStateDetails.setPresence("true");
        mCallStateDetails.setDialedNumber("");
        mCallStateDetails.setDuration("");
        mCallStateDetails.setTimeRemaining("0");
        mCallStateDetails.setState("available");


        offerReplacingDefaultDialer();
        startService();


       updateStateToFirebase(mCallStateDetails);
//
//        Firebase.setAndroidContext(this);
//        //reference1 = new Firebase(Constants.FIREBASE_URL + "/agentStatus/" + UserDetails.username + "_" + "admin");
//        reference1 = new Firebase(Constants.FIREBASE_URL + "/agentStatus/");
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopService();
                finish();
            }
        });
        agentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(mCallStateDetails.getState().equals(Constants.CALL) || mCallStateDetails.getState().equals(Constants.CONNECTED) ){
//
//                    mCallStateDetails.setDialedNumber("");
//                    mCallStateDetails.setDuration("");
//                    mCallStateDetails.setTimeRemaining("0");
//                    mCallStateDetails.setState(Constants.DISCONNECT);
//                    updateStateToFirebase(mCallStateDetails);
//
//                }
//                String messageText = messageArea.getText().toString();
//
//                if(!messageText.equals("")){
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put("message", messageText);
//                    map.put("user", UserDetails.username);
//                    reference1.push().child(UserDetails.username +"_Status").setValue(map);
//                    //reference1.push().setValue(map);
//                    //reference2.push().setValue(map);
//                    messageArea.setText("");
//                }
            }
        });

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
//
//                    agentStatus.setText(updatedCsd.getAgent()  + " "+updatedCsd.getState());
//                    addMessageBox(dataSnapshot.toString(), 1);
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
//                    //we create the csd object from the received data
//                    CallStateDetails updatedCsd = new CallStateDetails();
//                    updatedCsd.setAgent(agentName);
//                    updatedCsd.setPresence(map.get("presence").toString());
//                    updatedCsd.setDialedNumber(map.get("dialedNumber").toString());
//                    updatedCsd.setDuration(map.get("duration").toString());
//                    updatedCsd.setTimeRemaining(map.get("timeRemaining").toString());
//                    updatedCsd.setState(map.get("state").toString());
//                    mCallStateDetails = updatedCsd;
//                    agentStatus.setText(updatedCsd.getAgent()  + " "+updatedCsd.getState());
//
//                    addMessageBox(updatedCsd.getAll(), 1);
//
//                    if(mCallStateDetails.getState().equals(Constants.CALL)){
//
//                        //if(mCallStateDetails.getDialedNumber())
//                        makeCall();
//                    }
//
//
//                }
//
//
//
//
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
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter(AgentService.BROADCAST_ACTION));
                if(mCallStateDetails.getState().equals(Constants.ATTEMPT) || mCallStateDetails.getState().equals(Constants.CONNECTED) || mCallStateDetails.getState().equals(Constants.REGISTERED)){

            mCallStateDetails.setState(Constants.AVAILABLE);
            mCallStateDetails.setDialedNumber("");
            mCallStateDetails.setDuration("");
            mCallStateDetails.setPresence("true");
            updateStateToFirebase(mCallStateDetails);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(AgentService.BROADCAST_ACTION));

        //This is not working on some phones
//later this has been changed to update the state when call is disconnected and not shown as available
        if(mCallStateDetails.getState().equals(Constants.DISCONNECT) || mCallStateDetails.getState().equals(Constants.CONNECTED) || mCallStateDetails.getState().equals(Constants.REGISTERED)){

            mCallStateDetails.setState(Constants.AVAILABLE);
            mCallStateDetails.setDialedNumber("");
            mCallStateDetails.setDuration("");
            mCallStateDetails.setPresence("true");

            updateStateToFirebase(mCallStateDetails);

        }
        registerReceiver(broadcastReceiver, new IntentFilter(AgentService.BROADCAST_ACTION));
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        CallStateDetails updatedCsd = new CallStateDetails();
        updatedCsd.setAgent(UserDetails.username);
        updatedCsd.setPresence("false");
        updatedCsd.setDialedNumber("");
        updatedCsd.setDuration("");
        updatedCsd.setTimeRemaining("");
        updatedCsd.setState(Constants.REGISTERED);
        updateStateToFirebase(updatedCsd);
    }



    public void startService() {
        String input = mCallStateDetails.getDialedNumber();

        Intent serviceIntent = new Intent(this, AgentService.class);
        serviceIntent.putExtra("inputExtra", input);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, AgentService.class);
        stopService(serviceIntent);
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(AgentHome.this);
        //textView.settext

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            textView.setText("from admin:\r\n" +message);
            lp2.gravity = Gravity.LEFT;
        }
        else{
            textView.setText("me:\r\n" +message);
            lp2.gravity = Gravity.RIGHT;
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public void updateStateToFirebase(CallStateDetails csd){

        Map<String, String> map = new HashMap<String, String>();
        map.put("presence", csd.getPresence());
        map.put("dialedNumber", csd.getDialedNumber());
        map.put("duration", csd.getDuration());
        map.put("state", csd.getState());
        map.put("timeRemaining", csd.getTimeRemaining());

        Firebase.setAndroidContext(this);
        reference2 = new Firebase(Constants.FIREBASE_URL + "/agentStatus/");
        //reference1.push().child(UserDetails.username +"_Status").setValue(map);
        //reference1.push().setValue(map);
        reference2.child( csd.getAgent() + "_" + "admin").setValue(map);

//        reference2.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
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
        addMessageBox(mCallStateDetails.getAll(), 0);
        agentStatus.setText(mCallStateDetails.getState());
    }

    private final void makeCall() {
        if (PermissionChecker.checkSelfPermission(AgentHome.this, "android.permission.CALL_PHONE") == REQUEST_PERMISSION) {

            //Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", mCallStateDetails.getDialedNumber(), null));

            int oddEven = 0;
//            view = new StringBuilder("tel:");
//            view.append(DialActivity.this.txt_input_text.getText());
//            view = Uri.parse(view.toString());

            if(mCallStateDetails.getDuration() != null && mCallStateDetails.getDuration() != "" ){

                try{

                    oddEven = Integer.getInteger(mCallStateDetails.getDuration()) % 2;


                }catch(Exception e){


                }

            }

            final Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCallStateDetails.getDialedNumber()));
            //final int simSlotIndex = 1; //Second sim slot
            final int simSlotIndex = oddEven; //sim slot by odd even duration

            try {
                final Method getSubIdMethod = SubscriptionManager.class.getDeclaredMethod("getSubId", int.class);
                getSubIdMethod.setAccessible(true);
                final long subIdForSlot = ((long[]) getSubIdMethod.invoke(SubscriptionManager.class, simSlotIndex))[0];

                final ComponentName componentName = new ComponentName("com.android.phone", "com.android.services.telephony.TelephonyConnectionService");
                final PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(componentName, String.valueOf(subIdForSlot));
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandle);
            } catch (Exception e) {
                e.printStackTrace();
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //startActivity(intent);


//            Intent intent = new Intent("android.intent.action.CALL");
//            intent.putExtra("com.android.phone.extra.slot", 0);
//            intent.putExtra("simSlot", 0);
//            intent.setData(Uri.fromParts("tel", mCallStateDetails.getDialedNumber(), null));
//
            if(mCallStateDetails.getDuration() != null && mCallStateDetails.getDuration() != "" ){

                try {
                    duration = Long.parseLong(mCallStateDetails.getDuration());
                }catch(Exception e){

                }
            }else{
                duration =30;
            }
            mCallStateDetails.setState(Constants.ATTEMPT);
            updateStateToFirebase(mCallStateDetails);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(AgentHome.this, new String[]{"android.permission.CALL_PHONE"}, REQUEST_PERMISSION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        Intrinsics.checkParameterIsNotNull(permissions, "permissions");
        Intrinsics.checkParameterIsNotNull(grantResults, "grantResults");
        if (requestCode == 0 && ArraysKt.contains(grantResults, 0)) {
            this.makeCall();
        }
    }

    private final void offerReplacingDefaultDialer() {
        Object var10000 = this.getSystemService(TelecomManager.class);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "getSystemService(TelecomManager::class.java)");
        if (Intrinsics.areEqual(((TelecomManager)var10000).getDefaultDialerPackage(), this.getPackageName()) ^ true) {
            Intent var1 = (new Intent("android.telecom.action.CHANGE_DEFAULT_DIALER")).putExtra("android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME", this.getPackageName());
            AgentHome.this.startActivity(var1);
        }

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            mCallStateDetails.setDialedNumber(intent.getExtras().getString("number"));
            mCallStateDetails.setDuration(intent.getExtras().getString("duration"));
            mCallStateDetails.setState(intent.getExtras().getString("state"));
            if(mCallStateDetails.getState().equals(Constants.CALL)) {
                makeCall();
            }
//              mCallStateDetails = updatedCsd;
//              agentStatus.setText(updatedCsd.getAgent()  + " "+updatedCsd.getState());
//
//              addMessageBox(updatedCsd.getAll(), 1);
//
//               if(mCallStateDetails.getState().equals(Constants.CALL)){
//
//                        //if(mCallStateDetails.getDialedNumber())
//                        makeCall();
//              }
        }
    };


}

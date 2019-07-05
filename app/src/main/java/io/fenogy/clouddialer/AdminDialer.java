package io.fenogy.clouddialer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

//public class AdminDialer extends AppCompatActivity
public class AdminDialer extends AppCompatActivity {

    RecyclerView mAgentListView;
    ProgressDialog pd;
    ArrayList<String> rawJsonData = new ArrayList<>();
    ArrayList<CallStateDetails> mAgentCallStates = new ArrayList<>();
    AgentAdapter mAgentAdapter;
    int totalUsers = 0;
    Firebase reference1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dialer);

        mAgentListView = (RecyclerView)findViewById(R.id.agentList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        //llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setOrientation(RecyclerView.VERTICAL);
        mAgentListView.setLayoutManager(llm);
        mAgentAdapter = new AgentAdapter();
        mAgentListView.setAdapter(mAgentAdapter);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mAgentListView.getContext(),
//                llm.getOrientation());
//        mAgentListView.addItemDecoration(dividerItemDecoration);

        pd = new ProgressDialog(AdminDialer.this);
        pd.setMessage("Loading...");
        pd.show();

        String url = Constants.FIREBASE_URL + "/agentStatus.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(AdminDialer.this);
        rQueue.add(request);

        Firebase.setAndroidContext(this);
        //reference1 = new Firebase(Constants.FIREBASE_URL + "/agentStatus/" + UserDetails.username + "_" + "admin");
        reference1 = new Firebase(Constants.FIREBASE_URL + "/agentStatus");

        reference1.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                //this gives only the updated child:user1_admin
                Map map = dataSnapshot.getValue(Map.class);
                String key = dataSnapshot.getKey();
                StringTokenizer st = new StringTokenizer(key);
                String agentName = st.nextToken("_");

                //we create the csd object from the received data
                CallStateDetails updatedCsd = new CallStateDetails();
                updatedCsd.setAgent(agentName);
                updatedCsd.setPresence(map.get("presence").toString());
                updatedCsd.setDialedNumber(map.get("dialedNumber").toString());
                updatedCsd.setDuration(map.get("duration").toString());
                updatedCsd.setTimeRemaining(map.get("timeRemaining").toString());
                updatedCsd.setState(map.get("state").toString());

                //find the right item in the arraylist and update with new values
                for(int x = 0; x < mAgentCallStates.size(); x++ ) {

                    //CallStateDetails nextCsd = (CallStateDetails)i.next();
                    if(mAgentCallStates.get(x).getAgent().equals(updatedCsd.getAgent())){

                        mAgentCallStates.set(x,updatedCsd);
                        mAgentAdapter.notifyDataSetChanged();
                        break;

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


//        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                UserDetails.chatWith = al.get(position);
//                startActivity(new Intent(Users.this, Chat.class));
//            }
//        });
    }

    public void doOnSuccess(String s){

        //In here we should create the ArrayList of agents
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            //Decompose the Json object level by level
            //FullObject-->multipleAgent state JSON object-->each staus as a key value pair
            //each staus as a key value pair-->CallStateObject field-->Callstate object-->ArrayLIst

            while(i.hasNext()) {

                if (!key.startsWith("_")) { //To Prevent "_admin" type of wrong status get in to the list

                    key = i.next().toString();

                    rawJsonData.add(key);
                    totalUsers++;
                    try {
                        //key = username_admin
                        JSONObject agentStatus = obj.getJSONObject(key);

                        CallStateDetails csd = new CallStateDetails();
                        StringTokenizer st = new StringTokenizer(key);
                        csd.setAgent(st.nextToken("_"));

                        try {
                            csd.setPresence(agentStatus.getString("presence"));
                            csd.setDialedNumber(agentStatus.getString("dialedNumber"));
                            csd.setDuration(agentStatus.getString("duration"));
                            csd.setTimeRemaining(agentStatus.getString("timeRemaining"));
                            csd.setState(agentStatus.getString("state"));
                            mAgentCallStates.add(csd);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        pd.dismiss();
        mAgentAdapter.notifyDataSetChanged();

    }



    //////////View creation///////////
    private class AgentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CheckBox mCheckBox;
        private TextView mAgent;
        private EditText mDialedNumber;
        private EditText mDuration;
        private TextView mRemDuration;
        private ImageView mDial;
        private ImageView mCut;


        public AgentViewHolder(View itemView) {
            super(itemView);

            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            mAgent = (TextView) itemView.findViewById(R.id.tvAgent);
            mDial = (ImageView) itemView.findViewById(R.id.imDial);
            mCut = (ImageView) itemView.findViewById(R.id.imCut);
            mDialedNumber = (EditText) itemView.findViewById(R.id.etDialedNumber);
            mDuration = (EditText) itemView.findViewById(R.id.etDuration);
            mRemDuration = (TextView) itemView.findViewById(R.id.etRemDuration);

//            mDial.setClickable(true);
////            mDial.setOnClickListener(this);
////            mCut.setClickable(true);
////            mCut.setOnClickListener(this);

            mDial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(AdminDialer.this, "Clicked Dial on" + String.valueOf(getAdapterPosition()), Toast.LENGTH_LONG).show();
                    CallStateDetails sendToAgent = mAgentCallStates.get(getAdapterPosition());
                    if(sendToAgent != null){

                        sendToAgent.setDialedNumber(mDialedNumber.getText().toString());
                        sendToAgent.setDuration(mDuration.getText().toString());

                        if(mDuration.getText().toString().equals("0")){
                            sendToAgent.setState(Constants.WAKEUP);
                            Toast.makeText(AdminDialer.this, "Clicked Wake on " + String.valueOf(getAdapterPosition()), Toast.LENGTH_LONG).show();
                        }else{
                            sendToAgent.setState(Constants.CALL);
                            Toast.makeText(AdminDialer.this, "Clicked Call from " + String.valueOf(getAdapterPosition()), Toast.LENGTH_LONG).show();
                        }


                        updateStateToFirebase(sendToAgent);

                    }
                }
            });

            mCut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(AdminDialer.this, "Clicked Cut on " + String.valueOf(getAdapterPosition()), Toast.LENGTH_LONG).show();
                    CallStateDetails sendToAgent = mAgentCallStates.get(getAdapterPosition());
                    if(sendToAgent != null){

                        sendToAgent.setDialedNumber("");
                        sendToAgent.setDuration("");
                        sendToAgent.setState("disconnect");
                        updateStateToFirebase(sendToAgent);

                    }
                }
            });

        }

        public void setupAgentStausDetails(CallStateDetails csd) {

            mAgent.setText(csd.getAgent());
            mDialedNumber.setText(csd.getDialedNumber());
            mDuration.setText(csd.getDuration());
            mRemDuration.setText(csd.getTimeRemaining());

            if(csd.getPresence().equals("true")
                    && (csd.getState().equals(Constants.DISCONNECT) || csd.getState().equals(Constants.AVAILABLE)) ) {

                mDial.setImageResource(R.drawable.green);
                mDial.setClickable(true);

            }else if(csd.getPresence().equals("false")){

                mDial.setImageResource(R.drawable.gray);
                //mDial.setClickable(false);
            }else if(csd.getState().equals(Constants.CONNECTED)||csd.getState().equals(Constants.ATTEMPT) ){

                mDial.setImageResource(R.drawable.orange);
                mDial.setClickable(false);
            }else{


            }

            mCut.setImageResource(R.drawable.red);


        }

        @Override
        public void onClick(View v) {
            int i = getAdapterPosition();

        }


    }

    private final class AgentAdapter extends RecyclerView.Adapter {

        @Override
        public int getItemCount() {

            return mAgentCallStates.size();
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context ctx = viewGroup.getContext();

            View view = LayoutInflater.from(ctx)
                    .inflate(R.layout.agent_list_item_cardview_new, viewGroup, false);
            return new AgentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

            CallStateDetails csd = mAgentCallStates.get(i);
            AgentViewHolder holder = (AgentViewHolder) viewHolder;
            holder.setupAgentStausDetails(csd);

        }
    }

    public void updateStateToFirebase(CallStateDetails csd){

        Map<String, String> map = new HashMap<String, String>();
        map.put("presence", csd.getPresence());
        map.put("dialedNumber", csd.getDialedNumber());
        map.put("duration", csd.getDuration());
        map.put("state", csd.getState());
        map.put("timeRemaining", csd.getTimeRemaining());

        reference1 = new Firebase(Constants.FIREBASE_URL + "/agentStatus/");
        //reference1.push().child(UserDetails.username +"_Status").setValue(map);
        //reference1.push().setValue(map);
        reference1.child( csd.getAgent() + "_" + "admin").setValue(map);

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
    }



}

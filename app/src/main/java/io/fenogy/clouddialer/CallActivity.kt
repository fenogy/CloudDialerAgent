package io.fenogy.clouddialer

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
//import android.support.v7.app.AppCompatActivity
import android.telecom.Call
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_call.*
import java.util.concurrent.TimeUnit

class CallActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private lateinit var number: String
    private lateinit var duration: String
    private var durationMilis: Long = 0
    var receiver: BroadcastReceiver? = null
    private lateinit var mHandler: Handler
    //private lateinit var mRunnable:Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        number = intent.data.schemeSpecificPart

        durationMilis = AgentHome.duration *1000;
        //durationMilis = AgentService.duration *1000;

        mHandler = Handler()

    }

    override fun onResume() {
        super.onResume()

        val filter = IntentFilter()
        filter.addAction("io.fenogy.clouddialer")
        filter.addAction(AgentService.BROADCAST_ACTION)
        registerReceiver(broadCastReceiver, filter)

    }

    override fun onStart() {
        super.onStart()

        val filter = IntentFilter()
        filter.addAction("io.fenogy.clouddialer")
        filter.addAction(AgentService.BROADCAST_ACTION)
        //receiver = OnGoingReceiver()
        //registerReceiver(receiver, filter)
        registerReceiver(broadCastReceiver, filter)


        answer.setOnClickListener {
            OngoingCall.answer()
        }

        hangup.setOnClickListener {
            OngoingCall.hangup()
            mHandler.removeCallbacks(mRunnable)
        }

        //OngoingCall.state.u
        OngoingCall.state.subscribe(::predefDisconnect).addTo(disposables)
        OngoingCall.state.subscribe()
        OngoingCall.state
            .subscribe(::updateUi)
            .addTo(disposables)

        OngoingCall.state
            .filter { it == Call.STATE_DISCONNECTED }
            .delay(1, TimeUnit.SECONDS)
            .firstElement()
            .subscribe { finish() }
            .addTo(disposables)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(state: Int) {
        //state.toString()
        callInfo.text = "${state.asString().toLowerCase().capitalize()}\n$number"
        //callInfo.text = "${state.toString().toLowerCase().capitalize()}\n$number"

        answer.isVisible = state == Call.STATE_RINGING
        hangup.isVisible = state in listOf(
            Call.STATE_DIALING,
            Call.STATE_RINGING,
            Call.STATE_ACTIVE
        )

//        if( state in listOf(Call.STATE_DIALING,Call.STATE_RINGING,Call.STATE_ACTIVE)){
////
////            val r = Runnable {
////                //OngoingCall.hangup()
////                finish()
////
////            }
//            Handler().postDelayed({finish()}, 1000)
//        }
    }

    @SuppressLint("SetTextI18n")
    private fun predefDisconnect(state: Int) {


        if( state in listOf(Call.STATE_ACTIVE)){

            //Handler().postDelayed({OngoingCall.hangup()}, durationMilis)
            //mHandler.postDelayed({OngoingCall.hangup()}, durationMilis)
            mHandler.postDelayed(mRunnable, durationMilis)
        }
    }
    val mRunnable: Runnable = Runnable {
        // do my stuff

        //if(OngoingCall.state.hasValue() )
        OngoingCall.hangup()

    }


    override fun onStop() {
        super.onStop()
        disposables.clear()
        unregisterReceiver(broadCastReceiver)
    }




    companion object {
        fun start(context: Context, call: Call) {
            Intent(context, CallActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.details.handle)
                .let(context::startActivity)
        }
    }

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent) {

            if(intent.extras!!.getString("state").equals(Constants.DISCONNECT)) {

                OngoingCall.hangup()
                mHandler.removeCallbacks(mRunnable)
            }

        }
    }


}

//class OnGoingReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        Toast.makeText(context, "Broadcast Intent Detected.",
//            Toast.LENGTH_LONG).show()
//
//        if(intent.extras!!.getString("state").equals(Constants.DISCONNECT))
//        OngoingCall.hangup()
//    }
//}

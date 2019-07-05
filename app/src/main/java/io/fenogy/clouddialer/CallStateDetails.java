package io.fenogy.clouddialer;

public class CallStateDetails {

    private static final String TOKEN = "|";


    private String agent = "";
    private String presence = "";
    private String dialedNumber = "";
    private String duration = "";
    private String state = "";
    private String timeRemaining = "";



    public CallStateDetails(){


    }

    public CallStateDetails(String s){


    }

    public CallStateDetails(String presence,String dialedNumber, String duration, String state, String timeRemaining ){

        this.presence =  presence;
        this.dialedNumber =  dialedNumber;
        this.duration =  duration;
        this.state =  state;
        this.timeRemaining =  timeRemaining;

    }

    public String getAll(){

        return (presence + TOKEN + dialedNumber + TOKEN + duration + TOKEN + state + TOKEN + timeRemaining);
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String user) {
        this.agent = user;
    }

    public String getPresence() {
        return presence;
    }

    public void setPresence(String presence) {
        this.presence = presence;
    }

    public String getDialedNumber() {
        return dialedNumber;
    }

    public void setDialedNumber(String dialedNumber) {
        this.dialedNumber = dialedNumber;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(String timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

}

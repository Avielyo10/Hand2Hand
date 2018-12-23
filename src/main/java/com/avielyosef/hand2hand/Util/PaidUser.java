package com.avielyosef.hand2hand.Util;

//import com.avielyosef.hand2hand.DB.MyDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Upgraded User
 */
public class PaidUser extends User {
    private int numOfDays;
    private String lastDay;

    public PaidUser(){}
    public PaidUser(String name, String email, int id, String phoneNum, ArrayList<Ad> myAds, int numOfDays) {
        super(name, email, id, phoneNum, myAds);
        this.numOfDays = numOfDays;
        lastDay = getTheLastPaidDay(this.numOfDays);
        setPaidUser(true);
    }

    public PaidUser(RegUser regUser, int numOfDays){
        this(regUser.getName(),regUser.getEmail(),regUser.getId(),regUser.getPhoneNum(),regUser.getMyAds(),numOfDays);
    }

    public RegUser downgradeUser(){
//        MyDB.Users.remove(this);
        return new RegUser(this);
    }

    @Override
    public String toString() {
        return "PaidUser{" +
                "name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", id=" + getId() +
                ", phoneNum=" + getPhoneNum() +
                ", myAds=" + getMyAds() +
                '}';
    }

    public String getTheLastPaidDay(int numOfDays){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, numOfDays);
        return sdf.format(cal.getTime());
    }

    public int getNumOfDays() {
        return numOfDays;
    }

    public void setNumOfDays(int numOfDays) {
        this.numOfDays = numOfDays;
    }

    public String getLastDay() {
        return lastDay;
    }

    public void setLastDay(String lastDay) {
        this.lastDay = lastDay;
    }
}

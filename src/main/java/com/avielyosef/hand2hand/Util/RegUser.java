package com.avielyosef.hand2hand.Util;

//import com.avielyosef.hand2hand.DB.MyDB;

import java.util.ArrayList;

/**
 * Regular User
 */
public class RegUser extends User {
    private String lastDay;

    public RegUser(){}
    public RegUser(String name, String email, int id, String phoneNum, ArrayList<Ad> myAds) {
        super(name, email, id, phoneNum, myAds);
    }

    public RegUser(PaidUser paidUser){
        this(paidUser.getName(),paidUser.getEmail(),paidUser.getId(),paidUser.getPhoneNum(),paidUser.getMyAds());
    }

    public PaidUser upgradeUser(int numOfDays){
//        MyDB.Users.remove(this);
        return new PaidUser(this,numOfDays);
    }
    public String getLastDay() {
        return lastDay;
    }

    public void setLastDay(String lastDay) {
        this.lastDay = lastDay;
    }
}

package com.avielyosef.hand2hand.Util;

//import com.avielyosef.hand2hand.DB.MyDB;

import java.util.ArrayList;

/**
 * User on the system
 */
public abstract class User {
    private String name;
    private String email;
    private int id;
    private String phoneNum;
    private ArrayList<Ad> myAds;
    private boolean isPaidUser;

    public User(){}

    public User(String name, String email, int id, String phoneNum, ArrayList<Ad> myAds) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.phoneNum = phoneNum;
        myAds  = new ArrayList<Ad>();
        this.myAds = myAds;
        this.isPaidUser = false;
//        pushToDB();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public ArrayList<Ad> getMyAds() { return myAds; }

    public void setMyAds(ArrayList<Ad> myAds) { this.myAds = myAds; }

    @Override
    public String toString() {
        return "RegUser{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", id=" + id +
                ", phoneNum=" + phoneNum +
                ", myAds=" + myAds +
                '}';
    }

//    public void pushToDB(){
//        MyDB.Users.add(this);
//    }

//    public void createNewAd(Ad ad){
//        if(!myAds.contains(ad)){
//            myAds.add(ad);
//            MyDB.Ads.add(ad);
//        }
//    }

//    public void deleteAd(Ad ad){
//        if(myAds.contains(ad)){
//            myAds.remove(ad);
//            MyDB.Ads.remove(ad);
//        }
//    }

    public boolean isPaidUser() {
        return isPaidUser;
    }

    public void setPaidUser(boolean paidUser) {
        isPaidUser = paidUser;
    }
}

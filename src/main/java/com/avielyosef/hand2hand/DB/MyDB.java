package com.avielyosef.hand2hand.DB;

import com.avielyosef.hand2hand.Util.Ad;
import com.avielyosef.hand2hand.Util.User;

import java.util.ArrayList;

public class MyDB {
    public static ArrayList<Ad> Ads = new ArrayList<>();
    public static ArrayList<User> Users = new ArrayList<User>();

    public static void printAds(){
        for (int i = 0; i < Ads.size(); ++i) System.out.println(Ads.get(i));
    }

    public static void printUsers(){
        for (int i = 0; i < Users.size(); ++i) System.out.println(Users.get(i));
    }

    public static void clearUsers(){
        Users.clear();
    }

    public static void clearAds(){
        Ads.clear();
    }

}

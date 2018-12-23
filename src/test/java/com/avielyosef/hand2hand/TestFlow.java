package com.avielyosef.hand2hand;


import com.avielyosef.hand2hand.DB.MyDB;
import com.avielyosef.hand2hand.Util.Ad;
import com.avielyosef.hand2hand.Util.PaidUser;
import com.avielyosef.hand2hand.Util.RegUser;
import com.avielyosef.hand2hand.Util.User;

import org.junit.*;


public class TestFlow {

    @Test
    public void upgradeAndDowngradeUsers(){
        User Aviel = new RegUser("Aviel","ayosef",308213198,"0509067205",null);
        Assert.assertTrue(Aviel instanceof RegUser);
        System.out.println("isPaidUser = "+ Aviel.isPaidUser());

        Aviel = ((RegUser) Aviel).upgradeUser(30);
        Assert.assertFalse(Aviel instanceof RegUser);
        System.out.println("isPaidUser = "+ Aviel.isPaidUser());
        System.out.println("number of days until return to RegUser = "+ ((PaidUser) Aviel).getNumOfDays());
        System.out.println("At = "+ ((PaidUser) Aviel).getLastDay());

        Aviel = ((PaidUser) Aviel).downgradeUser();
        Assert.assertTrue(Aviel instanceof RegUser);
        System.out.println("isPaidUser = "+ Aviel.isPaidUser());

        MyDB.clearUsers();
    }

    @Test
    public void createAndDeleteAds(){
        User a = new RegUser("Aviel","ayosef",308213198,"0509067205",null);
        User b = new PaidUser("Naor","npinhas",123456789,"0509067205",null,30);
        Ad ad1 = new Ad("first Ad","this is RegUser Ad","cars",100,null,false,"");
        Ad ad2 = new Ad("first Ad","this is RegUser Ad","cars",100,null,false,"");
        a.createNewAd(ad1);
        Assert.assertTrue(a.getMyAds().size() == 1);
//        Assert.assertTrue(a.getMyAds().get(0).getUser() == a );
        Assert.assertTrue(MyDB.Ads.size() == 1);
        b.createNewAd(ad2);
        Assert.assertTrue(b.getMyAds().size() == 1);
//        Assert.assertTrue(b.getMyAds().get(0).getUser() == b );
        Assert.assertTrue(MyDB.Ads.size() == 2);
        b.deleteAd(ad2);
        Assert.assertTrue(MyDB.Ads.size() == 1);
        MyDB.clearAds();
        Assert.assertTrue(MyDB.Ads.size() == 0);
        MyDB.clearUsers();
    }
}

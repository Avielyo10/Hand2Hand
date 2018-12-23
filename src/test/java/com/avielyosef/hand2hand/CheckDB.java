package com.avielyosef.hand2hand;

import com.avielyosef.hand2hand.DB.MyDB;
import com.avielyosef.hand2hand.Util.PaidUser;
import com.avielyosef.hand2hand.Util.RegUser;
import com.avielyosef.hand2hand.Util.User;

import org.junit.*;

public class CheckDB {

    @Test
    public void checkDB(){
        User a = new RegUser("Aviel","ayosef",308213198,"0509067205",null);
        User b = new PaidUser("Naor","npinhas",123456789,"0509067205",null,30);

        Assert.assertTrue(MyDB.Users.size() == 2);

        int countPaid = 0,countReg = 0;

        for (int i = 0;i < MyDB.Users.size(); ++i){
            if(MyDB.Users.get(i) instanceof RegUser) countReg++;
            else{
                countPaid++;
            }
        }
        Assert.assertTrue(countPaid == 1);
        Assert.assertTrue(countReg == 1);
        MyDB.clearUsers();
        Assert.assertTrue(MyDB.Users.size() == 0);
    }
}

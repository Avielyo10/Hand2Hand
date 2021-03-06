package com.avielyosef.hand2hand.Util;

import android.widget.ImageView;

/**
 * Advertisement
 */
public class Ad {
    private String title;
    private String description;
    private String category;
    private int price;
    private ImageView img;
    private boolean isNotPaid;
    private String userPhoneNum;
    private String userMail;
    private String adId;
    private String uid;
    private String username;

    public Ad(){}

    public Ad(String title, String description, String category, int price, ImageView img,
              boolean isNotPaid, String userPhoneNum, String userMail, String adId, String uid, String username) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.img = img;
        this.isNotPaid = isNotPaid;
        this.userPhoneNum = userPhoneNum;
        this.userMail = userMail;
        this.adId = adId;
        this.uid = uid;
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ImageView getImg() {
        return img;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    public boolean isNotPaid() { return isNotPaid; }

    public void setNotPaid(boolean notPaid) { isNotPaid = notPaid; }

    public String getAdId() { return adId; }

    public void setAdId(String adId) { this.adId = adId; }

    public String getUserPhoneNum() { return userPhoneNum; }

    public void setUserPhoneNum(String userPhoneNum) { this.userPhoneNum = userPhoneNum; }

    public String getUserMail() { return userMail; }

    public void setUserMail(String userMail) { this.userMail = userMail; }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    //    @NonNull
    @Override
    public String toString() {
        return "Ad{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category=" + category +
                ", price=" + price +
                ", img=" + img +
                '}';
    }
}

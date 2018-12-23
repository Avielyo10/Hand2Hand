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
    private boolean isPaid;
    private String uid;

    public Ad(){}
    public Ad(String title, String description, String category, int price, ImageView img, boolean isPaid, String uid) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.img = img;
        this.isPaid = isPaid;
        this.uid = uid;
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

    public boolean isPaid() { return isPaid; }

    public void setPaid(boolean paid) { isPaid = paid; }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

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

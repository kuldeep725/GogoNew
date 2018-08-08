package com.iam725.kunal.gogonew.AdminUtil;

import com.google.firebase.database.Exclude;

public class AdminItemData {
    private String key;   //email is the key
    private String request;
//    @Exclude
//    private String password;

    public AdminItemData(String key, String request) {
        this.key = encoder(key);
        this.request = request;
    }

    public AdminItemData(){}

    public String getKey() {
        return decoder(key);
    }

    public String getEncodedKey(){
        return key;
    }

//    @Exclude
//    public String getPassword() {
//        return password;
//    }
//
//    @Exclude
//    public void setPassword(String password) {
//        this.password = password;
//    }

    public void setKey(String key) {
        this.key = encoder(key);
    }

    public String getRequest() {
        return request;
    }

    public String encoder(String s){
        return s.replace(".", ", ");
    }

    public String decoder(String s){
        return s.replace(",", ".");
    }
}

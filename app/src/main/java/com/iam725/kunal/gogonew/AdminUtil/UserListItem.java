package com.iam725.kunal.gogonew.AdminUtil;

import com.google.firebase.database.Exclude;

public class UserListItem {
    private String email;
    private boolean isAdmin;

    public UserListItem() {}
    public UserListItem(String email, boolean isAdmin) {
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public String getEmail() {
        return decoder(email);
    }

    @Exclude
    public String getEncodedEmail(){
        return email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setEmail(String email) {
        this.email = encoder(email);
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String encoder(String s){
        return s.replace(".", ", ");
    }

    public String decoder(String s){
        return s.replace(",", ".");
    }
}

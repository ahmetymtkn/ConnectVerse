package com.ahmetymtkn.connectversenew.model;

public class user {
    public String name;
    public String downloadurl;
    public String userID;

    public user(String name, String downloadurl,String userID){
        this.name =name;
        this.downloadurl = downloadurl;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

}

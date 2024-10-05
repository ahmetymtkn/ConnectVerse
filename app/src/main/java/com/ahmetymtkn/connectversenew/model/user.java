package com.ahmetymtkn.connectversenew.model;

import java.io.Serializable;

public class user implements Serializable {
    public String name;
    public String downloadurl;
    public String userID;
    public String lastmessage;

    public user(String name, String downloadurl,String userID){
        this.name =name;
        this.downloadurl = downloadurl;
        this.userID = userID;
    }
    public user(String name, String downloadurl,String userID,String lastmessage){
        this.name =name;
        this.downloadurl = downloadurl;
        this.userID = userID;
        this.lastmessage = lastmessage;
    }
    public String getLastmessage(){return lastmessage;}

    public String getName() {
        return name;
    }

}

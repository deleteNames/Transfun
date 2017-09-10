package net.translives.app.bean;

import java.io.Serializable;

public class RegisterCheck implements Serializable {

    private long uid;
    private int status;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}

package org.chatontime.backend.model;

public class FeedbackToUseMsg {
    private boolean success;
    private String str;
    private UserMsg msg;

    public FeedbackToUseMsg(){}

    public FeedbackToUseMsg(boolean success, String str, UserMsg msg) {
        this.success = success;
        this.str = str;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public UserMsg getMsg() {
        return msg;
    }

    public void setMsg(UserMsg msg) {
        this.msg = msg;
    }
}
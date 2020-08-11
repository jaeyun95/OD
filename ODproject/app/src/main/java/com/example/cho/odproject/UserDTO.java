package com.example.cho.odproject;


public class UserDTO {
    private String user_name;
    private String user_email;
    private boolean user_sex;
    private String user_pwd;

    public UserDTO() {}
    public UserDTO(String user_name, String user_email, String user_pwd, boolean user_sex) {
        this.user_email = user_email;
        this.user_name = user_name;
        this.user_pwd = user_pwd;
        this.user_sex = user_sex;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public boolean getUser_sex() {
        return user_sex;
    }

    public String getUser_pwd() {
        return user_pwd;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_sex(boolean user_sex) {
        this.user_sex = user_sex;
    }

    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }
}

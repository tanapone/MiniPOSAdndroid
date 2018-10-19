package minipos.miniproject.com.miniposadndroid.Models;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class UserModel extends Application {
    @Expose
    private User user;

    public UserModel(){
        user = new User();
    }

    public UserModel(String jsonResponse){
        Gson gson  = new GsonBuilder().create();
        user = gson.fromJson(jsonResponse,User.class);
    }

    public void setModelByJson(String jsonResponse){
        Gson gson  = new GsonBuilder().create();
        user = gson.fromJson(jsonResponse,User.class);
    }

    public User getUser(){
        return this.user;
    }

    public String toJSONString(){
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this.user);
    }

    public class User{
        @Expose
        private long id = 0;
        @Expose
        private String username;
        @Expose
        private String password;
        @Expose
        private int userType;
        @Expose
        private String firstName;
        @Expose
        private String lastName;
        @Expose
        private String email;
        @Expose
        private String phoneNumber;
        @Expose
        private String address;
        @Expose
        private boolean userStatus;
        @Expose
        private String authKey;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAuthKey() {
            return authKey;
        }

        public boolean isUserStatus() {
            return userStatus;
        }

        public void setUserStatus(boolean userStatus) {
            this.userStatus = userStatus;
        }

        public void setAuthKey(String authKey) {
            this.authKey = authKey;
        }
    }
}


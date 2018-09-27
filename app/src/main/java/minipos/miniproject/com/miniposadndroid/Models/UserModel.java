package minipos.miniproject.com.miniposadndroid.Models;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserModel extends Application {
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
        private long id = 0;
        private String username;
        private String password;
        private int userType;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String address;
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

        public void setAuthKey(String authKey) {
            this.authKey = authKey;
        }
    }
}


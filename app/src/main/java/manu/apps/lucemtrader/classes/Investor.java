package manu.apps.lucemtrader.classes;

import com.google.firebase.database.Exclude;

public class Investor {

    @Exclude
    String id;
    String name;
    String username;
    String country;
    String phoneNo;
    String securityCode;
    String dateJoined;
    String sharedAgreed;
    String avatarUrl;

    public Investor() {
    }

    public Investor(String name,
                    String username, String country,
                    String phoneNo, String securityCode,
                    String dateJoined, String sharedAgreed,
                    String avatarUrl) {
        this.name = name;
        this.username = username;
        this.country = country;
        this.phoneNo = phoneNo;
        this.securityCode = securityCode;
        this.dateJoined = dateJoined;
        this.sharedAgreed = sharedAgreed;
        this.avatarUrl = avatarUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getSharedAgreed() {
        return sharedAgreed;
    }

    public void setSharedAgreed(String sharedAgreed) {
        this.sharedAgreed = sharedAgreed;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}

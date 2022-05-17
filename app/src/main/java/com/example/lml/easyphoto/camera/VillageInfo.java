package com.example.lml.easyphoto.camera;

import androidx.annotation.NonNull;

public class VillageInfo {
    public VillageInfo() {
    }

    public VillageInfo(String province, String city, String county, String country, String village) {
        this.province = province;
        this.city = city;
        this.county = county;
        this.country = country;
        this.village = village;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    @Override
    public String toString() {
        return  province +  city + county + country + village;
    }

    private String province;
    private String city;
    private String county;
    private String country;
    private String village;
}

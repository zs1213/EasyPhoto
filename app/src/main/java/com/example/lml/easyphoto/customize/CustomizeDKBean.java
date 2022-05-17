package com.example.lml.easyphoto.customize;

import java.util.List;

public class CustomizeDKBean {
     private String ID;
    private String taskId;
    private String userName;
    private String crop;
    private String dkName;
    private String drawArea;
    private String changeArea;
    private String state;
    private String createTime;
    private String subTime;
    private String userId;
    private String province;
    private String city;
    private String town;
    private String countryside;
    private String village;
    private String provinceCode;
    private String cityCode;
    private String townCode;
    private String countrysideCode;
    private String villageCode;
    private String proposalNo;//投保单号
    private String certificateId;//身份证号
    private String customerIds ;//地块关联的分户清单号
    private String isBusinessEntity;
    private String policyNumber;
    private String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getIsBusinessEntity() {
        return isBusinessEntity;
    }

    public void setIsBusinessEntity(String isBusinessEntity) {
        this.isBusinessEntity = isBusinessEntity;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getProposalNo() {
        return proposalNo;
    }

    public void setProposalNo(String proposalNo) {
        this.proposalNo = proposalNo;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getCustomerIds() {
        return customerIds;
    }

    public void setCustomerIds(String customerIds) {
        this.customerIds = customerIds;
    }

    private List<CustomizeDKPointBean> mList;
    private boolean isSelsect;

    public boolean isSelsect() {
        return isSelsect;
    }

    public void setSelsect(boolean selsect) {
        isSelsect = selsect;
    }

    public List<CustomizeDKPointBean> getmList() {
        return mList;
    }

    public void setmList(List<CustomizeDKPointBean> mList) {
        this.mList = mList;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getDkName() {
        return dkName;
    }

    public void setDkName(String dkName) {
        this.dkName = dkName;
    }

    public String getDrawArea() {
        return drawArea;
    }

    public void setDrawArea(String drawArea) {
        this.drawArea = drawArea;
    }

    public String getChangeArea() {
        return changeArea;
    }

    public void setChangeArea(String changeArea) {
        this.changeArea = changeArea;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSubTime() {
        return subTime;
    }

    public void setSubTime(String subTime) {
        this.subTime = subTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCountryside() {
        return countryside;
    }

    public void setCountryside(String countryside) {
        this.countryside = countryside;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getTownCode() {
        return townCode;
    }

    public void setTownCode(String townCode) {
        this.townCode = townCode;
    }

    public String getCountrysideCode() {
        return countrysideCode;
    }

    public void setCountrysideCode(String countrysideCode) {
        this.countrysideCode = countrysideCode;
    }

    public String getVillageCode() {
        return villageCode;
    }

    public void setVillageCode(String villageCode) {
        this.villageCode = villageCode;
    }
}

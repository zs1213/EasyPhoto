package com.example.lml.easyphoto.dikuai.finish;

public class FinishBean {
    private String userName;//保户姓名
    private String insureArea;//投保总面积
    private String customerIds;//关联ID
    private String plotArea;//标绘总面积
    private String massifName;//地块名称
    private boolean isChoose;

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getInsureArea() {
        return insureArea;
    }

    public void setInsureArea(String insureArea) {
        this.insureArea = insureArea;
    }

    public String getCustomerIds() {
        return customerIds;
    }

    public void setCustomerIds(String customerIds) {
        this.customerIds = customerIds;
    }

    public String getPlotArea() {
        return plotArea;
    }

    public void setPlotArea(String plotArea) {
        this.plotArea = plotArea;
    }

    public String getMassifName() {
        return massifName;
    }

    public void setMassifName(String massifName) {
        this.massifName = massifName;
    }
}

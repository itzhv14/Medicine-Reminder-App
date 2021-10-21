package com.gautam.medicinetime;

public class PrescriptionModel {
    String pId, uId, name, company_name, exp_date, price, imgurl;

    public PrescriptionModel() {
    }

    public PrescriptionModel(String pId, String uId, String name, String company_name, String exp_date, String price, String imgurl) {
        this.pId = pId;
        this.uId = uId;
        this.name = name;
        this.company_name = company_name;
        this.exp_date = exp_date;
        this.price = price;
        this.imgurl = imgurl;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getExp_date() {
        return exp_date;
    }

    public void setExp_date(String exp_date) {
        this.exp_date = exp_date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}

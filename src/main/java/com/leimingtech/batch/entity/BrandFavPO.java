package com.leimingtech.batch.entity;

/**
 * @author zhangtai
 * @date 2020/5/8 18:06
 * @Description:
 */
public class BrandFavPO {

    private String brandName;

    private String memberId;

    private String id;

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BrandFavPO{" +
                "brandName='" + brandName + '\'' +
                ", memberId='" + memberId + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}

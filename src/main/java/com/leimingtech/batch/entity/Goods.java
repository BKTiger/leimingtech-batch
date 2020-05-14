package com.leimingtech.batch.entity;

/**
 * @author zhangtai
 * @date 2020/5/12 15:49
 * @Description:
 */
public class Goods {
    private String id;

    private String name;

    private String price;

    private String colorId;

    private String colorName;

    private String sizeId;

    private String sizeName;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", colorId='" + colorId + '\'' +
                ", colorName='" + colorName + '\'' +
                ", sizeId='" + sizeId + '\'' +
                ", sizeName='" + sizeName + '\'' +
                '}';
    }
}

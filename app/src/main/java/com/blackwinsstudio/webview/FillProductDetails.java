package com.blackwinsstudio.webview;

import java.util.ArrayList;
import java.util.Arrays;

public class FillProductDetails {
    private String ProductSKU;
    private String status;
    public ArrayList<Tray> trayDetails;

    public String getProductSKU() {
        return ProductSKU;
    }

    public void setProductSKU(String productSKU) {
        ProductSKU = productSKU;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Tray> getTrayDetails() {
        return trayDetails;
    }

    @Override
    public String toString() {
        return "FillProductDetails{" +
                "ProductSKU='" + ProductSKU + '\'' +
                ", status='" + status + '\'' +
                ", trayDetails=" + trayDetails +
                '}';
    }

    public void setTrayDetails(ArrayList<Tray> trayDetails) {
        this.trayDetails = trayDetails;
    }
}

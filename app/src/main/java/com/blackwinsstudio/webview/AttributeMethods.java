package com.blackwinsstudio.webview;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface AttributeMethods {

    @GET("wc/v3/products/attributes/2/terms")
    Call<List<AttributesModel>> getMachineInPlaceData( @Header("Authorization") String authkey);

    @GET("wc/v3/orders")
    Call<List<OrdersModel>> getAllOrders(@Header("Authorization") String authkey);
}

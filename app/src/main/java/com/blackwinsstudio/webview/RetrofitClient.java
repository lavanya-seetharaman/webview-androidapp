package com.blackwinsstudio.webview;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static String BASE_URL="https://staging5.sastaamart.com/wp-json/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


    public static Retrofit getRetrofitInstance(){
        if(retrofit ==null){
            retrofit = new Retrofit.Builder ()
                    .baseUrl (BASE_URL)
                    .addConverterFactory (GsonConverterFactory.create ())
                    .build ();
        }
        return retrofit;
    }
}

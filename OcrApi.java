package com.example.final_pro;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface OcrApi {
    @Multipart
    @POST("parse/image")
    Call<ResponseBody> uploadImage(@Part("apikey") String apiKey, @Part("file\"; filename=\"image.jpg\"") RequestBody file);
}

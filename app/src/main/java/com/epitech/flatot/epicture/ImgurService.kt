package com.epitech.flatot.epicture

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST
import retrofit2.http.FormUrlEncoded



interface ImgurService {
    @POST("/token")
    @FormUrlEncoded
    //@POST("/token")
    fun getAccessToken(
            @Field("code") code: String,
            @Field("grant_type") grantType: String): Call<AccessToken>
}
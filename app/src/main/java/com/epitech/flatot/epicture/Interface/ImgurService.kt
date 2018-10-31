package com.epitech.flatot.epicture.Interface

import com.epitech.flatot.epicture.Model.AccessToken
import com.epitech.flatot.epicture.Model.ImgurInterface
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST
import retrofit2.http.FormUrlEncoded



interface ImgurService {

    @Headers("Accept: application/json")
    @POST("/oauth2/token")
    @FormUrlEncoded
    fun getAccessToken(@Field("client_id") clientId: String,
                       @Field("client_secret") clientSecret: String,
                       @Field("access_token") accessToken: String):
            Call<AccessToken>

    @GET("/3/account/me/images")
    fun getUser(@Header("Authorization") authHeader: String): Call<ImgurInterface.Result>

    @GET("/3/gallery/search/{{sort}}/{{window}}/{{page}}?q={{query}}")
    fun searchGallery(@Header("Authorization") authHeader: String,
                      @Path("sort") sort: String,
                      @Path("window") window: String,
                      @Path("page") page: String,
                      @Path("query") query: String): Call<ImgurInterface.SearchResult>


    //@POST("/token")
    //@FormUrlEncoded
    //@POST("/token")
    //fun getAccessToken(
    //        @Field("code") code: String,
    //        @Field("grant_type") grantType: String): Call<AccessToken>
}
package com.epitech.flatot.epicture.Interface

import com.epitech.flatot.epicture.Model.ImgurInterface
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST
import retrofit2.http.FormUrlEncoded
import okhttp3.RequestBody
import retrofit2.http.Multipart

interface ImgurService {
    
    @GET("/3/account/me/images")
    fun getUser(@Header("Authorization") authHeader: String): Call<ImgurInterface.Result>

    @GET("/3/gallery/search/{sort}/{window}/{page}")
    fun searchGallery(@Header("Authorization") authHeader: String,
                      @Path("sort") sort: String,
                      @Path("window") window: String,
                      @Path("page") page: Int,
                      @Query("q") query: String): Call<ImgurInterface.SearchResult>

    @GET("/3/account/me/favorites")
    fun getFavorite(@Header("Authorization") authHeader: String): Call<ImgurInterface.GetFavoriteResult>

    @POST("/3/image/{imageHash}/favorite")
    fun favoriteImage(@Header("Authorization") authHeader: String,
                      @Path("imageHash") imageId: String): Call<ImgurInterface.FavoriteResult>

    @Multipart
    @POST("/3/image")
    fun uploadImage(@Header("Authorization") authHeader: String,
                    @Part image: MultipartBody.Part,
                    @PartMap queries: Map<String, @JvmSuppressWildcards RequestBody>): Call<ImgurInterface.UploadResult>

    @Multipart
    @POST("/3/album")
    fun createAlbum(@Header("Authorization") authHeader: String,
                    @PartMap queries: Map<String, @JvmSuppressWildcards RequestBody>): Call<ImgurInterface.UploadResult>

    @Multipart
    @POST("/3/album/{albumId}/add")
    fun addToAlbum(@Header("Authorization") authHeader: String,
                   @Path("albumId") albumId: String,
                   @PartMap queries: Map<String, @JvmSuppressWildcards RequestBody>): Call<ImgurInterface.AddAlbumResult>

    @GET("/3/account/me/albums")
    fun getAlbums(@Header("Authorization") authHeader: String): Call<ImgurInterface.Result>

    @GET("/3/album/{albumId}")
    fun getAlbum(@Header("Authorization") authHeader: String,
                 @Path("albumId") albumId: String): Call<ImgurInterface.ImgurSearchItem>

    @GET("/3/account/me/settings")
    fun myProfil(@Header("Authorization") authHeader: String): Call<ImgurInterface.ProfilResult>

    @GET("/3/account/{username}/avatar")
    fun myAvatar(@Header("Authorization") authHeader: String,
            @Path("username") username: String): Call<ImgurInterface.AvatarResult>

    @GET("/3/account/me/available_avatars")
    fun availableAvatar(@Header("Authorization") authHeader: String): Call<ImgurInterface.AvailableAvatarResult>

    @GET("/3/account/me")
    fun myBio(@Header("Authorization") authHeader: String): Call<ImgurInterface.BioResult>

    @PUT("3/account/{user}/settings")
    fun mySet(@Header("Authorization") authHeader: String,
              @Path("user") user: String,
              @Query("username") username: String,
              @Query("bio") bio: String): Call<ImgurInterface.SetResult>


}
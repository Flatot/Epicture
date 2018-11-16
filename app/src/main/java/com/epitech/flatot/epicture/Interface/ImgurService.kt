package com.epitech.flatot.epicture.Interface

import com.epitech.flatot.epicture.Model.ImgurModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST
import okhttp3.RequestBody
import retrofit2.http.Multipart

interface ImgurService {
    
    @GET("/3/account/me/images")
    fun getUser(@Header("Authorization") authHeader: String): Call<ImgurModel.Result>

    @GET("/3/gallery/{section}/{sort}/{pages}/week")
    fun getGallery(@Header("Authorization") authHeader: String,
                   @Path("section") section: String,
                   @Path("sort") sort: String,
                   @Path("pages") pages: Int,
                   @Query("showViral") showViral: Boolean,
                   @Query("mature") mature: Boolean,
                   @Query("album_previews") album_previews: Boolean): Call<ImgurModel.SearchResult>

    @GET("/3/gallery/search/{sort}/{window}/{page}")
    fun searchGallery(@Header("Authorization") authHeader: String,
                      @Path("sort") sort: String,
                      @Path("window") window: String,
                      @Path("page") page: Int,
                      @Query("q") query: String): Call<ImgurModel.SearchResult>

    @GET("/3/account/me/favorites")
    fun getFavorite(@Header("Authorization") authHeader: String): Call<ImgurModel.GetFavoriteResult>

    @POST("/3/image/{imageHash}/favorite")
    fun favoriteImage(@Header("Authorization") authHeader: String,
                      @Path("imageHash") imageId: String): Call<ImgurModel.FavoriteResult>

    @Multipart
    @POST("/3/image")
    fun uploadImage(@Header("Authorization") authHeader: String,
                    @Part image: MultipartBody.Part,
                    @PartMap queries: Map<String, @JvmSuppressWildcards RequestBody>): Call<ImgurModel.UploadResult>

    @Multipart
    @POST("/3/album")
    fun createAlbum(@Header("Authorization") authHeader: String,
                    @PartMap queries: Map<String, @JvmSuppressWildcards RequestBody>): Call<ImgurModel.UploadResult>

    @Multipart
    @POST("/3/album/{albumId}/add")
    fun addToAlbum(@Header("Authorization") authHeader: String,
                   @Path("albumId") albumId: String,
                   @PartMap queries: Map<String, @JvmSuppressWildcards RequestBody>): Call<ImgurModel.AddAlbumResult>

    @GET("/3/account/me/albums")
    fun getAlbums(@Header("Authorization") authHeader: String): Call<ImgurModel.Result>

    @GET("/3/album/{albumId}")
    fun getAlbum(@Header("Authorization") authHeader: String,
                 @Path("albumId") albumId: String): Call<ImgurModel.ImgurSearchItem>

    @GET("/3/account/me/settings")
    fun myProfil(@Header("Authorization") authHeader: String): Call<ImgurModel.ProfilResult>

    @GET("/3/account/{username}/avatar")
    fun myAvatar(@Header("Authorization") authHeader: String,
            @Path("username") username: String): Call<ImgurModel.AvatarResult>

    @GET("/3/account/me/available_avatars")
    fun availableAvatar(@Header("Authorization") authHeader: String): Call<ImgurModel.AvailableAvatarResult>

    @GET("/3/account/me")
    fun myBio(@Header("Authorization") authHeader: String): Call<ImgurModel.BioResult>

    @PUT("3/account/{user}/settings")
    fun mySet(@Header("Authorization") authHeader: String,
              @Path("user") user: String,
              @Query("username") username: String,
              @Query("bio") bio: String): Call<ImgurModel.SetResult>

    @PUT("3/account/{user}/settings")
    fun mySetAvatar(@Header("Authorization") authHeader: String,
              @Path("user") user: String,
              @Query("avatar") avatar : String): Call<ImgurModel.SetResult>

}
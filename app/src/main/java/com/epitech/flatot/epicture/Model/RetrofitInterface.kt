package com.epitech.flatot.epicture.Model

import com.epitech.flatot.epicture.Interface.ImgurService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInterface() {

    fun createRetrofitBuilder(): ImgurService
    {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val imgurApi = retrofit.create(ImgurService::class.java)
        return imgurApi
    }
}
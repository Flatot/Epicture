package com.epitech.flatot.epicture

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.epitech.flatot.epicture.Interface.ImgurService
import com.epitech.flatot.epicture.Model.ImgurInterface
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity(), Callback<ImgurInterface.Result> {

    private var access_token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        access_token = intent.getStringExtra("access_token")

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val imgurApi = retrofit.create(ImgurService::class.java)



        val token = access_token
        val call = imgurApi.getUser("Bearer " + token)
        call.enqueue(this)
    }

    override fun onFailure(call: retrofit2.Call<ImgurInterface.Result>, t: Throwable) {
        Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()
    }

    override fun onResponse(call: retrofit2.Call<ImgurInterface.Result>, response: Response<ImgurInterface.Result>) {
        if (response.isSuccessful()) {
            val changesList = response.body()
            changesList!!.data.forEach {
                change -> System.out.println(change.link)
                Toast.makeText(this, change.link, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, response.message(), Toast.LENGTH_SHORT).show()
            System.out.println(response.errorBody())
        }
    }
}

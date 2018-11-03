package com.epitech.flatot.epicture.Views.FragmentBottom

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.fragment_profil.*
import kotlinx.android.synthetic.main.fragment_profil.view.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.item_cardview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek

class ProfilFragment : Fragment(), Callback<ImgurInterface.ProfilResult> {

    companion object {
        fun newInstance(access_token: String, refresh_token: String, username: String): ProfilFragment {
            val args = Bundle()
            args.putString("access_token", access_token)
            args.putString("refresh_token", refresh_token)
            args.putString("username", username)
            val fragment = ProfilFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater!!.inflate(R.layout.fragment_profil, container, false)
        GetProfil()
        return rootView
    }

    fun GetProfil() {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val call = imgurApi.myProfil("Bearer " + token)
        call.enqueue(this)
    }

    override fun onFailure(call: Call<ImgurInterface.ProfilResult>, t: Throwable) {
        txt_name.text = "No information"
        txt_bio.text = "No information"
        txt_time.text = "No information"
    }

    override fun onResponse(call: Call<ImgurInterface.ProfilResult>, response: Response<ImgurInterface.ProfilResult>) {
        if (response.isSuccessful) {
            txt_name.text = response.body()?.data?.account_url
            txt_bio.text = response.body()?.data?.email
            txt_time.text = response.body()?.data?.birthdate
        }
        else {
            txt_name.text = "Server response: null"
            txt_bio.text = "Server response: null"
            txt_time.text = "Server response: null"
        }
    }
}

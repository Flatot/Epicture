package com.epitech.flatot.epicture.Views.FragmentBottom

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.epitech.flatot.epicture.Adapter.LoadingAdapter
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_bottom_nav.*
import kotlinx.android.synthetic.main.dialog_filters.*
import kotlinx.android.synthetic.main.dialog_profile.*
import kotlinx.android.synthetic.main.dialog_profile.view.*
import kotlinx.android.synthetic.main.fragment_profil.*
import kotlinx.android.synthetic.main.fragment_profil.view.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.item_cardview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.util.*


class ProfilFragment : Fragment() {
    var items: MutableList<ImgurInterface.ImgurItem>? = null

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

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
        openSetProfile(rootView, context!!)
        if (items != null && items!!.isNotEmpty())
        {
            val layoutManager = LinearLayoutManager(context)
            rootView.ProfilRecyclerView?.layoutManager = layoutManager
            val adapter = LoadingAdapter(arguments?.getString("access_token")!!, context!!, items!!)
            rootView.ProfilRecyclerView?.adapter = adapter
        }
        else
            getAlbums()
        return rootView
    }


    fun getAlbums()
    {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val call = imgurApi.getUser("Bearer " + token)
        call.enqueue(object: Callback<ImgurInterface.Result> {
            override fun onFailure(call: Call<ImgurInterface.Result>, t: Throwable?) {
            }

            override fun onResponse(call: Call<ImgurInterface.Result>, response: Response<ImgurInterface.Result>) {
                if (response.isSuccessful) {
                    items = ArrayList()
                    val picList = response.body()
                    picList!!.data.forEach {
                        pic ->
                        val item = ImgurInterface.ImgurItem(pic)
                        items!!.add(item)
                    }
                    val layoutManager = LinearLayoutManager(context)
                    ProfilRecyclerView.layoutManager = layoutManager
                    val adapter = LoadingAdapter(arguments?.getString("access_token")!!, context!!, items!!)
                    ProfilRecyclerView.adapter = adapter
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    System.out.println(response.errorBody())
                }
            }
        })
    }

    fun editProfil(customDialog: android.support.v7.app.AlertDialog) {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val call = imgurApi.myProfil("Bearer " + token)
        call.enqueue(object: Callback<ImgurInterface.ProfilResult> {
            override fun onFailure(call: Call<ImgurInterface.ProfilResult>, t: Throwable?) { }

            override fun onResponse(call: Call<ImgurInterface.ProfilResult>, response: Response<ImgurInterface.ProfilResult>) {
                if (response.isSuccessful) {
                    var current = response.body()?.data?.account_url
                    customDialog.i_username.text = current?.toEditable()
                    current = response.body()?.data?.email
                    customDialog.i_email.text = current?.toEditable()
                }
            }
        })
        val avatar = imgurApi.myBio("Bearer " + token)
        avatar.enqueue(object: Callback<ImgurInterface.BioResult> {
            override fun onFailure(call: Call<ImgurInterface.BioResult>, t: Throwable) {
                Toast.makeText(context, "Failed to load bio", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurInterface.BioResult>, response: Response<ImgurInterface.BioResult>) {
                if (response.isSuccessful)
                {
                    var current = response.body()?.data?.bio
                    customDialog.i_desc.text = current?.toEditable()
                }
            }

        })
    }

    fun openSetProfile(rootView: View, context: Context) { //jpeg, png, gif && all time, last week && views
        rootView.set_profile.setOnClickListener {
            val myDialog = android.support.v7.app.AlertDialog.Builder(context)
            val myDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_profile, null)
            myDialog.setView(myDialogView)
            //myDialog.setCancelable(false)
            val customDialog = myDialog.create()
            customDialog.show()
            editProfil(customDialog)
        }
    }

    fun GetMyAvatar() {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val avatar = imgurApi.myAvatar("Bearer " + token, arguments?.getString("username")!!)
        avatar.enqueue(object: Callback<ImgurInterface.AvatarResult> {
            override fun onFailure(call: Call<ImgurInterface.AvatarResult>, t: Throwable) {
                Toast.makeText(context, "Failed to load Picture", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurInterface.AvatarResult>, response: Response<ImgurInterface.AvatarResult>) {
                if (response.isSuccessful)
                {
                    if (response.body()!!.data.avatar == null || response.body()!!.data.avatar == "")
                        profil_pic.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.grow))
                    else
                        Picasso.with(context).load(response.body()!!.data.avatar).into(profil_pic)
                }
                else {
                    Picasso.with(context).load(response.body()!!.data.avatar).into(profil_pic)
                }
            }

        })
    }

    fun GetInfoProfil() {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val call = imgurApi.myProfil("Bearer " + token)
        call.enqueue(object: Callback<ImgurInterface.ProfilResult> {
            override fun onFailure(call: Call<ImgurInterface.ProfilResult>, t: Throwable?) {
                txt_name.text = "No information"
                txt_time.text = "No information"
            }

            override fun onResponse(call: Call<ImgurInterface.ProfilResult>, response: Response<ImgurInterface.ProfilResult>) {
                if (response.isSuccessful) {
                    var current = response.body()?.data?.account_url
                    var tmp = response.body()?.data?.email
                    var final = ""
                    if (current != null && current != "")
                        final = final + current + " "
                    else
                        final = final + "No username "
                    if (tmp != null && tmp != "")
                        final = final + "(" + tmp + ")"
                    else
                        final = final + " (No email)"
                    txt_name.text = final
                    current = response.body()?.data?.birthdate
                    if (current == null || current == "")
                        txt_time.text = "No birthday filled"
                    else
                        txt_time.text = "Birthday: " + current
                    current = response.body()?.data?.gender
                    if (current == null || current == "")
                        txt_gender.text = "No gender filled"
                    else
                        txt_gender.text = "Gender: " + current
                } else {
                    txt_name.text = "Server response: null"
                    txt_time.text = "Server response: null"
                    txt_gender.text = "Server response: null"
                    txt_desc.text = "Server response: null"
                }
            }
        })
    }

    fun GetBio() {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val avatar = imgurApi.myBio("Bearer " + token)
        avatar.enqueue(object: Callback<ImgurInterface.BioResult> {
            override fun onFailure(call: Call<ImgurInterface.BioResult>, t: Throwable) {
                Toast.makeText(context, "Failed to load bio", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurInterface.BioResult>, response: Response<ImgurInterface.BioResult>) {
                if (response.isSuccessful)
                {
                    var current = response.body()?.data?.bio
                    if (current == null || current == "")
                        txt_desc.text = "No description filled"
                    else
                        txt_desc.text = "Description: " + current
                }
                else {
                    txt_desc.text = "No description filled"
                }
            }

        })
    }

    fun GetProfil() {
        GetInfoProfil()
        GetBio()
        GetMyAvatar()
    }
}

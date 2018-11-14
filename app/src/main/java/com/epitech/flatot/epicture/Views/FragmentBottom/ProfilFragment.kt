package com.epitech.flatot.epicture.Views.FragmentBottom

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.epitech.flatot.epicture.Adapter.AvatarsDialogAdapter
import com.epitech.flatot.epicture.Adapter.LoadingAdapter
import com.epitech.flatot.epicture.Adapter.SearchAdapter
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.R.string.available_avatars
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_add_to_album.*
import kotlinx.android.synthetic.main.dialog_add_to_album.view.*
import kotlinx.android.synthetic.main.dialog_filters.*
import kotlinx.android.synthetic.main.dialog_profile.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profil.*
import kotlinx.android.synthetic.main.fragment_profil.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ProfilFragment : Fragment() {
    var items: MutableList<ImgurInterface.ImgurItem>? = null
    var albums_items_pro: MutableList<ImgurInterface.ImgurSearchItem>? = null

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    val available_avatars: MutableList<ImgurInterface.Available_avatar> = ArrayList()
    val c_avatar: ImgurInterface.Available_avatar? = null
    var nameUser: String = ""
    var bool_album_pro: Boolean = false


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
        val token = arguments?.getString("access_token")
        if (available_avatars.isEmpty())
        {
            val imgurApi = RetrofitInterface().createRetrofitBuilder()
            val call = imgurApi.availableAvatar("Bearer " + token)
            call.enqueue(object: Callback<ImgurInterface.AvailableAvatarResult> {
                override fun onFailure(call: Call<ImgurInterface.AvailableAvatarResult>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<ImgurInterface.AvailableAvatarResult>, response: Response<ImgurInterface.AvailableAvatarResult>) {
                    if (response.isSuccessful)
                    {
                        response.body()!!.data.available_avatars.forEach {
                            avatar ->
                            available_avatars.add(avatar)
                        }
                    }
                }
            })
        }
        try {
            rootView.profil_pic.setOnClickListener {
                val myDialog = AlertDialog.Builder(context!!)
                val myDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_album, null)
                myDialogView.text_dialog.text = getString(R.string.available_avatars)
                myDialogView.text_dialog.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
                myDialog.setView(myDialogView)
                //myDialog.setCancelable(false)
                val customDialog = myDialog.create()
                customDialog.show()
                customDialog.dialogRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
                val username_c = arguments?.getString("username")
                val adapter = AvatarsDialogAdapter(token!!, context!!, available_avatars, username_c!!, customDialog)
                customDialog.dialogRecyclerView.adapter = adapter
            }
            if (c_avatar != null)
                Toast.makeText(context, c_avatar.name, Toast.LENGTH_SHORT).show()
            GetProfil()
            openSetProfile(rootView, context!!)
            if (items != null && items!!.isNotEmpty()) {
                val layoutManager = LinearLayoutManager(context)
                rootView.ProfilRecyclerView?.layoutManager = layoutManager
                val adapter = LoadingAdapter(arguments?.getString("access_token")!!, context!!, items!!)
                rootView.ProfilRecyclerView?.adapter = adapter
            } else
                getGallery()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        return rootView
    }

    private fun getAlbums() {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()

        val token = arguments?.getString("access_token")
        val call = imgurApi.getAlbums("Bearer " + token)
        call.enqueue(object: Callback<ImgurInterface.Result> {
            override fun onFailure(call: Call<ImgurInterface.Result>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurInterface.Result>, response: Response<ImgurInterface.Result>) {
                if (response.isSuccessful)
                {
                    albums_items_pro = ArrayList()
                    val picList = response.body()
                    picList!!.data.forEach {
                        pic ->
                        val album = imgurApi.getAlbum("Bearer " + token, pic.id)
                        album.enqueue(object: Callback<ImgurInterface.ImgurSearchItem> {
                            override fun onFailure(call: Call<ImgurInterface.ImgurSearchItem>, t: Throwable) {
                                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(call: Call<ImgurInterface.ImgurSearchItem>, response: Response<ImgurInterface.ImgurSearchItem>) {
                                try {
                                    if (response.isSuccessful) {
                                        val albumList = response.body()
                                        val alb_item = ImgurInterface.ImgurSearchItem(albumList!!.data)
                                        albums_items_pro!!.add(alb_item)

                                        if (albums_items_pro!!.size == picList.data.size) {
                                            val layoutManager = LinearLayoutManager(context) //StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                                            HomeRecyclerView.layoutManager = layoutManager
                                            val adapter = SearchAdapter(HomeRecyclerView, arguments?.getString("access_token")!!, context!!, albums_items_pro!!)
                                            HomeRecyclerView.adapter = adapter
                                        }
                                    } else
                                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                                }
                                catch (e:Exception) {
                                    e.printStackTrace()
                                }
                            }
                        })
                    }
                }
                else
                    Toast.makeText(context, "Failed load album", Toast.LENGTH_SHORT).show()
            }
        })
    }



    fun getGallery()
    {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val call = imgurApi.getUser("Bearer " + token)
        call.enqueue(object: Callback<ImgurInterface.Result> {
            override fun onFailure(call: Call<ImgurInterface.Result>, t: Throwable?) {
            }

            override fun onResponse(call: Call<ImgurInterface.Result>, response: Response<ImgurInterface.Result>) {
                try {
                    if (response.isSuccessful) {
                        items = ArrayList()
                        val picList = response.body()
                        picList!!.data.forEach { pic ->
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
                catch (e:Exception) {
                    e.printStackTrace()
                }
            }
        })
    }



    fun editProfil(customDialog: android.support.v7.app.AlertDialog) {
        if (bool_album_pro) customDialog.radio_albums.setChecked(true) else customDialog.radio_albums.setChecked(false)
        val imgurApi = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val call = imgurApi.myProfil("Bearer " + token)
        call.enqueue(object: Callback<ImgurInterface.ProfilResult> {
            override fun onFailure(call: Call<ImgurInterface.ProfilResult>, t: Throwable?) { }

            override fun onResponse(call: Call<ImgurInterface.ProfilResult>, response: Response<ImgurInterface.ProfilResult>) {
                try {
                    if (response.isSuccessful) {
                        var current = response.body()?.data?.account_url
                        customDialog.i_username.text = current?.toEditable()
                    }
                }
                catch (e:Exception) {
                    e.printStackTrace()
                }
            }
        })
        val avatar = imgurApi.myBio("Bearer " + token)
        avatar.enqueue(object: Callback<ImgurInterface.BioResult> {
            override fun onFailure(call: Call<ImgurInterface.BioResult>, t: Throwable) {
                Toast.makeText(context, "Failed to load bio", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurInterface.BioResult>, response: Response<ImgurInterface.BioResult>) {
                try {
                    if (response.isSuccessful) {
                        var current = response.body()?.data?.bio
                        customDialog.i_desc.text = current?.toEditable()
                    }
                }
                catch (e:Exception) {
                    e.printStackTrace()
                }
            }

        })
    }

    fun setDatas(customDialog: AlertDialog) {
        var username: String
        if (customDialog.i_username.text.toString() == "")
            username = nameUser
        else
            username = customDialog.i_username.text.toString()
        var description: String
        if (customDialog.i_username.text.toString() == "")
            description = "Empty description"
        else
            description = customDialog.i_desc.text.toString()

        val imgurApi = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val user = arguments?.getString("username")
        val call = imgurApi.mySet("Bearer " + token, user!!, username , description)
        call.enqueue(object: Callback<ImgurInterface.SetResult> {
            override fun onFailure(call: Call<ImgurInterface.SetResult>, t: Throwable?) {
                Toast.makeText(context, "Some fields contain error(s). Changes were not saved", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurInterface.SetResult>, response: Response<ImgurInterface.SetResult>) {
                try {
                    if (response.isSuccessful) {
                        if (radio_albums.isChecked) {
                            Toast.makeText(context, "Changes saved... Loading Album(s)", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                catch (e:Exception) {
                    Toast.makeText(context, "Can't save changes...", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        })
    }

    fun clickSetProfile(customDialog: AlertDialog) {
        customDialog.back_profile.setOnClickListener {
            customDialog.hide()
        }
        customDialog.save_profile.setOnClickListener {
            setDatas(customDialog)
            customDialog.hide()
            GetProfil()
            if (customDialog.radio_albums.isChecked)
                getAlbums()
        }
        customDialog.radio_albums.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) bool_album_pro = true else bool_album_pro = false
        }
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
            clickSetProfile(customDialog)
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
                try {
                    if (response.isSuccessful) {
                        if (response.body()!!.data.avatar == null || response.body()!!.data.avatar == "")
                            profil_pic.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.grow))
                        else {
                            var pic = Glide.with(context).load(response.body()!!.data.avatar).apply(RequestOptions.circleCropTransform())
                            pic.into(profil_pic)
                        }
                    } else {
                        profil_pic.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.grow))
                    }
                }
                catch (e:Exception) {
                    e.printStackTrace()
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
            }

            override fun onResponse(call: Call<ImgurInterface.ProfilResult>, response: Response<ImgurInterface.ProfilResult>) {
                try {
                    if (response.isSuccessful) {
                        var current = response.body()?.data?.account_url
                        var tmp = response.body()?.data?.email
                        var final = ""
                        if (current != null && current != "") {
                            final = final + current + " "
                            nameUser = current
                        }
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
                catch (e:Exception) {
                    e.printStackTrace()
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
                try {
                    if (response.isSuccessful) {
                        var current = response.body()?.data?.bio
                        if (current == null || current == "")
                            txt_desc.text = "No description filled"
                        else
                            txt_desc.text = "Description: " + current
                    } else {
                        txt_desc.text = "No description filled"
                    }
                }
                catch (e:Exception) {
                    e.printStackTrace()
                }
            }

        })
    }

    fun GetProfil() {
        try {
            GetInfoProfil()
            GetBio()
            GetMyAvatar()
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
    }
}

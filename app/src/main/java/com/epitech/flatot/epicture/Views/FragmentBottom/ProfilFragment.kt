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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import com.epitech.flatot.epicture.Adapter.AvatarsDialogAdapter
import com.epitech.flatot.epicture.Adapter.LoadingAdapter
import com.epitech.flatot.epicture.Adapter.SearchAdapter
import com.epitech.flatot.epicture.Model.ImgurModel
import com.epitech.flatot.epicture.Model.RetrofitModel
import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.dialog_add_to_album.*
import kotlinx.android.synthetic.main.dialog_add_to_album.view.*
import kotlinx.android.synthetic.main.dialog_profile.*
import kotlinx.android.synthetic.main.fragment_profil.*
import kotlinx.android.synthetic.main.fragment_profil.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ProfilFragment : Fragment() {
    var items: MutableList<ImgurModel.ImgurItem>? = null
    var albums_items_pro: MutableList<ImgurModel.ImgurSearchItem>? = null

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    val available_avatars: MutableList<ImgurModel.Available_avatar> = ArrayList()
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
            val imgurApi = RetrofitModel().createRetrofitBuilder()
            val call = imgurApi.availableAvatar("Bearer " + token)
            call.enqueue(object: Callback<ImgurModel.AvailableAvatarResult> {
                override fun onFailure(call: Call<ImgurModel.AvailableAvatarResult>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<ImgurModel.AvailableAvatarResult>, response: Response<ImgurModel.AvailableAvatarResult>) {
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
            rootView.profil_pic.setOnClickListener { it ->
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

                customDialog.setOnDismissListener {
                    GetMyAvatar()
                }
            }
            GetProfil()
            openSetProfile(rootView, context!!)
            if (bool_album_pro)
                getAlbums()
            else
                getGallery()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        return rootView
    }

    private fun getAlbums() {
        val imgurApi = RetrofitModel().createRetrofitBuilder()

        val token = arguments?.getString("access_token")
        val call = imgurApi.getAlbums("Bearer " + token)
        call.enqueue(object: Callback<ImgurModel.Result> {
            override fun onFailure(call: Call<ImgurModel.Result>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurModel.Result>, response: Response<ImgurModel.Result>) {
                if (response.isSuccessful)
                {
                    albums_items_pro = ArrayList()
                    val picList = response.body()
                    picList!!.data.forEach {
                        pic ->
                        val album = imgurApi.getAlbum("Bearer " + token, pic.id)
                        album.enqueue(object: Callback<ImgurModel.ImgurSearchItem> {
                            override fun onFailure(call: Call<ImgurModel.ImgurSearchItem>, t: Throwable) {
                                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(call: Call<ImgurModel.ImgurSearchItem>, response: Response<ImgurModel.ImgurSearchItem>) {
                                try {
                                    if (response.isSuccessful) {
                                        val albumList = response.body()
                                        val alb_item = ImgurModel.ImgurSearchItem(albumList!!.data)
                                        albums_items_pro!!.add(alb_item)

                                        if (albums_items_pro!!.size == picList.data.size) {
                                            val layoutManager = LinearLayoutManager(context) //StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                                            ProfilRecyclerView.layoutManager = layoutManager
                                            val adapter = SearchAdapter(ProfilRecyclerView, arguments?.getString("access_token")!!, context!!, albums_items_pro!!)
                                            ProfilRecyclerView.adapter = adapter
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
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val call = imgurApi.getUser("Bearer " + token)
        call.enqueue(object: Callback<ImgurModel.Result> {
            override fun onFailure(call: Call<ImgurModel.Result>, t: Throwable?) {
            }

            override fun onResponse(call: Call<ImgurModel.Result>, response: Response<ImgurModel.Result>) {
                try {
                    if (response.isSuccessful) {
                        items = ArrayList()
                        val picList = response.body()
                        picList!!.data.forEach { pic ->
                            val item = ImgurModel.ImgurItem(pic)
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
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val call = imgurApi.myProfil("Bearer " + token)
        call.enqueue(object: Callback<ImgurModel.ProfilResult> {
            override fun onFailure(call: Call<ImgurModel.ProfilResult>, t: Throwable?) { }

            override fun onResponse(call: Call<ImgurModel.ProfilResult>, response: Response<ImgurModel.ProfilResult>) {
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
        avatar.enqueue(object: Callback<ImgurModel.BioResult> {
            override fun onFailure(call: Call<ImgurModel.BioResult>, t: Throwable) {
                Toast.makeText(context, "Failed to load bio", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurModel.BioResult>, response: Response<ImgurModel.BioResult>) {
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

        val imgurApi = RetrofitModel().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val user = arguments?.getString("username")
        val call = imgurApi.mySet("Bearer " + token, user!!, username , description)
        call.enqueue(object: Callback<ImgurModel.SetResult> {
            override fun onFailure(call: Call<ImgurModel.SetResult>, t: Throwable?) {
                Toast.makeText(context, "Some fields contain error(s). Changes were not saved", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurModel.SetResult>, response: Response<ImgurModel.SetResult>) {
                try {
                    if (response.isSuccessful) {
                        if (bool_album_pro) {
                            Toast.makeText(context, "Changes saved... Loading Album(s)", Toast.LENGTH_SHORT).show()
                            getAlbums()
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
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val avatar = imgurApi.myAvatar("Bearer " + token, arguments?.getString("username")!!)
        avatar.enqueue(object: Callback<ImgurModel.AvatarResult> {
            override fun onFailure(call: Call<ImgurModel.AvatarResult>, t: Throwable) {
                Toast.makeText(context, "Failed to load Picture", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurModel.AvatarResult>, response: Response<ImgurModel.AvatarResult>) {
                try {
                    if (response.isSuccessful) {
                        if (response.body()!!.data.avatar == null || response.body()!!.data.avatar == "")
                            profil_pic.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.grow))
                        else {
                            profil_pic.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.grow))
                            Glide.with(context).load(response.body()!!.data.avatar)
                                    .apply(RequestOptions.circleCropTransform()
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true)
                                            .signature(MediaStoreSignature("null", System.currentTimeMillis(), 1)))
                                    .into(profil_pic)
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
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val call = imgurApi.myProfil("Bearer " + token)
        call.enqueue(object: Callback<ImgurModel.ProfilResult> {
            override fun onFailure(call: Call<ImgurModel.ProfilResult>, t: Throwable?) {
            }

            override fun onResponse(call: Call<ImgurModel.ProfilResult>, response: Response<ImgurModel.ProfilResult>) {
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
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val avatar = imgurApi.myBio("Bearer " + token)
        avatar.enqueue(object: Callback<ImgurModel.BioResult> {
            override fun onFailure(call: Call<ImgurModel.BioResult>, t: Throwable) {
                Toast.makeText(context, "Failed to load bio", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurModel.BioResult>, response: Response<ImgurModel.BioResult>) {
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

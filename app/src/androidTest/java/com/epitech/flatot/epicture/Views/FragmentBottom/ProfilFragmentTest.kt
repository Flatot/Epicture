package com.epitech.flatot.epicture.Views.FragmentBottom

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.Editable
import android.view.LayoutInflater
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
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

// 44 Tests

class ProfilFragmentTest: Fragment() {

    var items: MutableList<ImgurModel.ImgurItem>? = null
    var albums_items_pro: MutableList<ImgurModel.ImgurSearchItem>? = null

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    val available_avatars: MutableList<ImgurModel.Available_avatar> = ArrayList()
    var nameUser: String = ""
    var bool_album_pro: Boolean = false


    @Test
    fun onCreateView()  {
        val token = arguments?.getString("access_token")
        if (available_avatars.isEmpty())
        {
            val imgurApi = RetrofitModel().createRetrofitBuilder()
            assertNotNull(imgurApi)
            val call = imgurApi.availableAvatar("Bearer " + token)
            assertNotNull(call)
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
            profil_pic.setOnClickListener { it ->
                val myDialog = AlertDialog.Builder(context!!)
                assertNotNull(myDialog)
                val myDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_album, null)
                assertNotNull(myDialogView)
                myDialogView.text_dialog.text = getString(R.string.available_avatars)
                myDialogView.text_dialog.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
                myDialog.setView(myDialogView)
                //myDialog.setCancelable(false)
                val customDialog = myDialog.create()
                customDialog.show()
                customDialog.dialogRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
                val username_c = arguments?.getString("username")
                assertNotNull(username_c)
                val adapter = AvatarsDialogAdapter(token!!, context!!, available_avatars, username_c!!, customDialog)
                assertNotNull(adapter)
                customDialog.dialogRecyclerView.adapter = adapter

                customDialog.setOnDismissListener {
                    GetMyAvatar()
                }
            }
            getProfil()
            if (bool_album_pro)
                getAlbums()
            else
                getGallery()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    @Test
    fun getGallery() {
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        assertNotNull(imgurApi)
        val token = arguments?.getString("access_token")
        val call = imgurApi.getUser("Bearer " + token)
        assertNotNull(call)
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
                            assertNotNull(item)
                            items!!.add(item)
                        }
                        val layoutManager = LinearLayoutManager(context)
                        assertNotNull(layoutManager)
                        ProfilRecyclerView.layoutManager = layoutManager
                        val adapter = LoadingAdapter(arguments?.getString("access_token")!!, context!!, items!!)
                        assertNotNull(adapter)
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

    @Test
    fun setDatas() {
        var username: String
        username = nameUser
        assertNotNull(username) //The username exist and isn't null
        var description: String
        description = "Empty description"
        assertNotNull(description) //The bio exist and isn't null
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        assertNotNull(imgurApi) //init the call api
        var token = arguments?.getString("access_token")
        var user = arguments?.getString("username")
        assertNull(token) //Get the token for the call api
        assertNull(user) //Get the username for the call api
        if (token.isNullOrEmpty()) { token = ""}
        if (user.isNullOrEmpty()) { user = ""}
        val call = imgurApi.mySet("Bearer " + token, user!!, username , description)
        assertNotNull(call) //The call is ok, all mandatory info were filled
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

    @Test
    fun getAlbums() {
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        assertNotNull(imgurApi) //Call api init is ok
        val token = arguments?.getString("access_token")
        val call = imgurApi.getAlbums("Bearer " + token)
        assertNotNull(call) //The call is ok, all mandatory info were filled
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
                                        assertNotNull(alb_item) //The album isn't null, let's add the elem to the list
                                        albums_items_pro!!.add(alb_item)

                                        if (albums_items_pro!!.size == picList.data.size) {
                                            val layoutManager = LinearLayoutManager(context) //StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                                            assertNotNull(layoutManager)
                                            ProfilRecyclerView.layoutManager = layoutManager
                                            val adapter = SearchAdapter(ProfilRecyclerView, arguments?.getString("access_token")!!, context!!, albums_items_pro!!)
                                            assertNotNull(adapter)
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
            }
        })
    }

    @Test
    fun editProfil() {
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        assertNotNull(imgurApi)
        val token = arguments?.getString("access_token")
        val call = imgurApi.myProfil("Bearer " + token)
        assertNotNull(call)
        call.enqueue(object: Callback<ImgurModel.ProfilResult> {
            override fun onFailure(call: Call<ImgurModel.ProfilResult>, t: Throwable?) { }

            override fun onResponse(call: Call<ImgurModel.ProfilResult>, response: Response<ImgurModel.ProfilResult>) {
                try {
                    if (response.isSuccessful) {
                        var current = response.body()?.data?.account_url
                        assertNotNull(current)
                    }
                }
                catch (e:Exception) {
                    e.printStackTrace()
                }
            }
        })
        val avatar = imgurApi.myBio("Bearer " + token)
        assertNotNull(avatar)
        avatar.enqueue(object: Callback<ImgurModel.BioResult> {
            override fun onFailure(call: Call<ImgurModel.BioResult>, t: Throwable) {
                Toast.makeText(context, "Failed to load bio", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurModel.BioResult>, response: Response<ImgurModel.BioResult>) {
                try {
                    if (response.isSuccessful) {
                        var current = response.body()?.data?.bio
                        assertNotNull(current)
                    }
                }
                catch (e:Exception) {
                    e.printStackTrace()
                }
            }

        })
    }

    @Test
    fun GetMyAvatar() {
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        assertNotNull(imgurApi)
        val token = arguments?.getString("access_token")
        var username = "rubion97"
        val avatar = imgurApi.myAvatar("Bearer " + token, username)
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

    @Test
    fun getInfoProfil() {
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        assertNotNull(imgurApi)
        val token = arguments?.getString("access_token")
        val call = imgurApi.myProfil("Bearer " + token)
        assertNotNull(call)
        call.enqueue(object: Callback<ImgurModel.ProfilResult> {
            override fun onFailure(call: Call<ImgurModel.ProfilResult>, t: Throwable?) {
            }

            override fun onResponse(call: Call<ImgurModel.ProfilResult>, response: Response<ImgurModel.ProfilResult>) {
                try {
                    if (response.isSuccessful) {
                        var current = response.body()?.data?.account_url
                        var tmp = response.body()?.data?.email
                        assertNotNull(current)
                        assertNotNull(tmp)
                    }
                }
                catch (e:Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    @Test
    fun getBio() {
        val imgurApi = RetrofitModel().createRetrofitBuilder()
        assertNotNull(imgurApi)
        val token = arguments?.getString("access_token")
        val avatar = imgurApi.myBio("Bearer " + token)
        assertNotNull(avatar)
        avatar.enqueue(object: Callback<ImgurModel.BioResult> {
            override fun onFailure(call: Call<ImgurModel.BioResult>, t: Throwable) {
                Toast.makeText(context, "Failed to load bio", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImgurModel.BioResult>, response: Response<ImgurModel.BioResult>) {
                try {
                    if (response.isSuccessful) {
                        var current = response.body()?.data?.bio
                        assertNotNull(current)
                    }
                }
                catch (e:Exception) {
                    e.printStackTrace()
                }
            }

        })
    }

    @Test
    fun getProfil() {
        try {
            getInfoProfil()
            getBio()
            GetMyAvatar()
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
    }
}
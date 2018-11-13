package com.epitech.flatot.epicture.Views.FragmentBottom

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.epitech.flatot.epicture.Adapter.AvatarsDialogAdapter
import com.epitech.flatot.epicture.Adapter.DialogAdapter
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_add_to_album.*
import kotlinx.android.synthetic.main.dialog_add_to_album.view.*
import kotlinx.android.synthetic.main.fragment_profil.*
import kotlinx.android.synthetic.main.fragment_profil.view.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.item_cardview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek

class ProfilFragment : Fragment() {

    val available_avatars: MutableList<ImgurInterface.Available_avatar> = ArrayList()
    val c_avatar: ImgurInterface.Available_avatar? = null

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
            val adapter = AvatarsDialogAdapter(token!!, context!!, available_avatars, customDialog)
            customDialog.dialogRecyclerView.adapter = adapter
        }
        if (c_avatar != null)
            Toast.makeText(context, c_avatar.name, Toast.LENGTH_SHORT).show()
        GetProfil()
        return rootView
    }

    fun GetProfil() {
        val imgurApi = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val call = imgurApi.myProfil("Bearer " + token)
        call.enqueue(object: Callback<ImgurInterface.ProfilResult> {
            override fun onFailure(call: Call<ImgurInterface.ProfilResult>, t: Throwable?) {
                txt_name.text = "No information"
                txt_bio.text = "No information"
                txt_time.text = "No information"
            }

            override fun onResponse(call: Call<ImgurInterface.ProfilResult>, response: Response<ImgurInterface.ProfilResult>) {
                if (response.isSuccessful) {
                    var current = response.body()?.data?.account_url
                    if (current == null || current == "")
                        txt_name.text = "No username"
                    else
                        txt_name.text = "Username: " + current
                    current = response.body()?.data?.email
                    if (current == null || current == "")
                        txt_name.text = "No email"
                    else
                        txt_bio.text = "Email: " + current
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
                    txt_bio.text = "Server response: null"
                    txt_time.text = "Server response: null"
                    txt_gender.text = "Server response: null"
                }
            }
        })
        val avatar = imgurApi.myAvatar("Bearer " + token, arguments?.getString("username")!!)
        avatar.enqueue(object: Callback<ImgurInterface.AvatarResult> {
            override fun onFailure(call: Call<ImgurInterface.AvatarResult>, t: Throwable) {
                Toast.makeText(context, "Failed load Picture", Toast.LENGTH_SHORT).show()
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
}

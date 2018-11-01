package com.epitech.flatot.epicture.Views.FragmentBottom

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.CursorLoader
import android.support.v4.content.PermissionChecker.checkSelfPermission
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface

import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.R.id.*
import kotlinx.android.synthetic.main.fragment_upload.*
import kotlinx.android.synthetic.main.fragment_upload.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class UploadFragment : Fragment() {

    private val GALLERY = 1
    private val CAMERA = 2
    private var MyPicBinary: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(view!!.context)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun takePhotoFromCamera() {
        //val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //startActivityForResult(intent, CAMERA)
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, CAMERA)
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY)
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                MyPicBinary = data.data
                val inputStream = activity!!.contentResolver.openInputStream(MyPicBinary)
                imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream))
                Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()

                btn_upload_imgur.isEnabled = true
            }

        }
        else if (requestCode == CAMERA)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imageView!!.setImageBitmap(thumbnail)
            Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
            btn_upload_imgur.isEnabled = true
        }
    }

    companion object {
        fun newInstance(access_token: String): UploadFragment {
            val args = Bundle()
            args.putString("access_token", access_token)
            val fragment = UploadFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_upload, container, false)
        if (checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    777)
        }
        choosePhotoFromGallary()
        //TODO revenir sur l'ancienne nav
        //if (rootView.imageView.drawable == null)
        rootView.btn_upload_imgur.setOnClickListener {
            upload_on_imgur()
        }
        rootView.imageView.setOnClickListener {
            choosePhotoFromGallary()
        }
        //val btn = rootView.btn_load_img
        //btn.setOnClickListener {
        //showPictureDialog()
        //choosePhotoFromGallary()
        //}
        return rootView
    }

    private fun upload_on_imgur() {

        val file = File(getRealPathFromURI(MyPicBinary!!))

        val requestFile = RequestBody.create(MediaType.parse(activity!!.contentResolver.getType(MyPicBinary)), file)
        val imageBody = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val titleBody = RequestBody.create(okhttp3.MultipartBody.FORM, text_title.text.toString())
        val descriptionBody = RequestBody.create(okhttp3.MultipartBody.FORM, text_description.text.toString())
        val optinalBodyMap = mapOf("title" to titleBody, "description" to descriptionBody)

        val retrofit = RetrofitInterface().createRetrofitBuilder()
        val token = arguments?.getString("access_token")
        val call = retrofit.uploadImage("Bearer " + token, imageBody, optinalBodyMap)
        progressUpload.visibility = View.VISIBLE

        call.enqueue(object: Callback<ImgurInterface.UploadResult> {
            override fun onFailure(call: Call<ImgurInterface.UploadResult>, t: Throwable) {
                    Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                    progressUpload.visibility = View.GONE
            }
            override fun onResponse(call: Call<ImgurInterface.UploadResult>, response: Response<ImgurInterface.UploadResult>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                    progressUpload.visibility = View.GONE
                }
            }
        })
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(context!!, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val result = cursor?.getString(column_index!!)
        cursor?.close()
        return result
    }
}

package com.epitech.flatot.epicture.Views.FragmentBottom

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.CursorLoader
import android.support.v4.content.PermissionChecker.checkSelfPermission
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.epitech.flatot.epicture.Model.ImgurInterface
import com.epitech.flatot.epicture.Model.RetrofitInterface
import com.epitech.flatot.epicture.R
import com.epitech.flatot.epicture.Views.BottomNavActivity
import kotlinx.android.synthetic.main.fragment_upload.*
import kotlinx.android.synthetic.main.fragment_upload.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class UploadFragment : Fragment() {

    private val GALLERY = 1
    private val CAMERA = 2
    private var MyPicBinary: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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

            MyPicBinary = getImageUri(context!!, thumbnail!!)
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null)
        return Uri.parse(path)
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

    fun dialogUpload()
    {
        val pictureDialog = AlertDialog.Builder(context!!)
        pictureDialog.setTitle("Choose action")
        val pictureDialogItems = arrayOf("Take a photo", "Gallery")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> takePhotoFromCamera()
                1 -> choosePhotoFromGallery()
            }
        }
        pictureDialog.show()
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_upload, container, false)
        if (checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE),
                    777)
        }
        else {
            dialogUpload()
            rootView.btn_upload_imgur.setOnClickListener {
                upload_on_imgur()
            }
            rootView.imageView.setOnClickListener {
                dialogUpload()
            }
        }
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

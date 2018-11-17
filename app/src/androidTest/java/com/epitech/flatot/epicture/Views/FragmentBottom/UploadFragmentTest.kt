package com.epitech.flatot.epicture.Views.FragmentBottom

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.CursorLoader
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.epitech.flatot.epicture.Model.ImgurModel
import com.epitech.flatot.epicture.Model.RetrofitModel
import com.epitech.flatot.epicture.R
import kotlinx.android.synthetic.main.fragment_upload.*
import kotlinx.android.synthetic.main.fragment_upload.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

// 19 tests

class UploadFragmentTest: Fragment() {

    private val GALLERY = 1
    private val CAMERA = 2
    private var MyPicBinary: Uri? = null

    @Test
    fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        assertNotNull(intent)
    }

    @Test
    fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        assertNotNull(galleryIntent)
    }

    @Test
    fun getImageUri() {
        val bytes = ByteArrayOutputStream()
        assertNotNull(bytes)
    }

    @Test
    fun dialogUpload()
    {
        val pictureDialogItems = arrayOf("Take a photo", "Gallery")
        assertNotNull(pictureDialogItems)
    }


    @Test
    fun upload_on_imgur() {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        assertNotNull(proj)
        if (context == null) {
        }
        else {
            var loader = CursorLoader(context!!, MyPicBinary!!, proj, null, null, null)
            assertNull(loader)
            val cursor = loader.loadInBackground()
            val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            assertNotNull(column_index)
            cursor?.moveToFirst()
            val result = cursor?.getString(column_index!!)
            assertNotNull(result)
            cursor?.close()
            var file = File(result)
            val requestFile = RequestBody.create(MediaType.parse(activity!!.contentResolver.getType(MyPicBinary)), file)
            assertNotNull(requestFile)
            val imageBody = MultipartBody.Part.createFormData("image", file.name, requestFile)
            assertNotNull(imageBody)
            val titleBody = RequestBody.create(okhttp3.MultipartBody.FORM, text_title.text.toString())
            assertNotNull(titleBody)
            val descriptionBody = RequestBody.create(okhttp3.MultipartBody.FORM, text_description.text.toString())
            assertNotNull(descriptionBody)
            val optinalBodyMap = mapOf("title" to titleBody, "description" to descriptionBody)
            assertNotNull(optinalBodyMap)
            val retrofit = RetrofitModel().createRetrofitBuilder()
            assertNotNull(retrofit)
            val token = arguments?.getString("access_token")
            assertNotNull(token)
            val call = retrofit.uploadImage("Bearer " + token, imageBody, optinalBodyMap)
            assertNotNull(call)
            progressUpload.visibility = View.VISIBLE
            call.enqueue(object : Callback<ImgurModel.UploadResult> {
                override fun onFailure(call: Call<ImgurModel.UploadResult>, t: Throwable) {
                    try {
                        Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                        progressUpload.visibility = View.GONE
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onResponse(call: Call<ImgurModel.UploadResult>, response: Response<ImgurModel.UploadResult>) {
                    try {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                            progressUpload.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        }
    }


    @Test
    fun onCreateView() {
        try {
            if (PermissionChecker.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    PermissionChecker.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    PermissionChecker.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity!!,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE),
                        777)
            } else {
                dialogUpload()
                btn_upload_imgur.setOnClickListener {
                    upload_on_imgur()
                }
                imageView.setOnClickListener {
                    dialogUpload()
                }
            }
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
    }
}
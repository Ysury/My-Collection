package com.collect.mycollection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import database.ImageDbHelper
import android.Manifest
import android.widget.*


class HomePage : AppCompatActivity() {

        companion object {
            const val REQUEST_CODE = 100
        }

        private lateinit var dbHelper: ImageDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        dbHelper = ImageDbHelper(this)

        val addImageButton = findViewById<Button>(R.id.addImageButton)
        addImageButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
            } else {
                openGallery()
            }
        }

        imageList = findViewById<ListView>(R.id.imageList) // Initialize the imageList variable

        updateImageList()
    }


    private fun openGallery() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            val imageName = imageUri?.let { getImageName(it) }
            val imagePath = imageUri?.let { getPathFromUri(it) }

            if (imagePath != null && imageName != null) {
                dbHelper.insertImage(imageName, imagePath)
            }

            updateImageList()
        }
    }

    private fun getPathFromUri(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        if (cursor != null) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val path = cursor.getString(column_index)
            cursor.close()
            return path ?: ""
        } else {
            return uri.path.toString()
        }
    }

    private fun getImageName(uri: Uri): String {
            val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
            val cursor = contentResolver.query(uri, projection, null, null, null)
            var name = ""
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
            }
            cursor?.close()
            return name
        }
    private lateinit var imageList: ListView

        private fun updateImageList() {
            val images = dbHelper.getAllImages()

            val adapter = ImageAdapter(this, images)
            imageList.adapter = adapter

            imageList.setOnItemClickListener { _, _, position, _ ->
                val image = adapter.getItem(position)
                if (image != null) {
                    dbHelper.deleteImage(image.name)
                }

                updateImageList()
            }
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == REQUEST_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                }
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            dbHelper.close()
        }
    }
class ImageAdapter(context: Context, images: ArrayList<ImageDbHelper.Image>) : ArrayAdapter<ImageDbHelper.Image>(context, 0, images) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        }

        val image = getItem(position)
        val imageView = view?.findViewById<ImageView>(R.id.imageView)
        imageView?.setImageBitmap(BitmapFactory.decodeFile(image?.path))

        return view!!
    }
}





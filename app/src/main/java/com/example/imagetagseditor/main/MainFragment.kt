package com.example.imagetagseditor.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetagseditor.R
import com.example.imagetagseditor.main.adapter.ViewAdapter
import com.example.imagetagseditor.model.ImageData


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        const val REQUEST_CODE = 2
    }

    private val viewModel: MainViewModel by viewModels()
    private val imageData = ImageData(emptyMap())
    private lateinit var adapter: ViewAdapter
    private lateinit var imageView: ImageView
    private lateinit var loadButton: Button
    private lateinit var editButton: Button
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ViewAdapter(imageData.tags)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        imageView = view.findViewById(R.id.image_view)
        loadButton = view.findViewById(R.id.load)
        loadButton.setOnClickListener { openImage() }
        editButton = view.findViewById(R.id.edit)
        editButton.setOnClickListener {
            val args = Bundle()
            ImageData.editableTagNames.forEach { tagName ->
                val pair = imageData.tags.find { tag -> tag.first == tagName }
                val tagValue = pair?.second ?: ""
                args.putString(tagName, tagValue)
            }
            it.findNavController().navigate(R.id.action_mainFragment_to_editFragment, args)
        }
        recyclerView = view.findViewById(R.id.tags_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return view
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            loadImage(data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun openImage() {
        val pickPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPictureIntent, REQUEST_CODE)
    }

    private fun loadImage(intent: Intent?) {
        if (intent == null)
            return
        val pickedImage = intent.data ?: return
        imageView.setImageURI(pickedImage)
        val tags = loadExifTags(pickedImage, ImageData.tagNames)
        imageData.tags.clear()
        imageData.tags.addAll(tags)
        adapter.update()
    }

    private fun loadExifTags(uri: Uri, tagNames: List<String>): MutableList<Pair<String, String>> {
        val inputStream = context?.contentResolver?.openInputStream(uri)
        val metadata = ExifInterface(inputStream!!)
        val tags = emptyList<Pair<String, String>>().toMutableList()
        tagNames.forEach {
            val tag = metadata.getAttribute(it)
            if (!tag.isNullOrEmpty())
                tags.add(Pair(it, tag))
        }
        return tags
    }
}
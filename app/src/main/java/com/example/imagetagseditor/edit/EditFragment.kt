package com.example.imagetagseditor.edit

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetagseditor.R
import com.example.imagetagseditor.edit.adapter.ViewAdapter
import com.example.imagetagseditor.model.ImageData

class EditFragment : Fragment() {

    companion object {
        fun newInstance() = EditFragment()
    }

    private val viewModel: EditViewModel by viewModels()
    private val imageData = ImageData(emptyMap())
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            ImageData.editableTagNames.forEach {
                imageData.tags.add(Pair(it, requireArguments().getString(it) ?: ""))
            }
        }
        adapter = ViewAdapter(imageData.tags)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)
        recyclerView = view.findViewById(R.id.editable_tags_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return view
    }
}
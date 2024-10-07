package com.example.pdf_viewer_recyclerview

import CommonPdfViewerAdapter
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.pdf_viewer_recyclerview.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    var adapter: CommonPdfViewerAdapter? = null


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val input = ParcelFileDescriptor.open(
            File(this.cacheDir, "downloaded_file.pdf"),
            ParcelFileDescriptor.MODE_READ_ONLY
        )
        val renderer = PdfRenderer(input)

        val width = this.display?.width ?: 100
        adapter = CommonPdfViewerAdapter(renderer, width)
        binding.parentRv.adapter = adapter

    }
}
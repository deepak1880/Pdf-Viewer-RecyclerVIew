import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pdf_viewer_recyclerview.databinding.ViewerImageItemBinding

class CommonPdfViewerAdapter(
    private val renderer: PdfRenderer,
    private val width: Int,
) : RecyclerView.Adapter<CommonPdfViewerAdapter.PdfReaderViewHolder>() {

    inner class PdfReaderViewHolder(private val imageView: ImageView) :
        RecyclerView.ViewHolder(imageView) {

        private lateinit var scaleGestureDetector: ScaleGestureDetector
        private var scaleFactor = 1.0f
        private val imageMatrix = Matrix()

        init {
            // Initialize the ScaleGestureDetector for pinch-to-zoom
            scaleGestureDetector = ScaleGestureDetector(
                imageView.context,
                object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    override fun onScale(detector: ScaleGestureDetector): Boolean {
                        scaleFactor *= detector.scaleFactor
                        scaleFactor =
                            scaleFactor.coerceIn(0.5f, 3.0f) // Limit zoom level between 0.5x and 3x

                        // Apply the zoom using matrix
                        imageMatrix.setScale(scaleFactor, scaleFactor)
                        imageView.imageMatrix = imageMatrix
                        imageView.invalidate()

                        return true
                    }
                })

            // Set the touch listener to detect pinch gestures
            imageView.setOnTouchListener { _, event ->
                scaleGestureDetector.onTouchEvent(event)
                true
            }
        }

        fun onBind(data: Bitmap) {
            scaleFactor = 1.0f
            imageMatrix.reset()
            imageView.imageMatrix = imageMatrix
            Glide.with(imageView).load(data).into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfReaderViewHolder {
        val root = ViewerImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PdfReaderViewHolder(root.root)
    }

    override fun onBindViewHolder(holder: PdfReaderViewHolder, position: Int) {
        val page = renderer.openPage(position)
        holder.onBind(page.renderAndClose(width))
    }

    override fun getItemCount(): Int {
        return renderer.pageCount
    }

    private fun PdfRenderer.Page.renderAndClose(width: Int) = use {
        val bitmap = createBitmap(width)
        render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        bitmap
    }

    private fun PdfRenderer.Page.createBitmap(bitmapWidth: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(
            bitmapWidth, (bitmapWidth.toFloat() / width * height).toInt(), Bitmap.Config.ARGB_8888
        )

//        val canvas = Canvas(bitmap)
//        canvas.drawColor(Color.WHITE)
//        canvas.drawBitmap(bitmap, 0f, 0f, null)

        return bitmap
    }
}
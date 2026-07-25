package com.kayo.minimaltimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.kayo.minimaltimer.utils.HelperMethods

class GalleryFragment : Fragment() {

    private val images = intArrayOf(
        android.R.drawable.ic_menu_report_image,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_slideshow,
        android.R.drawable.ic_menu_compass,
        android.R.drawable.ic_menu_directions
    )

    private var currentIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)

        val imageSwitcher = view.findViewById<ImageSwitcher>(R.id.imageSwitcher)
        val btnPrevious = view.findViewById<Button>(R.id.btnPrevious)
        val btnNext = view.findViewById<Button>(R.id.btnNext)
        val gridView = view.findViewById<GridView>(R.id.gridView)

        // MÓDULO 4: Configuração do ImageSwitcher
        imageSwitcher.setFactory {
            val imageView = ImageView(requireContext())
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            imageView
        }
        imageSwitcher.setImageResource(images[currentIndex])

        btnPrevious.setOnClickListener {
            currentIndex = if (currentIndex > 0) currentIndex - 1 else images.size - 1
            imageSwitcher.setImageResource(images[currentIndex])
            HelperMethods.showToast(requireContext(), "Imagem Anterior")
        }

        btnNext.setOnClickListener {
            currentIndex = if (currentIndex < images.size - 1) currentIndex + 1 else 0
            imageSwitcher.setImageResource(images[currentIndex])
            HelperMethods.showToast(requireContext(), "Próxima Imagem")
        }

        // MÓDULO 4: Configuração do GridView (Galeria)
        gridView.adapter = object : BaseAdapter() {
            override fun getCount(): Int = images.size
            override fun getItem(position: Int): Any = images[position]
            override fun getItemId(position: Int): Long = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val imageView = if (convertView == null) {
                    // MÓDULO 4: Uso de ImageView
                    ImageView(requireContext()).apply {
                        layoutParams = AbsListView.LayoutParams(250, 250)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        setPadding(8, 8, 8, 8)
                    }
                } else {
                    convertView as ImageView
                }
                imageView.setImageResource(images[position])
                return imageView
            }
        }

        gridView.setOnItemClickListener { _, _, position, _ ->
            currentIndex = position
            imageSwitcher.setImageResource(images[currentIndex])
            HelperMethods.showToast(requireContext(), "Selecionado item $position")
        }

        return view
    }
}

package com.example.helmonzy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

class MyAdapter (private val imageList:ArrayList<Images>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =  LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentImage = imageList[position]

        holder.time.text = currentImage.time

        // Ambil URL gambar dari objek currentImage
        val imageUrl = currentImage.image_url

        // Tampilkan gambar menggunakan Glide
        Glide.with(holder.image_url.context)
            .load(imageUrl)
            .into(holder.image_url)

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val  time : TextView = itemView.findViewById(R.id.timeTextView)
        val  image_url : PhotoView = itemView.findViewById(R.id.photoView) //Dengan fitur zoom PhotoView dari com.github.chrisbanes:PhotoView:2.3.0

    }
}
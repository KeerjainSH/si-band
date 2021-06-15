package com.machina.siband.admin.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.databinding.ItemLantaiBinding
import com.machina.siband.model.Ruangan

class AdminListRuanganAdapter(
    private val onItemClick: (Ruangan) -> (Unit)
): RecyclerView.Adapter<ItemRuangan>() {

    private var dataSet = listOf<Ruangan>()

    fun setData(newData: List<Ruangan>){
        dataSet = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemRuangan {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = ItemLantaiBinding.inflate(layoutInflater, parent, false)
        return ItemRuangan(binding)
    }

    override fun onBindViewHolder(holder: ItemRuangan, position: Int) {
        holder.onBind(dataSet[position], onItemClick)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}

class ItemRuangan(binding: ItemLantaiBinding): RecyclerView.ViewHolder(binding.root) {

    val nama = binding.itemLantaiNamaLantai
    val detailButton = binding.itemLantaiDetail
    val hapusButton = binding.itemLantaiHapus

    fun onBind(ruangan: Ruangan, onItemClick: (Ruangan) -> Unit) {
        nama.text = ruangan.nama

        detailButton.setOnClickListener {
            onItemClick(ruangan)
        }
    }

}
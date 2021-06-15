package com.machina.siband.admin.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.databinding.ItemLantaiBinding

class AdminListItemAdapter: RecyclerView.Adapter<ItemItem>() {

    private var dataSet = listOf<String>()

    fun setData(newData: List<String>){
        dataSet = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemItem {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = ItemLantaiBinding.inflate(layoutInflater, parent, false)
        return ItemItem(binding)
    }

    override fun onBindViewHolder(holder: ItemItem, position: Int) {
        holder.onBind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }


}


class ItemItem(binding: ItemLantaiBinding): RecyclerView.ViewHolder(binding.root) {

    val nama = binding.itemLantaiNamaLantai
    val detailButton = binding.itemLantaiDetail
    val hapusButton = binding.itemLantaiHapus

    fun onBind(namaItem: String) {
        detailButton.visibility = View.GONE

        nama.text = namaItem
    }

}
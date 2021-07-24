package com.machina.siband.admin.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.databinding.ItemLantaiBinding

class AdminListItemAdapter(private val listener: (String) -> Unit) :
  RecyclerView.Adapter<ItemItem>() {

  private var dataSet = arrayListOf<String>()

  fun setData(newData: ArrayList<String>) {
    dataSet = newData
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemItem {
    val inflater = LayoutInflater.from(parent.context)

    val binding = ItemLantaiBinding.inflate(inflater, parent, false)
    return ItemItem(binding)
  }

  override fun onBindViewHolder(holder: ItemItem, position: Int) {
    holder.onBind(dataSet[position], listener)
  }

  override fun getItemCount(): Int {
    return dataSet.size
  }

}


class ItemItem(binding: ItemLantaiBinding) : RecyclerView.ViewHolder(binding.root) {

  val nama = binding.itemLantaiNamaLantai
  val detailButton = binding.itemLantaiDetail
  val hapusButton = binding.itemLantaiHapus

  fun onBind(namaItem: String, listener: (String) -> Unit) {
    detailButton.visibility = View.GONE
    nama.text = namaItem
    hapusButton.setOnClickListener {
      listener(namaItem)
    }
  }

}
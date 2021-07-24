package com.machina.siband.admin.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.databinding.ItemLantaiBinding
import com.machina.siband.model.Lantai

class AdminListLantaiAdapter(
  private val onItemClick: (Lantai) -> Unit,
  private val onItemDelete: (Lantai) -> Unit
) : RecyclerView.Adapter<ItemLantai>() {


  private var dataSet = listOf<Lantai>()

  fun setData(newData: List<Lantai>) {
    dataSet = newData
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemLantai {
    val layoutInflater = LayoutInflater.from(parent.context)

    val binding = ItemLantaiBinding.inflate(layoutInflater, parent, false)
    return ItemLantai(binding)
  }

  override fun onBindViewHolder(holder: ItemLantai, position: Int) {
    holder.onBind(dataSet[position], onItemClick, onItemDelete)
  }

  override fun getItemCount(): Int {
    return dataSet.size
  }
}

class ItemLantai(binding: ItemLantaiBinding) : RecyclerView.ViewHolder(binding.root) {

  private val namaLantai = binding.itemLantaiNamaLantai
  private val detailLantai = binding.itemLantaiDetail
  private val hapusLantai = binding.itemLantaiHapus

  fun onBind(
    lantai: Lantai,
    onItemClick: (Lantai) -> Unit,
    onItemDelete: (Lantai) -> Unit
  ) {
    namaLantai.text = lantai.nama
    detailLantai.setOnClickListener { onItemClick(lantai) }
    hapusLantai.setOnClickListener { onItemDelete(lantai) }
  }
}
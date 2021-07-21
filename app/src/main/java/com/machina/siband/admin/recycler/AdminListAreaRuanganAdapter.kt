package com.machina.siband.admin.recycler

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.databinding.ItemLantaiBinding
import com.machina.siband.model.AreaRuangan

class AdminListLetakRuanganAdapter(private val onDeleteAreaRuangan: (AreaRuangan) -> Unit) :
  RecyclerView.Adapter<ItemAreaRuangan>() {

  private var dataSet = listOf<AreaRuangan>()

  fun setData(data: List<AreaRuangan>) {
    dataSet = data
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAreaRuangan {
    val inflater = LayoutInflater.from(parent.context)

    val binding = ItemLantaiBinding.inflate(inflater, parent, false)
    return ItemAreaRuangan(binding)
  }

  override fun onBindViewHolder(holder: ItemAreaRuangan, position: Int) {
    holder.onBind(dataSet[position], onDeleteAreaRuangan)
  }

  override fun getItemCount(): Int {
    return dataSet.size
  }


}


class ItemAreaRuangan(binding: ItemLantaiBinding) : RecyclerView.ViewHolder(binding.root) {

  private val container = binding.itemLantaiContainer
  private val textView = binding.itemLantaiNamaLantai
  private val detail = binding.itemLantaiDetail
  private val hapus = binding.itemLantaiHapus

  fun onBind(areaRuangan: AreaRuangan, onDeleteAreaRuangan: (AreaRuangan) -> Unit) {
    detail.visibility = View.GONE
    textView.text = areaRuangan.nama
    container.setBackgroundColor(Color.parseColor(areaRuangan.warna))
    hapus.setOnClickListener {
      onDeleteAreaRuangan(areaRuangan)
    }
  }
}


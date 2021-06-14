package com.machina.siband.user.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.databinding.ItemLantaiBinding
import com.machina.siband.model.Lantai

class AdminListLantaiAdapter: RecyclerView.Adapter<ItemLantai>() {

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
        holder.onBind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}




class ItemLantai(binding: ItemLantaiBinding): RecyclerView.ViewHolder(binding.root) {

    private val namaLantai = binding.itemLantaiNamaLantai

    fun onBind(lantai: Lantai) {
        namaLantai.text = lantai.nama
    }
}
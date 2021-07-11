package com.machina.siband.user.recycler

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.databinding.ItemRuanganBinding
import com.machina.siband.model.Ruangan

class ListRuanganAdapter(
        private val onItemRuanganClicked: (String) -> (Unit))
    : RecyclerView.Adapter<ItemRuangan>() {

    private var dataSet = listOf<Ruangan>()

    fun setData(newList: List<Ruangan>) {
        dataSet = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemRuangan {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRuanganBinding.inflate(layoutInflater, parent, false)
        return ItemRuangan(binding)
    }

    override fun onBindViewHolder(holder: ItemRuangan, position: Int) {
        holder.onBind(dataSet[position], onItemRuanganClicked)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}


class ItemRuangan(binding: ItemRuanganBinding): RecyclerView.ViewHolder(binding.root) {
    private val textView = binding.itemRuanganName
    private val container = binding.itemRuanganContainer
    private val linearLayout = binding.itemRuanganLinearLayout

    fun onBind(ruangan: Ruangan, listener: (String) -> (Unit)) {
        val text = ruangan.nama
        textView.text = text
        linearLayout.setBackgroundColor(Color.parseColor(ruangan.warna))
        container.setOnClickListener {
            listener(ruangan.nama)
        }
    }
}
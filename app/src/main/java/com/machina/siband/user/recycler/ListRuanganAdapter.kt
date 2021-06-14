package com.machina.siband.user.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.databinding.ItemRuanganBinding

class ListRuanganAdapter(
        private val onItemRuanganClicked: (String) -> (Unit))
    : RecyclerView.Adapter<ItemRuangan>() {

    private var dataSet = listOf<String>()

    fun setData(newList: List<String>) {
        dataSet = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemRuangan {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRuanganBinding.inflate(layoutInflater, parent, false)
        return ItemRuangan(binding)
    }

    override fun onBindViewHolder(holder: ItemRuangan, position: Int) {
        holder.onBind(dataSet[position], position + 1, onItemRuanganClicked)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}


class ItemRuangan(binding: ItemRuanganBinding): RecyclerView.ViewHolder(binding.root) {

    private val textView = binding.itemRuanganName
    private val container = binding.itemRuanganContainer

    fun onBind(name: String, position: Int, listener: (String) -> (Unit)) {
        val text = "$position. $name"
        textView.text = text
        container.setOnClickListener {
            listener(name)
        }
    }
}
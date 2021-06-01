package com.machina.siband.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.R
import com.machina.siband.databinding.ItemLaporanRuanganBinding
import com.machina.siband.user.model.LaporanRuangan

class ListLaporanRuanganAdapter(private val listener: (LaporanRuangan) -> Unit): RecyclerView.Adapter<ItemLaporanRuangan>() {

    private var dataSet = listOf<LaporanRuangan>()

    fun setData(newSet: List<LaporanRuangan>) {
        dataSet = newSet
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemLaporanRuangan {
        val inflater = LayoutInflater.from(parent.context)

        val binding = ItemLaporanRuanganBinding.inflate(inflater, parent, false)
        return ItemLaporanRuangan(binding)
    }

    override fun onBindViewHolder(holder: ItemLaporanRuangan, position: Int) {
        holder.onBind(dataSet[position], listener)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }


}


class ItemLaporanRuangan(binding: ItemLaporanRuanganBinding): RecyclerView.ViewHolder(binding.root) {

    val itemContainer = binding.itemLaporanRuanganContainer
    val itemText = binding.itemLaporanRuanganNama
    val itemCheck = binding.itemLaporanRuanganCheck

    fun onBind(data: LaporanRuangan, listener: (LaporanRuangan) -> Unit) {
        itemText.text = data.nama
        resolveChecked(data.isChecked)
        itemContainer.setOnClickListener {
            listener(data)
        }

    }

    private fun resolveChecked(isChecked: Boolean) {
        if (isChecked) itemCheck.setImageResource(R.drawable.ic_baseline_check_box_24)
        else itemCheck.setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24)
    }
}
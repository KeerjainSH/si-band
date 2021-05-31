package com.machina.siband.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.databinding.ItemComplaintBinding
import com.machina.siband.user.listener.OnItemLaporanClickListener
import com.machina.siband.user.model.LaporanBase
import com.machina.siband.user.model.LaporanRuangan
import kotlin.random.Random

class ListComplaintAdapter(private val listener: (LaporanRuangan) -> Unit): RecyclerView.Adapter<ComplaintVh>() {

    private var dataSet = listOf<LaporanRuangan>()

    fun setData(newData: List<LaporanRuangan>) {
        dataSet = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintVh {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemComplaintBinding.inflate(
                layoutInflater,
                parent,
                false)

        return ComplaintVh(binding)
    }

    override fun onBindViewHolder(holder: ComplaintVh, position: Int) {
        holder.onBind(dataSet[position], listener)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}



class ComplaintVh(binding: ItemComplaintBinding): RecyclerView.ViewHolder(binding.root) {

    private val container = binding.itemComplaintContainer
    private val tanggalTv = binding.itemComplaintTanggal
    private val lokasiTv = binding.itemComplaintLokasi
    private val itemTv = binding.itemComplaintName
    private val statusTv = binding.itemComplaintStatus

    fun onBind(data: LaporanRuangan, listener: (LaporanRuangan) -> Unit) {
        lokasiTv.text = data.lokasi
        tanggalTv.text = data.tanggal
        itemTv.text = data.nama

        container.setOnClickListener { listener(data) }
    }
}
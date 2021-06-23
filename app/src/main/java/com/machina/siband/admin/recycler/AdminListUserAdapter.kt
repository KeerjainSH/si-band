package com.machina.siband.admin.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.databinding.ItemAccountBinding
import com.machina.siband.model.Account

class AdminListUserAdapter: RecyclerView.Adapter<AccountItem>() {

    private var data = listOf<Account>()

    fun setData(newData: List<Account>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountItem {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAccountBinding.inflate(layoutInflater, parent, false)
        return AccountItem(binding)
    }

    override fun onBindViewHolder(holder: AccountItem, position: Int) {
        holder.onBind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }


}

class AccountItem(binding: ItemAccountBinding): RecyclerView.ViewHolder(binding.root) {
    val nama = binding.itemAccountNama
    val email = binding.itemAccountEmail
    val tipe = binding.itemAccountTipe

    fun onBind(account: Account) {
        nama.text = account.nama
        email.text = account.email
        tipe.text = account.tipeAkun
    }
}
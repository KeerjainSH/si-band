package com.machina.siband.admin.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.machina.siband.R
import com.machina.siband.databinding.DialogAddItemBinding

class DialogAddRuangan(
  private val dialogAddItemListener: DialogAddItemListener,
  message: String,
  private val arrayAreaRuangan: Array<String>
) : DialogFragment() {

  internal lateinit var listener: DialogAddItemListener
  private var textMessage: String = message

  interface DialogAddItemListener {
    fun onDialogPositiveClick(dialog: DialogFragment, itemName: String, area: String)
    fun onDialogNegativeClick(dialog: DialogFragment)
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return activity?.let { it ->
      // Use the Builder class for convenient dialog construction
      val builder = AlertDialog.Builder(it)
      val binding = DialogAddItemBinding.inflate(layoutInflater)
      val areaAdapter =
        ArrayAdapter(requireContext(), R.layout.item_list_dropdown, arrayAreaRuangan)
      (binding.dialogAddItemKelompok.editText as? AutoCompleteTextView)?.setAdapter(areaAdapter)

      builder.setView(binding.root)
        .setMessage(textMessage)
        .setPositiveButton("Add") { _, _ ->
          val itemName = binding.dialogAddItemNama.editText?.text.toString()
          val area = binding.dialogAddItemKelompok.editText?.text.toString()

          if (itemName.isBlank() || area.isBlank()) {
            Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_LONG).show()
            return@setPositiveButton
          }

          listener.onDialogPositiveClick(this, itemName, area)
        }
        .setNegativeButton("Cancel") { _, _ ->
          listener.onDialogNegativeClick(this)
        }
      // Create the AlertDialog object and return it
      builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    try {
      listener = dialogAddItemListener
    } catch (e: ClassCastException) {
      throw ClassCastException(
        (context.toString() +
          " must implement DialogAddItemListener")
      )
    }
  }
}
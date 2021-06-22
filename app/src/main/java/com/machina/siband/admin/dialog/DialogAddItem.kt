package com.machina.siband.admin.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.machina.siband.databinding.DialogAddItemBinding

class DialogAddItem(private val dialogAddItemListener: DialogAddItemListener, message: String) : DialogFragment() {

    internal lateinit var listener: DialogAddItemListener
    private var textMessage: String = message

    interface DialogAddItemListener {
        fun onDialogPositiveClick(dialog: DialogFragment, itemName: String)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val binding = DialogAddItemBinding.inflate(layoutInflater)
            builder.setView(binding.root)
                .setMessage(textMessage)
                .setPositiveButton("Add") { _, _ ->
                    val itemName = binding.dialogAddItemNama.editText?.text.toString()
                    if (itemName.isNotEmpty()) {
                        listener.onDialogPositiveClick(this, itemName)
                    } else {
                        listener.onDialogNegativeClick(this)
                    }
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
            throw ClassCastException((context.toString() +
                    " must implement DialogAddItemListener"))
        }
    }
}
package com.example.instacleaner.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.instacleaner.R

class LogoutDialog(private val onApprove:() -> Unit):DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.logout_username)
                .setPositiveButton(R.string.yes_logout
                ) { _, _ ->
                    onApprove()
                    dismiss()
                }
                .setNegativeButton(R.string.no
                ) { _, _ ->

                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}
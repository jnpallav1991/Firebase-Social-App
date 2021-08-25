package com.encoding.socialapp.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog

object Constant {

    fun alertDialog(context: Context?, msg: String) {
        try {
            val dialogBuilder = AlertDialog.Builder(context!!)
            dialogBuilder.setCancelable(false)
            dialogBuilder.setMessage(msg)
            dialogBuilder.setPositiveButton("Ok") { dialog, whichButton ->
                dialog.dismiss()
            }

            val b = dialogBuilder.create()
            b.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openKeyBoard(context: Context, view: View)
    {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyBoard(context: Context, view: View)
    {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
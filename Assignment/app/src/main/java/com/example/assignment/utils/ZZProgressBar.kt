package com.example.assignment
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.ProgressBar


object ZZProgressBar {

    private lateinit var mDialog: Dialog

    fun showProgress(mContext: Context) {
        mDialog = Dialog(mContext)
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog.setContentView(R.layout.progressbar_layout)
        mDialog.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(true)
        if (mDialog.isShowing) {
            mDialog.dismiss()
        } else {
            try {
                mDialog.show()
            }catch (e :Exception){
                e.printStackTrace()
            }

        }
    }

    fun hideProgress() {
        mDialog.dismiss()
    }

}
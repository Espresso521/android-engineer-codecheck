package jp.co.yumemi.android.code_check.ui

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import dagger.hilt.android.qualifiers.ActivityContext
import jp.co.yumemi.android.code_check.R
import java.util.*
import javax.inject.Inject

class ProgressDismissDialog @Inject constructor(
    @ActivityContext val context: Context,
) {
    private var dialog: AlertDialog? = null
    private var startTime: Long = 0

    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setView(LayoutInflater.from(context).inflate(R.layout.more_loading, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.show()
        startTime = System.currentTimeMillis()
    }

    fun dismissDialog() {
        val checkTime = System.currentTimeMillis() - startTime
        if (checkTime < 2000) {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    dialog?.dismiss()
                }
            }, 2000 - checkTime)
        } else {
            dialog?.dismiss()
        }
    }

    fun startDismissProgressDialog(delay: Long = 2000L) {
        val builder = AlertDialog.Builder(context)
        builder.setView(LayoutInflater.from(context).inflate(R.layout.more_loading, null))
        builder.setCancelable(false)
        val dismissDialog = builder.create()
        dismissDialog.show()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                dismissDialog.dismiss()
            }
        }, delay)
    }

}

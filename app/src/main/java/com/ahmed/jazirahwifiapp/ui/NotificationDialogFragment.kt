package com.ahmed.jazirahwifiapp.ui

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.ahmed.jazirahwifiapp.R
import android.view.*
import android.view.Gravity
import androidx.annotation.Nullable
import androidx.navigation.fragment.navArgs
import com.ahmed.jazirahwifiapp.databinding.FragmentNotificationDialogBinding
import com.ahmed.jazirahwifiapp.utils.Constants.DIALOG_NOTIFICATION_DELAY
import kotlinx.android.synthetic.main.fragment_notification_dialog.*
import kotlinx.coroutines.*


class NotificationDialogFragment : DialogFragment() {
    private var _binding: FragmentNotificationDialogBinding? = null
    private val binding get() = _binding
    private lateinit var notificationViewModel: NotificationViewModel
    private val args by navArgs<NotificationDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationDialogBinding.inflate(inflater, container, false)
        notificationViewModel =
            ViewModelProvider(requireActivity()).get(NotificationViewModel::class.java)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(100)
        binding?.apply {
            this.errorClose.setOnClickListener {
                dismiss()
            }
            this.successClose.setOnClickListener {
                dismiss()
            }
            this.infoClose.setOnClickListener {
                dismiss()
            }
        }
        args.let {
            if (args.isError) {
                binding?.apply {
                    this.lyErrorNotification.visibility = View.VISIBLE
                    this.lySuccessNotification.visibility = View.GONE
                    this.lyInfoNotification.visibility = View.GONE
                    this.errorMessage.text = args.message
                }
            }
            if (args.isInfo) {
                this.lyErrorNotification.visibility = View.GONE
                this.lyInfoNotification.visibility = View.VISIBLE
                this.lySuccessNotification.visibility = View.GONE
                this.infoErrorMessage.text = args.message
            }
            if (args.isSuccess) {
                this.lyErrorNotification.visibility = View.GONE
                this.lyInfoNotification.visibility = View.GONE
                this.lySuccessNotification.visibility = View.VISIBLE
                this.successMessage.text = args.message
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            delay(DIALOG_NOTIFICATION_DELAY)
            withContext(Dispatchers.Main) {
                dismiss()
            }
        }
    }

    private fun DialogFragment.setWidthPercent(percentage: Int) {
        val percent = percentage.toFloat() / 100
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * percent
        dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }


    override fun onCreateDialog(@Nullable savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NO_TITLE, R.style.DialogTop)
        val dialog = super.onCreateDialog(savedInstanceState)
        val window = dialog.window
        window?.setGravity(Gravity.TOP)
        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

}


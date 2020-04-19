package com.covilights.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("visibility")
fun View.setVisibleOrGone(isVisible: Boolean?) {
    visibility = if (isVisible == true) View.VISIBLE else View.GONE
}

@BindingAdapter("htmlText")
fun TextView.setHtmlTextValue(htmlText: String?) {
    if (htmlText == null) return
    val result: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(htmlText)
    }
    text = result
}

@BindingAdapter("linkMovementMethod")
fun TextView.setLinkMovementMethod(isLinkMovementMethod: Boolean) {
    movementMethod = if (isLinkMovementMethod) LinkMovementMethod.getInstance() else null
}

/*
 * Copyright 2020 CoviLights GbR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.covilights.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * BindingAdapter which allows setting a [View] visibility [View.VISIBLE]/[View.GONE] using a boolean value.
 */
@BindingAdapter("visibility")
fun View.setVisibleOrGone(isVisible: Boolean?) {
    visibility = if (isVisible == true) View.VISIBLE else View.GONE
}

/**
 * BindingAdapter which allows setting [TextView] text a html value.
 */
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

/**
 * BindingAdapter which enables hyperlinking a [TextView] contents.
 */
@BindingAdapter("linkMovementMethod")
fun TextView.setLinkMovementMethod(isLinkMovementMethod: Boolean) {
    movementMethod = if (isLinkMovementMethod) LinkMovementMethod.getInstance() else null
}

/*
 * Copyright (C) 2017 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.recordedweather.activity.activity

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity

import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

import com.example.recordedweather.R
import com.skydoves.powermenu.CircularEffect
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem

object PowerMenuUtils {

    fun getHamburgerPowerMenu(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        onMenuItemClickListener: OnMenuItemClickListener<PowerMenuItem>,
        onDismissedListener: () -> Int
    ): PowerMenu {


        return PowerMenu.Builder(context)
            .addItem(PowerMenuItem("Rate Me", false))
            .addItem(PowerMenuItem("Share App", false))
            .addItem(PowerMenuItem("About us", false))
            .setAutoDismiss(true)
            .setLifecycleOwner(lifecycleOwner)
            .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT)
            .setCircularEffect(CircularEffect.BODY)
            .setMenuRadius(10f)
            .setMenuShadow(10f)
            .setTextColor(ContextCompat.getColor(context, R.color.md_grey_800))
            .setTextSize(12)
            .setTextGravity(Gravity.CENTER)
            .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
            .setSelectedTextColor(Color.WHITE)
            .setMenuColor(Color.WHITE)
            .setSelectedMenuColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .setPreferenceName("HamburgerPowerMenu")
            .setInitializeRule(Lifecycle.Event.ON_CREATE, 0)
            .build()
    }


}

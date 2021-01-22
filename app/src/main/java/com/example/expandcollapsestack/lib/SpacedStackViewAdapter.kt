package com.example.expandcollapsestack.lib

import android.view.View
import android.view.ViewGroup

abstract class SpacedStackViewAdapter {
    abstract fun getItemCount(): Int
    abstract fun onBindView(parent: ViewGroup, index: Int): View
    abstract fun onBindCTA(parent: ViewGroup, index: Int): View
}
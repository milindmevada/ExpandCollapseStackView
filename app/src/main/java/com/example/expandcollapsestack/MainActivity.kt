package com.example.expandcollapsestack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.expandcollapsestack.lib.SpacedStackView
import com.example.expandcollapsestack.lib.SpacedStackViewAdapter
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.expanded_view.view.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spacedStackView.setAdapter(TestAdapter(spacedStackView))
    }

    class TestAdapter(private val stackView: SpacedStackView) : SpacedStackViewAdapter() {
        override fun getItemCount(): Int {
            return 4
        }

        override fun onBindView(parent: ViewGroup, index: Int): View {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.expanded_view, parent, false)
            view.tvExpanded.text = "Expanded $index"
            view.tvCollapsed.text = "Collapsed $index"
            view.setOnClickListener {
                //stackView.expandViewAt(index)
            }
            return view
        }


        override fun onBindCTA(parent: ViewGroup, index: Int): View {
            val button = MaterialButton(parent.context)
            button.text = "Go to ${index + 1}"
            button.setOnClickListener {
                stackView.goToNextItem()
            }
            return button
        }

    }

}


package com.examples.teamproject

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.examples.teamproject.databinding.FragmentCompareBinding

class CompareFragment : Fragment() {
    val binding by lazy { FragmentCompareBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLayout()
        return binding.root
    }

    private fun initLayout() {
        binding.buttonShare.setOnClickListener {
            val intent = Intent(this.context, CompareShareActivity::class.java)
            startActivity(intent)
        }

        binding.buttonCompare.setOnClickListener {
            val intent = Intent(context, CompareCalendarActivity::class.java)
            startActivity(intent)
        }
    }
}
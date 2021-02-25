package com.shawn.onestepmessageDemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelStore
import androidx.navigation.fragment.findNavController
import com.shawn.oneStepMessage.OSM
import com.shawn.onestepmessageDemo.viewModel.DemoViewModel
import org.w3c.dom.Text

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        //监听消息1
        OSM.with(DemoViewModel::class.java).getMessage1().observeEvent(this, ViewModelStore()){
            view.findViewById<TextView>(R.id.textView_first_message1).text = it
        }

        //监听消息2
        OSM.with(DemoViewModel::class.java).getMessage2().observeEvent(this, ViewModelStore()){
            view.findViewById<TextView>(R.id.textView_first_message2).text = it.toString()
        }
    }
}
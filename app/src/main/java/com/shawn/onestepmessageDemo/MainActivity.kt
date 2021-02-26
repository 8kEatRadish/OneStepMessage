package com.shawn.onestepmessageDemo

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelStore
import com.shawn.oneStepMessage.OSM
import com.shawn.onestepmessageDemo.viewModel.DemoViewModel
import com.shawn.onestepmessageDemo.viewModel.bean.Bean

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            //发送一个消息1
            OSM.with(DemoViewModel::class.java).message1.postEventValue("更改message1了 random = ${(0..100).random()}")
        }
        findViewById<FloatingActionButton>(R.id.fab_2).setOnClickListener {
            //发送一个消息2
            OSM.with(DemoViewModel::class.java).message2.postEventValue(Bean("name${(0..100).random()}","feature1",(0..100).random(),true))
        }
        //监听消息1
        OSM.with(DemoViewModel::class.java).message1.observeEvent(this, ViewModelStore()){
            Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
        }
        //监听消息2
        OSM.with(DemoViewModel::class.java).message2.observeEvent(this, ViewModelStore()){
            Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this,SecondActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
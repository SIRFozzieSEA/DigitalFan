package com.codef.digitalfan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ManageAlarmsActivity : AppCompatActivity() {

//    private lateinit var recyclerView: RecyclerView
//    private lateinit var viewAdapter: RecyclerView.Adapter<*>
//    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_alarms)

//        val alarmKeys = intent.getStringArrayListExtra("alarmKeys")
//
//        viewManager = LinearLayoutManager(this)
//        viewAdapter = AlarmAdapter(alarmKeys)
//
//        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
//            setHasFixedSize(true)
//            layoutManager = viewManager
//            adapter = viewAdapter
//        }
//
//        val addButton: Button = findViewById(R.id.add_button)
//        addButton.setOnClickListener {
//            // Implement functionality to add an alarm
//        }
    }
}

//class AlarmAdapter(private val myDataset: List<String>) :
//    RecyclerView.Adapter<AlarmAdapter.MyViewHolder>() {
//
//    class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val textView = LayoutInflater.from(parent.context)
//            .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
//        return MyViewHolder(textView)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.textView.text = myDataset[position]
//    }
//
//    override fun getItemCount() = myDataset.size
//}
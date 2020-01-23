package appnexus.com.appnexussdktestapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import appnexus.com.appnexussdktestapp.adapter.AdRecyclerAdapter
import appnexus.com.appnexussdktestapp.listener.RecyclerItemClickListener
import kotlinx.android.synthetic.main.activity_marsettings.*

class MARSettingsActivity : AppCompatActivity(), RecyclerItemClickListener{

    var selectedAdType = ""
    var selectedBidType = ""
    var arrayListAdType = ArrayList<String>()
    var arrayListBidType = ArrayList<String>()
    var arrayListRecycler = ArrayList<String>()
    lateinit var spinnerAdType: Spinner
    lateinit var spinnerBidType: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marsettings)
        setupSpinners()
        setupButtonClick()
        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerList.layoutManager = layoutManager
        recyclerList.adapter = AdRecyclerAdapter(arrayListRecycler, this, this)
    }

    private fun setupButtonClick() {
        var btnAdd = findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener(View.OnClickListener {
            if (!TextUtils.isEmpty(selectedAdType) && !TextUtils.isEmpty(selectedBidType)) {
                arrayListAdType.add(selectedAdType)
                arrayListBidType.add(selectedBidType)
                arrayListRecycler.add("$selectedAdType($selectedBidType)")
                spinnerAdType.setSelection(0)
                spinnerBidType.setSelection(0)
                recyclerList.adapter!!.notifyDataSetChanged()
            }
        })

        var btnLoad: Button = findViewById(R.id.btnLoad)
        btnLoad.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MARSettingsActivity, MARLoadAndDisplayActivity::class.java)
            intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
            intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
            startActivity(intent)
        })
    }

    private fun setupSpinners() {
        spinnerAdType = findViewById(R.id.spnrAdUnitType)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.ad_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerAdType.adapter = adapter
        }

        spinnerAdType.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                selectedAdType = resources.getStringArray(R.array.ad_type_array)[pos]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Another interface callback
            }
        }

        spinnerBidType = findViewById(R.id.spnrBidType)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.bid_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerBidType.adapter = adapter
        }

        spinnerBidType.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                selectedBidType = resources.getStringArray(R.array.bid_type_array)[pos]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Another interface callback
            }
        }
    }

    override fun onItemClick(position: Int) {
        arrayListRecycler.removeAt(position)
        arrayListAdType.removeAt(position)
        arrayListBidType.removeAt(position)
        recyclerList.adapter!!.notifyDataSetChanged()
    }
}

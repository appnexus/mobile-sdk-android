package appnexus.com.trackertestapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import appnexus.com.trackertestapp.R
import appnexus.com.trackertestapp.listener.RecyclerItemClickListener
import kotlinx.android.synthetic.main.layout_recycler.view.*

class AdRecyclerAdapter(
    val items: ArrayList<String>,
    val context: Context,
    val listener: RecyclerItemClickListener
) : RecyclerView.Adapter<AdRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(
            R.layout.layout_recycler, parent, false
        ))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvAd.text = items.get(position)
        holder.tvAd.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val tvAd = view.tvAd
    }

}
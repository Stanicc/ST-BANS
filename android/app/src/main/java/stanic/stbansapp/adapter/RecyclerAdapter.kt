package stanic.stbansapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import stanic.stbansapp.R
import stanic.stbansapp.app.model.Punishment

class RecyclerAdapter(private var context: Context, private var punishments: List<Punishment>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private lateinit var buttonClickListener: ButtonClickListener

    interface ButtonClickListener {
        fun onClick(view: View, punishment: Punishment, position: Int, holder: ViewHolder)
    }

    fun setClickListener(listener: ButtonClickListener) {
        this.buttonClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_punishment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return punishments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val punishment = punishments[position]

        holder.nick.text = punishment.nick
        holder.reason.text = punishment.reason
        holder.date.text = punishment.date

        holder.viewButton.setOnClickListener {
            buttonClickListener.onClick(it, punishment, position, holder)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var nick = view.findViewById<TextView>(R.id.nickRecycler)
        internal var reason = view.findViewById<TextView>(R.id.reasonRecycler)
        internal var date = view.findViewById<TextView>(R.id.dateRecycler)
        internal var viewButton = view.findViewById<View>(R.id.recyclerButton)
    }

}
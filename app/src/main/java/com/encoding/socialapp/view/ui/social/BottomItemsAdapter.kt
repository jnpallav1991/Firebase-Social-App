package com.encoding.socialapp.view.ui.social

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.encoding.socialapp.R
import kotlinx.android.synthetic.main.fragment_item_list_dialog_list_dialog_item.view.*


class BottomItemsAdapter(
    private val arrayList: ArrayList<String>,
    private val sendSelectedType: SendSelectedType
) :
    RecyclerView.Adapter<BottomItemsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(
            R.layout.fragment_item_list_dialog_list_dialog_item, viewGroup,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList[position], position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(result: String, position: Int) {
            itemView.text.text = result
            itemView.text.setOnClickListener {
                sendSelectedType.send(result, position)
            }

        }
    }

    interface SendSelectedType {
        fun send(result: String, position: Int)
    }
}
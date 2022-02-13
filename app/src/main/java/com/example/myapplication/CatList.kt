package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DetailActivity.Companion.CAT_FACT_TEXT_TAG
import com.example.myapplication.DetailActivity.Companion.TITLE_TAG

class CatAdapter(private val cats: List<Cat>, private val title: String) : RecyclerView.Adapter<CatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val rootView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.cat_item, parent, false)
        return CatViewHolder(rootView, title)
    }

    override fun getItemCount(): Int {
        return cats.size
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        holder.bind(cats[position])
    }
}


class CatViewHolder(itemView: View, private val title: String) : RecyclerView.ViewHolder(itemView){
    private val textView: TextView = itemView.findViewById(R.id.textViewId)

    fun bind(cat: Cat) {
        textView.text = cat.text
        itemView.setOnClickListener{
            openDetailActivity(itemView.context, cat)
        }
    }

    private fun openDetailActivity(context: Context, cat: Cat){
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(CAT_FACT_TEXT_TAG, cat.text)
        intent.putExtra(TITLE_TAG, title)
        (context as MainActivity).getResult.launch(intent)
    }
}



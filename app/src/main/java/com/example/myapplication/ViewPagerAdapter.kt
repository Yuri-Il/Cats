package com.example.myapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import io.realm.Realm
import org.json.JSONArray

class ViewPagerAdapter: RecyclerView.Adapter<PagerVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH
    = PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.fragment_facts, parent, false))

    override fun getItemCount(): Int = 2

    override fun onBindViewHolder(holder: PagerVH, position: Int) {
        holder.bind(position)
    }
}

class PagerVH(itemView: View): RecyclerView.ViewHolder(itemView){
    private val urlFacts = "https://cat-fact.herokuapp.com/facts"
    private val queue = Volley.newRequestQueue(itemView.context)
    private val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerId)
    private val listTitles  = listOf("Кошки", "Избранное")
    val colors = listOf("#7E1DE9B6", "#4FF16897")

    fun bind(position: Int) {
        itemView.setBackgroundColor(Color.parseColor(colors[position]))
        when (position){
            0 -> getCatsFromServer()
            else -> {
                val cats = loadFromDB()
                setList(recyclerView, cats, position)
            }
        }
    }

    private fun setList(view: RecyclerView, cats: List<Cat>, position: Int){
        val adapter = CatAdapter(cats, listTitles[position])
        view.adapter = adapter
        val layoutManager = LinearLayoutManager(itemView.context)
        view.layoutManager = layoutManager
    }

    private fun getCatsFromServer() {
        val stringRequestFacts = StringRequest(
            Request.Method.GET,
            urlFacts,
            { response ->
                val cats = parseResponse(response)
                setList(recyclerView, cats, 0)
            },
            {
                Toast.makeText(itemView.context,"Ошибка запроса фактов", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(stringRequestFacts)
    }
}


private fun loadFromDB(): List<Cat> {
    val realm = Realm.getDefaultInstance()
    return realm.where(Cat::class.java).findAll()
}

private fun parseResponse(responseText: String): List<Cat> {
    val catList: MutableList<Cat> = mutableListOf()
    val jsonArray = JSONArray(responseText)
    for (index in 0 until jsonArray.length()){
        val jsonObject = jsonArray.getJSONObject(index)
        val catText = jsonObject.getString("text")
        val cat = Cat()
        cat.text = catText
        catList.add(cat)
    }
    return catList
}



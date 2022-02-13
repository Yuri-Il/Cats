package com.example.myapplication


import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityDetailBinding
import io.realm.Realm
import org.json.JSONObject


class DetailActivity : AppCompatActivity() {
    private val urlImages = "https://aws.random.cat/meow"
    private lateinit var binding: ActivityDetailBinding

    companion object {
        const val CAT_FACT_TEXT_TAG = "com.example.myapplication.cat_fact_text_tag"
        const val TITLE_TAG = "com.example.myapplication.title_tag"
    }

     override fun onSupportNavigateUp(): Boolean {
         finish()
         return true
     }

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        setTextAndButton()
        val queue = Volley.newRequestQueue(this)
        getImageFromServer(queue)
    }

    private fun getImageFromServer(queue: RequestQueue) {
        val stringRequestImages = StringRequest(
                Request.Method.GET,
                urlImages,
                { response ->
                    val catImage = parseResponse(response)
                    val circularProgressDrawable = CircularProgressDrawable(this)
                    circularProgressDrawable.centerRadius = 70f
                    circularProgressDrawable.start()
                    Glide.with(this).load(catImage).placeholder(circularProgressDrawable).into(binding.imageViewId)
                },
                {
                    Toast.makeText(this,"Ошибка запроса картинки", Toast.LENGTH_SHORT).show()
                }
            )
        queue.add(stringRequestImages)
    }

    private fun parseResponse(responseText: String): String {
        val jsonObject = JSONObject(responseText)
        val catImage = jsonObject.getString("file")
        return catImage
    }


    private fun setupActionBar(){
        val fragmentTitle = intent?.extras?.getString(TITLE_TAG)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = fragmentTitle
        }
    }


    private fun setTextAndButton() {
        val text = intent?.extras?.getString(CAT_FACT_TEXT_TAG)
        binding.textViewId.text = text
        var fav = false
        val realm = Realm.getDefaultInstance()
        val cats: List<Cat> = realm.where(Cat::class.java).findAll()
        for (index in cats.indices){
            if (cats[index].text == text){
                binding.buttonId.text = "Удалить из избранного"
                 fav = true
            }
        }
        if (!fav) binding.buttonId.text = "Добавить в избранное"
    }

    fun buttonClick(view: View){
        if (binding.buttonId.text == "Добавить в избранное"){
            addToFavourites()
        }
        else {
            deleteFromFavourites()
        }
        setResult(Activity.RESULT_OK)
    }

    private fun  addToFavourites(){
        val text = intent?.extras?.getString(CAT_FACT_TEXT_TAG)
        val cat = Cat()
        if (text != null) {
            cat.text = text
        }
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.copyToRealm(cat)
        realm.commitTransaction()
        binding.buttonId.text = "Удалить из избранного"
    }

    private fun  deleteFromFavourites(){
        val text = intent?.extras?.getString(CAT_FACT_TEXT_TAG)
        val realm = Realm.getDefaultInstance()
        val cats: List<Cat> = realm.where(Cat::class.java).findAll()
        for (index in cats.indices){
            if (cats[index].text == text){
                realm.beginTransaction()
                cats[index].deleteFromRealm()
                realm.commitTransaction()
                break
            }
        }
        binding.buttonId.text = "Добавить в избранное"
    }
}

package com.example.myapplication

import io.realm.RealmObject

open class Cat: RealmObject() {
    lateinit var text: String
}
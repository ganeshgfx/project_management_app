package com.ganeshgfx.projectmanagement.Utils

fun randomString(length:Int):String{
    var str = ""
    val set = mutableSetOf<Char>()
    set.addAll('0'..'9')
    set.addAll('a'..'z')
    set.addAll('A'..'Z')
    for (i in 0..length){
        str+=set.random()
    }
    return str
}
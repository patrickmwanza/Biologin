package com.example.biologin

data class logdata(val date:String?=null, var timein:String?=null, var timeout:String?=null){
    constructor() : this("", "","")
}

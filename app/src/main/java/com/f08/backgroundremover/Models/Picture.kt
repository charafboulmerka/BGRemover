package com.f08.backgroundremover.Models

class Picture {
    var id:String?=null
    var dateMs:Long?=null
    var url:String?=null
    constructor(id:String,dateMs:Long,url:String){
        this.id=id
        this.dateMs=dateMs
        this.url=url
    }
    constructor()
}
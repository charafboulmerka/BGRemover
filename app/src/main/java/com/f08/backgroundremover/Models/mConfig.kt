package com.f08.backgroundremover.Models

class mConfig {
    var version:Int?=null
    var update_features:String?=null
    var force_update:Boolean?=null
    var ads_level:Int?=null
    var privacy_url:String?=null
    constructor(version:Int,update_features:String,force_update:Boolean,ads_level:Int,privacy_url:String){
        this.version=version
        this.update_features=update_features
        this.force_update=force_update
        this.ads_level=ads_level
        this.privacy_url=privacy_url
    }
    constructor()
}
package data

data class Talent(
    var link: String = "",
    var id: String = "",
    var color: String = "",

    var imNameJP: String = "",
    var imNameEN: String = "",
    var imageFull: String = "",

    var imageThumb: String = "",
    var nameEN: String = "",
    var nameJP: String = "",

    var tags: MutableList<String> = mutableListOf(),

    )

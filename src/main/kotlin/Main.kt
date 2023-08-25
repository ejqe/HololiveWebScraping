import com.google.gson.Gson
import data.Talent

import java.io.File

fun main() {
    val zipFinal = combineWithTags()
    //for checking
    zipFinal.forEach{ println(it)}

    //FOR EXPORT JSON
    val gson = Gson()
    val json: String = gson.toJson(zipFinal)
    println("Enter file location you want to save the JSON. follow the format C:\\Users\\YourUserName\\Desktop :")
    val fileLocation = readln()
    val fileName = "\\HololiveWebScraping.json"
    val file = File(fileLocation + fileName)

    file.writeText(json)
    println("Done! Check your file here:")
    println(file)
}



fun combineWithTags(): List<Talent> {
    val combList = combineMainToTalent().sortedBy { it.nameEN }
    val tagList = parseTags().sortedBy { it.nameEN }

    val zipFinal = combList.zip(tagList) { comb, tag ->
        Talent(
            link = comb.link,
            id = comb.id,
            color = comb.color,
            imNameJP = comb.imNameJP,
            imNameEN = comb.imNameEN,
            imageFull = comb.imageFull,
            imageThumb = comb.imageThumb,
            nameEN = comb.nameEN,
            nameJP = comb.nameJP,
            tags = tag.tags
        )
    }
    return zipFinal
}
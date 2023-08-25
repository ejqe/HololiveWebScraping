import URL.MAIN_PAGE
import URL.TALENT_PAGE
import data.ScrapingResult
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.attribute
import it.skrape.selects.html5.*
import data.Talent


fun combineMainToTalent(): List<Talent> {

    print("Talent Counts: ${parseMainPage().count} ")
    println(
        if (parseMainPage().count == parseTalentPage().count){
            "   :OK"
        } else "Error Occurred: Count did not match"
    )
    val listMain = parseMainPage().talents.sortedBy { it.link }
    val listTalent = parseTalentPage().talents.sortedBy { it.link }

    val zipList = listMain.zip(listTalent) { home, detail ->
        Talent(
            link = home.link,
            id = home.id,
            color = home.color,
            imNameJP = home.imNameJP,
            imNameEN = home.imNameEN,
            imageFull = home.imageFull,
            imageThumb = detail.imageThumb,
            nameEN = detail.nameEN,
            nameJP = detail.nameJP,
            tags = mutableListOf()
        )
    }
    return zipList

}


fun parseMainPage(): ScrapingResult {
    val websiteUrl = MAIN_PAGE
    val extracted = skrape(HttpFetcher) { // <-- could use any valid fetcher depending on your use-case
        request { url = websiteUrl }

        extractIt<ScrapingResult> { result ->
            htmlDocument {
                relaxed = true
                var link = ""
                var imNameJP = ""
                var imNameEN = ""
                var imageFull = ""
                var color = listOf<String>()
                var idList = listOf<String>()

                ul(".swiper-wrapper.talent.clearfix") {

                    li {
                        findAll {
                            val id = attribute("id")
                            idList = id.split(", ") //use this

                            val colorLong = attribute("style")
                            val colorRaw = colorLong.split(", ") //converts string to list
                            color = colorRaw.mapNotNull { extractColorValue(it) } //truncate //use this
                        }

                        findAll {

                            map {


                                it.img {
                                    findFirst {
                                        imNameJP = attribute("src")
                                    }
                                }
                                it.img {
                                    findSecond {
                                        imNameEN = attribute("src")
                                    }
                                }
                                it.img {
                                    findThird {
                                        imageFull = attribute("src")
                                    }
                                }
                                it.a {
                                    findFirst {
                                        link = attribute("href")
                                    }
                                }

                                result.talents.add(
                                    Talent(
                                        link = link,
                                        imNameJP = imNameJP,
                                        imNameEN = imNameEN,
                                        imageFull = imageFull
                                    )
                                )
                                result.count = result.talents.size


                            }
                        }

                    }
                }

                val zipList = result.talents.zip(idList).zip(color) { (talent, id), color ->
                    Talent(
                        link = talent.link,
                        id = id,
                        color = color,
                        imNameJP = talent.imNameJP,
                        imNameEN = talent.imNameEN,
                        imageFull = talent.imageFull,

                    )
                }

                result.talents = zipList.toMutableList()


            }
        }
    }

    return extracted
}

fun parseTalentPage(): ScrapingResult {
    val websiteUrl = TALENT_PAGE
    val extracted = skrape(HttpFetcher) {
        request { url = websiteUrl }

        extractIt<ScrapingResult> { result ->
            htmlDocument {
                relaxed = true

                var link = ""
                var imageThumb = ""
                var nameEN = ""
                var nameJP = ""

                ul(".talent_list.clearfix") {

                    li {
                        findAll {
                            map {

                                it.a {
                                    findFirst {
                                        link = attribute("href")
                                    }
                                }
                                it.img {
                                    findFirst {
                                        imageThumb = attribute("src")
                                    }
                                }

                                it.span {
                                    findFirst{
                                        nameJP = text
                                    }
                                }


                                it.h3 {
                                    findFirst {
                                        val text = text
                                        nameEN = text.removeSuffix(nameJP)
                                    }
                                }

                                result.talents.add(
                                    Talent(
                                        link = link,
                                        imageThumb = imageThumb,
                                        nameEN = nameEN,
                                        nameJP = nameJP,
                                    )
                                )

                                result.count = result.talents.size

                            }
                        }
                    }
                }
            }
        }
    }
    return extracted
}


fun extractColorValue(input: String): String? {
    val regex = Regex("#[a-fA-F0-9]{6}")
    val matchResult = regex.find(input)
    return matchResult?.value
}






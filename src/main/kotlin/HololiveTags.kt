import data.ScrapingResult
import data.Talent
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.*

fun parseTags(): List<Talent> {
    val resultList = mutableListOf<Talent>()
    val linkList = parseGroupLinks().links

    for (i in 0 until linkList.size) {

        val link = linkList[i].link
        val tag = linkList[i].tag

        skrape(HttpFetcher) {
            request { url = link }

            extractIt<ScrapingResult> { result ->
                htmlDocument {
                    relaxed = true

                    var nameEN = ""
                    var nameJP = ""

                    ul(".talent_list.clearfix") {

                        li {
                            findAll {
                                map {

                                    it.span {
                                        findFirst {
                                            nameJP = text
                                        }
                                    }

                                    it.h3 {
                                        findFirst() {
                                            val text = text
                                            nameEN = text.removeSuffix(nameJP)
                                        }
                                    }

                                    //for this gen
                                    result.talents.add(Talent(nameEN = nameEN, tags = mutableListOf(tag)))
                                    result.count = result.talents.size

                                }
                            }
                        }
                    }
                }
                resultList.addAll(result.talents.toMutableList())
            }

        }

    }

    val mergedList = resultList.groupBy { it.nameEN }
        .mapValues { (_ ,talents) ->
            val mergedTags = talents.flatMap { it.tags }.distinct()
            Talent(
                nameEN = talents.first().nameEN,
                tags = mergedTags.toMutableList()
            )
        }.values.toList()


    return mergedList
}


import URL.TALENT_PAGE
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.a
import it.skrape.selects.html5.li
import it.skrape.selects.html5.section

data class LinkGroup(var link: String, var tag: String)
data class Links(var links: MutableList<LinkGroup> = mutableListOf(), var count: Int = 0)

fun parseGroupLinks(): Links {
    val websiteUrl = TALENT_PAGE
    val groupLinks = skrape(HttpFetcher) {
        request { url = websiteUrl }

        extractIt<Links> { result ->
            htmlDocument {


                var link = ""
                var tag = ""

                section(".talent") {

                    li {
                        findAll {
                            map {

                                it.a {
                                    findFirst {
                                        link = attribute("href")
//                                        println(link)
                                    }
                                }

                                it.a {
                                    findFirst {
                                        tag = text
//                                        println(tag)
                                    }
                                }

                                result.links.add(
                                    LinkGroup(link = link, tag = tag)
                                )
                                result.count = result.links.size


                            }
                        }
                    }
                }
            }
        }
    }
    println("Category Counts (incl. ALL): ${ groupLinks.count }")
    return groupLinks
}

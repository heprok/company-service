package com.briolink.companyservice.api.dataloader

import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.briolink.companyservice.common.jpa.write.entity.KeywordWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.IndustryWriteRepository
import com.briolink.companyservice.common.jpa.write.repository.KeywordWriteRepository
import com.briolink.companyservice.common.jpa.write.repository.OccupationWriteRepository
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.net.URL
import java.util.*
import kotlin.random.Random

@Component
@Order(2)
class CompanyDataLoader(
    private var companyReadRepository: CompanyReadRepository,
    private var industryWriteRepository: IndustryWriteRepository,
    private var occupationWriteRepository: OccupationWriteRepository,
    private var keywordWriteRepository: KeywordWriteRepository,
    var service: CompanyService
) : DataLoader() {
    override fun loadData() {
        if (companyReadRepository.count().toInt() == 0 &&
            industryWriteRepository.count().toInt() != 0 &&
            occupationWriteRepository.count().toInt() != 0 &&
            keywordWriteRepository.count().toInt() != 0
        ) {
            val industryList = industryWriteRepository.findAll()
            val occupationList = occupationWriteRepository.findAll()
            val keywordList = keywordWriteRepository.findAll()
            val companyWriteEntityList: MutableList<CompanyWriteEntity> = mutableListOf()

            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Pay Pal",
                            website = "https://www.paypal.com",
                            logo = URL("https://www.paypalobjects.com/webstatic/icon/pp258.png"),
                            location = "USA, California, San-Mateo",
                            facebook = "paypal",
                            twitter = "paypal",
                            isTypePublic = false,
                            description = "PayPal is the safer, easier way to pay and get paid online. The service allows anyone to pay in any way they prefer, including through credit cards, bank accounts, PayPal Smart Connect or account balances, without sharing financial information. PayPal has quickly become a global leader in online payment solutions with more than  million accounts worldwide. Available in 202 countries and 25 currencies around the world, PayPal enables global ecommerce by making payments possible across different locations, currencies, and languages.  PayPal has received more than 20 awards for excellence from the internet industry and the business community - most recently the 2006 Webby Award for Best Financial Services Site and the 2006 Webby People's Voice Award for Best Financial Services Site.  Located in San Jose, California, PayPal was founded in 1998. PayPal (Europe) S.à r.l. et Cie, S.C.A. is a credit institution (or bank) authorised and supervised by Luxembourg’s financial regulator, the Commission de Surveillance du Secteur Financier (or CSSF). CSSF’s registered office: 283, route d’Arlon, L-1150 Luxembourg.",
                    ).apply {
                        id = UUID.fromString("728e5333-6f00-4de1-bf0f-1ace3374c47b")
                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Amazon",
                            website = "https://www.amazon.com/",
                            logo = URL("https://regnum.ru/uploads/pictures/news/2018/11/30/regnum_picture_154356037138044_normal.png"),
                            location = "USA, Texas, Houston",
                            facebook = "amazon",
                            twitter = "amazon",
                            isTypePublic = true,
                            description = "When Amazon.com launched in 1995, it was with the mission “to be Earth’s most customer-centric company.” What does this mean? It's simple. We're a company that obsesses over customers. Our actions, goals, projects, programmes and inventions begin and end with the customer at the forefront of our minds. In other words, we start with the customer and work backwards. When we hit on something that is really working for customers, we commit to it in the hope that it will turn into an even bigger success. However, it’s not always as straightforward as that. Inventing is messy, and over time, it’s certain that we’ll fail at some big bets too.",
                    ).apply {
                        id = UUID.fromString("728e5333-6f00-4de1-bf0f-1ace3374c47b")
                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Notion Labs INC",
                            website = "https://www.notion.so/",
                            logo = URL("https://upload.wikimedia.org/wikipedia/commons/4/45/Notion_app_logo.png"),
                            location = "USA, California, Los Angeles",
                            facebook = "notion-labs-inc",
                            twitter = "notion-labs-inc",
                            isTypePublic = false,
                            description = "You probably have fifteen tabs open: one for email, one for Slack, one for Google Docs, and on, and on…But have you ever thought about where these work tools came from? Or why there are so many of them?To answer these questions, and to explain why we created Notion, we have to travel back in time.",
                    ).apply {
                        id = UUID.fromString("974ae794-a929-434d-a939-b0fb49d8bf64")
                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Google",
                            website = "https://www.google.com/",
                            logo = URL("https://w7.pngwing.com/pngs/760/624/png-transparent-google-logo-google-search-advertising-google-company-text-trademark.png"),
                            location = "USA, Texas, Dallas",
                            facebook = "google",
                            twitter = "google",
                            isTypePublic = true,
                            description = "American multinational technology company that specializes in Internet-related services and products, which include online advertising technologies, a search engine, cloud computing, software, and hardware. It is considered one of the Big Five companies in the American information technology industry, along with Amazon, Facebook, Apple, and Microsoft. Google was founded on September 4, 1998, by Larry Page and Sergey Brin while they were Ph.D. students at Stanford University in California. Together they own about 14% of its publicly-listed shares and control 56% of the stockholder voting power through super-voting stock. The company went public via an initial public offering (IPO) in 2004. In 2015, Google was reorganized as a wholly-owned subsidiary of Alphabet Inc.. Google is Alphabet's largest subsidiary and is a holding company for Alphabet's Internet properties and interests. Sundar Pichai was appointed CEO of Google on October 24, 2015, replacing Larry Page, who became the CEO of Alphabet. On December 3, 2019, Pichai also became the CEO of Alphabet.",
                    ).apply {
                        id = UUID.fromString("95d3b063-8ab5-4abb-9887-4006db7a20a8")
                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Twitter",
                            website = "https://twitter.com/",
                            logo = URL("https://upload.wikimedia.org/wikipedia/ru/thumb/9/9f/Twitter_bird_logo_2012.svg/1261px-Twitter_bird_logo_2012.svg.png"),
                            location = "USA, Utah, Moscow",
                            facebook = "twitter",
                            twitter = "twitter",
                            isTypePublic = false,
                            description = "Twitter is an American microblogging and social networking service on which users post and interact with messages known as tweets. Registered users can post, like, and retweet tweets, but unregistered users can only read those that are publicly available. Users interact with Twitter through browser or mobile frontend software, or programmatically via its APIs. Prior to April 2020 services were accessible via SMS. The service is provided by Twitter, Inc., a corporation based in San Francisco, California, and has more than 25 offices around the world.[14] Tweets were originally restricted to 140 characters, but the limit was doubled to 280 for non-CJK languages in November 2017. Audio and video tweets remain limited to 140 seconds for most accounts.",
                    ).apply {
                        id = UUID.fromString("1e325d92-38b2-420a-a79e-4504110e0959")
                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Git Hub",
                            website = "https://github.com/",
                            logo = URL("https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png"),
                            location = "USA, California, San Francisco",
                            facebook = "github",
                            twitter = "github",
                            isTypePublic = true,
                            description = "GitHub, Inc. is a provider of Internet hosting for software development and version control using Git. It offers the distributed version control and source code management (SCM) functionality of Git, plus its own features. It provides access control and several collaboration features such as bug tracking, feature requests, task management, continuous integration and wikis for every project. Headquartered in California, it has been a subsidiary of Microsoft since 2018.",
                    ).apply {
                        id = UUID.fromString("765ca9e5-1bc0-4c94-abad-63e75ab3aa7c")
                    },
            )

            companyWriteEntityList.forEach {
                val keywordsList = mutableListOf<KeywordWriteEntity>()
                keywordsList.addAll(keywordList.shuffled().take(Random.nextInt(0, 10)))
                it.apply {
                    industry = industryList.random()
                    occupation = occupationList.random()
                    keywords = keywordsList
                }
                service.createCompany(it)

            }

        }
    }

}

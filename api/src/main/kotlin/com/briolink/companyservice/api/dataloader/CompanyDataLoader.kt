package com.briolink.companyservice.api.dataloader

import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.common.dataloader.DataLoader
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
                            name = "PayPal",
                            logo = URL("https://www.paypalobjects.com/webstatic/icon/pp258.png"),
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            facebook = "paypal",
                            twitter = "paypal",
                            isTypePublic = false,
                            createdBy = UUID.randomUUID(),
                            description = "PayPal is the safer, easier way to pay and get paid online. The service allows anyone to pay in any way they prefer, including through credit cards, bank accounts, PayPal Smart Connect or account balances, without sharing financial information. PayPal has quickly become a global leader in online payment solutions with more than  million accounts worldwide. Available in 202 countries and 25 currencies around the world, PayPal enables global ecommerce by making payments possible across different locations, currencies, and languages.  PayPal has received more than 20 awards for excellence from the internet industry and the business community - most recently the 2006 Webby Award for Best Financial Services Site and the 2006 Webby People's Voice Award for Best Financial Services Site.  Located in San Jose, California, PayPal was founded in 1998. PayPal (Europe) S.à r.l. et Cie, S.C.A. is a credit institution (or bank) authorised and supervised by Luxembourg’s financial regulator, the Commission de Surveillance du Secteur Financier (or CSSF). CSSF’s registered office: 283, route d’Arlon, L-1150 Luxembourg.",
                    ).apply {
                        websiteUrl = URL("https://www.paypal.com")

                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Nikonv&Sheshuk",
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            isTypePublic = false,
                            createdBy = UUID.randomUUID(),
                    )
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Nokia",
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            isTypePublic = false,
                            createdBy = UUID.randomUUID(),
                    )
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Amazon",
                            logo = URL("https://regnum.ru/uploads/pictures/news/2018/11/30/regnum_picture_154356037138044_normal.png"),
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            facebook = "amazon",
                            twitter = "amazon",
                            isTypePublic = true,
                            createdBy = UUID.randomUUID(),
                            description = "When Amazon.com launched in 1995, it was with the mission “to be Earth’s most customer-centric company.” What does this mean? It's simple. We're a company that obsesses over customers. Our actions, goals, projects, programmes and inventions begin and end with the customer at the forefront of our minds. In other words, we start with the customer and work backwards. When we hit on something that is really working for customers, we commit to it in the hope that it will turn into an even bigger success. However, it’s not always as straightforward as that. Inventing is messy, and over time, it’s certain that we’ll fail at some big bets too.",
                    ).apply {
                        websiteUrl = URL("https://www.amazon.com/")

                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Notion Labs INC",
                            logo = URL("https://upload.wikimedia.org/wikipedia/commons/4/45/Notion_app_logo.png"),
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            facebook = "notion-labs-inc",
                            twitter = "notion-labs-inc",
                            isTypePublic = false,
                            createdBy = UUID.randomUUID(),
                            description = "You probably have fifteen tabs open: one for email, one for Slack, one for Google Docs, and on, and on…But have you ever thought about where these work tools came from? Or why there are so many of them?To answer these questions, and to explain why we created Notion, we have to travel back in time.",
                    ).apply {
                        websiteUrl = URL("https://www.notion.so/")

                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Google",
                            logo = URL("https://w7.pngwing.com/pngs/760/624/png-transparent-google-logo-google-search-advertising-google-company-text-trademark.png"),
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            facebook = "google",
                            twitter = "google",
                            isTypePublic = true,
                            createdBy = UUID.randomUUID(),
                            description = "American multinational technology company that specializes in Internet-related services and products, which include online advertising technologies, a search engine, cloud computing, software, and hardware. It is considered one of the Big Five companies in the American information technology industry, along with Amazon, Facebook, Apple, and Microsoft. Google was founded on September 4, 1998, by Larry Page and Sergey Brin while they were Ph.D. students at Stanford University in California. Together they own about 14% of its publicly-listed shares and control 56% of the stockholder voting power through super-voting stock. The company went public via an initial public offering (IPO) in 2004. In 2015, Google was reorganized as a wholly-owned subsidiary of Alphabet Inc.. Google is Alphabet's largest subsidiary and is a holding company for Alphabet's Internet properties and interests. Sundar Pichai was appointed CEO of Google on October 24, 2015, replacing Larry Page, who became the CEO of Alphabet. On December 3, 2019, Pichai also became the CEO of Alphabet.",
                    ).apply {
                        websiteUrl = URL("https://www.google.com/")

                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Twitter",
                            logo = URL("https://upload.wikimedia.org/wikipedia/ru/thumb/9/9f/Twitter_bird_logo_2012.svg/1261px-Twitter_bird_logo_2012.svg.png"),
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            facebook = "twitter",
                            twitter = "twitter",
                            isTypePublic = false,
                            createdBy = UUID.randomUUID(),
                            description = "Twitter is an American microblogging and social networking service on which users post and interact with messages known as tweets. Registered users can post, like, and retweet tweets, but unregistered users can only read those that are publicly available. Users interact with Twitter through browser or mobile frontend software, or programmatically via its APIs. Prior to April 2020 services were accessible via SMS. The service is provided by Twitter, Inc., a corporation based in San Francisco, California, and has more than 25 offices around the world.[14] Tweets were originally restricted to 140 characters, but the limit was doubled to 280 for non-CJK languages in November 2017. Audio and video tweets remain limited to 140 seconds for most accounts.",
                    ).apply {
                        websiteUrl = URL("https://twitter.com/")

                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "GitHub",
                            logo = URL("https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png"),
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            facebook = "github",
                            twitter = "github",
                            isTypePublic = true,
                            createdBy = UUID.randomUUID(),
                            description = "GitHub, Inc. is a provider of Internet hosting for software development and version control using Git. It offers the distributed version control and source code management (SCM) functionality of Git, plus its own features. It provides access control and several collaboration features such as bug tracking, feature requests, task management, continuous integration and wikis for every project. Headquartered in California, it has been a subsidiary of Microsoft since 2018.",
                    ).apply {
                        websiteUrl = URL("https://github.com/")

                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Walmart",
                            logo = URL("https://logo.clearbit.com/www.walmart.com/"),
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            facebook = "github",
                            twitter = "github",
                            isTypePublic = true,
                            createdBy = UUID.randomUUID(),
                            description = "Walmart is a retailing company that operates a chain of hypermarkets, discount department stores, and grocery stores.",
                    ).apply {
                        websiteUrl = URL("https://www.walmart.com/")

                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Oaktree Capital",
                            logo = URL("https://logo.clearbit.com/www.oaktreecapital.com/"),
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            facebook = "oaktreecapital",
                            twitter = "Oaktree",
                            isTypePublic = true,
                            createdBy = UUID.randomUUID(),
                            description = "Oaktree is a leader among global investment managers specializing in alternative investments.",
                    ).apply {
                        websiteUrl = URL("https://www.oaktreecapital.com/")

                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "KFC",
                            logo = URL("https://logo.clearbit.com/www.kfc.com/"),
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            facebook = "KFC",
                            twitter = "kfc",
                            isTypePublic = true,
                            createdBy = UUID.randomUUID(),
                            description = "KFC (also known as Kentucky Fried Chicken) is a global chicken restaurant brand.",
                    ).apply {
                        websiteUrl = URL("https://www.kfc.com/")

                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Tata",
                            logo = URL("https://logo.clearbit.com/www.tata.com/"),
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            facebook = "TataMotorsGroup",
                            twitter = "TataMotors",
                            isTypePublic = true,
                            createdBy = UUID.randomUUID(),
                            description = "Tata Group is an international conglomerate that owns and operates independent companies, with the main focus on steel, hydro-power, hospitality, and airline industries. ",
                    ).apply {
                        websiteUrl = URL("http://www.tata.com")

                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Compass Group",
                            logo = URL("https://logo.clearbit.com/www.compass-group.com/"),
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            facebook = "compassgroupusa",
                            twitter = "compassgroupusa",
                            isTypePublic = true,
                            createdBy = UUID.randomUUID(),
                            description = "Compass Group is a global provider of food and support services.",
                    ).apply {
                        websiteUrl = URL("https://www.compass-group.com/")

                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Volkswagen",
                            logo = URL("https://logo.clearbit.com/www.volkswagenag.com/"),
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            facebook = "Volkswagenrussia",
                            twitter = "Volkswagen",
                            isTypePublic = true,
                            createdBy = UUID.randomUUID(),
                            description = "Volkswagen Group (also known as VAG and Volkswagen AG) is an automobile manufacturer. ",
                    ).apply {
                        websiteUrl = URL("https://www.volkswagenag.com/")

                    },
            )
            companyWriteEntityList.add(
                    CompanyWriteEntity(
                            name = "Deutsche Post DHL Group",
                            logo = URL("https://logo.clearbit.com/www.dpdhl.com/"),
                            countryId = Random.nextInt(1, 130),
                            stateId = Random.nextInt(1, 3000),
                            cityId = Random.nextInt(1, 12000),
                            facebook = "dhl",
                            twitter = "DeutschePostDHL",
                            isTypePublic = true,
                            createdBy = UUID.randomUUID(),
                            description = "Deutsche Post DHL Group (also known as Deutsche Post) is a mail and logistics company.",
                    ).apply {
                        websiteUrl = URL("https://www.dpdhl.com/")

                    },
            )

            companyWriteEntityList.forEach {
                val keywordsList = mutableListOf<KeywordWriteEntity>()
                keywordsList.addAll(keywordList.shuffled().take(Random.nextInt(0, 10)))
                it.apply {
                    industry = if(Random.nextBoolean()) industryList.random() else null
                    occupation = if(Random.nextBoolean()) occupationList.random() else null
                    keywords = if(Random.nextBoolean()) keywordsList else mutableListOf()
                }
                service.createCompany(it)
            }
        }
    }
}

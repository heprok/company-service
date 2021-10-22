//package com.briolink.companyservice.updater.dataloader
//
//import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
//import com.briolink.companyservice.common.util.StringUtil
//import com.briolink.companyservice.updater.dto.User
//import com.briolink.companyservice.updater.event.UserCreatedEvent
//import org.springframework.context.ApplicationEventPublisher
//import org.springframework.core.annotation.Order
//import org.springframework.stereotype.Component
//import java.net.URL
//import java.util.*
//
//@Component
//@Order(1)
//class UserDataLoader(
//    var readRepository: UserReadRepository,
//    private val applicationEventPublisher: ApplicationEventPublisher,
//
//    ) : DataLoader() {
//    val listFirstName: List<String> = listOf(
//            "Lynch", "Kennedy", "Williams", "Evans", "Jones", "Burton", "Miller", "Smith", "Nelson", "Lucas",
//    )
//
//    val listLastName: List<String> = listOf(
//            "Scott", "Cynthia", "Thomas", "Thomas", "Lucy", "Dawn", "Jeffrey", "Ann", "Joan", "Lauren",
//    )
//
//    override fun loadData() {
//        if (readRepository.count().toInt() == 0) {
//            for (i in 1..COUNT_USER) {
//                applicationEventPublisher.publishEvent(
//                        UserCreatedEvent(
//                                User(
//                                        id = UUID.randomUUID(),
//                                        firstName = listFirstName.random(),
//                                        lastName = listLastName.random(),
//                                        image = URL("https://placeimg.com/148/148/people"),
//                                        slug = ""
//                                ).apply{
//                                    slug = StringUtil.slugify(
//                                            listFirstName.random() + " " + listLastName.random() + " " + UUID.randomUUID().toString(),
//                                    )
//                                },
//                        ),
//                )
//            }
//        }
//    }
//
//    companion object {
//        const val COUNT_USER = 100
//    }
//}

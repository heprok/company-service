package com.briolink.companyservice.api.rest

import com.briolink.companyservice.api.service.employee.EmployeeService
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.service.PermissionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ApiRest(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
    private val employeeService: EmployeeService,
    private val permissionService: PermissionService
) {
    // @GetMapping("/statistic/refresh")
    // fun refreshStatistic(): ResponseEntity<Int> {
    //
    //     eventPublisher.publishAsync(
    //         StatisticRefreshEvent(Statistic(null)),
    //     )
    //     return ResponseEntity.ok(1)
    // }

//     @GetMapping("/generator/data")
//     fun loadData(): ResponseEntity<Int> {
//         industryDataLoader.loadData()
//         occupationDataLoader.loadData()
//         keywordDataLoader.loadData()
// //        companyDataLoader.loadData()
//         return ResponseEntity.ok(1)
//     }

    // @GetMapping("/load-logo/{pathCsv}")
    // fun loadLogoCompany(@PathVariable pathCsv: String): ResponseEntity<Int> {
    //     companyService.importLogoFromCSV(pathCsv)
    //     return ResponseEntity.ok(1)
    // }

    @GetMapping("/permission")
    fun permission(): ResponseEntity<Int> {
        userJobPositionReadRepository.findAll().forEach {
            it.data.userPermission = permissionService.getUserPermissionRights(
                it.userId,
                it.companyId,
                AccessObjectTypeEnum.Company
            )
            userJobPositionReadRepository.save(it)
            employeeService.refreshEmployees(it.companyId)
        }
        return ResponseEntity(HttpStatus.OK)
    }
}

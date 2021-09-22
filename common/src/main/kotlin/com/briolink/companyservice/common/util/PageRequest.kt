package com.briolink.companyservice.common.util

import org.springframework.data.domain.AbstractPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

open class PageRequest(
    private val offset: Int = 0,
    private val limit: Int = 10,
    private val sort: Sort = Sort.unsorted()
) : AbstractPageRequest(offset / limit + 1, limit) {
    override fun next(): Pageable = PageRequest(offset + limit, limit)
    override fun getOffset(): Long = offset.toLong()
    override fun getSort(): Sort = sort
    override fun first(): Pageable = PageRequest(0, limit)
    override fun withPage(pageNumber: Int): Pageable = PageRequest(pageNumber, limit)
    override fun previous(): PageRequest {
        return if (offset == 0) this else {
            var newOffset = this.offset - limit
            if (newOffset < 0) newOffset = 0
            PageRequest(newOffset, limit)
        }
    }
}

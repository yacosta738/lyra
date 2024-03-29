package com.lyra.app.newsletter.domain

import com.lyra.common.domain.criteria.Criteria
import com.lyra.common.domain.presentation.pagination.Cursor
import com.lyra.common.domain.presentation.pagination.CursorPageResponse
import com.lyra.common.domain.presentation.pagination.OffsetPageResponse
import com.lyra.common.domain.presentation.sort.Sort
import kotlinx.coroutines.flow.Flow

/**
 *
 * @created 6/1/24
 */
interface SubscriberRepository {
    suspend fun create(subscriber: Subscriber)
    suspend fun searchAllByOffset(
        criteria: Criteria? = null,
        size: Int? = null,
        page: Int? = null,
        sort: Sort? = null
    ): OffsetPageResponse<Subscriber>
    suspend fun searchAllByCursor(
        criteria: Criteria? = null,
        size: Int? = null,
        sort: Sort? = null,
        cursor: Cursor? = null,
    ): CursorPageResponse<Subscriber>
    suspend fun searchActive(): Flow<Subscriber>
}

package org.hazelcast.cache

import com.hazelcast.map.IMap
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.future.await
import org.springframework.data.domain.Sort


class CachingService(private val cache: IMap<Long, Person>, private val repository: PersonRepository) {

    suspend fun findById(id: Long) = cache.getAsync(id).await()
        ?: repository.findById(id)?.also { cache.putAsync(it.id, it) }

    suspend fun findAll(sort: Sort) = repository
        .findAll(sort)
        .apply {
            collect {
                cache.putAsync(it.id, it)
            }
        }
}

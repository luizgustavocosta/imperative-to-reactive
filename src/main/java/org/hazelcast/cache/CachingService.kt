package org.hazelcast.cache

import com.hazelcast.map.IMap
import org.springframework.data.domain.Sort
import reactor.core.publisher.Mono


class CachingService(private val cache: IMap<Long, Person>, private val repository: PersonRepository) {

    fun findById(id: Long) = Mono.fromCompletionStage { cache.getAsync(id) }
        .switchIfEmpty(repository
            .findById(id)
            .doOnNext { cache.putAsync(it.id, it) }
        )

    fun findAll(sort: Sort) = repository
        .findAll(sort)
        .doOnNext { cache.putAsync(it.id, it) }
}

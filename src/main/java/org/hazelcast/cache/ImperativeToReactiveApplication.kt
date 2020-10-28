package org.hazelcast.cache

import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.connectionfactory.init.*
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import reactor.blockhound.BlockHound


@SpringBootApplication
@EnableR2dbcRepositories
class ImperativeToReactiveApplication {

    @Bean
    fun initialize(factory: ConnectionFactory) = ConnectionFactoryInitializer()
        .apply {
            setConnectionFactory(factory)
            val populator = CompositeDatabasePopulator()
                .apply {
                    addPopulators(
                        ResourceDatabasePopulator(ClassPathResource("/schema.sql")),
                        ResourceDatabasePopulator(ClassPathResource("/data.sql"))
                    )
                }
            setDatabasePopulator(populator)
        }
}

fun main() {
    BlockHound.install()
    SpringApplication.run(ImperativeToReactiveApplication::class.java)
}

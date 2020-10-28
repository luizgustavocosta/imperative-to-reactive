package org.hazelcast.cache

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.util.StreamUtils
import reactor.blockhound.BlockHound
import reactor.core.scheduler.ReactorBlockHoundIntegration


@SpringBootApplication
@EnableR2dbcRepositories
class ImperativeToReactiveApplication {

    @Bean
    fun initialize(client: DatabaseClient) = CommandLineRunner {
            val schemaIn = ClassPathResource("/schema.sql").inputStream
            val schema = StreamUtils.copyToString(schemaIn, Charsets.UTF_8)
            val dataIn = ClassPathResource("/data.sql").inputStream
            val data = StreamUtils.copyToString(dataIn, Charsets.UTF_8)
            client.execute(schema)
                .then()
                .and(client
                    .execute(data)
                    .then())
                .block()
        }
}

fun main() {
    BlockHound.install()
    SpringApplication.run(ImperativeToReactiveApplication::class.java)
}

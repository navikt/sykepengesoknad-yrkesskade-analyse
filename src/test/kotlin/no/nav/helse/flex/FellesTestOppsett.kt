package no.nav.helse.flex

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import okhttp3.mockwebserver.MockWebServer
import org.apache.kafka.clients.producer.KafkaProducer
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.util.*
import kotlin.concurrent.thread

private class PostgreSQLContainer14 : PostgreSQLContainer<PostgreSQLContainer14>("postgres:14-alpine")

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureObservability
@EnableMockOAuth2Server
@SpringBootTest(classes = [Application::class])
@AutoConfigureMockMvc(print = MockMvcPrint.NONE, printOnlyOnFailure = false)
abstract class FellesTestOppsett {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var kafkaProducer: KafkaProducer<String, String>

    companion object {
        private val yrkesskadeMockWebserver: MockWebServer

        init {
            val threads = mutableListOf<Thread>()

            thread {
                KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.2")).apply {
                    start()
                    System.setProperty("KAFKA_BROKERS", bootstrapServers)
                }
            }.also { threads.add(it) }

            thread {
                PostgreSQLContainer14().apply {
                    start()
                    System.setProperty("spring.datasource.url", "$jdbcUrl&reWriteBatchedInserts=true")
                    System.setProperty("spring.datasource.username", username)
                    System.setProperty("spring.datasource.password", password)
                }
            }.also { threads.add(it) }

            yrkesskadeMockWebserver = MockWebServer().apply {
                System.setProperty("YRKESSKADE_URL", "http://localhost:$port")
                dispatcher = YrkesskadeMockDispatcher
            }
            threads.forEach { it.join() }
        }
    }
}

package no.nav.helse.flex.kafka

import no.nav.helse.flex.logger
import no.nav.helse.flex.yrkesskadeanalyse.SjekkYrkesskade
import no.nav.helse.flex.yrkesskadeanalyse.tilEnkelSykepengesoknad
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.TopicPartition
import org.springframework.kafka.listener.ConsumerSeekAware
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZoneOffset

const val SYKEPENGESOKNAD_TOPIC = "flex." + "sykepengesoknad"

@Component
class AivenSykepengesoknadListener(
    private val sjekkYrkesskade: SjekkYrkesskade
) : ConsumerSeekAware {

    val log = logger()

    override fun onPartitionsAssigned(
        assignments: Map<org.apache.kafka.common.TopicPartition, Long>,
        callback: ConsumerSeekAware.ConsumerSeekCallback
    ) {
        val timestamp =
            LocalDate.of(2023, 2, 20).atStartOfDay().minusDays(1).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        log.info("Seeking to timestamp $timestamp")
        callback.seekToTimestamp(assignments.keys, timestamp)
    }

    @KafkaListener(
        topics = [SYKEPENGESOKNAD_TOPIC],
        concurrency = "3",
        containerFactory = "aivenKafkaListenerContainerFactory",
        id = "sykepengesoknad-sendt",
        idIsGroup = false
    )
    fun listen(cr: ConsumerRecord<String, String>, acknowledgment: Acknowledgment) {
        val sykepengesoknadDTO = cr.value().tilEnkelSykepengesoknad()

        sjekkYrkesskade.sjekkSoknad(sykepengesoknadDTO)
        acknowledgment.acknowledge()
    }
}

package no.nav.helse.flex.kafka

import no.nav.helse.flex.yrkesskadeanalyse.SjekkYrkesskade
import no.nav.helse.flex.yrkesskadeanalyse.tilEnkelSykepengesoknad
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

const val SYKEPENGESOKNAD_TOPIC = "flex." + "sykepengesoknad"

@Component
class AivenSykepengesoknadListener(
    private val sjekkYrkesskade: SjekkYrkesskade
) {

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

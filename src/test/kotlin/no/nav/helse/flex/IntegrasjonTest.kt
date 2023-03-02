package no.nav.helse.flex

import no.nav.helse.flex.kafka.SYKEPENGESOKNAD_TOPIC
import no.nav.helse.flex.yrkesskadeanalyse.EnkelSykepengesoknad
import no.nav.helse.flex.yrkesskadeanalyse.Soknadsstatus.SENDT
import no.nav.helse.flex.yrkesskadeanalyse.Soknadstype.ARBEIDSTAKERE
import no.nav.helse.flex.yrkesskadeanalyse.SykepengesoknadYrkesskadeStatusRepo
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.apache.kafka.clients.producer.ProducerRecord
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class IntegrasjonTest : FellesTestOppsett() {

    @Autowired
    lateinit var sykepengesoknadYrkesskadeStatusRepo: SykepengesoknadYrkesskadeStatusRepo

    @Test
    fun testerKallTilYrkesskade() {
        sykepengesoknadYrkesskadeStatusRepo.findAll().toList().shouldBeEmpty()

        val soknad = EnkelSykepengesoknad(
            id = "sdfsdf",
            sendtArbeidsgiver = LocalDateTime.now(),
            sendtNav = LocalDateTime.now(),
            fnr = "sdfsdf",
            sykmeldingId = "bla",
            status = SENDT,
            type = ARBEIDSTAKERE
        )

        kafkaProducer.send(
            ProducerRecord(
                SYKEPENGESOKNAD_TOPIC,
                null,
                soknad.id,
                soknad.serialisertTilString()
            )
        )

        await().untilAsserted {
            sykepengesoknadYrkesskadeStatusRepo.findAll().toList().size shouldBe 1
        }
        val first = sykepengesoknadYrkesskadeStatusRepo.findAll().toList().first()
        first.sykepengesoknadUuidHash shouldBeEqualTo "4bac27393bdd9777ce02453256c5577cd02275510b2227f473d03f533924f877"
        first.yrkesskadeStatus shouldBeEqualTo "MAA_SJEKKES_MANUELT"
    }
}

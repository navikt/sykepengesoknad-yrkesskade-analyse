package no.nav.helse.flex.yrkesskadeanalyse

import no.nav.helse.flex.client.yrkesskade.HarYsSakerRequest
import no.nav.helse.flex.client.yrkesskade.YrkesskadeClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class SjekkYrkesskade {

    @Autowired
    lateinit var yrkesskadeClient: YrkesskadeClient

    @Autowired
    lateinit var sykepengesoknadYrkesskadeStatusRepo: SykepengesoknadYrkesskadeStatusRepo

    fun sjekkSoknad(soknad: EnkelSykepengesoknad) {
        if (soknad.status != Soknadsstatus.SENDT) {
            return
        }

        val tjuendeFebruar2023: LocalDateTime = LocalDate.of(2023, 2, 20).atStartOfDay()
        val sendtDatotid: LocalDateTime = soknad.sendtNav ?: soknad.sendtArbeidsgiver ?: return

        if (sendtDatotid.isBefore(tjuendeFebruar2023)) {
            return
        }

        fun String.toSha256(): String {
            val bytes = this.toByteArray()
            val md = java.security.MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }

        val harYsSakerResponse = yrkesskadeClient.hentYrkesskade(HarYsSakerRequest(listOf(soknad.fnr), null)) ?: return
        sykepengesoknadYrkesskadeStatusRepo.save(
            SykepengesoknadYrkesskadeStatus(
                id = null,
                sykepengesoknadUuidHash = soknad.id.toSha256(),
                sendt = sendtDatotid,
                yrkesskadeStatus = harYsSakerResponse.harYrkesskadeEllerYrkessykdom.name
            )
        )
    }
}

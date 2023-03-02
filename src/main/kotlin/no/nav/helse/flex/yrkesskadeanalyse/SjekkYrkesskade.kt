package no.nav.helse.flex.yrkesskadeanalyse

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
public class SjekkYrkesskade {
    fun sjekkSoknad(soknad: EnkelSykepengesoknad) {
        if (soknad.status != Soknadsstatus.SENDT) {
            return
        }

        val tjuendeFebruar2023: LocalDateTime = LocalDate.of(2023, 2, 20).atStartOfDay()
        val sendtDatotid: LocalDateTime = soknad.sendtNav ?: soknad.sendtArbeidsgiver ?: return

        if (sendtDatotid.isBefore(tjuendeFebruar2023)) {
            return
        }
    }
}

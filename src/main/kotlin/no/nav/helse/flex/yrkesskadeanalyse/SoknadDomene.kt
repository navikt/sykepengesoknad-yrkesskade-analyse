package no.nav.helse.flex.yrkesskadeanalyse

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.helse.flex.objectMapper

data class EnkelSykepengesoknad(
    val id: String,
    val status: Soknadsstatus,
    val type: Soknadstype,
    val fnr: String,
    val sykmeldingId: String?,
    val sendtArbeidsgiver: java.time.LocalDateTime?,

    val sendtNav: java.time.LocalDateTime?

)

enum class Soknadsstatus {
    NY,
    SENDT,
    FREMTIDIG,
    KORRIGERT,
    AVBRUTT,
    SLETTET,
    UTGAATT
}

enum class Soknadstype {
    SELVSTENDIGE_OG_FRILANSERE,
    OPPHOLD_UTLAND,
    ARBEIDSTAKERE,
    ANNET_ARBEIDSFORHOLD,
    ARBEIDSLEDIG,
    BEHANDLINGSDAGER,
    REISETILSKUDD,
    GRADERT_REISETILSKUDD
}

fun String.tilEnkelSykepengesoknad(): EnkelSykepengesoknad = objectMapper.readValue(this)

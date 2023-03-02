package no.nav.helse.flex.yrkesskadeanalyse

import org.springframework.data.annotation.Id
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SendtSoknadRepository : CrudRepository<SendtSoknad, String>

data class SendtSoknad(
    @Id
    val id: String? = null,
    val sykepengesoknadUuidHash: String,
    val sendt: LocalDateTime,
    val yrkesskadeStatus: String
)

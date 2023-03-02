package no.nav.helse.flex.client.yrkesskade

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.*

@Component
class YrkesskadeClient(
    @Value("\${YRKESSKADE_URL}")
    private val url: String,
    private val yrkesskadeRestTemplate: RestTemplate
) {

    fun hentYrkesskade(fnr: String): String {
        return "TJA"
    }
}

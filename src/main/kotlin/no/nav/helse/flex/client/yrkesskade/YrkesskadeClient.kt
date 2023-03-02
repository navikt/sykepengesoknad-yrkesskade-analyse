package no.nav.helse.flex.client.yrkesskade

import no.nav.helse.flex.logger
import no.nav.helse.flex.serialisertTilString
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@Component
class YrkesskadeClient(

    @Value("\${YRKESSKADE_URL}")
    private val url: String,
    private val yrkesskadeRestTemplate: RestTemplate
) {

    val log = logger()

    fun hentYrkesskade(harYsSakerRequest: HarYsSakerRequest): HarYsSakerResponse? {
        try {
            val uriBuilder =
                UriComponentsBuilder.fromHttpUrl("$url/api/v1/saker/har-yrkesskade-eller-yrkessykdom")

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            headers["Nav-Consumer-Id"] = "sykepengesoknad-yrkesskade-analyse"

            val result = yrkesskadeRestTemplate
                .exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.POST,
                    HttpEntity(
                        harYsSakerRequest.serialisertTilString(),
                        headers
                    ),
                    HarYsSakerResponse::class.java
                )

            if (result.statusCode != HttpStatus.OK) {
                val message = "Kall mot yrkesskade feiler med HTTP-" + result.statusCode
                log.error(message)
                throw RuntimeException(message)
            }

            result.body?.let { return it }

            val message = "Kall mot yrkesskade returnerer ikke data"
            log.error(message)
            throw RuntimeException(message)
        } catch (e: HttpClientErrorException) {
            if (e.message?.contains("Det må angis et gyldig fødselsnummer") == true) {
                return null
            }
            throw e
        }
    }
}

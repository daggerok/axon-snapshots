package daggerok.rest

import com.mongodb.MongoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class EventsProjection(val mongoClient: MongoClient,
                       @Value("\${spring.datasource.name}") val name: String) {

  @ResponseStatus(OK)
  @DateTimeFormat(iso = DATE_TIME)
  @GetMapping(path = ["", "/"], produces = [APPLICATION_JSON_UTF8_VALUE])
  fun index(@RequestParam(name = "collection", defaultValue = "snapshots") collection: String) =
      mongoClient.getDatabase(name)
          .getCollection(collection)
          .find()
}

package pl.dna.domain

import java.time.OffsetDateTime

class User(val login: String, val password: ByteArray, val name: String, val creationDate: OffsetDateTime) {
}

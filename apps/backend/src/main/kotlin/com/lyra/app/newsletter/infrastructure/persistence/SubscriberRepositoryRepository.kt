package com.lyra.app.newsletter.infrastructure.persistence

import com.lyra.app.newsletter.domain.FirstName
import com.lyra.app.newsletter.domain.LastName
import com.lyra.app.newsletter.domain.Name
import com.lyra.app.newsletter.domain.Subscriber
import com.lyra.app.newsletter.domain.SubscriberId
import com.lyra.app.newsletter.domain.SubscriberRepository
import com.lyra.app.newsletter.domain.SubscriberStatus
import com.lyra.app.newsletter.infrastructure.persistence.entity.SubscriberEntity
import com.lyra.app.newsletter.infrastructure.persistence.repository.SubscriberRegistratorR2dbcRepository
import com.lyra.common.domain.email.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class SubscriberRepositoryRepository(
    private val subscriberRegistratorR2dbcRepository: SubscriberRegistratorR2dbcRepository
) : SubscriberRepository {
    override suspend fun create(subscriber: Subscriber) {
        val entity = subscriber.toEntity()

        subscriberRegistratorR2dbcRepository
            .save(entity)
            .onErrorResume { throwable ->
                if (throwable is DuplicateKeyException) {
                    log.info("Subscriber already exists in the database: ${subscriber.email.email}")
                    Mono.empty() // Ignore the exception and continue with an empty Mono
                } else {
                    log.error("Error while saving subscriber to database: ${subscriber.email.email}", throwable)
                    Mono.error(throwable) // Propagate the exception
                }
            }
            .subscribe()
    }

    override suspend fun searchAll(): Flow<Subscriber> {
        return subscriberRegistratorR2dbcRepository.findAll()
            .map { it.toDomain() }.asFlow()
    }

    override suspend fun searchActive(): Flow<Subscriber> {
        return subscriberRegistratorR2dbcRepository.findAllByStatus(SubscriberStatus.ENABLED)
            .map { it.toDomain() }.asFlow()
    }

    private fun Subscriber.toEntity(): SubscriberEntity {
        return SubscriberEntity.create(
            id = id.value,
            email = email.value,
            firstname = name.firstName.toString(),
            lastname = name.lastName.toString(),
            status = status,
        )
    }

    private fun SubscriberEntity.toDomain(): Subscriber {
        return Subscriber(
            id = SubscriberId(id),
            email = Email(email),
            name = Name(
                firstName = FirstName(firstname),
                lastName = lastname?.let { LastName(it) },
            ),
            status = status,
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(SubscriberRepositoryRepository::class.java)
    }
}

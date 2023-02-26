/*
 * Copyright 2023 Paul Schickling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.schiggo.transformer.demo.pipeline;

import de.schiggo.transformer.basics.DataSource;
import de.schiggo.transformer.basics.StateContext;
import de.schiggo.transformer.basics.interfaces.Sink;
import de.schiggo.transformer.demo.persistence.source.entity.AddressEntity;
import de.schiggo.transformer.demo.persistence.source.entity.PersonEntity;
import de.schiggo.transformer.demo.persistence.source.repo.AddressRepository;
import de.schiggo.transformer.demo.persistence.source.repo.PersonRepository;
import de.schiggo.transformer.demo.persistence.target.entity.PersonReportingEntity;
import de.schiggo.transformer.demo.persistence.target.repo.PersonReportingRepository;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineExecutor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Optional;

/**
 * This pipeline determines all {@link PersonReportingEntity} and store them in the repository.
 * <br>
 * Source entities:
 * <ul>
 *  <li>{@link PersonEntity}</li>
 *  <li>{@link AddressEntity}</li>
 * </ul>
 * <br>
 * Only the first address of a person is considered. Persons without address or with another country code then "DE" gets
 * filtered out.
 *
 * @author Paul Schickling
 */
@RequiredArgsConstructor
@Slf4j
public class SimplePersonPipeline implements PipelineExecutor {

    // Source repositories
    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;

    // Target repository
    private final PersonReportingRepository personReportingRepository;

    // TODO fails for concurrency. Pass it to every function as argument to solve this
    private final Calendar validAt;

    /**
     * Executes the whole pipeline.
     */
    public void execute() {

        // StateContext only necessary for ExceptionHandling
        StateContext<Long> sc = new StateContext<>();

        log.info("Initialize pipeline");
        // Pipeline to update all person-reporting
        Sink<PersonReportingEntity> sink =
                // Load all main ids from source
                // Here we only load ids first to prevent high memory usage. It is also possible to implement an
                // iterator that fetches each entry with just storing the last id.
                new DataSource<>(personRepository.findAllMainIds(validAt).iterator()).setStateContext(sc)
                        // Load entity for the given id
                        .transform(l -> personRepository.getByMainId(l, validAt))
                        // Transform person to person-reporting
                        .transform(this::personToPersonReporting)
                        // add address to person-reporting
                        .transform(this::addFirstAddressToPersonReporting)
                        // filter if no address or address without country-code "DE"
                        .filter(w -> w.getAddress() == null || !"DE".equals(w.getAddress().getCountryCode()))
                        // remove wrapper
                        .transform(SourceTargetWrapper::getPersonReporting)
                        // Filter out entities which did not change
                        .transform(this::currentOrNext)
                        // Write to target repository
                        .sink(personReportingRepository::save)
                        .exceptionHandling(sc, (id, e) -> log.error("Failed to process id {} with message '{}'", id, e.getMessage()), true);

        log.info("Execute Pipeline");
        sink.execute();

        // Finalize pipeline update
        // 1. Delete entries where next=false
        personReportingRepository.deleteWithoutNext();
        // 2. Set current=next and then next=false
        personReportingRepository.nextToCurrent();
    }

    /**
     * Receives a person from source and initializes the target person. Thereby, all information from source person
     * are moved to the target person. A {@link SourceTargetWrapper wrapper} is used to keep person information for
     * possible later use.
     * <br>
     * Sets the following information in the target:
     * <ul>
     *     <li>MainPersonId</li>
     *     <li>Age</li>
     * </ul>
     *
     * @param person person from source
     * @return Wrapper with source and target person
     */
    private SourceTargetWrapper personToPersonReporting(PersonEntity person) {
        log.debug("Execute pipeline transformation 'personToPersonReporting'");
        int age = toAge(person.getDayOfBirth());
        PersonReportingEntity target = PersonReportingEntity.builder().age(age).mainPersonId(person.getMainId()).build();
        return SourceTargetWrapper.builder().personReporting(target).person(person).build();
    }

    /**
     * Calculates the age from a given day of birth. The age is determined to {@link #validAt}.
     *
     * @param dayOfBirth the day of birth
     * @return Age. Can be negative, if day of birth is in the future.
     */
    private int toAge(Calendar dayOfBirth) {
        Calendar now = validAt;
        int age = now.get(Calendar.YEAR) - dayOfBirth.get(Calendar.YEAR) - 1;
        if (now.get(Calendar.MONTH) > dayOfBirth.get(Calendar.MONTH)) {
            age++;
        } else if (now.get(Calendar.MONTH) == dayOfBirth.get(Calendar.MONTH) || now.get(Calendar.DAY_OF_MONTH) >= dayOfBirth.get(Calendar.DAY_OF_MONTH)) {
            age++;
        }
        return age;
    }

    /**
     * Loads person's addresses and updates address information in the target.
     * <br>
     * Sets the following information in the target:
     * <ul>
     *     <li>MainAddressId</li>
     *     <li>City</li>
     *     <li>ZipCode</li>
     * </ul>
     *
     * @param wrapper Wrapper containing target and already some source entities
     * @return Wrapper with changed target and maybe additional address entity
     */
    private SourceTargetWrapper addFirstAddressToPersonReporting(SourceTargetWrapper wrapper) {
        log.debug("Execute pipeline transformation 'addFirstAddressToPersonReporting'");
        // Load person's first address from repository
        Optional<AddressEntity> optionalAddress = addressRepository.findAllByMainPersonId(wrapper.getPerson().getMainId(), validAt).stream().findFirst();
        if (optionalAddress.isPresent()) {
            AddressEntity address = optionalAddress.get();
            // If address available store it in the wrapper for optional later use
            wrapper.setAddress(address);

            // write necessary address information to person-reporting
            wrapper.getPersonReporting().setMainAddressId(address.getMainId());
            wrapper.getPersonReporting().setCity(address.getCity());
            wrapper.getPersonReporting().setZipCode(address.getZipCode());
        }
        return wrapper;
    }

    /**
     * {@link PersonReportingEntity} have the values {@link PersonReportingEntity#getCurrent() current} and
     * {@link PersonReportingEntity#getNext() next}. This function manages these.
     * <br>
     * In case the values are changed, then we only set next to true.
     * <br>
     * In case the values are unchanged (same to the current in the repository), then we replace the new one with the
     * current one and then set next to true. Then, when we persist it, the existing one while updated instead of
     * inserting a new line.
     *
     * @param update the new determined version of {@link PersonReportingEntity}.
     * @return Either update or the current entity from the repository, depending on weather they had different values.
     */
    private PersonReportingEntity currentOrNext(PersonReportingEntity update) {
        log.debug("Execute pipeline transformation 'currentOrNext'");

        // Load current from repository
        Optional<PersonReportingEntity> optionalCurrent = personReportingRepository.findCurrentByMainPersonId(update.getMainPersonId());
        if (optionalCurrent.isPresent()) {
            PersonReportingEntity current = optionalCurrent.get();
            // Current entry exist, therefore check for changes

            // If change detected, then proceed with the given one
            if ((current.getAge() == null && update.getAge() != null) || (current.getAge() != null && !current.getAge().equals(update.getAge()))) {
                log.debug("Person with main id {} is changed", update.getMainPersonId());
                return update;
            }
            if ((current.getMainAddressId() == null && update.getMainAddressId() != null) || (current.getMainAddressId() != null && !current.getMainAddressId().equals(update.getMainAddressId()))) {
                log.debug("Person with main id {} is changed", update.getMainPersonId());
                return update;
            }
            if ((current.getCity() == null && update.getCity() != null) || (current.getCity() != null && !current.getCity().equals(update.getCity()))) {
                log.debug("Person with main id {} is changed", update.getMainPersonId());
                return update;
            }
            if ((current.getZipCode() == null && update.getZipCode() != null) || (current.getZipCode() != null && !current.getZipCode().equals(update.getZipCode()))) {
                log.debug("Person with main id {} is changed", update.getMainPersonId());
                return update;
            }

            // No change detected, therefore replace with current and set next to true
            log.debug("Person with main id {} is unchanged", update.getMainPersonId());
            current.setNext(true);
            return current;
        }

        // No current entry, therefore just proceed with the given one (attribute 'next' is true by default)
        log.debug("Person with main id {} is a new entry", update.getMainPersonId());
        return update;
    }


    /**
     * Helper class to wrap about target and source entities. Allows to carry used entities to later stages and
     * possibly reuse them.
     */
    @Data
    @Builder
    private static final class SourceTargetWrapper {
        private PersonReportingEntity personReporting;
        private PersonEntity person;
        private AddressEntity address;
    }

}

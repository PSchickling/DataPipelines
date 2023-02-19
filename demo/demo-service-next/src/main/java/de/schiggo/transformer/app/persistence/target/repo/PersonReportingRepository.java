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

package de.schiggo.transformer.app.persistence.target.repo;

import de.schiggo.transformer.app.persistence.target.enity.PersonReportingEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonReportingRepository extends CrudRepository<PersonReportingEntity, Long> {

    @Query(value = """
            SELECT p.* FROM D_PersonReporting p WHERE p.MainPersonId = :mainPersonId AND p.Current
            """, nativeQuery = true)
    Optional<PersonReportingEntity> findCurrentByMainPersonId(long mainPersonId);

    @Modifying
    @Query(value = """
            DELETE FROM D_PersonReporting AS p WHERE NOT p.next
            """, nativeQuery = true)
    void deleteWithoutNext();

    @Modifying
    @Query(value = """
            UPDATE D_PersonReporting AS p SET p.current = p.next, p.next = FALSE WHERE p.next
            """, nativeQuery = true)
    void nextToCurrent();

}

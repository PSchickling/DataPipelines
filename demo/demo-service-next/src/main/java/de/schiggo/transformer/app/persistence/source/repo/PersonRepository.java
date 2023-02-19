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

package de.schiggo.transformer.app.persistence.source.repo;

import de.schiggo.transformer.app.persistence.source.enity.PersonEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends org.springframework.data.repository.Repository<PersonEntity, Long> {

    @Query(value = """
            SELECT p.* FROM T_Person AS p WHERE NOT p.Deleted AND p.ValidFrom <= CURRENT_TIMESTAMP
            AND NOT EXISTS (SELECT sub.* FROM T_Person AS sub WHERE p.MainId = sub.MainId AND sub.ValidFrom <= CURRENT_TIMESTAMP AND sub.ValidFrom > p.ValidFrom)
            """, nativeQuery = true)
    List<PersonEntity> findAll();

    @Query(value = """
            SELECT p.MainId FROM T_Person AS p WHERE NOT p.Deleted AND p.ValidFrom <= :validAt
            AND NOT EXISTS (SELECT sub.* FROM T_Person AS sub WHERE p.MainId = sub.MainId AND sub.ValidFrom <= :validAt AND sub.ValidFrom > p.ValidFrom)
            """, nativeQuery = true)
    List<Long> findAllMainIds(Calendar validAt);

    @Query(value = """
            SELECT p.* FROM T_Person AS p WHERE NOT p.Deleted AND p.ValidFrom <= :validAt
            AND NOT EXISTS (SELECT sub.* FROM T_Person AS sub WHERE p.MainId = sub.MainId AND sub.ValidFrom <= :validAt AND sub.ValidFrom > p.ValidFrom)
            """, nativeQuery = true)
    List<PersonEntity> findAll(Calendar validAt);

    @Query(value = """
            SELECT p.* FROM T_Person AS p WHERE p.MainId = :mainId AND NOT p.Deleted AND p.ValidFrom <= CURRENT_TIMESTAMP
            AND NOT EXISTS (SELECT sub.* FROM T_Person AS sub WHERE p.MainId = sub.MainId AND sub.ValidFrom <= CURRENT_TIMESTAMP AND sub.ValidFrom > p.ValidFrom)
            """, nativeQuery = true)
    Optional<PersonEntity> findByMainId(long mainId);

    @Query(value = """
            SELECT p.* FROM T_Person AS p WHERE p.MainId = :mainId AND NOT p.Deleted AND p.ValidFrom <= :validAt
            AND NOT EXISTS (SELECT sub.* FROM T_Person AS sub WHERE p.MainId = sub.MainId AND sub.ValidFrom <= :validAt AND sub.ValidFrom > p.ValidFrom)
            """, nativeQuery = true)
    Optional<PersonEntity> findByMainId(long mainId, Calendar validAt);

    @Query(value = """
            SELECT p.* FROM T_Person AS p WHERE p.MainId = :mainId AND NOT p.Deleted AND p.ValidFrom <= :validAt
            AND NOT EXISTS (SELECT sub.* FROM T_Person AS sub WHERE p.MainId = sub.MainId AND sub.ValidFrom <= :validAt AND sub.ValidFrom > p.ValidFrom)
            """, nativeQuery = true)
    PersonEntity getByMainId(long mainId, Calendar validAt);
}

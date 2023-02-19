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

import de.schiggo.transformer.app.persistence.source.enity.AddressEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository
public interface AddressRepository extends org.springframework.data.repository.Repository<AddressEntity, Long> {

    @Query(value = """
            SELECT a.* FROM T_Address AS a WHERE a.MainPersonId = :mainPersonId AND NOT a.Deleted AND a.ValidFrom <= CURRENT_TIMESTAMP
            AND NOT EXISTS (SELECT sub.* FROM T_Address AS sub WHERE a.MainId = sub.MainId AND sub.ValidFrom <= CURRENT_TIMESTAMP AND sub.ValidFrom > a.ValidFrom)
            """, nativeQuery = true)
    List<AddressEntity> findAllByMainPersonId(Long mainPersonId);

    @Query(value = """
            SELECT a.* FROM T_Address AS a WHERE a.MainPersonId = :mainPersonId AND NOT a.Deleted AND a.ValidFrom <= :validAt
            AND NOT EXISTS (SELECT sub.* FROM T_Address AS sub WHERE a.MainId = sub.MainId AND sub.ValidFrom <= :validAt AND sub.ValidFrom > a.ValidFrom)
            """, nativeQuery = true)
    List<AddressEntity> findAllByMainPersonId(Long mainPersonId, Calendar validAt);

}

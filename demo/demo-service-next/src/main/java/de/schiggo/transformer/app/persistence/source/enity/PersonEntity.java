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

package de.schiggo.transformer.app.persistence.source.enity;

import de.schiggo.transformer.app.persistence.source.versioning.VersionedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "T_Person")
public class PersonEntity extends VersionedEntity {

    @Id
    @Column(name = "personid")
    private Long personId;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "dayofbirth")
    private Calendar dayOfBirth;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person")
    @ToString.Exclude
    @Singular
    private List<AddressEntity> addresses;

}

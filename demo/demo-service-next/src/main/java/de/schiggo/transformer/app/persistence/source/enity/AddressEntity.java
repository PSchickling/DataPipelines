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

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Calendar;

@Data
@Entity(name = "T_Address")
public class AddressEntity {

    @Id
    @Column(name = "mainid")
    private Long mainId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mainpersonid")
    @ToString.Exclude
    private PersonEntity person;

    @Column(name = "street")
    private String street;

    @Column(name = "streetnumber")
    private String streetNumber;

    @Column(name = "zipcode")
    private String zipCode;

    @Column(name = "city")
    private String city;

    @Column(name = "countrycode")
    private String countryCode;

    @Column(name = "validfrom")
    private Calendar validFrom;

    @Column(name = "deleted")
    private Boolean deleted;

}

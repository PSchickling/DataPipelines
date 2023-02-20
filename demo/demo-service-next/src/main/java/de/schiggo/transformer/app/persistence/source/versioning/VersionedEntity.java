package de.schiggo.transformer.app.persistence.source.versioning;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Calendar;

@Data
@MappedSuperclass
public class VersionedEntity {

    @Column(name = "mainid")
    private Long mainId;

    @Column(name = "validfrom")
    private Calendar validFrom;

    @Column(name = "deleted")
    private Boolean deleted;

}

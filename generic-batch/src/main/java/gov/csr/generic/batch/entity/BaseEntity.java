package gov.csr.generic.batch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@MappedSuperclass
public class BaseEntity {

    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "MODIFIED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedOn;

    @Column(name = "ERROR_MESSAGE", length = 450)
    private String errorMessage;

    /** 1 = HIGH, 0 = LOW */
    @Column(name = "PRIORITY", nullable = false)
    private Integer priority = 0;

    /** 1 = ACTIVE, 0 = INACTIVE */
    @Column(name = "IS_ACTIVE", nullable = false)
    private Integer isActive = 1;

    /** 0 = NOT UPDATED, 1 = UPDATED */
    @Column(name = "IS_UPDATED", nullable = false)
    private Integer isUpdated = 0;

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        createdOn = now;
        modifiedOn = now;
    }

    @PreUpdate
    public void preUpdate() {
        modifiedOn = new Date();
    }
}

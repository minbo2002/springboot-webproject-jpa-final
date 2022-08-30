package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist  // persist 하기전 이벤트 발생
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate  // update 하기전 이벤트 발생
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}

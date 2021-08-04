package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

//진짜 상속관계는 아니고 속성만 내려서 쓸 수 있는 관계. 테이블에서 속성만 내려서 쓸 수 있게 함. 진짜 상속이 아님.
@MappedSuperclass // 이걸 통해 속성들이 내려감. 그러면 createdDate updatedDate    가 들어감.
@Getter
public class JpaBaseEntity {

    @Column(updatable = false) // db의 값이 변경되지 않음.
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        // persist 하기 전 이벤트 발생
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate // update 하기 전 이벤트 발생
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }

}

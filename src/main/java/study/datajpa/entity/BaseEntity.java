package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class) // 이벤트를 기반으로 동작한다는 걸 넣어준다. 이게 귀찮으면 전체 적용하려면 META-INF/orm.xml에 entity-listeners 넣어주면 된다.
@MappedSuperclass
@Getter
public class BaseEntity extends BaseTimeEntity{

    // BaseTimeEntity를 상속받았으므로 타임 관련해서는 필요 없다.
    // 명시적으로 작성해준다.
//    @CreatedDate
//    @Column(updatable = false)
//    private LocalDateTime createdDate;
//
//    @LastModifiedDate
//    private LocalDateTime lastModifiedDate;

    // 얘네는 Application에서 더 작성해 줄 게 있다.
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}

package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class) // 이벤트를 기반으로 동작한다는 걸 넣어준다. 이게 귀찮으면 전체 적용하려면 META-INF/orm.xml에 entity-listeners 넣어주면 된다.
@MappedSuperclass
@Getter
public class BaseTimeEntity {
    // 이건 웬만하면 다 쓰니까 얘는 따로 만들어서 얘를 하고, 등록자나 수정자가 필요하면 BaseEntity를 상속받도록 한다.
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}

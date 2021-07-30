package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    private Long Id;
    private String username;

    // jpa의 프록시 기술에서 객체를 만들어야 할 때 필요. private은 안 됨.
    protected Member() {
    }

    public Member(String username) {
        this.username = username;
    }

//    // setter 말고 이런 식으로 하는 게 더 나은 방법이다.
//    public void changeUsername(String username) {
//        this.username = username;
//    }
}

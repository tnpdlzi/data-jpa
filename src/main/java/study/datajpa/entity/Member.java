package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"}) //team은 들어가면 안된다. 그럼 무한 루프 일어남. 연관관계 필드는 toString 하면 안 된다.
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long Id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

//    // jpa의 프록시 기술에서 객체를 만들어야 할 때 필요. private은 안 됨. 이거 대신 noargsconstructor 해서 protected 하면 된다.
//    protected Member() {
//    }

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

//    // setter 말고 이런 식으로 하는 게 더 나은 방법이다.
//    public void changeUsername(String username) {
//        this.username = username;
//    }

    // == 연관관계 편의 메서드 == //
    // 팀에도 멤버가 추가되도록.
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}

package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

// 인터페이스이고 구현체가 없다. 구현 코드가 하나도 없다. 인터페이스만 있다.
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 구현하지 않아도 동작한다. 쿼리메소드 기능.
    // 메소드 이름으로 쿼리를 생성해준다. 관례를 가지고.
    // Username과 AndAge하면 and 조건으로 묶인다. GreaterThan하면 이 파라미터 조건보다 크면! 으로 나오게 된다.
    // 이름을 다르게 하면 안 된다. 프로퍼티 명을 맞춰줘야한다.
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

    // @Param은 :username과 같이 명확히 jpql 이 적혀있을 때 필요하다.
//    @Query(name = "Member.findByUsername") // jpql을 이걸로 찾아서 실행해준다. 근데 이걸 없애도 동작한다. 관례가 있기 때문. 해당 타입에 .을 찍고 메소드 이름으로 named query를 찾는다.
    List<Member> findByUsername(@Param("username") String username);
}

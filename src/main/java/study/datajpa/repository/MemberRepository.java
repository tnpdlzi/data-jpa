package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

// 인터페이스이고 구현체가 없다. 구현 코드가 하나도 없다. 인터페이스만 있다.
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 구현하지 않아도 동작한다. 쿼리메소드 기능.
    // 메소드 이름으로 쿼리를 생성해준다. 관례를 가지고.
    // Username과 AndAge하면 and 조건으로 묶인다. GreaterThan하면 이 파라미터 조건보다 크면! 으로 나오게 된다.
    // 이름을 다르게 하면 안 된다. 프로퍼티 명을 맞춰줘야한다.
    // 길어지면 문제가 생기는 단점.
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

    // @Param은 :username과 같이 명확히 jpql 이 적혀있을 때 필요하다.
//    @Query(name = "Member.findByUsername") // jpql을 이걸로 찾아서 실행해준다. 근데 이걸 없애도 동작한다. 관례가 있기 때문. 해당 타입에 .을 찍고 메소드 이름으로 named query를 찾는다.
    List<Member> findByUsername(@Param("username") String username);

    // 실무에서 많이 사용하는 방법.
    // 여기에 바로 쿼리를 쳐버릴 수 있다. JPQL을 인터페이스 메서드에 바로 적어버릴 수 있다는 장점이 있다.
    // 또한 오타를 쳐도 애플리케이션 로딩 시점에 문법 오류를 잡아준다는 장점이 있다.
    // 동적 쿼리는 Query Dsl을 쓰자.
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    // @Query로 값 조회하기.
    //username 다 가져오기. 그냥 반환 타입을 String으로 하면 된다.
    @Query("select m.username from Member m")
    List<String> findUsernameList(); // 반환 타입 String.

    // @Query로 DTO 조회하기.
    // new operation을 사용해서 마치 생성자로 만들듯이 패키지 루트 다 적고 안에 값을 다 적어주어야 한다.
    // 근데 query dsl을 쓰자...
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 컬렉션으로 검색.
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);


    // 반환 타입을 아무거나 적어도 다 된다. 단건이라는게 보장만 된다면. 반환타입을 유연하게 지원해 준다.
    // 이 외에도 다양한 타입을 지원한다. 공홈 참고. 이터레이터, 컬렉터, 리스트, 스트림, 퓨처, 컴플리터블 퓨처, 슬라이스, 페이지 등 다 지원한다.
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

}

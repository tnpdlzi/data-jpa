package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
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

    // 반환 타입을 Page로 받는다.
    // 두 번째 파라미터로 Pageable이라는 인터페이스를 넘긴다. 쿼리에 대한 조건이 들어감. 1페이지다 2페이지다 등.
//    Page<Member> findByAge(int age, Pageable pageable);

    // 카운트 쿼리만 분리
    // 실무가 복잡해지면 성능을 위해 카운트 쿼리를 분리해주자.
    // sorting조건도 이걸로 넣을 수 있다. 소팅이 복잡해지면 여기서 넣자.
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m") // 카운트 쿼리에서는 레프트조인 필요 없도록. (left join이라는 가정 하에)

    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSlicedByAge(int age, Pageable pageable);

    List<Member> findListByAge(int age, PageRequest pageRequest);

    // 얘가 있어야 jpa executeUpdate를 호출한다. 그래야 변경된 값의 갯수를 받을 수 있다. 얘가 변경한다는 것도 알게 된다. 얘를 빼면 에러가 난다.
    @Modifying(clearAutomatically = true) // 쿼리가 나가고 난 다음에 영속성 컨텍스트의 em.clear 과정을 자동으로 해 준다.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team") // fetch join ---> 연관된 팀을 한방 쿼리로 다 긁어온다.
    List<Member> findMemberFetchJoin();

    @Override // findAll Override
    @EntityGraph(attributePaths = {"team"}) // jpql사용 안 하고 fetchjoin 가능 이거 내부적으로는 사실 fetch join이다.
    List<Member> findAll();

    // 이런 식으로 쿼리 안에서 페치 조인만 살짝 추가하는 것도 가능한다.
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 팀까지 가져오자.
//    @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all") // named entity graph. 근데 이거 실무에서 잘 안 쓴다.
    List<Member> findEntityGraphByUsername(@Param("username") String username);


    // jpa 제공 queryhint.
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //select for update. 비관적인 lock. select 할 때 다른 애들 손대지 말라는 lock을 걸 수 있다.

    // jpa가 제공하는 락을 편리하게.
    // 실시간 트래픽이 많은 애들은 락을 걸지 말자. 돈을 맞추거나 하는 걸 할 때 사용하자.
    @Lock(LockModeType.PESSIMISTIC_WRITE) // jpa 거라는 것.
    List<Member> findLockByUsername(String username);

}

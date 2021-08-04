package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    // 이런 식으로 하나에 다 몰아넣기 보다는 이렇게 분리할 필요가 있따. 커맨드와 쿼리의 분리. 핵심 비즈니스 로직과 화면의 분리. 라이프사이클에 따른 분리 등을 고려해야한다.
    @Autowired MemberQueryRepository memberQueryRepository;

    // interface밖에 없는데 전부 다 동작한다.
    @Test
    public void testMember() {
        // 구현체가 없는데 정체가 뭐냐? 스프링 데이터 jpa가 인터페이스를 보고 구현 클래스를 만들어서 꽂아버린다.
        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        // 원래는 if else로 null 여부 판별해줘야함.
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증.
        // 사실 get을 통해 꺼내는 것은 안 좋은 방법이다.
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 변경 감지 (더티체킹)를 이용한 변경. 그래서 변경 메서드가 따로 없다.
//        findMember1.setUsername("updated member");

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloTop3By() {
        // 전체 조회 중 상위 3개 limit으로 받는다.
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }


    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBBnb"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }


    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");
        Member findMember = memberRepository.findMemberByUsername("AAA");
        Optional<Member> findOptional = memberRepository.findOptionalByUsername("AAA");

        // 얘가 null을 반환하는 게 아닌 empty 컬렉션을 반환한다. 주의. List는 무조건 null이 아니다. 빈 것일 뿐.
        List<Member> result = memberRepository.findListByUsername("asdasd");
        System.out.println("result.size() = " + result.size());

        // 얘는 null을 반환한다.
        Member findWrongMember = memberRepository.findMemberByUsername("asdsa");
        System.out.println("findWrongMember = " + findWrongMember);

        // 얘는 Optional.empty가 나온다. 데이터가 있을 수도 있고 없을 수도 있으면 이걸 쓰자.
        // 하나 타입을 조회하고 싶은데 두 개가 있으면 예외가 터진다. nonuniqueResultException이 터진다.
        Optional<Member> findOptionalMember = memberRepository.findOptionalByUsername("asdsa");
        System.out.println("findOptionalMember = " + findOptionalMember);
    }

    @Test
    public void pagings() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        // 페이지는 0번부터 시작. 0페이지부터 3개 가져와라. 내림차순.
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");

        //when
        // 파라미터에 페이지 조건이 들어간다. 페이저블 인터페이스만 넘기면 된다. 근데 PageRequest인데? 근데 부모를 따라가면 Pageable이 있다. PageRequest가 구현체인것.
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        // 토탈카운트 메서드는 필요없다. 반환타입 Page로 넘어오면 페이지는 토탈카운트가 필요하니 토탈카운트 쿼리까지 같이 날린다.

        // slice는 가져올 때 3개를 가져오지 않고 +1 해서 4개를 요청해서 가져온다. slice return 함수를 가져와야한다. 안 그러면 상속관계가 Slice가 더 위쪽이라 받을 수 있어버린다.
        // 얘는 count query를 보내지 않는다. 그리고 limit는 4개로 나간다.
        // 모바일에서 더보기 사용할 때 사용. Page에서 Slice로 반환 타입만 바꾸면 된다.
        Slice<Member> slice = memberRepository.findSlicedByAge(age, pageRequest);

        // 다른 기능이 동작 안 하고 그냥 잘라서 가져오고 싶으면 그냥 List로 가져오면 된다. 그냥 페이징만 해서 가져오고 싶다면 그냥 이렇게 반환 타입만 List로 바꿔주면 된다.
        List<Member> list = memberRepository.findListByAge(age, pageRequest);

        //then
        List<Member> content = page.getContent(); // content가져오기. 0번째 페이지 3개.
        List<Member> slicedContent = slice.getContent(); // slice 컨텐트 가져오기.

        long totalElements = page.getTotalElements(); // totalCount 가져오기.

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        // === 페이징 === //
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        // 페이지 몇 페이지 인지도 다 계산해준다.
        assertThat(page.getNumber()).isEqualTo(0);
        // 전체 페이지 개수도 계산해 준다.
        assertThat(page.getTotalPages()).isEqualTo(2);
        // 이게 첫 번째 페이지인지 확인도 해 준다. 마지막 페이지도 가능.
        assertThat(page.isFirst()).isTrue();
        // 다음 페이지가 있는지도 확인해 준다. 이전 페이지도 가능.
        assertThat(page.hasNext()).isTrue();

        // === 슬라이스 === //
        assertThat(slicedContent.size()).isEqualTo(3);
        // 슬라이스는 +1 이기 때문에 전체 컨텐트를 가져오지 않는다.
//        assertThat(slice.getTotalElements()).isEqualTo(5);
        // 페이지 몇 페이지 인지도 다 계산해준다.
        assertThat(slice.getNumber()).isEqualTo(0);
        // 슬라이스는 +1이기 때문에 전체 페이지도 가져오지 않는다.
//        assertThat(slice.getTotalPages()).isEqualTo(2);
        // 이게 첫 번째 페이지인지 확인도 해 준다. 마지막 페이지도 가능.
        assertThat(slice.isFirst()).isTrue();
        // 다음 페이지가 있는지도 확인해 준다. 이전 페이지도 가능.
        assertThat(slice.hasNext()).isTrue();


    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        // 페이지는 0번부터 시작. 0페이지부터 3개 가져와라. 내림차순.
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");

        //when
        // 실무에서 페이징을 잘 안 쓰는 이유는 페이징에서 토탈 카운트 쿼리 자체가 모든 컨텐트를 한 번 읽어야해서 성능이 안 나오기 때문이다. 이 토탈 카운트가 데이타가 많아질수록 안 좋아진다.
        // 그래서 이 토탈카운트 쿼리를 잘 짜야할 때가 있다. 그래서 카운트 쿼리를 분리해주는 것도 지원한다.
        // api에서 이 엔티티를 그대로 반환하면 안 된다. DTO로 변환해서 넘겨야한다.
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // DTO로 쉽게 변환하는 방법
        // 얘를 api로 넘기자.
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //then
        List<Member> content = page.getContent(); // content가져오기. 0번째 페이지 3개.

        long totalElements = page.getTotalElements(); // totalCount 가져오기.

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        // === 페이징 === //
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        // 페이지 몇 페이지 인지도 다 계산해준다.
        assertThat(page.getNumber()).isEqualTo(0);
        // 전체 페이지 개수도 계산해 준다.
        assertThat(page.getTotalPages()).isEqualTo(2);
        // 이게 첫 번째 페이지인지 확인도 해 준다. 마지막 페이지도 가능.
        assertThat(page.isFirst()).isTrue();
        // 다음 페이지가 있는지도 확인해 준다. 이전 페이지도 가능.
        assertThat(page.hasNext()).isTrue();

    }


    @Test
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        // bulk 연산은 영속성 컨텍스트를 무시하고 디비에 바로 때려버리기 때문에 주의해야한다.
        // 위의 save는 아직 영속성 컨텍스트에 있기 때문에 벌크를 때리면 안 맞을 수 있다. 벌크 연산할 때 조심해야한다.
        int resultCount = memberRepository.bulkAgePlus(20);

        // db에 남은 변경 내용 반영. save하면 디비에 flush를 반영하기 때문에 flush는 사실 필요하진 않음.
//        em.flush();
        // 영속성 컨텍스트 초기화 그런데 springdatajpa의 @Modifying의 clearAutomatically 옵션을 true 해 주면 없어도 된다.
//        em.clear();
        // 이러면 벌크연산 후의 영속성 컨텍스트를 초기화 된다.

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5); // em.clear를 안 하면 40 살로 나온다. 그런데 디비에는 41살로 되어있다.
        // 영속성 컨텍스트에 값이 남아있기 때문. 1차 캐시에 값이 남아있음.

        //then
        // 20살 이상 다 나이 +1됨.
        assertThat(resultCount).isEqualTo(3);

    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        // select Member --> N + 1  문제 발생.
//        List<Member> members = memberRepository.findAll();
        // fetch join으로 한방에 다 긁어오기. 해결. 가짜 프록시 객체가 아닌 진짜 객체가 들어가게 된다.
//        List<Member> members = memberRepository.findMemberFetchJoin();

        // Entity Graph 설정 한 후의 findAll하면 lazy로딩 되는 것 없이 member와 team을 같이 가져온다.
        //        List<Member> members = memberRepository.findAll();

        // entity graph를 사용하면 fetch join이 편리해진다.
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");


        for (Member member : members) {
            System.out.println("member = " + member.getUsername());

            // 하이버네이트 프록시 객체가 나타난다. lazy면 프록시라는 가짜 객체를 가지고 해놨다가 나중에 실제 데이터를 호출해 집어넣는다.
            System.out.println("member.teamClass = " + member.getTeam().getClass());

            // 팀을 디비에서 가져온다. 팀의 이름을 가져와야하기 때문에 가져오게된다. 이 때 프록시를 초기화 한다.
            System.out.println("member.team = " + member.getTeam().getName());
        }

    }

    @Test
    public void queryHint() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush(); // 여기엔 영속성 컨텍스트가 남아있다.
        em.clear(); // 이러면 영속성 컨텍스트가 다 날아간다.
        
        //when
//        Member findMember = memberRepository.findById(member1.getId()).get();// 실무에서는 get 쓰면 안 된다.
        // 변경 안 하고 읽기만 하겠다! 할 경우 객체를 안 만들고 싶다! 그런데도 이걸 가져오는 순간 원본과 복제본을 만들어버린다. 즉 비효율적인 리소스가 생긴다.
        // 100퍼 조회용으로 사용 할 때 hibernate가 hint로 제공한다.
//        findMember.setUsername("member2"); // 변경 감지 동작. update query가 나간다.

        // 전체 어플리케이션 기준으로 해서 전부다 리드온리로 해야지! 할 필요는 없다. 전체로 봤을 땐 성능 최적화에 크게 작동하지 않는다. 진짜 중요하고 트래픽이 많은 애만 몇 개 넣는 거다.
        // 사실 성능테스트의 큰 이슈들은 디비의 쿼리에서 나온다.

        // @QueryHint를 통한 것. snapshot을 생성하지 않는다. 그래서 변경 감지가 일어나지 않는다.
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        // update되지 않는다. readonly이기 때문.
        findMember.setUsername("member2");

        // 객체 상태가 바뀐 후 flush하면 변경 감지가 돼서 바꼈다는 걸 알게 되고 바꿔준다.
        // 변경감지의 단점은 원본이 있어야한다. 그래서 객체를 두 개 관리하게 되는 것이다. 즉, 비효율적이다.
        em.flush();

    }

    @Test
    public void lock() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // 쿼리를 보면 for update가 붙어있다.
        List<Member> findMember = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom() {
        // 구현된 거가 실행된 거 확인 가능
        List<Member> result = memberRepository.findMemberCustom();
    }

    // 얘의 문제는 inner join은 되는데 outer join이 안 된다. 그리고 중첩 제약조건도 안 된다. 매칭 조건도 단순하다. 실무에서는 query dsl 사용하자.
    @Test
    public void queryByExample() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
//        memberRepository.findByUsername("m1"); 이건 정적일 때 쓰는거. 동적일 땐 다르게.
        //Probe --> 실제 도메인 객체.
        // 도메인 객체 자체를 가지고 그대로 검색조건을 만들어 버리는 것.
        Member member = new Member("m1"); // 엔티티 자체가 검색 조건이 된다.
        Team team = new Team("teamA");
        member.setTeam(team);

        // null은 무시하는데 프리미티브 타입은 0을 가져오기 때문에 무시하겠다고 한것.
        // age는 다 무시하겠다.
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");
        // prove와 matcher로 구성.
        Example<Member> example = Example.of(member, matcher);// entity로 example 만들기.

        List<Member> result = memberRepository.findAll(example); // example을 파라미터로 받는 걸 기본 기능으로 넣어버렸다. spring data jpa의 jpa repository에서 queryByExampleExecutor가 들어가있다.
        assertThat(result.get(0).getUsername()).isEqualTo("m1");

    }
}
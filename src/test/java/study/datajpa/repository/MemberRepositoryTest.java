package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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

}
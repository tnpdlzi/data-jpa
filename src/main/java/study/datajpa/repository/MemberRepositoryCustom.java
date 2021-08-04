package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

// 커스텀으로 만들고 싶은 메서드들 작성한 인터페이스. 주로 query dsl같은 거 사용할 때 많이 쓴다.
// 그리고 구현체에 내가 만들고 싶은 대로 자세히 작성한다.
// 이 인터페이스를 MemberRepository에서 상속받으면 이 구현체에 구현된 메서드가 실행이 된다.
// 실무에서 많이 사용.
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}

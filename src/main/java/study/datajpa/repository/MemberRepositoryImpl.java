package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

// 이름은 꼭 ~~Impl로 맞춰야 한다. 그것이 규칙이다. 그래야 MemberRepository에서 읽을 수 있다. Custom은 아무거나 써도 되는데 Impl은 필수이다. 그럼 스프링 데이터 jpa가 call 했을 때 구현체로 Impl을 찾아서 호출해 준다.
// 실무에서 많이 사용.
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    // 여기서 쓰고싶은 대로 만들어서 쓴다.
    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}

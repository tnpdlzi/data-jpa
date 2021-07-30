package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

import java.util.List;

// 인터페이스이고 구현체가 없다. 구현 코드가 하나도 없다. 인터페이스만 있다.
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 구현하지 않아도 동작한다. 쿼리메소드 기능.
    List<Member> findByUsername(String username);
}

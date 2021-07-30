package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;

// 얘네는 @Repository 생략 가능. 인터페이스만 봐도 스프링 데이터 jpa가 알아서 인식한다.
public interface TeamRepository extends JpaRepository<Team, Long> {
}

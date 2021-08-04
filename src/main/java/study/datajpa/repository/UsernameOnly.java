package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

// Projections
// 내가 원하는 것만 골라서 받고 싶을 때
public interface UsernameOnly {
    // close projection
//    String getUsername();

    // open projection
    // 엔티티를 다 가져와서 처리.
    // username과 age를 다 가져와서 문자를 더해서 넣어준다.
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}

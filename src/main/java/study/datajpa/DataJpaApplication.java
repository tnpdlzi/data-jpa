package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

// data jpa를 이용한 Auditing을 위해 필수
//@EnableJpaAuditing(modifyOnCreate = false) // update가 null 컬럼으로 들어간다. 근데 관례상 같이 넣는 경우가 많기 때문에 굳이 이걸 할 필요는 없다.
@EnableJpaAuditing
@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.datajpa.repository") 부트를 사용하면 이게 필요없음. 자동으로 스프링 데이터 jpa가 끌어올 수 있다.
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		// 지금은 랜덤으로 했지만 실제로 쓸 땐 세션값 꺼내서 쓰면 된다.
		return () -> Optional.of(UUID.randomUUID().toString());
		// 람다 풀면 아래처럼됨. 인터페이스에서 메서드가 하나면 람다로 바꿀 수 있다.
//		return new AuditorAware<String>() {
//			@Override
//			public Optional<String> getCurrentAuditor() {
//				return Optional.of(UUID.randomUUID().toString());
//			}
//		};
	}
}

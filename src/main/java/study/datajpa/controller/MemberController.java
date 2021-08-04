package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 도메인 클래스 컨버터.
    // 이런 기능을 권장하지는 않는다. pk를 외부에 공개하는 경우도 없고 쿼리가 이렇게 단순한 경우도 거의 없고. 간단간단할 때만 쓸 수 있고 복잡해지면 못 쓴다.
    // 얘는 조회용으로만 써야한다.
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) { // 이렇게 그냥 Member를 파라미터로 인젝션 할 수 있다. 등록할 필요 X
        return member.getUsername();
    }

    @PostConstruct
    public void init() {
        memberRepository.save(new Member("userA"));
    }
}

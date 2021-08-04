package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
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

    // 페이징과 정렬
    // http://localhost:8080/members?page=0 하면 20번 까지, page=1하면 21번부터 ~ 이런식으로 나옴
    // http://localhost:8080/members?page=1&size=3 이러면 3개씩 끊어서 나오게 된다. 4 5 6 나옴.
    // pageable의 기능을 자동으로 세팅해줘서 가능. 파라미터들이 바인딩 될 때 pageable이 있으면 pageRequest만들어서 injection 해 준다.
    // http://localhost:8080/members?page=1&size=3&sort=id,desc 이러면 sorting이 id기준, 역순으로 나온다.
    // http://localhost:8080/members?page=1&size=3&sort=id,desc&sort=username,desc 이러면 username기준 역순 소팅. asc는 생략 가능.
    // default는 20개. global 설정을 바꾸고싶다면 application.yml을 바꾸자.

//    @GetMapping("/members")
////    public Page<Member> list(@PageableDefault(size = 5, sort = "username") Pageable pageable) { //@PageableDefault(size = 5, sort = "username") 이걸로 특정 부분 설정 가능
//    public Page<Member> list(Pageable pageable) {
//        return memberRepository.findAll(pageable); // 람다 사용
////
////        Page<Member> page = memberRepository.findAll(pageable);
////        return page;
//    }

//    @GetMapping("/members")
//    public Page<MemberDto> list(Pageable pageable) {
//
////        Page<Member> page = memberRepository.findAll(pageable);
////        // 이렇게 엔티티를 그대로 노출하면 안 된다. DTO로 바꾸자.
////        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
////        return map;
//        // 이렇게 엔티티를 그대로 노출하면 안 된다. DTO로 바꾸자.
////        return memberRepository.findAll(pageable)
////                .map(member -> new MemberDto(member.getId(), member.getUsername(), null));
//
////        // dto가 entity를 보도록 변경
////        return memberRepository.findAll(pageable)
////                .map(member -> new MemberDto(member));
//
//        return memberRepository.findAll(pageable)
//                .map(MemberDto::new);
//    }

//    @GetMapping("/members")
//    public Page<MemberDto> list(Pageable pageable) {
//        // page를 0이 아닌 1부터 시작하는 법 1.
////        PageRequest request = PageRequest.of(1, 2);
////
////        // Page도 바꿔줘야 한다. MyPage같은거 만들어서.
////        MyPage<MemberDto> map = memberRepository.findAll(request)
////                .map(MemberDto::new);
////
//        // page를 0이 아닌 1부터 시작하는법 2. application.yml 설정에서 one-indexed-parameters를 true로 설정한다.
//    }

    @GetMapping("/members")
    public Page<MemberDto> list(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);

    }





//    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}

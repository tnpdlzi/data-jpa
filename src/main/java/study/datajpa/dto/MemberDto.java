package study.datajpa.dto;

import lombok.Data;
import study.datajpa.entity.Member;

@Data // dto라서 사용.
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    // entity는 DTO를 보면 안 된다. 그런데 DTO는 entity를 봐도 된다.
    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
    }
}

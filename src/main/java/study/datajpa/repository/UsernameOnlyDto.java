package study.datajpa.repository;

public class UsernameOnlyDto {

    private final String username;

    // 생성자의 파라미터 이름으로 매칭을 시켜서 projection도 가능.
    // 구체적은 클래스를 명시했기 때문에 프록시가 필요하지 않다.
    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}

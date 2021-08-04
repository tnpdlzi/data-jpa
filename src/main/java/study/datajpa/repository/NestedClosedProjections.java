package study.datajpa.repository;

public interface NestedClosedProjections {

    String getUsername();
    TeamInfo getTeam();

    // 두 번째부터는 최적화가 안 된다. Team은 그냥 엔티티로 가져오게 된다. left join으로 가져오게 된다.
    interface TeamInfo {
        String getName();
    }

}

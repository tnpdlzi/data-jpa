package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    // debug 찍어서 debug 모드로 돌려보기
    // id가 null이라서 새로운 엔티티라고 판단. primitive type의 경우는 0으로 판단.
    @Test
    public void save() {
        Item item = new Item("A"); // 이 때는 pk에 값이 있어서 persist가 호출이 안 된다. 식별자가 null이 아니고 값이 있기 때문에 merge로 가버린다.
        itemRepository.save(item);
        // merge를 하면 있을거라 생각하고 일단 가져오고 없으면 넣는다. 비효율적.
    }

}
package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter  // 실제 entity 클래스에서는 쓰면 안됨
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    protected Member() {

    }

    public Member(String username) {

        this.username = username;
    }
}

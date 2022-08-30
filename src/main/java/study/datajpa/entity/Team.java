package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team extends JpaBaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    private String name;

    // @OneToMany, @ManyToOne 둘다 세팅을 하면
    // 한쪽에 mappedBy 설정을 해야하는데 FK 없는쪽에 설정하는게 좋다.
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}

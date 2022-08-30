package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter  // 실제 entity 클래스에서는 쓰면 안됨
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA는 기본적으로 default 생성자 필요
@ToString(of = {"id", "username", "age"})           // --> AccessLevel을 protected까지 허용 (private 안됨)
@NamedQuery(
        name="Member.findByUsername",
        query = "select m from Member m where m.username = :username")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")  // FK명
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {  // 연관관계를 세팅하는 메서드
        this.team = team;
        team.getMembers().add(this);
    }
}

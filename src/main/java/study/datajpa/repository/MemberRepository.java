package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    // 쿼리메서드 기능1 : 메서드이름으로 쿼리생성
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 쿼리메서드 기능2 : 메서드이름으로 JPA NamedQuery 호출
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    // 쿼리메서드 기능3 : 레포지토리 메서드에 @Query 정의한다.
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsername();

    // @Query로 DTO 조회
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // @Query로 컬렉션 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    // JPA 반환타입 종류 3개
    List<Member> findListByUsername(String username);  // 컬렉션
    Member findMemberByUsername(String username);      // 단건
    Optional<Member> findOptionalByUsername(String username);  // 단건 Optional

    // JPA 페이징, 정렬, count 쿼리 분리(join 필요없을때)
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    // 벌크성 수정쿼리
    @Modifying(clearAutomatically = true)  // update 쿼리 및 .executeUpdate() 메서드를 사용할 수 있는 어노테이션
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // lazy loading N+1문제 해결을 위한 fetch join
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // 엔티티 그래프
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // JPQL + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 메서드 이름으로 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);

}

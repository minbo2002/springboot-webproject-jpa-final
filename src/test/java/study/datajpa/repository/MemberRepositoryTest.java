package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember() {

        Member member = new Member("memberA");
        Member saveMember = memberRepository.save(member);

        /*
        Optional<Member> byId = memberRepository.findById(saveMember.getId());
        Member member1 = byId.get();
        */
        Member findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단 건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void namedQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void queryMethod() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void findUsernameList() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsername();
        for(String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    // @Query로 DTO 조회
    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10);
        member1.setTeam(team);
        memberRepository.save(member1);

        List<MemberDto> memberDtos = memberRepository.findMemberDto();
        for(MemberDto dto : memberDtos) {
            System.out.println("dto = " + dto);
        }
    }

    // @Query로 Collection 파라미터 바인딩
    @Test
    public void findByNames() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    // JPA 반환타입 종류 3개 (단건, 컬렉션, optional)
    @Test
    public void returnType() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember = memberRepository.findMemberByUsername("AAA");  // 단건
        System.out.println("findMember = " + findMember);

        List<Member> findListMember = memberRepository.findListByUsername("AAA");  // 컬렉션
        System.out.println("findListMember = " + findListMember);

        Optional<Member> findOptionalMember = memberRepository.findOptionalByUsername("AAA"); // optional
        System.out.println("findOptionalMember =" + findOptionalMember);
    }

    // 페이징, 정렬
    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // Page entity -> DTO 변환 방법
        Page<MemberDto> mapToDto = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        List<Member> content = page.getContent();  // getContent() : 0번째 페이지의 3개의 데이터 가져옴
        long totalElements = page.getTotalElements();  // .getTotalElements() : totalCount 의미

        assertThat(content.size()).isEqualTo(3);          // .size() : 1페이지당 데이터 개수
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);        // .getNumber() : 페이지 번호 의미
        assertThat(page.getTotalPages()).isEqualTo(2);    // .getTotalPages() : 총페이지 개수
        assertThat(page.isFirst()).isTrue();              // .isFirst()  :  첫페이지인지 여부 확인
        assertThat(page.hasNext()).isTrue();              // .hasNext()  : 다음페이지 있는지 여부 확인
    }

    // 벌크성 수정쿼리
    @Test
    public void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    // Lazy loading N+1 문제  -->  fetch join 사용
    // @EntityGraph 예시
    @Test
    public void findMemberLazy() {
        // given
        // member1 -> teamA 참조
        // member2 -> teamB 참조

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();  // 영속성 컨텍스트에 있는 캐시정보를 DB에 전부 반영
        em.clear();  // 이후에 영속성 컨텍스트 다 날림.

        // when
        // .finaAll() 메서드일 경우 Member 조회 쿼리 1번 날렸는데, member 결과가 2개 나옴 (N+1문제)
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for(Member member : members) {
            System.out.println("member = " + member.getUsername());  // 위에 실제조회한 member 객체값만 나옴
            System.out.println("member.teamClass = " + member.getTeam().getClass());  // 가짜객체인 프록시 team 객체 가져옴
            System.out.println("member.team = " + member.getTeam().getName());  // 실제 team의 이름을 가져와야하므로 그제서야 team 객체에 접근
        }
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }
}
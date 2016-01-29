package spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class MemberDao {
	private JdbcTemplate jdbcTemplate;
	
	public MemberDao(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public Member selectByEmail(String email){
		
		List<Member> results = jdbcTemplate.query("select * from springmember where email = ?", new RowMapper<Member>(){

			@Override
			public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				Member member = new Member(rs.getString("email"),
						rs.getString("password"),
						rs.getString("name"),
						rs.getTimestamp("regdate"));
				member.setId(rs.getLong("id"));
				return member;
			}
			
		},email);
		return results.isEmpty()?null:results.get(0);
	}
	
	public void insert(final Member member){
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				// TODO Auto-generated method stub
				
				PreparedStatement pstmt = conn.prepareStatement("insert into springmember (email,password,name,regdate)"+
				"values(?,?,?,?)",new String[]{"id"}
						);
				pstmt.setString(1, member.getEmail());
				pstmt.setString(2, member.getPassword());
				pstmt.setString(3, member.getName());
				pstmt.setTimestamp(4, new Timestamp(member.getRegisterDate().getTime()));
				return pstmt;
			}
		},keyHolder);
		Number keyValue = keyHolder.getKey();
		member.setId(keyValue.longValue());
	}
	
	public void update(Member member){
		jdbcTemplate.update("update springmember set name = ?,password = ? where email=?",
				member.getName(),member.getPassword(),member.getEmail()
				);
		
	}
	
	/**
	 * 결과가 다수의 행일때는 quert메서드를 구현 , 결과가 다수의 칼럼일경우 두번째 인자에 RowMapper인터페이스를 구현
	 * **/
	public List<Member> selectAll(){
		
		List<Member> members = null;
		members = jdbcTemplate.query("select * from springmember",new RowMapper<Member>(){

			@Override
			public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				Member member = new Member(rs.getString("email"),
						rs.getString("password"),
						rs.getString("name"),
						rs.getTimestamp("regdate"));
				member.setId(rs.getLong("id"));
				return member;
			}});
		return members.isEmpty()?null:members;
	}
	
	public int count(){
		/**
		 * 결과가 한개의 행일때는 queryForObject 메서드를 구현 , 결과가 한칼럼일때는 칼럼값을 리턴
		 * **/
		Integer count = jdbcTemplate.queryForObject("select count(*) from springmember", Integer.class);
		return count;
	}
}

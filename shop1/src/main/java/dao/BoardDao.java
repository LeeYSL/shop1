package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.checkerframework.checker.units.qual.t;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import logic.Board;

@Repository
public class BoardDao {
	private NamedParameterJdbcTemplate template;
	private Map<String, Object> param = new HashMap<>();
	private RowMapper<Board> mapper = new BeanPropertyRowMapper<>(Board.class);

	@Autowired // spring-db.xml에서 설정한 dataSource 객체 주입
	public void setDataSource(DataSource dataSource) {
		template = new NamedParameterJdbcTemplate(dataSource);

	}

	public int maxNum() {
		return template.queryForObject("select ifnull(max(num),0) from board", param, Integer.class);

	}

	public void insert(Board board) { // board에 있는 프로퍼티로 쓰도록...?
		SqlParameterSource param = new BeanPropertySqlParameterSource(board);
		String sql = "insert into board (num,writer,pass, "
				+ " title, content, file1, boardid, regdate, readcnt, grp, grplevel, grpstep) values "
				+ "(:num, :writer, :pass, :title, :content,:fileurl, "
				+ " :boardid, now(), 0, :grp, :grplevel, :grpstep)";
		template.update(sql, param); // fil1에는 fileurl 넣어라

	}

	public int count(String boardid, String searchtype, String searchcontent ) {
		String sql = "select count(*) from board where boardid=:boardid";
		param.clear();
		param.put("boardid", boardid);
		if(searchtype != null && searchcontent != null) { //검색 값을 가지고 있는 상태(검색 요청)
			sql += " and " + searchtype + " like :searchcontent"; //like searchcontent
			param.put("searchcontent","%" + searchcontent + "%");
		}
		return template.queryForObject(sql, param, Integer.class);
	}

	private String select = "select num,writer,pass,title,content,file1 fileurl,"
			+ " regdate, readcnt, grp, grplevel, grpstep, boardid from board";

	public List<Board> list(Integer pageNum, int limit, String boardid,String searchtype, String searchcontent) {
		param.clear();
		String sql = select;
//		sql += " where boardid=:boardid order by grp desc, grpstep asc limit :startrow, :limit";
		sql += " where boardid=:boardid";
		if(searchtype != null && searchcontent != null ) {
			sql += " and " + searchtype + " like :searchcontent";
			param.put("searchcontent","%" + searchcontent + "%");
		}
		param.put("startrow", (pageNum - 1) * limit); // 1페이지 : 0, 2페이지 :10 페이지
		param.put("limit", limit);
		param.put("boardid", boardid);
		return template.query(sql, param, mapper);
	}

	public Board selectOne(Integer num) {
		String sql = select + " where num=:num";
		param.clear();
		param.put("num", num);
		return template.queryForObject(sql, param, mapper);

	}

	public void addReadcnt(Integer num) {
		param.clear();
		param.put("num", num);
		String sql = "update board set readcnt = readcnt + 1 " + " where num=:num";
		template.update(sql, param);
	}


	public void updateGrpStep(Board board) {
		String sql = "update board set grpstep=grpstep +1"
                  + " where grp = :grp and grpstep > :grpstep";
		param.clear();
		param.put("grp", board.getGrp()); // 원글의 grp
		param.put("grpstep", board.getGrpstep()); // 원글의 grpstep
		template.update(sql, param);
 	}
}
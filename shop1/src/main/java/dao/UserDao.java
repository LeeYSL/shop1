package dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import logic.User;



@Repository
public class UserDao {
	private NamedParameterJdbcTemplate template;
	private RowMapper<User> mapper = new BeanPropertyRowMapper<User>(User.class); 
	private Map<String,Object> param = new HashMap<>();
	@Autowired
	public void setDataSource(DataSource dataSource) {
		template = new NamedParameterJdbcTemplate(dataSource);
	}
	public void insert(User user) { // user : 화면에서 입력받은 값을 저장하고 있는 객체
       //param : user 객체의 프로퍼티를 이용하여 db에 값을 등록
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		String sql = "insert into useraccount (userid,username,password, "
				    + " birthday, phoneno,postcode,address,email) values "
				    + " (:userid,:username,:password,"
				    + " :birthday,:phoneno,:postcode,:address,:email)";
		   template.update(sql, param);
	}
	public User selectOne(String userid) {
		param.clear();
		param.put("userid",userid);
		return template.queryForObject
				   ("select * from useraccount where userid=:userid",param, mapper);
	}
//	public User selectUserpass(String userid, String password)  {
//		param.clear();
//		param.put("userid",userid);
//		param.put("password",password);
//		return template.queryForObject
//				   ("select * from useraccount where userid=:userid and password=:password",param, mapper);
//	
//	}
	
}
	

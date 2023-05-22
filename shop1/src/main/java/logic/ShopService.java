package logic;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dao.ItemDao;
import dao.UserDao;

@Service // @Compoent + Service(controller 기능과 dao 기능의 중간 역할 기능(중간다리))
public class ShopService {
	@Autowired // itemDao 객체 주입
	private ItemDao itemDao;
	
	@Autowired
    private UserDao userDao;

	public List<Item> itemList() {
		return itemDao.list();
	}

	public Item getItem(Integer id) {
		return itemDao.getItem(id);
	}

	public void itemCreate(Item item, HttpServletRequest request) {
		if (item.getPicture() != null && !item.getPicture().isEmpty()) {
			// 업로드해야 되는 파일의 내용이 있는 경우
			String path = request.getServletContext().getRealPath("/") + "img/";
			uploadFileCreate(item.getPicture(), path);
			// 업로드 된 파일 이름을 setPictureUrl에다 저장
			item.setPictureUrl(item.getPicture().getOriginalFilename());
		}
		// db에 내용 저장
		int maxid = itemDao.maxId(); // item 테이블에 저장된 최대 id 값
		item.setId(maxid + 1);
		itemDao.insert(item);// db에 추가

	}

	private void uploadFileCreate(MultipartFile file, String path) {
		// file : 파일의 내용 path : 업로드할 파일
		String orgFile = file.getOriginalFilename(); // 파일 이름
		File f = new File(path);
		if (!f.exists())
			f.mkdirs(); // 폴더 만들어줘
		try {
			// file에 저장 된 내용을 path+orgFile(이미지 밑에 원래 파일 이름으로 바꿔서) 저장해라
			file.transferTo(new File(path + orgFile));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void itemUpdate(Item item, HttpServletRequest request) {
		if (item.getPicture() != null && !item.getPicture().isEmpty()) {
			// 업로드해야 되는 파일의 내용이 있는 경우
			String path = request.getServletContext().getRealPath("/") + "img/";
			uploadFileCreate(item.getPicture(), path);
			// 업로드 된 파일 이름을 setPictureUrl에다 저장
			item.setPictureUrl(item.getPicture().getOriginalFilename());
		}
		// db에 내용 저장
//		int maxid = itemDao.maxId(); //item 테이블에 저장된 최대 id 값
//		item.setId(maxid+1);
		itemDao.update(item);// db에 추가

	}

	public void itemDelete(Integer id) {
		itemDao.delete(id);

	}

	public void userInsert(User user) {
	      userDao.insert(user);
		
	}

	public User selectUserOne(String userid) {
		return userDao.selectOne(userid);
	}

	public User selectUserpass(String userid, String password) {
		return userDao.selectUserpass(userid,password);
	}

}

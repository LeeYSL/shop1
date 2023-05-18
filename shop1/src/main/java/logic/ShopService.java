package logic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dao.ItemDao;

@Service //@Compoent + Service(controller 기능과 dao 기능의 중간 역할 기능(중간다리)) 
public class ShopService {
    @Autowired // itemDao 객체 주입
    private ItemDao itemDao;
    
    public List<Item> itemList() {
    	return itemDao.list();
    }

	public Item getItem(Integer id) {
		return itemDao.getItem(id);
	}
}

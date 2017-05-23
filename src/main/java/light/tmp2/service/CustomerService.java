package light.tmp2.service;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.light.framework.helper.DatabaseHelper;
import com.ligth.framework.annotation.Service;
import com.ligth.framework.annotation.Transaction;

import light.tmp2.model.Customer;

/**
 * 提供客户数据服务
 */
@Service
public class CustomerService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);
	
	/**
	 * 获取客户列表 
	 */
	public List<Customer> getCustomerList(){
		String sql = "SELECT * FROM customer";
		return DatabaseHelper.queryEntityList(Customer.class, sql);	
	}
	
	/**
	 * 获取客户
	 */
	public Customer getCustomer(long id){
		//TODO
		return null;
	}
	
	/**
	 * 创建客户
	 */
	@Transaction
	public boolean createCustomer(Map<String, Object> fieldMap){
		
		return DatabaseHelper.insertEntity(Customer.class, fieldMap);
	}
	
	/**
	 * 更新客户
	 */
	@Transaction
	public boolean updateCustomer(long id, Map<String, Object> fieldMap){
		return DatabaseHelper.updateEntity(Customer.class, id, fieldMap);
	}
	
	/**
	 * 删除用户
	 */
	@Transaction
	public boolean deleteCustomer(long id){
		return DatabaseHelper.deleteEntity(Customer.class, id);
	}
}

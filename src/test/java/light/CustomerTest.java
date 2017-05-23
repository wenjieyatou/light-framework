package light;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import light.tmp2.model.Customer;
import light.tmp2.service.CustomerService;

public class CustomerTest {
	
	private final CustomerService customerService;
	
	public CustomerTest(){
		customerService = new CustomerService();
	}

	@Test
	public void customerMethodTest(){
		//System.out.println(CustomerTest.class.newInstance());
		Class<?> cls;
		System.out.println(Thread.currentThread().getContextClassLoader().getResource("ligth"));
	}
}

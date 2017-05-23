package light.tmp2.controller;

import java.util.List;

import com.light.framework.bean.View;
import com.ligth.framework.annotation.Action;
import com.ligth.framework.annotation.Controller;
import com.ligth.framework.annotation.Inject;

import light.tmp2.model.Customer;
import light.tmp2.service.CustomerService;

@Controller
public class CustomerController {
	
	@Inject
	private CustomerService customerService;
	
	@Action("GET:/customer")
	public View index() {
		List<Customer> customerList = customerService.getCustomerList();
		System.out.println(customerList.size());
		return new View("customer.jsp").addModel("customerList", customerList);
	}
}

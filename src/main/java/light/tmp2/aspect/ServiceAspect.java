package light.tmp2.aspect;

import com.light.framework.proxy.AspectProxy;
import com.light.framework.proxy.ProxyChain;
import com.ligth.framework.annotation.Aspect;
import com.ligth.framework.annotation.Service;
@Aspect(Service.class)
public class ServiceAspect extends AspectProxy {

	@Override
	public void begin() {
		System.out.println("ServiceAspect切面 ************ Service被调用");
	}
	
	@Override
	public Object doProxy(ProxyChain proxyChain) throws Throwable {
		Object obj = super.doProxy(proxyChain);
		System.out.println("ServiceAspect切面返回值 ************ " + obj);
		return obj;
	}
}
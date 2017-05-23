该项目是一个轻量级MVC框架，重点实现了依赖注入和AOP特性，适合需要学习这两方面知识的人

依赖注入的实现：通过BeanHelper(位于Helper包中)获取所有Bean Map（是一个记录了类与对象的映射关系的Map<Class<?>,Object>结构），遍历这个映射关系，取出Bean类与Bean实例，通过反射获取类中的所有成员变量，然后遍历这些变量，判断是否带有Inject注解，有的话从Map中取出Bean实例，通过Field.set方法来修改当前成员变量的值

Aop的实现：使用动态代理来实现具有局限性：（1）代理类必须实现一个接口（2）反射大量生成类文件可能导致方法区触发Full GC。于是采用Cglib来实现
因为一个类可以被多重代理，（安全验证，时间计算等），采用了代理链的思路

项目流程：
（1）DispatcherServlet的init方法实例化ClassHelper.class,BeanHelper.class,AopHelper.class,IocHelper.class,ControllerHelper.class
（2）ClassHelper类：用于获取应用包名下的类（所有类，Service类，Controller类，其他的带有某注解的类）
（3）BeanHelper类：主要用于存放Bean类与Bean实例的映射关系
（4）AopHelper类：切面辅助类，用于初始化AOP框架
（5）IocHelper类：依赖注入类，通过遍历BeanMap，找出Field中带有Inject注解的成员变量，然后通过反射进行初始化
（6）ControllerHelper类：用于存放请求和处理器的映射关系

项目优化：
（1）使用ThreadLocal来存放JDBC Connection
（2）增加事务控制特性

TODO：
（1）增加Shiro
（2）增加Web服务框架（计划使用CXF）
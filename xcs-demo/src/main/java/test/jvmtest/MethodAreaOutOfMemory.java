//package test.jvmtest;
//
//import java.lang.reflect.Method;
//
//import org.aopalliance.intercept.MethodInterceptor;
//import org.springframework.cglib.proxy.Enhancer;
//import org.springframework.cglib.proxy.MethodProxy;
//
//import junit.framework.TestCase;
//
//public class MethodAreaOutOfMemory {
//
//    
//
//    /**
//
//     * @param args
//
//     * @Author YHJ create at 2011-11-12 下午08:47:51
//
//     */
//
//    public static void main(String[] args) {
//
//       while(true){
//
//           Enhancer enhancer = new Enhancer();
//
//           enhancer.setSuperclass(TestCase.class);
//
//           enhancer.setUseCache(false);
//
//           enhancer.setCallback(new MethodInterceptor() {
//
//              @Override
//
//              public Object intercept(Object arg0, Method arg1, Object[] arg2,
//
//                     MethodProxy arg3) throws Throwable {
//
//                  return arg3.invokeSuper(arg0, arg2);
//
//              }
//
//           });
//
//           enhancer.create();
//
//       }
//
//    }
//
// 
//
//}

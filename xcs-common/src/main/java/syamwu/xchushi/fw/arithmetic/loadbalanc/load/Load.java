package syamwu.xchushi.fw.arithmetic.loadbalanc.load;

/**
 * 负载算法接口
 * 
 * @author: syam_wu
 * @date: 2018
 */
public interface Load<T> {

    /**
     * 返回负载权值比
     * 
     * @return
     */
    int[] load();

}

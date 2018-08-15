package syamwu.logtranslate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import syamwu.logtranslate.entity.PageInfo;

public interface BaseDao<T> {

    /**
     * 单表查询
     *
     * @param wheres       查询条件
     * @param selects      需要返回的列，如果返回全部传null
     * @param orderColumns 需要排序的列
     * @param isAsc        是否升序排序
     * @param pageInfo     分页（只需要传入pageSize和 pageNum就可以实现分页）
     * @return T
     */
    List<T> select(@Param("wheres") T wheres, @Param("selects") List<String> selects, @Param("orderColumns") List<String> orderColumns, @Param("isAsc") Boolean isAsc, @Param("pageInfo") PageInfo pageInfo);

    List<T> select(@Param("wheres") T wheres, @Param("selects") List<String> selects, @Param("orderColumns") List<String> orderColumns, @Param("isAsc") Boolean isAsc);

    List<T> select(@Param("wheres") T wheres, @Param("orderColumns") List<String> orderColumns, @Param("isAsc") Boolean isAsc);

    List<T> select(@Param("wheres") T wheres, @Param("orderColumns") List<String> orderColumns, @Param("isAsc") Boolean isAsc, @Param("pageInfo") PageInfo pageInfo);

    List<T> select(@Param("wheres") T wheres, @Param("pageInfo") PageInfo pageInfo);

    List<T> select(@Param("wheres") T wheres, @Param("selects") List<String> selects);

    List<T> select(@Param("wheres") T wheres);

    List<T> select();

    /**
     * 根据主键，返回对应的信息
     *
     * @param id
     * @return
     */
    T selectById(String id);

    /**
     * 根据主键集合，返回对应的列表
     *
     * @param ids
     * @return
     */
    List<T> selectByIds(List<String> ids);

    /**
     * 返回该条件下所有的记录数
     *
     * @param wheres 查询条件
     * @return
     */
    long selectCount(@Param("wheres")
                             T wheres);

    /**
     * @return
     */
    long selectCount();

    /**
     * 新增笔数
     *
     * @param data T结构
     */
    void insert(T data);

    /**
     * 按主键ID更新记录(只要结构不为空即可更新)
     *
     * @param data T结构
     */
    void update(T data);

    /**
     * 按住建ID删除记录
     *
     * @param data
     */
    void delete(T data);

    /**
     * 按主键的IDS删除
     *
     * @param list
     */
    void deleteByIds(List<String> list);

    /**
     * 批量插入
     *
     * @param list
     */
    void batchInsert(List<T> list);
    
}

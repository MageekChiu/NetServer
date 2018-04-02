package cn.mageek.NetServer.model.mapper;

import cn.mageek.NetServer.model.pojo.History;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * @author Mageek Chiu
 * @date 2018/4/2 0002:12:10
 */
public interface  HistoryMapper {

    @Select("SELECT * FROM history WHERE id = #{id}")
    History getById(int id);

    @Insert("insert into history (id,signal,power) values(#{id},#{signal},#{power})")
    void insert(History history);

}

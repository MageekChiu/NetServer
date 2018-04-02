package cn.mageek.NetServer.model.mapper;

import cn.mageek.NetServer.model.pojo.History;
import cn.mageek.NetServer.model.pojo.Info;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author Mageek Chiu
 * @date 2018/4/2 0002:12:10
 */
public interface InfoMapper {

    @Select("SELECT * FROM info WHERE id = #{id}")
    Info getById(int id);

    @Update("update info set `signal` = #{signal},createTime = #{createTime} WHERE mac = #{mac}")
    void  updateByMac(Info info);
}

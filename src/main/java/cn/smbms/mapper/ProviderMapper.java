package cn.smbms.mapper;

import cn.smbms.pojo.Provider;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProviderMapper {
    /**
     * 添加供应商
     * @param provider
     * @return
     */
    Integer addProvider(Provider provider);

    /**
     * 根据id删除供应商
     * @param id
     * @return
     */
    Integer deleteProviderById(String id);

    /**
     * 根据id修改供应商
     * @param provider
     * @return
     */
    Integer updateProviderById(Provider provider);

    /**
     * 根据id获取供应商
     * @param id
     * @return
     */
    Provider getProviderById(String id);

    /**
     * 根据参数获取供应商列表
     * @param proName
     * @param proCode
     * @return
     */
    List<Provider> getProviderList(@Param("proName") String proName, @Param("proCode") String proCode);
}

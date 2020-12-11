package com.iiot.business.mapper;

import java.util.List;
import com.iiot.business.domain.XtCustomer;

/**
 * 客户Mapper接口
 * 
 * @author desom
 * @date 2020-07-26
 */
public interface XtCustomerMapper 
{
    /**
     * 查询客户
     * 
     * @param id 客户ID
     * @return 客户
     */
    public XtCustomer selectXtCustomerById(Long id);

    /**
     * 查询客户列表
     * 
     * @param xtCustomer 客户
     * @return 客户集合
     */
    public List<XtCustomer> selectXtCustomerList(XtCustomer xtCustomer);

    /**
     * 新增客户
     * 
     * @param xtCustomer 客户
     * @return 结果
     */
    public int insertXtCustomer(XtCustomer xtCustomer);

    /**
     * 修改客户
     * 
     * @param xtCustomer 客户
     * @return 结果
     */
    public int updateXtCustomer(XtCustomer xtCustomer);

    /**
     * 删除客户
     * 
     * @param id 客户ID
     * @return 结果
     */
    public int deleteXtCustomerById(Long id);

    /**
     * 批量删除客户
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXtCustomerByIds(String[] ids);
}

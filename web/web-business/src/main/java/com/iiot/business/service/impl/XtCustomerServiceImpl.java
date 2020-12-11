package com.iiot.business.service.impl;

import java.util.List;
import com.iiot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iiot.business.mapper.XtCustomerMapper;
import com.iiot.business.domain.XtCustomer;
import com.iiot.business.service.IXtCustomerService;
import com.iiot.common.core.text.Convert;

/**
 * 客户Service业务层处理
 * 
 * @author desom
 * @date 2020-07-26
 */
@Service
public class XtCustomerServiceImpl implements IXtCustomerService 
{
    @Autowired
    private XtCustomerMapper xtCustomerMapper;

    /**
     * 查询客户
     * 
     * @param id 客户ID
     * @return 客户
     */
    @Override
    public XtCustomer selectXtCustomerById(Long id)
    {
        return xtCustomerMapper.selectXtCustomerById(id);
    }

    /**
     * 查询客户列表
     * 
     * @param xtCustomer 客户
     * @return 客户
     */
    @Override
    public List<XtCustomer> selectXtCustomerList(XtCustomer xtCustomer)
    {
        return xtCustomerMapper.selectXtCustomerList(xtCustomer);
    }

    /**
     * 新增客户
     * 
     * @param xtCustomer 客户
     * @return 结果
     */
    @Override
    public int insertXtCustomer(XtCustomer xtCustomer)
    {
        xtCustomer.setCreateTime(DateUtils.getNowDate());
        return xtCustomerMapper.insertXtCustomer(xtCustomer);
    }

    /**
     * 修改客户
     * 
     * @param xtCustomer 客户
     * @return 结果
     */
    @Override
    public int updateXtCustomer(XtCustomer xtCustomer)
    {
        xtCustomer.setUpdateTime(DateUtils.getNowDate());
        return xtCustomerMapper.updateXtCustomer(xtCustomer);
    }

    /**
     * 删除客户对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXtCustomerByIds(String ids)
    {
        return xtCustomerMapper.deleteXtCustomerByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除客户信息
     * 
     * @param id 客户ID
     * @return 结果
     */
    @Override
    public int deleteXtCustomerById(Long id)
    {
        return xtCustomerMapper.deleteXtCustomerById(id);
    }
}

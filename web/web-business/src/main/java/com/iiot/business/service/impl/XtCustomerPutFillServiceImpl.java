package com.iiot.business.service.impl;

import java.util.List;
import com.iiot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iiot.business.mapper.XtCustomerPutFillMapper;
import com.iiot.business.domain.XtCustomerPutFill;
import com.iiot.business.service.IXtCustomerPutFillService;
import com.iiot.common.core.text.Convert;

/**
 * 客户打印小票Service业务层处理
 * 
 * @author desom
 * @date 2020-07-26
 */
@Service
public class XtCustomerPutFillServiceImpl implements IXtCustomerPutFillService 
{
    @Autowired
    private XtCustomerPutFillMapper xtCustomerPutFillMapper;

    /**
     * 查询客户打印小票
     * 
     * @param id 客户打印小票ID
     * @return 客户打印小票
     */
    @Override
    public XtCustomerPutFill selectXtCustomerPutFillById(Long id)
    {
        return xtCustomerPutFillMapper.selectXtCustomerPutFillById(id);
    }

    /**
     * 查询客户打印小票列表
     * 
     * @param xtCustomerPutFill 客户打印小票
     * @return 客户打印小票
     */
    @Override
    public List<XtCustomerPutFill> selectXtCustomerPutFillList(XtCustomerPutFill xtCustomerPutFill)
    {
        return xtCustomerPutFillMapper.selectXtCustomerPutFillList(xtCustomerPutFill);
    }

    /**
     * 新增客户打印小票
     * 
     * @param xtCustomerPutFill 客户打印小票
     * @return 结果
     */
    @Override
    public int insertXtCustomerPutFill(XtCustomerPutFill xtCustomerPutFill)
    {
        xtCustomerPutFill.setCreateTime(DateUtils.getNowDate());
        return xtCustomerPutFillMapper.insertXtCustomerPutFill(xtCustomerPutFill);
    }

    /**
     * 修改客户打印小票
     * 
     * @param xtCustomerPutFill 客户打印小票
     * @return 结果
     */
    @Override
    public int updateXtCustomerPutFill(XtCustomerPutFill xtCustomerPutFill)
    {
        xtCustomerPutFill.setUpdateTime(DateUtils.getNowDate());
        return xtCustomerPutFillMapper.updateXtCustomerPutFill(xtCustomerPutFill);
    }

    /**
     * 删除客户打印小票对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXtCustomerPutFillByIds(String ids)
    {
        return xtCustomerPutFillMapper.deleteXtCustomerPutFillByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除客户打印小票信息
     * 
     * @param id 客户打印小票ID
     * @return 结果
     */
    @Override
    public int deleteXtCustomerPutFillById(Long id)
    {
        return xtCustomerPutFillMapper.deleteXtCustomerPutFillById(id);
    }
}

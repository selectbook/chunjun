package com.iiot.business.service;

import java.util.List;
import com.iiot.business.domain.XtCustomerPutFill;

/**
 * 客户打印小票Service接口
 * 
 * @author desom
 * @date 2020-07-26
 */
public interface IXtCustomerPutFillService 
{
    /**
     * 查询客户打印小票
     * 
     * @param id 客户打印小票ID
     * @return 客户打印小票
     */
    public XtCustomerPutFill selectXtCustomerPutFillById(Long id);

    /**
     * 查询客户打印小票列表
     * 
     * @param xtCustomerPutFill 客户打印小票
     * @return 客户打印小票集合
     */
    public List<XtCustomerPutFill> selectXtCustomerPutFillList(XtCustomerPutFill xtCustomerPutFill);

    /**
     * 新增客户打印小票
     * 
     * @param xtCustomerPutFill 客户打印小票
     * @return 结果
     */
    public int insertXtCustomerPutFill(XtCustomerPutFill xtCustomerPutFill);

    /**
     * 修改客户打印小票
     * 
     * @param xtCustomerPutFill 客户打印小票
     * @return 结果
     */
    public int updateXtCustomerPutFill(XtCustomerPutFill xtCustomerPutFill);

    /**
     * 批量删除客户打印小票
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXtCustomerPutFillByIds(String ids);

    /**
     * 删除客户打印小票信息
     * 
     * @param id 客户打印小票ID
     * @return 结果
     */
    public int deleteXtCustomerPutFillById(Long id);
}

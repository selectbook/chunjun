package com.iiot.business.mapper;

import java.util.List;
import com.iiot.business.domain.XtCustomerCar;

/**
 * 客户车型Mapper接口
 * 
 * @author desom
 * @date 2020-07-26
 */
public interface XtCustomerCarMapper 
{
    /**
     * 查询客户车型
     * 
     * @param id 客户车型ID
     * @return 客户车型
     */
    public XtCustomerCar selectXtCustomerCarById(Long id);

    /**
     * 查询客户车型列表
     * 
     * @param xtCustomerCar 客户车型
     * @return 客户车型集合
     */
    public List<XtCustomerCar> selectXtCustomerCarList(XtCustomerCar xtCustomerCar);

    /**
     * 新增客户车型
     * 
     * @param xtCustomerCar 客户车型
     * @return 结果
     */
    public int insertXtCustomerCar(XtCustomerCar xtCustomerCar);

    /**
     * 修改客户车型
     * 
     * @param xtCustomerCar 客户车型
     * @return 结果
     */
    public int updateXtCustomerCar(XtCustomerCar xtCustomerCar);

    /**
     * 删除客户车型
     * 
     * @param id 客户车型ID
     * @return 结果
     */
    public int deleteXtCustomerCarById(Long id);

    /**
     * 批量删除客户车型
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXtCustomerCarByIds(String[] ids);


    /**
     * 按客户id修改客户车型
     *
     * @param xtCustomerCar 客户车型
     * @return 结果
     */
    public int updateXtCustomerCarByCustomerId(XtCustomerCar xtCustomerCar);


    /**
     * 查询客户车型
     *
     * @param customerId 客户车型ID
     * @return 客户车型
     */
    public XtCustomerCar selectXtCustomerCarByCustomerId(Long customerId);


    /**
     * 删除客户车型
     *
     * @param customerIds 客户车型ID
     * @return 结果
     */
    public int deleteXtCustomerCarByCustomerIds(String[] customerIds);
}

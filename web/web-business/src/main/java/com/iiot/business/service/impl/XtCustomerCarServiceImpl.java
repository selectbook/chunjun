package com.iiot.business.service.impl;

import java.util.List;

import com.iiot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iiot.business.mapper.XtCustomerCarMapper;
import com.iiot.business.domain.XtCustomerCar;
import com.iiot.business.service.IXtCustomerCarService;
import com.iiot.common.core.text.Convert;

/**
 * 客户车型Service业务层处理
 *
 * @author desom
 * @date 2020-07-26
 */
@Service
public class XtCustomerCarServiceImpl implements IXtCustomerCarService {
    @Autowired
    private XtCustomerCarMapper xtCustomerCarMapper;

    /**
     * 查询客户车型
     *
     * @param id 客户车型ID
     * @return 客户车型
     */
    @Override
    public XtCustomerCar selectXtCustomerCarById(Long id) {
        return xtCustomerCarMapper.selectXtCustomerCarById(id);
    }

    /**
     * 查询客户车型列表
     *
     * @param xtCustomerCar 客户车型
     * @return 客户车型
     */
    @Override
    public List<XtCustomerCar> selectXtCustomerCarList(XtCustomerCar xtCustomerCar) {
        return xtCustomerCarMapper.selectXtCustomerCarList(xtCustomerCar);
    }

    /**
     * 新增客户车型
     *
     * @param xtCustomerCar 客户车型
     * @return 结果
     */
    @Override
    public int insertXtCustomerCar(XtCustomerCar xtCustomerCar) {
        xtCustomerCar.setCreateTime(DateUtils.getNowDate());
        return xtCustomerCarMapper.insertXtCustomerCar(xtCustomerCar);
    }

    /**
     * 修改客户车型
     *
     * @param xtCustomerCar 客户车型
     * @return 结果
     */
    @Override
    public int updateXtCustomerCar(XtCustomerCar xtCustomerCar) {
        xtCustomerCar.setUpdateTime(DateUtils.getNowDate());
        return xtCustomerCarMapper.updateXtCustomerCar(xtCustomerCar);
    }

    /**
     * 删除客户车型对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXtCustomerCarByIds(String ids) {
        return xtCustomerCarMapper.deleteXtCustomerCarByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除客户车型信息
     *
     * @param id 客户车型ID
     * @return 结果
     */
    @Override
    public int deleteXtCustomerCarById(Long id) {
        return xtCustomerCarMapper.deleteXtCustomerCarById(id);
    }


    /**
     * 按客户id修改客户车型
     *
     * @param xtCustomerCar 客户车型
     * @return 结果
     */
    @Override
    public int updateXtCustomerCarByCustomerId(XtCustomerCar xtCustomerCar) {
        return xtCustomerCarMapper.updateXtCustomerCarByCustomerId(xtCustomerCar);
    }


    /**
     * 查询客户车型
     *
     * @param customerId 客户车型ID
     * @return 客户车型
     */
    @Override
    public XtCustomerCar selectXtCustomerCarByCustomerId(Long customerId) {
        return xtCustomerCarMapper.selectXtCustomerCarByCustomerId(customerId);
    }

    /**
     * 删除客户车型
     *
     * @param customerId 客户车型ID
     * @return 结果
     */
    @Override
    public int deleteXtCustomerCarByCustomerIds(String customerId) {
        return xtCustomerCarMapper.deleteXtCustomerCarByCustomerIds(Convert.toStrArray(customerId));
    }
}

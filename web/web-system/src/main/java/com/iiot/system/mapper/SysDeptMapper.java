package com.iiot.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.iiot.system.domain.SysDept;

/**
 * 部门管理 数据层
 * 
 * @author ruoyi
 */
public interface SysDeptMapper
{
    /**
     * 查询部门人数
     * 
     * @param dept 部门信息
     * @return 结果
     */
    public int selectDeptCount(SysDept dept);

    /**
     * 查询部门是否存在用户
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    public int checkDeptExistUser(Long deptId);

    /**
     * 查询部门管理数据
     * 
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<SysDept> selectDeptList(SysDept dept);

    /**
     * 删除部门管理信息
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    public int deleteDeptById(Long deptId);

    /**
     * 新增部门信息
     * 
     * @param dept 部门信息
     * @return 结果
     */
    public int insertDept(SysDept dept);

    /**
     * 修改部门信息
     * 
     * @param dept 部门信息
     * @return 结果
     */
    public int updateDept(SysDept dept);

    /**
     * 修改子元素关系
     * 
     * @param depts 子元素
     * @return 结果
     */
    public int updateDeptChildren(@Param("depts") List<SysDept> depts);

    /**
     * 根据部门ID查询信息
     * 
     * @param deptId 部门ID
     * @return 部门信息
     */
    public SysDept selectDeptById(Long deptId);

    /**
     * 校验部门名称是否唯一
     * 
     * @param deptName 部门名称
     * @param parentId 父部门ID
     * @return 结果
     */
    public SysDept checkDeptNameUnique(@Param("deptName") String deptName, @Param("parentId") Long parentId);

    /**
     * 根据角色ID查询部门
     *
     * @param roleId 角色ID
     * @return 部门列表
     */
    public List<String> selectRoleDeptTree(Long roleId);

    /**
     * 修改所在部门的父级部门状态
     * 
     * @param dept 部门
     */
    public void updateDeptStatus(SysDept dept);

    /**
     * 根据ID查询所有子部门
     * @param deptId 部门ID
     * @return 部门列表
     */
    public List<SysDept> selectChildrenDeptById(Long deptId);


    /**
     * 根据部门编号查询所有部门信息
     * @param deptId 部门编号
     * @return 部门列表
     */
    public SysDept selectDeptByDeptId(Long deptId);

    /**
     * 查询部门信息
     * @param dept 部门信息
     * @return
     */
    public List<SysDept> selectDeptByDeptIds(SysDept dept);


    /**
     * 根据部门id统计部门设备个数,将下属部门个数汇总于一级部门
     * @param deptId
     * @return
     */
    public Integer selectXtDeviceCountByDeptId(@Param("deptId") Long deptId);


    /**
     * 查询出部门信息
     * @param sysDept
     * @return
     */
    public List<SysDept> selectDepts(SysDept sysDept);

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<SysDept> selectDeptListAll(SysDept dept);

    /**
     * 根据部门id统计部门设备个数,将下属部门个数汇总于一级部门
     * @param deptId
     * @return
     */
    public Integer selectXtOilDrumCountByDeptId(@Param("deptId") Long deptId);


}

package cn.smbms.mapper;

import cn.smbms.pojo.Bill;

import java.util.List;

public interface BillMapper {
    /**
     * 添加订单
     * @param bill
     * @return
     */
    Integer addBill(Bill bill);

    /**
     * 根据id删除订单
     * @param id
     * @return
     */
    Integer deleteBillById(String id);

    /**
     * 根据id修改订单
     * @param bill
     * @return
     */
    Integer updateBillById(Bill bill);

    /**
     * 根据id获取订单
     * @param id
     * @return
     */
    Bill getBillById(String id);

    /**
     * 根据供应商id获取订单总数
     * @param providerId
     * @return
     */
    Integer getBillCountByProviderId(String providerId);

    /**
     * 根据参数获取订单列表
     * @param bill
     * @return
     */
    List<Bill> getBillList(Bill bill);
}

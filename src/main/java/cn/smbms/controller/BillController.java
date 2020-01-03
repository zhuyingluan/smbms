package cn.smbms.controller;

import cn.smbms.pojo.Bill;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.bill.BillService;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.util.Constants;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class BillController {
    @Resource
    private BillService billService;
    @Resource
    private ProviderService providerService;

    @RequestMapping(path = "/billList")
    public String billList(HttpServletRequest request,String queryProductName, String queryProviderId, String queryIsPayment) {
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList("","");
        request.setAttribute("providerList", providerList);
        if(StringUtils.isNullOrEmpty(queryProductName)){
            queryProductName = "";
        }
        List<Bill> billList = new ArrayList<Bill>();
        Bill bill = new Bill();
        if(StringUtils.isNullOrEmpty(queryIsPayment)){
            bill.setIsPayment(0);
        }else{
            bill.setIsPayment(Integer.parseInt(queryIsPayment));
        }

        if(StringUtils.isNullOrEmpty(queryProviderId)){
            bill.setProviderId(0);
        }else{
            bill.setProviderId(Integer.parseInt(queryProviderId));
        }
        bill.setProductName(queryProductName);
        billList = billService.getBillList(bill);
        request.setAttribute("billList", billList);
        request.setAttribute("queryProductName", queryProductName);
        request.setAttribute("queryProviderId", queryProviderId);
        request.setAttribute("queryIsPayment", queryIsPayment);
        return "billlist";
    }

    @RequestMapping(path = "/addBill")
    public String addBill(){
        return "billadd";
    }

    @RequestMapping(path = "/doAddBill")
    public String doAddBill(HttpServletRequest request, Bill bill){
        bill.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        bill.setCreationDate(new Date());
        boolean flag = false;
        flag = billService.add(bill);
        if(flag){
            return "redirect:/billList";
        }else{
            return "billadd";
        }
    }

    @RequestMapping(path = "/billView/{id}")
    public String billView(HttpServletRequest request, @PathVariable String id){
        if(!StringUtils.isNullOrEmpty(id)){
            Bill bill = null;
            bill = billService.getBillById(id);
            request.setAttribute("bill", bill);
            return "billview";
        }
        return "redirect:/billList";
    }

    @RequestMapping(path = "/updateBill/{id}")
    public String updateBill(HttpServletRequest request, @PathVariable String id) {
        if(!StringUtils.isNullOrEmpty(id)){
            Bill bill = null;
            bill = billService.getBillById(id);
            request.setAttribute("bill", bill);
            return "billmodify";
        }
        return "redirect:/billList";
    }

    @RequestMapping(path = "/doUpdateBill")
    public String doUpdateBill(HttpServletRequest request, Bill bill) {
        bill.setModifyBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        bill.setModifyDate(new Date());
        boolean flag = false;
        flag = billService.modify(bill);
        if(flag){
            return "redirect:/billList";
        }else{
            return "billmodify";
        }
    }

    @ResponseBody
    @RequestMapping(path = "/deleteBill")
    public String deleteBill(String billid){
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(!StringUtils.isNullOrEmpty(billid)){
            boolean flag = billService.deleteBillById(billid);
            if(flag){//删除成功
                resultMap.put("delResult", "true");
            }else{//删除失败
                resultMap.put("delResult", "false");
            }
        }else{
            resultMap.put("delResult", "notexit");
        }
        return JSONArray.toJSONString(resultMap);
    }
}

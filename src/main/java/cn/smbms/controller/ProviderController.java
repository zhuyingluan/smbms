package cn.smbms.controller;

import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.util.Constants;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
public class ProviderController {
    @Resource
    private ProviderService providerService;

    @RequestMapping(path = "/providerList")
    public String providerList(HttpServletRequest request, String queryProName, String queryProCode){
        if(StringUtils.isNullOrEmpty(queryProName)){
            queryProName = "";
        }
        if(StringUtils.isNullOrEmpty(queryProCode)){
            queryProCode = "";
        }
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList(queryProName,queryProCode);
        request.setAttribute("providerList", providerList);
        request.setAttribute("queryProName", queryProName);
        request.setAttribute("queryProCode", queryProCode);
        return "providerlist";
    }

    @ResponseBody
    @RequestMapping(path = "/ajaxProviderList")
    public String ajaxProviderList(){
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList("","");
        return JSONArray.toJSONString(providerList);
    }

    @RequestMapping("/addProvider")
    public String addProvider(){
        return "provideradd";
    }

    @RequestMapping(path = "/doAddProvider")
    public String doAddProvider(HttpSession session, Provider provider){
        provider.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        provider.setCreationDate(new Date());
        boolean flag = false;
        flag = providerService.add(provider);
        if(flag){
            return "redirect:/providerList";
        }else{
            return "provideradd";
        }
    }

    @RequestMapping(path = "/providerView/{id}")
    public String providerView(HttpServletRequest request, @PathVariable String id){
        if(!StringUtils.isNullOrEmpty(id)){
            Provider provider = null;
            provider = providerService.getProviderById(id);
            request.setAttribute("provider", provider);
            return "providerview";
        }
        return "redirect:/providerList";
    }

    @RequestMapping(path = "/updateProvider/{id}")
    public String updateProvider(HttpServletRequest request, @PathVariable String id){
        if(!StringUtils.isNullOrEmpty(id)){
            Provider provider = null;
            provider = providerService.getProviderById(id);
            request.setAttribute("provider", provider);
            return "providermodify";
        }
        return "redirect:/providerList";
    }

    @RequestMapping(path = "/doUpdateProvider")
    public String doUpdateProvider(HttpSession session, Provider provider){
        provider.setModifyBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        provider.setModifyDate(new Date());
        boolean flag = false;
        flag = providerService.modify(provider);
        System.err.println(provider.getId());
        if(flag){
            return "redirect:/providerList";
        }else{
            return "providermodify";
        }
    }

    @ResponseBody
    @RequestMapping(path = "/deleteProvider")
    public String deleteProvider(String proid){
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(!StringUtils.isNullOrEmpty(proid)){
            int flag = providerService.deleteProviderById(proid);
            if(flag == 0){//删除成功
                resultMap.put("delResult", "true");
            }else if(flag == -1){//删除失败
                resultMap.put("delResult", "false");
            }else if(flag > 0){//该供应商下有订单，不能删除，返回订单数
                resultMap.put("delResult", String.valueOf(flag));
            }
        }else{
            resultMap.put("delResult", "notexit");
        }
        return JSONArray.toJSONString(resultMap);
    }
}

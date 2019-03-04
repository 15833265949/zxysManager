package com.fh.controller.patient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.service.patient.PatientOrderQuantityService;
import com.fh.util.Const;
import com.fh.util.IsNull;
import com.fh.util.Jurisdiction;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;

/**
 * Controller类-统计患者下订单数
 * 注：前端页面的开始、结束日期初始值，由页面加载前的查询方法返回值赋予
 * @author 陈彦林
 * @date 2018-08-02
 * 注释者：陈彦林
 * 开发者：陈彦林
 */
@Controller
@RequestMapping("patientOrderQuantity")
public class PatientOrderQuantityController extends BaseController {
	
	//菜单地址(权限用)
	String menuUrl = "patientOrderQuantity/mainList.do";
	//自动获取service对象
	@Resource(name="patientOrderQuantityService")
	private PatientOrderQuantityService patientOrderQuantityService;
	
	/**
	 * 汇总查询患者下订单数，查询条件：开始日期，结束日期，患者姓名，患者手机号,前端每页可以显示的数据条数
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping(value="/mainList")
	public ModelAndView mainList(Page page) {
		logBefore(logger, "fun in PatientOrderQuantityController.mainList()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_main");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		//开始日期,条件查询
		String startDate = pd.getString("startDate");
		//结束日期,条件查询
		String endDate = pd.getString("endDate");
		//患者姓名或手机号,条件查询
		String trueNameOrPhone = pd.getString("trueNameOrPhone");
		//对接收的前端参数，进行判空；如果所有参数都为空，默认日期为当前系统日期
		if(!IsNull.paramsIsNull(startDate)){
			pd.put("startDate", startDate.trim());
		}
		if(!IsNull.paramsIsNull(endDate)){
			pd.put("endDate", endDate.trim());
		}
		if(!IsNull.paramsIsNull(trueNameOrPhone)){
			pd.put("trueNameOrPhone", trueNameOrPhone.trim());
		}
		if(IsNull.paramsIsNull(startDate) && 
		   IsNull.paramsIsNull(endDate) && 
		   IsNull.paramsIsNull(trueNameOrPhone)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				pd.put("startDate", sdf.format(new Date()));
				pd.put("endDate", sdf.format(new Date()));
		}
		//如果
		page.setPd(pd);
		//用于接收从数据库查询出来的数据
		List<PageData> varList;
		PageData pageData;
		try {
			//将数据库表内符合条件的数据，查询出来
			varList = patientOrderQuantityService.listMain(page);
			//将'订单总数，成交总数，总成交率',从数据库表内查询计算出来
			pageData = patientOrderQuantityService.selectTotal(pd);
			System.out.println("--------varList.size():"+varList.size());
			System.out.println("--------pageData:"+pageData);
			//将查询出来的数据，返给前端
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_main");
			mv.addObject("list", varList);
			mv.addObject("pageData", pageData);
			mv.addObject("pd", pd);
			mv.addObject("msg", "查询成功！");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_main");
			return mv;
		}
		logBefore(logger, "fun end PatientOrderQuantityController.mainList()");
		return mv;
	}
	/**
	 * 查询患者已支付订单明细，查询条件：患者ID，开始日期，结束日期，患者姓名，患者手机号,前端每页可以显示的数据条数
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping(value="/detailList")
	public ModelAndView detailList(Page page) {
		logBefore(logger, "fun in PatientOrderQuantityController.detailList()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_detail");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		//患者ID
		String patientId = pd.getString("patientId");
		//开始日期,条件查询
		String startDate = pd.getString("startDate");
		//结束日期,条件查询
		String endDate = pd.getString("endDate");
		//患者姓名或手机号,条件查询
		String trueNameOrPhone = pd.getString("trueNameOrPhone");
		//对接收的前端参数，进行判空；如果所有参数都为空，默认日期为当前系统日期
		if(!IsNull.paramsIsNull(patientId)){	
			System.out.println("---------patientId:"+patientId);
			pd.put("patientId", patientId.trim());
		} else {
			mv.addObject("error","未接收到患者ID");
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_detail");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		if(!IsNull.paramsIsNull(startDate)){	
			System.out.println("---------startDate:"+startDate);
			pd.put("startDate", startDate.trim());
		}
		if(!IsNull.paramsIsNull(endDate)){
			System.out.println("---------endDate:"+endDate);
			pd.put("endDate", endDate.trim());
		}
		if(!IsNull.paramsIsNull(trueNameOrPhone)){
			System.out.println("---------trueNameOrPhone:"+trueNameOrPhone);
			pd.put("trueNameOrPhone", trueNameOrPhone.trim());
		}
		if(IsNull.paramsIsNull(startDate) && 
		   IsNull.paramsIsNull(endDate) && 
		   IsNull.paramsIsNull(trueNameOrPhone)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				pd.put("startDate", sdf.format(new Date()));
				pd.put("endDate", sdf.format(new Date()));
		}
		//如果
		page.setPd(pd);
		//用于接收从数据库查询出来的数据
		List<PageData> varList;
		try {
			//将数据库表内符合条件的数据，查询出来
			varList = patientOrderQuantityService.listDetail(page);
			System.out.println("--------varList.size():"+varList.size());
			//将查询出来的数据，返给前端
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_detail");
			mv.addObject("list", varList);
			mv.addObject("pd", pd);
			mv.addObject("msg", "查询成功！");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			mv.addObject("error",e.getMessage());
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_detail");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		logBefore(logger, "fun end PatientOrderQuantityController.detailList()");
		return mv;
	}
	
	/**
	 * 导出到excel:汇总查询患者下订单数，查询条件：开始日期，结束日期，患者姓名，患者手机号
	 * @return ModelAndView
	 */
	@RequestMapping(value="/excelMain")
	public ModelAndView exportExcelMain(){
		logBefore(logger, "fun in PatientOrderQuantityController.exportExcelMain()");
		ModelAndView mv = new ModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_main");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		PageData pd = new PageData();
		pd = this.getPageData();
		//开始日期,条件查询
		String startDate = pd.getString("startDate");
		//结束日期,条件查询
		String endDate = pd.getString("endDate");
		//患者姓名或手机号,条件查询
		String trueNameOrPhone = pd.getString("trueNameOrPhone");
		//对接收的前端参数，进行判空；如果所有参数都为空，默认日期为当前系统日期
		if(!IsNull.paramsIsNull(startDate)){
			pd.put("startDate", startDate.trim());
		}
		if(!IsNull.paramsIsNull(endDate)){
			pd.put("endDate", endDate.trim());
		}
		if(!IsNull.paramsIsNull(trueNameOrPhone)){
			pd.put("trueNameOrPhone", trueNameOrPhone.trim());
		}
		if(IsNull.paramsIsNull(startDate) && 
		   IsNull.paramsIsNull(endDate) && 
		   IsNull.paramsIsNull(trueNameOrPhone)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				pd.put("startDate", sdf.format(new Date()));
				pd.put("endDate", sdf.format(new Date()));
		}
		//用于接收导出的数据
		Map<String,Object> dataMap = new HashMap<String,Object>();
		//存储表头列名
		List<String> titles = new ArrayList<String>();
		titles.add("患者姓名");	//1
		titles.add("订单总数");	//2
		titles.add("成交数");//3
		titles.add("成交率");	//4
		titles.add("订单总金额（元）");	//5
		titles.add("手机号");	//6
		dataMap.put("titles", titles);
		//接收从数据库查询出来的数据
		List<PageData> varOList;
		//存储导出所需数据
		List<PageData> varList;
		try{
			//将数据库表内符合条件的数据，查询出来
			varOList = patientOrderQuantityService.listAllMain(pd);
			System.out.println("--------varOList.size():"+varOList.size());
			varList = new ArrayList<PageData>();
			for(int i=0;i<varOList.size();i++){
				PageData vpd = new PageData();
				if (IsNull.paramsIsNull(varOList.get(i).get("trueName"))){
					vpd.put("var1", "");	//1
				} else {
					vpd.put("var1", varOList.get(i).get("trueName").toString());	//1					
				}
				if (IsNull.paramsIsNull(varOList.get(i).get("orderNum"))){
					vpd.put("var2", "0");//2
				} else {
					vpd.put("var2", varOList.get(i).get("orderNum").toString());//2					
				}
				if (IsNull.paramsIsNull(varOList.get(i).get("succeNum"))){
					vpd.put("var3", "");//3
				} else {
					vpd.put("var3", varOList.get(i).get("succeNum").toString());//3					
				}
				if (IsNull.paramsIsNull(varOList.get(i).get("succeRate"))){
					vpd.put("var4", "");//4
				} else {
					vpd.put("var4", varOList.get(i).get("succeRate").toString());//4					
				}
				if (IsNull.paramsIsNull(varOList.get(i).get("sumMoney"))){
					vpd.put("var5", "");//5
				} else {
					vpd.put("var5", varOList.get(i).get("sumMoney").toString());//5					
				}
				if (IsNull.paramsIsNull(varOList.get(i).get("phone"))){
					vpd.put("var6", "");//6
				} else {
					vpd.put("var6", varOList.get(i).get("phone").toString());//6					
				}
				varList.add(vpd);
			}
			dataMap.put("varList", varList);
			//导出到excel
			ObjectExcelView erv = new ObjectExcelView();
			mv = new ModelAndView(erv,dataMap);
			mv.addObject("msg", "成功导出到excel");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch(Exception e){
			logger.error(e.toString(), e);
			mv.addObject("error", e.getMessage());
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_main");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		logBefore(logger, "fun end PatientOrderQuantityController.exportExcelMain()");
		return mv;
	}
	
	/**
	 * 导出到excel:查询患者已支付订单明细,条件：患者ID,开始日期，结束日期，患者姓名，患者手机号
	 * @return ModelAndView
	 */
	@RequestMapping(value="/excelDetail")
	public ModelAndView exportExcelDetail(){
		logBefore(logger, "fun in PatientOrderQuantityController.exportExcelDetail()");
		ModelAndView mv = new ModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_detail");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		PageData pd = new PageData();
		pd = this.getPageData();
		//患者ID
		String patientId = pd.getString("patientId");
		//开始日期,条件查询
		String startDate = pd.getString("startDate");
		//结束日期,条件查询
		String endDate = pd.getString("endDate");
		//患者姓名或手机号,条件查询
		String trueNameOrPhone = pd.getString("trueNameOrPhone");
		//对接收的前端参数，进行判空；如果所有参数都为空，默认日期为当前系统日期
		if(!IsNull.paramsIsNull(patientId)){
			pd.put("patientId", patientId.trim());
		} else {
			mv.addObject("error","未接收到患者ID");
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_detail");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		if(!IsNull.paramsIsNull(startDate)){
			pd.put("startDate", startDate.trim());
		}
		if(!IsNull.paramsIsNull(endDate)){
			pd.put("endDate", endDate.trim());
		}
		if(!IsNull.paramsIsNull(trueNameOrPhone)){
			pd.put("trueNameOrPhone", trueNameOrPhone.trim());
		}
		if(IsNull.paramsIsNull(startDate) && 
		   IsNull.paramsIsNull(endDate) && 
		   IsNull.paramsIsNull(trueNameOrPhone)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				pd.put("startDate", sdf.format(new Date()));
				pd.put("endDate", sdf.format(new Date()));
		}
		//用于接收导出的数据
		Map<String,Object> dataMap = new HashMap<String,Object>();
		//存储表头列名
		List<String> titles = new ArrayList<String>();
		titles.add("患者姓名");	//1
		titles.add("订单项目");//2
		titles.add("订单金额（元）");//3
		titles.add("订单日期");//4
		titles.add("患者手机号");//5
		titles.add("接单医生");//6
		dataMap.put("titles", titles);
		//接收从数据库查询出来的数据
		List<PageData> varOList;
		//存储导出所需数据
		List<PageData> varList;
		try{
			//将数据库表内符合条件的数据，查询出来
			varOList = patientOrderQuantityService.listAllDetail(pd);
			System.out.println("--------varOList.size():"+varOList.size());
			varList = new ArrayList<PageData>();
			for(int i=0;i<varOList.size();i++){
				PageData vpd = new PageData();
				if (IsNull.paramsIsNull(varOList.get(i).get("patientName"))){
					vpd.put("var1", "");	//1
				}else{
					vpd.put("var1", varOList.get(i).get("patientName").toString());	//1				
				}
				if (IsNull.paramsIsNull(varOList.get(i).get("payStyle"))){
					vpd.put("var2", "");	//2
				}else if(varOList.get(i).get("payStyle").toString().equals("0")){
					vpd.put("var2", "赏金");		//2				
				}else if(varOList.get(i).get("payStyle").toString().equals("1")){
					vpd.put("var2", "处方");		//2					
				}else if(varOList.get(i).get("payStyle").toString().equals("2")){
					vpd.put("var2", "药品");		//2					
				}else if(varOList.get(i).get("payStyle").toString().equals("3")){
					vpd.put("var2", "转账");		//2					
				}else{
					mv.addObject("error", "下单项目：存在错误数据");
					mv.addObject(Const.SESSION_QX, this.getHC());
					mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_detail");					
					return mv;
				}
				if (IsNull.paramsIsNull(varOList.get(i).get("payMoney"))){
					vpd.put("var3", "0.00");	//3
				}else{
					vpd.put("var3", varOList.get(i).get("payMoney").toString());	//3				
				}
				if (IsNull.paramsIsNull(varOList.get(i).get("createTime"))){
					vpd.put("var4", "");	//4
				}else{
					vpd.put("var4", varOList.get(i).get("createTime").toString());	//4			
				}
				if (IsNull.paramsIsNull(varOList.get(i).get("phone"))){
					vpd.put("var5", "");	//5
				}else{
					vpd.put("var5", varOList.get(i).get("phone").toString());	//5		
				}
				if (IsNull.paramsIsNull(varOList.get(i).get("doctorName"))){
					vpd.put("var6", "");	//6
				}else{
					vpd.put("var6", varOList.get(i).get("doctorName").toString());	//6	
				}
				varList.add(vpd);
			}
			dataMap.put("varList", varList);
			//导出到excel
			ObjectExcelView erv = new ObjectExcelView();
			mv = new ModelAndView(erv,dataMap);
			mv.addObject("msg", "成功导出到excel");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch(Exception e){
			logger.error(e.toString(), e);
			mv.addObject("error", e.getMessage());
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_detail");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		logBefore(logger, "fun end PatientOrderQuantityController.exportExcelDetail()");
		return mv;
	}
	/**
	 * 查询患者所有订单明细，查询条件：患者ID，开始日期，结束日期，患者姓名，患者手机号,前端每页可以显示的数据条数
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping(value="/detailListAll")
	public ModelAndView detailListAll(Page page) {
		logBefore(logger, "fun in PatientOrderQuantityController.detailListAll()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_detail");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		//患者ID
		String patientId = pd.getString("patientId");
		//开始日期,条件查询
		String startDate = pd.getString("startDate");
		//结束日期,条件查询
		String endDate = pd.getString("endDate");
		//患者姓名或手机号,条件查询
		String trueNameOrPhone = pd.getString("trueNameOrPhone");
		//对接收的前端参数，进行判空；如果所有参数都为空，默认日期为当前系统日期
		if(!IsNull.paramsIsNull(patientId)){	
			System.out.println("---------patientId:"+patientId);
			pd.put("patientId", patientId.trim());
		} else {
			mv.addObject("error","未接收到患者ID");
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_detail");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		if(!IsNull.paramsIsNull(startDate)){	
			System.out.println("---------startDate:"+startDate);
			pd.put("startDate", startDate.trim());
		}
		if(!IsNull.paramsIsNull(endDate)){
			System.out.println("---------endDate:"+endDate);
			pd.put("endDate", endDate.trim());
		}
		if(!IsNull.paramsIsNull(trueNameOrPhone)){
			System.out.println("---------trueNameOrPhone:"+trueNameOrPhone);
			pd.put("trueNameOrPhone", trueNameOrPhone.trim());
		}
		if(IsNull.paramsIsNull(startDate) && 
		   IsNull.paramsIsNull(endDate) && 
		   IsNull.paramsIsNull(trueNameOrPhone)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				pd.put("startDate", sdf.format(new Date()));
				pd.put("endDate", sdf.format(new Date()));
		}
		//如果
		page.setPd(pd);
		//用于接收从数据库查询出来的数据
		List<PageData> varList;
		try {
			//将数据库表内符合条件的数据，查询出来
			varList = patientOrderQuantityService.listDetail(page);
			System.out.println("--------varList.size():"+varList.size());
			//将查询出来的数据，返给前端
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_detail");
			mv.addObject("list", varList);
			mv.addObject("pd", pd);
			mv.addObject("msg", "查询成功！");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			mv.addObject("error",e.getMessage());
			mv.setViewName("patient/patientOrderQuantity/patient_order_quantity_detail");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		logBefore(logger, "fun end PatientOrderQuantityController.detailListAll()");
		return mv;
	}
	
	/**
	 * 从session中,获取操作权限 QX
	 * @return Map<String, String>
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getHC() {
		Subject currentUser = SecurityUtils.getSubject(); // shiro管理的session
		Session session = currentUser.getSession();
		return (Map<String, String>) session.getAttribute(Const.SESSION_QX);
	}
}
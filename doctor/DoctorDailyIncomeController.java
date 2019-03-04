package com.fh.controller.doctor;

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
import com.fh.service.doctor.DoctorDailyIncomeService;
import com.fh.util.Const;
import com.fh.util.IsNull;
import com.fh.util.Jurisdiction;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;

/**
 * Controller类-统计医生每日收益情况
 * 注：前端页面的开始、结束日期初始值，由页面加载前的查询方法返回值赋予
 * @author 陈彦林
 * @date 2018-08-01
 * 注释者：陈彦林
 * 开发者：陈彦林
 */
@Controller
@RequestMapping("doctorDailyIncome")
//@RequestMapping(value="doctorDailyIncome")
public class DoctorDailyIncomeController extends BaseController {
	
	//菜单地址(权限用)
	String menuUrl = "doctorDailyIncome/mainList.do";
	//自动获取service对象
	@Resource(name="doctorDailyIncomeService")
	private DoctorDailyIncomeService doctorDailyIncomeService;
	
	/**
	 * 按收入来源，查询医生的总收益，查询条件：开始日期，结束日期，订单类型,前端每页可以显示的数据条数
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping(value="/mainListByItem")
	public ModelAndView mainListByItem(Page page) {
		logBefore(logger, "DoctorDailyIncomeController.mainListByItem()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/daily_income/doctor_daily_income_mainitem");
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
		//医生姓名或手机号,条件查询
		String payStyle = pd.getString("payStyle");
		System.out.println("--------payStyle: "+payStyle);
		
		//对接收的前端参数，进行判空；如果所有参数都为空，默认日期为当前系统日期
		if(!IsNull.paramsIsNull(startDate)){
			pd.put("startDate", startDate.trim());
		}
		if(!IsNull.paramsIsNull(endDate)){
			pd.put("endDate", endDate.trim());
		}
		if(!IsNull.paramsIsNull(payStyle)){
			pd.put("payStyle", payStyle.trim());
		}
		if(IsNull.paramsIsNull(startDate) && 
		   IsNull.paramsIsNull(endDate) && 
		   IsNull.paramsIsNull(payStyle)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				pd.put("startDate", sdf.format(new Date()));
				pd.put("endDate", sdf.format(new Date()));
		}
		//如果
		page.setPd(pd);
		System.out.println("--------pd:"+pd);
		System.out.println("--------page:"+page);
		//用于接收从数据库查询出来的数据
		List<PageData> varList;
		try {
			//将数据库表内符合条件的数据，查询出来
			varList = doctorDailyIncomeService.listMainByItem(page);
			//将查询出来的数据，返给前端
			mv.setViewName("doctor/daily_income/doctor_daily_income_mainitem");
			mv.addObject("list", varList);
			mv.addObject("pd", pd);
			mv.addObject("msg", "查询成功！");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/daily_income/doctor_daily_income_mainitem");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	/**
	 * 按收入来源，查询医生收益明细，查询条件：开始日期，结束日期，订单类型,前端每页可以显示的数据条数
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping(value="/detailListByItem")
	public ModelAndView detailListByItem(Page page) {
		logBefore(logger, "DoctorDailyIncomeController.detailListByItem()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/daily_income/doctor_daily_income_detailitem");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		//医生ID
		String payStyle = pd.getString("payStyle");
		//开始日期,条件查询
		String startDate = pd.getString("startDate");
		//结束日期,条件查询
		String endDate = pd.getString("endDate");
		//对接收的前端参数，进行判空；如果所有参数都为空，默认日期为当前系统日期
		if(!IsNull.paramsIsNull(payStyle)){
			pd.put("payStyle", Integer.parseInt(payStyle.trim()));
		}
		if(!IsNull.paramsIsNull(startDate)){
			pd.put("startDate", startDate.trim());
		}
		if(!IsNull.paramsIsNull(endDate)){
			pd.put("endDate", endDate.trim());
		}
		if(IsNull.paramsIsNull(startDate) && 
		   IsNull.paramsIsNull(endDate) && 
		   IsNull.paramsIsNull(payStyle)){
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
			varList = doctorDailyIncomeService.listDetailByItem(page);
			//将查询出来的数据，返给前端
			mv.setViewName("doctor/daily_income/doctor_daily_income_detailitem");
			mv.addObject("list", varList);
			mv.addObject("pd", pd);
			mv.addObject("msg", "查询成功！");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/daily_income/doctor_daily_income_detailitem");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 按医生，查询医生的总收益，查询条件：开始日期，结束日期，医生姓名，医生手机号,前端每页可以显示的数据条数
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping(value="/mainList")
	public ModelAndView mainList(Page page) {
		logBefore(logger, "DoctorDailyIncomeController.mainList()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/daily_income/doctor_daily_income_main");
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
		//医生姓名或手机号,条件查询
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
		try {
			//将数据库表内符合条件的数据，查询出来
			varList = doctorDailyIncomeService.listMain(page);
			//将查询出来的数据，返给前端
			mv.setViewName("doctor/daily_income/doctor_daily_income_main");
			mv.addObject("list", varList);
			mv.addObject("pd", pd);
			mv.addObject("msg", "查询成功！");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/daily_income/doctor_daily_income_main");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	/**
	 * 按医生，查询医生收益明细，查询条件：开始日期，结束日期，医生姓名，医生手机号,前端每页可以显示的数据条数
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping(value="/detailList")
	public ModelAndView detailList(Page page) {
		logBefore(logger, "DoctorDailyIncomeController.detailList()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/daily_income/doctor_daily_income_detail");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		//医生ID
		String doctorId = pd.getString("doctorId");
		//开始日期,条件查询
		String startDate = pd.getString("startDate");
		//结束日期,条件查询
		String endDate = pd.getString("endDate");
		//医生姓名或手机号,条件查询
		String trueNameOrPhone = pd.getString("trueNameOrPhone");
		//对接收的前端参数，进行判空；如果所有参数都为空，默认日期为当前系统日期
		if(!IsNull.paramsIsNull(doctorId)){
			pd.put("doctorId", doctorId.trim());
		} else {
			mv.addObject("error","未接收到医生ID");
			mv.setViewName("doctor/daily_income/doctor_daily_income_detail");
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
		//如果
		page.setPd(pd);
		//用于接收从数据库查询出来的数据
		List<PageData> varList;
		try {
			//将数据库表内符合条件的数据，查询出来
			varList = doctorDailyIncomeService.listDetail(page);
			//将查询出来的数据，返给前端
			mv.setViewName("doctor/daily_income/doctor_daily_income_detail");
			mv.addObject("list", varList);
			mv.addObject("pd", pd);
			mv.addObject("msg", "查询成功！");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/daily_income/doctor_daily_income_detail");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 导出到excel:按收入来源条件查询-医生总收益,条件：开始日期，结束日期，订单类型
	 * @return ModelAndView
	 */
	@RequestMapping(value="/excelMainByItem")
	public ModelAndView exportExcelMainByItem(){
		logBefore(logger, "DoctorDailyIncomeController.exportExcelMainByItem()");
		ModelAndView mv = new ModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/daily_income/doctor_daily_income_mainitem");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		PageData pd = new PageData();
		pd = this.getPageData();
		//开始日期,条件查询
		String startDate = pd.getString("startDate");
		//结束日期,条件查询
		String endDate = pd.getString("endDate");
		//医生姓名或手机号,条件查询
		String payStyle = pd.getString("payStyle");
		//对接收的前端参数，进行判空；如果所有参数都为空，默认日期为当前系统日期
		if(!IsNull.paramsIsNull(startDate)){
			pd.put("startDate", startDate.trim());
		}
		if(!IsNull.paramsIsNull(endDate)){
			pd.put("endDate", endDate.trim());
		}
		if(!IsNull.paramsIsNull(payStyle)){
			pd.put("trueNameOrPhone", payStyle.trim());
		}
		if(IsNull.paramsIsNull(startDate) && 
		   IsNull.paramsIsNull(endDate) && 
		   IsNull.paramsIsNull(payStyle)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				pd.put("startDate", sdf.format(new Date()));
				pd.put("endDate", sdf.format(new Date()));
		}
		//用于接收导出的数据
		Map<String,Object> dataMap = new HashMap<String,Object>();
		//存储表头列名
		List<String> titles = new ArrayList<String>();
		titles.add("收入来源");	//1
		titles.add("总数量");	//2
		titles.add("总收益（元）");	//3
		dataMap.put("titles", titles);
		//接收从数据库查询出来的数据
		List<PageData> varOList;
		//存储导出所需数据
		List<PageData> varList;
		try{
			//将数据库表内符合条件的数据，查询出来
			varOList = doctorDailyIncomeService.listAllMainByItem(pd);
			varList = new ArrayList<PageData>();
			for(int i=0;i<varOList.size();i++){
				PageData vpd = new PageData();
				if (!IsNull.paramsIsNull(varOList.get(i).get("payStyle")) && 
					varOList.get(i).get("payStyle").toString().equals("0")) {//1
					vpd.put("var1", "赏金");						
				}else if(!IsNull.paramsIsNull(varOList.get(i).get("payStyle")) && 
						varOList.get(i).get("payStyle").toString().equals("1")) {//1	
						vpd.put("var1", "处方");					
				}else if(!IsNull.paramsIsNull(varOList.get(i).get("payStyle")) && 
						varOList.get(i).get("payStyle").toString().equals("2")) {//1	
						vpd.put("var1", "药品");					
				}else if(!IsNull.paramsIsNull(varOList.get(i).get("payStyle")) && 
						varOList.get(i).get("payStyle").toString().equals("3")) {//1	
						vpd.put("var1", "转账");
				}else{
					mv.addObject("error", "收入来源：数据存在错误");
					mv.setViewName("doctor/daily_income/doctor_daily_income_detailitem");
					mv.addObject(Const.SESSION_QX, this.getHC());
					return mv;			
				}
				if(IsNull.paramsIsNull(varOList.get(i).get("sumNum"))){					
					vpd.put("var2", "0");	//2
				}else{					
					vpd.put("var2", varOList.get(i).get("sumNum").toString());	//2
				}
				if(IsNull.paramsIsNull(varOList.get(i).get("sumMoney"))){		
					vpd.put("var3", "0.00");	//3
				}else{
					vpd.put("var3", varOList.get(i).get("sumMoney").toString());	//3
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
			mv.setViewName("doctor/daily_income/doctor_daily_income_mainitem");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 导出到excel:按收入来源条件查询-医生每日收益明细,条件：开始日期，结束日期，订单类型
	 * @return ModelAndView
	 */
	@RequestMapping(value="/excelDetailByItem")
	public ModelAndView exportExcelDetailByItem(){
		logBefore(logger, "DoctorDailyIncomeController.exportExcelDetailByItem()");
		ModelAndView mv = new ModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/daily_income/doctor_daily_income_detailitem");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		PageData pd = new PageData();
		pd = this.getPageData();
		//开始日期,条件查询
		String startDate = pd.getString("startDate");
		//结束日期,条件查询
		String endDate = pd.getString("endDate");
		//医生姓名或手机号,条件查询
		String payStyle = pd.getString("payStyle");
		//对接收的前端参数，进行判空；如果所有参数都为空，默认日期为当前系统日期
		if(!IsNull.paramsIsNull(startDate)){
			pd.put("startDate", startDate.trim());
		}
		if(!IsNull.paramsIsNull(endDate)){
			pd.put("endDate", endDate.trim());
		}
		if(!IsNull.paramsIsNull(payStyle)){
			pd.put("payStyle", payStyle.trim());
		}
		if(IsNull.paramsIsNull(startDate) && 
		   IsNull.paramsIsNull(endDate) && 
		   IsNull.paramsIsNull(payStyle)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				pd.put("startDate", sdf.format(new Date()));
				pd.put("endDate", sdf.format(new Date()));
		}
		//用于接收导出的数据
		Map<String,Object> dataMap = new HashMap<String,Object>();
		//存储表头列名
		List<String> titles = new ArrayList<String>();
		titles.add("收入来源");	//1
		titles.add("医生姓名");	//2
		titles.add("总收益量");//3
		titles.add("总收益金额（元）");//4
		titles.add("日期");//5
		titles.add("手机号");//6
		titles.add("所在诊所");//7
		dataMap.put("titles", titles);
		//接收从数据库查询出来的数据
		List<PageData> varOList;
		//存储导出所需数据
		List<PageData> varList;
		try{
			//将数据库表内符合条件的数据，查询出来
			varOList = doctorDailyIncomeService.listAllDetailByItem(pd);
			System.out.println("--------varOList:"+varOList);
			varList = new ArrayList<PageData>();
			for(int i=0;i<varOList.size();i++){
				PageData vpd = new PageData();
				if (!IsNull.paramsIsNull(varOList.get(i).get("payStyle")) && 
					varOList.get(i).get("payStyle").toString().equals("0")) {//1
					vpd.put("var1", "赏金");						
				}else if(!IsNull.paramsIsNull(varOList.get(i).get("payStyle")) && 
						varOList.get(i).get("payStyle").toString().equals("1")) {//1	
						vpd.put("var1", "处方");					
				}else if(!IsNull.paramsIsNull(varOList.get(i).get("payStyle")) && 
						varOList.get(i).get("payStyle").toString().equals("2")) {//1	
						vpd.put("var1", "药品");					
				}else if(!IsNull.paramsIsNull(varOList.get(i).get("payStyle")) && 
						varOList.get(i).get("payStyle").toString().equals("3")) {//1	
						vpd.put("var1", "转账");
				}else{
					mv.addObject("error", "收入来源：数据存在错误");
					mv.setViewName("doctor/daily_income/doctor_daily_income_detailitem");
					mv.addObject(Const.SESSION_QX, this.getHC());
					return mv;			
				}
				if(IsNull.paramsIsNull(varOList.get(i).get("trueName"))){
					vpd.put("var2", "");	//2					
				}else{
					vpd.put("var2", varOList.get(i).get("trueName").toString());	//2					
				}
				if(IsNull.paramsIsNull(varOList.get(i).get("sumNum"))){
					vpd.put("var3", "0");	//3					
				}else{
					vpd.put("var3", varOList.get(i).get("sumNum").toString());	//3					
				}
				if(IsNull.paramsIsNull(varOList.get(i).get("sumMoney"))){
					vpd.put("var4", "0.00");	//4				
				}else{
					vpd.put("var4", varOList.get(i).get("sumMoney").toString());	//4				
				}
				if(IsNull.paramsIsNull(varOList.get(i).get("createTime"))){
					vpd.put("var5", "");	//5			
				}else{
					vpd.put("var5", varOList.get(i).get("createTime").toString());	//5				
				}
				if(IsNull.paramsIsNull(varOList.get(i).get("phone"))){
					vpd.put("var6", "");	//6			
				}else{
					vpd.put("var6", varOList.get(i).get("phone").toString());	//6			
				}
				if(IsNull.paramsIsNull(varOList.get(i).get("clinicName"))){
					vpd.put("var7", "");	//7			
				}else{
					vpd.put("var67", varOList.get(i).get("clinicName").toString());	//7			
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
			mv.setViewName("doctor/daily_income/doctor_daily_income_detailitem");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 导出到excel:按医生条件查询-医生总收益,条件：开始日期，结束日期，医生姓名，医生手机号
	 * @return ModelAndView
	 */
	@RequestMapping(value="/excelMain")
	public ModelAndView exportExcelMain(){
		logBefore(logger, "DoctorDailyIncomeController.exportExcelMain()");
		ModelAndView mv = new ModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/daily_income/doctor_daily_income_main");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		PageData pd = new PageData();
		pd = this.getPageData();
		//开始日期,条件查询
		String startDate = pd.getString("startDate");
		//结束日期,条件查询
		String endDate = pd.getString("endDate");
		//医生姓名或手机号,条件查询
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
		titles.add("医生姓名");	//1
		titles.add("总收益（元）");	//2
		titles.add("手机号");//4
		titles.add("所在诊所");	//5
		dataMap.put("titles", titles);
		//接收从数据库查询出来的数据
		List<PageData> varOList;
		//存储导出所需数据
		List<PageData> varList;
		try{
			//将数据库表内符合条件的数据，查询出来
			varOList = doctorDailyIncomeService.listAllMain(pd);
			varList = new ArrayList<PageData>();
			for(int i=0;i<varOList.size();i++){
				PageData vpd = new PageData();
				vpd.put("var1", varOList.get(i).getString("trueName"));	//1
				vpd.put("var2", varOList.get(i).get("sumMoney").toString());	//2
				vpd.put("var3", varOList.get(i).getString("phone"));	//3
				vpd.put("var4", varOList.get(i).getString("clinicName"));//4
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
			mv.setViewName("doctor/daily_income/doctor_daily_income_main");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 导出到excel:按医生条件查询-医生每日收益明细,条件：医生ID,开始日期，结束日期，医生姓名，医生手机号
	 * @return ModelAndView
	 */
	@RequestMapping(value="/excelDetail")
	public ModelAndView exportExcelDetail(){
		logBefore(logger, "DoctorDailyIncomeController.exportExcelDetail()");
		ModelAndView mv = new ModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/daily_income/doctor_daily_income_detail");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		PageData pd = new PageData();
		pd = this.getPageData();
		//医生ID
		String doctorId = pd.getString("doctorId");
		//开始日期,条件查询
		String startDate = pd.getString("startDate");
		//结束日期,条件查询
		String endDate = pd.getString("endDate");
		//医生姓名或手机号,条件查询
		String trueNameOrPhone = pd.getString("trueNameOrPhone");
		//对接收的前端参数，进行判空；如果所有参数都为空，默认日期为当前系统日期
		if(!IsNull.paramsIsNull(doctorId)){
			pd.put("doctorId", doctorId.trim());
		} else {
			mv.addObject("error","未接收到医生ID");
			mv.setViewName("doctor/daily_income/doctor_daily_income_detail");
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
		titles.add("医生姓名");	//1
		titles.add("收益金额（元）");//2
		titles.add("收益来源");//3
		titles.add("日期");//4
		titles.add("手机号");//5
		titles.add("所在诊所");//6
		dataMap.put("titles", titles);
		//接收从数据库查询出来的数据
		List<PageData> varOList;
		//存储导出所需数据
		List<PageData> varList;
		try{
			//将数据库表内符合条件的数据，查询出来
			varOList = doctorDailyIncomeService.listAllDetail(pd);
			System.out.println("--------varOList:"+varOList);
			varList = new ArrayList<PageData>();
			for(int i=0;i<varOList.size();i++){
				PageData vpd = new PageData();
				vpd.put("var1", varOList.get(i).getString("trueName"));	//1
				vpd.put("var2", varOList.get(i).getString("payMoney"));	//2
				if (!IsNull.paramsIsNull(varOList.get(i).get("payStyle")) && 
					varOList.get(i).get("payStyle").toString().equals("0")) {//3
					vpd.put("var3", "赏金");						
				}else if(!IsNull.paramsIsNull(varOList.get(i).get("payStyle")) && 
						varOList.get(i).get("payStyle").toString().equals("1")) {//3	
						vpd.put("var3", "处方");					
				}else if(!IsNull.paramsIsNull(varOList.get(i).get("payStyle")) && 
						varOList.get(i).get("payStyle").toString().equals("2")) {//3	
						vpd.put("var3", "药品");					
				}else if(!IsNull.paramsIsNull(varOList.get(i).get("payStyle")) && 
						varOList.get(i).get("payStyle").toString().equals("3")) {//1	
						vpd.put("var3", "转账");
				}else{
					mv.addObject("error", "收入来源：数据存在错误");
					mv.setViewName("doctor/daily_income/doctor_daily_income_detail");
					mv.addObject(Const.SESSION_QX, this.getHC());
					return mv;				
				}
				vpd.put("var4", varOList.get(i).getString("createTime"));//4
				vpd.put("var5", varOList.get(i).getString("phone"));//5
				vpd.put("var6", varOList.get(i).getString("clinicName"));//6
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
			mv.setViewName("doctor/daily_income/doctor_daily_income_detail");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 从session中,获取操作权限 QX
	 * @return Map<String, String>
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getHC() {
		Subject currentUser = SecurityUtils.getSubject(); // shiro管理的session
		Session session = currentUser.getSession();
		return (Map<String, String>) session.getAttribute(Const.SESSION_QX);
	}
}

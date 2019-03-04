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
import com.fh.service.doctor.DoctorOnlineTimeService;
import com.fh.util.Const;
import com.fh.util.IsNull;
import com.fh.util.Jurisdiction;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;

/**
 * Controller类-统计医生每日在线时长
 * 注：前端页面的开始、结束日期初始值，由页面加载前的查询方法返回值赋予
 * @author 陈彦林
 * @date 2018-07-30
 * 注释者：陈彦林
 * 开发者：陈彦林
 */
@Controller
@RequestMapping(value="doctorOnlineTime")
public class DoctorOnlineTimeController extends BaseController {
	
	//菜单地址(权限用)
	String menuUrl = "doctorOnlineTime/list.do";
	//自动获取service对象
	@Resource(name="doctorOnlineTimeService")
	private DoctorOnlineTimeService doctorOnlineTimeService;
	
	/**
	 * 条件查询-在线时长数据,条件：开始日期，结束日期，医生姓名，医生手机号,前端每页可以显示的数据条数
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) {
		logBefore(logger, "fun in DoctorOnlineTimeController.list()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/online_time/doctor_online_time");
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
			varList = doctorOnlineTimeService.list(page);
			//将查询出来的数据，返给前端
			mv.setViewName("doctor/online_time/doctor_online_time");
			mv.addObject("list", varList);
			mv.addObject("pd", pd);
			mv.addObject("msg", "查询成功！");
		} catch (Exception e) {
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/online_time/doctor_online_time");
			e.printStackTrace();
			return mv;
		}
		logBefore(logger, "fun end DoctorOnlineTimeController.list()");
		return mv;
	}
	
	/**
	 * 导出到excel:条件查询-在线时长数据,条件：开始日期，结束日期，医生姓名，医生手机号
	 * @param pd
	 * @return ModelAndView
	 */
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel(){
		logBefore(logger, "fun in DoctorOnlineTimeController.exportExcel()");
		ModelAndView mv = new ModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/online_time/doctor_online_time");
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
		titles.add("在线时长");	//2
		titles.add("日期");	//3
		titles.add("手机号");//4
		titles.add("所在诊所");	//5
		dataMap.put("titles", titles);
		//接收从数据库查询出来的数据
		List<PageData> varOList;
		//存储导出所需数据
		List<PageData> varList;
		try{
			//将数据库表内符合条件的数据，查询出来
			varOList = doctorOnlineTimeService.listAll(pd);
			varList = new ArrayList<PageData>();
			for(int i=0;i<varOList.size();i++){
				PageData vpd = new PageData();
				vpd.put("var1", varOList.get(i).getString("trueName"));	//1
				vpd.put("var2", varOList.get(i).get("longTime"));	//2
				vpd.put("var3", varOList.get(i).get("dayDate"));	//3
				vpd.put("var4", varOList.get(i).getString("phone"));	//4
				vpd.put("var5", varOList.get(i).getString("clinicName"));//5
				varList.add(vpd);
			}
			dataMap.put("varList", varList);
			//导出到excel
			ObjectExcelView erv = new ObjectExcelView();
			mv = new ModelAndView(erv,dataMap);
			mv.addObject("msg", "成功导出到excel");
		} catch(Exception e){
			logger.error(e.toString(), e);
			mv.addObject("error", e.getMessage());
			mv.setViewName("doctor/online_time/doctor_online_time");
			return mv;
		}
		logBefore(logger, "fun end DoctorOnlineTimeController.exportExcel()");
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
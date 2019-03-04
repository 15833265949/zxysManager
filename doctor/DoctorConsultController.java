package com.fh.controller.doctor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.service.doctor.DoctorConsultService;
import com.fh.service.doctor.DoctorTrendsService;
import com.fh.util.Const;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;
import com.fh.util.tools.IsNull;

/**
 * 创建人：张东东 创建时间：2018-07-31
 */
@Controller
@RequestMapping(value = "/doctorConsult")
public class DoctorConsultController extends BaseController {

	String menuUrl = "doctorConsult/consultlist.do"; // 对应统计被咨询医生数量界面
	@Resource(name = "doctorConsultService")
	private DoctorConsultService doctorConsultService;
	
	
	@RequestMapping(value="/consultlist")
	@ResponseBody
	public ModelAndView ConsultDoctorlist(Page page) {
		logBefore(logger, "每日被咨询医生数量");
		ModelAndView mv=this.getModelAndView();
		PageData pd=new PageData();
		try {
			pd=this.getPageData();
			String BeginDate=(String) pd.get("begindate");
			String EndDate=(String) pd.get("enddate");
			
			if(!IsNull.paramsIsNull(BeginDate)&&!IsNull.paramsIsNull(EndDate)){
				pd.put("begintime", BeginDate+" 00:00:00");
				pd.put("endtime", EndDate+" 23:59:59");
			}
			
			page.setPd(pd);
			List<PageData> varList=doctorConsultService.ConsultDoctorList(page);
			long lConsult=(long) doctorConsultService.ConsultNum(pd).get("consultCount");
			mv.setViewName("doctor/doctorconsult/doctorconsult_list");
			mv.addObject("consultNum",lConsult);
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX,this.getHC());	//按钮权限
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	@RequestMapping(value="/consultType")
	@ResponseBody
	public ModelAndView ConsultTypeIntime() {
		logBefore(logger, "一定时间内医生回复情况");
		ModelAndView mv=this.getModelAndView();
		PageData pd=new PageData();
		try {
			pd=this.getPageData();
			String BeginDate=(String) pd.get("begindate");
			String EndDate=(String) pd.get("enddate");
			if(!IsNull.paramsIsNull(BeginDate)&&!IsNull.paramsIsNull(EndDate)){
				pd.put("begintime", BeginDate+" 00:00:00");
				pd.put("endtime", EndDate+" 23:59:59");
			}
			List<PageData> varList=doctorConsultService.ConsultTypeIntime(pd);
			mv.setViewName("doctor/doctorconsult/doctorconsult_list2");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX,this.getHC());	//按钮权限
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	@RequestMapping(value="/consultTimes")
	@ResponseBody
	public ModelAndView ConsultTimes() {
		logBefore(logger, "一定时间内医生回复情况");
		ModelAndView mv=this.getModelAndView();
		PageData pd=new PageData();
		try {
			pd=this.getPageData();
			String BeginDate=(String) pd.get("begindate");
			String EndDate=(String) pd.get("enddate");
			if(!IsNull.paramsIsNull(BeginDate)&&!IsNull.paramsIsNull(EndDate)){
				pd.put("begintime", BeginDate+" 00:00:00");
				pd.put("endtime", EndDate+" 23:59:59");
			}
			List<PageData> varList=doctorConsultService.ConsultTimes(pd);
			mv.setViewName("doctor/doctorconsult/doctorconsult_list5");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX,this.getHC());	//按钮权限
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	@RequestMapping(value="/consultTypeDetail")
	@ResponseBody
	public ModelAndView ConsultTypeDetail(Page page) {
		logBefore(logger, "一定时间内医生回复明细情况");
		ModelAndView mv=this.getModelAndView();
		PageData pd=new PageData();
		try {
			pd=this.getPageData();
			String BeginDate=(String) pd.get("begindate");
			String EndDate=(String) pd.get("enddate");
			if(!IsNull.paramsIsNull(BeginDate)&&!IsNull.paramsIsNull(EndDate)){
				pd.put("begintime", BeginDate+" 00:00:00");
				pd.put("endtime", EndDate+" 23:59:59");
			}
			page.setPd(pd);
			System.err.println(pd);
			List<PageData> varList=doctorConsultService.ConsultTypeDetailIntime(page);
			mv.setViewName("doctor/doctorconsult/doctorconsult_list4");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX,this.getHC());	//按钮权限
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	@RequestMapping(value="/consultTimesDetail")
	@ResponseBody
	public ModelAndView ConsultTimesDetail(Page page) {
		logBefore(logger, "一定时间内医生回复明细情况");
		ModelAndView mv=this.getModelAndView();
		PageData pd=new PageData();
		try {
			pd=this.getPageData();
			String BeginDate=(String) pd.get("begindate");
			String EndDate=(String) pd.get("enddate");
			if(!IsNull.paramsIsNull(BeginDate)&&!IsNull.paramsIsNull(EndDate)){
				pd.put("begintime", BeginDate+" 00:00:00");
				pd.put("endtime", EndDate+" 23:59:59");
			}
			page.setPd(pd);
			System.err.println(pd);
			List<PageData> varList=doctorConsultService.ConsultTimesDetail(page);
			mv.setViewName("doctor/doctorconsult/doctorconsult_list6");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX,this.getHC());	//按钮权限
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	@RequestMapping(value="/withOutAnswer")
	@ResponseBody
	public ModelAndView ConsultWithOutAnswer(Page page) {
		logBefore(logger, "未回复患者咨询的医生信息");
		ModelAndView mv=this.getModelAndView();
		PageData pd=new PageData();
		try {
			pd=this.getPageData();
			String BeginDate=(String) pd.get("begindate");
			String EndDate=(String) pd.get("enddate");
			if(!IsNull.paramsIsNull(BeginDate)&&!IsNull.paramsIsNull(EndDate)){
				pd.put("begintime", BeginDate+" 00:00:00");
				pd.put("endtime", EndDate+" 23:59:59");
			}
			page.setPd(pd);
			List<PageData> varList=doctorConsultService.ConsultWithOutAnswerlistPage(page);
			mv.setViewName("doctor/doctorconsult/doctorconsult_list3");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX,this.getHC());	//按钮权限
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	@RequestMapping(value="/excel1")
	@ResponseBody
	public ModelAndView toexcel1() {
		ModelAndView mv=this.getModelAndView();
		PageData pd=new PageData();
		try {
			pd=this.getPageData();
			String BeginDate=(String) pd.get("begindate");
			String EndDate=(String) pd.get("enddate");
			if(!IsNull.paramsIsNull(BeginDate)&&!IsNull.paramsIsNull(EndDate)){
				pd.put("begintime", BeginDate+" 00:00:00");
				pd.put("endtime", EndDate+" 23:59:59");
			}
			Map<String,Object> dataMap = new HashMap<String,Object>();
			List<String> titles = new ArrayList<String>();
			
			titles.add("医生姓名"); 		//1
			titles.add("电话");  		//2
			titles.add("地址");			//3
			titles.add("诊所名称");			//4
			titles.add("请求次数");			//5
			titles.add("回答次数");			//6
			dataMap.put("titles", titles);
			List<PageData> consultList=doctorConsultService.ConsultDoctorAll(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for(int i=0;i<consultList.size();i++){
				PageData vpd = new PageData();
				vpd.put("var1", consultList.get(i).getString("trueName"));		//1
				vpd.put("var2", consultList.get(i).getString("phone"));		//2
				vpd.put("var3", consultList.get(i).getString("clinicProvince")+","+consultList.get(i).getString("cityname")+","+consultList.get(i).getString("clinicAddress"));			//3
				vpd.put("var4", consultList.get(i).getString("clinicName"));	//4
				vpd.put("var5", consultList.get(i).get("questNum")+"");		//5
				vpd.put("var6", consultList.get(i).get("answerNum")+"");		//6
				varList.add(vpd);
			}
			dataMap.put("varList", varList);
			ObjectExcelView erv = new ObjectExcelView();					//执行excel操作
			mv = new ModelAndView(erv,dataMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	@RequestMapping(value="/excel2")
	@ResponseBody
	public ModelAndView toexcel2() {
		ModelAndView mv=this.getModelAndView();
		PageData pd=new PageData();
		try {
			pd=this.getPageData();
			String BeginDate=(String) pd.get("begindate");
			String EndDate=(String) pd.get("enddate");
			if(!IsNull.paramsIsNull(BeginDate)&&!IsNull.paramsIsNull(EndDate)){
				pd.put("begintime", BeginDate+" 00:00:00");
				pd.put("endtime", EndDate+" 23:59:59");
			}
			Map<String,Object> dataMap = new HashMap<String,Object>();
			List<String> titles = new ArrayList<String>();
			
			titles.add("医生姓名"); 		//1
			titles.add("电话");  		//2
			titles.add("地址");			//3
			titles.add("诊所名称");			//4
			titles.add("回答次数");			//5
			dataMap.put("titles", titles);
			List<PageData> consultList=doctorConsultService.ConsultTypeDetailAll(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for(int i=0;i<consultList.size();i++){
				PageData vpd = new PageData();
				vpd.put("var1", consultList.get(i).getString("trueName"));		//1
				vpd.put("var2", consultList.get(i).getString("phone"));		//2
				vpd.put("var3", consultList.get(i).getString("clinicProvince")+","+consultList.get(i).getString("cityname")+","+consultList.get(i).getString("clinicAddress"));			//3
				vpd.put("var4", consultList.get(i).getString("clinicName"));	//4
				vpd.put("var5", consultList.get(i).get("count")+"");		//5
				varList.add(vpd);
			}
			dataMap.put("varList", varList);
			ObjectExcelView erv = new ObjectExcelView();					//执行excel操作
			mv = new ModelAndView(erv,dataMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	@RequestMapping(value="/excel4")
	@ResponseBody
	public ModelAndView toexcel4() {
		ModelAndView mv=this.getModelAndView();
		PageData pd=new PageData();
		try {
			pd=this.getPageData();
			String BeginDate=(String) pd.get("begindate");
			String EndDate=(String) pd.get("enddate");
			if(!IsNull.paramsIsNull(BeginDate)&&!IsNull.paramsIsNull(EndDate)){
				pd.put("begintime", BeginDate+" 00:00:00");
				pd.put("endtime", EndDate+" 23:59:59");
			}
			Map<String,Object> dataMap = new HashMap<String,Object>();
			List<String> titles = new ArrayList<String>();
			
			titles.add("医生姓名"); 		//1
			titles.add("电话");  		//2
			titles.add("地址");			//3
			titles.add("诊所名称");			//4
			titles.add("回答次数");			//5
			dataMap.put("titles", titles);
			List<PageData> consultList=doctorConsultService.ConsultTimesDetail(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for(int i=0;i<consultList.size();i++){
				PageData vpd = new PageData();
				vpd.put("var1", consultList.get(i).getString("trueName"));		//1
				vpd.put("var2", consultList.get(i).getString("phone"));		//2
				vpd.put("var3", consultList.get(i).getString("clinicProvince")+","+consultList.get(i).getString("cityname")+","+consultList.get(i).getString("clinicAddress"));			//3
				vpd.put("var4", consultList.get(i).getString("clinicName"));	//4
				vpd.put("var5", consultList.get(i).get("count")+"");		//5
				varList.add(vpd);
			}
			dataMap.put("varList", varList);
			ObjectExcelView erv = new ObjectExcelView();					//执行excel操作
			mv = new ModelAndView(erv,dataMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	@RequestMapping(value="/excel3")
	@ResponseBody
	public ModelAndView toexcel3() {
		ModelAndView mv=this.getModelAndView();
		PageData pd=new PageData();
		try {
			pd=this.getPageData();
			String BeginDate=(String) pd.get("begindate");
			String EndDate=(String) pd.get("enddate");
			if(!IsNull.paramsIsNull(BeginDate)&&!IsNull.paramsIsNull(EndDate)){
				pd.put("begintime", BeginDate+" 00:00:00");
				pd.put("endtime", EndDate+" 23:59:59");
			}
			Map<String,Object> dataMap = new HashMap<String,Object>();
			List<String> titles = new ArrayList<String>();
			
			titles.add("医生姓名"); 		//1
			titles.add("电话");  		//2
			titles.add("地址");			//3
			titles.add("诊所名称");			//4
			titles.add("未回复请求次数");			//5
			dataMap.put("titles", titles);
			List<PageData> consultList=doctorConsultService.ConsultWithOutAnswerAll(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for(int i=0;i<consultList.size();i++){
				PageData vpd = new PageData();
				vpd.put("var1", consultList.get(i).getString("trueName"));		//1
				vpd.put("var2", consultList.get(i).getString("phone"));		//2
				vpd.put("var3", consultList.get(i).getString("clinicProvince")+","+consultList.get(i).getString("cityname")+","+consultList.get(i).getString("clinicAddress"));			//3
				vpd.put("var4", consultList.get(i).getString("clinicName"));	//4
				vpd.put("var5", consultList.get(i).get("consultCount")+"");		//5
				varList.add(vpd);
			}
			dataMap.put("varList", varList);
			ObjectExcelView erv = new ObjectExcelView();					//执行excel操作
			mv = new ModelAndView(erv,dataMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	
	
	
	
	/* ===============================权限================================== */
	public Map<String, String> getHC(){
		Subject currentUser = SecurityUtils.getSubject();  //shiro管理的session
		Session session = currentUser.getSession();
		return (Map<String, String>)session.getAttribute(Const.SESSION_QX);
	}
}

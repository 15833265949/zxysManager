package com.fh.controller.patient;

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
import com.fh.service.patient.PatientConsultService;
import com.fh.util.Const;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;
import com.fh.util.tools.IsNull;

@Controller
@RequestMapping("/PatientConsult")
public class PatientConsultController extends BaseController{

	String menuUrl = "PatientConsult/consultlist.do"; // 对应统计被咨询医生数量界面
	@Resource(name = "patientConsultService")
	private PatientConsultService patientConsultService;
	
	@RequestMapping(value="/consultlist")
	@ResponseBody
	public ModelAndView Consultlist(Page page) {
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
			List<PageData> varList=patientConsultService.ConsultNum(page);
			mv.setViewName("patient/patientConsult/patientconsult_list");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX,this.getHC());	//按钮权限
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	@RequestMapping(value="/patientlist")
	@ResponseBody
	public ModelAndView Patientlist(Page page) {
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
			List<PageData> varList=patientConsultService.PatientNum(page);
			mv.setViewName("patient/patientConsult/patientconsult_list2");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX,this.getHC());	//按钮权限
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	@RequestMapping(value="/consultdetail")
	@ResponseBody
	public ModelAndView ConsultDetail(Page page) {
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
			List<PageData> varList=patientConsultService.ConsultDetail(page);
			mv.setViewName("patient/patientConsult/patientconsult_list4");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX,this.getHC());	//按钮权限
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	@RequestMapping(value="/patientdetail")
	@ResponseBody
	public ModelAndView PatientDetail(Page page) {
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
			List<PageData> varList=patientConsultService.PatientDetail(page);
			mv.setViewName("patient/patientConsult/patientconsult_list3");
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
			
			titles.add("日期"); 		//1
			titles.add("新增患者咨询问题数");			//3
			titles.add("老患者咨询问题数");			//4
			titles.add("当日问题总数");			//5
			dataMap.put("titles", titles);
			List<PageData> consultList=patientConsultService.ConsultNum(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for(int i=0;i<consultList.size();i++){
				PageData vpd = new PageData();
				vpd.put("var1", consultList.get(i).getString("dayDate"));		//1
				vpd.put("var2", consultList.get(i).get("newCount")+"");		//2
				vpd.put("var3", consultList.get(i).get("oldCount")+"");			//3
				vpd.put("var4", consultList.get(i).get("allCount")+"");	//4
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
			
			titles.add("日期"); 		//1
			titles.add("咨询问题新增患者数量");			//2
			titles.add("咨询问题新增患者占比");			//3
			titles.add("咨询问题老患者数量");			//4
			titles.add("咨询问题老患者占比");			//5
			titles.add("咨询问题患者总数");			//6
			titles.add("活跃患者咨询问题概率");			//7
			dataMap.put("titles", titles);
			List<PageData> consultList=patientConsultService.PatientNum(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for(int i=0;i<consultList.size();i++){
				PageData vpd = new PageData();
				vpd.put("var1", consultList.get(i).getString("dayDate"));		//1
				vpd.put("var2", consultList.get(i).get("newCount")+"");		//2
				vpd.put("var3", consultList.get(i).getString("newRatio"));		//3
				vpd.put("var4", consultList.get(i).get("oldCount")+"");			//4
				vpd.put("var5", consultList.get(i).getString("oldRatio"));			//5
				vpd.put("var6", consultList.get(i).get("allCount")+"");	//6
				vpd.put("var7", consultList.get(i).getString("allRatio"));	//7
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
			
			titles.add("患者名称"); 		//1
			titles.add("电话");			//2
			titles.add("咨询问题数");			//3
			titles.add("是否新增患者");			//4
			dataMap.put("titles", titles);
			List<PageData> consultList=patientConsultService.PatientNum(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for(int i=0;i<consultList.size();i++){
				PageData vpd = new PageData();
				vpd.put("var1", consultList.get(i).getString("trueName"));		//1
				vpd.put("var2", consultList.get(i).get("quesCount")+"");		//2
				vpd.put("var3", consultList.get(i).getString("phone"));		//3
				vpd.put("var4", consultList.get(i).getString("type"));		//3
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
			
			titles.add("咨询内容"); 		//1
			titles.add("回复次数");			//2
			titles.add("咨询科室");			//3
			titles.add("咨询诊所");			//4
			titles.add("咨询医生");			//4
			titles.add("沟通时长");			//4
			dataMap.put("titles", titles);
			List<PageData> consultList=patientConsultService.PatientNum(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for(int i=0;i<consultList.size();i++){
				PageData vpd = new PageData();
				vpd.put("var1", consultList.get(i).getString("content"));		//1
				vpd.put("var2", consultList.get(i).get("dNum")+"");		//2
				vpd.put("var3", consultList.get(i).getString("officeName"));		//3
				vpd.put("var4", consultList.get(i).getString("clinicName"));		//3
				vpd.put("var5", consultList.get(i).getString("trueName"));		//3
				vpd.put("var6", consultList.get(i).getString("time"));		//3
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

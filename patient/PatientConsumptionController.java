package com.fh.controller.patient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.fh.service.patient.PatientConsumptionService;
import com.fh.util.Const;
import com.fh.util.IsNull;
import com.fh.util.Jurisdiction;
import com.fh.util.PageData;

/**
 * Controller类-患者消费统计：年度，月度，当月
 * 注：前端页面的开始、结束日期初始值，由页面加载前的查询方法返回值赋予
 * @author 陈彦林
 * @date 2018-08-12
 * 注释者：陈彦林
 * 开发者：陈彦林
 */
@Controller
@RequestMapping("patientConsumption")
public class PatientConsumptionController extends BaseController {
	
	//菜单地址(权限用)
	String menuUrl = "patientConsumption/byDay.do";
	//自动获取service对象
	@Resource(name="patientConsumptionService")
	private PatientConsumptionService patientConsumptionService;
	
	/**
	 * 患者当月消费数据-统计图
	 * @return ModelAndView
	 */
	@RequestMapping("/byDay")
	public ModelAndView listByDayMap() {
		logBefore(logger, "PatientConsumptionController.listByDayMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("patient/patientConsumption/patient_consumption_bydayMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		List<PageData> varList = new ArrayList<PageData>();
		List<Object> dateList = new ArrayList<Object>();
		List<Object> valueList = new ArrayList<Object>();
		//获取系统当前日期的天数
		SimpleDateFormat sdf = new SimpleDateFormat("d");
		int dayInt = Integer.parseInt(sdf.format(new Date()));
		//X轴日期数据
		for (int i = 1; i <= dayInt; i++) {
			dateList.add("'" + i + "日'");
		}
		int count = 1;//1日到dayInt日的计数器
		try {
			varList = patientConsumptionService.listDayMap();
			logOut(logger, "varList", varList);
			for(int i = 0; i < varList.size(); i++){
				//若日期为空
				if (IsNull.paramsIsNull(varList.get(i).get("dat"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("patient/patientConsumption/patient_consumption_bydayMap");
					return mv;
				}
				for(int j = count; j <= dayInt; j++){
					//若日期不为空
					if (Integer.parseInt(varList.get(i).get("dat").toString().substring(8))==j) {//若日期  = j日
						valueList.add(varList.get(i).get("sumMoney"));
						count++;
						if (i != (varList.size()-1)) {
							break;
						}
					}else {//若日期   ！=  j日 
						valueList.add(0.00);
						count++;
					}
				}
			}
			logOut(logger, "dateList", dateList);
			logOut(logger, "valueList", valueList);
			mv.addObject("dateList", dateList);
			mv.addObject("valueList", valueList);
			mv.setViewName("patient/patientConsumption/patient_consumption_bydayMap");
		} catch (Exception e) {
			mv.addObject("error",e.getMessage());
			System.out.println("--------error:"+e.getMessage());
			mv.setViewName("patient/patientConsumption/patient_consumption_bydayMap");
			e.printStackTrace();
		}	
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 患者月度消费曲线统计图
	 * @return ModelAndView
	 */
	@RequestMapping("/byMonth")
	public ModelAndView listByMonthMap() {
		logBefore(logger, "PatientConsumptionController.listByMonthMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("patient/patientConsumption/patient_consumption_bymonthMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		List<PageData> varList = new ArrayList<PageData>();
		List<Object> dateList = new ArrayList<Object>();
		List<Object> valueList = new ArrayList<Object>();
		//获取系统当前月份
		SimpleDateFormat sdf = new SimpleDateFormat("M");
		int monthInt = Integer.parseInt(sdf.format(new Date()));
		//X轴日期数据
		for (int i = 1; i <= monthInt; i++) {
			dateList.add("'" + i + "月'");
		}
		int count = 1;//1月到monthInt月的计数器
		try {
			varList = patientConsumptionService.listMontylyMap();
			logOut(logger, "varList", varList);
			for(int i = 0; i < varList.size(); i++){
				//若日期为空
				if (IsNull.paramsIsNull(varList.get(i).get("dat"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("patient/patientConsumption/patient_consumption_bymonthMap");
					return mv;
				}
				for(int j = count; j <= monthInt; j++){
					//若日期不为空
					if (Integer.parseInt(varList.get(i).get("dat").toString().substring(5))==j) {//若日期  = j月
						valueList.add(varList.get(i).get("sumMoney"));
						count++;
						if (i != (varList.size()-1)) {
							break;
						}
					}else {//若日期   ！=  j月
						valueList.add(0.00);
						count++;
					}
				}
			}
			logOut(logger, "dateList", dateList);
			logOut(logger, "valueList", valueList);
			mv.addObject("dateList", dateList);
			mv.addObject("valueList", valueList);
			mv.setViewName("patient/patientConsumption/patient_consumption_bymonthMap");
		} catch (Exception e) {
			mv.addObject("error",e.getMessage());
			System.out.println("--------error:"+e.getMessage());
			mv.setViewName("patient/patientConsumption/patient_consumption_bymonthMap");
			e.printStackTrace();
		}	
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 查询患者年度消费数据
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/byYear")
	public ModelAndView listByYearMap() {
		logBefore(logger, "PatientConsumptionController.listByYearMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("patient/patientConsumption/patient_consumption_byyearMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		List<PageData> varList = new ArrayList<PageData>();
		List<Object> dateList = new ArrayList<Object>();
		List<Object> valueList = new ArrayList<Object>();
		//获取系统当前日期的月份
		try {
			varList = patientConsumptionService.listAnnualMap();
			for (int i = 0; i < varList.size(); i++) {
				//若日期为空
				if (IsNull.paramsIsNull(varList.get(i).get("dat"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("patient/patientConsumption/patient_consumption_byyearMap");
					return mv;
				}
				//若日期不为空
				dateList.add("'" + varList.get(i).get("dat") + "年'");
				valueList.add(varList.get(i).get("sumMoney"));				
			}
			mv.addObject("dateList", dateList);
			mv.addObject("valueList", valueList);
			mv.setViewName("patient/patientConsumption/patient_consumption_byyearMap");
		} catch (Exception e) {
			mv.addObject("error",e.getMessage());
			System.out.println("--------error:"+e.getMessage());
			mv.setViewName("patient/patientConsumption/patient_consumption_byyearMap");
			e.printStackTrace();
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

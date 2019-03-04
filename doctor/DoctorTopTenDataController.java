package com.fh.controller.doctor;

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
import com.fh.entity.Page;
import com.fh.service.doctor.DoctorTopTenDataService;
import com.fh.util.Const;
import com.fh.util.IsNull;
import com.fh.util.Jurisdiction;
import com.fh.util.PageData;

/**
 * Controller类-全国医生-各类收入的前10名数据：
 * 总收入，打赏赏金，接诊数，开处方数，出售处方数，出售处方金额，出售处方药品数，出售处方药品金额，转账收入金额 
 * 
 * @author 陈彦林
 * @date 2018-08-16
 * 注释者：陈彦林
 * 开发者：陈彦林
 */
@Controller
@RequestMapping("doctorTopTenData")
public class DoctorTopTenDataController extends BaseController {
	
	//菜单地址(权限用)
	String menuUrl = "doctorTopTenData/totalIncomeByDay.do";
	//自动获取service对象
	@Resource(name="doctorTopTenDataService")
	private DoctorTopTenDataService doctorTopTenDataService;
	//接收-今年总收入前10名医生数据:doctorId医生ID，trueName医生姓名，clinicId诊所ID，clinicName诊所名称
	private List<PageData> topTenDoctors;
	
	/**
	 * 当月总收入-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/totalIncomeByDay")
	public ModelAndView totalIncomeByDayMap() {
		logBefore(logger, "DoctorTopTenDataController.totalIncomeByDayMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_bydayMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		List<PageData> dayAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//今天处于这个月第几天
		SimpleDateFormat sdf = new SimpleDateFormat("d");
		int dayInt = Integer.parseInt(sdf.format(new Date()));
		//循环存储X轴数据
		for (int i = 1; i <= dayInt; i++) {
			dateList.add("'" + i + "日'");
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			//获取今年总收入前10名医生数据
			topTenDoctors = doctorTopTenDataService.totalIncomeTopTenDoctorList(pd);
			logOut(logger, "topTenDoctors", topTenDoctors);
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_bydayMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				dayAmountDoctorsList = doctorTopTenDataService.dayTotalIncomelistMap(pd);
				logOut(logger, "varList", dayAmountDoctorsList);
				//1日到今日的计数器
				int count = 1;
				//封装1日到今日的该医生的数据(若某日的数据空，则赋值0)
				for (int j = 0; j < dayAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(dayAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_bydayMap");
						return mv;
					}
					for (int m = count; m <= dayInt; m++) {
						if (Integer.parseInt(dayAmountDoctorsList.get(j).get("dat").toString().substring(8))==m) {
							list.add(dayAmountDoctorsList.get(j).get("doctorTotalIncome"));
							count++;								
							if (j != (dayAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = 1; i <= dayInt; i++) {
				remarkList.add(0.00);
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_bydayMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_bydayMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 月度总收入-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/totalIncomeByMonth")
	public ModelAndView totalIncomeByMonthMap() {
		logBefore(logger, "DoctorTopTenDataController.totalIncomeByMonthMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_bymonthMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		List<PageData> monthAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//当前月份
		SimpleDateFormat sdf = new SimpleDateFormat("M");
		int monthInt = Integer.parseInt(sdf.format(new Date()));
		//循环存储X轴数据
		for (int i = 1; i <= monthInt; i++) {
			dateList.add("'" + i + "月'");
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_bymonthMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				monthAmountDoctorsList = doctorTopTenDataService.monthTotalIncomelistMap(pd);
				logOut(logger, "varList", monthAmountDoctorsList);
				//1月到当前月的计数器
				int count = 1;
				//封装1月到今月的该医生的数据(若某月的数据为空，则赋值0)
				for (int j = 0; j < monthAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(monthAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_bymonthMap");
						return mv;
					}
					for (int m = count; m <= monthInt; m++) {
						if (Integer.parseInt(monthAmountDoctorsList.get(j).get("dat").toString().substring(5))==m) {
							list.add(monthAmountDoctorsList.get(j).get("doctorTotalIncome"));
							count++;								
							if (j != (monthAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元 
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = 1; i <= monthInt; i++) {
				remarkList.add(0.00);				
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_bymonthMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_bymonthMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 年度总收入-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/totalIncomeByYear")
	public ModelAndView totalIncomeByYearMap() {
		logBefore(logger, "DoctorTopTenDataController.totalIncomeByYearMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_byyearMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> yearAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//今年年份
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		int curYearInt = Integer.parseInt(sdf.format(new Date()));
		try {
			//获取患者订单最早创建时间的年份
			PageData pdYear = doctorTopTenDataService.earliestYear(pd);
			if (IsNull.paramsIsNull(pdYear)) {
				mv.addObject("error", "系统数据错误");
				mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_byyearMap");
				return mv;
			}
			int earliestYearInt = Integer.parseInt(pdYear.get("earliestYear").toString());
			for (int i = earliestYearInt; i <= curYearInt; i++) {
				dateList.add("'" + i + "年'");
			}
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_byyearMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				yearAmountDoctorsList = doctorTopTenDataService.yearTotalIncomelistMap(pd);
				logOut(logger, "varList", yearAmountDoctorsList);
				//"earliestYearInt"年到今年的计数器
				int count = earliestYearInt;
				//封装这几年该医生的数据(若某年的数据为空，则赋值0)
				for (int j = 0; j < yearAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(yearAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_byyearMap");
						return mv;
					}
					for (int m = count; m <= curYearInt; m++) {
						if (Integer.parseInt(yearAmountDoctorsList.get(j).get("dat").toString())==m) {
							list.add(yearAmountDoctorsList.get(j).get("doctorTotalIncome"));
							count++;								
							if (j != (yearAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元 
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = earliestYearInt; i <= curYearInt; i++) {
				remarkList.add(0.00);				
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_byyearMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/totalIncome/doctor_topten_total_income_byyearMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 当月打赏金额-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/rewardByDay")
	public ModelAndView rewardByDayMap() {
		logBefore(logger, "DoctorTopTenDataController.rewardByDayMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_bydayMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		List<PageData> dayAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//今天处于这个月第几天
		SimpleDateFormat sdf = new SimpleDateFormat("d");
		int dayInt = Integer.parseInt(sdf.format(new Date()));
		//循环存储X轴数据
		for (int i = 1; i <= dayInt; i++) {
			dateList.add("'" + i + "日'");
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_bydayMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				dayAmountDoctorsList = doctorTopTenDataService.dayRewardlistMap(pd);
				logOut(logger, "varList", dayAmountDoctorsList);
				//1日到今日的计数器
				int count = 1;
				//封装1日到今日的该医生的数据(若某日的数据空，则赋值0)
				for (int j = 0; j < dayAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(dayAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_bydayMap");
						return mv;
					}
					for (int m = count; m <= dayInt; m++) {
						if (Integer.parseInt(dayAmountDoctorsList.get(j).get("dat").toString().substring(8))==m) {
							list.add(dayAmountDoctorsList.get(j).get("shangjin"));
							count++;								
							if (j != (dayAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = 1; i <= dayInt; i++) {
				remarkList.add(0.00);
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_bydayMap");
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_bydayMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 月度打赏金额-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/rewardByMonth")
	public ModelAndView rewardByMonthMap() {
		logBefore(logger, "DoctorTopTenDataController.rewardByMonthMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_bymonthMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		List<PageData> monthAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//当前月份
		SimpleDateFormat sdf = new SimpleDateFormat("M");
		int monthInt = Integer.parseInt(sdf.format(new Date()));
		//循环存储X轴数据
		for (int i = 1; i <= monthInt; i++) {
			dateList.add("'" + i + "月'");
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_bymonthMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				monthAmountDoctorsList = doctorTopTenDataService.monthRewardlistMap(pd);
				logOut(logger, "varList", monthAmountDoctorsList);
				//1月到今月的计数器
				int count = 1;
				//封装1月到今月的该医生的数据(若某月的数据空，则赋值0)
				for (int j = 0; j < monthAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(monthAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_bymonthMap");
						return mv;
					}
					for (int m = count; m <= monthInt; m++) {
						if (Integer.parseInt(monthAmountDoctorsList.get(j).get("dat").toString().substring(5))==m) {
							list.add(monthAmountDoctorsList.get(j).get("shangjin"));
							count++;								
							if (j != (monthAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = 1; i <= monthInt; i++) {
				remarkList.add(0.00);
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_bymonthMap");
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_bymonthMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 年度打赏金额-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/rewardByYear")
	public ModelAndView rewardByYearMap() {
		logBefore(logger, "DoctorTopTenDataController.rewardByYearMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_byyearMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> yearAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//今年年份
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		int curYearInt = Integer.parseInt(sdf.format(new Date()));
		try {
			//获取患者订单最早创建时间的年份
			PageData pdYear = doctorTopTenDataService.earliestYear(pd);
			if (IsNull.paramsIsNull(pdYear)) {
				mv.addObject("error", "系统数据错误");
				mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_byyearMap");
				return mv;
			}
			int earliestYearInt = Integer.parseInt(pdYear.get("earliestYear").toString());
			for (int i = earliestYearInt; i <= curYearInt; i++) {
				dateList.add("'" + i + "年'");
			}
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_byyearMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				yearAmountDoctorsList = doctorTopTenDataService.yearRewardlistMap(pd);
				logOut(logger, "varList", yearAmountDoctorsList);
				//"earliestYearInt"年到今年的计数器
				int count = earliestYearInt;
				//封装这几年该医生的数据(若某年的数据为空，则赋值0)
				for (int j = 0; j < yearAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(yearAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_byyearMap");
						return mv;
					}
					for (int m = count; m <= curYearInt; m++) {
						if (Integer.parseInt(yearAmountDoctorsList.get(j).get("dat").toString())==m) {
							list.add(yearAmountDoctorsList.get(j).get("shangjin"));
							count++;								
							if (j != (yearAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元 
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = earliestYearInt; i <= curYearInt; i++) {
				remarkList.add(0.00);				
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_byyearMap");
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/reward/doctor_topten_reward_amount_byyearMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 当月转账收入金额-统计图表
	 * @return ModelAndView
	 */
	@RequestMapping("/transferByDay")
	public ModelAndView transferByDayMap() {
		logBefore(logger, "DoctorTopTenDataController.transferByDayMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_bydayMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		List<PageData> dayAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//今天处于这个月第几天
		SimpleDateFormat sdf = new SimpleDateFormat("d");
		int dayInt = Integer.parseInt(sdf.format(new Date()));
		//循环存储X轴数据
		for (int i = 1; i <= dayInt; i++) {
			dateList.add("'" + i + "日'");
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_bydayMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				dayAmountDoctorsList = doctorTopTenDataService.dayTransferAmountlistMap(pd);
				logOut(logger, "varList", dayAmountDoctorsList);
				//1日到今日的计数器
				int count = 1;
				//封装1日到今日的该医生的数据(若某日的数据空，则赋值0)
				for (int j = 0; j < dayAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(dayAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_bydayMap");
						return mv;
					}
					for (int m = count; m <= dayInt; m++) {
						if (Integer.parseInt(dayAmountDoctorsList.get(j).get("dat").toString().substring(8))==m) {
							list.add(dayAmountDoctorsList.get(j).get("zhuanzhang"));
							count++;								
							if (j != (dayAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = 1; i <= dayInt; i++) {
				remarkList.add(0.00);
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_bydayMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_bydayMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 月度转账收入金额-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/transferByMonth")
	public ModelAndView transferByMonthMap(Page page) {
		logBefore(logger, "DoctorTopTenDataController.transferByMonthMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_bymonthMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		List<PageData> monthAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//当前月份
		SimpleDateFormat sdf = new SimpleDateFormat("M");
		int monthInt = Integer.parseInt(sdf.format(new Date()));
		//循环存储X轴数据
		for (int i = 1; i <= monthInt; i++) {
			dateList.add("'" + i + "月'");
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_bymonthMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				monthAmountDoctorsList = doctorTopTenDataService.monthTransferAmountlistMap(pd);
				logOut(logger, "varList", monthAmountDoctorsList);
				//1月到今月的计数器
				int count = 1;
				//封装1月到今月的该医生的数据(若某的数据空，则赋值0)
				for (int j = 0; j < monthAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(monthAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_bymonthMap");
						return mv;
					}
					for (int m = count; m <= monthInt; m++) {
						if (Integer.parseInt(monthAmountDoctorsList.get(j).get("dat").toString().substring(5))==m) {
							list.add(monthAmountDoctorsList.get(j).get("zhuanzhang"));
							count++;								
							if (j != (monthAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = 1; i <= monthInt; i++) {
				remarkList.add(0.00);
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_bymonthMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_bymonthMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 年度转账收入金额-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/transferByYear")
	public ModelAndView transferByYearMap(Page page) {
		logBefore(logger, "DoctorTopTenDataController.transferByYearMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_byyearMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> yearAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//获取今年年份
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		int curYearInt = Integer.parseInt(sdf.format(new Date()));
		try {
			//获取患者订单最早创建时间的年份
			PageData pdYear = doctorTopTenDataService.earliestYear(pd);
			if (IsNull.paramsIsNull(pdYear)) {
				mv.addObject("error", "系统数据错误");
				mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_byyearMap");
				return mv;
			}
			int earliestYearInt = Integer.parseInt(pdYear.get("earliestYear").toString());
			for (int i = earliestYearInt; i <= curYearInt; i++) {
				dateList.add("'" + i + "年'");
			}
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_byyearMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				yearAmountDoctorsList = doctorTopTenDataService.yearTransferAmountlistMap(pd);
				logOut(logger, "varList", yearAmountDoctorsList);
				//"earliestYearInt"年到今年的计数器
				int count = earliestYearInt;
				//封装这几年该医生的数据(若某年的数据为空，则赋值0)
				for (int j = 0; j < yearAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(yearAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_byyearMap");
						return mv;
					}
					for (int m = count; m <= curYearInt; m++) {
						if (Integer.parseInt(yearAmountDoctorsList.get(j).get("dat").toString())==m) {
							list.add(yearAmountDoctorsList.get(j).get("zhuanzhang"));
							count++;								
							if (j != (yearAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元 
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = earliestYearInt; i <= curYearInt; i++) {
				remarkList.add(0.00);				
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_byyearMap");
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/transfer/doctor_topten_transfer_amount_byyearMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 当月出售处方总额-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/prescribeAmountByDay")
	public ModelAndView prescribeAmountByDayMap() {
		logBefore(logger, "DoctorTopTenDataController.prescribeAmountByDayMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_bydayMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		List<PageData> dayAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//今天处于这个月第几天
		SimpleDateFormat sdf = new SimpleDateFormat("d");
		int dayInt = Integer.parseInt(sdf.format(new Date()));
		//循环存储X轴数据
		for (int i = 1; i <= dayInt; i++) {
			dateList.add("'" + i + "日'");
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_bydayMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				dayAmountDoctorsList = doctorTopTenDataService.daySellPrescriptionAmountlistMap(pd);
				logOut(logger, "varList", dayAmountDoctorsList);
				//1日到今日的计数器
				int count = 1;
				//封装1日到今日的该医生的数据(若某日的数据空，则赋值0)
				for (int j = 0; j < dayAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(dayAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_bydayMap");
						return mv;
					}
					for (int m = count; m <= dayInt; m++) {
						if (Integer.parseInt(dayAmountDoctorsList.get(j).get("dat").toString().substring(8))==m) {
							list.add(dayAmountDoctorsList.get(j).get("chufang"));
							count++;								
							if (j != (dayAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = 1; i <= dayInt; i++) {
				remarkList.add(0.00);
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_bydayMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_bydayMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 月度出售处方总额-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/prescribeAmountByMonth")
	public ModelAndView prescribeAmountByMonthMap() {
		logBefore(logger, "DoctorTopTenDataController.prescribeAmountByMonthMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_bymonthMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		List<PageData> monthAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//当前月份
		SimpleDateFormat sdf = new SimpleDateFormat("M");
		int monthInt = Integer.parseInt(sdf.format(new Date()));
		//循环存储X轴数据
		for (int i = 1; i <= monthInt; i++) {
			dateList.add("'" + i + "月'");
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_bymonthMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				monthAmountDoctorsList = doctorTopTenDataService.monthSellPrescriptionAmountlistMap(pd);
				logOut(logger, "varList", monthAmountDoctorsList);
				//1月到当前月的计数器
				int count = 1;
				//封装1月到今月的该医生的数据(若某月的数据为空，则赋值0)
				for (int j = 0; j < monthAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(monthAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_bymonthMap");
						return mv;
					}
					for (int m = count; m <= monthInt; m++) {
						if (Integer.parseInt(monthAmountDoctorsList.get(j).get("dat").toString().substring(5))==m) {
							list.add(monthAmountDoctorsList.get(j).get("chufang"));
							count++;								
							if (j != (monthAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元 
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = 1; i <= monthInt; i++) {
				remarkList.add(0.00);				
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_bymonthMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_bymonthMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 年度出售处方总额-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/prescribeAmountByYear")
	public ModelAndView prescribeAmountByYearMap() {
		logBefore(logger, "DoctorTopTenDataController.prescribeAmountByYearMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_byyearMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> yearAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//今年年份
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		int curYearInt = Integer.parseInt(sdf.format(new Date()));
		try {
			//获取患者订单最早创建时间的年份
			PageData pdYear = doctorTopTenDataService.earliestYear(pd);
			if (IsNull.paramsIsNull(pdYear)) {
				mv.addObject("error", "系统数据错误");
				mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_byyearMap");
				return mv;
			}
			int earliestYearInt = Integer.parseInt(pdYear.get("earliestYear").toString());
			for (int i = earliestYearInt; i <= curYearInt; i++) {
				dateList.add("'" + i + "年'");
			}
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_byyearMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				yearAmountDoctorsList = doctorTopTenDataService.yearSellPrescriptionAmountlistMap(pd);
				logOut(logger, "varList", yearAmountDoctorsList);
				//"earliestYearInt"年到今年的计数器
				int count = earliestYearInt;
				//封装这几年该医生的数据(若某年的数据为空，则赋值0)
				for (int j = 0; j < yearAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(yearAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_byyearMap");
						return mv;
					}
					for (int m = count; m <= curYearInt; m++) {
						if (Integer.parseInt(yearAmountDoctorsList.get(j).get("dat").toString())==m) {
							list.add(yearAmountDoctorsList.get(j).get("chufang"));
							count++;								
							if (j != (yearAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元 
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = earliestYearInt; i <= curYearInt; i++) {
				remarkList.add(0.00);				
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_byyearMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/prescribe/doctor_topten_sell_prescription_amount_byyearMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 当月出售处方药品总额-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/sellDrugsAmountByDay")
	public ModelAndView sellDrugsAmountByDayMap() {
		logBefore(logger, "DoctorTopTenDataController.sellDrugsAmountByDayMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_bydayMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		List<PageData> dayAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//今天处于这个月第几天
		SimpleDateFormat sdf = new SimpleDateFormat("d");
		int dayInt = Integer.parseInt(sdf.format(new Date()));
		//循环存储X轴数据
		for (int i = 1; i <= dayInt; i++) {
			dateList.add("'" + i + "日'");
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_bydayMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				dayAmountDoctorsList = doctorTopTenDataService.daySellDrugsAmountlistMap(pd);
				logOut(logger, "varList", dayAmountDoctorsList);
				//1日到今日的计数器
				int count = 1;
				//封装1日到今日的该医生的数据(若某日的数据空，则赋值0)
				for (int j = 0; j < dayAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(dayAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_bydayMap");
						return mv;
					}
					for (int m = count; m <= dayInt; m++) {
						if (Integer.parseInt(dayAmountDoctorsList.get(j).get("dat").toString().substring(8))==m) {
							list.add(dayAmountDoctorsList.get(j).get("gouyao"));
							count++;								
							if (j != (dayAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = 1; i <= dayInt; i++) {
				remarkList.add(0.00);
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_bydayMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_bydayMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 月度出售处方药品总额-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/sellDrugsAmountByMonth")
	public ModelAndView sellDrugsAmountByMonthMap() {
		logBefore(logger, "DoctorTopTenDataController.sellDrugsAmountByMonthMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_bymonthMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		List<PageData> monthAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//当前月份
		SimpleDateFormat sdf = new SimpleDateFormat("M");
		int monthInt = Integer.parseInt(sdf.format(new Date()));
		//循环存储X轴数据
		for (int i = 1; i <= monthInt; i++) {
			dateList.add("'" + i + "月'");
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_bymonthMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				monthAmountDoctorsList = doctorTopTenDataService.monthSellDrugsAmountlistMap(pd);
				logOut(logger, "varList", monthAmountDoctorsList);
				//1月到当前月的计数器
				int count = 1;
				//封装1月到今月的该医生的数据(若某月的数据为空，则赋值0)
				for (int j = 0; j < monthAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(monthAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_bymonthMap");
						return mv;
					}
					for (int m = count; m <= monthInt; m++) {
						if (Integer.parseInt(monthAmountDoctorsList.get(j).get("dat").toString().substring(5))==m) {
							list.add(monthAmountDoctorsList.get(j).get("gouyao"));
							count++;								
							if (j != (monthAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元 
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = 1; i <= monthInt; i++) {
				remarkList.add(0.00);				
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_bymonthMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_bymonthMap");
			return mv;
		}
		logAfter(logger);
		return mv;
	}
	
	/**
	 * 年度出售处方药品总额-统计图表
	 * @param page
	 * @return ModelAndView
	 */
	@RequestMapping("/sellDrugsAmountByYear")
	public ModelAndView sellDrugsAmountByYearMap() {
		logBefore(logger, "DoctorTopTenDataController.sellDrugsAmountByYearMap()");
		//获取ModelAndView对象
		ModelAndView mv = this.getModelAndView();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
			mv.addObject("error","您没有查询权限");
			mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_byyearMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}
		//获取数据操作对象
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> yearAmountDoctorsList;
		//用于存储-X轴日期数据
		List<Object> dateList = new ArrayList<Object>();
		//今年年份
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		int curYearInt = Integer.parseInt(sdf.format(new Date()));
		try {
			//获取患者订单最早创建时间的年份
			PageData pdYear = doctorTopTenDataService.earliestYear(pd);
			if (IsNull.paramsIsNull(pdYear)) {
				mv.addObject("error", "系统数据错误");
				mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_byyearMap");
				return mv;
			}
			int earliestYearInt = Integer.parseInt(pdYear.get("earliestYear").toString());
			for (int i = earliestYearInt; i <= curYearInt; i++) {
				dateList.add("'" + i + "年'");
			}
			//根据医生ID，获取此医生的数据
			for (int i = 0; i < topTenDoctors.size(); i++) {
				//用于存储某个医生的全部数据
				List<Object> list = new ArrayList<Object>();
				//判断'名次'字段值是否为空
				if (IsNull.paramsIsNull(topTenDoctors.get(i).get("rowno"))) {
					mv.addObject("error", "系统数据错误");
					mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_byyearMap");
					return mv;
				}
				String rowno = topTenDoctors.get(i).get("rowno").toString();//名次
				rowno = rowno.substring(0, rowno.indexOf("."));//把名次中的小数部分".0"截取掉
				//获取名次=(i+1)的医生ID,封装到pd对象里
				pd.put("doctorId", topTenDoctors.get(i).get("doctorId"));
				//查询数据库名次=(i+1)的医生的今年总收入数据
				yearAmountDoctorsList = doctorTopTenDataService.yearSellDrugsAmountlistMap(pd);
				logOut(logger, "varList", yearAmountDoctorsList);
				//"earliestYearInt"年到今年的计数器
				int count = earliestYearInt;
				//封装这几年该医生的数据(若某年的数据为空，则赋值0)
				for (int j = 0; j < yearAmountDoctorsList.size(); j++) {
					if (IsNull.paramsIsNull(yearAmountDoctorsList.get(j).get("dat"))) {
						mv.addObject("error", "系统数据错误");
						mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_byyearMap");
						return mv;
					}
					for (int m = count; m <= curYearInt; m++) {
						if (Integer.parseInt(yearAmountDoctorsList.get(j).get("dat").toString())==m) {
							list.add(yearAmountDoctorsList.get(j).get("gouyao"));
							count++;								
							if (j != (yearAmountDoctorsList.size()-1)) {
								break;
							}
						}else{
							list.add(0.00);
							count++;						
						}
					}
				}
				//将查询出来的数据，返给前端
				mv.addObject("list"+(i+1),list);
				logOut(logger, "list"+(i+1), list);
			}
			//前10名医生人数，不够10人时，其他空的赋值0.00元 
			List<Object> remarkList = new ArrayList<Object>();
			for (int i = earliestYearInt; i <= curYearInt; i++) {
				remarkList.add(0.00);				
			}
			for (int j = topTenDoctors.size(); j < 10; j++) {
				mv.addObject("list" + (j+1), remarkList);
				logOut(logger, "list" + (j+1), remarkList);
			}
			mv.addObject("dateList", dateList);
			mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_byyearMap");
			mv.addObject(Const.SESSION_QX, this.getHC());
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject(Const.SESSION_QX, this.getHC());
			mv.addObject("error",e.getMessage());
			mv.setViewName("doctor/topTen/sellDrugs/doctor_topten_sell_drugs_amount_byyearMap");
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

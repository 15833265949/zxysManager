package com.fh.controller.patient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.service.patient.PatientActiveService;
import com.fh.util.Const;
import com.fh.util.IsNull;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;


/**
 * 
 * 创建人：郭立成 创建时间：2018-08-01
 *
 */
@Controller
@RequestMapping("/patientActive")
public class PatientActiveController extends BaseController {
	
	@Resource(name = "patientActiveService")
	private PatientActiveService patientService;

	// 根据患者分组查询当天的患者活跃记录
	@RequestMapping(value = "")
	public ModelAndView listDoctorActives(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		//将前端页面中检索中的USERNAME的值加入到pd中
		String USERNAME = pd.getString("USERNAME");
		if (null != USERNAME && !"".equals(USERNAME)) {
			USERNAME = USERNAME.trim();
			pd.put("USERNAME", USERNAME);
		}
		//加入当前时间
		Date date = new Date();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sif.format(date);
		pd.put("dayDate", dateString);

		page.setPd(pd);
		//分页查询复合条件的记录
		List<PageData> patientList = patientService.listPdPageDoctorActiveByDoctor(page);
		
		//查询活跃患者的总次数
		Integer activeNumberInteger = patientService.findActiveNumberBydayDate(page);
		if (IsNull.paramsIsNull(activeNumberInteger)) {
			activeNumberInteger = 0;
		}
		//查询活跃患者的总人数
		Integer parentsNum = patientService.findparentsNumBydayDate(page);
		if (IsNull.paramsIsNull(parentsNum)) {
			parentsNum = 0;
		}

		mv.setViewName("patient/patientActive/patientActive_list");
		mv.addObject("patientList", patientList);
		mv.addObject("activeNumberInteger", activeNumberInteger);
		mv.addObject("parentsNum", parentsNum);
		mv.addObject("pd", pd);
		mv.addObject(Const.SESSION_QX, this.getHC());
		return mv;
	}

	// 按患者查询导出Excel
	@RequestMapping(value = "/excel1")
	public ModelAndView exportExcel() {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {

			Date date = new Date();
			SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = sif.format(date);
			pd.put("dayDate", dateString);

			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<String> titles = new ArrayList<String>();

			titles.add("患者姓名"); // 1
			titles.add("性别"); // 2
			titles.add("电话"); // 3
			titles.add("日期"); // 4
			titles.add("活跃次数"); // 5

			dataMap.put("titles", titles);

			List<PageData> patientList = patientService.exportExcel1(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < patientList.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", patientList.get(i).getString("trueName")); // 1
				vpd.put("var2", patientList.get(i).get("sex").toString().equals("1")?"男":"女"); // 2
				vpd.put("var3", patientList.get(i).getString("phone")); // 3
				vpd.put("var4", patientList.get(i).getString("dayDate")); // 4
				vpd.put("var5", patientList.get(i).get("num").toString()); // 5
				varList.add(vpd);
			}
			dataMap.put("varList", varList);
			ObjectExcelView erv = new ObjectExcelView(); // 执行excel操作
			mv = new ModelAndView(erv, dataMap);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return mv;
	}

	// 根据时间段查询医生活跃人数
	@RequestMapping(value = "byTime")
	public ModelAndView listDoctorActivesbyTime(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();

		Date date = new Date();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sif.format(date);
		pd.put("dayDate", dateString);

		page.setPd(pd);
		//分页查询复合条件的记录
		List<PageData> patientList = patientService.listPdPagePatientActiveByTime(pd);
		System.out.println(patientList+"************************");
		
		//根据查询到的num列进行排序
		Stream<PageData> stream = patientList.stream();
		List<PageData> list2 = stream.sorted(PatientActiveController::comparator).collect(Collectors.toList());

		mv.setViewName("patient/patientActive/patientActive_listByTime");
		mv.addObject("patientList", list2);
		mv.addObject("pd", pd);
		mv.addObject(Const.SESSION_QX, this.getHC());
		return mv;
	}

	// 根据时间段查询详细医生信息
	@RequestMapping(value = "sendSumByTime")
	public ModelAndView sendSumByTime(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();

		Date date = new Date();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sif.format(date);

		pd.put("dayDate", dateString);
		
		//分页查询复合条件的记录
		List<PageData> patientList = patientService.listPdPagePatientActiveSendSumByTime(pd);
		

		mv.setViewName("patient/patientActive/patientActive_listBySendSum");
		mv.addObject("patientList", patientList);
		mv.addObject("pd", pd);
		mv.addObject(Const.SESSION_QX, this.getHC());
		return mv;
	}

	// 按时间查询导出Excel
	@RequestMapping(value = "/excel3")
	public ModelAndView exportExcel3() {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {

			Date date = new Date();

			SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");

			String dateString = sif.format(date);

			pd.put("dayDate", dateString);

			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<String> titles = new ArrayList<String>();

			titles.add("时间段"); // 1
			titles.add("活跃患者次数"); // 2
			dataMap.put("titles", titles);

			List<PageData> doctorList = patientService.listPdPagePatientActiveByTime(pd);
			Stream<PageData> stream = doctorList.stream();
			List<PageData> list2 = stream.sorted(PatientActiveController::comparator).collect(Collectors.toList());
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < list2.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", list2.get(i).getString("timeName")); // 1
				vpd.put("var2", list2.get(i).get("num").toString()); // 2
				varList.add(vpd);
			}
			dataMap.put("varList", varList);
			ObjectExcelView erv = new ObjectExcelView(); // 执行excel操作
			mv = new ModelAndView(erv, dataMap);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return mv;
	}

	// 根据地域分组查询患者活跃记录
	@RequestMapping(value = "/byArea")
	public ModelAndView listPatientActivesByArea(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String USERNAME = pd.getString("USERNAME");
		if (null != USERNAME && !"".equals(USERNAME)) {
			USERNAME = USERNAME.trim();
			pd.put("USERNAME", USERNAME);
		}
		Date date = new Date();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sif.format(date);
		pd.put("dayDate", dateString);

		page.setPd(pd);
		//查询符合条件的记录
		List<PageData> patientList = patientService.listPagePatientActiveByArea(page);
		
		//如果记录中的region列为空，那么去除这列
		if (!IsNull.paramsIsNull(patientList)) {
			for (PageData pageData : patientList) {
				if (IsNull.paramsIsNull(pageData.get("region"))) {
					patientList.remove(pageData);
					break;
				}
			}
		}

		mv.setViewName("patient/patientActive/patientActive_listByArea");
		mv.addObject("patientList", patientList);
		mv.addObject("pd", pd);
		mv.addObject(Const.SESSION_QX, this.getHC());
		return mv;
	}

	// 按地域查询导出Excel
	@RequestMapping(value = "/excelArea")
	public ModelAndView exportexcelArea() {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {

			String USERNAME = pd.getString("USERNAME");
			if (null != USERNAME && !"".equals(USERNAME)) {
				USERNAME = USERNAME.trim();
				pd.put("USERNAME", USERNAME);
			}

			Date date = new Date();

			SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");

			String dateString = sif.format(date);

			pd.put("dayDate", dateString);

			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<String> titles = new ArrayList<String>();

			titles.add("省"); // 1
			titles.add("市"); // 2
			titles.add("患者活跃数"); // 4

			dataMap.put("titles", titles);

			List<PageData> doctorList = patientService.exportExcel(pd);

			if (!IsNull.paramsIsNull(doctorList)) {
				for (PageData pageData : doctorList) {
					if (IsNull.paramsIsNull(pageData.get("region"))) {
						doctorList.remove(pageData);
						break;
					}
				}
			}
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < doctorList.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", doctorList.get(i).getString("province")); // 1
				vpd.put("var2", doctorList.get(i).getString("region")); // 2
				vpd.put("var3", doctorList.get(i).get("num").toString()); // 4
				varList.add(vpd);
			}
			dataMap.put("varList", varList);
			ObjectExcelView erv = new ObjectExcelView(); // 执行excel操作
			mv = new ModelAndView(erv, dataMap);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return mv;
	}

	//
	// 排序算法
	public static int comparator(PageData map1, PageData map2) {
		if (map1 == null && map2 == null)
			return 0;

		if (map1 == null || map2 == null) {
			throw new NullPointerException();
		}

		int age1 = Integer.valueOf(map1.get("num").toString());
		int age2 = Integer.valueOf(map2.get("num").toString());

		return age2 - age1;
	}

	public Map<String, String> getHC() {
		Subject currentUser = SecurityUtils.getSubject(); // shiro管理的session
		Session session = currentUser.getSession();
		return (Map<String, String>) session.getAttribute(Const.SESSION_QX);
	}
}

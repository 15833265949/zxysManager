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
import com.fh.service.patient.PatientDrainageService;
import com.fh.service.patient.PatientInformationService;
import com.fh.util.Const;
import com.fh.util.IsNull;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;

import oracle.net.aso.a;


/**
 * 
 * 创建人：郭立成 创建时间：2018-08-02
 *
 */
@Controller
@RequestMapping("/patientDrainage")
public class PatientDrainageController extends BaseController {

	@Resource(name = "patientDrainageService")
	private PatientDrainageService patientDrainageService;
	
	// 患者分组查询当天引流数
	@RequestMapping(value = "")
	public ModelAndView listDoctorActives(Page page) throws Exception {
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

		List<PageData> patientList = patientDrainageService.listPdPagePatientDrainage(page);
		
		Integer allNumber = patientDrainageService.findDrainageAllNumber(page);
		if (IsNull.paramsIsNull(allNumber)) {
			allNumber = 0;
		}
//		Integer perfectNumber = patientDrainageService.findDrainagePerfectNumber(page);
//		if (IsNull.paramsIsNull(perfectNumber)) {
//			perfectNumber = 0;
//		}

		mv.setViewName("patient/patientDrainage/patientDrainage_list");
		mv.addObject("patientList", patientList);
		mv.addObject("allNumber", allNumber);
//		mv.addObject("perfectNumber", perfectNumber);
		mv.addObject("pd", pd);
		mv.addObject(Const.SESSION_QX, this.getHC());
		return mv;
	}
	
	//根据日期查询详细引流患者信息
		@RequestMapping(value = "/sendDrainage")
		public ModelAndView sendSum(Page page) throws Exception {
			ModelAndView mv = this.getModelAndView();
			PageData pd = new PageData();
			pd = this.getPageData();
			
			
			Date date = new Date();

			SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");

			String dateString = sif.format(date);

			pd.put("dayDate", dateString);
			List<PageData> patientList = patientDrainageService.listPdPagePatientByDate(pd);
			

			mv.setViewName("patient/patientDrainage/patientDrainage_listBySendDrainage");
			mv.addObject("patientList", patientList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX, this.getHC());
			return mv;
		}

	// 导出Excel
	@RequestMapping(value = "/excel")
	public ModelAndView exportExcel() {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {

			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<String> titles = new ArrayList<String>();

			titles.add("日期"); // 1
			titles.add("引流数"); // 2

			dataMap.put("titles", titles);

			List<PageData> patientList = patientDrainageService.exportExcel(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < patientList.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", patientList.get(i).getString("date")); // 1
				vpd.put("var2", patientList.get(i).get("num").toString()); // 2
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
	public Map<String, String> getHC() {
		Subject currentUser = SecurityUtils.getSubject(); // shiro管理的session
		Session session = currentUser.getSession();
		return (Map<String, String>) session.getAttribute(Const.SESSION_QX);
	}
}

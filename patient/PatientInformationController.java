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
import com.fh.service.patient.PatientInformationService;
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
@RequestMapping("/patientInformation")
public class PatientInformationController extends BaseController {

	@Resource(name = "patientInformationService")
	private PatientInformationService patientInformationService;

	// 根据患者分组查询当天的患者活跃记录
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

		page.setPd(pd);

		List<PageData> patientList = patientInformationService.listPdPagePatient(page);

		Integer allNumber = patientInformationService.findInformationAllNumber(page);
		if (IsNull.paramsIsNull(allNumber)) {
			allNumber = 0;
		}
		Integer perfectNumber = patientInformationService.findInformationPerfectNumber(page);
		if (IsNull.paramsIsNull(perfectNumber)) {
			perfectNumber = 0;
		}

		mv.setViewName("patient/patientInformation/patientInformation_list");
		mv.addObject("patientList", patientList);
		mv.addObject("allNumber", allNumber);
		mv.addObject("perfectNumber", perfectNumber);
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

			titles.add("患者姓名"); // 1
			titles.add("性别"); // 2
			titles.add("年龄"); // 3
			titles.add("电话"); // 4
			titles.add("既往史"); // 5

			dataMap.put("titles", titles);

			List<PageData> patientList = patientInformationService.exportExcel(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < patientList.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", patientList.get(i).getString("trueName")); // 1
				vpd.put("var2", patientList.get(i).get("sex").toString()); // 2
				vpd.put("var3", patientList.get(i).get("age").toString()); // 3
				vpd.put("var4", patientList.get(i).get("phone").toString()); // 4
				vpd.put("var5", patientList.get(i).get("patientMedicalHistory").toString()); // 5
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

	public Map<String, String> getHC() {
		Subject currentUser = SecurityUtils.getSubject(); // shiro管理的session
		Session session = currentUser.getSession();
		return (Map<String, String>) session.getAttribute(Const.SESSION_QX);
	}
}

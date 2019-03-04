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
import com.fh.service.patient.PatientEvaluationService;
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
@RequestMapping("/patientEvaluation")
public class PatientEvaluationController extends BaseController {

	@Resource(name = "patientEvaluationService")
	private PatientEvaluationService patientEvaluationService;

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

		Date date = new Date();

		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");

		String dateString = sif.format(date);

		pd.put("dayDate", dateString);

		page.setPd(pd);

		List<PageData> patientList = patientEvaluationService.listPdPagePatientEvaluation(page);

		Integer allNumber = patientEvaluationService.findEvaluationNumber(page);
		if (IsNull.paramsIsNull(allNumber)) {
			allNumber = 0;
		}

		mv.setViewName("patient/patientEvaluation/patientEvaluation_list");
		mv.addObject("patientList", patientList);
		mv.addObject("allNumber", allNumber);
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
		
		Date date = new Date();

		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");

		String dateString = sif.format(date);

		pd.put("dayDate", dateString);

		try {

			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<String> titles = new ArrayList<String>();

			titles.add("患者姓名"); // 1
			titles.add("评价医生姓名"); // 2
			titles.add("评价时间"); // 2
			titles.add("评价内容"); // 2
			titles.add("是否打赏"); // 2
			titles.add("打赏金额(单位/元)"); // 2

			dataMap.put("titles", titles);

			List<PageData> patientList = patientEvaluationService.exportExcel(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < patientList.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", patientList.get(i).getString("patientName")); // 1
				vpd.put("var2", patientList.get(i).getString("doctorName")); // 2
				vpd.put("var3", patientList.get(i).get("pingTime").toString()); // 2
				vpd.put("var4", patientList.get(i).getString("pingContent")); // 2
				vpd.put("var5", patientList.get(i).get("isShang").toString().equals("0")  ? "未打赏" : "以打赏"); // 2
				vpd.put("var6", patientList.get(i).getString("shangMoney")); // 2.toString().equals("0")  ? "未打赏" : "以打赏"
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

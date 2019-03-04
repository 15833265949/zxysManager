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
import com.fh.service.patient.PatientDownloadService;
import com.fh.util.Const;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;

/**
 * 
 * 创建人：郭立成 创建时间：2018-08-02
 *
 */
@Controller
@RequestMapping("/patientDownload")
public class PatientDownloadController extends BaseController {

	@Resource(name = "patientDownloadService")
	private PatientDownloadService patientDownloadService;

	// 患者端下载之后无任何操作边退出的数量
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
		
		//查询所有复合条件的记录
		List<PageData> patientList = patientDownloadService.listPdPagePatientDownload(page);
		//查询复合条件记录的数量
		PageData allNum = patientDownloadService.findallNum(page);

		mv.setViewName("patient/patientDownload/patientDownload_list");
		mv.addObject("patientList", patientList);
		mv.addObject("num", allNum);
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

			titles.add("日期"); // 1
			titles.add("当日下载数量"); // 2
			titles.add("当日注册数量"); // 2
			titles.add("当日下载后无任何操作便退出数量"); // 2

			dataMap.put("titles", titles);

			List<PageData> patientList = patientDownloadService.exportExcel(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < patientList.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", patientList.get(i).getString("dayDate")); // 1
				vpd.put("var2", patientList.get(i).get("downNumber").toString()); // 2
				vpd.put("var3", patientList.get(i).get("registerNumber").toString()); // 2
				vpd.put("var4", String.valueOf(Integer.valueOf(patientList.get(i).get("downNumber").toString())
						- Integer.valueOf(patientList.get(i).get("registerNumber").toString()))); // 2

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

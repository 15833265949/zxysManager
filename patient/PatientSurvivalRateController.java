package com.fh.controller.patient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.service.patient.NewPatientLoginWayService;
import com.fh.service.patient.PatientSurvivalRateService;
import com.fh.util.IsNull;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;

@Controller
@RequestMapping("/patientSurvivalRate")
public class PatientSurvivalRateController extends BaseController{
	@Resource(name = "patientSurvivalRateService")
	private PatientSurvivalRateService patientSurvivalRateService;
	// 根据患者分组查询当天的患者活跃记录
		@RequestMapping(value = "")
		public ModelAndView listDoctorActives(Page page) throws Exception {
			ModelAndView mv = this.getModelAndView();
			PageData pd = new PageData();
			pd = this.getPageData();
			//将前端页面中检索中的USERNAME的值加入到pd中
			//加入当前时间
			Date date = new Date();
			SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = sif.format(date);
			//lastLoginStart,lastLoginEnd
			if (IsNull.paramsIsNull(pd.get("lastLoginStart"))) {
				pd.put("lastLoginStart", dateString);
			}
			if (IsNull.paramsIsNull(pd.get("lastLoginEnd"))) {
				pd.put("lastLoginEnd", dateString);
			}
			pd.put("dayDate", dateString);
			System.out.println("--------------pd:"+pd);
			page.setPd(pd);
			//分页查询复合条件的记录
			List<PageData> patientList = patientSurvivalRateService.listNewPatientLoginWay(page);
			System.out.println("--------patientList:"+patientList);

			mv.setViewName("patient/PatientSurvivalRate/patientSurvivalRate_list");
			mv.addObject("patientList", patientList);
			mv.addObject("pd", pd);
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

				List<PageData> patientList = patientSurvivalRateService.exportExcel(pd);
				List<PageData> varList = new ArrayList<PageData>();
				for (int i = 0; i < patientList.size(); i++) {
					PageData vpd = new PageData();
					vpd.put("var1", patientList.get(i).getString("trueName")); // 1
					vpd.put("var2", patientList.get(i).get("sex").toString()); // 2
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


}

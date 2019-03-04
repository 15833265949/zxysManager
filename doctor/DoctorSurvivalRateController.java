package com.fh.controller.doctor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.service.doctor.DoctorSurvivalRateService;
import com.fh.service.patient.PatientSurvivalRateService;
import com.fh.util.IsNull;
import com.fh.util.PageData;

@Controller
@RequestMapping("/doctorSurvivalRate")
public class DoctorSurvivalRateController extends BaseController{
	@Resource(name = "doctorSurvivalRateService")
	private DoctorSurvivalRateService doctorSurvivalRateService;
	
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
		List<PageData> patientList = doctorSurvivalRateService.listNewPatientLoginWay(page);
		System.out.println("--------patientList:"+patientList);

		mv.setViewName("doctor/DoctorSurvivalRate/doctorSurvivalRate_list");
		mv.addObject("patientList", patientList);
		mv.addObject("pd", pd);
		return mv;
	}
}

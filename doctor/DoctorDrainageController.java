package com.fh.controller.doctor;

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
import com.fh.service.doctor.DoctorDrainageService;
import com.fh.util.Const;
import com.fh.util.IsNull;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;

@Controller
@RequestMapping("/doctorDrainage")
public class DoctorDrainageController extends BaseController {

		@Resource(name = "DoctorDrainageService")
		private DoctorDrainageService doctorDrainageService;
		
		// 医生分组查询当天引流数
		@RequestMapping(value = "")
		public ModelAndView listDoctorActives(Page page) throws Exception {
			ModelAndView mv = this.getModelAndView();
			PageData pd = new PageData();
			pd = this.getPageData();
			//开始日期,条件查询
			String lastLoginStart = pd.getString("lastLoginStart");
			//结束日期,条件查询
			String lastLoginEnd = pd.getString("lastLoginEnd");
			//对接收的前端参数，进行判空；如果所有参数都为空，默认日期为当前系统日期
			if(!IsNull.paramsIsNull(lastLoginStart)){
				pd.put("lastLoginStart", lastLoginStart.trim());
			}
			if(!IsNull.paramsIsNull(lastLoginEnd)){
				pd.put("lastLoginEnd", lastLoginEnd.trim());
			}
			if(IsNull.paramsIsNull(lastLoginStart) && IsNull.paramsIsNull(lastLoginEnd)){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					pd.put("lastLoginStart", sdf.format(new Date()));
					pd.put("lastLoginEnd", sdf.format(new Date()));
			}


			page.setPd(pd);

			List<PageData> doctorList = doctorDrainageService.listPdPagePatientDrainage(page);
			Integer allNumber = doctorDrainageService.findDrainageAllNumber(page);
			if (IsNull.paramsIsNull(allNumber)) {
				allNumber = 0;
			}
			mv.setViewName("doctor/doctorDrainage/doctorDrainage_list");
			mv.addObject("doctorList", doctorList);
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
			try {

				Map<String, Object> dataMap = new HashMap<String, Object>();
				List<String> titles = new ArrayList<String>();

				titles.add("日期"); // 1
				titles.add("注册人数"); // 2
				titles.add("登录人数"); // 3

				dataMap.put("titles", titles);

				List<PageData> doctorList = doctorDrainageService.exportExcel(pd);
				List<PageData> varList = new ArrayList<PageData>();
				for (int i = 0; i < doctorList.size(); i++) {
					PageData vpd = new PageData();
					vpd.put("var1", doctorList.get(i).getString("date")); // 1
					vpd.put("var2", doctorList.get(i).get("num").toString()); // 2
					vpd.put("var3", doctorList.get(i).get("num").toString()); // 3
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
		@SuppressWarnings("unchecked")
		public Map<String, String> getHC() {
			Subject currentUser = SecurityUtils.getSubject(); // shiro管理的session
			Session session = currentUser.getSession();
			return (Map<String, String>) session.getAttribute(Const.SESSION_QX);
		}
}

package com.fh.controller.doctor;

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
import com.fh.service.doctor.DoctorActiveService;
import com.fh.util.Const;
import com.fh.util.IsNull;
import com.fh.util.Jurisdiction;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;

/**
 * 
 * 创建人：郭立成 创建时间：2018-07-31
 *
 */
@Controller
@RequestMapping("/doctorActive")
public class DoctorActiveController extends BaseController {

	@Resource(name = "doctorActiveService")
	private DoctorActiveService doctorService;

	// 根据医生分组查询当天的医生活跃记录
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
		// 当前日期
		Date date = new Date();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sif.format(date);
		pd.put("dayDate", dateString);

		pd.put("unName", "石家庄知心诊所");
		page.setPd(pd);

		// 分页查询复合条件记录
		List<PageData> doctorList = doctorService.listPdPageDoctorActiveByDoctor(page);

		// 查询医生活跃总次数
		Integer activeNumberInteger = doctorService.findActiveNumberBydayDate(page);
		if (IsNull.paramsIsNull(activeNumberInteger)) {
			activeNumberInteger = 0;
		}

		// 查询活跃医生总人数
		Integer parentsNum = doctorService.findparentsNumBydayDate(page);
		if (IsNull.paramsIsNull(parentsNum)) {
			parentsNum = 0;
		}

		mv.setViewName("doctor/active/doctorActive_list");
		mv.addObject("doctorList", doctorList);
		mv.addObject("activeNumberInteger", activeNumberInteger);
		mv.addObject("parentsNum", parentsNum);
		mv.addObject("pd", pd);
		mv.addObject(Const.SESSION_QX, this.getHC());
		return mv;
	}

	// 根据科室查询活跃医生数量
	@RequestMapping(value = "/dept")
	public ModelAndView listDoctorActivesByDept(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		// 当前日期
		Date date = new Date();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sif.format(date);
		pd.put("dayDate", dateString);
		pd.put("unName", "石家庄知心诊所");
		page.setPd(pd);

		// 分页查询复合条件记录
		List<PageData> doctorList = doctorService.listPdPageDoctorActiveByDept(page);

		// 如果科室为空，怎去除这条记录
		for (PageData pageData : doctorList) {
			if (IsNull.paramsIsNull(pageData.get("section"))) {
				doctorList.remove(pageData);
				break;
			}

		}
		// Integer activeNumberInteger =
		// doctorService.findActiveNumberBydayDate(page);
		// if (activeNumberInteger == null && "".equals(activeNumberInteger)) {
		// activeNumberInteger = 0;
		// }

		mv.setViewName("doctor/active/doctorActive_listByPtmt");
		mv.addObject("doctorList", doctorList);
		// mv.addObject("activeNumberInteger", activeNumberInteger);
		mv.addObject("pd", pd);
		mv.addObject(Const.SESSION_QX, this.getHC());
		return mv;
	}

	// 根据科室查询详细医生信息
	@RequestMapping(value = "/sendSum")
	public ModelAndView sendSum(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		// 当前日期
		Date date = new Date();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sif.format(date);
		pd.put("dayDate", dateString);
		pd.put("unName", "石家庄知心诊所");
		// 分页查询符合条件的记录
		List<PageData> doctorList = doctorService.listPdPageDoctorActiveBySection(pd);

		mv.setViewName("doctor/active/doctorActive_listBySendSum");
		mv.addObject("doctorList", doctorList);
		mv.addObject("pd", pd);
		mv.addObject(Const.SESSION_QX, this.getHC());
		return mv;
	}

	// 根据时间段查询医生活跃人数
	@RequestMapping(value = "byTime")
	public ModelAndView listDoctorActivesbyTime(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		// 当前日期
		Date date = new Date();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sif.format(date);
		pd.put("dayDate", dateString);
		pd.put("unName", "石家庄知心诊所");
		page.setPd(pd);

		// 分页查询符合条件的记录
		List<PageData> doctorList = doctorService.listPdPageDoctorActiveByTime(pd);

		doctorList.forEach(a -> System.out.println(a + "-=-=-="));
		List<PageData> list2 = null;
		if (!IsNull.paramsIsNull(doctorList)) {

			// 根据num列进行排序
			Stream<PageData> stream = doctorList.stream();
			list2 = stream.sorted(DoctorActiveController::comparator).collect(Collectors.toList());
		}

		mv.setViewName("doctor/active/doctorActive_listByTime");
		mv.addObject("doctorList", list2);
		// mv.addObject("activeNumberInteger", activeNumberInteger);
		mv.addObject("pd", pd);
		mv.addObject(Const.SESSION_QX, this.getHC());
		return mv;
	}

	public Map<String, String> getHC() {
		Subject currentUser = SecurityUtils.getSubject(); // shiro管理的session
		Session session = currentUser.getSession();
		return (Map<String, String>) session.getAttribute(Const.SESSION_QX);
	}

	// 根据时间段查询详细医生信息
	@RequestMapping(value = "sendSumByTime")
	public ModelAndView sendSumByTime(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		// 当前时间
		Date date = new Date();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sif.format(date);
		pd.put("dayDate", dateString);
		pd.put("unName", "石家庄知心诊所");
		// 分页查询符合条件的记录
		List<PageData> doctorList = doctorService.listPdPageDoctorActiveSendSumByTime(pd);

		mv.setViewName("doctor/active/doctorActive_listBySendSum");
		mv.addObject("doctorList", doctorList);
		mv.addObject("pd", pd);
		mv.addObject(Const.SESSION_QX, this.getHC());
		return mv;
	}

	// 导出Excel
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
			pd.put("unName", "石家庄知心诊所");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<String> titles = new ArrayList<String>();

			titles.add("医生姓名"); // 1
			titles.add("电话"); // 2
			titles.add("所在机构名称"); // 3
			titles.add("日期"); // 4
			titles.add("活跃次数"); // 5

			dataMap.put("titles", titles);

			List<PageData> doctorList = doctorService.exportExcel1(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < doctorList.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", doctorList.get(i).getString("trueName")); // 1
				vpd.put("var2", doctorList.get(i).getString("phone")); // 2
				vpd.put("var3", doctorList.get(i).getString("clinicName")); // 3
				vpd.put("var4", doctorList.get(i).getString("dayDate")); // 4
				vpd.put("var5", doctorList.get(i).get("num").toString()); // 5
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

	// 导出Excel
	@RequestMapping(value = "/excel2")
	public ModelAndView exportExcel2() {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {

			Date date = new Date();

			SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");

			String dateString = sif.format(date);

			pd.put("dayDate", dateString);
			pd.put("unName", "石家庄知心诊所");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<String> titles = new ArrayList<String>();

			titles.add("医生所在科室名称"); // 1
			titles.add("日期"); // 2
			titles.add("医生活跃总数"); // 3
			dataMap.put("titles", titles);

			List<PageData> doctorList = doctorService.exportExcel2(pd);
			for (PageData pageData : doctorList) {
				if (IsNull.paramsIsNull(pageData.get("section"))) {
					doctorList.remove(pageData);
					break;
				}

			}
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < doctorList.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", doctorList.get(i).getString("section")); // 1
				vpd.put("var2", doctorList.get(i).getString("dayDate")); // 2
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

	// 导出Excel
	@RequestMapping(value = "/excel3")
	public ModelAndView exportExcel3() {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {

			Date date = new Date();

			SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");

			String dateString = sif.format(date);
			pd.put("unName", "石家庄知心诊所");
			pd.put("dayDate", dateString);

			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<String> titles = new ArrayList<String>();

			titles.add("时间段"); // 1
			titles.add("活跃医生人数"); // 2
			dataMap.put("titles", titles);

			List<PageData> doctorList = doctorService.listPdPageDoctorActiveByTime(pd);
			Stream<PageData> stream = doctorList.stream();
			List<PageData> list2 = stream.sorted(DoctorActiveController::comparator).collect(Collectors.toList());
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

}

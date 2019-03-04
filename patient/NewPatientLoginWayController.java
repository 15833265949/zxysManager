package com.fh.controller.patient;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.fh.service.patient.PatientActiveService;
import com.fh.util.Const;
import com.fh.util.IsNull;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;
/**
 * 新增患者登录
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/patientLoginWay")
public class NewPatientLoginWayController extends BaseController {
	@Resource(name = "newPatientLoginWayService")
	private NewPatientLoginWayService newPatientLoginWayService;
	// 年度患者登录方式
	@RequestMapping(value = "")
	public ModelAndView listDoctorActives(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Date date = new Date();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sif.format(date);
		pd.put("dayDate", dateString);
		page.setPd(pd);
		List<PageData> patientList = newPatientLoginWayService.listYear(page);
		mv.setViewName("patient/NewPatientLoginWay/newPatientLoginWay_list");
		System.out.println("*******patientList***year*****:"+patientList);
		List<Object> list1 = new ArrayList<Object>();
		List<Object> list2 = new ArrayList<Object>();
		List<Object> list3 = new ArrayList<Object>();
		List<Object> list4 = new ArrayList<Object>();
		for (int i = 0; i < patientList.size(); i++) {
			list1.add(patientList.get(i).get("DATE"));
			list2.add(patientList.get(i).get("phone"));
			list3.add(patientList.get(i).get("wxopenId"));
			list4.add(patientList.get(i).get("qqopenId"));
		}
		mv.addObject("list1", list1);
		mv.addObject("list2", list2);
		mv.addObject("list3", list3);
		mv.addObject("list4", list4);
		mv.addObject("pd", pd);
		return mv;
	}
	// 月度患者登录方式
	@RequestMapping(value = "/month.do")
	public ModelAndView listMonth(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Date date = new Date();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sif.format(date);
		pd.put("dayDate", dateString);
		page.setPd(pd);
		List<PageData> patientList = newPatientLoginWayService.listMonth(page);
		mv.setViewName("patient/NewPatientLoginWay/newPatientLoginWay_list_byMonth");
		System.out.println("*******patientList****month****:"+patientList);
		List<Object> list1 = new ArrayList<Object>();
		List<Object> list2 = new ArrayList<Object>();
		List<Object> list3 = new ArrayList<Object>();
		List<Object> list4 = new ArrayList<Object>();
		for (int i = 0; i < patientList.size(); i++) {
			list1.add("'" + patientList.get(i).get("DATE") + "'");
			list2.add(patientList.get(i).get("phone"));
			list3.add(patientList.get(i).get("wxopenId"));
			list4.add(patientList.get(i).get("qqopenId"));
		}
		mv.addObject("list1", list1);
		mv.addObject("list2", list2);
		mv.addObject("list3", list3);
		mv.addObject("list4", list4);
		mv.addObject("pd", pd);
		return mv;
	}
	// 当月患者登录方式
	@RequestMapping(value = "/day.do")
	public ModelAndView listDay(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Date date = new Date();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sif.format(date);
		pd.put("dayDate", dateString);
		page.setPd(pd);
		List<PageData> patientList = newPatientLoginWayService.listDay(page);
		mv.setViewName("patient/NewPatientLoginWay/newPatientLoginWay_list_byDay");
		System.out.println("*******patientList*****DAY********:"+patientList);
		List<Object> list1 = new ArrayList<Object>();
		List<Object> list2 = new ArrayList<Object>();
		List<Object> list3 = new ArrayList<Object>();
		List<Object> list4 = new ArrayList<Object>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		SimpleDateFormat sdf = new SimpleDateFormat("d");
		int dayInt = Integer.parseInt(sdf.format(new Date()));
		
		List<PageData> returnList = new ArrayList<PageData>();
		PageData data = new PageData();
		data.put("phone", 0);
		data.put("wxopenId", 0);
		data.put("qqopenId", 0);
		// 首先对没有的日期进行补齐
		int index=0;
		Calendar cale = Calendar.getInstance();
		for (int i = 0; i < dayInt; i++) {
			cale.set(Calendar.DAY_OF_MONTH, (i+1));
			String	tempDate = format.format(cale.getTime());
			if (index<patientList.size()&&tempDate.equals(patientList.get(index).get("DATE"))) {// 相等的话，则证明存在
				System.out.println(tempDate+"+"+patientList.get(index)+"+"+index+"+"+i);
				returnList.add(patientList.get(index));
				index=index+1;
				
			}else {//否则的话传入一个空
				data.put("DATE", tempDate);
				returnList.add(data);
			}
		}
		System.out.println("到这里了吗");
		for (int i = 0; i < returnList.size(); i++) {
			System.out.println("WC+"+i+":"+returnList.get(i));
		}
		System.out.println("*************结束***********");
		List<PageData> PageDataslist = new ArrayList<PageData>();
		int c = dayInt/5;
		int k=0;
		for (int i = 0; i < c; i++) {
			BigDecimal phone = new BigDecimal(0);
			BigDecimal wxopenId = new BigDecimal(0);
			BigDecimal qqopenId = new BigDecimal(0);
			//System.out.println("HH:"+(i*5)+"-"+((i+1)*5-1));
			for (int j = (i*5); j <= ((i+1)*5-1); j++) {
				System.out.println("这是第"+j+"个");
				phone = phone.add(new BigDecimal(returnList.get(j).get("phone").toString()));
				wxopenId = wxopenId.add(new BigDecimal(returnList.get(j).get("wxopenId").toString()));
				qqopenId = qqopenId.add(new BigDecimal(returnList.get(j).get("qqopenId").toString()));
			}
			PageData pageData = new PageData();
			pageData.put("DATE", (i*5+1)+"-"+((i+1)*5));
			pageData.put("phone", phone);
			pageData.put("wxopenId", wxopenId);
			pageData.put("qqopenId", qqopenId);
			PageDataslist.add(pageData);
			k=(i+1)*5;
		}
		if (k!=dayInt) {
			BigDecimal phone = new BigDecimal(0);
			BigDecimal wxopenId = new BigDecimal(0);
			BigDecimal qqopenId = new BigDecimal(0);
			// System.out.println("HH:"+(k)+"-"+(dayInt-1));
			for (int i = k; i <= (dayInt-1); i++) {
				System.out.println("这是第"+i+"个");
				phone = phone.add(new BigDecimal(returnList.get(i).get("phone").toString()));
				wxopenId = wxopenId.add(new BigDecimal(returnList.get(i).get("wxopenId").toString()));
				qqopenId = qqopenId.add(new BigDecimal(returnList.get(i).get("qqopenId").toString()));
			}
			PageData pageData = new PageData();
			pageData.put("DATE", (k+1)+"-"+(dayInt));
			pageData.put("phone", phone);
			pageData.put("wxopenId", wxopenId);
			pageData.put("qqopenId", qqopenId);
			PageDataslist.add(pageData);
		}
		
		for (int i = 0; i < PageDataslist.size(); i++) {
			System.out.println("**********PageDataslist***********:"+PageDataslist.get(i));
			list1.add("'" + PageDataslist.get(i).get("DATE") + "'");
			list2.add(PageDataslist.get(i).get("phone"));
			list3.add(PageDataslist.get(i).get("wxopenId"));
			list4.add(PageDataslist.get(i).get("qqopenId"));
		}
		mv.addObject("list1", list1);
		System.out.println("list1"+list1);
		mv.addObject("list2", list2);
		System.out.println("list2"+list2);
		mv.addObject("list3", list3);
		System.out.println("list3"+list3);
		mv.addObject("list4", list4);
		mv.addObject("pd", pd);
		return mv;
	}
}

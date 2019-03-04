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
import com.fh.service.doctor.DoctorActiveAreaService;
import com.fh.service.doctor.DoctorShareService;
import com.fh.util.Const;
import com.fh.util.IsNull;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;

/**
 * 
 * 创建人：郭立成
 * 创建时间：2018-08-03
 *
 */
@Controller
@RequestMapping("/doctorShare")
public class DoctorShareController extends BaseController {

	@Resource(name="doctorShareService")
	private DoctorShareService doctorShareService;

	// 统计医生分享软件情况
	@RequestMapping(value = "")
	public ModelAndView listDoctorActives(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String USERNAME = pd.getString("USERNAME");
		if(null != USERNAME && !"".equals(USERNAME)){
			USERNAME = USERNAME.trim();
			pd.put("USERNAME", USERNAME);
		}
		//当前日期
		Date date = new Date();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sif.format(date);
		pd.put("dayDate", dateString);
		pd.put("unName", "石家庄知心诊所");
		page.setPd(pd);
		
		//分页查询复合条件记录
		List<PageData> doctorList = doctorShareService.listPageDoctorActiveByArea(page);
		
		
		mv.setViewName("doctor/doctorShare/doctorShare_list");
		mv.addObject("doctorList", doctorList);
		mv.addObject("pd", pd);
		mv.addObject(Const.SESSION_QX, this.getHC());
		return mv;
	}
	
	

	public Map<String, String> getHC() {
		Subject currentUser = SecurityUtils.getSubject(); // shiro管理的session
		Session session = currentUser.getSession();
		return (Map<String, String>) session.getAttribute(Const.SESSION_QX);
	}
	
	
	//导出Excel
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel(){
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try{
			
			String USERNAME = pd.getString("USERNAME");
			if(null != USERNAME && !"".equals(USERNAME)){
				USERNAME = USERNAME.trim();
				pd.put("USERNAME", USERNAME);
			}
				
			Date date = new Date();
			SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = sif.format(date);
			pd.put("dayDate", dateString);
			pd.put("unName", "石家庄知心诊所");
			Map<String,Object> dataMap = new HashMap<String,Object>();
			List<String> titles = new ArrayList<String>();
			
			titles.add("医生姓名"); 		//1
			titles.add("时间内分享次数");  		//2
			
			dataMap.put("titles", titles);

			List<PageData> doctorList = doctorShareService.exportExcel(pd);
			
			
				List<PageData> varList = new ArrayList<PageData>();
				for(int i=0;i<doctorList.size();i++){
					PageData vpd = new PageData();
					vpd.put("var1", doctorList.get(i).getString("trueName"));		//1
					vpd.put("var2", doctorList.get(i).get("num").toString());	//4
					varList.add(vpd);
				}
				dataMap.put("varList", varList);
				ObjectExcelView erv = new ObjectExcelView();					//执行excel操作
				mv = new ModelAndView(erv,dataMap);
		} catch(Exception e){
			logger.error(e.toString(), e);
		}
		return mv;
	}
	
	
}

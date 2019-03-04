package com.fh.controller.doctor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.service.doctor.DoctorFunctionService;
import com.fh.util.Const;
import com.fh.util.Jurisdiction;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;
/**
 * 
 * @author 张东东
 * 医生端软件功能使用情况统计
 */
@Controller
@RequestMapping(value="/doctorFunction")
public class DoctorFunctionController extends BaseController{
	String menuUrl = "doctorFunction/list.do"; //对应统计发布动态医生界面
	@Resource(name="doctorFunctionService")
	private DoctorFunctionService doctorFunctionService;
	
	
	/**
	 * 查询一段时间内发布动态的总人数以及所占活跃人数的比例,还有发布动态的医生信息
	 */
	@RequestMapping(value="/list")
	@ResponseBody
	public ModelAndView trends(Page page){
		logBefore(logger, "每日发布动态人数统计");	
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		try{
			pd = this.getPageData();
			page.setPd(pd);
			List<PageData>	varList = doctorFunctionService.functionList(page);	//列出列表
			mv.setViewName("doctor/function/doctorFunction");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX,this.getHC());	//按钮权限
		} catch(Exception e){
			logger.error(e.toString(), e);
		}
		return mv;
	}
	
	
	/*
	 * 活跃人数信息到EXCEL
	 * @return
	 */
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel(){
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try{
			if(Jurisdiction.buttonJurisdiction(menuUrl, "cha")){
				//检索条件===
				Map<String,Object> dataMap = new HashMap<String,Object>();
				List<String> titles = new ArrayList<String>();
				
				titles.add("功能名称"); 		//1
				titles.add("使用人次数");  		//2
				
				dataMap.put("titles", titles);
				
				List<PageData> trendsList = doctorFunctionService.functionList(pd);
				List<PageData> varList = new ArrayList<PageData>();
				for(int i=0;i<trendsList.size();i++){
					PageData vpd = new PageData();
					vpd.put("var1", trendsList.get(i).getString("fState"));		//1
					vpd.put("var2", trendsList.get(i).get("fCount")+"");		//2
					varList.add(vpd);
				}
				dataMap.put("varList", varList);
				ObjectExcelView erv = new ObjectExcelView();					//执行excel操作
				mv = new ModelAndView(erv,dataMap);
			}
		} catch(Exception e){
			logger.error(e.toString(), e);
		}
		return mv;
	}
	/* ===============================权限================================== */
	public Map<String, String> getHC(){
		Subject currentUser = SecurityUtils.getSubject();  //shiro管理的session
		Session session = currentUser.getSession();
		return (Map<String, String>)session.getAttribute(Const.SESSION_QX);
	}
}

package com.fh.controller.doctor;

import java.math.BigDecimal;
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
import com.fh.service.doctor.DoctorTrendsService;
import com.fh.util.Const;
import com.fh.util.Jurisdiction;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;
import com.fh.util.tools.IsNull;

/** 
 * 创建人：张东东 
 * 创建时间：2018-07-31
 */
@Controller
@RequestMapping(value="/doctorTrends")
public class DoctorTrendsController extends BaseController {

	String menuUrl = "doctorTrends/trentslist.do"; //对应统计发布动态医生界面
	@Resource(name="doctorTrendsService")
	private DoctorTrendsService doctorTrendsService;
	
	/**
	 * 查询每天发布动态的总人数
	 */
	@RequestMapping(value="/trentslist")
	@ResponseBody
	public ModelAndView list(Page page){
		logBefore(logger, "每日发布动态人数统计");
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		try{
			pd = this.getPageData();
			String BeginDate=(String) pd.get("begindate");
			String EndDate=(String) pd.get("enddate");
			if(!IsNull.paramsIsNull(BeginDate)&&!IsNull.paramsIsNull(EndDate)){
				pd.put("begintime", BeginDate+" 00:00:00");
				pd.put("endtime", EndDate+" 23:59:59");
			}
			page.setPd(pd);
			List<PageData>	varList = doctorTrendsService.list(page);	//列出Pictures列表
			mv.setViewName("doctor/doctortrends/doctorTrends_list");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX,this.getHC());	//按钮权限
		} catch(Exception e){
			logger.error(e.toString(), e);
		}
		return mv;
	}
	/**
	 * 查询一段时间内发布动态的总人数以及所占活跃人数的比例,还有发布动态的医生信息
	 */
	
	@RequestMapping(value="/trents")
	@ResponseBody
	public ModelAndView trends(Page page){
		logBefore(logger, "每日发布动态人数统计");	
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		try{
			pd = this.getPageData();
			String BeginDate=(String) pd.get("begindate");
			String EndDate=(String) pd.get("enddate");
			long lactive=(Long) doctorTrendsService.actives(pd).get("activeNum");//总活跃人数
			
			if(!IsNull.paramsIsNull(BeginDate)&&!IsNull.paramsIsNull(EndDate)){
				pd.put("begintime", BeginDate+" 00:00:00");
				pd.put("endtime", EndDate+" 23:59:59");
			}
			page.setPd(pd);
			List<PageData>	varList = doctorTrendsService.detaillist(page);	//列出列表
			mv.setViewName("doctor/doctortrends/doctorTrends");
			long ltrends= (Long) doctorTrendsService.trends(pd).get("trendsNum");
			
			mv.addObject("trendsNum", ltrends);//发布动态总人数
			mv.addObject("trendsRato", ltrends==0?0.00+"%":BigDecimal.valueOf(ltrends).divide(BigDecimal.valueOf(lactive),2,BigDecimal.ROUND_CEILING).multiply(BigDecimal.valueOf(100))+"%");
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
				String BeginDate=(String) pd.get("begindate");
				String EndDate=(String) pd.get("enddate");
				if(null!=BeginDate&&!BeginDate.isEmpty()&&null!=EndDate&&!EndDate.isEmpty()){
					pd.put("begintime", BeginDate+" 00:00:00");
					pd.put("endtime", EndDate+" 23:59:59");
				}
				//检索条件===
				
				Map<String,Object> dataMap = new HashMap<String,Object>();
				List<String> titles = new ArrayList<String>();
				
				titles.add("医生姓名"); 		//1
				titles.add("电话");  		//2
				titles.add("地址");			//3
				titles.add("诊所名称");			//4
				titles.add("发布动态");			//5
				titles.add("总浏览量");			//6
				
				dataMap.put("titles", titles);
				
				List<PageData> trendsList = doctorTrendsService.list(pd);
				List<PageData> varList = new ArrayList<PageData>();
				for(int i=0;i<trendsList.size();i++){
					PageData vpd = new PageData();
					vpd.put("var1", trendsList.get(i).getString("truename"));		//1
					vpd.put("var2", trendsList.get(i).getString("phone"));		//2
					vpd.put("var3", trendsList.get(i).getString("clinicProvince")+","+trendsList.get(i).getString("cityname")+","+trendsList.get(i).getString("clinicAddress"));			//3
					vpd.put("var4", trendsList.get(i).getString("clinicName"));	//4
					vpd.put("var5", trendsList.get(i).get("total")+"");		//5
					vpd.put("var6", trendsList.get(i).get("count")+"");		//6
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

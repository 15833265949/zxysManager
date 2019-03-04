package com.fh.controller.system.login;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.system.Menu;
import com.fh.entity.system.Role;
import com.fh.entity.system.User;
import com.fh.service.doctor.DoctorActiveAreaService;
import com.fh.service.doctor.RegisterService;
import com.fh.service.patient.PatientActiveService;
import com.fh.service.patient.PatientConsumptionService;
import com.fh.service.platform.PlatformEnableDrawMoneyAmountService;
import com.fh.service.platform.PlatformSystemCrashService;
import com.fh.service.system.menu.MenuService;
import com.fh.service.system.role.RoleService;
import com.fh.service.system.user.UserService;
import com.fh.util.AppUtil;
import com.fh.util.Const;
import com.fh.util.DateUtil;
import com.fh.util.IsNull;
import com.fh.util.PageData;
import com.fh.util.RightsHelper;
import com.fh.util.Tools;
/*
 * 总入口
 */
@Controller
public class LoginController extends BaseController {

	@Resource(name="userService")
	private UserService userService;
	@Resource(name="menuService")
	private MenuService menuService;
	@Resource(name="roleService")
	private RoleService roleService;
	@Resource(name="registerService")
	private RegisterService registerService;
	@Resource(name="doctorActiveAreaService")
	private DoctorActiveAreaService doctorActiveAreaService;
	@Resource(name="patientActiveService")
	private PatientActiveService patientActiveService;
	@Resource(name="patientConsumptionService")
	private PatientConsumptionService patientConsumptionService;
	@Resource(name="platformEnableDrawMoneyAmountService")
	private PlatformEnableDrawMoneyAmountService platformEnableDrawMoneyAmountService;
	@Resource(name="platformSystemCrashService")
	private PlatformSystemCrashService platformSystemCrashService;
	/**
	 * 获取登录用户的IP
	 * @throws Exception 
	 */
	public void getRemortIP(String USERNAME) throws Exception {  
		PageData pd = new PageData();
		HttpServletRequest request = this.getRequest();
		String ip = "";
		if (request.getHeader("x-forwarded-for") == null) {  
			ip = request.getRemoteAddr();  
	    }else{
	    	ip = request.getHeader("x-forwarded-for");  
	    }
		pd.put("USERNAME", USERNAME);
		pd.put("IP", ip);
		userService.saveIP(pd);
	}  
	
	
	/**
	 * 访问登录页
	 * @return
	 */
	@RequestMapping(value="/login_toLogin")
	public ModelAndView toLogin()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("SYSNAME", Tools.readTxtFile(Const.SYSNAME)); //读取系统名称
		mv.setViewName("system/admin/login");
		mv.addObject("pd",pd);
		return mv;
	}
	
	/**
	 * 请求登录，验证用户
	 */
	@RequestMapping(value="/login_login" ,produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object login()throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String errInfo = "";
		String KEYDATA[] = pd.getString("KEYDATA").replaceAll("qq313596790fh", "").replaceAll("QQ978336446fh", "").split(",fh,");
		
		if(null != KEYDATA && KEYDATA.length == 3){
			//shiro管理的session
			Subject currentUser = SecurityUtils.getSubject();  
			Session session = currentUser.getSession();
			String sessionCode = (String)session.getAttribute(Const.SESSION_SECURITY_CODE);		//获取session中的验证码
			
			String code = KEYDATA[2];
			if(null == code || "".equals(code)){
				errInfo = "nullcode"; //验证码为空
			}else{
				String USERNAME = KEYDATA[0];
				String PASSWORD  = KEYDATA[1];
				pd.put("USERNAME", USERNAME);
				if(Tools.notEmpty(sessionCode) && sessionCode.equalsIgnoreCase(code)){
					String passwd = new SimpleHash("SHA-1", USERNAME, PASSWORD).toString();	//密码加密
					pd.put("PASSWORD", passwd);
					pd = userService.getUserByNameAndPwd(pd);
					if(pd != null){
						pd.put("LAST_LOGIN",DateUtil.getTime().toString());
						userService.updateLastLogin(pd);
						User user = new User();
						user.setUSER_ID(pd.getString("USER_ID"));
						user.setUSERNAME(pd.getString("USERNAME"));
						user.setPASSWORD(pd.getString("PASSWORD"));
						user.setNAME(pd.getString("NAME"));
						user.setRIGHTS(pd.getString("RIGHTS"));
						user.setROLE_ID(pd.getString("ROLE_ID"));
						user.setLAST_LOGIN(pd.getString("LAST_LOGIN"));
						user.setIP(pd.getString("IP"));
						user.setSTATUS(pd.getString("STATUS"));
						session.setAttribute(Const.SESSION_USER, user);
						session.removeAttribute(Const.SESSION_SECURITY_CODE);
						
						//shiro加入身份验证
						Subject subject = SecurityUtils.getSubject(); 
					    UsernamePasswordToken token = new UsernamePasswordToken(USERNAME, PASSWORD); 
					    try { 
					        subject.login(token); 
					    } catch (AuthenticationException e) { 
					    	errInfo = "身份验证失败！";
					    }
					    
					}else{
						errInfo = "usererror"; 				//用户名或密码有误
					}
				}else{
					errInfo = "codeerror";				 	//验证码输入有误
				}
				if(Tools.isEmpty(errInfo)){
					errInfo = "success";					//验证成功
				}
			}
		}else{
			errInfo = "error";	//缺少参数
		}
		map.put("result", errInfo);
		return AppUtil.returnObject(new PageData(), map);
	}
	
	/**
	 * 访问系统首页
	 */
	@RequestMapping(value="/main/{changeMenu}")
	public ModelAndView login_index(@PathVariable("changeMenu") String changeMenu){
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try{
			
			//shiro管理的session
			Subject currentUser = SecurityUtils.getSubject();  
			Session session = currentUser.getSession();
			
			User user = (User)session.getAttribute(Const.SESSION_USER);
			if (user != null) {
				
				User userr = (User)session.getAttribute(Const.SESSION_USERROL);
				if(null == userr){
					user = userService.getUserAndRoleById(user.getUSER_ID());
					session.setAttribute(Const.SESSION_USERROL, user);
				}else{
					user = userr;
				}
				Role role = user.getRole();
				String roleRights = role!=null ? role.getRIGHTS() : "";
				//避免每次拦截用户操作时查询数据库，以下将用户所属角色权限、用户权限限都存入session
				session.setAttribute(Const.SESSION_ROLE_RIGHTS, roleRights); 		//将角色权限存入session
				session.setAttribute(Const.SESSION_USERNAME, user.getUSERNAME());	//放入用户名
				
				List<Menu> allmenuList = new ArrayList<Menu>();
				
				if(null == session.getAttribute(Const.SESSION_allmenuList)){
					allmenuList = menuService.listAllMenu();
					if(Tools.notEmpty(roleRights)){
						for(Menu menu : allmenuList){
							menu.setHasMenu(RightsHelper.testRights(roleRights, menu.getMENU_ID()));
							if(menu.isHasMenu()){
								List<Menu> subMenuList = menu.getSubMenu();
								for(Menu sub : subMenuList){
									sub.setHasMenu(RightsHelper.testRights(roleRights, sub.getMENU_ID()));
								}
							}
						}
					}
					session.setAttribute(Const.SESSION_allmenuList, allmenuList);			//菜单权限放入session中
				}else{
					allmenuList = (List<Menu>)session.getAttribute(Const.SESSION_allmenuList);
				}
				
				//切换菜单=====
				List<Menu> menuList = new ArrayList<Menu>();
				//if(null == session.getAttribute(Const.SESSION_menuList) || ("yes".equals(pd.getString("changeMenu")))){
				if(null == session.getAttribute(Const.SESSION_menuList) || ("yes".equals(changeMenu))){
					List<Menu> menuList1 = new ArrayList<Menu>();
					List<Menu> menuList2 = new ArrayList<Menu>();
					
					//拆分菜单
					for(int i=0;i<allmenuList.size();i++){
						Menu menu = allmenuList.get(i);
						if("1".equals(menu.getMENU_TYPE())){
							menuList1.add(menu);
						}else{
							menuList2.add(menu);
						}
					}
					
					session.removeAttribute(Const.SESSION_menuList);
					if("2".equals(session.getAttribute("changeMenu"))){
						session.setAttribute(Const.SESSION_menuList, menuList1);
						session.removeAttribute("changeMenu");
						session.setAttribute("changeMenu", "1");
						menuList = menuList1;
					}else{
						session.setAttribute(Const.SESSION_menuList, menuList2);
						session.removeAttribute("changeMenu");
						session.setAttribute("changeMenu", "2");
						menuList = menuList2;
					}
				}else{
					menuList = (List<Menu>)session.getAttribute(Const.SESSION_menuList);
				}
				//切换菜单=====
				
				if(null == session.getAttribute(Const.SESSION_QX)){
					session.setAttribute(Const.SESSION_QX, this.getUQX(session));	//按钮权限放到session中
				}
				
				//FusionCharts 报表
			 	String strXML = "<graph caption='前12个月订单销量柱状图' xAxisName='月份' yAxisName='值' decimalPrecision='0' formatNumberScale='0'><set name='2013-05' value='4' color='AFD8F8'/><set name='2013-04' value='0' color='AFD8F8'/><set name='2013-03' value='0' color='AFD8F8'/><set name='2013-02' value='0' color='AFD8F8'/><set name='2013-01' value='0' color='AFD8F8'/><set name='2012-01' value='0' color='AFD8F8'/><set name='2012-11' value='0' color='AFD8F8'/><set name='2012-10' value='0' color='AFD8F8'/><set name='2012-09' value='0' color='AFD8F8'/><set name='2012-08' value='0' color='AFD8F8'/><set name='2012-07' value='0' color='AFD8F8'/><set name='2012-06' value='0' color='AFD8F8'/></graph>" ;
			 	mv.addObject("strXML", strXML);
			 	//FusionCharts 报表
			 	
			 	//读取websocket配置
			 	String strWEBSOCKET = Tools.readTxtFile(Const.WEBSOCKET);//读取WEBSOCKET配置
			 	if(null != strWEBSOCKET && !"".equals(strWEBSOCKET)){
					String strIW[] = strWEBSOCKET.split(",fh,");
					if(strIW.length == 4){
						pd.put("WIMIP", strIW[0]);
						pd.put("WIMPORT", strIW[1]);
						pd.put("OLIP", strIW[2]);
						pd.put("OLPORT", strIW[3]);
					}
				}
			 	//读取websocket配置
			 	
				mv.setViewName("system/admin/index");
				mv.addObject("user", user);
				mv.addObject("menuList", menuList);
			}else {
				mv.setViewName("system/admin/login");//session失效后跳转登录页面
			}
			
			
		} catch(Exception e){
			mv.setViewName("system/admin/login");
			logger.error(e.getMessage(), e);
		}
		pd.put("SYSNAME", Tools.readTxtFile(Const.SYSNAME)); //读取系统名称
		mv.addObject("pd",pd);
		return mv;
	}
	
	/**
	 * 进入tab标签
	 * @return
	 */
	@RequestMapping(value="/tab")
	public String tab(){
		return "system/admin/tab";
	}
	
	/**
	 * 进入首页后的默认页面
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/login_default")
	public ModelAndView defaultPage() throws Exception{
		ModelAndView mvAndView=new ModelAndView("system/admin/default");
		
//		PageData pd = new PageData();
//		pd = this.getPageData();
//		mvAndView.addObject("list", registerService.patientRegister(pd));
//		mvAndView.addObject("list2", registerService.doctorRegister(pd));
//		mvAndView.addObject("list3", registerService.activeAndConsult(pd));
//		mvAndView.addObject("list4", registerService.moneyDoctorMade(pd));
//		mvAndView.addObject("weekCount", registerService.SaleCountByWeek(pd));
//		
//		Date date1 = new Date();
//		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
//		String dateString = sif.format(date1);
//		pd.put("dayDate", dateString);
//		mvAndView.addObject("listDoctorPatientRegi", registerService.listDoctorPatientRegi(pd));
//		
//		/*************************    陈彦林开始         *************************/
//		
//		//按地域，查询今天的医生活跃人数
//		mvAndView.addObject("doctorActiveNumByArea", doctorActiveAreaService.listDoctorActiveNumByArea(pd));
//		//按地域，查询今天的患者活跃人数
//		mvAndView.addObject("patientActiveNumByArea", patientActiveService.listPatientActiveNumByArea(pd));
//		
//		//患者消费-当月曲线图
//		mvAndView.addObject("patientConsumptionDay", patientConsumptionService.listDayMap(pd));
//		//患者消费-月度曲线图
//		List<PageData> pdList = patientConsumptionService.listMontylyMap(pd);
//		List<PageData> returnList = new ArrayList<PageData>();//用于接收存储-前端所需的数据
//		//添加月份日期数据
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
//		SimpleDateFormat sdf1 = new SimpleDateFormat("M");
//		String yeaString = sdf.format(new Date());//当前年份
//		int monthInteger = Integer.parseInt(sdf1.format(new Date()));//当前月份
//		PageData pd1 = new PageData();
//		for(int i =0; i < pdList.size(); i++){
//			//若日期-月度数据为空
//			if(IsNull.paramsIsNull(pdList.get(i).get("dat"))){
//				mvAndView.addObject("error", "月度日期获取失败");
//				return mvAndView;
//			}
//			for(int j = 1; j <= monthInteger; j++){
//				//10月以内数据
//				if(j<10){
//					if(!(pdList.get(i).get("dat").toString()).equals(yeaString+"-0"+j)){
//						if(pd1.size() < monthInteger*2){
//							pd1.put("dat"+j, j+"月");
//							pd1.put("sumMoney"+j, 0);					
//						}
//					}else{
//						pd1.put("dat"+j, pdList.get(i).get("dat").toString().substring(6)+"月");
//						pd1.put("sumMoney"+j, pdList.get(i).get("sumMoney"));
//					}
//				//大于等于10月
//				}else{
//					if(!(pdList.get(i).get("dat").toString()).equals(yeaString+"-"+j)){
//						if(pd1.size() < monthInteger*2){
//							pd1.put("dat"+j, j+"月");
//							pd1.put("sumMoney"+j, 0);					
//						}
//					}else{
//						pd1.put("dat"+j, pdList.get(i).get("dat").toString().substring(6)+"月");
//						pd1.put("sumMoney"+j, pdList.get(i).get("sumMoney"));
//					}						
//				}				
//			}
//		}
//		for(int i = 1; i <= (pd1.size()/2); i++){
//			PageData pData = new PageData();
//			pData.put("dat", pd1.get("dat"+i));
//			pData.put("sumMoney",pd1.get("sumMoney"+i));
//			returnList.add(pData);
//		}
//		mvAndView.addObject("patientConsumptionMontyly", returnList);
//		//患者消费-年度曲线图
//		mvAndView.addObject("patientConsumptionAnnual", patientConsumptionService.listAnnualMap(pd));
//		
//		//可提现资金(医生和诊所)-当月曲线图
//		mvAndView.addObject("enableDrawMoneyAmountDay", platformEnableDrawMoneyAmountService.listDay(pd));
//		//可提现资金(医生和诊所)-月度曲线图
//		pdList = platformEnableDrawMoneyAmountService.listMontyly(pd);
//		System.out.println("pdList:"+pdList);
//		returnList.clear();
//		PageData pd2 = new PageData();
//		for(int i =0; i < pdList.size(); i++){
//			//若日期-月度数据为空
//			if(IsNull.paramsIsNull(pdList.get(i).get("dat"))){
//				mvAndView.addObject("error", "系统数据错误");
//				return mvAndView;
//			}
//			for(int j = 1; j <= monthInteger; j++){
//				//10月以内数据
//				if(j<10){
//					if(!(pdList.get(i).get("dat").toString()).equals(yeaString+"-0"+j)){
//						if(pd2.size() < monthInteger*2){
//							pd2.put("dat"+j, j+"月");
//							pd2.put("sumMoney"+j, 0);					
//						}
//					}else{
//						pd2.put("dat"+j, pdList.get(i).get("dat").toString().substring(6)+"月");
//						pd2.put("sumMoney"+j, pdList.get(i).get("sumMoney"));
//					}
//				//大于等于10月
//				}else{
//					if(!(pdList.get(i).get("dat").toString()).equals(yeaString+"-"+j)){
//						if(pd2.size() < monthInteger*2){
//							pd2.put("dat"+j, j+"月");
//							pd2.put("sumMoney"+j, 0);					
//						}
//					}else{
//						pd2.put("dat"+j, pdList.get(i).get("dat").toString().substring(6)+"月");
//						pd2.put("sumMoney"+j, pdList.get(i).get("sumMoney"));
//					}						
//				}				
//			}
//		}
//		for(int i = 1; i <= (pd2.size()/2); i++){
//			PageData pData = new PageData();
//			pData.put("dat", pd2.get("dat"+i));
//			pData.put("sumMoney",pd2.get("sumMoney"+i));
//			returnList.add(pData);
//		}
//		mvAndView.addObject("enableDrawMoneyAmountMontyly", returnList);
//		//可提现资金(医生和诊所)-年度曲线图
//		mvAndView.addObject("enableDrawMoneyAmountAnnual", platformEnableDrawMoneyAmountService.listAnnual(pd));
//		
//		//系统故障近7天（包括今天）-统计曲线图
//		pdList = platformSystemCrashService.listLatelyDayMapONE();//安卓系统故障数据
//		for (int j = 0; j < pdList.size(); j++) {
//			if (IsNull.paramsIsNull(pdList.get(j).get("dat"))) {//若日期数据为空
//				mvAndView.addObject("error", "系统数据错误");
//				return mvAndView;
//			}
//		}
//		returnList.clear();
//		returnList = findLatelySevenDate(pdList);
//		mvAndView.addObject("oneSystemList", returnList);
//		/*************************    陈彦林结束         *************************/
//		
//		
//		
//		
//		SimpleDateFormat sifYear = new SimpleDateFormat("yyyy");
//		String year = sifYear.format(date1);
//		
//		SimpleDateFormat sifDay = new SimpleDateFormat("MM-dd");
//		String day = sifDay.format(date1);
//		pd.put("day", day);
//		pd.put("year", year);
//		pd.put("unName", "石家庄知心诊所");
//		mvAndView.addObject("listToDayClinicRanking", registerService.listToDayClinicRanking(pd));
//		
//		mvAndView.addObject("listSubscribersByYear",registerService.findlistSubscribersByYear(pd));
//		mvAndView.addObject("listSubscribersByMonth",registerService.findlistSubscribersByMonth(pd));
//		
//		List<String> listNameList = new ArrayList<String>();
//		Date date = new Date();
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(date);
//		
//		SimpleDateFormat format2 = new SimpleDateFormat("MM-dd");
//		
//		date = new Date();
//		calendar.setTime(date);
//		calendar.add(Calendar.DATE, -6);
//		date = calendar.getTime();
//		listNameList.add(format2.format(date));
//		
//		date = new Date();
//		calendar.setTime(date);
//		calendar.add(Calendar.DATE, -5);
//		date = calendar.getTime();
//		listNameList.add(format2.format(date));
//		
//		date = new Date();
//		calendar.setTime(date);
//		calendar.add(Calendar.DATE, -4);
//		date = calendar.getTime();
//		listNameList.add(format2.format(date));
//		
//		date = new Date();
//		calendar.setTime(date);
//		calendar.add(Calendar.DATE, -3);
//		date = calendar.getTime();
//		listNameList.add(format2.format(date));
//		
//		date = new Date();
//		calendar.setTime(date);
//		calendar.add(Calendar.DATE, -2);
//		date = calendar.getTime();
//		listNameList.add(format2.format(date));
//		
//		date = new Date();
//		calendar.setTime(date);
//		calendar.add(Calendar.DATE, -1);
//		date = calendar.getTime();
//		listNameList.add(format2.format(date));
//		
//		date = new Date();
//		calendar.setTime(date);
//		calendar.add(Calendar.DATE, -0);
//		date = calendar.getTime();
//		listNameList.add(format2.format(date));
//		
//		
//		
//		
//		mvAndView.addObject("listSubscribersByDay",registerService.findlistSubscribersByDay(pd));
//		mvAndView.addObject("listNameList",listNameList);
		
		return mvAndView;
	}
	
	/**
	 * 用户注销
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/logout")
	public ModelAndView logout(){
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		
		//shiro管理的session
		Subject currentUser = SecurityUtils.getSubject();  
		Session session = currentUser.getSession();
		
		session.removeAttribute(Const.SESSION_USER);
		session.removeAttribute(Const.SESSION_ROLE_RIGHTS);
		session.removeAttribute(Const.SESSION_allmenuList);
		session.removeAttribute(Const.SESSION_menuList);
		session.removeAttribute(Const.SESSION_QX);
		session.removeAttribute(Const.SESSION_userpds);
		session.removeAttribute(Const.SESSION_USERNAME);
		session.removeAttribute(Const.SESSION_USERROL);
		session.removeAttribute("changeMenu");
		
		//shiro销毁登录
		Subject subject = SecurityUtils.getSubject(); 
		subject.logout();
		
		pd = this.getPageData();
		String  msg = pd.getString("msg");
		pd.put("msg", msg);
		
		pd.put("SYSNAME", Tools.readTxtFile(Const.SYSNAME)); //读取系统名称
		mv.setViewName("system/admin/login");
		mv.addObject("pd",pd);
		return mv;
	}
	
	/**
	 * 获取用户权限
	 */
	public Map<String, String> getUQX(Session session){
		PageData pd = new PageData();
		Map<String, String> map = new HashMap<String, String>();
		try {
			String USERNAME = session.getAttribute(Const.SESSION_USERNAME).toString();
			pd.put(Const.SESSION_USERNAME, USERNAME);
			String ROLE_ID = userService.findByUId(pd).get("ROLE_ID").toString();
			
			pd.put("ROLE_ID", ROLE_ID);
			
			PageData pd2 = new PageData();
			pd2.put(Const.SESSION_USERNAME, USERNAME);
			pd2.put("ROLE_ID", ROLE_ID);
			
			pd = roleService.findObjectById(pd);																
				
			pd2 = roleService.findGLbyrid(pd2);
			if(null != pd2){
				map.put("FX_QX", pd2.get("FX_QX").toString());
				map.put("FW_QX", pd2.get("FW_QX").toString());
				map.put("QX1", pd2.get("QX1").toString());
				map.put("QX2", pd2.get("QX2").toString());
				map.put("QX3", pd2.get("QX3").toString());
				map.put("QX4", pd2.get("QX4").toString());
			
				pd2.put("ROLE_ID", ROLE_ID);
				pd2 = roleService.findYHbyrid(pd2);
				map.put("C1", pd2.get("C1").toString());
				map.put("C2", pd2.get("C2").toString());
				map.put("C3", pd2.get("C3").toString());
				map.put("C4", pd2.get("C4").toString());
				map.put("Q1", pd2.get("Q1").toString());
				map.put("Q2", pd2.get("Q2").toString());
				map.put("Q3", pd2.get("Q3").toString());
				map.put("Q4", pd2.get("Q4").toString());
			}
			
			map.put("adds", pd.getString("ADD_QX"));
			map.put("dels", pd.getString("DEL_QX"));
			map.put("edits", pd.getString("EDIT_QX"));
			map.put("chas", pd.getString("CHA_QX"));
			
			//System.out.println(map);
			
			this.getRemortIP(USERNAME);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}	
		return map;
	}
	
	/**
	 * 获取封装最近7天的数据
	 * @param pdList
	 * @return List<PageData>
	 */
	private List<PageData> findLatelySevenDate(List<PageData> pdList){
		//用于存储封装数据
		List<PageData> returnList = new ArrayList<PageData>();
			//获取日期操作对象
		Calendar latelyDate = Calendar.getInstance();
		SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
		for(int i = 6; i >= 0; i--){
			latelyDate.roll(Calendar.DATE, -i);//日期回滚i天
			String agoDate = sif.format(latelyDate.getTime());
			PageData pageData = new PageData();
			pageData.put("dat", agoDate);
			for (int j = 0; j < pdList.size(); j++) {
				if((pdList.get(j).get("dat").toString().trim()).equals(agoDate)){
					pageData.put("num", pdList.get(j).get("num"));
					break;
				}else{
					pageData.put("num", 0);				
				}
			}
			returnList.add(pageData);
		}
		return returnList;
	}
}

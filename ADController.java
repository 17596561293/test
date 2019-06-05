package com.mall.controller.Index;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.mall.servicebean.GoodsInfoBean;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mall.controller.BaseController;
import com.mall.servicebean.AdBean;
import com.mall.util.CheckTool;
import com.mall.util.PageData;
@Controller
@RequestMapping(value = "/ad")
public class ADController extends BaseController {
	private AdBean adBean=new AdBean();
	private GoodsInfoBean goodsInfoBean = new GoodsInfoBean();
	/*
	 * 被客户端调用，显示首页推送的滚动广告、广告位广告和五个爆款机型
	 */
	@RequestMapping("/getIndexAD")
	public void getIndexAD(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter(); 
		PageData pd=this.getPageData();
		int companyId=pd.getInteger("companyId", 1);
		pd.put("companyId", companyId);
		String[] checkPara={"imei","timestamp","version"};
		String str=CheckTool.checkParameter(checkPara, pd);
		if(str.equals("1"))//校驗通過
		{
		    logger.info("获取首页广告数据"+pd.toParaString());
			str=adBean.getIndexAD(commonService,pd);
			logger.info("返回首页广告数据结果"+str);
		}
		
		out.print(str);
	}
	/*
	 * 被客户端调用，记录点击的广告或推荐，并返回到相应广告或推荐详情页面
	 */
	@RequestMapping("/getAdclick")
	public void getDetails(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter(); 
		PageData pd=this.getPageData();
		PageData requestPD=new PageData(pd);
		int companyId=pd.getInteger("companyId", 1);
		pd.put("companyId", companyId);
		String[] checkPara={"imei","timestamp","version"};
		String str=CheckTool.checkParameter(checkPara, pd);
		if(str.equals("1"))//校驗通過
		{
			adBean.saveClick(commonService,pd);
			if(pd.getString("type").equals("1"))
			{
				adBean.saveClick(commonService,pd);
				String url=adBean.findUrl(commonService,pd);
				if(url.contains("bocaibao.com.cn/"))
				{
					if(url.indexOf("?")>5)
					{
						url=url+"&userId="+pd.getString("userId","")+"&imei="+pd.getString("imei","")+"&version="+pd.getString("version","")+"&timestamp="+pd.getString("timestamp","")+"&sign="+pd.getString("sign");
						
					}else {
						url=url+"?userId="+pd.getString("userId","")+"&imei="+pd.getString("imei","")+"&version="+pd.getString("version","")+"&timestamp="+pd.getString("timestamp","")+"&sign="+pd.getString("sign");
					}
					
				}
				logger.info("广告跳转地址："+url);
				response.sendRedirect(url);
				
			}else {
				pd.put("goodsid", pd.getString("cid"));
				PageData user=commonBean.getUserdatabyUserId(commonService, pd.getString("userId"));
				str=goodsInfoBean.getDetails(commonService,pd,user);
				logbean.addLog(request, requestPD, str);//添加日志
				out.print(str);
			}
			
		}

	}
	
	
	
}

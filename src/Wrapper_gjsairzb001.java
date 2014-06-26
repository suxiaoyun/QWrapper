import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.qunar.qfwrapper.bean.booking.BookingInfo;
import com.qunar.qfwrapper.bean.booking.BookingResult;
import com.qunar.qfwrapper.bean.search.FlightDetail;
import com.qunar.qfwrapper.bean.search.FlightSearchParam;
import com.qunar.qfwrapper.bean.search.FlightSegement;
import com.qunar.qfwrapper.bean.search.ProcessResultInfo;
import com.qunar.qfwrapper.bean.search.RoundTripFlightInfo;
import com.qunar.qfwrapper.constants.Constants;
import com.qunar.qfwrapper.interfaces.QunarCrawler;
import com.qunar.qfwrapper.util.QFGetMethod;
import com.qunar.qfwrapper.util.QFHttpClient;


public class Wrapper_gjsairzb001 implements QunarCrawler{

	@Override
	public BookingResult getBookingInfo(FlightSearchParam arg0) {

		String bookingUrlPre = "http://www.monarch.co.uk/searchgateway/flight/scheduledflight";
		BookingResult bookingResult = new BookingResult();
		
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("post");
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		map.put("VisitorId", "f516b176-38eb-43a2-99ef-9981528c04c3");
		map.put("brandName", "Monarch");
		map.put("departureIATAs", arg0.getDep());
		map.put("returnDestinationIATAs", arg0.getDep());
		map.put("returnIATAs", arg0.getArr());
		map.put("returnOriginIATAs", arg0.getArr());
		map.put("lowFareFinder", "undefined");
		map.put("oneWayDirection", "false");
		map.put("referrerPage", "/arrivals/flights");
		map.put("adults", "1");
		map.put("children", "0");
		map.put("infants", "0");
		
		String depDate = arg0.getDepDate();
		String[] depDates = depDate.split("-");
		depDate=depDates[2]+"/"+depDates[1]+"/"+depDates[0];
		
		String retDate = arg0.getRetDate();
		String[] retDates = retDate.split("-");
		retDate=retDates[2]+"/"+retDates[1]+"/"+retDates[0];
		
		map.put("departureDate",depDate);
		map.put("returnDate",retDate);
		
		bookingInfo.setInputs(map);		
		bookingResult.setData(bookingInfo);
		bookingResult.setRet(true);
		return bookingResult;

	}

	@Override
	public String getHtml(FlightSearchParam arg0) {
		// http://bookflights.monarch.co.uk/SearchSelect.aspx?roundtrip=False&origin=LTN&destination=FCO&outbounddate=20140727&adult=1&child=0&infant=0&lff=False&offer=False

		QFGetMethod get = null;	
		try {	
		QFHttpClient httpClient = new QFHttpClient(arg0, false);
		
		/*对于需要cookie的网站，请自己处理cookie（必须）
		* 例如：
		* httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		*/
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
		String depDate = arg0.getDepDate();
		String retDate = arg0.getRetDate();
		Date dep = formatter.parse(depDate);
		Date ret = formatter.parse(retDate);
		System.out.println(formatter.format(dep));
		System.out.println(formatter.format(ret));
		arg0.setDepDate(formatter.format(dep));
		arg0.setRetDate(formatter.format(ret));
		
		
		String getUrl = String.format("http://bookflights.monarch.co.uk/SearchSelect.aspx?roundtrip=False&origin=%s&destination=%s&outbounddate=%s&inbounddate=%s&adult=1&child=0&infant=0&lff=False&offer=False", 
                arg0.getDep(), arg0.getArr(), formatter.format(dep).replaceAll("-", ""),formatter.format(ret).replaceAll("-", ""));
			get = new QFGetMethod(getUrl);
			
			//1、对于通过多次get|post请求才能得到包含机票信息的网站，需要注意对status的判断
			//2、对于通过多次get|post请求才能得到包含机票信息的网站，如果需要cookie，则在每一次get|post请求前都处理好cookie
			//3、如果网站需要使用cookie，GetMethod 遇到 302 时默认会自动跳转，不留机会给 开发处理Cookie，这个时候要特别小心， 需要使用 get.setFollowRedirects(false); 阻止自动跳转，然后自己处理302 以及Cookie。
			/* 例如：
			try {
				get.setFollowRedirects(false);
				get.getParams().setContentCharset("utf-8");
				client.executeMethod(get);
			
				if(get.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY || get.getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY){
					Header location = get.getResponseHeader("Location");
					String url = "";
					if(location !=null){
						url = location.getValue();
						if(!url.startsWith("http")){
							url = get.getURI().getScheme() + "://" + get.getURI().getHost() + (get.getURI().getPort()==-1?"":(":"+get.getURI().getPort())) + url;
						}
					}else{
						return;
					}
					String cookie = StringUtils.join(client.getState().getCookies(),"; ");
					get = new QFGetMethod(url);
					client.getState().clearCookies();
					get.addRequestHeader("Cookie",cookie);
					client.executeMethod(get);
				}
			} catch (Exception e) {
			e.printStackTrace();
			} finally {
				if(get!=null){
					get.releaseConnection();
				}
			}
			 */
		    int status = httpClient.executeMethod(get);
		    
		    return get.getResponseBodyAsString();

		} catch (Exception e) {			
			e.printStackTrace();
		} finally{
			if (null != get){
				get.releaseConnection();
			}
		}
		return "Exception";
	
	}

	@Override
	public ProcessResultInfo process(String arg0, FlightSearchParam arg1) {
		String html = arg0;
		
		/* ProcessResultInfo中，
		 * ret为true时，status可以为：SUCCESS(抓取到机票价格)|NO_RESULT(无结果，没有可卖的机票)
		 * ret为false时，status可以为:CONNECTION_FAIL|INVALID_DATE|INVALID_AIRLINE|PARSING_FAIL|PARAM_ERROR
		 */
		ProcessResultInfo result = new ProcessResultInfo();
		if ("Exception".equals(html)) {	
			result.setRet(false);
			result.setStatus(Constants.CONNECTION_FAIL);
			return result;			
		}		
		//需要有明显的提示语句，才能判断是否INVALID_DATE|INVALID_AIRLINE|NO_RESULT
		if (html.contains("Today Flight is full, select an other day or check later for any seat released. ")) {
			result.setRet(false);
			result.setStatus(Constants.INVALID_DATE);
			return result;			
		}
		if (html.contains("All charter flights booked using this process are booked with our award winning sister company Avro and are ATOL protected.")) {
			result.setRet(false);
			result.setStatus(Constants.INVALID_AIRLINE);
			return result;			
		}
		

		String  jsonStr= org.apache.commons.lang.StringUtils.substringBetween(html, "\"marketsJson\":", ");");		
		String jsonStr2 = jsonStr.substring(0,jsonStr.lastIndexOf("}"));
		try {			
			List<RoundTripFlightInfo > flightList = new ArrayList<RoundTripFlightInfo >();
			JSONArray ajson = JSON.parseArray(jsonStr2);				
			List<FlightSegement> segs = new ArrayList<FlightSegement>();
			
			List<FlightDetail> depFlightDetailList=new ArrayList<FlightDetail>();
			List<FlightDetail> retFlightDetailList=new ArrayList<FlightDetail>();
			
			double sumTax=0;
			double goPrice=0;
			double returnPrice=0;
			double sumPrice=0;
//去程		
			JSONObject goJson = ajson.getJSONObject(0);
			Object goMarketDatesJsonValue = goJson.get("marketDatesJson");
			JSONArray goMarketDatesJsons=(JSONArray)goMarketDatesJsonValue;
			for(int i = 0; i < goMarketDatesJsons.size(); i++){
				JSONObject marketDatesJson=(JSONObject) goMarketDatesJsons.get(i);
				Object date = marketDatesJson.get("dateDisplay");
				String dateString = date.toString();
				String[] dateStrings = dateString.split(",");
				
				String[] dateStringTrims = dateStrings[1].trim().split(" ");
				String depDate = arg1.getDepDate();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
				Date s=null;  
				try {  
					s= formatter.parse(depDate); 
				} catch (ParseException e) {  
					// TODO Auto-generated catch block  
					e.printStackTrace();  
				}  
				String[] depDates = depDate.split("-");
				depDate=depDates[depDates.length-1];
				if(Integer.parseInt(dateStringTrims[0])==Integer.parseInt(depDate)){
					FlightDetail flightDetail = new FlightDetail();
					List<String> flightNoList =Lists.newArrayList();
					String flightNo = marketDatesJson.getString("flightNumber").replaceAll("[^a-zA-Z\\d]", "");
					flightNoList.add(flightNo);
					
					flightDetail.setFlightno(flightNoList);
					flightDetail.setDepcity(arg1.getDep());
					flightDetail.setArrcity(arg1.getArr());
					flightDetail.setWrapperid(arg1.getWrapperid());
					
					String marketDatesJsonDateDepartureString = marketDatesJson.getString("dateDeparture");
					String marketDatesJsonDateDepartureStringsub = marketDatesJsonDateDepartureString.substring(marketDatesJsonDateDepartureString.indexOf("(")+1,marketDatesJsonDateDepartureString.lastIndexOf(")"));
					Date marketDatesJsonDateDeparture=new Date(Long.parseLong(marketDatesJsonDateDepartureStringsub));
					String dateDeparture = formatter.format(marketDatesJsonDateDeparture);
					
					flightDetail.setDepdate(formatter.parse(dateDeparture));
					flightDetail.setMonetaryunit("GBP");
					
					JSONArray airPacksJson=(JSONArray)marketDatesJson.get("airPacksJson");
					JSONObject airPacksJson0=(JSONObject)airPacksJson.get(0);
					JSONArray passengerTypesJson=(JSONArray)airPacksJson0.get("passengerTypesJson");
					JSONObject passengerTypesJson0=(JSONObject)passengerTypesJson.get(0);
					
					goPrice=passengerTypesJson0.getDouble("farePrice");
					
					JSONArray taxesJson=(JSONArray)passengerTypesJson0.get("taxesJson");
					double goTax=0;
					for(int k=0;k<taxesJson.size();k++){
						JSONObject taxesJsonk=(JSONObject)taxesJson.get(k);
						Double tax = taxesJsonk.getDouble("price");
						goTax+=tax;
					}
					
					flightDetail.setTax(goTax);
					flightDetail.setPrice(goPrice);
					depFlightDetailList.add(flightDetail);
				}
			}
			
//返程
			JSONObject returnJson = ajson.getJSONObject(1);
			Object returnMarketDatesJsonValue = returnJson.get("marketDatesJson");
			JSONArray returnMarketDatesJsons=(JSONArray)returnMarketDatesJsonValue;
			for(int i = 0; i < returnMarketDatesJsons.size(); i++){
				JSONObject marketDatesJson=(JSONObject) returnMarketDatesJsons.get(i);
				Object date = marketDatesJson.get("dateDisplay");
				String dateString = date.toString();
				String[] dateStrings = dateString.split(",");
				
				String[] dateStringTrims = dateStrings[1].trim().split(" ");
				String retDate = arg1.getRetDate();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
				String[] depDates = retDate.split("-");
				retDate=depDates[depDates.length-1];
				if(Integer.parseInt(dateStringTrims[0])==Integer.parseInt(retDate)){
					FlightDetail flightDetail = new FlightDetail();
					List<String> retflightno = Lists.newArrayList(); 
					FlightSegement seg = new FlightSegement();
					
					String flightNode = marketDatesJson.getString("flightNumber").replaceAll("[^a-zA-Z\\d]", "");
					retflightno.add(flightNode);
					
					seg.setFlightno(flightNode);
					seg.setDepairport(marketDatesJson.getString("origin"));
					seg.setArrairport(marketDatesJson.getString("destination"));
					seg.setDeptime(marketDatesJson.getString("departureTime"));
					seg.setArrtime(marketDatesJson.getString("arrivalTime"));
					
					flightDetail.setFlightno(retflightno);
					flightDetail.setDepcity(marketDatesJson.getString("origin"));
					flightDetail.setArrcity(marketDatesJson.getString("destination"));
					flightDetail.setWrapperid(arg1.getWrapperid());

					String marketDatesJsonDateDepartureString = marketDatesJson.getString("dateDeparture");
					String marketDatesJsonDateDepartureStringsub = marketDatesJsonDateDepartureString.substring(marketDatesJsonDateDepartureString.indexOf("(")+1,marketDatesJsonDateDepartureString.lastIndexOf(")"));
					Date marketDatesJsonDateDeparture=new Date(Long.parseLong(marketDatesJsonDateDepartureStringsub));
					
					
					String marketDatesJsonDateString = marketDatesJson.getString("dateArrival");
					String marketDatesJsonDateStringsub = marketDatesJsonDateString.substring(marketDatesJsonDateString.indexOf("(")+1,marketDatesJsonDateString.lastIndexOf(")"));
					Date marketDatesJsonDate=new Date(Long.parseLong(marketDatesJsonDateStringsub));
					String dateArrival = formatter.format(marketDatesJsonDate);
					String dateDeparture = formatter.format(marketDatesJsonDateDeparture);
					seg.setArrDate(dateArrival);
					seg.setDepDate(dateDeparture);
					
					flightDetail.setDepdate(formatter.parse(dateDeparture));
					flightDetail.setMonetaryunit("GBP");

					segs.add(seg);
					
					JSONArray airPacksJson=(JSONArray)marketDatesJson.get("airPacksJson");
					JSONObject airPacksJson0=(JSONObject)airPacksJson.get(0);
					JSONArray passengerTypesJson=(JSONArray)airPacksJson0.get("passengerTypesJson");
					JSONObject passengerTypesJson0=(JSONObject)passengerTypesJson.get(0);
					returnPrice=passengerTypesJson0.getDouble("farePrice");
					
					JSONArray taxesJson=(JSONArray)passengerTypesJson0.get("taxesJson");
					double returnTax=0;
					for(int k=0;k<taxesJson.size();k++){
						JSONObject taxesJsonk=(JSONObject)taxesJson.get(k);
						Double tax = taxesJsonk.getDouble("price");
						returnTax+=tax;
					}
					
					flightDetail.setTax(returnTax);
					flightDetail.setPrice(returnPrice);
					retFlightDetailList.add(flightDetail);
				}
			}

//总和
			for(FlightDetail depFlightDetail:depFlightDetailList){
				
				for(FlightDetail retFlightDetail:retFlightDetailList){
					FlightDetail flightDetail=new FlightDetail();
					
					List<String> depFlightNos = depFlightDetail.getFlightno();
					
					
					
					List<FlightSegement> segList = new ArrayList<FlightSegement>();
					RoundTripFlightInfo  baseFlight = new RoundTripFlightInfo ();
					baseFlight.setOutboundPrice(depFlightDetail.getPrice());
					
					String retDate = arg1.getRetDate();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					Date s=null;  
					try {  
						s= formatter.parse(retDate); 
					} catch (ParseException e) {  
						// TODO Auto-generated catch block  
						e.printStackTrace();  
					}  					
					baseFlight.setRetdepdate(s);
					
					for(FlightSegement segement:segs){
						if(segement.getFlightno().equals(retFlightDetail.getFlightno().get(0))){
							segList.add(segement);
							baseFlight.setInfo(segList);
						}
					}
					
					baseFlight.setReturnedPrice(retFlightDetail.getPrice());
					baseFlight.setRetflightno(retFlightDetail.getFlightno());
					
					sumTax=depFlightDetail.getTax()+retFlightDetail.getTax();
					DecimalFormat df = new DecimalFormat("0.0000");
					String t = df.format(sumTax);
					sumTax=Double.parseDouble(t);
					
					sumPrice=depFlightDetail.getPrice()+retFlightDetail.getPrice();
					String tt = df.format(sumPrice);
					sumPrice=Double.parseDouble(tt);
					
					
					
					flightDetail.setFlightno(depFlightNos);
					flightDetail.setDepcity(arg1.getDep());
					flightDetail.setArrcity(arg1.getArr());
					flightDetail.setWrapperid(arg1.getWrapperid());
					flightDetail.setDepdate(depFlightDetail.getDepdate());
					flightDetail.setMonetaryunit("GBP");
					flightDetail.setTax(sumTax);
					flightDetail.setPrice(sumPrice);	
					
					baseFlight.setDetail(flightDetail);
					
					flightList.add(baseFlight);
				}
			}
			
			result.setRet(true);
			result.setStatus(Constants.SUCCESS);
			result.setData(flightList);
			return result;
		} catch(Exception e){
			result.setRet(false);
			result.setStatus(Constants.PARSING_FAIL);
			return result;
		}
	}
	
	public static void main(String[] args) {

		FlightSearchParam searchParam = new FlightSearchParam();
		searchParam.setWrapperid("gjsairzb001");
		searchParam.setDep("BHX");
		searchParam.setArr("LEI");
		searchParam.setDepDate("2014-6-26");
		searchParam.setRetDate("2014-7-3");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");
		
		String html = new  Wrapper_gjsairzb001().getHtml(searchParam);
		ProcessResultInfo result = new ProcessResultInfo();
		result = new  Wrapper_gjsairzb001().process(html,searchParam);
		if(result.isRet() && result.getStatus().equals(Constants.SUCCESS))
		{
			List<RoundTripFlightInfo> flightList = (List<RoundTripFlightInfo>) result.getData();
			for (RoundTripFlightInfo in : flightList){
				System.out.println("************" + in.getInfo().toString());
				System.out.println("++++++++++++" + in.getDetail().toString());
			}
		}
		else
		{
			System.out.println(result.getStatus());
		}
	}

}

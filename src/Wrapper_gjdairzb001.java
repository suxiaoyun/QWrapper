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
import com.qunar.qfwrapper.bean.booking.BookingInfo;
import com.qunar.qfwrapper.bean.booking.BookingResult;
import com.qunar.qfwrapper.bean.search.FlightDetail;
import com.qunar.qfwrapper.bean.search.FlightSearchParam;
import com.qunar.qfwrapper.bean.search.FlightSegement;
import com.qunar.qfwrapper.bean.search.OneWayFlightInfo;
import com.qunar.qfwrapper.bean.search.ProcessResultInfo;
import com.qunar.qfwrapper.constants.Constants;
import com.qunar.qfwrapper.interfaces.QunarCrawler;
import com.qunar.qfwrapper.util.QFGetMethod;
import com.qunar.qfwrapper.util.QFHttpClient;


public class Wrapper_gjdairzb001 implements QunarCrawler{

	@Override
	public BookingResult getBookingInfo(FlightSearchParam arg0) {

		String bookingUrlPre = "http://bookflights.monarch.co.uk/Select.aspx";
		BookingResult bookingResult = new BookingResult();
		String depDate=arg0.getDepDate();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String formatDate = null;
        
		SimpleDateFormat in = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd");

		try {
			formatDate=in.format(out.parse(depDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("post");
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("AvailabilitySearchInputSelectView$DropDownListDestination2", arg0.getDep());
		map.put("AvailabilitySearchInputSelectView$DropDownListDestination1", arg0.getArr());
		map.put("AvailabilitySearchInputSelectView$DropDownListOrigin1", arg0.getDep());
		map.put("AvailabilitySearchInputSelectView$DropDownListOrigin2", arg0.getArr());
		map.put("AvailabilitySearchInputSelectView$DropDownListPassengerTypeADT", "1");
		map.put("AvailabilitySearchInputSelectView$DropDownListPassengerTypeCHD", "0");
		map.put("AvailabilitySearchInputSelectView$DropDownListPassengerTypeINFANT", "0");
		map.put("AvailabilitySearchInputSelectView$RadioButtonMarketStructure", "RadioButtonOneWay");
		map.put("AvailabilitySearchInputSelectView$SelectedDestination", "FCO");
		map.put("AvailabilitySearchInputSelectView$SelectedOrigin", "LTN");
		map.put("AvailabilitySearchInputSelectView$TextBoxDate1", formatDate);
		map.put("AvailabilitySearchInputSelectView$TextBoxDate2", "17/06/2014");
		map.put("ControlGroupSelectAvailabilityInputSelectView$MonMemberLoginSelectView$PasswordFieldPassword", "");
		map.put("ControlGroupSelectAvailabilityInputSelectView$MonMemberLoginSelectView$TextBoxUserID", "");
		map.put("ControlGroupSelectAvailabilityInputSelectView$SelectAvailabilityInputSelectView$RadioSell_1", "0~D~~DO~ZB01~~11~X|ZB~3566~ ~~LTN~07/27/2014 15:00~FCO~07/27/2014 18:40~||");
		map.put("MemberLoginSelectViewTop$PasswordFieldPassword", "");
		map.put("MemberLoginSelectViewTop$TextBoxUserID", "");
		map.put("MonIp", "10.216.19.20");
		map.put("NavigationCultureViewSelectView$DropDownListNavigationCulture", "en-GB");
		map.put("__EVENTARGUMENT", "");
		map.put("__EVENTTARGET", "ControlGroupSelectAvailabilityInputSelectView$MonLinkButtonProceed");
		map.put("__VIEWSTATE", "/wEPDwUBMGQYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFggFXUNvbnRyb2xHcm91cFNlbGVjdEF2YWlsYWJpbGl0eUlucHV0U2VsZWN0VmlldyRTZWxlY3RBdmFpbGFiaWxpdHlJbnB1dFNlbGVjdFZpZXckU2VsbEtleV8xXzJfMQVhQ29udHJvbEdyb3"+
"VwU2VsZWN0QXZhaWxhYmlsaXR5SW5wdXRTZWxlY3RWaWV3JFNlbGVjdEF2YWlsYWJpbGl0eUlucHV0U2VsZWN0VmlldyRTZWxsS2V5XzFfMl8xRVNTUAVhQ29udHJvbEdyb3VwU2VsZWN0QXZhaWxhYmlsaXR5SW5wdXRTZWxlY3RWaWV3JFNlbGVjdEF2YWlsYWJpbGl0eUlucHV0U2Vs"+"ZWN0VmlldyRTZWxsS2V5XzFfMl8xUFJNUAVdQ29udHJvbEdyb3VwU2VsZWN0QXZhaWxhYmlsaXR5SW5wdXRTZWxlY3RWaWV3JFNlbGVjdEF2YWlsYWJpbGl0eUlucHV0U2VsZWN0VmlldyRTZWxsS2V5XzFfM18xBWFDb250cm9sR3JvdXBTZWxlY3RBdmFpbGFiaWxpdHlJbnB1dFNlbG"+
"VjdFZpZXckU2VsZWN0QXZhaWxhYmlsaXR5SW5wdXRTZWxlY3RWaWV3JFNlbGxLZXlfMV8zXzFFU1NQBWFDb250cm9sR3JvdXBTZWxlY3RBdmFpbGFiaWxpdHlJbnB1dFNlbGVjdFZpZXckU2VsZWN0QXZhaWxhYmlsaXR5SW5wdXRTZWxlY3RWaWV3JFNlbGxLZXlfMV8zXzFQUk1QBTlB"+
"dmFpbGFiaWxpdHlTZWFyY2hJbnB1dFNlbGVjdFZpZXckQ2hlY2tCb3hGbGV4aWJsZU9uRGF0ZXMFM0F2YWlsYWJpbGl0eVNlYXJjaElucHV0U2VsZWN0VmlldyRDaGVja0JveE11bHRpQ2l0ec9zc2tV3hcdpBJeFSHOXZd4LtS+");
		map.put("pageToken", "");
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
		
		/*������Ҫcookie����վ�����Լ�����cookie�����룩
		* ���磺
		* httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		*/

//		String getUrl = String.format("http://bookflights.monarch.co.uk/SearchSelect.aspx?roundtrip=False&origin=LTN&destination=FCO&outbounddate=20140727&adult=1&child=0&infant=0&lff=False&offer=False", arg0.getDep(), arg0.getArr(), arg0.getDepDate().replaceAll("-", "/"), arg0.getDepDate());
		String getUrl ="http://bookflights.monarch.co.uk/SearchSelect.aspx?roundtrip=False&origin=LTN&destination=FCO&outbounddate=20140727&adult=1&child=0&infant=0&lff=False&offer=False";
			
			get = new QFGetMethod(getUrl);
			
			//1������ͨ�����get|post������ܵõ�������Ʊ��Ϣ����վ����Ҫע���status���ж�
			//2������ͨ�����get|post������ܵõ�������Ʊ��Ϣ����վ�������Ҫcookie������ÿһ��get|post����ǰ��������cookie
			//3�������վ��Ҫʹ��cookie��GetMethod ���� 302 ʱĬ�ϻ��Զ���ת����������� ��������Cookie�����ʱ��Ҫ�ر�С�ģ� ��Ҫʹ�� get.setFollowRedirects(false); ��ֹ�Զ���ת��Ȼ���Լ�����302 �Լ�Cookie��
			/* ���磺
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
		
		/* ProcessResultInfo�У�
		 * retΪtrueʱ��status����Ϊ��SUCCESS(ץȡ����Ʊ�۸�)|NO_RESULT(�޽����û�п����Ļ�Ʊ)
		 * retΪfalseʱ��status����Ϊ:CONNECTION_FAIL|INVALID_DATE|INVALID_AIRLINE|PARSING_FAIL|PARAM_ERROR
		 */
		ProcessResultInfo result = new ProcessResultInfo();
		if ("Exception".equals(html)) {	
			result.setRet(false);
			result.setStatus(Constants.CONNECTION_FAIL);
			return result;			
		}		
		//��Ҫ�����Ե���ʾ��䣬�����ж��Ƿ�INVALID_DATE|INVALID_AIRLINE|NO_RESULT
		if (html.contains("Today Flight is full, select an other day or check later for any seat released. ")) {
			result.setRet(false);
			result.setStatus(Constants.INVALID_DATE);
			return result;			
		}
		

		String  jsonStr= org.apache.commons.lang.StringUtils.substringBetween(html, "\"marketsJson\":", ");");		
		String jsonStr2 = jsonStr.substring(0,jsonStr.lastIndexOf("}"));
		try {			
			List<OneWayFlightInfo> flightList = new ArrayList<OneWayFlightInfo>();
			JSONArray ajson = JSON.parseArray(jsonStr2);				
			for (int i = 0; i < ajson.size(); i++){
				OneWayFlightInfo baseFlight = new OneWayFlightInfo();
				List<FlightSegement> segs = new ArrayList<FlightSegement>();
				FlightDetail flightDetail = new FlightDetail();
				FlightSegement seg = new FlightSegement();
				List<String> flightNoList = new ArrayList<String>();
				JSONObject ojson = ajson.getJSONObject(i);
				Object marketDatesJsonValue = ojson.get("marketDatesJson");
				if(marketDatesJsonValue instanceof JSONArray){
					JSONArray marketDatesJsons=(JSONArray)marketDatesJsonValue;
					System.out.println(marketDatesJsons.size());
					for(int j = 0; j < marketDatesJsons.size(); j++){
						JSONObject marketDatesJson=(JSONObject) marketDatesJsons.get(j);
						System.out.println(marketDatesJson.get("dateDisplay"));
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
							flightNoList.clear();
							
							seg.setFlightno(marketDatesJson.getString("flightNumber"));
							seg.setDepairport(marketDatesJson.getString("origin"));
							seg.setArrairport(marketDatesJson.getString("destination"));
							seg.setDeptime(marketDatesJson.getString("departureTime"));
							seg.setArrtime(marketDatesJson.getString("arrivalTime"));
							
							String marketDatesJsonDateString = marketDatesJson.getString("dateArrival");
							String marketDatesJsonDateStringsub = marketDatesJsonDateString.substring(marketDatesJsonDateString.indexOf("(")+1,marketDatesJsonDateString.lastIndexOf(")"));
							Date marketDatesJsonDate=new Date(Long.parseLong(marketDatesJsonDateStringsub));
							String dateArrival = formatter.format(marketDatesJsonDate);
							String dateDeparture = formatter.format(marketDatesJsonDateDeparture);
							seg.setArrDate(dateArrival);
							seg.setDepDate(dateDeparture);
							flightNoList.add(marketDatesJson.getString("flightNumber"));
							
							
							flightDetail.setDepdate(formatter.parse(dateDeparture));
							flightDetail.setFlightno(flightNoList);
							flightDetail.setMonetaryunit("GBP");
							
							JSONArray airPacksJson=(JSONArray)marketDatesJson.get("airPacksJson");
							JSONObject airPacksJson0=(JSONObject)airPacksJson.get(0);
							JSONArray passengerTypesJson=(JSONArray)airPacksJson0.get("passengerTypesJson");
							JSONObject passengerTypesJson0=(JSONObject)passengerTypesJson.get(0);
							JSONArray taxesJson=(JSONArray)passengerTypesJson0.get("taxesJson");
							double taxSum = 0;
							for(int k=0;k<taxesJson.size();k++){
								JSONObject taxesJsonk=(JSONObject)taxesJson.get(k);
								Double tax = taxesJsonk.getDouble("price");
								taxSum+=tax;
							}
							DecimalFormat df = new DecimalFormat("0.0000");
							String t = df.format(taxSum);
							taxSum=Double.parseDouble(t);
							
							
							flightDetail.setPrice(passengerTypesJson0.getDouble("farePrice"));
							flightDetail.setDepcity(arg1.getDep());
							flightDetail.setArrcity(arg1.getArr());
							flightDetail.setWrapperid(arg1.getWrapperid());
							flightDetail.setTax(taxSum);
							
							segs.add(seg);
							baseFlight.setDetail(flightDetail);
							baseFlight.setInfo(segs);
							flightList.add(baseFlight);
						}
					}
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
		searchParam.setWrapperid("gjdairzb001");
		searchParam.setDep("LTN");
		searchParam.setArr("FCO");
		searchParam.setDepDate("2014-07-27");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");
		
		String html = new  Wrapper_gjdairzb001().getHtml(searchParam);
		ProcessResultInfo result = new ProcessResultInfo();
		result = new  Wrapper_gjdairzb001().process(html,searchParam);
		if(result.isRet() && result.getStatus().equals(Constants.SUCCESS))
		{
			List<OneWayFlightInfo> flightList = (List<OneWayFlightInfo>) result.getData();
			for (OneWayFlightInfo in : flightList){
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

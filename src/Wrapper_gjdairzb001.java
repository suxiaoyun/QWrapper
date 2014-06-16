import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
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
		map.put("roundtrip", "False");
		map.put("origin", arg0.getDep());
		map.put("destination", arg0.getArr());
		map.put("outbounddate",arg0.getDepDate().replaceAll("-", ""));
		map.put("adult", "1");
		map.put("child", "0");
		map.put("infant", "0");
		map.put("lff", "False");
		map.put("offer", "False");
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

		String getUrl = String.format("http://bookflights.monarch.co.uk/SearchSelect.aspx?roundtrip=False&origin=%s&destination=%s&outbounddate=%s&adult=1&child=0&infant=0&lff=False&offer=False", arg0.getDep(), arg0.getArr(), arg0.getDepDate().replaceAll("-", ""));
//		String getUrl ="http://bookflights.monarch.co.uk/SearchSelect.aspx?roundtrip=False&origin=LTN&destination=FCO&outbounddate=20140727&adult=1&child=0&infant=0&lff=False&offer=False";
			
			get = new QFGetMethod(getUrl);
			
			//1������ͨ�����get|post������ܵõ�������Ʊ��Ϣ����վ����Ҫע���status���ж�
			//2������ͨ�����get|post������ܵõ�������Ʊ��Ϣ����վ�������Ҫcookie������ÿһ��get|post����ǰ�������cookie
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
							
							System.out.println(marketDatesJson.getString("flightNumber"));
							String no="["+marketDatesJson.getString("flightNumber")+"]";
							System.out.println(no.replaceAll("[^a-zA-Z\\d]", ""));
							
							seg.setFlightno(marketDatesJson.getString("flightNumber").replaceAll("[^a-zA-Z\\d]", ""));
							seg.setDepairport(marketDatesJson.getString("origin"));
							seg.setArrairport(marketDatesJson.getString("destination"));
							seg.setDeptime(marketDatesJson.getString("departureTime"));
							seg.setArrtime(marketDatesJson.getString("arrivalTime"));
							
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
		searchParam.setDep("VCE");
		searchParam.setArr("BHX");
		searchParam.setDepDate("2014-08-21");
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

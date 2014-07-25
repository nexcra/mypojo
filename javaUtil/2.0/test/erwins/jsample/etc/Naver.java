
package erwins.jsample.etc;



/**
 * 네이버의 각종 API에 관한 결과를 래핑한다.
 */
public abstract class Naver{
/*
    *//**
     * 지오코딩 결과를 HTML Option으로 제공한다.
     *//*
    public static String getGeoCode(String address){
        
        String query = null;
        try {
            query = "key="+getNaverMapKey()+"&query="+URLEncoder.encode(address,"euc-kr");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        

        //DocParser dp = new DocParser(RESTful.post(NAVER_GEOCODE_URL).query(query).run().asStream());
        //List<HashMap<String,String>> results =  dp.getElementsByTagName("x","y","address");
        
        HtmlOptionBuilder o = new HtmlOptionBuilder();
        o.addDefault("원하시는 지역을 골라주세요");
        
        for(Map<String,String> result : results){
            o.add(result.get("x")+","+result.get("y"), result.get("address"));
        }
        return o.toString();
        
    }*/

    //URL
    private static final String NAVER_GEOCODE_URL = "http://maps.naver.com/api/geocode.php";
    //지도 관련
    private static final String NAVER_MAP_JS_URL = "http://map.naver.com/js/naverMap.naver";
    
    @SuppressWarnings("unused")
	private static final String NAVER_NORMAL_KEY = "3826a877cfff565aa2f109ad9a9c121d";
    //private static final String NAVER_LOCAL_MAP_KEY = "ac7298e783b0c0ebced2520878d43c35";
    private static final String NAVER_SERVER_MAP_KEY = "3bd776115c4d41a66793a69ae642865c";
    
    /**
     * 지정된 서버IP가 아니면 LOCAL Key를 , 지정된 서버이면 서버 key를 리턴한다.
     */    
    public static String getNaverMapKey(){
        //return SystemInfo.isServer() ? NAVER_SERVER_MAP_KEY : NAVER_LOCAL_MAP_KEY;
    	return NAVER_SERVER_MAP_KEY;
    }
    /*
    *//**
     * 주소 변경시 일괄 적용 위함 
     *//*    
    public static String getNaverScript(){
        Script wqe = new Script();
        wqe.setSrc(NAVER_MAP_JS_URL+"?key="+getNaverMapKey());
        wqe.setType("text/JavaScript");
        return wqe.toString();
    }*/
   

}

package cn.blacklighting;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		URL url =new URL("https://www.baidu.com/");
//		HttpsURLConnection conn=(HttpsURLConnection) url.openConnection();
//		conn.addRequestProperty("Accept", "*/*");
//		conn.addRequestProperty("User-Agent", "Opera/12.80 (Windows NT 5.1; U; en) Presto/2.10.289 Version/12.02");
//		conn.addRequestProperty("Accept-Encoding", "gzip, deflate");
//		conn.addRequestProperty("Accept-Language", "zh-cn");
//		conn.addRequestProperty("Connection", "Keep-Alive");
//		conn.connect();
//		InputStream input = conn.getInputStream();
//		GZIPInputStream gzipInput=new GZIPInputStream(input);
//		Map<String, List<String>> headerFields = conn.getHeaderFields();
//		byte[] byteArray = IOUtils.toByteArray(gzipInput);
//		String byString=new String(byteArray,"utf8");
//		List<String> list=new ArrayList<>();
//		list=new LinkedList<>();
		Map<String,String> map=new HashMap<>();
		map.put("1","2");
	}

}
